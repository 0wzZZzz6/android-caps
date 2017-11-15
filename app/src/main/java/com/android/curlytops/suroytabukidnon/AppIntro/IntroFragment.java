package com.android.curlytops.suroytabukidnon.AppIntro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.curlytops.suroytabukidnon.R;

/**
 * Created by jan_frncs
 */

public class IntroFragment extends Fragment {

    int layout;
    View view;

    public  IntroFragment(int layout){
        this.layout = layout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        switch (layout) {
            case 1:
                view = inflater.inflate(R.layout.intro1, container, false);
                break;
            case 2:
                view = inflater.inflate(R.layout.intro2, container, false);
                break;
            case 3:
                view = inflater.inflate(R.layout.intro3, container, false);
                break;
            case 4:
                view = inflater.inflate(R.layout.intro4, container, false);
                break;

            default:
                break;
        }

        return view;

    }
}
