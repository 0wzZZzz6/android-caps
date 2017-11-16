package com.android.curlytops.suroytabukidnon;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by jan_frncs
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE = "url";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_MUNICIPALITY = "municipality";
    public static final String EXTRA_DATE = "date";

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
//        firebase();
    }

// remove firebase municipality

}

