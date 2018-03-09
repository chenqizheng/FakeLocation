package me.chen.fakelocation

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import android.os.Build


/**
 * Created by chenqizheng on 2018/2/9.
 */
class MockLocationManager private constructor(var context: Context) {
    lateinit var mLocationManger: LocationManager;
    lateinit var mMockThread: Thread;
    var mLatitude: Double = 0.0;
    var mLongitude: Double = 0.0;
    var hasAddTestProvider: Boolean = false;

    enum class State {
    }

    init {
        initMock();
    }

    fun start() {
        if (mMockThread == null) {
            mMockThread = Thread {
                while (true) {
                    try {
                        Thread.sleep(500);
                        setLocation(mLatitude, mLongitude);
                    } catch (e: Exception) {

                    }
                }
            };
            mMockThread.start();
        }

    }

    fun updateLocation(latitude: Double, longitude: Double) {
        mLatitude = latitude;
        mLongitude = longitude;
    }


    fun stop() {
        if (hasAddTestProvider) {
            try {
                mLocationManger.removeTestProvider(LocationManager.GPS_PROVIDER)
            } catch (ex: Exception) {
                // 若未成功addTestProvider，或者系统模拟位置已关闭则必然会出错
            }
            hasAddTestProvider = false
        }
    }

    private fun setLocation(latitude: Double, longitude: Double) {
        try {
            val providerStr = LocationManager.GPS_PROVIDER
            val mockLocation = Location(providerStr)
            mockLocation.latitude = latitude
            mockLocation.longitude = longitude
            mockLocation.altitude = 0.0    // 高程（米）
            mockLocation.bearing = 0F   // 方向（度）
            mockLocation.speed = 0F    //速度（米/秒）
            mockLocation.accuracy = 2F   // 精度（米）
            mockLocation.time = System.currentTimeMillis()   // 本地时间
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //api 16以上的需要加上这一句才能模拟定位 , 也就是targetSdkVersion > 16
                mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos())
            }
            mLocationManger.setTestProviderLocation(providerStr, mockLocation)
        } catch (e: Exception) {
            // 防止用户在软件运行过程中关闭模拟位置或选择其他应用
            stop()
            throw e
        }

    }

    private fun initMock() {
        mLocationManger = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager;
        if (mLocationManger.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                mLocationManger.addTestProvider(LocationManager.GPS_PROVIDER, false, false, false, false, true, true, true, 0, 5)
                mLocationManger.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    companion object {
        @Volatile
        lateinit var instance: MockLocationManager;

        fun getInstance(context: Context): MockLocationManager {
            if (instance == null) {
                synchronized(MockLocationManager::class) {
                    if (instance == null) {
                        instance = MockLocationManager(context);
                    }
                }
            }
            return instance;
        }
    }

}