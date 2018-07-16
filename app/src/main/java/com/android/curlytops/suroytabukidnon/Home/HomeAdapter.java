package com.android.curlytops.suroytabukidnon.Home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.Model.Home;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by jan_frncs
 */
class HomeAdapter extends RecyclerView.Adapter
        <HomeAdapter.HomeViewHolder> {

    private List<Home> homeList;
    private Context context;

    HomeAdapter(Context context, List<Home> homeList) {
        this.context = context;
        this.homeList = homeList;
    }

    @Override
    public HomeAdapter.HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);

        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeAdapter.HomeViewHolder holder, final int position) {
        final Home item = homeList.get(position);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.suroybukidnon)
                .priority(Priority.HIGH);

        holder.textView.setText(item.name);
        Glide.with(this.context)
                .load(item.image)
                .apply(options)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        HomeViewHolder(View view) {
            super(view);
            imageView = itemView.findViewById(R.id.home_img);
            textView = itemView.findViewById(R.id.home_name);
        }

    }
}
