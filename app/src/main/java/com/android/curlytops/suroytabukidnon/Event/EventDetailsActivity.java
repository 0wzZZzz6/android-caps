package com.android.curlytops.suroytabukidnon.Event;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.ImageModel;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by jan_frncs
 */

public class EventDetailsActivity extends BaseActivity {

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

    @BindView(R.id.totalInterested)
    TextView totalInterested;
    @BindView(R.id.totalGoing)
    TextView totalGoing;
    @BindView(R.id.statusLinearlayout)
    View statusLinearlayout;
    @BindView(R.id.recyclerView_gallery)
    RecyclerView recyclerViewGallery;

    DatabaseReference eventReference;

    String month;
    String day;
    int mShortAnimationDuration;
    ArrayList<ImageModel> data = new ArrayList<>();
    GalleryAdapter galleryAdapter;
    Animator mCurrentAnimator;

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

        reactStatus();

        String dateTimeInfo = getDate(event) + System.lineSeparator()
                + getTime(event.fromTime, event.toTime);

        Glide.with(this)
                .load(event.coverURL)
                .into(iv_header);
        tv_title.setText(event.title);
        tv_location.setText(event.location);
        tv_time.setText(dateTimeInfo);
        expTv1.setText(event.description);
        tv_day.setText(day);
        tv_month.setText(month);

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        List<String> imageURLS = event.getImageURLS();
        for (int i = 0; i < imageURLS.size(); i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.setName("Image " + i);
            imageModel.setUrl(imageURLS.get(i));
            data.add(imageModel);
        }
        galleryAdapter = new GalleryAdapter(this, data);
        recyclerViewGallery.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewGallery.setHasFixedSize(true);
        recyclerViewGallery.setAdapter(galleryAdapter);

        ic_interested = getResources().getDrawable(R.drawable.ic_star_yellow_24dp);
        ic_going = getResources().getDrawable(R.drawable.ic_check_grey600_24dp);
        ic_not_interested = getResources().getDrawable(R.drawable.ic_star_grey600_24dp);
        ic_not_going = getResources().getDrawable(R.drawable.ic_check_grey600_24dp);

    }

    private void getDetails() {
        DateTime startDate = new DateTime(event.startDate);

        SimpleDateFormat month_format = new SimpleDateFormat("MMM");
        SimpleDateFormat day_format = new SimpleDateFormat("DD");

        month = month_format.format(event.startDate);
        day = String.valueOf(startDate.getDayOfMonth());
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
        SimpleDateFormat _24HourSDF, _12HourSDF;
        Date date;
        String result = null;
        try {
            _24HourSDF = new SimpleDateFormat("HH:mm");
            _12HourSDF = new SimpleDateFormat("hh:mm a"); // with am/pm

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
//        reactStatus();
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
//        reactStatus();
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

    public void reactStatus() {
        try {
            eventReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Event event = dataSnapshot.getValue(Event.class);
                    String t_Interested = "0 interested";
                    String t_Going = "0 going";

                    try {
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

                            t_Interested = event.interested.size() + " interested";
                            t_Going = event.going.size() + " going";

                            totalInterested.setText(t_Interested);
                            totalGoing.setText(t_Going);
                        } else {
                            statusLinearlayout.setVisibility(View.GONE);
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
        findViewById(R.id.activity_event_details_coordinatorLayout).getGlobalVisibleRect(finalBounds, globalOffset);
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

    class GalleryAdapter extends RecyclerView.Adapter
            <GalleryAdapter.GalleryViewHolder> {

        private Context context;
        private List<ImageModel> data;

        GalleryAdapter(EventDetailsActivity context, List<ImageModel> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public GalleryAdapter.GalleryViewHolder
        onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item, parent, false);
            return new GalleryAdapter.GalleryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final GalleryAdapter.GalleryViewHolder holder,
                                     int position) {
            final ImageModel item = data.get(position);
            Glide.with(context)
                    .load(item.getUrl())
                    .thumbnail(0.5f)
                    .into(holder.imageView);

//            holder.imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    zoomImageFromThumb(holder.imageView, item.getUrl());
//                }
//            });
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

}

