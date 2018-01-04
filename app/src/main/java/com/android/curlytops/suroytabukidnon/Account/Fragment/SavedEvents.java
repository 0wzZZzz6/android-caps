package com.android.curlytops.suroytabukidnon.Account.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Event.EventDetailActivity;
import com.android.curlytops.suroytabukidnon.Model.Bookmark;
import com.android.curlytops.suroytabukidnon.Model.Event;
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

public class SavedEvents extends Fragment {

    private static final String TAG = "SavedEvents";

    private static final String jsonPathNode_bookmarkEvents = "bookmark_events";

    List<Bookmark> bookmarkList_events = new ArrayList<>();
    List<Event> eventList = new ArrayList<>();
    List<Event> new_eventList = new ArrayList<>();

    @BindView(R.id.recyclerview_bookmark)
    RecyclerView recyclerView;

    public static SavedEvents newInstance() {
        SavedEvents savedEvents = new SavedEvents();
        Bundle args = new Bundle();
        savedEvents.setArguments(args);
        return savedEvents;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview_bookmark, container, false);
        ButterKnife.bind(this, view);

        eventList = new BaseActivity().readEvents(getContext());
        readBookmark_events();
        filterEvents();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setPadding(0, 0, 0, 52);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SavedEventAdapter adapter = new SavedEventAdapter(getActivity(), new_eventList);
        recyclerView.setAdapter(adapter);
    }

    private void readBookmark_events() {
        try {
            FileInputStream fis = getContext().openFileInput("bookmark_events.json");
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
                int length = JsonPath.read(document, "$.bookmark_events.length()");
                int i = 0;
                while (i < length) {
                    String bid = JsonPath.read(document,
                            jsonPath(i, "b_id", jsonPathNode_bookmarkEvents));
                    String item_id = JsonPath.read(document,
                            jsonPath(i, "item_id", jsonPathNode_bookmarkEvents));

                    bookmarkList_events.add(new Bookmark(bid, item_id));

                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void filterEvents() {
        for (int i = 0; i < bookmarkList_events.size(); i++) {
            for (int j = 0; j < eventList.size(); j++) {
                if (bookmarkList_events.get(i).item_id.equalsIgnoreCase(eventList.get(j).e_id)) {
                    Log.d(TAG, eventList.get(j).e_id + " [bookmarked]");
                    new_eventList.add(eventList.get(j));
                    eventList.remove(j);
                }
            }
        }
    }

    private String jsonPath(int index, String keyword, String jsonPathNode) {
        return "$." + jsonPathNode + "[" + index + "]." + keyword;
    }

    class SavedEventAdapter extends
            RecyclerView.Adapter<SavedEventAdapter.SavedEventViewHolder> {

        private List<Event> eventList;
        private Context context;

        SavedEventAdapter(Context context, List<Event> eventList) {
            this.context = context;
            this.eventList = eventList;
        }

        @Override
        public SavedEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.saved_events_item, parent, false);
            return new SavedEventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SavedEventViewHolder holder, int position) {
            final Event event = eventList.get(position);

            holder.saved_event_title.setText(event.title);
            holder.saved_event_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), EventDetailActivity.class);
                    intent.putExtra("myEvent", event);
                    getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        class SavedEventViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.saved_event_item_title)
            TextView saved_event_title;

            SavedEventViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
