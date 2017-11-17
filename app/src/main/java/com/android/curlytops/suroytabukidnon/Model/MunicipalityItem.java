package com.android.curlytops.suroytabukidnon.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jan_frncs
 */

public class MunicipalityItem implements Serializable{

    private String id, title, location, contact, coverName, coverURL;
    private List<String> category, imageURLS, imageNames;
    private boolean starred;

    public MunicipalityItem() {}

    public MunicipalityItem(String id, String title, String location, String contact,
                            List<String> category, List<String> imageURLS, List<String> imageNames,
                            String coverURL, String coverName, boolean starred) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.contact = contact;
        this.category = category;
        this.imageURLS = imageURLS;
        this.imageNames = imageNames;
        this.coverURL = coverURL;
        this.coverName = coverName;
        this.starred = starred;
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
}
