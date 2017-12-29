package com.android.curlytops.suroytabukidnon.Event;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Connection.ConnectivityReceiver;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */

public class EventDetailActivity extends BaseActivity {

    private static final String TAG = "EventDetailActivity";

    @BindView(R.id.activity_event_detail_coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.activity_event_detail_collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.activity_event_detail_appBarLayout)
    public AppBarLayout appBarLayout;
    @BindView(R.id.activity_event_detail_header)
    ImageView imageView_header;
    @BindView(R.id.activity_event_detail_toolbar)
    Toolbar toolbar;

    Event event;

    DatabaseReference eventReference;
    DatabaseReference bookmarkReference;

    private Menu menu;
    Map<String, String> marked = new HashMap<>();
    String item_id;

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
        item_id = event.e_id;

        Glide.with(this)
                .load(event.coverURL)
                .into(imageView_header);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, EventDetailFragment.newInstance())
                .commit();

        eventReference = FirebaseDatabase.getInstance()
                .getReference("events")
                .child(item_id);

        bookmarkReference = FirebaseDatabase.getInstance()
                .getReference("bookmark")
                .child("saved_events")
                .child(getUid());

    }

    public Event getEvent() {
        return (Event) getIntent().getSerializableExtra("myEvent");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tab_item_menu, menu);
        this.menu = menu;
        bookmarkStatus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bookmark: {
                if (checkConnection())
                    onBookmarkClicked();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bookmarkStatus() {
        try {
            eventReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Event eventItem = dataSnapshot.getValue(Event.class);

                    // Determine if the current user has liked this post and set UI accordingly
                    if (eventItem != null) {
                        if (eventItem.bookmark.containsKey(getUid())) {
                            menu.getItem(0).setIcon(getResources()
                                    .getDrawable(R.drawable.ic_bookmark_white_24dp));
                        } else {
                            menu.getItem(0).setIcon(getResources()
                                    .getDrawable(R.drawable.ic_bookmark_outline_white_24dp));
                        }
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

    private void onBookmarkClicked() {
        invalidateOptionsMenu();
        marked.clear();
        eventReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event eventItem = mutableData.getValue(Event.class);
                if (eventItem == null) {
                    return Transaction.success(mutableData);
                }

                if (eventItem.bookmark.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    String val = eventItem.bookmark.get(getUid());
                    bookmarkReference.child(val).removeValue();

                    eventItem.bookmark.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    String key = bookmarkReference.push().getKey();
                    marked.put("item_id", item_id);
                    bookmarkReference.child(key).setValue(marked);

                    eventItem.bookmark.put(getUid(), key);
                }

                // Set value and report transaction success
                mutableData.setValue(eventItem);
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
                    .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            sbView.setBackgroundColor(Color.RED);
            snackbar.show();
        }


    }

}
