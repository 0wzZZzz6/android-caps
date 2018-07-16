package com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Detail;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.curlytops.suroytabukidnon.Account.Fragment.SavedPlaces;
import com.android.curlytops.suroytabukidnon.Account.Fragment.SavedPlaces$SavedPlacesAdapter$SavedPlacesViewHolder_ViewBinding;
import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mapswithme.maps.api.MapsWithMeApi;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jan_frncs
 */

public class TabItemDetailActivity extends BaseActivity {

    private static final String TAG = "TabItemDetailActivity";

    @BindView(R.id.activity_tab_item_detail_coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
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
    DatabaseReference bookmarkReference;

    String item_id;
    String municipalityId;
    MunicipalityItem municipalityItem;
    private Menu menu;

    Map<String, String> marked = new HashMap<>();

    SavedPlaces savedPlaces = new SavedPlaces();

    @Override
    protected void onStart() {
        super.onStart();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, TabItemDetailFragment.newInstance())
                .commit();
    }

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
        municipalityId = (String)
                getIntent().getSerializableExtra("municipalityId");
        item_id = municipalityItem.id;

        municipalityReference = FirebaseDatabase.getInstance()
                .getReference("municipality")
                .child(municipalityId)
                .child(item_id);

        bookmarkReference = FirebaseDatabase.getInstance()
                .getReference("bookmark")
                .child("saved_places")
                .child(getUid());

        Glide.with(this)
                .load(municipalityItem.coverURL)
                .into(header);

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
                if (checkConnection(coordinatorLayout))
                    onBookmarkClicked();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bookmarkStatus() {
        try {
            municipalityReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MunicipalityItem municipalityItem = dataSnapshot.getValue(MunicipalityItem.class);

                    // Determine if the current user has liked this post and set UI accordingly
                    if (municipalityItem != null) {
                        if (municipalityItem.bookmark.containsKey(getUid())) {
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

        municipalityReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                MunicipalityItem municipalityItem = mutableData.getValue(MunicipalityItem.class);
                if (municipalityItem == null) {
                    return Transaction.success(mutableData);
                }

                if (municipalityItem.bookmark.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    String val = municipalityItem.bookmark.get(getUid());
                    bookmarkReference.child(val).removeValue();

                    municipalityItem.bookmark.remove(getUid());

                } else {
                    // Star the post and add self to stars
                    String key = bookmarkReference.push().getKey();
                    marked.put("item_id", item_id);
                    bookmarkReference.child(key).setValue(marked);
                    municipalityItem.bookmark.put(getUid(), key);
                }

                savedPlaces.sectionedRecyclerViewAdapter.notifyDataSetChanged();

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

    @OnClick(R.id.fab_directions)
    public void details_fab() {
        String latlon = municipalityItem.latlon;
        latlon = latlon.replace(" ", "");
        String[] parts = latlon.split(",");
        double lat = Double.parseDouble(parts[0]);
        double lon = Double.parseDouble(parts[1]);

        MapsWithMeApi.showPointOnMap(this, lat, lon, municipalityItem.title);
    }

}
