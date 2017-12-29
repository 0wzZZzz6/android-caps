package com.android.curlytops.suroytabukidnon.Event;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.curlytops.suroytabukidnon.Connection.ConnectivityReceiver;
import com.android.curlytops.suroytabukidnon.Gallery.GalleryAdapter;
import com.android.curlytops.suroytabukidnon.Gallery.GalleryItemClickListener;
import com.android.curlytops.suroytabukidnon.Gallery.GalleryViewPagerFragment;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.ImageModel;
import com.android.curlytops.suroytabukidnon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jan_frncs
 */

public class EventDetailFragment extends Fragment implements GalleryItemClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    public static final String TAG = EventDetailFragment.class.getSimpleName();
    private static final String INTERESTED = "interested";
    private static final String GOING = "going";

    EventDetailActivity eventDetailActivity;
    Event event;
    ArrayList<ImageModel> data = new ArrayList<>();
    String day, month, dateTimeInfo;
    DatabaseReference eventReference;
    AppBarLayout appBarLayout;

    @BindView(R.id.fragment_event_details_textView_month)
    TextView tv_month;
    @BindView(R.id.fragment_event_details_textView_day)
    TextView tv_day;
    @BindView(R.id.fragment_event_details_title)
    TextView tv_title;
    @BindView(R.id.fragment_event_details_location)
    TextView tv_location;
    @BindView(R.id.fragment_event_details_time)
    TextView tv_time;
    @BindView(R.id.fragment_event_details_description)
    ExpandableTextView tv_description;

    @BindView(R.id.recyclerView_gallery)
    RecyclerView rv_gallery;

    @BindView(R.id.totalInterested)
    TextView totalInterested;
    @BindView(R.id.totalGoing)
    TextView totalGoing;
    @BindView(R.id.statusLinearlayout)
    View statusLinearlayout;

    @BindView(R.id.fragment_event_details_button_interested)
    Button button_interested;
    @BindView(R.id.fragment_event_details_button_going)
    Button button_going;
    @BindView(R.id.statusDivider)
    LinearLayout statusDivider;

    public EventDetailFragment() {
    }


    public static EventDetailFragment newInstance() {
        return new EventDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventDetailActivity = (EventDetailActivity) getActivity();
        event = eventDetailActivity.getEvent();
        appBarLayout = eventDetailActivity.appBarLayout;
        eventReference = FirebaseDatabase.getInstance()
                .getReference("events").child(event.e_id);
        getDetails();

        List<String> imageURLS = event.imageURLS;
        for (int i = 0; i < imageURLS.size(); i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.setName("Image " + i);
            imageModel.setUrl(imageURLS.get(i));
            data.add(imageModel);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reactStatus();

        tv_month.setText(month);
        tv_day.setText(day);
        tv_title.setText(event.title);
        tv_location.setText(event.location);
        tv_time.setText(dateTimeInfo);
        tv_description.setText(event.description);

        GalleryAdapter galleryAdapter = new GalleryAdapter(data, this);
        rv_gallery.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rv_gallery.setHasFixedSize(true);
        rv_gallery.setAdapter(galleryAdapter);
    }

    private void getDetails() {
        DateTime startDate = new DateTime(event.startDate);

        SimpleDateFormat month_format = new SimpleDateFormat("MMM");

        month = month_format.format(event.startDate);
        day = String.valueOf(startDate.getDayOfMonth());

        dateTimeInfo = getDate(event) + System.lineSeparator()
                + getTime(event.fromTime, event.toTime);
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

    private String getTime(String fromTime, String toTime) {
        return convertTime(fromTime) + " - " + convertTime(toTime);
    }

    private String convertTime(String time) {
        SimpleDateFormat _24HourSDF;
        Date date;
        String result = null;
        try {
            _24HourSDF = new SimpleDateFormat("HH:mm");
//            _12HourSDF = new SimpleDateFormat("hh:mm a"); // with am/pm

            date = _24HourSDF.parse(time);

            result = _24HourSDF.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String convertDate(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatter.format(date);
    }

    @OnClick(R.id.fragment_event_details_button_interested)
    public void button_interested() {
        if (checkConnection())
            onReactButtonClicked(eventReference, INTERESTED);
    }

    @OnClick(R.id.fragment_event_details_button_going)
    public void button_going() {
        if (checkConnection())
            onReactButtonClicked(eventReference, GOING);
    }

    private void onReactButtonClicked(DatabaseReference eventReference, final String btn) {
        eventReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event event = mutableData.getValue(Event.class);
                if (event == null) {
                    return Transaction.success(mutableData);
                }

                if (btn.equals(INTERESTED)) {
                    if (event.interested.containsKey(getUid())) {
                        // Unstar the post and remove self from stars
                        event.interested.remove(getUid());
                    } else {
                        // Star the post and add self to stars
                        if (!event.going.containsKey(getUid())) {
                            event.interested.put(getUid(), true);
                        }
                    }
                } else if (btn.equals(GOING)) {
                    if (event.going.containsKey(getUid())) {
                        // Unstar the post and remove self from stars
                        event.going.remove(getUid());
                    } else {
                        // Star the post and add self to stars
                        if (!event.interested.containsKey(getUid())) {
                            event.going.put(getUid(), true);
                        }
                    }
                }

                // Set value and report transaction success
                mutableData.setValue(event);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void reactStatus() {
        try {
            eventReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Event event = dataSnapshot.getValue(Event.class);
                    String t_Interested;
                    String t_Going;

                    try {
                        // Determine if the current user has liked this post and set UI accordingly
                        if (event != null) {
                            if (event.interested.containsKey(getUid())) {
                                button_interested.setCompoundDrawablesWithIntrinsicBounds
                                        (R.drawable.ic_star_black_24dp, 0, 0, 0);

                                button_going.setVisibility(View.GONE);
                            } else {
                                button_interested.setCompoundDrawablesWithIntrinsicBounds
                                        (R.drawable.ic_star_grey600_24dp, 0, 0, 0);

                                button_going.setVisibility(View.VISIBLE);
                            }

                            // Determine if the current user has liked this post and set UI accordingly
                            if (event.going.containsKey(getUid())) {
                                button_interested.setVisibility(View.GONE);

                                button_going.setCompoundDrawablesWithIntrinsicBounds
                                        (R.drawable.ic_check_black_24dp, 0, 0, 0);
                            } else {
                                button_interested.setVisibility(View.VISIBLE);

                                button_going.setCompoundDrawablesWithIntrinsicBounds
                                        (R.drawable.ic_check_grey600_24dp, 0, 0, 0);
                            }

                            if (event.interested.containsKey(getUid()) ||
                                    event.going.containsKey(getUid())) {
                                statusLinearlayout.setVisibility(View.VISIBLE);
                                statusLinearlayout.animate().alpha(1.0f);

                                statusDivider.setVisibility(View.GONE);
                                statusDivider.animate().alpha(0.0f);

                                t_Interested = event.interested.size() + " interested";
                                t_Going = event.going.size() + " going";

                                totalInterested.setText(t_Interested);
                                totalGoing.setText(t_Going);
                            } else {
                                statusLinearlayout.setVisibility(View.GONE);
                                statusLinearlayout.animate().alpha(0.0f);

                                statusDivider.setVisibility(View.VISIBLE);
                                statusDivider.animate().alpha(1.0f);
                            }
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onGalleryItemClickListener(int position, ImageModel imageModel, ImageView imageView) {
        appBarLayout.setExpanded(false);
        GalleryViewPagerFragment galleryViewPagerFragment =
                GalleryViewPagerFragment.newInstance(position, data);

        getFragmentManager()
                .beginTransaction()
                .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                .addToBackStack(TAG)
                .replace(R.id.content, galleryViewPagerFragment)
                .commit();
    }

    // Method to manually check connection status
    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);

        return isConnected;
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;

        if (!isConnected) {
            message = "Sorry! Not connected to internet";

            Snackbar snackbar = Snackbar
                    .make(eventDetailActivity.coordinatorLayout, message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            sbView.setBackgroundColor(Color.RED);
            snackbar.show();
        }


    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

}
