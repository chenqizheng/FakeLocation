package me.chen.fakelocation;

import android.arch.persistence.room.Entity;

/**
 * Created by chenqizheng on 2018/3/12.
 */
@Entity(primaryKeys = {"lat", "lng"})
public class SearchHistory {
    public String name;
    public String address;
    public double lat;
    public double lng;

    public SearchHistory(String name, String address, double lat, double lng) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }
}
