package com.android.curlytops.suroytabukidnon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.curlytops.suroytabukidnon.Model.Event;
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
        firebaseEvents();
        firebaseMunicipalityItem();
    }

    private void firebaseEvents() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventReference = database.getReference("events");

        eventReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                JSONArray data = new JSONArray();
                JSONObject eventObject;
                JSONObject finalEventObject = new JSONObject();

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    try {
                        eventObject = new JSONObject();
                        eventObject.put("e_id", eventSnapshot.getKey());
                        eventObject.put("title", event.getTitle());
                        eventObject.put("description", event.getDescription());
                        eventObject.put("location", event.getLocation());
                        eventObject.put("allDay", event.getAllDay());
                        eventObject.put("fromTime", event.getFromTime());
                        eventObject.put("toTime", event.getToTime());

                        // new
                        eventObject.put("coverURL", event.getCoverURL());
                        eventObject.put("coverName", event.getCoverName());
                        eventObject.put("imageURLS", event.getImageURLS());
                        eventObject.put("imageNames", event.getImageNames());
                        eventObject.put("eventStorageKey", event.getEventStorageKey());
                        eventObject.put("starred", event.getStarred());

                        if (event.getAllDay()) {
                            eventObject.put("date", event.getDate());
                        } else {
                            eventObject.put("fromDate", event.getFromDate());
                            eventObject.put("toDate", event.getToDate());
                        }

                        data.put(eventObject);
                        finalEventObject.put("events", data);
                        FileOutputStream fos = openFileOutput("event.json", MODE_PRIVATE);
                        fos.write(finalEventObject.toString().getBytes());
                        fos.flush();
                        fos.close();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e("Error: ", e.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(BaseActivity.this, "[CHANGE] event item", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    private void firebaseMunicipalityItem() {
        final JSONObject municipalityObject = new JSONObject();
        final String[] municipalities = getResources().getStringArray(R.array.municipalityId);

        for (final String municipality : municipalities) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference municipalityReference = database
                    .getReference("municipality")
                    .child(municipality);

            municipalityReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    JSONObject object;
                    JSONArray data = new JSONArray();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MunicipalityItem municipalityItem = snapshot.getValue(MunicipalityItem.class);
                        try {
                            object = new JSONObject();
                            object.put("id", snapshot.getKey());
                            object.put("title", municipalityItem.getTitle());
                            object.put("location", municipalityItem.getLocation());
                            object.put("contact", municipalityItem.getContact());
                            object.put("category", municipalityItem.getCategory());
                            object.put("imageURLS", municipalityItem.getImageURLS());
                            object.put("imageNames", municipalityItem.getImageNames());

                            // new
                            object.put("coverURL", municipalityItem.getCoverURL());
                            object.put("coverName", municipalityItem.getCoverName());
                            object.put("starred", municipalityItem.getStarred());
                            data.put(object);
                            municipalityObject.put(municipality, data);
                            FileOutputStream fos = openFileOutput("municipality.json", Context.MODE_PRIVATE);
                            fos.write(municipalityObject.toString().getBytes());
                            fos.flush();
                            fos.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.e("Error: ", e.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(BaseActivity.this, "[CHANGE] municipality item", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getMessage());
                }
            });
        }


    }

}

