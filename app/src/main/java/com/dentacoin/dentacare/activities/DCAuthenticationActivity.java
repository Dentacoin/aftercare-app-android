package com.dentacoin.dentacare.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCAuthenticationFragment;
import com.dentacoin.dentacare.fragments.DCLoginFragment;
import com.dentacoin.dentacare.fragments.DCSignupFragment;

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


    public void showSignupFragment() {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right);
        transaction.replace(R.id.fragment_container, new DCSignupFragment());
        transaction.addToBackStack(DCSignupFragment.TAG);
        transaction.commit();
    }

    public void showLoginFragment() {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.slide_in_left, R.animator.slide_out_right);
        transaction.replace(R.id.fragment_container, new DCLoginFragment());
        transaction.addToBackStack(DCLoginFragment.TAG);
        transaction.commit();
    }
}
