package com.android.curlytops.suroytabukidnon.Account.Fragment;

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
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Model.Bookmark;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Detail.TabItemDetailActivity;
import com.android.curlytops.suroytabukidnon.R;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */

public class SavedPlaces extends Fragment {

    private static final String TAG = "SavedPlaces";
    private static final String jsonPathNode_bookmarkplaces = "bookmark_places";

    List<MunicipalityItem> itemList = new ArrayList<>();
    List<MunicipalityItem> new_itemList = new ArrayList<>();
    List<Bookmark> bookmarkList_places = new ArrayList<>();

    @BindView(R.id.recyclerview_bookmark)
    RecyclerView recyclerView;

    public static SavedPlaces newInstance() {
        SavedPlaces savedPlaces = new SavedPlaces();
        Bundle args = new Bundle();
        savedPlaces.setArguments(args);
        return savedPlaces;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview_bookmark, container, false);
        ButterKnife.bind(this, view);

        itemList = new BaseActivity().readMunicipalityItems(getContext());
        readBookmark_places();
        filterPlaces();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setPadding(0, 0, 0, 52);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SavedPlacesAdapter adapter =
                new SavedPlacesAdapter(getActivity(), new_itemList);
        recyclerView.setAdapter(adapter);
    }

    private void readBookmark_places() {
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
    }

    private void filterPlaces() {
        new_itemList.clear();
        for (int i = 0; i < bookmarkList_places.size(); i++) {
            for (int j = 0; j < itemList.size(); j++) {
                if (bookmarkList_places.get(i).item_id.equalsIgnoreCase(itemList.get(j).id)) {
                    Log.d(TAG, itemList.get(j).id + " [bookmarked]");
                    new_itemList.add(itemList.get(j));
                    itemList.remove(j);
                }
            }
        }
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
            final MunicipalityItem item = itemList.get(position);
            holder.saved_event_title.setText(item.title);

            holder.saved_event_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TabItemDetailActivity.class);
                    intent.putExtra("municipalityItem", item);
                    intent.putExtra("municipalityId", item.municipality);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        class SavedPlacesViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.saved_event_item_title)
            TextView saved_event_title;

            SavedPlacesViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
