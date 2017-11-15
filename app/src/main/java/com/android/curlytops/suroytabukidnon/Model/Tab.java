package com.android.curlytops.suroytabukidnon.Model;

/**
 * Created by jan_frncs
 */
public class Tab {

    public String id, title, description;
    public double lat, lon;

    public Tab() {

    }

    public Tab(String id, String title, String description, double latitude, double longitude) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lat = latitude;
        this.lon = longitude;
    }

}
