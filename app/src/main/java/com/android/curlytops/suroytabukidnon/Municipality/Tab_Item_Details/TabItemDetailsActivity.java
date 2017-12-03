package com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Details;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jan_frncs
 */

public class TabItemDetailsActivity extends BaseActivity {

    private static final String TAG = "TabItemDetailsActivity";

    ArrayList<ImageModel> data = new ArrayList<>();
    MunicipalityItem municipalityItem;
    String municipality;
    String item_id;
    int mShortAnimationDuration;

    DatabaseReference municipalityReference;

    GalleryAdapter galleryAdapter;
    ChipsAdapter chipsAdapter;
    Animator mCurrentAnimator;

    Menu menu;
    MenuItem menuItem;

    @BindView(R.id.tab_item_details_toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_item_details_coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.tab_item_details_collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.tab_item_details_appBar)
    AppBarLayout appBarLayout;
    @BindView(R.id.tab_item_details_header)
    ImageView imageView;
    @BindView(R.id.recyclerView_chips)
    RecyclerView recyclerViewChips;
    @BindView(R.id.recyclerView_gallery)
    RecyclerView recyclerViewGallery;
    @BindView(R.id.bottomSheet_title)
    TextView textView_title;

    @BindView(R.id.detail1)
    TextView detail1;
    @BindView(R.id.detail2)
    TextView detail2;
    @BindView(R.id.detail3)
    TextView detail3;

    @BindView(R.id.heart_status)
    ImageView heart_status;
    @BindView(R.id.heart_count)
    TextView heart_count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_item_details);
        ButterKnife.bind(this);
        supportPostponeEnterTransition();
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationContentDescription(R.string.app_name);


        municipalityItem = (MunicipalityItem) getIntent().getSerializableExtra("municipalityItem");
        municipality = (String) getIntent().getSerializableExtra("_municipality");

        item_id = municipalityItem.getId();
        textView_title.setText(municipalityItem.getTitle());
        detail1.setText(municipalityItem.getDescription());
        detail2.setText(String.valueOf(municipalityItem.getStarred()));
        detail3.setText(String.valueOf(municipalityItem.getLatlon()));
        List<String> imageURLS = municipalityItem.getImageURLS();

        Glide.with(this)
                .load(municipalityItem.getCoverURL())
                .into(imageView);

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        for (int i = 0; i < imageURLS.size(); i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.setName("Image " + i);
            imageModel.setUrl(imageURLS.get(i));
            data.add(imageModel);
        }

        galleryAdapter = new GalleryAdapter(TabItemDetailsActivity.this, data);
        recyclerViewGallery.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewGallery.setHasFixedSize(true);
        recyclerViewGallery.setAdapter(galleryAdapter);

        chipsAdapter = new ChipsAdapter(municipalityItem.getCategory());
        recyclerViewChips.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewChips.setHasFixedSize(false);
        recyclerViewChips.setAdapter(chipsAdapter);

        municipalityReference = FirebaseDatabase.getInstance()
                .getReference("municipality")
                .child(municipality).child(item_id);


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
                        heart_status.setBackground(getResources()
                                .getDrawable(R.drawable.ic_heart_black_24dp));
                        heart_count.setText("(" + municipalityItem.stars.size() + ")");
                    } else {
                        menuItem.setIcon(getResources()
                                .getDrawable(R.drawable.ic_heart_outline_white_24dp));
                        heart_status.setBackground(getResources()
                                .getDrawable(R.drawable.ic_heart_outline_black_24dp));
                        heart_count.setText("(" + municipalityItem.stars.size() + ")");
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void zoomImageFromThumb(final View thumbView, String imageResId) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = findViewById(R.id.expanded_image);
        Glide.with(this)
                .load(imageResId)
                .thumbnail(0.5f)
                .into(expandedImageView);
//        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.tab_item_details_coordinator).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
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

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    class GalleryAdapter extends RecyclerView.Adapter
            <GalleryAdapter.GalleryViewHolder> {

        private Context context;
        private List<ImageModel> data;

        GalleryAdapter(Context context, List<ImageModel> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public GalleryAdapter.GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item, parent, false);
            return new GalleryAdapter.GalleryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final GalleryAdapter.GalleryViewHolder holder, int position) {
            final ImageModel item = data.get(position);
            Glide.with(context)
                    .load(item.getUrl())
                    .thumbnail(0.5f)
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomImageFromThumb(holder.imageView, item.getUrl());
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class GalleryViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.item_img)
            ImageView imageView;

            GalleryViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    class ChipsAdapter extends RecyclerView.Adapter
            <ChipsAdapter.ChipsViewHolder> {

        private List<String> categories;

        ChipsAdapter(List<String> categories) {
            this.categories = categories;
        }

        @Override
        public ChipsAdapter.ChipsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_chips, parent, false);
            return new ChipsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChipsAdapter.ChipsViewHolder holder, int position) {
            String title = categories.get(position);
            holder.chip.setText(title);
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        class ChipsViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.button_chip)
            TextView chip;

            ChipsViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
