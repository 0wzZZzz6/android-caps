package com.android.curlytops.suroytabukidnon.Account.Fragment;

import android.content.Context;
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
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Model.Bookmark;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.Municipality.Tab.Fragment.More;
import com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Detail.TabItemDetailActivity;
import com.android.curlytops.suroytabukidnon.R;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by jan_frncs
 */

public class SavedPlaces extends Fragment {

    private static final String TAG = "SavedPlaces";
    private static final String jsonPathNode_bookmarkplaces = "bookmark_places";

    List<MunicipalityItem> filtered_places = new ArrayList<>();
    List<String> municipalities = new ArrayList<>();

    @BindView(R.id.recyclerview_bookmark)
    RecyclerView recyclerView;

    SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter;

    public static SavedPlaces newInstance() {
        SavedPlaces savedPlaces = new SavedPlaces();
        Bundle args = new Bundle();
        savedPlaces.setArguments(args);
        return savedPlaces;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        municipalities = new BaseActivity().readAvailableMunicipalities(getContext());
        sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        filterPlaces();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        if (filtered_places.size() > 0) {
            Log.d("SHIELAMAE", "not empty");
            view = inflater.inflate(R.layout.fragment_recyclerview_bookmark,
                    container, false);
            ButterKnife.bind(this, view);
        } else {
            Log.d("SHIELAMAE", "empty");
            view = inflater.inflate(R.layout.empty_state, container, false);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (filtered_places.size() > 0) {
            filtered_places.clear();
            filterPlaces();
            sectionedRecyclerViewAdapter.notifyDataSetChanged();
            sectionedRecyclerViewAdapter.removeAllSections();
            sectionAdapter();
            Log.d("SHIELAMAE", filtered_places.size() + "");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (filtered_places.size() > 0) {
            filtered_places.clear();
            filterPlaces();
            sectionedRecyclerViewAdapter.notifyDataSetChanged();
            sectionedRecyclerViewAdapter.removeAllSections();
            sectionAdapter();
            Log.d("SHIELAMAE", filtered_places.size() + "");
        }
    }

    private List<Bookmark> readBookmark_places() {

        List<Bookmark> bookmarkList_places = new ArrayList<>();
        try {
            FileInputStream fis = getContext().openFileInput("bookmark_places.json");
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
                int length = JsonPath.read(document, "$.bookmark_places.length()");
                int i = 0;
                while (i < length) {
                    String bid = JsonPath.read(document,
                            jsonPath(i, "b_id", jsonPathNode_bookmarkplaces));
                    String item_id = JsonPath.read(document,
                            jsonPath(i, "item_id", jsonPathNode_bookmarkplaces));

                    bookmarkList_places.add(new Bookmark(bid, item_id));
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookmarkList_places;
    }

    private void filterPlaces() {
        List<MunicipalityItem> itemList = new BaseActivity().readMunicipalityItems(getContext());
        List<Bookmark> bookmarkList_places = readBookmark_places();

        for (int i = 0; i < bookmarkList_places.size(); i++) {
            for (int j = 0; j < itemList.size(); j++) {
                if (bookmarkList_places.get(i).item_id.equalsIgnoreCase(itemList.get(j).id)) {
                    filtered_places.add(itemList.get(j));
                    itemList.remove(j);
                }
            }
        }

        Collections.sort(filtered_places, new Comparator<MunicipalityItem>() {
            @Override
            public int compare(MunicipalityItem o1, MunicipalityItem o2) {
                return o1.municipalityStorageKey.compareToIgnoreCase(o2.municipalityStorageKey);
            }
        });
    }

    private String jsonPath(int index, String keyword, String item) {
        return "$." + item + "[" + index + "]." + keyword;
    }

    class SavedPlacesAdapter extends
            RecyclerView.Adapter<SavedPlacesAdapter.SavedPlacesViewHolder> {
        private List<MunicipalityItem> itemList;
        private Context context;

        SavedPlacesAdapter(Context context, List<MunicipalityItem> new_itemList) {
            this.itemList = new_itemList;
            this.context = context;
        }

        @Override
        public SavedPlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.saved_events_item, parent, false);
            return new SavedPlacesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SavedPlacesViewHolder holder, int position) {
            final MunicipalityItem municipalityItem = itemList.get(position);
            holder.saved_event_title.setText(municipalityItem.title);
            String[] parts = municipalityItem.municipalityStorageKey.split("_");
            final String municipality = parts[0];
            holder.saved_event_title.setText(municipalityItem.title);
            holder.saved_event_item_municipality.setText(municipality);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TabItemDetailActivity.class);
                    intent.putExtra("municipalityItem", municipalityItem);
                    intent.putExtra("municipalityId", municipality);
                    startActivity(intent);
                }
            });

            Log.d(TAG, municipalityItem + " item    --   " + municipality + "  -- id");
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        class SavedPlacesViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.saved_event_item_title)
            TextView saved_event_title;
            @BindView(R.id.saved_event_item_municipality)
            TextView saved_event_item_municipality;

            SavedPlacesViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    public void sectionAdapter() {
        for (String municipality : municipalities) {
            List<MunicipalityItem> municipalityItem = sortByMunicipality(municipality);
            if (municipalityItem.size() > 0) {
                sectionedRecyclerViewAdapter.addSection(new ExpandableSection(municipality, municipalityItem));
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionedRecyclerViewAdapter);
    }

    private List<MunicipalityItem> sortByMunicipality(String municipality) {

        List<MunicipalityItem> items = new ArrayList<>();
        for (MunicipalityItem municipalityItem : filtered_places) {
            if (municipalityItem.municipality.equalsIgnoreCase(municipality)) {
                items.add(municipalityItem);
            }
        }

        Log.d(TAG, items.size() + "");

        return items;
    }

    private class ExpandableSection extends StatelessSection {

        String title;
        List<MunicipalityItem> list;
        boolean expanded = false;

        ExpandableSection(String title, List<MunicipalityItem> list) {
            super(new SectionParameters.Builder(R.layout.saved_events_item)
                    .headerResourceId(R.layout.section_expandable_header)
                    .build());

            this.title = title;
            this.list = list;
        }

        @Override
        public int getContentItemsTotal() {
            return expanded ? list.size() : 0;
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            final MunicipalityItem municipalityItem = list.get(position);
            itemHolder.saved_event_title.setText(municipalityItem.title);
            String[] parts = municipalityItem.municipalityStorageKey.split("_");
            final String municipality = parts[0];
            itemHolder.saved_event_title.setText(municipalityItem.title);
            itemHolder.saved_event_item_municipality.setText(municipality);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TabItemDetailActivity.class);
                    intent.putExtra("municipalityItem", municipalityItem);
                    intent.putExtra("municipalityId", municipality);
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
            final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            int letter = list.size();
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(String.valueOf(letter), R.color.grey_100);

            headerHolder.itemCount.setImageDrawable(textDrawable);
            headerHolder.headerTitle.setText(title);
            headerHolder.headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expanded = !expanded;
                    headerHolder.headerArrow.setImageResource(
                            expanded ? R.drawable.ic_arrow_drop_up_white_24dp : R.drawable.ic_arrow_drop_down_white_24dp
                    );
                    sectionedRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.section_exapandable_item_view)
        View headerView;
        @BindView(R.id.section_expandable_header_title)
        TextView headerTitle;
        @BindView(R.id.section_expandable_header_arrow)
        ImageView headerArrow;
        @BindView(R.id.section_expandable_header_count)
        ImageView itemCount;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.saved_event_item_title)
        TextView saved_event_title;
        @BindView(R.id.saved_event_item_municipality)
        TextView saved_event_item_municipality;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}

