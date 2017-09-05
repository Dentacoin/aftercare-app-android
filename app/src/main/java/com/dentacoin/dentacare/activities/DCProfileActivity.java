package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCAuthenticationFragment;
import com.dentacoin.dentacare.fragments.DCProfileOverviewFragment;


/**
 * Created by Atanas Chervarov on 9/1/17.
 */

public class DCProfileActivity extends DCToolbarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_profile);
        setActionBarTitle(R.string.profile_hdl_my_profile);
        getFragmentManager().beginTransaction().add(R.id.fragment_container, new DCProfileOverviewFragment()).commit();
    }
}

