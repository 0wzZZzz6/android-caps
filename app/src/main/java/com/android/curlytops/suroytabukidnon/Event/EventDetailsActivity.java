package com.android.curlytops.suroytabukidnon.Event;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by jan_frncs
 */

public class EventDetailsActivity extends AppCompatActivity {
    Event event;
    String date;

    @BindView(R.id.event_details_toolbar)
    Toolbar eventDetailsToolbar;
    @BindView(R.id.event_details_title)
    TextView eventDetailsTitle;
    @BindView(R.id.event_details_description)
    TextView eventDetailsDescription;
    @BindView(R.id.event_details_location)
    TextView eventDetailsLocation;
    @BindView(R.id.event_details_date)
    TextView eventDetailsDate;
    @BindView(R.id.event_details_time)
    TextView eventDetailsTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_layout);
        ButterKnife.bind(this);

        setSupportActionBar(eventDetailsToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        event = (Event) getIntent().getSerializableExtra("myEvent");

        eventDetailsTitle.setText(event.getTitle());
        eventDetailsLocation.setText(event.getLocation());
        eventDetailsDate.setText(getDate(event.getAllDay())); // ifAllday
        eventDetailsTime.setText(getTime(event.getFromTime(), event.getToTime()));
        eventDetailsDescription.setText(event.getDescription());
    }

    private String getDate(boolean ifAllday) {
        if (ifAllday) {
            date = convertDate(event.getDate());
        } else {
            date = convertDate(event.getFromDate()) + " - " + convertDate(event.getToDate());
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

}
