package com.android.curlytops.suroytabukidnon.Municipality.Tab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Municipality.Tab.Fragment.About;
import com.android.curlytops.suroytabukidnon.Municipality.Tab.Fragment.Tab2;
import com.android.curlytops.suroytabukidnon.R;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by jan_frncs
 */
public class TabActivity extends BaseActivity {

    public String _id, _img, _municipality;
    int mutedColor = R.attr.colorPrimary;

    @BindView(R.id.tab_toolbar) Toolbar toolbar;
    @BindView(R.id.tab_viewpager) ViewPager viewPager;
    @BindView(R.id.tab_tablayout) TabLayout tabLayout;
    @BindView(R.id.tab_collapse_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.tab_header) ImageView tabHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        ButterKnife.bind(this);

        _id = getIntent().getExtras().getString(EXTRA_ID);
        _img = getIntent().getExtras().getString(EXTRA_IMAGE);
        _municipality = getIntent().getExtras().getString(EXTRA_MUNICIPALITY);

        supportPostponeEnterTransition();
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(UpperCaseFirstLetter(_id));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        collapsingToolbarLayout.setTitleEnabled(false);

        viewPager.setCurrentItem(0, false);


        Bundle bundle = new Bundle();
        bundle.putString("_municipality", _municipality);
        Tab2 tab2 = new Tab2();
        tab2.setArguments(bundle);

//        Drawable drawable = getResources().getDrawable(Integer.parseInt(_img));
//
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable);
//
//        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
//            @SuppressWarnings("ResourceType")
//            @Override
//            public void onGenerated(Palette palette) {
//                int vibrantColor = palette.getVibrantColor(R.color.grey_200);
//                int vibrantDarkColor = palette.getDarkVibrantColor(R.color.grey_600);
//                collapsingToolbarLayout.setContentScrimColor(vibrantColor);
//                collapsingToolbarLayout.setStatusBarScrimColor(vibrantDarkColor);
//            }
//        });

        Glide.with(this)
                .load(_img)
                .into(tabHeader);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new About(), "About");
        adapter.addFragment(new Tab2(), "Tab2");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private String UpperCaseFirstLetter(String title){
        if (title.equalsIgnoreCase("malaybalay") || title.equalsIgnoreCase("valencia")) {
            return "City of " + title.substring(0, 1).toUpperCase() + title.substring(1);
        } else {
            return "Municipality of " + title.substring(0, 1).toUpperCase() + title.substring(1);
        }
    }

    public String get_id() {
        return _id;
    }
}

