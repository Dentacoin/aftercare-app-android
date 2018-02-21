package com.dentacoin.dentacare.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCOnboardSlide;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTablLayout;
import com.dentacoin.dentacare.widgets.DCTextView;

/**
 * Created by Atanas Chervarov on 28.01.18.
 */

public class DCOnboardingActivity extends DCActivity implements View.OnClickListener {

    private DCTextView tvSkip;
    private DCButton btnNext;
    private ViewPager vpPager;
    private DCTablLayout tlTabs;

    private FragmentPagerAdapter adapter = new FragmentPagerAdapter(getFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return DCOnboardSlide.create(getString(R.string.onboarding_txt_title_1), getString(R.string.onboarding_txt_message_1), R.drawable.onboarding_1);
                case 1:
                    return DCOnboardSlide.create(getString(R.string.onboarding_txt_title_2), getString(R.string.onboarding_txt_message_2), R.drawable.onboarding_2);
                case 2:
                    return DCOnboardSlide.create(getString(R.string.onboarding_txt_title_3), getString(R.string.onboarding_txt_message_3), R.drawable.onboarding_3);
                case 3:
                    return DCOnboardSlide.create(getString(R.string.onboarding_txt_title_4), getString(R.string.onboarding_txt_message_4), R.drawable.onboarding_4);
                case 4:
                    return DCOnboardSlide.create(getString(R.string.onboarding_txt_title_5), getString(R.string.onboarding_txt_message_5), R.drawable.onboarding_5);
                case 5:
                    return DCOnboardSlide.create(getString(R.string.onboarding_txt_title_6), getString(R.string.onboarding_txt_message_6), R.drawable.onboarding_6);
            }

            return null;
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        tvSkip = findViewById(R.id.tv_skip);
        tvSkip.setOnClickListener(this);
        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        vpPager = findViewById(R.id.vp_pager);
        vpPager.setAdapter(adapter);
        tlTabs = findViewById(R.id.tl_tabs);
        tlTabs.setupWithViewPager(vpPager);
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == adapter.getCount() - 1) {
                    btnNext.setText(getString(R.string.onboarding_txt_got_it));
                } else {
                    btnNext.setText(getString(R.string.onboarding_txt_next));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_skip:
                skip();
                break;
            case R.id.btn_next:
                onNext();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (vpPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            vpPager.setCurrentItem(vpPager.getCurrentItem() - 1);
        }
    }

    private void skip() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.onboarding_txt_skip_question)
                .setMessage(R.string.onboarding_txt_skip_message)
                .setPositiveButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        complete();
                    }
                })
                .setNegativeButton(R.string.txt_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();

        dialog.show();
    }

    private void onNext() {
        if (vpPager.getCurrentItem() < adapter.getCount() - 1) {
            vpPager.setCurrentItem(vpPager.getCurrentItem() + 1);
        } else {
            complete();
        }
    }

    private void complete() {
        DCSharedPreferences.saveBoolean(DCSharedPreferences.DCSharedKey.SEEN_ONBOARDING, true);
        if (DCSession.getInstance().isValid()) {
            DCSession.getInstance().loadSocialAvatar(this);
            final Intent intent = new Intent(this, DCDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
            final Intent intent = new Intent(this, DCAuthenticationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }
}
