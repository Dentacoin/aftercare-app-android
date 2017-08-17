package com.dentacoin.dentacare.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.dentacoin.dentacare.LaunchActivity;
import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.network.DCApiManager;

import de.mateware.snacky.Snacky;

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
     * If the error type is NETWORK it will check if the device has lost connectivity
     * @param error
     */
    public void onError(DCError error) {
        if (error != null) {
            if (error.getCode() == DCError.DCErrorType.NETWORK.getCode()) {
                if (!DCApiManager.hasInternetConnectivity(this)) {
                    Snacky.builder().setActivty(this).setText(R.string.error_txt_offline).error().show();
                    return;
                }
            }

            if (error.getMessage(this) != null) {
                Snacky.builder().setActivty(this).setText(error.getMessage(this)).error().show();
            }
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
}
