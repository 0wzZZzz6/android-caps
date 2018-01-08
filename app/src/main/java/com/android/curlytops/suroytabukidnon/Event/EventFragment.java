package com.android.curlytops.suroytabukidnon.Event;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.LinearLayout;
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

import org.joda.time.DateTime;
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
import java.util.Arrays;
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
import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by jan_frncs
 */

public class EventFragment extends Fragment implements OnDateSelectedListener {

    private static final String TAG = "EventFragment";
    private static final String jsonPathNode = "events";

    List<Event> eventList = new ArrayList<>();
    ArrayList<CalendarDay> dates = new ArrayList<>();
    BottomSheetBehavior behavior;
    Context context;
    Toolbar toolbar;
    SectionedRecyclerViewAdapter sectionAdapter;
    String[] months;
    Animation growAnimation, shrinkAnimation;

    @BindView(R.id.fragment_event_calendarView)
    MaterialCalendarView widget;
    @BindView(R.id.fragment_event_bottomSheet_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_event_bottomSheet)
    View bottomSheet;
    @BindView(R.id.fragment_event_fab)
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        ButterKnife.bind(this, rootView);

        sectionAdapter = new SectionedRecyclerViewAdapter();
        if ((getActivity()) != null) {
            toolbar = ((MainActivity) getActivity()).toolbar;
        }

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
                Calendar cal = Calendar.getInstance();
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                fab.setImageBitmap(textAsBitmap(String.valueOf(dayOfMonth), Color.WHITE));
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



        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarViewWidget();
        firebaseEvents();
        readEvents(0);
        SectionAdapter("default");
    }

    public void collapseBottomSheet() {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @OnClick(R.id.fragment_event_fab)
    public void fabClick() {
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

    private void firebaseEvents() {
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
                            eventObject.put("taggedMunicipality", event.taggedMunicipality);

                            if (event.allDay) {
                                eventObject.put("startDate", event.startDate);
                            } else {
                                eventObject.put("startDate", event.startDate);
                                eventObject.put("endDate", event.endDate);
                            }
                            data.put(eventObject);
                            rootEventObject.put("events", data);
                            FileOutputStream fos = context.openFileOutput("event.json", MODE_PRIVATE);
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
                String eid = JsonPath.read(document, jsonPath(i, "e_id"));
                String title = JsonPath.read(document, jsonPath(i, "title"));
                String location = JsonPath.read(document, jsonPath(i, "location"));
                String description = JsonPath.read(document, jsonPath(i, "description"));
                boolean allDay = JsonPath.read(document, jsonPath(i, "allDay"));
                String fromTime = JsonPath.read(document, jsonPath(i, "fromTime"));
                String toTime = JsonPath.read(document, jsonPath(i, "toTime"));
                String coverURL = JsonPath.read(document, jsonPath(i, "coverURL"));
                String coverName = JsonPath.read(document, jsonPath(i, "coverName"));
                String eventStorageKey = JsonPath.read(document, jsonPath(i, "eventStorageKey"));
                boolean starred = JsonPath.read(document, jsonPath(i, "starred"));
                String stringImageURLS = JsonPath.read(document, jsonPath(i, "imageURLS"));
                String stringImageNames = JsonPath.read(document, jsonPath(i, "imageNames"));
                String stringTaggedMunicipality = JsonPath.read(document, jsonPath(i, "taggedMunicipality"));

                List<String> imageURLS = convertToArray(stringImageURLS);
                List<String> imageNames = convertToArray(stringImageNames);
                List<String> taggedMunicipality = convertToArray(stringTaggedMunicipality);

                if (allDay) {
                    date = JsonPath.read(document, jsonPath(i, "startDate"));
                    dates.add(ConvertCalendarDate(date));
                } else {
                    fDate = JsonPath.read(document, jsonPath(i, "startDate"));
                    tDate = JsonPath.read(document, jsonPath(i, "endDate"));

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
                        eventList.add(new Event(eid, title, location, description, true, date,
                                fromTime, toTime, coverURL, coverName, eventStorageKey, starred,
                                imageURLS, imageNames, taggedMunicipality));
                    } else {
                        eventList.add(new Event(eid, title, location, description, false, fDate,
                                tDate, fromTime, toTime, coverURL, coverName, eventStorageKey, starred,
                                imageURLS, imageNames, taggedMunicipality));
                    }
                } else if (milliseconds == date) {// show only events sorted by longDate
                    if (allDay) {
                        eventList.add(new Event(eid, title, location, description, true, date,
                                fromTime, toTime, coverURL, coverName, eventStorageKey, starred,
                                imageURLS, imageNames, taggedMunicipality));
                    }
                }

                if (!allDay && inRange(milliseconds, fromDate, toDate)) {
                    eventList.add(new Event(eid, title, location, description, false, fDate,
                            tDate, fromTime, toTime, coverURL, coverName, eventStorageKey, starred,
                            imageURLS, imageNames, taggedMunicipality));
                }

                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String jsonPath(int index, String keyword) {
        return "$." + jsonPathNode + "[" + index + "]." + keyword;
    }

    private List<String> convertToArray(String item) {
        String category = item.replaceAll("\\s+", "");
        category = category.replace("[", "");
        category = category.replace("]", "");

        return new ArrayList<>(Arrays.asList(category.split(",")));
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
            if (ev.allDay && month.equalsIgnoreCase(getMonth(ev.startDate))) {
                events.add(ev);
            }
            if (!ev.allDay && month.equalsIgnoreCase(getMonth(ev.startDate))) {
                events.add(ev);
            } else if (!ev.allDay && month.equalsIgnoreCase(getMonth(ev.endDate))) {
                events.add(ev);
            }
        }

        return events;
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

    private static Bitmap textAsBitmap(String text, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(30);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
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

        Log.d(TAG, String.valueOf(date));

        boolean isEvent = false;
//        long millis = System.currentTimeMillis();

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
            return new EventFragment.ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final EventFragment.ItemViewHolder itemHolder = (EventFragment.ItemViewHolder) holder;
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
