package com.android.curlytops.suroytabukidnon.Event;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;
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
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by jan_frncs
 */

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";

    Event event;
    String date;

    boolean interested = false;
    boolean going = false;

    Drawable ic_interested, ic_going;
    Drawable ic_not_interested, ic_not_going;

    @BindView(R.id.activity_event_details_collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.activity_event_details_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_event_details_title)
    TextView tv_title;
    @BindView(R.id.activity_event_details_location)
    TextView tv_location;
    @BindView(R.id.activity_event_details_time)
    TextView tv_time;
    @BindView(R.id.activity_event_details_header)
    ImageView iv_header;
    @BindView(R.id.expand_text_view)
    ExpandableTextView expTv1;
    @BindView(R.id.activity_event_details_button_interested)
    Button button_interested;
    @BindView(R.id.activity_event_details_button_going)
    Button button_going;
    @BindView(R.id.activity_event_details_textView_month)
    TextView tv_month;
    @BindView(R.id.activity_event_details_textView_day)
    TextView tv_day;

    DatabaseReference eventReference;

    String month;
    String day;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        ButterKnife.bind(this);

        supportPostponeEnterTransition();
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationContentDescription(R.string.app_name);

        event = (Event) getIntent().getSerializableExtra("myEvent");
        getDetails();
        eventReference = FirebaseDatabase.getInstance()
                .getReference("events").child(event.e_id);

        interestedStatus();
        goingStatus();

        Glide.with(this)
                .load(event.coverURL)
                .into(iv_header);
        tv_title.setText(event.title);
        tv_location.setText(event.location);
        tv_time.setText(getTime(event.fromTime, event.toTime));
        expTv1.setText(event.description);
        tv_day.setText(day);
        tv_month.setText(month);

        ic_interested = getResources().getDrawable(R.drawable.ic_star_yellow_24dp);
        ic_going = getResources().getDrawable(R.drawable.ic_check_grey600_24dp);
        ic_not_interested = getResources().getDrawable(R.drawable.ic_star_grey600_24dp);
        ic_not_going = getResources().getDrawable(R.drawable.ic_check_grey600_24dp);

    }

    private void getDetails() {
        DateTime date = new DateTime(event.date);
        DateTime fromDate = new DateTime(event.fromDate);

        SimpleDateFormat month_format = new SimpleDateFormat("MMM");
        SimpleDateFormat day_format = new SimpleDateFormat("DD");

        if (event.allDay) {
            month = month_format.format(event.date);
            day = String.valueOf(date.getDayOfMonth());
        } else {
            month = month_format.format(event.fromDate);
            day = String.valueOf(fromDate.getDayOfMonth());
        }
    }

    private String getDate(boolean ifAllday) {
        if (ifAllday) {
            date = convertDate(event.date);
        } else {
            date = convertDate(event.fromDate) + " - " + convertDate(event.toDate);
        }

        return date;
    }

    private String getTime(String fromTime, String toTime) {
        return convertTime(fromTime) + " - " + convertTime(toTime);
    }

    private String convertTime(String time) {
        String _24HourTime;
        SimpleDateFormat _24HourSDF;
        SimpleDateFormat _12HourSDF;
        Date _24HourDt;
        String result = null;
        try {
            _24HourTime = time;
            _24HourSDF = new SimpleDateFormat("HH:mm");
            _12HourSDF = new SimpleDateFormat("hh:mm a");
            _24HourDt = _24HourSDF.parse(_24HourTime);

            result = _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String convertDate(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatter.format(date);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @OnClick(R.id.activity_event_details_button_interested)
    public void button_interested() {
        if (interested) {
            onInterestedClicked(eventReference);
            button_going.setVisibility(View.VISIBLE);
            button_interested.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_star_grey600_24dp, 0, 0, 0);
            interested = false;
        } else {
            onInterestedClicked(eventReference);
            button_going.setVisibility(View.GONE);
            button_interested.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_star_yellow_24dp, 0, 0, 0);
            interested = true;
        }
        interestedStatus();
    }

    @OnClick(R.id.activity_event_details_button_going)
    public void button_going() {
        if (going) {
            onGoingClicked(eventReference);
            button_interested.setVisibility(View.VISIBLE);
            button_going.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_check_grey600_24dp, 0, 0, 0);
            going = false;
        } else {
            onGoingClicked(eventReference);
            button_interested.setVisibility(View.GONE);
            button_going.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_check_black_24dp, 0, 0, 0);
            going = true;
        }
        goingStatus();
    }

    // [START post_interested_transaction]
    private void onInterestedClicked(DatabaseReference eventRef) {
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event event = mutableData.getValue(Event.class);
                if (event == null) {
                    return Transaction.success(mutableData);
                }

                if (event.interested.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    event.interested.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    event.interested.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(event);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_interested_transaction]

    // [START post_going_transaction]
    private void onGoingClicked(DatabaseReference eventRef) {
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event event = mutableData.getValue(Event.class);
                if (event == null) {
                    return Transaction.success(mutableData);
                }

                if (event.going.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    event.going.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    event.going.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(event);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_going_transaction]

    public void interestedStatus() {
        try {
            eventReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Event event = dataSnapshot.getValue(Event.class);

                    // Determine if the current user has liked this post and set UI accordingly
                    if (event.interested.containsKey(getUid())) {
                        button_going.setVisibility(View.GONE);
                        button_interested.setCompoundDrawablesWithIntrinsicBounds
                                (R.drawable.ic_star_yellow_24dp, 0, 0, 0);
                    } else {
                        button_going.setVisibility(View.VISIBLE);
                        button_interested.setCompoundDrawablesWithIntrinsicBounds
                                (R.drawable.ic_star_grey600_24dp, 0, 0, 0);
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

    public void goingStatus() {
        try {
            eventReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Event event = dataSnapshot.getValue(Event.class);

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
}
