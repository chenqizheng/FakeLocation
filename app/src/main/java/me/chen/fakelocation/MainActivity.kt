package me.chen.fakelocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.UiSettings
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.services.core.PoiItem
import com.amap.api.services.core.SuggestionCity
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlin.collections.ArrayList


const val WRITE_COARSE_LOCATION_REQUEST_CODE = 100

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, PoiSearch.OnPoiSearchListener, PoiSearchAdapter.OnItemClick, AMap.OnMapClickListener, MockLocationManager.MockLocationListenter {


    private lateinit var mMockLocationManager: MockLocationManager;
    private lateinit var mMapView: MapView
    private lateinit var mSearchView: SearchView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAMap: AMap
    private var mCity: String = ""
    private var mWord: String = ""
    private lateinit var mAppDatabase: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView(savedInstanceState)
        checkPermission()
        initLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        val searchItem = menu!!.findItem(R.id.menu_search)
        mSearchView = searchItem.actionView as SearchView
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    search(query, mCity)
                    hideSoftInput()
                }
                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)

    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_COARSE_LOCATION_REQUEST_CODE);//自定义的code
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_COARSE_LOCATION_REQUEST_CODE) {
        }
    }

    private fun search(word: String, city: String) {
        mWord = word
        mCity = city
        val poiSearchQuery = PoiSearch.Query(word, "", city);
        poiSearchQuery.pageNum = 0
        poiSearchQuery.pageSize = 10
        val poiSearch = PoiSearch(this, poiSearchQuery)
        poiSearch.setOnPoiSearchListener(this)
        poiSearch.searchPOIAsyn()
    }

    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {
    }

    override fun onPoiSearched(p0: PoiResult?, p1: Int) {
        if (p0 != null && p0.searchSuggestionCitys.size > 0) {
            showSuggestionCitysDialog(p0.searchSuggestionCitys)
        } else if (p0 != null && p0.pois.size > 0) {
            var mAdapter = PoiSearchAdapter(p0.pois, this)
            mRecyclerView.adapter = mAdapter;
            mRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun showSuggestionCitysDialog(citys: MutableList<SuggestionCity>) {
        var build = AlertDialog.Builder(this)
        build.setTitle(R.string.dialog_show_suggestion_city)
        var adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice)
        adapter.addAll(citys.map { it.cityName })
        build.setAdapter(adapter, { dialog, which ->
            mCity = citys[which].cityCode
            search(mWord, mCity)
            dialog.dismiss()
        })
        build.create().show()

    }

    private fun hideSoftInput() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun initLocation() {
        mMockLocationManager = MockLocationManager.getInstance(this)
        mMockLocationManager.registerListtenter(this)
        mMockLocationManager.initMock()
        mAMap = mMapView.map;
        mAMap.setOnMapClickListener(this)
        val uiSettings: UiSettings = mAMap.uiSettings
        uiSettings.isMyLocationButtonEnabled = true
        mAMap.isMyLocationEnabled = true
        val locationStyle = MyLocationStyle()
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
        mAMap.myLocationStyle = locationStyle

    }


    private fun initView(savedInstanceState: Bundle?) {
        mAppDatabase = (application as App).mAppDatabase
        var navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setCheckedItem(R.id.nav_map)
        mRecyclerView = findViewById(R.id.list)
        mRecyclerView.visibility = View.GONE
        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            if (mMockLocationManager.mState == STATE_RUNNING) {
                fab.setImageResource(R.drawable.vector_drawable_start_btn)
                mMockLocationManager.stop()
            } else {
                fab.setImageResource(R.drawable.vector_drawable_stop_btn)
                mMockLocationManager.start()
            }
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        mMapView.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_about -> {
                showAboutDialog()
            }
            R.id.nav_map -> {
                mRecyclerView.visibility = View.GONE
                mAMap.mapType = AMap.MAP_TYPE_NORMAL
            }
            R.id.nav_satellite -> {
                mRecyclerView.visibility = View.GONE
                mAMap.mapType = AMap.MAP_TYPE_SATELLITE

            }
            R.id.nav_history -> {
                showHistoryyList()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showHistoryyList() {
        Schedulers.io().scheduleDirect({
            var list = mAppDatabase.searchHistoryDAO.loadAll().toList()
            mRecyclerView.post {
                mRecyclerView.visibility = View.VISIBLE
                var adapter = SearchHistroyAdapter(list, object : SearchHistroyAdapter.OnItemClick {
                    override fun onItemClick(item: SearchHistory) {
                        mRecyclerView.visibility = View.GONE
                        setMarkerOptions(item.name, item.lat, item.lng)
                    }

                })
                mRecyclerView.adapter = adapter;
            }

        })
    }

    private fun showAboutDialog() {
        var alertDialogBuild = AlertDialog.Builder(this)
        alertDialogBuild.setTitle(R.string.nav_about)
        alertDialogBuild.setMessage(R.string.about_message)
        val alertDialog: AlertDialog = alertDialogBuild.create()
        alertDialog.show()
        alertDialog.findViewById<TextView>(android.R.id.message)!!.movementMethod = LinkMovementMethod.getInstance();
    }

    override fun onItemClick(poiItem: PoiItem) {
        mRecyclerView.visibility = View.GONE
        Schedulers.io().scheduleDirect({
            mAppDatabase.searchHistoryDAO.insertHistorys(SearchHistory(poiItem.title, "", poiItem.latLonPoint.latitude, poiItem.latLonPoint.longitude))
        })

        setMarkerOptions(poiItem.title, poiItem.latLonPoint
                .latitude, poiItem.latLonPoint.longitude);
        mCity = ""
    }

    fun setMarkerOptions(title: String?, latitude: Double, longitude: Double) {
        val mk = MarkerOptions();
        mk.icon(BitmapDescriptorFactory.defaultMarker());
        mk.title(title);
        mk.isFlat = true;
        val ll = LatLng(latitude, longitude);
        mk.position(ll);
        mAMap.clear(true);
        val cu = CameraUpdateFactory.newLatLng(ll);
        mAMap.animateCamera(cu);
        mAMap.addMarker(mk);
        mMockLocationManager.updateLocation(latitude, longitude)
    }

    override fun onMapClick(latLng: LatLng) {
        mAMap.clear()
        var latitude = latLng.latitude
        var longtitude = latLng.longitude
        var markerOptions = MarkerOptions()
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
        markerOptions.position(latLng)
        mAMap.addMarker(markerOptions)
        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng))
        mMockLocationManager.updateLocation(latitude, longtitude)
    }

    override fun onError(e: Exception) {
        mMapView.post {
            fab.setImageResource(R.drawable.vector_drawable_start_btn)
            if (e is SecurityException) {
                showGoToDevSettingsDialog()
            }
        }

    }

    private fun showGoToDevSettingsDialog() {
        var build = AlertDialog.Builder(this)
        build.setTitle(R.string.open_dev_setting_title)
        build.setMessage(R.string.open_dev_setting_msg)
        build.setNegativeButton(R.string.open_dev_setting_sure, { dialog, whichButton ->
            SettingsUtils.openTestGPSProvider(this)
        })
        build.create().show()
    }
}
