package com.android.curlytops.suroytabukidnon.Search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.curlytops.suroytabukidnon.R;

import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    private final static String TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.search_frame, new SearchFragment())
                .commit();
    }
}
