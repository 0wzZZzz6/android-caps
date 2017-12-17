package com.android.curlytops.suroytabukidnon.Municipality.Tab_Item_Details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.Event.EventDetailActivity;
import com.android.curlytops.suroytabukidnon.Event.EventDetailFragment;
import com.android.curlytops.suroytabukidnon.Gallery.GalleryAdapter;
import com.android.curlytops.suroytabukidnon.Gallery.GalleryItemClickListener;
import com.android.curlytops.suroytabukidnon.Gallery.GalleryViewPagerFragment;
import com.android.curlytops.suroytabukidnon.Model.ImageModel;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */

public class TabItemDetailFragment extends Fragment implements GalleryItemClickListener {

    public static final String TAG = TabItemDetailFragment.class.getSimpleName();

    @BindView(R.id.fragment_tab_item_title)
    TextView tv_title;
    @BindView(R.id.fragment_tab_item_detail1)
    TextView tv_detail1;
    @BindView(R.id.fragment_tab_item_detail2)
    TextView tv_detail2;
    @BindView(R.id.fragment_tab_item_detail3)
    TextView tv_detail3;

    @BindView(R.id.heart_status)
    ImageView heart_status;
    @BindView(R.id.heart_count)
    TextView heart_count;

    @BindView(R.id.fragment_tab_item_rv_chips)
    RecyclerView rv_chips;
    @BindView(R.id.fragment_tab_item_rv_gallery)
    RecyclerView rv_gallery;

    DatabaseReference municipalityReference;

    String item_id;
    String municipality;
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
        municipality = tabItemDetailActivity.municipality;
        item_id = municipalityItem.getId();
        municipalityReference = FirebaseDatabase.getInstance()
                .getReference("municipality")
                .child(municipality).child(item_id);

        List<String> imageURLS = municipalityItem.getImageURLS();
        for (int i = 0; i < imageURLS.size(); i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.setName("Image " + i);
            imageModel.setUrl(imageURLS.get(i));
            data.add(imageModel);
        }


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(isAdded()){
            starStatus();
        }

        tv_title.setText(municipalityItem.getTitle());
        tv_detail1.setText(municipalityItem.getDescription());
        tv_detail2.setText(municipalityItem.getContact());

        chipsAdapter = new ChipsAdapter(municipalityItem.getCategory());
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

    public void starStatus() {
        try {
            municipalityReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MunicipalityItem municipalityItem = dataSnapshot.getValue(MunicipalityItem.class);

                    // Determine if the current user has liked this post and set UI accordingly
                    if (municipalityItem.stars.containsKey(getUid())) {
                        heart_status.setBackground(getResources()
                                .getDrawable(R.drawable.ic_heart_black_24dp));
                        heart_count.setText("(" + municipalityItem.stars.size() + ")");
                    } else {
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
    public void onGalleryItemClickListener(int position, ImageModel imageModel, ImageView imageView) {
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

    @Override
    public void onResume() {
        super.onResume();
    }

}