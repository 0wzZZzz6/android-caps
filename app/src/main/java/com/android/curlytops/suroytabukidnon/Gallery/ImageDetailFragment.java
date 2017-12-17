package com.android.curlytops.suroytabukidnon.Gallery;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.curlytops.suroytabukidnon.Model.ImageModel;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by jan_frncs
 */

public class ImageDetailFragment extends Fragment {

    private static final String EXTRA_IMAGE = "image_item";
    private static final String EXTRA_TRANSITION_NAME= "transition_name";

    public ImageDetailFragment() {
        // Required empty public constructor
    }

    public static ImageDetailFragment newInstance(ImageModel image, String transitionName) {
        ImageDetailFragment fragment = new ImageDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_IMAGE, image);
        args.putString(EXTRA_TRANSITION_NAME, transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ImageModel image = getArguments().getParcelable(EXTRA_IMAGE);
        String transitionName = getArguments().getString(EXTRA_TRANSITION_NAME);

//        final PhotoView imageView = view.findViewById(R.id.detail_image);
        final ImageView imageView = view.findViewById(R.id.detail_image);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName(transitionName);
        }

        Glide.with(getActivity())
                .load(image.getUrl())
                .into(imageView);

    }

}
