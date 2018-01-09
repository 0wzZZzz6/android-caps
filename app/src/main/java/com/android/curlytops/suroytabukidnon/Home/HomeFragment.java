package com.android.curlytops.suroytabukidnon.Home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.Home;
import com.android.curlytops.suroytabukidnon.Model.News;
import com.android.curlytops.suroytabukidnon.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    SnapHelper snapHelperNews = new LinearSnapHelper();
    SnapHelper snapHelperEvents = new LinearSnapHelper();
    SnapHelper snapHelperTribes = new LinearSnapHelper();

    @BindView(R.id.fragment_home_swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerView_home)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_home_recyclerView_news)
    RecyclerView rv_news;
    @BindView(R.id.fragment_home_recyclerView_events)
    RecyclerView rv_events;


    @BindView(R.id.fragment_home_events)
    View events;
    @BindView(R.id.fragment_home_news)
    View news;

    List<Event> eventList = new ArrayList<>();
    List<Event> newEventList = new ArrayList<>();
    List<News> newsList = new ArrayList<>();

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        newEventList.clear();
        newsList.clear();
        eventList = new BaseActivity().readEvents(getContext());
        newsList = new BaseActivity().readNews(getContext());

        for (Event event : eventList) {
            if (event.starred)
                newEventList.add(event);
        }

        if (newsList.size() > 0) {
            news.setVisibility(View.VISIBLE);
            homeNewsAdapter = new HomeNewsAdapter(this.getContext(), newsList);
            LinearLayoutManager linearLayout1 = new LinearLayoutManager(this.getContext(),
                    LinearLayoutManager.HORIZONTAL, false);
            rv_news.setHasFixedSize(false);
            rv_news.setLayoutManager(linearLayout1);
            rv_news.setAdapter(homeNewsAdapter);
            snapHelperNews.attachToRecyclerView(rv_news);
        }

        if (newEventList.size() > 0) {
            Collections.sort(newEventList, new Comparator<Event>() {
                @Override
                public int compare(Event o1, Event o2) {
                    return Long.compare(o1.startDate, o2.startDate);
                }
            });
            events.setVisibility(View.VISIBLE);
            homeEventAdapter = new HomeEventAdapter(this.getContext(), newEventList);
            LinearLayoutManager linearLayout2 = new LinearLayoutManager(this.getContext(),
                    LinearLayoutManager.HORIZONTAL, false);
            rv_events.setHasFixedSize(false);
            rv_events.setLayoutManager(linearLayout2);
            rv_events.setAdapter(homeEventAdapter);
            snapHelperEvents.attachToRecyclerView(rv_events);
        }

        homeAdapter = new HomeAdapter(this.getContext(), readHomeJson());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(homeAdapter);
        snapHelperTribes.attachToRecyclerView(recyclerView);

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

}


