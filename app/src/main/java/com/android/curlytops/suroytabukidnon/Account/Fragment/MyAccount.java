package com.android.curlytops.suroytabukidnon.Account.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.curlytops.suroytabukidnon.LoginActivity;
import com.android.curlytops.suroytabukidnon.R;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jan_frncs
 */

public class MyAccount extends Fragment {

    public static MyAccount newInstance(){
        MyAccount myAccount = new MyAccount();
        Bundle args= new Bundle();
        myAccount.setArguments(args);
        return myAccount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myaccount, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.logout)
    public void button_logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), LoginActivity.class));
    }
}
