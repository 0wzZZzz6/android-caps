package com.android.curlytops.suroytabukidnon.Model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by jan_frncs
 */
@IgnoreExtraProperties
public class Event implements Serializable {

    private String e_id;
    private String title;
    private String location;
    private String description;
    private long date;
    private long fromDate;
    private long toDate;
    private boolean allDay;
    private String fromTime;
    private String toTime;

    public Event() {}

    public Event(String e_id, String title, String location,
                 String description, boolean allDay, long date, String fromTime, String toTime) {
        this.e_id = e_id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.allDay = allDay;
        this.date = date;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public Event(String e_id, String title, String location, String description,
                 boolean allDay, long fromDate, long toDate, String fromTime, String toTime) {
        this.e_id = e_id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.allDay = allDay;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public Event(String e_id, String title, String location, String description,
                 long date, long fromDate, long toDate, boolean allDay) {
        this.e_id = e_id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.date = date;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.allDay = allDay;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public long getDate() {
        return date;
    }

    public long getFromDate() {
        return fromDate;
    }

    public long getToDate() {
        return toDate;
    }

    public boolean getAllDay() {
        return allDay;
    }

    public String getFromTime() {
        return fromTime;
    }

    public String getToTime() {
        return toTime;
    }
}
