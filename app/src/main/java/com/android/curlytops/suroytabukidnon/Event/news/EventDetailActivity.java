package com.android.curlytops.suroytabukidnon.Event.news;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */

public class EventDetailActivity extends BaseActivity {

    @BindView(R.id.activity_event_detail_collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.activity_event_detail_header)
    ImageView imageView_header;
    @BindView(R.id.activity_event_detail_toolbar)
    Toolbar toolbar;

    Event event;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        supportPostponeEnterTransition();
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        event = getEvent();

        Glide.with(this)
                .load(event.coverURL)
                .into(imageView_header);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, EventDetailFragment.newInstance())
                .commit();
    }

    public Event getEvent() {
        return (Event) getIntent().getSerializableExtra("myEvent");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
