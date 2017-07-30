package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCAuthenticationFragment;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCAuthenticationActivity extends DCActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        if (savedInstanceState != null)
            return;

        getFragmentManager().beginTransaction().add(R.id.fragment_container, new DCAuthenticationFragment()).commit();
    }
}
