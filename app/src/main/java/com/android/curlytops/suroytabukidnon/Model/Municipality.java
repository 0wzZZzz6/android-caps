package com.android.curlytops.suroytabukidnon.Model;

/**
 * Created by jan_frncs on 8/24/2016.
 */
public class Municipality {

    public String id;
    public String title;
    public String imgUrl;

    public String name;
    public String address;
    public String latitude;
    public String longitude;
    public String contact;
    public String website;
    public String description;
    public String municipality;


    public Municipality() {

    }

    public Municipality(String id, String title, String imgUrl) {
        this.id = id;
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public Municipality(String id, String name, String latitude, String longitude,
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
