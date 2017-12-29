package com.android.curlytops.suroytabukidnon.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jan_frncs
 */

public class MunicipalityItem implements Serializable{

    public Map<String, Boolean> stars = new HashMap<>();
    public Map<String, String> bookmark = new HashMap<>();
    public String id, title, location, contact, coverName, coverURL,
            description, latlon, municipalityStorageKey;

    public List<String> category, imageURLS, imageNames;
    public Boolean starred;
    public String municipality;

    public MunicipalityItem() {}

    public MunicipalityItem(String id, String title, String location, String contact,
                            List<String> category,String municipalityStorageKey,
                            List<String> imageURLS, List<String> imageNames,
                            String coverURL, String coverName, Boolean starred,
                            String description, String latlon) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.contact = contact;
        this.category = category;
        this.municipalityStorageKey = municipalityStorageKey;
        this.imageURLS = imageURLS;
        this.imageNames = imageNames;
        this.coverURL = coverURL;
        this.coverName = coverName;
        this.starred = starred;
        this.description = description;
        this.latlon = latlon;
    }

    public MunicipalityItem(String id, String municipality, String title, String location, String contact,
                            List<String> category,String municipalityStorageKey,
                            List<String> imageURLS, List<String> imageNames,
                            String coverURL, String coverName, Boolean starred,
                            String description, String latlon) {
        this.id = id;
        this.municipality = municipality;
        this.title = title;
        this.location = location;
        this.contact = contact;
        this.category = category;
        this.municipalityStorageKey = municipalityStorageKey;
        this.imageURLS = imageURLS;
        this.imageNames = imageNames;
        this.coverURL = coverURL;
        this.coverName = coverName;
        this.starred = starred;
        this.description = description;
        this.latlon = latlon;
    }

}
