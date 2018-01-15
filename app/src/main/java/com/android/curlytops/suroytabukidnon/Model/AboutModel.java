package com.android.curlytops.suroytabukidnon.Model;

import java.util.List;

/**
 * Created by jan_frncs
 */

public class AboutModel {

    public String municipality;
    public List<String> imageURLS, imageNames;
    public String description;

    public AboutModel() {
    }

    public AboutModel(String municipality, String description, List<String> imageURLS, List<String> imageNames) {
        this.municipality = municipality;
        this.description = description;
        this.imageURLS = imageURLS;
        this.imageNames = imageNames;
    }
}
