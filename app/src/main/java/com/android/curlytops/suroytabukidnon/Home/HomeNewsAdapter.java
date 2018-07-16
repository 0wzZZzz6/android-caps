package com.android.curlytops.suroytabukidnon.Home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.Model.News;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */

public class HomeNewsAdapter extends
        RecyclerView.Adapter<HomeNewsAdapter.HomeNewsViewHolder> {

    private List<News> newsList;
    private Context context;

    HomeNewsAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @Override
    public HomeNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from
                (parent.getContext()).inflate(R.layout.home_news_item, parent, false);
        return new HomeNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeNewsViewHolder holder, int position) {
        final News item = newsList.get(position);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.suroybukidnon)
                .priority(Priority.HIGH);

        Glide.with(this.context)
                .load(item.coverURL)
                .apply(options)
                .into(holder.home_news_item_imageView);

        holder.home_news_item_title.setText(item.title);
        holder.home_news_item_timestamp.setReferenceTime(item.timestamp);
        holder.home_news_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = item.link;
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class HomeNewsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.home_news_item)
        View home_news_item;
        @BindView(R.id.home_news_item_title)
        TextView home_news_item_title;
        @BindView(R.id.home_news_item_imageView)
        ImageView home_news_item_imageView;
        @BindView(R.id.home_news_item_timestamp)
        RelativeTimeTextView home_news_item_timestamp;

        HomeNewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
