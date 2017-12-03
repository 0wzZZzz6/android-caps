package com.android.curlytops.suroytabukidnon.Model;

import java.util.Comparator;

/**
 * Created by jan_frncs
 */

public class News {

    public String id, title, link, newsStorageKey, coverURL, coverName;
    public long timestamp;

    public News(){}

    public News(String id, String title, String link, String newsStorageKey,
                String coverURL, String coverName, long timestamp){
        this.id = id;
        this.title = title;
        this.link = link;
        this.newsStorageKey = newsStorageKey;
        this.coverURL = coverURL;
        this.coverName = coverName;
        this.timestamp = timestamp;
    }
}
