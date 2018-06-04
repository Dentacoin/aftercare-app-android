package com.dentacoin.dentacare.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.anthonycr.grant.PermissionsManager;
import com.dentacoin.dentacare.LaunchActivity;
import com.dentacoin.dentacare.fragments.DCLoadingFragment;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.network.response.DCAuthToken;
import com.dentacoin.dentacare.utils.DCLocalNotificationsManager;
import com.dentacoin.dentacare.widgets.DCSoundManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.twitter.sdk.android.core.TwitterCore;

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
        onError(error, 0);
    }

    public void onError(DCError error, int duration) {
        if (error != null) {
            error.show(this, duration);
        }
    }

    /**
     * Clear the flags, launch the LaunchActivity so the authentication process can start again
     * Call this after clearing the session data
     */
    public void onLogout() {
        DCLocalNotificationsManager.getInstance().scheduleNotifications(this, true);
        DCSession.getInstance().clear();

        LoginManager.getInstance().logOut();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, gso);
        client.signOut();

        final Intent intent = new Intent(this, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    /**
     * Switch accounts
     */
    public void onAccountSwitch(DCAuthToken token) {
        DCSession.getInstance().partialClear();

        LoginManager.getInstance().logOut();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, gso);
        client.signOut();

        DCSession.getInstance().setAuthToken(token);

        DCApiManager.getInstance().getUser(new DCResponseListener<DCUser>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);
            }

            @Override
            public void onResponse(DCUser object) {
                final Intent intent = new Intent(DCActivity.this, DCDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
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

    @Override
    public void onPause() {
        super.onPause();
        DCSoundManager.getInstance().cancelSounds();
        DCSoundManager.getInstance().pauseMusic();
    }

    @Override
    public void onResume() {
        super.onResume();
        DCSoundManager.getInstance().resumeMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
