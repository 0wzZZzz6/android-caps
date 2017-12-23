package com.android.curlytops.suroytabukidnon.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jan_frncs
 */

public class Bookmark {
    public Map<String, Boolean> marked = new HashMap<>();

    String key, item_id;

    public Bookmark() {
    }

    public Bookmark(String key, String item_id) {
        this.key = key;
        this.item_id = item_id;
    }

}
