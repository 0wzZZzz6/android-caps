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

import java.util.ArrayList;
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
    SnapHelper snapHelperPlaces = new LinearSnapHelper();
    SnapHelper snapHelperEvents = new LinearSnapHelper();

    @BindView(R.id.fragment_about_feature)
    RecyclerView recyclerView_starred;

    @BindView(R.id.fragment_about_tagged)
    RecyclerView recyclerView_tagged;
    @BindView(R.id.taggedCount)
    TextView taggedCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        municipalityId = ((TabActivity) getActivity()).getMunicipalityId();
        municipality = ((TabActivity) getActivity()).getMunicipality();

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

        starredAdapter = new StarredAdapter(getContext(), newItemList);
        recyclerView_starred.setHasFixedSize(false);
        recyclerView_starred.setLayoutManager(new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView_starred.setAdapter(starredAdapter);
        snapHelperPlaces.attachToRecyclerView(recyclerView_starred);

        taggedCount.setText(String.valueOf(newEventList.size()));
        taggedAdapter = new TaggedAdapter(getContext(), newEventList);
        recyclerView_tagged.setHasFixedSize(false);
        recyclerView_tagged.setLayoutManager(new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView_tagged.setAdapter(taggedAdapter);
        snapHelperEvents.attachToRecyclerView(recyclerView_tagged);

        return view;
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
                    (parent.getContext()).inflate(R.layout.about_starred_item, parent, false);
            return new TaggedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TaggedViewHolder holder, int position) {
            final Event event = eventList.get(position);

            Glide.with(this.context)
                    .load(event.coverURL)
                    .into(holder.cover);

            holder.title.setText(event.title);
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), EventDetailActivity.class);
                    intent.putExtra("myEvent", event);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        class TaggedViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.about_starred_item_imageView)
            ImageView cover;
            @BindView(R.id.about_starred_item_title)
            TextView title;

            TaggedViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
