package com.android.curlytops.suroytabukidnon.Utils;

import com.mapswithme.maps.api.MWMPoint;

/**
 * Created by jan_frncs
 */

public class MwmDataItem {

    private final String id;
    private final String name;
    private final String description;
    private final double lat;
    private final double lon;

    public MwmDataItem(String id, String name, String description, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return name;
    }

    public MWMPoint toMWMPoint() {
        return new MWMPoint(lat, lon, name, id);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static MwmDataItem[] ITEMS = {
            new MwmDataItem("BNN", "Baungon", "Municipality of Baungon", 8.312594, 124.687185),
            new MwmDataItem("CLN", "Cabanglasan", "Municipality of Cabanglasan", 8.076538, 125.301185),
            new MwmDataItem("DMG", "Damulog", "Municipality of Damulog", 7.48139, 124.938843),
            new MwmDataItem("DCN", "Dangcagan", "Municipality of Dangcagan", 7.609997, 125.004117),
            new MwmDataItem("DCL", "Don Carlos", "Municipality of Don Carlos", 7.683809, 124.99463),
            new MwmDataItem("IPO", "Impasug-ong", "Municipality of Impasug-ong", 8.303395, 125.000832),
            new MwmDataItem("KDL", "Kadingilan", "Municipality of Kadingilan", 7.600126, 124.909926),
            new MwmDataItem("KLL", "Kalilangan", "Municipality of Kalilangan", 7.746855, 124.748095),
            new MwmDataItem("KBW", "Kibawe", "Municipality of Kibawe", 7.567833, 124.990323),
            new MwmDataItem("LTP", "Lantapan", "Municipality of Lantapan", 8.02327, 124.988),
            new MwmDataItem("LBN", "Libona", "Municipality of Libona", 8.328322, 124.756944),
            new MwmDataItem("MLY", "Malaybalay", "City of Malaybalay", 8.155399, 125.130449),
            new MwmDataItem("MRM", "Maramag", "Municipality of Maramag", 7.761122, 125.004793),
            new MwmDataItem("MLB", "Malitbog", "Municipality of Malitbog", 8.516667, 124.933334),
            new MwmDataItem("MFT", "Manolo Fortich", "Municipality of Manolo Fortich", 8.365963, 124.863782),
            new MwmDataItem("PNT", "Pangantucan", "Municipality of Pangantucan", 7.832224, 124.828232),
            new MwmDataItem("QZN", "Quezon", "Municipality of Quezon", 7.733333, 125.133333),
            new MwmDataItem("SFN", "San Fernando", "Municipality of San Fernando", 7.916795, 125.32872),
            new MwmDataItem("SML", "Sumilao", "Municipality of Sumilao", 8.327045, 124.97796),
            new MwmDataItem("TLK", "Talakag", "Municipality of Talakag", 8.23227, 124.60357),
            new MwmDataItem("VLC", "Valencia", "City of Valencia", 7.902885, 125.089803)
    };
}
