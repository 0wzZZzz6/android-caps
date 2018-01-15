package com.android.curlytops.suroytabukidnon.AppIntro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.android.curlytops.suroytabukidnon.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

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

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("intro1",
                "description1",
                R.drawable.logo,
                getResources().getColor(R.color.md_green_500)));

        addSlide(AppIntroFragment.newInstance("intro2",
                "description2",
                R.drawable.logo,
                getResources().getColor(R.color.md_green_600)));

        addSlide(AppIntroFragment.newInstance("intro3",
                "description3",
                R.drawable.logo,
                getResources().getColor(R.color.md_green_700)));


        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
