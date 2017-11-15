package com.android.curlytops.suroytabukidnon.Model;

/**
 * Created by jan_frncs
 */
public class Home {
    public String name;
    public String description;
    public String image;

    public Home(){

    }

    public Home(String image){
        this.image = image;
    }

    public Home(String title, String image){
        this.name = title;
        this.image = image;
    }
}
