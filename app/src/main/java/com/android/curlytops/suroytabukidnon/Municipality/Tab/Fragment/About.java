package com.android.curlytops.suroytabukidnon.Municipality.Tab.Fragment;

import android.content.Context;
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

import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.Municipality.Tab.TabActivity;
import com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Details.TabItemDetailActivity;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
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

/**
 * Created by jan_frncs
 */
public class About extends Fragment {

    private static final String TAG = "About";

    StarredAdapter starredAdapter;

    String id = null;
    int itemLength = 0;

    @BindView(R.id.fragment_about_starred)
    RecyclerView recyclerView_starred;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = ((TabActivity) getActivity()).get_id();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);

        starredAdapter = new StarredAdapter(getContext(), readMunicipalityItems());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_starred.setHasFixedSize(false);
        recyclerView_starred.setLayoutManager(linearLayoutManager);
        recyclerView_starred.setAdapter(starredAdapter);

        return view;
    }

    private List<MunicipalityItem> readMunicipalityItems() {
        List<MunicipalityItem> itemList = new ArrayList<>();
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

                    if (starred) {
                        itemList.add(new MunicipalityItem(iid, title, location, contact,
                                category, municipalityStorageKey, imageURLS, imageNames,
                                coverURL, coverName, true, description, latlon));
                    }

                    i++;
                }
            } catch (Exception e) {
                itemLength = 0;
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, itemList.size() + "  size itemlist");

        return itemList;
    }

    private String jsonPath(int index, String keyword) {
        return "$." + id + "[" + index + "]." + keyword;
    }

    private List<String> convertToArray(String item) {
        String category = item.replaceAll("\\s+", "");
        category = category.replace("[", "");
        category = category.replace("]", "");

        return new ArrayList<>(Arrays.asList(category.split(",")));
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
                    intent.putExtra("_municipality", id);
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
}
