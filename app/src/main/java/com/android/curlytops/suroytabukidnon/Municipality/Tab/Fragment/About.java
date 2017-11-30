package com.android.curlytops.suroytabukidnon.Municipality.Tab.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.curlytops.suroytabukidnon.Municipality.Tab.TabActivity;
import com.android.curlytops.suroytabukidnon.R;

/**
 * Created by jan_frncs
 */
public class About extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        TextView textView = rootView.findViewById(R.id.textView2);
        textView.setText(((TabActivity) getActivity()).get_id());
        return rootView;
    }
}
