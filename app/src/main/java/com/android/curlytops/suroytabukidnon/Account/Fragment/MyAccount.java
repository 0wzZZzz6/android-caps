package com.android.curlytops.suroytabukidnon.Account.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.LoginActivity;
import com.android.curlytops.suroytabukidnon.Model.News;
import com.android.curlytops.suroytabukidnon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jan_frncs
 */

public class MyAccount extends Fragment {

    private static final String TAG = "MyAccount";
    private static final String Node = "user";

    String email, username;

    @BindView(R.id.username)
    TextView tv_username;
    @BindView(R.id.email)
    TextView tv_email;

    List<News> newsList = new ArrayList<>();
    FileCacher<List<News>> newsCache;

    public static MyAccount newInstance() {
        MyAccount myAccount = new MyAccount();
        Bundle args = new Bundle();
        myAccount.setArguments(args);
        return myAccount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseNews();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myaccount, container, false);
        ButterKnife.bind(this, view);
        readUser();
        tv_email.setText(email);
        tv_username.setText(username);
        newsCache = new FileCacher<>(getContext(), "newsCache.txt");


        return view;
    }

    private void readUser() {
        try {
            FileInputStream fis = getContext().openFileInput("user.json");
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
                int i = 0;
                username = JsonPath.read(document, jsonPath(i, "username", Node));
                email = JsonPath.read(document, jsonPath(i, "email", Node));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.logout)
    public void button_logout() {
//        FirebaseAuth.getInstance().signOut();
//        startActivity(new Intent(getContext(), LoginActivity.class));

        if (newsCache.hasCache()) {
            Log.d(TAG, "has cache");

            try {
                List<List<News>> list = newsCache.getAllCaches();
                for(List<News> text : list){
                    for(News news : text) {
                        Log.d(TAG, news.title);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "no cache");
        }
    }

    private String jsonPath(int index, String keyword, String node) {
        return "$." + node + "[" + index + "]." + keyword;
    }

    public void firebaseNews() {
        DatabaseReference newsReference = FirebaseDatabase
                .getInstance()
                .getReference("news");

        newsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    News news = eventSnapshot.getValue(News.class);
                    if (news != null) {

                        String id = eventSnapshot.getKey();
                        String title = news.title;
                        String link = news.link;
                        String newsStorageKey = news.newsStorageKey;
                        String coverURL = news.coverURL;
                        String coverName = news.coverName;
                        long timeStamp = news.timestamp;

                        newsList.add(new News(id, title, link, newsStorageKey,
                                coverURL, coverName, timeStamp));
                    }
                }

                if (newsCache.hasCache()) {
                    try {
                        newsCache.clearCache();
                        newsCache.writeCache(newsList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        newsCache.writeCache(newsList);
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
