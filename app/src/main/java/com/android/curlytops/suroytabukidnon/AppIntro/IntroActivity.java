package com.android.curlytops.suroytabukidnon.AppIntro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by jan_frncs
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.

        IntroFragment introFragment1 = new IntroFragment(1);
        IntroFragment introFragment2 = new IntroFragment(2);
        IntroFragment introFragment3 = new IntroFragment(3);
        IntroFragment introFragment4 = new IntroFragment(4);

        addSlide(introFragment1);
        addSlide(introFragment2);
        addSlide(introFragment3);
        addSlide(introFragment4);

        // Hide Skip/Done button.
        showSkipButton(false);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
