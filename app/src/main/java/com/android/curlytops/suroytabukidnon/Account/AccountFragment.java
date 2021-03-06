package com.android.curlytops.suroytabukidnon.Account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.curlytops.suroytabukidnon.Account.Fragment.MyAccount;
import com.android.curlytops.suroytabukidnon.Account.Fragment.SavedEvents;
import com.android.curlytops.suroytabukidnon.Account.Fragment.SavedPlaces;
import com.android.curlytops.suroytabukidnon.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */

public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";

    public AccountFragment() {

    }

    public static AccountFragment newInstance() {
        AccountFragment accountFragment = new AccountFragment();
        Bundle args = new Bundle();
        accountFragment.setArguments(args);
        return accountFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, rootView);

        // Setting ViewPager for each Tabs
        ViewPager viewPager = rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = rootView.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);

        setHasOptionsMenu(true);
        return rootView;
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new MyAccount(), "Account");
        adapter.addFragment(new SavedEvents(), "Saved Events");
        adapter.addFragment(new SavedPlaces(), "Saved Places");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}