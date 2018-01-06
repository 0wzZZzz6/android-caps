package com.android.curlytops.suroytabukidnon.Municipality.Tab.Fragment;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.Municipality.Tab.TabActivity;
import com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Details.TabItemDetailActivity;
import com.android.curlytops.suroytabukidnon.R;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
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

public class More extends Fragment {

    private static final String TAG = "More";

    SectionedRecyclerViewAdapter sectionAdapter;
    List<MunicipalityItem> itemList = new ArrayList<>();
    List<String> categories = new ArrayList<>();
    String municipalityId;
    int itemLength = 0;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        municipalityId = ((TabActivity) getActivity()).getMunicipalityId();
        Log.d(TAG, municipalityId);
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
                itemLength = JsonPath.read(document, "$." + municipalityId + ".length()");
                int i = 0;
                while (i < itemLength) {
                    String iid = JsonPath.read(document, jsonPath(i, "id"));
                    String title = JsonPath.read(document, jsonPath(i, "title"));
                    String location = JsonPath.read(document, jsonPath(i, "location"));
                    String contact = JsonPath.read(document, jsonPath(i, "contact"));
                    String stringCategory = JsonPath.read(document, jsonPath(i, "category"));
                    String stringImageURLS = JsonPath.read(document, jsonPath(i, "imageURLS"));
                    String stringImageNames = JsonPath.read(document, jsonPath(i, "imageNames"));
                    String municipalityStorageKey = JsonPath.read(document, jsonPath(i, "municipalityStorageKey"));
                    String coverURL = JsonPath.read(document, jsonPath(i, "coverURL"));
                    String coverName = JsonPath.read(document, jsonPath(i, "coverName"));
                    boolean starred = JsonPath.read(document, jsonPath(i, "starred"));
                    String description = JsonPath.read(document, jsonPath(i, "description"));
                    String latlon = JsonPath.read(document, jsonPath(i, "latlon"));

                    List<String> category = convertToArray(stringCategory);
                    List<String> imageURLS = convertToArray(stringImageURLS);
                    List<String> imageNames = convertToArray(stringImageNames);

                    itemList.add(new MunicipalityItem(iid, title, location, contact,
                            category, municipalityStorageKey, imageURLS, imageNames,
                            coverURL, coverName, starred, description, latlon));
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

    private String jsonPath(int index, String keyword) {
        return "$." + municipalityId + "[" + index + "]." + keyword;
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
            if (municipalityItem.category.contains(category)) {
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

            itemHolder.itemTextview.setText(municipalityItem.title);

            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TabItemDetailActivity.class);
                    intent.putExtra("municipalityItem", municipalityItem);
                    intent.putExtra("municipalityId", municipalityId);
                    startActivity(intent);
                }
            });

            Log.d(TAG, municipalityItem + " item    --   " + municipalityId + "  -- id");
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
        @BindView(R.id.section_expandable_header_count)
        ImageView itemCount;

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
