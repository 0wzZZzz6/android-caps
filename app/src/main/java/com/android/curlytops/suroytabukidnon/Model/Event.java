package com.android.curlytops.suroytabukidnon.Model;

import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jan_frncs
 */
@IgnoreExtraProperties
public class Event implements Serializable {

    public String e_id;
    public String title;
    public String location;
    public String description;
    public long date;
    public long fromDate;
    public long toDate;
    public boolean allDay;
    public String fromTime;
    public String toTime;

    public String coverURL, coverName, eventStorageKey;
    public boolean starred;
    public List<String> imageURLS, imageNames;

    public Map<String, Boolean> interested = new HashMap<>();
    public Map<String, Boolean> going = new HashMap<>();

    public Event() {}

    public Event(String e_id, String title, String location,
                 String description, boolean allDay, long date, String fromTime, String toTime,
                 String coverURL, String coverName, String eventStorageKey, boolean starred,
                 List<String> imageURLS, List<String> imageNames) {
        this.e_id = e_id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.allDay = allDay;
        this.date = date;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.coverURL = coverURL;
        this.coverName = coverName;
        this.eventStorageKey = eventStorageKey;
        this.starred = starred;
        this.imageURLS = imageURLS;
        this.imageNames = imageNames;
    }

    public Event(String e_id, String title, String location, String description,
                 boolean allDay, long fromDate, long toDate, String fromTime, String toTime,
                 String coverURL, String coverName, String eventStorageKey, boolean starred,
                 List<String> imageURLS, List<String> imageNames) {
        this.e_id = e_id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.allDay = allDay;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.coverURL = coverURL;
        this.coverName = coverName;
        this.eventStorageKey = eventStorageKey;
        this.starred = starred;
        this.imageURLS = imageURLS;
        this.imageNames = imageNames;
    }
}
