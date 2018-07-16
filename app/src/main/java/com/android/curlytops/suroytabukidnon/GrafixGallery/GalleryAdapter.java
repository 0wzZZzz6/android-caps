package com.android.curlytops.suroytabukidnon.GrafixGallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.Model.ImageModel;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jan_frncs
 */
public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "GalleryAdapter";
    private Context context;
    private List<ImageModel> data = new ArrayList<>();
    private String viewMode;
    private static final String viewMode_events = "events";
    private static final String viewMode_places = "places";

    public GalleryAdapter(Context context, List<ImageModel> data, String viewMode) {
        this.viewMode = viewMode;
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;
        if (viewMode.equalsIgnoreCase(viewMode_events)) {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.gallery_item_events, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.gallery_item_places, parent, false);
        }

        viewHolder = new MyItemHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.suroybukidnon)
                .priority(Priority.HIGH);

        if (data.size() > 6 && position == 5 && viewMode.equalsIgnoreCase(viewMode_places)) {
            int more = data.size() - 5;
            String textContent = "+" + more;
            ((MyItemHolder) holder).textLayout.setVisibility(View.VISIBLE);
            ((MyItemHolder) holder).textView.setText(textContent);
            Glide.with(context)
                    .load(data.get(position).getUrl())
                    .apply(options)
                    .into(((MyItemHolder) holder).mImg);
        } else {
            Glide.with(context)
                    .load(data.get(position).getUrl())
                    .apply(options)
                    .into(((MyItemHolder) holder).mImg);
        }
    }

    @Override
    public int getItemCount() {
        if (data.size() > 6 && viewMode.equalsIgnoreCase(viewMode_places)) {
            return 6;
        } else {
            return data.size();
        }
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        View textLayout;
        TextView textView;

        MyItemHolder(View itemView) {
            super(itemView);

            mImg = itemView.findViewById(R.id.item_img);
            textLayout = itemView.findViewById(R.id.gallery_item_places_layout);
            textView = itemView.findViewById(R.id.gallery_item_places_textView);
        }

    }


}
