package com.android.curlytops.suroytabukidnon.Municipality;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.Model.Municipality;
import com.android.curlytops.suroytabukidnon.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jan_frncs
 */
public class MunicipalityFragment extends Fragment {

    private RecyclerView recyclerView;

    public MunicipalityFragment() {
    }

    public static MunicipalityFragment newInstance() {
        MunicipalityFragment municipalityFragment = new MunicipalityFragment();
        Bundle args = new Bundle();
        municipalityFragment.setArguments(args);
        return municipalityFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setPadding(0, 0, 0, 52);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        MunicipalityAdapter adapter =
                    new MunicipalityAdapter(getActivity(),
                            new BaseActivity().getMunicipalityJson(getContext()));
        recyclerView.setAdapter(adapter);
    }

}
