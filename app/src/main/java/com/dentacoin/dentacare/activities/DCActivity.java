package com.dentacoin.dentacare.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.anthonycr.grant.PermissionsManager;
import com.dentacoin.dentacare.LaunchActivity;
import com.dentacoin.dentacare.fragments.DCLoadingFragment;
import com.dentacoin.dentacare.model.DCError;

/**
 * Created by Atanas Chervarov on 7/29/17.
 * Basic Activity
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

    /**
     * Basic method for handling errors
     * @param error
     */
    public void onError(DCError error) {
        if (error != null) {
            error.show(this);
        }
    }

    /**
     * Clear the flags, launch the LaunchActivity so the authentication process can start again
     * Call this after clearing the session data
     */
    public void onLogout() {
        final Intent intent = new Intent(this, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    /**
     * Show a loading dialog
     * Remember to dismiss it when your loading has finished
     * @return
     */
    final public DCLoadingFragment showLoading() {
        final DCLoadingFragment loadingFragment = new DCLoadingFragment();
        loadingFragment.show(getFragmentManager(), DCLoadingFragment.TAG);
        return loadingFragment;
    }

}
