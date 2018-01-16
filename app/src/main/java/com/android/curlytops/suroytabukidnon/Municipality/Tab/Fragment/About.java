package com.android.curlytops.suroytabukidnon.Municipality.Tab.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.Event.EventDetailActivity;
import com.android.curlytops.suroytabukidnon.GrafixGallery.DetailActivity;
import com.android.curlytops.suroytabukidnon.GrafixGallery.GalleryAdapter;
import com.android.curlytops.suroytabukidnon.GrafixGallery.RecyclerItemClickListener;
import com.android.curlytops.suroytabukidnon.Model.AboutModel;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.ImageModel;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.Municipality.Tab.TabActivity;
import com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Detail.TabItemDetailActivity;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.ms.square.android.expandabletextview.ExpandableTextView;

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

    private static final String TAG = "AboutModel";
    private static final String viewMode_places = "places";

    StarredAdapter starredAdapter;
    TaggedAdapter taggedAdapter;
    GalleryAdapter galleryAdapter;

    String municipalityId, municipality;
    AboutModel aboutModel = new AboutModel();
    ArrayList<ImageModel> data = new ArrayList<>();
    List<Event> newEventList = new ArrayList<>();
    List<MunicipalityItem> newItemList = new ArrayList<>();
    SnapHelper snapHelperPlaces = new GravitySnapHelper(Gravity.START);
    SnapHelper snapHelperEvents = new GravitySnapHelper(Gravity.START);

    @BindView(R.id.fragment_about_card_description)
    View fragment_about_card_description;
    @BindView(R.id.fragment_about_description)
    ExpandableTextView description;
    @BindView(R.id.fragment_about_feature)
    RecyclerView recyclerView_starred;
    @BindView(R.id.fragment_about_sample)
    RecyclerView recyclerView_samples;

    @BindView(R.id.fragment_about_tagged)
    RecyclerView recyclerView_tagged;
//    @BindView(R.id.taggedCount)
//    TextView taggedCount;

    @BindView(R.id.fragment_about_featured_places)
    View featured_places;
    @BindView(R.id.fragment_about_tagged_events)
    View tagged_events;
    @BindView(R.id.fragment_about_tagged_samplePictures)
    View sample_pictures;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getActivity()) != null) {
            municipalityId = ((TabActivity) getActivity()).getMunicipalityId();
            municipality = ((TabActivity) getActivity()).getMunicipality();
        }
        List<Event> eventList = ((TabActivity) getActivity()).eventList;
        List<MunicipalityItem> itemList = ((TabActivity) getActivity()).itemList;

        if (((TabActivity) getActivity()).aboutModel != null) {
            aboutModel = ((TabActivity) getActivity()).aboutModel;
        }

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

        if (aboutModel.description != null) {
            fragment_about_card_description.setVisibility(View.VISIBLE);
            description.setText(aboutModel.description);
        }

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
//            taggedCount.setText(String.valueOf(newEventList.size()));
            taggedAdapter = new TaggedAdapter(getContext(), newEventList);
            recyclerView_tagged.setHasFixedSize(false);
            recyclerView_tagged.setLayoutManager(new LinearLayoutManager(this.getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            recyclerView_tagged.setAdapter(taggedAdapter);
            snapHelperEvents.attachToRecyclerView(recyclerView_tagged);
        }

        try {
            if (aboutModel.imageURLS.size() > 0) {
                sample_pictures.setVisibility(View.VISIBLE);
                List<String> imageURLS = aboutModel.imageURLS;
                for (int i = 0; i < imageURLS.size(); i++) {
                    ImageModel imageModel = new ImageModel();
                    imageModel.setName("Image " + i);
                    imageModel.setUrl(imageURLS.get(i));
                    data.add(imageModel);
                }

                galleryAdapter = new GalleryAdapter(getContext(), data, viewMode_places);
                recyclerView_samples.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerView_samples.setHasFixedSize(true);
                recyclerView_samples.setAdapter(galleryAdapter);

                recyclerView_samples.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                        new RecyclerItemClickListener.OnItemClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {

                                Intent intent =
                                        new Intent(getActivity(), DetailActivity.class);
                                intent.putParcelableArrayListExtra("data", data);
                                intent.putExtra("pos", position);
                                startActivity(intent);

                            }
                        }));
            }
        } catch (Exception e) {
            e.printStackTrace();
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

            Glide.with(this.context)
                    .load(event.coverURL)
                    .into(holder.home_event_item_imageView);
            holder.home_event_item_title.setText(event.title);
            holder.home_event_item_date.setText(getDate(event));
            holder.home_event_item.setOnClickListener(new View.OnClickListener() {
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
            @BindView(R.id.home_event_item)
            View home_event_item;
            @BindView(R.id.home_event_item_title)
            TextView home_event_item_title;
            @BindView(R.id.home_event_item_date)
            TextView home_event_item_date;
            @BindView(R.id.home_event_item_imageView)
            ImageView home_event_item_imageView;

            TaggedViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
