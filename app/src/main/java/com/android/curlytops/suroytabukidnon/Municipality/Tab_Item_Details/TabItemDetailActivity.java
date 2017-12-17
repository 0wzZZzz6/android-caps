package com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Details;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
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
import com.mapswithme.maps.api.MapsWithMeApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jan_frncs
 */

public class TabItemDetailActivity extends BaseActivity {

    private static final String TAG = "TabItemDetailActivity";

    @BindView(R.id.activity_tab_item_detail_appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.activity_tab_item_detail_collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.activity_tab_item_detail_header)
    ImageView header;
    @BindView(R.id.activity_tab_item_detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab_directions)
    FloatingActionButton fab;

    DatabaseReference municipalityReference;

    String item_id;
    String municipality;
    MunicipalityItem municipalityItem;
    Menu menu;
    MenuItem menuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tab_item_detail);
        ButterKnife.bind(this);

        supportPostponeEnterTransition();
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        municipalityItem = (MunicipalityItem)
                getIntent().getSerializableExtra("municipalityItem");
        municipality = (String)
                getIntent().getSerializableExtra("_municipality");
        item_id = municipalityItem.getId();
        municipalityReference = FirebaseDatabase.getInstance()
                .getReference("municipality")
                .child(municipality).child(item_id);

        Glide.with(this)
                .load(municipalityItem.getCoverURL())
                .into(header);


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.tab_item_menu, menu);
        menuItem = menu.findItem(R.id.action_heart);
        starStatus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_heart: {
                onStarClicked(municipalityReference);
                starStatus();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void starStatus() {
        try {
            municipalityReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MunicipalityItem municipalityItem = dataSnapshot.getValue(MunicipalityItem.class);

                    // Determine if the current user has liked this post and set UI accordingly
                    if (municipalityItem.stars.containsKey(getUid())) {
                        menuItem.setIcon(getResources()
                                .getDrawable(R.drawable.ic_heart_white_24dp));
                    } else {
                        menuItem.setIcon(getResources()
                                .getDrawable(R.drawable.ic_heart_outline_white_24dp));
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

    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference municipalityRef) {
        municipalityRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                MunicipalityItem municipalityItem = mutableData.getValue(MunicipalityItem.class);
                if (municipalityItem == null) {
                    return Transaction.success(mutableData);
                }

                if (municipalityItem.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    municipalityItem.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    municipalityItem.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(municipalityItem);
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
    // [END post_stars_transaction]

    @OnClick(R.id.fab_directions)
    public void details_fab() {
        String latlon = municipalityItem.getLatlon();
        latlon = latlon.replace(" ", "");
        String[] parts = latlon.split(",");
        double lat = Double.parseDouble(parts[0]);
        double lon = Double.parseDouble(parts[1]);

        MapsWithMeApi.showPointOnMap(this, lat, lon, municipalityItem.getTitle());
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, TabItemDetailFragment.newInstance())
                .commit();
    }
}
