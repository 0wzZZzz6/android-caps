package com.android.curlytops.suroytabukidnon.Utils;

import android.content.Intent;
import android.os.Bundle;

import com.android.curlytops.suroytabukidnon.BaseActivity;
import com.android.curlytops.suroytabukidnon.MainActivity;
import com.android.curlytops.suroytabukidnon.R;

/**
 * Created by jan_frncs
 */

public class SplashScreen extends BaseActivity {

    private static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

}
