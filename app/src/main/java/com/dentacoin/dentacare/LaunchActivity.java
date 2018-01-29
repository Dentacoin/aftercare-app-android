package com.dentacoin.dentacare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dentacoin.dentacare.activities.DCAuthenticationActivity;
import com.dentacoin.dentacare.activities.DCDashboardActivity;
import com.dentacoin.dentacare.activities.DCOnboardingActivity;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCSharedPreferences;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        checkForUpdates();
    }

    private void checkForCrashes() {
        // Send crash reports only for release versions
        if (BuildConfig.BUILD_TYPE.equals(DCConstants.BUILD_TYPE_RELEASE)) {
            CrashManager.register(this);
        }
    }

    private void checkForUpdates() {
        // Remove this for store builds
        if (!BuildConfig.FLAVOR.equals(DCConstants.BUILD_FALVOR_LIVE) && !BuildConfig.BUILD_TYPE.equals(DCConstants.BUILD_TYPE_DEBUG)) {
            UpdateManager.register(this);
        }
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
        checkSession();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    private void checkSession() {
        if (DCSession.getInstance().isValid()) {
            if (!DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.SEEN_ONBOARDING, false)) {
                final Intent intent = new Intent(this, DCOnboardingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            } else {
                DCSession.getInstance().loadSocialAvatar(this);
                final Intent intent = new Intent(this, DCDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        } else {
            final Intent intent = new Intent(this, DCAuthenticationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }
}