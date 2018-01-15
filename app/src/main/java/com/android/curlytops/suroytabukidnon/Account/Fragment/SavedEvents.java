package com.android.curlytops.suroytabukidnon.Account.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Event.EventDetailActivity;
import com.android.curlytops.suroytabukidnon.Model.Bookmark;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.R;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import org.joda.time.DateTime;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by jan_frncs
 */

public class SavedEvents extends Fragment {

    private static final String TAG = "SavedEvents";
    private static final String jsonPathNode_bookmarkEvents = "bookmark_events";


    List<Event> filtered_events = new ArrayList<>();

    @BindView(R.id.recyclerview_bookmark)
    RecyclerView recyclerView;
    @BindView(R.id.noBookmarked)
    View noBookmarked;

    SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter;
    String[] months;


    public static SavedEvents newInstance() {
        SavedEvents savedEvents = new SavedEvents();
        Bundle args = new Bundle();
        savedEvents.setArguments(args);
        return savedEvents;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        months = getResources().getStringArray(R.array.months);
        sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        filterEvents();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_recyclerview_bookmark, container, false);
        ButterKnife.bind(this, view);

        if (filtered_events.size() > 0) {
            Log.d("SHIELAMAE", "not empty");
            noBookmarked.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            Log.d("SHIELAMAE", "empty");
            noBookmarked.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (filtered_events.size() > 0) {
            filtered_events.clear();
            filterEvents();
            sectionedRecyclerViewAdapter.notifyDataSetChanged();
            sectionedRecyclerViewAdapter.removeAllSections();
            SectionAdapter();
            Log.d("SHIELAMAE", filtered_events.size() + "");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (filtered_events.size() > 0) {
            filtered_events.clear();
            filterEvents();
            sectionedRecyclerViewAdapter.notifyDataSetChanged();
            sectionedRecyclerViewAdapter.removeAllSections();
            SectionAdapter();
        }
    }

    public void SectionAdapter() {
        for (String month : months) {
            List<Event> listEvents = sortEventList(month);
            if (listEvents.size() > 0)
                sectionedRecyclerViewAdapter.addSection(new EventSection(month, listEvents));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionedRecyclerViewAdapter);
    }

    private List<Event> sortEventList(String month) {
        List<Event> events = new ArrayList<>();
        for (Event ev : filtered_events) {
            if (ev.allDay) {    // if event is allDay == true
                if (month.equalsIgnoreCase(getMonth(ev.startDate)))
                    events.add(ev);
            } else {            // if event is allDay == false
                if (month.equalsIgnoreCase(getMonth(ev.startDate))) {
                    events.add(ev);
                } else if (month.equalsIgnoreCase(getMonth(ev.endDate))) {
                    events.add(ev);
                }
            }
        }

        return events;
    }

    private String getMonth(long date) {
        String month = null;
        Date tempDate = new Date(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tempDate);
        int monthNumeric = calendar.get(Calendar.MONTH);

        for (int i = 0; i < 12; i++) {
            if (i == monthNumeric) {
                month = months[i];
            }
        }

        return month;
    }

    private String getDate(Event item) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");

        String date;
        if (item.allDay) {
            date = convertDate(item.startDate);
        } else {
            DateTime fromDate = new DateTime(item.startDate);
            DateTime toDate = new DateTime(item.endDate);

            if (fromDate.getMonthOfYear() == toDate.getMonthOfYear() &&
                    fromDate.getYear() == toDate.getYear()) {

                return simpleDateFormat.format(item.startDate) + " " +
                        fromDate.getDayOfMonth() + " - " + toDate.getDayOfMonth() + ", " +
                        fromDate.getYear();
            } else if (!(fromDate.getMonthOfYear() == toDate.getMonthOfYear()) &&
                    fromDate.getYear() == toDate.getYear()) {
                return simpleDateFormat.format(item.startDate) + " " + fromDate.getDayOfMonth()
                        + " - " +
                        simpleDateFormat.format(item.endDate) + " " + toDate.getDayOfMonth() + " ," +
                        fromDate.getYear();
            } else {
                date = convertDate(item.startDate) + " - " + convertDate(item.endDate);
            }
        }

        return date;
    }

    private String convertDate(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatter.format(date);
    }

    private List<Bookmark> readBookmark_events() {
        List<Bookmark> bookmarkList_events = new ArrayList<>();
        try {
            FileInputStream fis = getContext().openFileInput("bookmark_events.json");
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
                int length = JsonPath.read(document, "$.bookmark_events.length()");
                int i = 0;
                while (i < length) {
                    String bid = JsonPath.read(document,
                            jsonPath(i, "b_id", jsonPathNode_bookmarkEvents));
                    String item_id = JsonPath.read(document,
                            jsonPath(i, "item_id", jsonPathNode_bookmarkEvents));

                    bookmarkList_events.add(new Bookmark(bid, item_id));

                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookmarkList_events;
    }

    private void filterEvents() {
        List<Event> eventList = new BaseActivity().readEvents(getContext());
        List<Bookmark> bookmarkList_events = readBookmark_events();

        for (int i = 0; i < bookmarkList_events.size(); i++) {
            for (int j = 0; j < eventList.size(); j++) {
                if (bookmarkList_events.get(i).item_id.equalsIgnoreCase(eventList.get(j).e_id)) {
                    filtered_events.add(eventList.get(j));
                    eventList.remove(j);
                }
            }
        }
    }

    private String jsonPath(int index, String keyword, String jsonPathNode) {
        return "$." + jsonPathNode + "[" + index + "]." + keyword;
    }

    private class EventSection extends StatelessSection {
        String title;
        List<Event> list;

        EventSection(String title, List<Event> list) {

            super(new SectionParameters.Builder(R.layout.event_item)
                    .headerResourceId(R.layout.event_item_header)
                    .build());

            this.title = title;
            this.list = list;
            Collections.sort(list, new Comparator<Event>() {
                @Override
                public int compare(Event e1, Event e2) {
                    return Long.compare(e1.startDate, e2.startDate);
                }
            });
        }

        @Override
        public int getContentItemsTotal() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            final Event event = list.get(position);

            String letter = event.title.substring(0, 1).toUpperCase();
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(letter, R.color.colorAccent);

            itemHolder.eventTitle.setText(event.title);
            itemHolder.eventDate.setText(getDate(event));
            itemHolder.eventLocation.setText(event.location);
            itemHolder.eventLetter.setImageDrawable(textDrawable);
            itemHolder.eventItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), EventDetailActivity.class);
                    intent.putExtra("myEvent", event);
                    startActivity(intent);
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.eventHeaderMonth.setText(title);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.event_header_month)
        TextView eventHeaderMonth;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.event_title)
        TextView eventTitle;
        @BindView(R.id.event_location)
        TextView eventLocation;
        @BindView(R.id.event_date)
        TextView eventDate;
        @BindView(R.id.event_letter)
        ImageView eventLetter;
        @BindView(R.id.event_item)
        LinearLayout eventItem;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
