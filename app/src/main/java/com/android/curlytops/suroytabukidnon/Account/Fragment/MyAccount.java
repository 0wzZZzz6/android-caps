package com.android.curlytops.suroytabukidnon.Account.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.LoginActivity;
import com.android.curlytops.suroytabukidnon.Model.Bookmark;
import com.android.curlytops.suroytabukidnon.Model.User;
import com.android.curlytops.suroytabukidnon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jan_frncs
 */

public class MyAccount extends Fragment {

    private static final String TAG = "MyAccount";
    private static final String Node = "user";

    String email, username;

    @BindView(R.id.username)
    TextView tv_username;
    @BindView(R.id.email)
    TextView tv_email;

    public static MyAccount newInstance() {
        MyAccount myAccount = new MyAccount();
        Bundle args = new Bundle();
        myAccount.setArguments(args);
        return myAccount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myaccount, container, false);
        ButterKnife.bind(this, view);
        readUser();
        tv_email.setText(email);
        tv_username.setText(username);

        return view;
    }

    private void readUser() {
        try {
            FileInputStream fis = getContext().openFileInput("user.json");
            BufferedInputStream bis = new BufferedInputStream(fis);
            StringBuilder b = new StringBuilder();

            while (bis.available() != 0) {
                char c = (char) bis.read();
                b.append(c);
            }
            bis.close();
            fis.close();

            String json = b.toString();
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
            try {
                int i = 0;
                username = JsonPath.read(document, jsonPath(i, "username", Node));
                email = JsonPath.read(document, jsonPath(i, "email", Node));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.logout)
    public void button_logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

    private String jsonPath(int index, String keyword, String node) {
        return "$." + node + "[" + index + "]." + keyword;
    }
}
