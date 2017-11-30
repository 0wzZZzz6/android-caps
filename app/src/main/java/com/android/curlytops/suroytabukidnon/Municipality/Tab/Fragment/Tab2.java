package com.android.curlytops.suroytabukidnon.Municipality.Tab.Fragment;

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
import android.widget.Toast;

import com.android.curlytops.suroytabukidnon.Event.EventDetailsActivity;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.Municipality.Tab.TabActivity;
import com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Details.TabItemDetailsActivity;
import com.android.curlytops.suroytabukidnon.R;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


/**
 * Created by jan_frncs
 */

public class Tab2 extends Fragment {

    SectionedRecyclerViewAdapter sectionAdapter;
    List<MunicipalityItem> itemList = new ArrayList<>();
    List<String> categories = new ArrayList<>();
    String id = null;
    int itemLength = 0;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = ((TabActivity) getActivity()).get_id();
        categories = Arrays.asList(getResources().getStringArray(R.array.catergory));
        readMunicipalityItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        if (itemLength > 0) {
            view = inflater.inflate(R.layout.recyclerview, container, false);
            ButterKnife.bind(this, view);
            sectionAdapter = new SectionedRecyclerViewAdapter();
            sectionAdapter();
        } else {
            Toast.makeText(this.getContext(), "itemLengh: " + itemLength, Toast.LENGTH_SHORT).show();
            view = inflater.inflate(R.layout.empty_state, container, false);
        }

        return view;
    }

    public void readMunicipalityItems() {
        try {
            FileInputStream fis = getContext().openFileInput("municipality.json");
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
                itemLength = JsonPath.read(document, "$." + id + ".length()");
                int i = 0;
                while (i < itemLength) {
                    String iid = JsonPath.read(document, "$." + id + "[" + i + "].id");
                    String title = JsonPath.read(document, "$." + id + "[" + i + "].title");
                    String location = JsonPath.read(document, "$." + id + "[" + i + "].location");
                    String contact = JsonPath.read(document, "$." + id + "[" + i + "].contact");
                    String stringCategory = JsonPath.read(document, "$." + id + "[" + i + "].category");
                    String stringImageURLS = JsonPath.read(document, "$." + id + "[" + i + "].imageURLS");
                    String stringImageNames = JsonPath.read(document, "$." + id + "[" + i + "].imageNames");

                    // new
                    String municipalityStorageKey = JsonPath.read(document, "$." + id + "[" + i + "].municipalityStorageKey");
                    String coverURL = JsonPath.read(document, "$." + id + "[" + i + "].coverURL");
                    String coverName = JsonPath.read(document, "$." + id + "[" + i + "].coverName");
                    boolean starred = JsonPath.read(document, "$." + id + "[" + i + "].starred");
                    String description = JsonPath.read(document, "$." + id + "[" + i + "].description");
                    String latlon = JsonPath.read(document, "$." + id + "[" + i + "].latlon");

                    List<String> category = convertToArray(stringCategory);
                    List<String> imageURLS =  convertToArray(stringImageURLS);
                    List<String> imageNames = convertToArray(stringImageNames);

                    itemList.add(new MunicipalityItem(iid, title, location, contact, category, municipalityStorageKey,
                            imageURLS, imageNames, coverURL, coverName, starred, description, latlon));
                    i++;
                }
            } catch (Exception e) {
                itemLength = 0;
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sectionAdapter() {
        for (String cat : categories) {
            List<MunicipalityItem> municipalityItem = getItemWithCategory(cat);
            if (municipalityItem.size() > 0) {
                sectionAdapter.addSection(new ExpandableSection(cat, municipalityItem));
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);
    }

    private List<String> convertToArray(String item) {
        String category = item.replaceAll("\\s+", "");
        category = category.replace("[", "");
        category = category.replace("]", "");

        return new ArrayList<>(Arrays.asList(category.split(",")));
    }

    private List<MunicipalityItem> getItemWithCategory(String category) {
        List<MunicipalityItem> items = new ArrayList<>();
        for (MunicipalityItem municipalityItem : itemList) {
            if (municipalityItem.getCategory().contains(category)) {
                items.add(municipalityItem);
            }
        }

        return items;
    }

    private class ExpandableSection extends StatelessSection {

        String title;
        List<MunicipalityItem> list;
        boolean expanded = false;

        ExpandableSection(String title, List<MunicipalityItem> list) {
            super(new SectionParameters.Builder(R.layout.section_expandable_item)
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

            itemHolder.itemTextview.setText(municipalityItem.getTitle());

            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TabItemDetailsActivity.class);
                    intent.putExtra("municipalityItem", municipalityItem);
                    intent.putExtra("_municipality", id);
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

            headerHolder.headerTitle.setText(title + " -- " + list.size());
            headerHolder.headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expanded = !expanded;
                    headerHolder.headerArrow.setImageResource(
                            expanded ? R.drawable.ic_arrow_drop_up_white_24dp : R.drawable.ic_arrow_drop_down_white_24dp
                    );
                    sectionAdapter.notifyDataSetChanged();
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

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.section_exapandable_item_view)
        View itemView;
        @BindView(R.id.section_exapandable_item_imageview)
        ImageView itemImageview;
        @BindView(R.id.section_exapandable_item_textview)
        TextView itemTextview;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
