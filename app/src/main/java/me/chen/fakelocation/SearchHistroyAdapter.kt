package me.chen.fakelocation

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by Chen on 2018/3/10.
 */
open class SearchHistroyAdapter(var list: List<SearchHistory>, var callback: OnItemClick) : RecyclerView.Adapter<SearchHistroyAdapter.SearchHistroyVH>() {

    override fun onBindViewHolder(holder: SearchHistroyVH, position: Int) {
        holder!!.text1.text = list[position].name
        holder.itemView.setOnClickListener {
            if (callback != null) {
                callback.onItemClick(list[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistroyVH {
        val view = LayoutInflater.from(parent!!.context).inflate(android.R.layout.simple_list_item_2, null);
        return SearchHistroyVH(view)
    }

    override fun getItemCount(): Int {
        if (list == null) {
            return 0
        }
        return list.size
    }

    class SearchHistroyVH(view: View) : RecyclerView.ViewHolder(view) {
        val text1 = view.findViewById<TextView>(android.R.id.text1)
    }


    interface OnItemClick {
        fun onItemClick(item: SearchHistory)
    }
}