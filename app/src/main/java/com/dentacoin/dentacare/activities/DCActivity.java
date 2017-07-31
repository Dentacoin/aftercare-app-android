package com.dentacoin.dentacare.activities;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCActivity extends AppCompatActivity {
    protected static final String TAG = DCActivity.class.getSimpleName();

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }
}
