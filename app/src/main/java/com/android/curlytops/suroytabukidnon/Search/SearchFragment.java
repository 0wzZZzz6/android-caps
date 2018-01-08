package com.android.curlytops.suroytabukidnon.Search;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Event.EventDetailActivity;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Details.TabItemDetailActivity;
import com.android.curlytops.suroytabukidnon.R;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jan_frncs
 */

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    private static final String events = "events";
    private static final String places = "places";

    @BindView(R.id.search_edit)
    EditText edit_txt;
    @BindView(R.id.search_clear)
    ImageButton im_searchClear;
    @BindView(R.id.search_filter)
    ImageButton im_searchFilter;
    @BindView(R.id.recyclerview_search)
    RecyclerView recyclerView;
    @BindView(R.id.filterBy)
    TextView filterBy;
    @BindView(R.id.noSearchFound)
    View noSearchFound;

    List<Event> search_result_events = new ArrayList<>();
    List<Event> eventList = new ArrayList<>();
    List<MunicipalityItem> search_result_places = new ArrayList<>();
    List<MunicipalityItem> itemList = new ArrayList<>();

    Set<Event> hashed_search_result_events = new HashSet<>();
    Set<MunicipalityItem> hashed_search_result_places = new HashSet<>();

    String search_mode = events;
    SearchAdapter searchAdapter;
    InputMethodManager imm;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventList = new BaseActivity().readEvents(getContext());
        itemList = new BaseActivity().readMunicipalityItems(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        edit_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    im_searchClear.setVisibility(View.VISIBLE);
                } else {
                    im_searchClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_txt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(search_mode, edit_txt.getText().toString());
                    return true;
                }
                return false;
            }
        });

        filterBy.setText(search_mode.toUpperCase());

        imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return view;
    }

    private void search(String mode, String query) {
        search_result_events.clear();
        search_result_places.clear();
        if (imm != null)
            imm.hideSoftInputFromWindow(edit_txt.getWindowToken(), 0);

        if (!query.equalsIgnoreCase("")) {
            if (mode.equalsIgnoreCase(events)) {
                for (Event event : eventList) {
                    if (event.title.toLowerCase().contains(query.toLowerCase())) {
                        search_result_events.add(event);
                    }

                    for (String place : event.taggedMunicipality) {
                        if (place.toLowerCase().contains(query.toLowerCase())) {
                            search_result_events.add(event);
                        }
                    }
                }

                hashed_search_result_events.addAll(search_result_events);
                search_result_events.clear();
                search_result_events.addAll(hashed_search_result_events);

                if (search_result_events.size() > 0) {
                    searchAdapter = new SearchAdapter
                            (getContext(), search_result_events,
                                    search_result_places, search_mode);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
                    recyclerView.setAdapter(searchAdapter);
                    noSearchFound.setVisibility(View.GONE);
                } else {
                    noSearchFound.setVisibility(View.VISIBLE);
                }

            } else if (mode.equalsIgnoreCase(places)) {
                for (MunicipalityItem item : itemList) {
                    if (item.title.toLowerCase().contains(query.toLowerCase())) {
                        search_result_places.add(item);
                    }

                    if (item.municipality.toLowerCase().contains(query.toLowerCase())) {
                        search_result_places.add(item);
                    }
                }

                hashed_search_result_places.addAll(search_result_places);
                search_result_places.clear();
                search_result_places.addAll(hashed_search_result_places);

                if (search_result_places.size() > 0) {
                    searchAdapter = new SearchAdapter
                            (getContext(), search_result_events,
                                    search_result_places, search_mode);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
                    recyclerView.setAdapter(searchAdapter);
                    noSearchFound.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    noSearchFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }
    }

    @OnClick(R.id.search_clear)
    public void clearSearch() {
        im_searchClear.setVisibility(View.GONE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edit_txt.getWindowToken(), 0);
        }
        edit_txt.setText("");
        edit_txt.setFocusableInTouchMode(false);
        edit_txt.setFocusable(false);
        edit_txt.setFocusableInTouchMode(true);
        edit_txt.setFocusable(true);
    }

    @OnClick(R.id.search_finish)
    public void backToActivity() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.search_filter)
    public void filterBy() {
        // custom dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_filter);

        Button buttonOk = dialog.findViewById(R.id.dialogButtonOK);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        RadioButton rb_events = dialog.findViewById(R.id.rb_events);
        RadioButton rb_places = dialog.findViewById(R.id.rb_places);

        if (search_mode.equalsIgnoreCase(events)) {
            rb_events.setChecked(true);
        } else {
            rb_places.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.rb_events == checkedId) {
                    search_mode = events;
                } else {
                    search_mode = places;
                }
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBy.setText(search_mode.toUpperCase());
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
        private List<Event> eventList;
        private List<MunicipalityItem> itemList;
        private Context context;
        private String mode;

        SearchAdapter(Context context, List<Event> eventList,
                      List<MunicipalityItem> itemList, String mode) {
            this.context = context;
            this.eventList = eventList;
            this.itemList = itemList;
            this.mode = mode;
        }

        @Override
        public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.search_item, parent, false);
            return new SearchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SearchViewHolder holder, int position) {
            if (mode.equalsIgnoreCase(events)) {
                final Event event = eventList.get(position);

                String dateTimeInfo = getDate(event);

                holder.searchItemTitle.setText(event.title);
                holder.searchItemDetail.setText(dateTimeInfo);
                holder.searchItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), EventDetailActivity.class);
                        intent.putExtra("myEvent", event);
                        startActivity(intent);
                    }
                });
            } else {
                final MunicipalityItem item = itemList.get(position);

                holder.searchItemTitle.setText(item.title);
                holder.searchItemDetail.setText(String.format("%s%s",
                        item.municipality.substring(0, 1).toUpperCase(),
                        item.municipality.substring(1)));
                holder.searchItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), TabItemDetailActivity.class);
                        intent.putExtra("municipalityItem", item);
                        intent.putExtra("municipalityId", item.municipality);
                        startActivity(intent);
                    }
                });
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

        @Override
        public int getItemCount() {
            if (mode.equalsIgnoreCase(events)) {
                return eventList.size();
            } else {
                return itemList.size();
            }
        }

        class SearchViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.search_item_view)
            View searchItemView;
            @BindView(R.id.search_item_title)
            TextView searchItemTitle;
            @BindView(R.id.search_item_detail)
            TextView searchItemDetail;

            SearchViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
