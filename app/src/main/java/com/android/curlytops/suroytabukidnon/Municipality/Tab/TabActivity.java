package com.android.curlytops.suroytabukidnon.Municipality.Tab;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Municipality.Tab.Fragment.About;
import com.android.curlytops.suroytabukidnon.Municipality.Tab.Fragment.More;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by jan_frncs
 */
public class TabActivity extends BaseActivity {

    public String municipalityId, imageUrl, municipality;

    @BindView(R.id.tab_toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.tab_collapse_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.tab_header)
    ImageView tabHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        ButterKnife.bind(this);

        municipalityId = getIntent().getExtras().getString(MUNICIPALITY_ID);
        imageUrl = getIntent().getExtras().getString(IMAGEURL);
        municipality = getIntent().getExtras().getString(MUNICIPALITY);

        Log.d("SHIELAMAE", municipalityId + " " + municipality);

        supportPostponeEnterTransition();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(FormatTitle(municipality));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        collapsingToolbarLayout.setTitleEnabled(false);

        viewPager.setCurrentItem(0, false);

//        Bundle bundle = new Bundle();
//        bundle.putString("municipality", municipality);
//        More more = new More();
//        more.setArguments(bundle);

        Glide.with(this)
                .load(imageUrl)
                .into(tabHeader);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new About(), "About");
        adapter.addFragment(new More(), "More");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private String FormatTitle(String title) {
        if (title.equalsIgnoreCase("malaybalay") || title.equalsIgnoreCase("valencia")) {
            return "City of " + title;
        } else {
            return "Municipality of " + title;
        }
    }

    public String getMunicipalityId() {
        return municipalityId;
    }

    public String getMunicipality() {
        return municipality;
    }
}

