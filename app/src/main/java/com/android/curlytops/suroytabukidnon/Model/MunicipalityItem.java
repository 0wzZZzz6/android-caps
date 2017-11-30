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
    private String id, title, location, contact, coverName, coverURL,
            description, latlon, municipalityStorageKey;
    private List<String> category, imageURLS, imageNames;
    private boolean starred;

    public MunicipalityItem() {}

    public MunicipalityItem(String id, String title, String location, String contact,
                            List<String> category,String municipalityStorageKey,
                            List<String> imageURLS, List<String> imageNames,
                            String coverURL, String coverName, boolean starred,
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

    public String getContact() {
        return contact;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getCategory() {
        return category;
    }

    public List<String> getImageNames() {
        return imageNames;
    }

    public List<String> getImageURLS() {
        return imageURLS;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public String getCoverName() {
        return coverName;
    }

    public boolean getStarred() {
        return starred;
    }

    public String getDescription() {
        return description;
    }

    public String getLatlon() {
        return latlon;
    }

    public String getMunicipalityStorageKey() {
        return municipalityStorageKey;
    }
}
