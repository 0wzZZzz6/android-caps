package com.android.curlytops.suroytabukidnon.Event.news;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.curlytops.suroytabukidnon.Model.ImageModel;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by jan_frncs
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private final GalleryItemClickListener galleryItemClickListener;
    private ArrayList<ImageModel> galleryList;

    GalleryAdapter(ArrayList<ImageModel> galleryList,
                   GalleryItemClickListener galleryItemClickListener) {
        this.galleryList = galleryList;
        this.galleryItemClickListener = galleryItemClickListener;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GalleryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final GalleryViewHolder holder, int position) {
        final ImageModel imageModel = galleryList.get(position);

        Glide.with(holder.galleryImageView.getContext()).
                load(imageModel.getUrl())
                .thumbnail(0.5f)
                .into(holder.galleryImageView);

        // Set transition name same as the Image name
        ViewCompat.setTransitionName(holder.galleryImageView, imageModel.getName());

        holder.galleryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryItemClickListener.onGalleryItemClickListener(holder.getAdapterPosition(),
                        imageModel, holder.galleryImageView);
            }
        });

    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }


    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        private ImageView galleryImageView;

        GalleryViewHolder(View view) {
            super(view);
            galleryImageView = view.findViewById(R.id.item_img) ;
        }
    }
}
