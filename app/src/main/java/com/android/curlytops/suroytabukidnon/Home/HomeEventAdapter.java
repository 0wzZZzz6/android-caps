package com.android.curlytops.suroytabukidnon.Home;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.Event.EventDetailActivity;
import com.android.curlytops.suroytabukidnon.Model.Event;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */

public class HomeEventAdapter extends RecyclerView.Adapter<HomeEventAdapter.HomeEventViewHolder> {

    private List<Event> eventList;
    private Context context;

    HomeEventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @Override
    public HomeEventAdapter.HomeEventViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_event_starred, parent, false);
        return new HomeEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeEventAdapter.HomeEventViewHolder holder, int position) {
        final Event item = eventList.get(position);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.suroybukidnon)
                .priority(Priority.HIGH);

        Glide.with(this.context)
                .load(item.coverURL)
                .apply(options)
                .into(holder.home_event_item_imageView);
        holder.home_event_item_title.setText(item.title);
        holder.home_event_item_date.setText(getDate(item));
        holder.home_event_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("myEvent", item);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
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
                        fromDate.getDayOfMonth() + " - " + toDate.getDayOfMonth() + " " +
                        fromDate.getYear();
            } else if (!(fromDate.getMonthOfYear() == toDate.getMonthOfYear()) &&
                    fromDate.getYear() == toDate.getYear()) {
                return simpleDateFormat.format(item.startDate) + " " + fromDate.getDayOfMonth()
                        + " - " +
                        simpleDateFormat.format(item.endDate) + " " + toDate.getDayOfMonth() + " " +
                        fromDate.getYear();
            } else {
                date = convertDate(item.startDate) + " - " + convertDate(item.endDate);
            }
        }

        return date;
    }

    private String convertDate(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        return formatter.format(date);
    }

    class HomeEventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.home_event_item)
        View home_event_item;
        @BindView(R.id.home_event_item_title)
        TextView home_event_item_title;
        @BindView(R.id.home_event_item_date)
        TextView home_event_item_date;
        @BindView(R.id.home_event_item_imageView)
        ImageView home_event_item_imageView;

        HomeEventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}