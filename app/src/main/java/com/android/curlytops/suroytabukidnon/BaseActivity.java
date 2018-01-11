package com.android.curlytops.suroytabukidnon;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.Connection.ConnectivityReceiver;
import com.android.curlytops.suroytabukidnon.Model.Bookmark;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.Municipality;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.Model.News;
import com.android.curlytops.suroytabukidnon.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by jan_frncs
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";

    public static final String IMAGEURL = "url";
    public static final String MUNICIPALITY_ID = "id";
    public static final String MUNICIPALITY = "municipality";
    public static final String event_path = "events";
    public static final String news_path = "news";

    DatabaseReference bookmarkReference_events;
    DatabaseReference bookmarkReference_places;

    String empty = "";
    String[] municipalities;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    // start_write_json
    public void firebaseEvents() {
        DatabaseReference eventReference = FirebaseDatabase
                .getInstance()
                .getReference("events");

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
                            eventObject.put("taggedMunicipality", event.taggedMunicipality);

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

                Log.d("SHIELA", "has changes");
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
        final List<String> list_municipality = new ArrayList<>();

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

                                list_municipality.add(municipality);

                                data.put(object);
                                municipalityObject.put(municipality, data);
                                FileOutputStream fos = openFileOutput("municipalityItem.json", Context.MODE_PRIVATE);
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


                    try {
                        Set<String> hs = new HashSet<>();
                        hs.addAll(list_municipality);
                        list_municipality.clear();
                        list_municipality.addAll(hs);

                        FileOutputStream fos2 = openFileOutput("available_municipality.txt", Context.MODE_PRIVATE);
                        fos2.write(list_municipality.toString().getBytes());
                        fos2.flush();
                        fos2.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e("Error: ", e.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
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
        DatabaseReference newsReference = FirebaseDatabase
                .getInstance()
                .getReference("news");

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

    public void firebaseBookmarked() {
        bookmarkReference_events = FirebaseDatabase.getInstance()
                .getReference("bookmark")
                .child("saved_events");

        bookmarkReference_places = FirebaseDatabase.getInstance()
                .getReference("bookmark")
                .child("saved_places");

        bookmarkReference_events.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(getUid())) {
                    bookmarkReference_events.child(getUid());

                    bookmarkReference_events
                            .child(getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    JSONArray data = new JSONArray();
                                    JSONObject bookmarkObject_event;
                                    JSONObject root = new JSONObject();
                                    FileOutputStream fos;

                                    for (DataSnapshot bookmarkSnapshot : dataSnapshot.getChildren()) {
                                        Bookmark bookmark = bookmarkSnapshot.getValue(Bookmark.class);
                                        try {
                                            if (bookmark != null) {
                                                bookmarkObject_event = new JSONObject();
                                                bookmarkObject_event.put("b_id", bookmarkSnapshot.getKey());
                                                bookmarkObject_event.put("item_id", bookmark.item_id);

                                                data.put(bookmarkObject_event);
                                                root.put("bookmark_events", data);
                                                fos = openFileOutput("bookmark_events.json", MODE_PRIVATE);
                                                fos.write(root.toString().getBytes());
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

                                }
                            });
                } else {
                    try {
                        FileOutputStream fos;
                        fos = openFileOutput("bookmark_events.json", MODE_PRIVATE);
                        fos.write(empty.getBytes());
                        fos.flush();
                        fos.close();
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

            }
        });

        bookmarkReference_places.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(getUid())) {
                    bookmarkReference_places.child(getUid());

                    bookmarkReference_places
                            .child(getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    JSONArray data = new JSONArray();
                                    JSONObject bookmarkObject_places;
                                    JSONObject root = new JSONObject();
                                    FileOutputStream fos;

                                    for (DataSnapshot bookmarkSnapshot : dataSnapshot.getChildren()) {
                                        Bookmark bookmark = bookmarkSnapshot.getValue(Bookmark.class);
                                        try {
                                            if (bookmark != null) {
                                                bookmarkObject_places = new JSONObject();
                                                bookmarkObject_places.put("b_id", bookmarkSnapshot.getKey());
                                                bookmarkObject_places.put("item_id", bookmark.item_id);

                                                data.put(bookmarkObject_places);
                                                root.put("bookmark_places", data);
                                                fos = openFileOutput("bookmark_places.json", MODE_PRIVATE);
                                                fos.write(root.toString().getBytes());
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

                                }
                            });
                } else {
                    try {
                        FileOutputStream fos;
                        fos = openFileOutput("bookmark_places.json", MODE_PRIVATE);
                        fos.write(empty.getBytes());
                        fos.flush();
                        fos.close();

                        FileOutputStream fos2;
                        fos2 = openFileOutput("list_bookmarkedPlaces.json", MODE_PRIVATE);
                        fos2.write(empty.getBytes());
                        fos2.flush();
                        fos2.close();
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

            }
        });

    }

    public void firebaseUser() {
        DatabaseReference userReference = FirebaseDatabase
                .getInstance()
                .getReference("users");

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                JSONArray data = new JSONArray();
                JSONObject userObject;
                JSONObject root = new JSONObject();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(userSnapshot.getKey(), getUid())) {
                        User user = userSnapshot.getValue(User.class);
                        try {
                            if (user != null) {
                                userObject = new JSONObject();
                                userObject.put("uid", userSnapshot.getKey());
                                userObject.put("username", user.username);
                                userObject.put("email", user.email);

                                data.put(userObject);
                                root.put("user", data);
                                FileOutputStream fos = openFileOutput("user.json", MODE_PRIVATE);
                                fos.write(root.toString().getBytes());
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // end_write_json

    // start_read_json
    public List<Event> readEvents(Context context) {
        List<Event> eventList = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput("event.json");
            BufferedInputStream bis = new BufferedInputStream(fis);
            StringBuilder b = new StringBuilder();

            long date = 0;
            long fDate = 0;
            long tDate = 0;

            while (bis.available() != 0) {
                char c = (char) bis.read();
                b.append(c);
            }
            bis.close();
            fis.close();

            String json = b.toString();
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
            int length = JsonPath.read(document, "$.events.length()");

            int i = 0;
            while (i < length) {
                String eid = JsonPath.read(document,
                        jsonPath(i, "e_id", event_path));
                String title = JsonPath.read(document,
                        jsonPath(i, "title", event_path));
                String location = JsonPath.read(document,
                        jsonPath(i, "location", event_path));
                String description = JsonPath.read(document,
                        jsonPath(i, "description", event_path));
                boolean allDay = JsonPath.read(document,
                        jsonPath(i, "allDay", event_path));
                String fromTime = JsonPath.read(document,
                        jsonPath(i, "fromTime", event_path));
                String toTime = JsonPath.read(document,
                        jsonPath(i, "toTime", event_path));
                String coverURL = JsonPath.read(document,
                        jsonPath(i, "coverURL", event_path));
                String coverName = JsonPath.read(document,
                        jsonPath(i, "coverName", event_path));
                String eventStorageKey = JsonPath.read(document,
                        jsonPath(i, "eventStorageKey", event_path));
                boolean starred = JsonPath.read(document,
                        jsonPath(i, "starred", event_path));
                String stringImageURLS = JsonPath.read(document,
                        jsonPath(i, "imageURLS", event_path));
                String stringImageNames = JsonPath.read(document,
                        jsonPath(i, "imageNames", event_path));
                String stringTaggedMunicipality = JsonPath.read(document,
                        jsonPath(i, "taggedMunicipality", event_path));

                List<String> imageURLS = convertToArray(stringImageURLS);
                List<String> imageNames = convertToArray(stringImageNames);
                List<String> taggedMunicipality = convertToArray(stringTaggedMunicipality);

                if (allDay) {
                    date = JsonPath.read(document,
                            jsonPath(i, "startDate", event_path));
                } else {
                    fDate = JsonPath.read(document,
                            jsonPath(i, "startDate", event_path));
                    tDate = JsonPath.read(document,
                            jsonPath(i, "endDate", event_path));
                }

                if (allDay) {
                    eventList.add(new Event(eid, title, location, description, true, date,
                            fromTime, toTime, coverURL, coverName, eventStorageKey, starred,
                            imageURLS, imageNames, taggedMunicipality));
                } else {
                    eventList.add(new Event(eid, title, location, description, false, fDate,
                            tDate, fromTime, toTime, coverURL, coverName, eventStorageKey, starred,
                            imageURLS, imageNames, taggedMunicipality));
                }

                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return eventList;
    }

    public List<MunicipalityItem> readMunicipalityItems(Context context) {
        List<MunicipalityItem> itemList = new ArrayList<>();
        int itemLength;
//        final String[] municipalities = context.getResources().getStringArray(R.array.municipalityId);
        final List<String> municipalities = readAvailableMunicipalities(context);
        try {
            FileInputStream fis = context.openFileInput("municipalityItem.json");
            BufferedInputStream bis = new BufferedInputStream(fis);
            StringBuilder b = new StringBuilder();
            while (bis.available() != 0) {
                char c = (char) bis.read();
                b.append(c);
            }
            bis.close();
            fis.close();
            String json = b.toString();
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);

            try {
                for (String municipality : municipalities) {
                    itemLength = JsonPath.read(document, "$." + municipality + ".length()");
                    int i = 0;
                    while (i < itemLength) {
                        String iid = JsonPath.read(document,
                                jsonPath(i, "id", municipality));
                        String title = JsonPath.read(document,
                                jsonPath(i, "title", municipality));
                        String location = JsonPath.read(document,
                                jsonPath(i, "location", municipality));
                        String contact = JsonPath.read(document,
                                jsonPath(i, "contact", municipality));
                        String stringCategory = JsonPath.read(document,
                                jsonPath(i, "category", municipality));
                        String stringImageURLS = JsonPath.read(document,
                                jsonPath(i, "imageURLS", municipality));
                        String stringImageNames = JsonPath.read(document,
                                jsonPath(i, "imageNames", municipality));
                        String municipalityStorageKey = JsonPath.read(document,
                                jsonPath(i, "municipalityStorageKey", municipality));
                        String coverURL = JsonPath.read(document,
                                jsonPath(i, "coverURL", municipality));
                        String coverName = JsonPath.read(document,
                                jsonPath(i, "coverName", municipality));
                        boolean starred = JsonPath.read(document,
                                jsonPath(i, "starred", municipality));
                        String description = JsonPath.read(document,
                                jsonPath(i, "description", municipality));
                        String latlon = JsonPath.read(document,
                                jsonPath(i, "latlon", municipality));

                        List<String> category = convertToArray(stringCategory);
                        List<String> imageURLS = convertToArray(stringImageURLS);
                        List<String> imageNames = convertToArray(stringImageNames);

                        itemList.add(new MunicipalityItem(iid, municipality, title, location, contact,
                                category, municipalityStorageKey, imageURLS, imageNames,
                                coverURL, coverName, starred, description, latlon));
                        i++;
                    }
                }
            } catch (Exception e) {
                itemLength = 0;
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return itemList;
    }

    public List<News> readNews(Context context) {

        List<News> newsList = new ArrayList<>();

        try {
            FileInputStream fis = context.openFileInput("news.json");
            BufferedInputStream bis = new BufferedInputStream(fis);
            StringBuilder b = new StringBuilder();

            while (bis.available() != 0) {
                char c = (char) bis.read();
                b.append(c);
            }
            bis.close();
            fis.close();

            String json = b.toString();
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
            int length = JsonPath.read(document, "$.news.length()");

            int i = 0;
            while (i < length) {
                String nid = JsonPath.read(document,
                        jsonPath(i, "n_id", news_path));
                String title = JsonPath.read(document,
                        jsonPath(i, "title", news_path));
                String link = JsonPath.read(document,
                        jsonPath(i, "link", news_path));
                String newsStorageKey = JsonPath.read(document,
                        jsonPath(i, "newsStorageKey", news_path));
                String coverURL = JsonPath.read(document,
                        jsonPath(i, "coverURL", news_path));
                String coverName = JsonPath.read(document,
                        jsonPath(i, "coverName", news_path));
                long timestamp = JsonPath.read(document,
                        jsonPath(i, "timestamp", news_path));

                newsList.add(new News(nid, title, link, newsStorageKey,
                        coverURL, coverName, timestamp));

                i++;
            }
//            if (newsList.size() == 0) {
//                textView_news.setVisibility(View.GONE);
//            } else {
//                textView_news.setVisibility(View.VISIBLE);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(newsList, new Comparator<News>() {
            @Override
            public int compare(News o1, News o2) {
                return Long.compare(o1.timestamp, o2.timestamp);
            }
        });
        Collections.reverse(newsList);

        return newsList;
    }

    public List<Municipality> getMunicipalityJson(Context context) {

        List<Municipality> municipalityList = new ArrayList<>();
        InputStream inputStream;
        BufferedInputStream bufferedInputStream;
        JSONArray jsonArray;
        StringBuffer buffer;

        try {
            inputStream = context.getAssets().open("municipalities.json");
            bufferedInputStream = new BufferedInputStream(inputStream);
            buffer = new StringBuffer();
            while (bufferedInputStream.available() != 0) {
                char c = (char) bufferedInputStream.read();
                buffer.append(c);
            }
            bufferedInputStream.close();
            inputStream.close();

            jsonArray = new JSONArray(buffer.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                String _id = jsonArray.getJSONObject(i).getString("ID");
                String _municipality = jsonArray.getJSONObject(i).getString("MUNICIPALITY");
                String _imgUrl = jsonArray.getJSONObject(i).getString("IMG_URL");
                municipalityList.add(new Municipality(_id, _municipality, _imgUrl));
                Log.d("json", _id + "-" + _municipality);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return municipalityList;
    }

    public List<String> readAvailableMunicipalities(Context context){
        String result = new String();
        try {
            FileInputStream fis = context.openFileInput("available_municipality.txt");
            BufferedInputStream bis = new BufferedInputStream(fis);
            StringBuilder b = new StringBuilder();

            while (bis.available() != 0) {
                char c = (char) bis.read();
                b.append(c);
            }
            bis.close();
            fis.close();

            result = b.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertToArray(result);
    }
    // end_read_json

    private List<String> convertToArray(String item) {
        String category = item.replaceAll("\\s+", "");
        category = category.replace("[", "");
        category = category.replace("]", "");

        return new ArrayList<>(Arrays.asList(category.split(",")));
    }

    private String jsonPath(int index, String keyword, String path) {
        return "$." + path + "[" + index + "]." + keyword;
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

    public boolean checkConnection(View view) {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected, view);

        return isConnected;
    }

    // Showing the status in Snackbar
    public void showSnack(boolean isConnected, View view) {
        String message;

        if (!isConnected) {
            message = "Sorry! Not connected to internet";

            Snackbar snackbar = Snackbar
                    .make(view, message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            sbView.setBackgroundColor(Color.RED);
            snackbar.show();
        }

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}

