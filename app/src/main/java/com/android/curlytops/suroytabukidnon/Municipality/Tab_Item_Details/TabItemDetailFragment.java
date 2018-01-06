package com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Details;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Connection.ConnectivityReceiver;
import com.android.curlytops.suroytabukidnon.Gallery.GalleryAdapter;
import com.android.curlytops.suroytabukidnon.Gallery.GalleryItemClickListener;
import com.android.curlytops.suroytabukidnon.Gallery.GalleryViewPagerFragment;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.ImageModel;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */

public class TabItemDetailFragment extends Fragment implements GalleryItemClickListener {

    public static final String TAG = "TabItemDetailFragment";

    @BindView(R.id.fragment_tab_item_title)
    TextView title;
    @BindView(R.id.fragment_tab_item_location)
    TextView location;
    @BindView(R.id.fragment_tab_item_contact)
    TextView contact;
    @BindView(R.id.fragment_tab_item_description)
    ExpandableTextView description;

    @BindView(R.id.heart_status)
    ImageButton heart_status;
    @BindView(R.id.heart_count)
    TextView heart_count;

    @BindView(R.id.fragment_tab_item_rv_chips)
    RecyclerView rv_chips;
    @BindView(R.id.fragment_tab_item_rv_gallery)
    RecyclerView rv_gallery;

    @BindView(R.id.myFrame)
    FrameLayout frameLayout;
    @BindView(R.id.reactView)
    LinearLayout reactView;

    DatabaseReference municipalityReference;

    String item_id;
    String id;
    MunicipalityItem municipalityItem;
    AppBarLayout appBarLayout;
    FloatingActionButton fab;
    ArrayList<ImageModel> data = new ArrayList<>();
    ChipsAdapter chipsAdapter;

    public TabItemDetailFragment() {
    }

    public static TabItemDetailFragment newInstance() {
        return new TabItemDetailFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_item_detail, container, false);
        ButterKnife.bind(this, view);

        TabItemDetailActivity tabItemDetailActivity = (TabItemDetailActivity) getActivity();

        appBarLayout = tabItemDetailActivity.appBarLayout;
        fab = tabItemDetailActivity.fab;
        municipalityItem = tabItemDetailActivity.municipalityItem;
        id = tabItemDetailActivity.municipalityId;
        item_id = tabItemDetailActivity.item_id;

        municipalityReference = FirebaseDatabase.getInstance()
                .getReference("municipality")
                .child(id)
                .child(item_id);

        List<String> imageURLS = municipalityItem.imageURLS;
        for (int i = 0; i < imageURLS.size(); i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.setName("Image " + i);
            imageModel.setUrl(imageURLS.get(i));
            data.add(imageModel);
        }

        heart_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection())
                    onHeartClicked(municipalityReference);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isAdded()) {
            starStatus();
        }

        title.setText(municipalityItem.title);
        location.setText(municipalityItem.location);
        contact.setText(municipalityItem.contact);
        description.setText(municipalityItem.description);

        chipsAdapter = new ChipsAdapter(municipalityItem.category);
        rv_chips.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,
                false));
        rv_chips.setHasFixedSize(false);
        rv_chips.setAdapter(chipsAdapter);

        GalleryAdapter galleryAdapter = new GalleryAdapter(data, this);
        rv_gallery.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rv_gallery.setHasFixedSize(true);
        rv_gallery.setAdapter(galleryAdapter);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    //  Collapsed
                    fab.hide();
                } else {
                    //Expanded
                    fab.show();
                }
            }
        });
    }

    private void onHeartClicked(DatabaseReference municipalityRef) {
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

    public void starStatus() {
        try {
            municipalityReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MunicipalityItem municipalityItem = dataSnapshot.getValue(MunicipalityItem.class);

                    reactView.setVisibility(View.VISIBLE);

                    // Determine if the current user has liked this post and set UI accordingly
                    if (municipalityItem != null) {
                        String heartCount = null;
                        if (municipalityItem.stars.size() == 1) {
                            heart_count.setVisibility(View.VISIBLE);
                            heartCount =
                                    String.valueOf(municipalityItem.stars.size()) + " like";
                        } else if (municipalityItem.stars.size() > 1) {
                            heart_count.setVisibility(View.VISIBLE);
                            heartCount =
                                    String.valueOf(municipalityItem.stars.size()) + " likes";
                        }

                        if (isAdded()) {
                            if (municipalityItem.stars.containsKey(getUid())) {
                                heart_status.setBackground(getResources()
                                        .getDrawable(R.drawable.ic_heart_black_24dp));
                                heart_count.setText(heartCount);
                            } else {
                                heart_status.setBackground(getResources()
                                        .getDrawable(R.drawable.ic_heart_outline_black_24dp));
                                heart_count.setText(heartCount);
                            }
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

    class ChipsAdapter extends RecyclerView.Adapter
            <ChipsAdapter.ChipsViewHolder> {

        private List<String> categories;

        ChipsAdapter(List<String> categories) {
            this.categories = categories;
        }

        @Override
        public ChipsAdapter.ChipsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.category_chips, parent, false);
            return new ChipsAdapter.ChipsViewHolder(view);
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

    @Override
    public void onGalleryItemClickListener
            (int position, ImageModel imageModel, ImageView imageView) {
        appBarLayout.setExpanded(false);
        fab.hide();
        GalleryViewPagerFragment galleryViewPagerFragment =
                GalleryViewPagerFragment.newInstance(position, data);

        getFragmentManager()
                .beginTransaction()
                .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                .addToBackStack(TAG)
                .replace(R.id.content, galleryViewPagerFragment)
                .commit();
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                    .make(frameLayout, message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            sbView.setBackgroundColor(Color.RED);
            snackbar.show();
        }

    }

}