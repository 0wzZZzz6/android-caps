package com.android.curlytops.suroytabukidnon.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.Home;
import com.android.curlytops.suroytabukidnon.Model.News;
import com.android.curlytops.suroytabukidnon.R;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    HomeAdapter homeAdapter;
    HomeEventAdapter homeEventAdapter;
    HomeNewsAdapter homeNewsAdapter;

    @BindView(R.id.fragment_home_swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerView_home)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_home_recyclerView_news)
    RecyclerView rv_news;
    @BindView(R.id.fragment_home_recyclerView_events)
    RecyclerView rv_events;

    @BindView(R.id.fragment_home_textView_news)
    TextView textView_news;
    @BindView(R.id.fragment_home_textView_events)
    TextView textView_events;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = rootView.findViewById(R.id.fragment_home_swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                content();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        ButterKnife.bind(this, rootView);

        content();

        setHasOptionsMenu(true);
        return rootView;
    }

    private void content() {
        homeAdapter = new HomeAdapter(this.getContext(), readHomeJson());
        homeEventAdapter = new HomeEventAdapter(this.getContext(), readEventsJson());
        homeNewsAdapter = new HomeNewsAdapter(this.getContext(), readNewsJson());


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(homeAdapter);

        LinearLayoutManager linearLayout1 = new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rv_news.setHasFixedSize(false);
        rv_news.setLayoutManager(linearLayout1);
        rv_news.setAdapter(homeNewsAdapter);

        LinearLayoutManager linearLayout2 = new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rv_events.setHasFixedSize(false);
        rv_events.setLayoutManager(linearLayout2);
        rv_events.setAdapter(homeEventAdapter);
    }

    public List<Home> readHomeJson() {
        List<Home> homeList = new ArrayList<>();
        InputStream inputStream;
        BufferedInputStream bufferedInputStream;
        JSONArray jsonArray;
        StringBuffer buffer;

        try {
            inputStream = getContext().getAssets().open("tribes.json");
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
                String _name = jsonArray.getJSONObject(i).getString("NAME");
                String _imgUrl = jsonArray.getJSONObject(i).getString("IMG_URL");
                homeList.add(new Home(_name, _imgUrl));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return homeList;
    }

    public List<Event> readEventsJson() {
        List<Event> eventList = new ArrayList<>();

        try {
            FileInputStream fis = getActivity().openFileInput("event.json");
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
                String eid = JsonPath.read(document, jsonPath("events", i, "e_id"));
                String title = JsonPath.read(document, jsonPath("events", i, "title"));
                String location = JsonPath.read(document, jsonPath("events", i, "location"));
                String description = JsonPath.read(document, jsonPath("events", i, "description"));
                boolean allDay = JsonPath.read(document, jsonPath("events", i, "allDay"));
                String fromTime = JsonPath.read(document, jsonPath("events", i, "fromTime"));
                String toTime = JsonPath.read(document, jsonPath("events", i, "toTime"));
                String coverURL = JsonPath.read(document, jsonPath("events", i, "coverURL"));
                String coverName = JsonPath.read(document, jsonPath("events", i, "coverName"));
                String eventStorageKey = JsonPath.read(document, jsonPath("events", i, "eventStorageKey"));
                boolean starred = JsonPath.read(document, jsonPath("events", i, "starred"));
                String stringImageURLS = JsonPath.read(document, jsonPath("events", i, "imageURLS"));
                String stringImageNames = JsonPath.read(document, jsonPath("events", i, "imageNames"));

                List<String> imageURLS = convertToArray(stringImageURLS);
                List<String> imageNames = convertToArray(stringImageNames);

                if (allDay) {
                    date = JsonPath.read(document, jsonPath("events", i, "date"));
                } else {
                    fDate = JsonPath.read(document, jsonPath("events", i, "fromDate"));
                    tDate = JsonPath.read(document, jsonPath("events", i, "toDate"));
                }

                if (starred) {
                    if (allDay) {
                        eventList.add(new Event(eid, title, location, description,
                                allDay, date, fromTime, toTime, coverURL, coverName,
                                eventStorageKey, starred, imageURLS, imageNames));
                    } else {
                        eventList.add(new Event(eid, title, location, description,
                                allDay, fDate, tDate, fromTime, toTime, coverURL, coverName,
                                eventStorageKey, starred, imageURLS, imageNames));
                    }
                }

                i++;
            }

            if (eventList.size() == 0) {
                textView_events.setVisibility(View.GONE);
            } else {
                textView_events.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return eventList;
    }

    public List<News> readNewsJson() {

        List<News> newsList = new ArrayList<>();

        try {
            FileInputStream fis = getActivity().openFileInput("news.json");
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
                String nid = JsonPath.read(document, jsonPath("news", i, "n_id"));
                String title = JsonPath.read(document, jsonPath("news", i, "title"));
                String link = JsonPath.read(document, jsonPath("news", i, "link"));
                String newsStorageKey = JsonPath.read(document, jsonPath("news", i, "newsStorageKey"));
                String coverURL = JsonPath.read(document, jsonPath("news", i, "coverURL"));
                String coverName = JsonPath.read(document, jsonPath("news", i, "coverName"));
                long timestamp = JsonPath.read(document, jsonPath("news", i, "timestamp"));

                newsList.add(new News(nid, title, link, newsStorageKey,
                        coverURL, coverName, timestamp));

                i++;
            }
            if (newsList.size() == 0) {
                textView_news.setVisibility(View.GONE);
            } else {
                textView_news.setVisibility(View.VISIBLE);
            }
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

        for (News item : newsList) {
            if(item.equals(newsList)){
                Log.d(TAG, "asd");
            }
        }
        return newsList;
    }

    private String jsonPath(String node, int index, String keyword) {
        return "$." + node + "[" + index + "]." + keyword;
    }

    private List<String> convertToArray(String item) {
        String category = item.replaceAll("\\s+", "");
        category = category.replace("[", "");
        category = category.replace("]", "");

        return new ArrayList<>(Arrays.asList(category.split(",")));
    }

}

