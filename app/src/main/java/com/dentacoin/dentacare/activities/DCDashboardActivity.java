package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 8/10/17.
 */

public class DCDashboardActivity extends DCToolbarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_dashboard);
        setActionBarTitle(R.string.welcome_txt_welcome);
    }
}
