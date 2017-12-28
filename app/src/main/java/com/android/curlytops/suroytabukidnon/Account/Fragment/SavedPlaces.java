package com.android.curlytops.suroytabukidnon.Account.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.curlytops.suroytabukidnon.R;

import butterknife.ButterKnife;

/**
 * Created by jan_frncs
 */

public class SavedPlaces extends Fragment {

    public static SavedPlaces newInstance(){
        SavedPlaces savedPlaces = new SavedPlaces();
        Bundle args= new Bundle();
        savedPlaces.setArguments(args);
        return savedPlaces;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savedplaces, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
}
