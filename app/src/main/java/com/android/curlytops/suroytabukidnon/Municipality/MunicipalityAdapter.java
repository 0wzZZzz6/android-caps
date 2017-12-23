package com.android.curlytops.suroytabukidnon.Municipality;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Model.Municipality;
import com.android.curlytops.suroytabukidnon.Municipality.Tab.TabActivity;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */
class MunicipalityAdapter extends RecyclerView.Adapter
        <MunicipalityAdapter.MunicipalityViewHolder> {

    private List<Municipality> municipalityList;
    private Context context;

    MunicipalityAdapter(Context context, List<Municipality> list) {
        this.context = context;
        this.municipalityList = list;
    }

    @Override
    public MunicipalityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.municipality_item_layout, parent, false);
        return new MunicipalityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MunicipalityViewHolder holder, int position) {
        final Municipality item = municipalityList.get(position);

        holder.municipalityTitle.setText(item.title);

        Glide.with(this.context)
                .load(item.imgUrl)
                .into(holder.municipalityImg);

        holder.municipalityTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TabActivity.class);
                intent.putExtra(BaseActivity.EXTRA_ID, item.id);
                intent.putExtra(BaseActivity.EXTRA_IMAGE, item.imgUrl);
                intent.putExtra(BaseActivity.EXTRA_MUNICIPALITY, item.municipality);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return municipalityList.size();
    }

    class MunicipalityViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.municipality_item) View municipalityView;
        @BindView(R.id.municipality_title) TextView municipalityTitle;
        @BindView(R.id.munipality_img) ImageView municipalityImg;

        MunicipalityViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
