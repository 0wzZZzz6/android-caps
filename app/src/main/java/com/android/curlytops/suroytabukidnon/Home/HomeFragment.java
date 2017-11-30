package com.android.curlytops.suroytabukidnon.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.Event.EventDetailsActivity;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.Home;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */

public class HomeFragment extends Fragment {

    View rootView;
    HomeAdapter homeAdapter;
    HomeEventAdapter homeEventAdapter;

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
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        homeAdapter = new HomeAdapter(this.getContext(), getHomeJson());
        homeEventAdapter = new HomeEventAdapter(this.getContext(), getEventsJson());

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView_home);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(homeAdapter);

        RecyclerView recyclerView1 = rootView.findViewById(R.id.recyclerView_news);
        LinearLayoutManager linearLayout1 = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setHasFixedSize(false);
        recyclerView1.setLayoutManager(linearLayout1);
        recyclerView1.setAdapter(homeAdapter);

        RecyclerView recyclerView2 = rootView.findViewById(R.id.recyclerView_events);
        LinearLayoutManager linearLayout2 = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setHasFixedSize(false);
        recyclerView2.setLayoutManager(linearLayout2);
        recyclerView2.setAdapter(homeEventAdapter);

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

    public List<Event> getEventsJson() {
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
                String eid = JsonPath.read(document, "$.events[" + i + "].e_id");
                String title = JsonPath.read(document, "$.events[" + i + "].title");
                String location = JsonPath.read(document, "$.events[" + i + "].location");
                String description = JsonPath.read(document, "$.events[" + i + "].description");
                boolean allDay = JsonPath.read(document, "$.events[" + i + "].allDay");
                String fromTime = JsonPath.read(document, "$.events[" + i + "].fromTime");
                String toTime = JsonPath.read(document, "$.events[" + i + "].toTime");

                // new
                String coverURL = JsonPath.read(document, "$.events[" + i + "].coverURL");
                String coverName = JsonPath.read(document, "$.events[" + i + "].coverName");
                String eventStorageKey = JsonPath.read(document, "$.events[" + i + "].eventStorageKey");
                boolean starred = JsonPath.read(document, "$.events[" + i + "].starred");
                String stringImageURLS = JsonPath.read(document, "$.events[" + i + "].imageURLS");
                String stringImageNames = JsonPath.read(document, "$.events[" + i + "].imageNames");

                List<String> imageURLS = convertToArray(stringImageURLS);
                List<String> imageNames = convertToArray(stringImageNames);

                if (allDay) {
                    date = JsonPath.read(document, "$.events[" + i + "].date");
                } else {
                    fDate = JsonPath.read(document, "$.events[" + i + "].fromDate");
                    tDate = JsonPath.read(document, "$.events[" + i + "].toDate");
                }

                if (starred) {
                    if (allDay) {
                        eventList.add(new Event(eid, title, location, description, allDay, date, fromTime, toTime, coverURL, coverName, eventStorageKey, starred, imageURLS, imageNames));
                    } else {
                        eventList.add(new Event(eid, title, location, description, allDay, fDate, tDate, fromTime, toTime, coverURL, coverName, eventStorageKey, starred, imageURLS, imageNames));
                    }
                }


                i++;
            }
            View view = rootView.findViewById(R.id.highlights_event);
            if (eventList.size() == 0) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return eventList;
    }

    private List<String> convertToArray(String item) {
        String category = item.replaceAll("\\s+", "");
        category = category.replace("[", "");
        category = category.replace("]", "");

        return new ArrayList<>(Arrays.asList(category.split(",")));
    }

    class HomeEventAdapter extends RecyclerView.Adapter<HomeEventAdapter.HomeEventViewHolder> {

        private List<Event> eventList;
        private Context context;
        private String date;

        HomeEventAdapter(Context context, List<Event> eventList) {
            this.context = context;
            this.eventList = eventList;
        }

        @Override
        public HomeEventAdapter.HomeEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_event_starred, parent, false);
            return new HomeEventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(HomeEventAdapter.HomeEventViewHolder holder, int position) {
            final Event item = eventList.get(position);

            holder.home_event_title.setText(item.getTitle());
            holder.home_event_date.setText(getDate(item));
            Glide.with(this.context)
                    .load(item.getCoverURL())
                    .into(holder.home_event_cover);

            holder.home_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                    intent.putExtra("myEvent", item);
                    getContext().startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        private String getDate(Event item) {
            if (item.getAllDay()) {
                date = convertDate(item.getDate());
            } else {
                date = convertDate(item.getFromDate()) + " - " + convertDate(item.getToDate());
            }

            return date;
        }

        private String convertDate(long date) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            return formatter.format(date);
        }

        class HomeEventViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.home_event) View home_event;
            @BindView(R.id.home_event_title) TextView home_event_title;
            @BindView(R.id.home_event_date) TextView home_event_date;
            @BindView(R.id.home_event_cover) ImageView home_event_cover;

            HomeEventViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}

