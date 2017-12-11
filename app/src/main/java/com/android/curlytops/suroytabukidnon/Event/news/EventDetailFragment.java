package com.android.curlytops.suroytabukidnon.Event.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.Model.ImageModel;
import com.android.curlytops.suroytabukidnon.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */

public class EventDetailFragment extends Fragment implements GalleryItemClickListener {

    public static final String TAG = EventDetailFragment.class.getSimpleName();

    Event event;
    ArrayList<ImageModel> data = new ArrayList<>();

    @BindView(R.id.fragment_event_details_title)
    TextView textView_title;

    public EventDetailFragment() {
    }


    public static EventDetailFragment newInstance() {
        return new EventDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventDetailActivity eventDetailActivity = (EventDetailActivity) getActivity();
        event = eventDetailActivity.getEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView_title.setText(event.title);

        List<String> imageURLS = event.getImageURLS();
        for (int i = 0; i < imageURLS.size(); i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.setName("Image " + i);
            imageModel.setUrl(imageURLS.get(i));
            data.add(imageModel);
        }


        GalleryAdapter galleryAdapter = new GalleryAdapter(data, this);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_gallery);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(galleryAdapter);
    }

    @Override
    public void onGalleryItemClickListener(int position, ImageModel imageModel, ImageView imageView) {
        GalleryViewPagerFragment galleryViewPagerFragment =
                GalleryViewPagerFragment.newInstance(position, data);

        getFragmentManager()
                .beginTransaction()
                .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                .addToBackStack(TAG)
                .replace(R.id.content, galleryViewPagerFragment)
                .commit();
    }

}
