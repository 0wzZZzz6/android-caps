package com.android.curlytops.suroytabukidnon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.curlytops.suroytabukidnon.Account.AccountFragment;
import com.android.curlytops.suroytabukidnon.AppIntro.IntroActivity;
import com.android.curlytops.suroytabukidnon.Event.EventFragment;
import com.android.curlytops.suroytabukidnon.Helper.BottomNavigationViewHelper;
import com.android.curlytops.suroytabukidnon.Home.HomeFragment;
import com.android.curlytops.suroytabukidnon.Municipality.MunicipalityFragment;
import com.android.curlytops.suroytabukidnon.Search.SearchActivity;
import com.android.curlytops.suroytabukidnon.Utils.MwmDataItem;
import com.google.firebase.auth.FirebaseAuth;
import com.mapswithme.maps.api.MWMPoint;
import com.mapswithme.maps.api.MapsWithMeApi;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    boolean doubleBackToExitPressedOnce = false;
    private FirebaseAuth mAuth;

    @BindView(R.id.activity_main_toolbar)
    public Toolbar toolbar;
    @BindView(R.id.activity_main_bottomNavigationView)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        onFirstRun();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        BottomNavigation();
        switchFragment(new HomeFragment());

        setSupportActionBar(toolbar);
        toolbar.setNavigationContentDescription(R.string.app_name);
    }

    @Override
    public void onBackPressed() {
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void BottomNavigation() {
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bottom_navigation_home:
                                toolbar.setNavigationIcon(null);
                                toolbar.animate();
                                switchFragment(HomeFragment.newInstance());
                                return true;
                            case R.id.bottom_navigation_event:
                                switchFragment(EventFragment.newInstance());
                                return true;
                            case R.id.bottom_navigation_municipality:
                                toolbar.setNavigationIcon(null);
                                toolbar.animate();
                                switchFragment(MunicipalityFragment.newInstance());
                                return true;
                            case R.id.bottom_navigation_map:
                                showOnMWMMap(MwmDataItem.ITEMS);
                                return true;
                            case R.id.bottom_navigation_account:
                                toolbar.setNavigationIcon(null);
                                toolbar.animate();
                                switchFragment(AccountFragment.newInstance());
                                return true;
                        }
                        return false;
                    }
                });
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.content_main_frame, fragment)
                .commit();
    }

    private void showOnMWMMap(MwmDataItem... items) {
        MWMPoint[] points = new MWMPoint[items.length];
        for (int i = 0; i < items.length; i++)
            points[i] = items[i].toMWMPoint();

        final String title = items.length == 1 ? items[0].getName() : "Points in Bukidnon";
        MapsWithMeApi.showPointsOnMap(this, title, points);
    }

    public void onFirstRun() {
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();


        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
//            onAuthSuccess(mAuth.getCurrentUser());
            Toast.makeText(this, "go to mainActivity", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseEvents();
        firebaseMunicipalityItem();
        firebaseNews();
        firebaseBookmarked();
        firebaseUser();
        firebaseAbout();

        if (getUid() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        Log.d(TAG, getUid());
    }

}
