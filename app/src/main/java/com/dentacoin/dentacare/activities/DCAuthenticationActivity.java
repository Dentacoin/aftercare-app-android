package com.dentacoin.dentacare.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCAuthenticationFragment;
import com.dentacoin.dentacare.fragments.DCLoginFragment;
import com.dentacoin.dentacare.fragments.DCSignupFragment;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCAuthenticationActivity extends DCActivity implements FacebookCallback<LoginResult>, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 101;

    private GoogleApiClient googleApiClient;
    private CallbackManager facebookCallbackManager;
    private TwitterAuthClient twitterAuthClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        if (savedInstanceState != null)
            return;
        getFragmentManager().beginTransaction().add(R.id.fragment_container, new DCAuthenticationFragment()).commit();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
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

    public void onFacebookLogin() {
        if (facebookCallbackManager == null)
            facebookCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(facebookCallbackManager, this);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    public void onTwitterLogin() {
        if (twitterAuthClient == null)
            twitterAuthClient = new TwitterAuthClient();

        twitterAuthClient.authorize(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                //TODO:
            }

            @Override
            public void failure(TwitterException exception) {
                //TODO:
            }
        });
    }

    public void onGoogleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        } else {
            if (facebookCallbackManager != null)
                facebookCallbackManager.onActivityResult(requestCode, resultCode, data);

            if (twitterAuthClient != null)
                twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, "googleSignedIn: " + acct.getDisplayName() + " : " + acct.getIdToken());
            //TODO:
        } else {
            //TODO:
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO:
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        //TODO:
    }

    @Override
    public void onCancel() {
        //TODO:
    }

    @Override
    public void onError(FacebookException exception) {
        //TODO:
    }
}
