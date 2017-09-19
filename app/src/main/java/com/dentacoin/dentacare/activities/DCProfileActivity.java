package com.dentacoin.dentacare.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCAuthenticationFragment;
import com.dentacoin.dentacare.fragments.DCProfileEditFragment;
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

    public void editProfile() {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right);
        transaction.replace(R.id.fragment_container, new DCProfileEditFragment());
        transaction.addToBackStack(DCProfileEditFragment.TAG);
        transaction.commit();
    }
}

