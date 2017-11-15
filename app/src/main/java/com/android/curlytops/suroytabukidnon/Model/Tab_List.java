package com.android.curlytops.suroytabukidnon.Model;

/**
 * Created by jan_frncs
 */

public class Tab_List {
    public String id;
    public String description;
    public String name;
    public String address;
    public String contact;
    public String website;
    public String latitude;
    public String longitude;
    public String municipality;

    public Tab_List() {

    }

    public Tab_List(String id, String name, String latitude, String longitude,
                    String address, String contact, String website, String description, String municipality) {

        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.contact = contact;
        this.website = website;
        this.description = description;
        this.municipality = municipality;

    }
}
