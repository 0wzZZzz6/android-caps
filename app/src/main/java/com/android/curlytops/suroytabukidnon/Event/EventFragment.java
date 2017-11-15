package com.android.curlytops.suroytabukidnon.Event;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.curlytops.suroytabukidnon.MainActivity;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jan_frncs
 */

public class EventFragment extends Fragment implements OnDateSelectedListener {
    List<Event> eventList = new ArrayList<>();
    ArrayList<CalendarDay> dates = new ArrayList<>();
    BottomSheetBehavior behavior;
    Context context;
    Toolbar toolbar;
    SectionedRecyclerViewAdapter sectionAdapter;
    String[] months;
    Animation growAnimation, shrinkAnimation;

    @BindView(R.id.event_calendarView)
    MaterialCalendarView widget;
    @BindView(R.id.eventBottomsheet_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.eventBottomsheet)
    View bottomSheet;
    @BindView(R.id.event_fab)
    FloatingActionButton fab;

    boolean fabStat = false;
    boolean openFab = true;

    public EventFragment() {
    }

    public static EventFragment newInstance() {
        EventFragment eventFragment = new EventFragment();
        Bundle args = new Bundle();
        eventFragment.setArguments(args);
        return eventFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        context = this.getContext();
        months = getResources().getStringArray(R.array.months);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_layout, container, false);
        ButterKnife.bind(this, rootView);
        sectionAdapter = new SectionedRecyclerViewAdapter();
        toolbar = ((MainActivity) getActivity()).toolbar;

        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    toolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_close_white_24dp, null));
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            collapseBottomSheet();
                            toolbar.setNavigationIcon(null);
                        }
                    });
                } else {
                    toolbar.setNavigationIcon(null);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        growAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.simple_grow);
        shrinkAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.simple_shrink);

        growAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                openFab = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        calendarViewWidget();
        firebase();
        readEvents(0);
        SectionAdapter("default");

        return rootView;
    }

    public void collapseBottomSheet() {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @OnClick(R.id.event_fab)
    public void fabClick(View view) {
        long millis = System.currentTimeMillis();
        widget.setCurrentDate(ConvertCalendarDate(millis));
        widget.setSelectedDate(ConvertCalendarDate(millis));
        readEvents(0);              // call this to reset the recyclerview
        SectionAdapter("default");           // call this to reset the recyclerview
        collapseBottomSheet();
        if (fabStat) {
            fab.startAnimation(shrinkAnimation);
            fabStat = false;
            openFab = true;
        }
    }

    private void firebase() {
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

                        if (event.getAllDay()) {
                            eventObject.put("date", event.getDate());
                        } else {
                            eventObject.put("fromDate", event.getFromDate());
                            eventObject.put("toDate", event.getToDate());
                        }

                        data.put(eventObject);
                        finalEventObject.put("events", data);
                        FileOutputStream fos = context.openFileOutput("event.json", MODE_PRIVATE);
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
                collapseBottomSheet();
                widget.removeDecorators();
                readEvents(0);
                widget.addDecorator(new EventDecorator(Color.RED, dates));
                SectionAdapter("default");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    public void readEvents(long milliseconds) {
        eventList.clear();
        dates.clear();
        sectionAdapter.notifyDataSetChanged();
        sectionAdapter.removeAllSections();

        try {
            FileInputStream fis = context.openFileInput("event.json");
            BufferedInputStream bis = new BufferedInputStream(fis);
            StringBuilder b = new StringBuilder();

            Date fromDate = new Date();
            Date toDate = new Date();
            Calendar calFromDate = Calendar.getInstance();
            Calendar calToDate = Calendar.getInstance();
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

                if (allDay) {
                    date = JsonPath.read(document, "$.events[" + i + "].date");
                    dates.add(ConvertCalendarDate(date));
                } else {
                    fDate = JsonPath.read(document, "$.events[" + i + "].fromDate");
                    tDate = JsonPath.read(document, "$.events[" + i + "].toDate");

                    // test
                    fromDate = new Date(fDate);
                    toDate = new Date(tDate);

                    calFromDate.setTime(fromDate);
                    calFromDate.add(Calendar.DATE, -1);

                    calToDate.setTime(toDate);
                    calToDate.add(Calendar.DATE, 1);

                    while (calFromDate.getTime().before(toDate)) {
                        calFromDate.add(Calendar.DATE, 1);
                        dates.add(SampleThis(calFromDate.getTime()));
                    }
                }

                if (milliseconds == 0) {// show all events
                    if (allDay) {
                        eventList.add(new Event(eid, title, location, description, allDay, date, fromTime, toTime));
                    } else {
                        eventList.add(new Event(eid, title, location, description, allDay, fDate, tDate, fromTime, toTime));
                    }
                } else if (milliseconds == date) {// show only events sorted by longDate
                    if (allDay) {
                        eventList.add(new Event(eid, title, location, description, allDay, date, fromTime, toTime));
                    }
                }

                if (!allDay && inRange(milliseconds, fromDate, toDate)) {
                    eventList.add(new Event(eid, title, location, description, allDay, fDate, tDate, fromTime, toTime));
                }

                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean inRange(long milliSeconds, Date fromDate, Date toDate) {
        Date inRange_milliSeconds = new Date(milliSeconds);
        boolean result = false;
        if ((inRange_milliSeconds.equals(fromDate) || inRange_milliSeconds.after(fromDate)) &&
                ((inRange_milliSeconds.equals(toDate) || inRange_milliSeconds.before(toDate)))) {
            result = true;
        }

        return result;
    }

    private List<Event> getEventMonth(String month) {
        List<Event> events = new ArrayList<>();
        for (Event ev : eventList) {
            if (ev.getAllDay() && month.equalsIgnoreCase(getMonth(ev.getDate()))) {
                events.add(ev);
            }
            if (!ev.getAllDay() && month.equalsIgnoreCase(getMonth(ev.getFromDate()))) {
                events.add(ev);
            } else if (!ev.getAllDay() && month.equalsIgnoreCase(getMonth(ev.getToDate()))) {
                events.add(ev);
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

    private String getDay(long longDate) {
        Date date = new Date(longDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    private CalendarDay SampleThis(Date date) {
        String[] part;
        int yyyy, MM, dd;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String stringDate = formatter.format(date);
        part = stringDate.split("-");

        yyyy = Integer.parseInt(part[0]);
        MM = Integer.parseInt(part[1]) - 1;
        dd = Integer.parseInt(part[2]);

        return CalendarDay.from(yyyy, MM, dd);
    }

    private CalendarDay ConvertCalendarDate(long longDate) {
        String[] part;
        int yyyy, MM, dd;

        Date tempLongDate = new Date(longDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(tempLongDate);
        part = date.split("-");

        yyyy = Integer.parseInt(part[0]);
        MM = Integer.parseInt(part[1]) - 1;
        dd = Integer.parseInt(part[2]);


        Log.d("part", part[0] + Integer.parseInt(part[1]) + part[2]);

        return CalendarDay.from(yyyy, MM, dd);
    }

    private void calendarViewWidget() {
        widget.setOnDateChangedListener(this);
        widget.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        Calendar instance = Calendar.getInstance();
        widget.setSelectedDate(instance.getTime());

        Calendar instance1 = Calendar.getInstance();
        instance1.set(instance1.get(Calendar.YEAR), Calendar.JANUARY, 1);

        Calendar instance2 = Calendar.getInstance();
        instance2.set(instance2.get(Calendar.YEAR), Calendar.DECEMBER, 31);

        widget.state().edit()
                .setMinimumDate(instance1.getTime())
                .setMaximumDate(instance2.getTime())
                .commit();

        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            widget.addDecorator(new EventDecorator(Color.RED, calendarDays));
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget,
                               @NonNull CalendarDay date, boolean selected) {

        boolean isEvent = false;
        long millis = System.currentTimeMillis();

        for (int i = 0; i < dates.size(); i++) {
            if (CalendarDay.from(date.getYear(), date.getMonth(), date.getDay()).equals(dates.get(i))) {
                isEvent = true;
                fabStat = true;
                break;
            } else {
                isEvent = false;
                fabStat = true;
            }
        }

        if (isEvent) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String string_date = String.valueOf(date.getYear()) + "-" +
                    String.valueOf(date.getMonth() + 1) + "-" +
                    String.valueOf(date.getDay());
            long milliseconds = 0;
            try {
                Date d = formatter.parse(string_date);
                milliseconds = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            readEvents(milliseconds);
            SectionAdapter(getMonth(milliseconds));

            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            Toast.makeText(this.getContext(), "No event found.", Toast.LENGTH_SHORT).show();
        }

        if (fabStat && openFab) {
            fab.startAnimation(growAnimation);
        }
    }

    public void SectionAdapter(String mo) {
        for (String month : months) {
            List<Event> listEvents = getEventMonth(month);
            if (listEvents.size() > 0 && mo.equalsIgnoreCase(month)) {
                sectionAdapter.addSection(new EventSection(month, listEvents));
            } else if (listEvents.size() > 0 && mo.equals("default")) {
                sectionAdapter.addSection(new EventSection(month, listEvents));
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);
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
                    return Long.compare(e1.getDate(), e2.getDate());
                }
            });
        }

        @Override
        public int getContentItemsTotal() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new EventFragment.ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final EventFragment.ItemViewHolder itemHolder = (EventFragment.ItemViewHolder) holder;
            final Event event = list.get(position);

            String letter = event.getTitle().substring(0, 1).toUpperCase();
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(letter, R.color.colorAccent);

            itemHolder.eventTitle.setText(event.getTitle());
            itemHolder.eventLocation.setText(event.getLocation());
            itemHolder.eventLetter.setImageDrawable(textDrawable);
            itemHolder.eventItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                    intent.putExtra("myEvent", event);
                    getContext().startActivity(intent);
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new EventFragment.HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            EventFragment.HeaderViewHolder headerHolder = (EventFragment.HeaderViewHolder) holder;
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
        @BindView(R.id.event_letter)
        ImageView eventLetter;
        @BindView(R.id.event_item)
        View eventItem;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
