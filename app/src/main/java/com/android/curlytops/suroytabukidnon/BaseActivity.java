package com.android.curlytops.suroytabukidnon;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.Model.News;
import com.google.firebase.auth.FirebaseAuth;
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

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        firebaseEvents();
        firebaseMunicipalityItem();
        firebaseNews();
    }

    public void firebaseEvents() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventReference = database.getReference("events");

        eventReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                JSONArray data = new JSONArray();
                JSONObject eventObject;
                JSONObject rootEventObject = new JSONObject();

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    try {
                        if (event != null) {
                            eventObject = new JSONObject();
                            eventObject.put("e_id", eventSnapshot.getKey());
                            eventObject.put("title", event.title);
                            eventObject.put("description", event.description);
                            eventObject.put("location", event.location);
                            eventObject.put("allDay", event.allDay);
                            eventObject.put("fromTime", event.fromTime);
                            eventObject.put("toTime", event.toTime);
                            eventObject.put("coverURL", event.coverURL);
                            eventObject.put("coverName", event.coverName);
                            eventObject.put("imageURLS", event.imageURLS);
                            eventObject.put("imageNames", event.imageNames);
                            eventObject.put("eventStorageKey", event.eventStorageKey);
                            eventObject.put("starred", event.starred);

                            if (event.allDay) {
                                eventObject.put("startDate", event.startDate);
                            } else {
                                eventObject.put("startDate", event.startDate);
                                eventObject.put("endDate", event.endDate);
                            }

                            data.put(eventObject);
                            rootEventObject.put("events", data);
                            FileOutputStream fos = openFileOutput("event.json", MODE_PRIVATE);
                            fos.write(rootEventObject.toString().getBytes());
                            fos.flush();
                            fos.close();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e("Error: ", e.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    public void firebaseMunicipalityItem() {
        final JSONObject municipalityObject = new JSONObject();
        final String[] municipalities = getResources().getStringArray(R.array.municipalityId);

        for (final String municipality : municipalities) {
            DatabaseReference municipalityReference = FirebaseDatabase.getInstance()
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
                            if (municipalityItem != null) {
                                object = new JSONObject();
                                object.put("id", snapshot.getKey());
                                object.put("title", municipalityItem.title);
                                object.put("location", municipalityItem.location);
                                object.put("contact", municipalityItem.contact);
                                object.put("category", municipalityItem.category);
                                object.put("imageURLS", municipalityItem.imageURLS);
                                object.put("imageNames", municipalityItem.imageNames);
                                object.put("municipalityStorageKey", municipalityItem.municipalityStorageKey);
                                object.put("coverURL", municipalityItem.coverURL);
                                object.put("coverName", municipalityItem.coverName);
                                object.put("starred", municipalityItem.starred);
                                object.put("description", municipalityItem.description);
                                object.put("latlon", municipalityItem.latlon);

                                data.put(object);
                                municipalityObject.put(municipality, data);
                                FileOutputStream fos = openFileOutput("municipality.json", Context.MODE_PRIVATE);
                                fos.write(municipalityObject.toString().getBytes());
                                fos.flush();
                                fos.close();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.e("Error: ", e.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getMessage());
                }
            });
        }


    }

    public void firebaseNews() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference newsReference = database.getReference("news");

        newsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                JSONArray data = new JSONArray();
                JSONObject newsObject;
                JSONObject rootNewsObject = new JSONObject();

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    News news = eventSnapshot.getValue(News.class);
                    try {
                        if (news != null) {
                            newsObject = new JSONObject();
                            newsObject.put("n_id", eventSnapshot.getKey());
                            newsObject.put("title", news.title);
                            newsObject.put("link", news.link);
                            newsObject.put("newsStorageKey", news.newsStorageKey);
                            newsObject.put("coverURL", news.coverURL);
                            newsObject.put("coverName", news.coverName);
                            newsObject.put("timestamp", news.timestamp);

                            data.put(newsObject);
                            rootNewsObject.put("news", data);
                            FileOutputStream fos = openFileOutput("news.json", MODE_PRIVATE);
                            fos.write(rootNewsObject.toString().getBytes());
                            fos.flush();
                            fos.close();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e("Error: ", e.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}

