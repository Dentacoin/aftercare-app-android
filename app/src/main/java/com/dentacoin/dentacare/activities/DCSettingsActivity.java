package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 9/19/17.
 */

public class DCSettingsActivity extends DCToolbarActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_settings);
        setActionBarTitle(R.string.settings_hdl_settings);
    }
}
