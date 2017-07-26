package com.dentacoin.dentacare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dentacoin.dentacare.utils.DCConstants;

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
}