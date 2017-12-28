package com.android.curlytops.suroytabukidnon.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jan_frncs
 */
public class Event implements Serializable {

    public String e_id, title, location, description, fromTime,
            toTime, coverURL, coverName, eventStorageKey;
    public long startDate, endDate;
    public boolean allDay, starred;
    public List<String> imageURLS, imageNames;
    public Map<String, Boolean> interested = new HashMap<>();
    public Map<String, Boolean> going = new HashMap<>();
    public Map<String, String> bookmark = new HashMap<>();

    public Event() {
    }

    public Event(String e_id, String title, String location,
                 String description, boolean allDay, long startDate, String fromTime, String toTime,
                 String coverURL, String coverName, String eventStorageKey, boolean starred,
                 List<String> imageURLS, List<String> imageNames) {
        this.e_id = e_id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.allDay = allDay;
        this.startDate = startDate;
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
                 boolean allDay, long startDate, long endDate, String fromTime, String toTime,
                 String coverURL, String coverName, String eventStorageKey, boolean starred,
                 List<String> imageURLS, List<String> imageNames) {
        this.e_id = e_id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.allDay = allDay;
        this.startDate = startDate;
        this.endDate = endDate;
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
