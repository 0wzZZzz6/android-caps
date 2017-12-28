package com.android.curlytops.suroytabukidnon.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jan_frncs
 */

public class Bookmark {
    String id;
    public String item_id;
    public String b_id;

    public Bookmark() {
    }

    public Bookmark(String item_id) {
        this.item_id = item_id;
    }

    public Bookmark(String b_id, String item_id) {
        this.b_id = b_id;
        this.item_id = item_id;
    }
}
