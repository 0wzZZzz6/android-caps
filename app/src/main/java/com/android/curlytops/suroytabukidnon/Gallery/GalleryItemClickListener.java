package com.android.curlytops.suroytabukidnon.Gallery;

import android.widget.ImageView;

import com.android.curlytops.suroytabukidnon.Model.ImageModel;

/**
 * Created by jan_frncs
 */

public interface GalleryItemClickListener {
    void onGalleryItemClickListener(int position, ImageModel imageModel, ImageView imageView);
}
