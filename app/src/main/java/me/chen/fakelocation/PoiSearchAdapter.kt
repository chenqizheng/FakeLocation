package me.chen.fakelocation

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amap.api.services.core.PoiItem

/**
 * Created by Chen on 2018/3/10.
 */
open class PoiSearchAdapter(var poiList: ArrayList<PoiItem>, var callback: OnItemClick) : RecyclerView.Adapter<PoiSearchAdapter.PoiSearchVH>() {

    override fun onBindViewHolder(holder: PoiSearchVH, position: Int) {
        holder!!.text1.text = poiList[position].title
        holder!!.text2.text = poiList[position].cityName + (poiList[position]).snippet
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (callback != null) {
                    callback.onItemClick(poiList[position])
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiSearchVH {
        val view = LayoutInflater.from(parent!!.context).inflate(android.R.layout.simple_list_item_2, null);
        return PoiSearchVH(view)
    }

    override fun getItemCount(): Int {
        if (poiList == null) {
            return 0
        }
        return poiList.size
    }

    class PoiSearchVH(view: View) : RecyclerView.ViewHolder(view) {
        val text1 = view.findViewById<TextView>(android.R.id.text1)
        val text2 = view.findViewById<TextView>(android.R.id.text2)
    }


    interface OnItemClick {
        fun onItemClick(poiItem: PoiItem)
    }
}