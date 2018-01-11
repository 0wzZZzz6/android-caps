package com.android.curlytops.suroytabukidnon.Municipality.Tab.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Event.EventDetailActivity;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.Municipality.Tab.TabActivity;
import com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Detail.TabItemDetailActivity;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */
public class About extends Fragment {

    private static final String TAG = "About";

    StarredAdapter starredAdapter;
    TaggedAdapter taggedAdapter;

    String municipalityId, municipality;
    List<Event> newEventList = new ArrayList<>();
    List<MunicipalityItem> newItemList = new ArrayList<>();
    SnapHelper snapHelperPlaces = new GravitySnapHelper(Gravity.START);
    SnapHelper snapHelperEvents = new GravitySnapHelper(Gravity.START);

    @BindView(R.id.fragment_about_feature)
    RecyclerView recyclerView_starred;

    @BindView(R.id.fragment_about_tagged)
    RecyclerView recyclerView_tagged;
    @BindView(R.id.taggedCount)
    TextView taggedCount;

    @BindView(R.id.fragment_about_featured_places)
    View featured_places;
    @BindView(R.id.fragment_about_tagged_events)
    View tagged_events;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getActivity()) != null) {
            municipalityId = ((TabActivity) getActivity()).getMunicipalityId();
            municipality = ((TabActivity) getActivity()).getMunicipality();
        }

        List<Event> eventList = new BaseActivity().readEvents(getContext());
        List<MunicipalityItem> itemList = new BaseActivity().readMunicipalityItems(getContext());

        for (Event event : eventList) {
            List<String> item = event.taggedMunicipality;

            if (item.contains(municipality)) {
                newEventList.add(event);
            }
        }

        for (MunicipalityItem item : itemList) {
            String itemMunicipality = item.municipality;

            if (item.starred && itemMunicipality.equalsIgnoreCase(municipality)) {
                newItemList.add(item);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (newItemList.size() > 0) {
            featured_places.setVisibility(View.VISIBLE);
            starredAdapter = new StarredAdapter(getContext(), newItemList);
            recyclerView_starred.setHasFixedSize(false);
            recyclerView_starred.setLayoutManager(new LinearLayoutManager(this.getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            recyclerView_starred.setAdapter(starredAdapter);
            snapHelperPlaces.attachToRecyclerView(recyclerView_starred);
        }

        if (newEventList.size() > 0) {
            Collections.sort(newEventList, new Comparator<Event>() {
                @Override
                public int compare(Event o1, Event o2) {
                    return Long.compare(o1.startDate, o2.startDate);
                }
            });
            tagged_events.setVisibility(View.VISIBLE);
            taggedCount.setText(String.valueOf(newEventList.size()));
            taggedAdapter = new TaggedAdapter(getContext(), newEventList);
            recyclerView_tagged.setHasFixedSize(false);
            recyclerView_tagged.setLayoutManager(new LinearLayoutManager(this.getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            recyclerView_tagged.setAdapter(taggedAdapter);
            snapHelperEvents.attachToRecyclerView(recyclerView_tagged);
        }

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
                        fromDate.getDayOfMonth() + " - " + toDate.getDayOfMonth() + " " +
                        fromDate.getYear();
            } else if (!(fromDate.getMonthOfYear() == toDate.getMonthOfYear()) &&
                    fromDate.getYear() == toDate.getYear()) {
                return simpleDateFormat.format(item.startDate) + " " + fromDate.getDayOfMonth()
                        + " - " +
                        simpleDateFormat.format(item.endDate) + " " + toDate.getDayOfMonth() + " " +
                        fromDate.getYear();
            } else {
                date = convertDate(item.startDate) + " - " + convertDate(item.endDate);
            }
        }

        return date;
    }

    private String convertDate(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        return formatter.format(date);
    }

    class StarredAdapter extends
            RecyclerView.Adapter<StarredAdapter.StarredViewHolder> {

        Context context;
        List<MunicipalityItem> itemList = new ArrayList<>();

        StarredAdapter(Context context, List<MunicipalityItem> itemList) {
            this.context = context;
            this.itemList = itemList;
        }

        @Override
        public StarredViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from
                    (parent.getContext()).inflate(R.layout.about_starred_item, parent, false);
            return new StarredViewHolder(view);
        }

        @Override
        public void onBindViewHolder(StarredViewHolder holder, int position) {
            final MunicipalityItem item = itemList.get(position);

            Glide.with(this.context)
                    .load(item.coverURL)
                    .into(holder.cover);
            holder.title.setText(item.title);
            holder.location.setText(item.location);
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TabItemDetailActivity.class);
                    intent.putExtra("municipalityItem", item);
                    intent.putExtra("municipalityId", municipalityId);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        class StarredViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.about_starred_item_imageView)
            ImageView cover;
            @BindView(R.id.about_starred_item_title)
            TextView title;
            @BindView(R.id.about_starred_item_location)
            TextView location;

            StarredViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    class TaggedAdapter extends
            RecyclerView.Adapter<TaggedAdapter.TaggedViewHolder> {

        Context context;
        List<Event> eventList = new ArrayList<>();

        TaggedAdapter(Context context, List<Event> eventList) {
            this.context = context;
            this.eventList = eventList;
        }

        @Override
        public TaggedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from
                    (parent.getContext()).inflate(R.layout.home_event_starred, parent, false);
            return new TaggedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TaggedViewHolder holder, int position) {
            final Event event = eventList.get(position);

            holder.home_event_title.setText(event.title);
            holder.home_event_date.setText(getDate(event));
            Glide.with(this.context)
                    .load(event.coverURL)
                    .into(holder.home_event_cover);

            holder.home_event_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra("myEvent", event);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        class TaggedViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.home_event)
            View home_event;
            @BindView(R.id.home_event_title)
            TextView home_event_title;
            @BindView(R.id.home_event_date)
            TextView home_event_date;
            @BindView(R.id.home_event_cover)
            ImageView home_event_cover;

            TaggedViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
