package com.android.curlytops.suroytabukidnon.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.curlytops.suroytabukidnon.Model.Home;
import com.android.curlytops.suroytabukidnon.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jan_frncs
 */

public class HomeFragment extends Fragment {

    View rootView;
    HomeAdapter homeAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    public HomeFragment() {}

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
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView_home);
        homeAdapter = new HomeAdapter(this.getContext(), getHomeJson());

        linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(homeAdapter);

        RecyclerView recyclerView1 = rootView.findViewById(R.id.recyclerView_news);
        LinearLayoutManager linearLayout1 = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setHasFixedSize(false);
        recyclerView1.setLayoutManager(linearLayout1);
        recyclerView1.setAdapter(homeAdapter);

        RecyclerView recyclerView2 = rootView.findViewById(R.id.recyclerView_favorites);
        LinearLayoutManager linearLayout2 = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setHasFixedSize(false);
        recyclerView2.setLayoutManager(linearLayout2);
        recyclerView2.setAdapter(homeAdapter);

        setHasOptionsMenu(true);
        return rootView;
    }

    public List<Home> getHomeJson() {
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

