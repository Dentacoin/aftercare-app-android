package com.dentacoin.dentacare.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.anthonycr.grant.PermissionsManager;
import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCAuthenticationFragment;
import com.dentacoin.dentacare.fragments.DCLoginFragment;
import com.dentacoin.dentacare.fragments.DCSignupFragment;
import com.dentacoin.dentacare.fragments.DCWelcomeFragment;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.network.response.DCAuthToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */
public class DCAuthenticationActivity extends DCActivity {

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        if (connectionResult.getErrorMessage() != null) {
                            onError(new DCError(connectionResult.getErrorMessage()));
                        }
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * Shows the signup fragment
     */
    public void showSignupFragment() {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right);
        transaction.replace(R.id.fragment_container, new DCSignupFragment());
        transaction.addToBackStack(DCSignupFragment.TAG);
        transaction.commit();
    }

    /**
     * Shows the Login Fragment
     */
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

        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                final DCUser user = new DCUser();
                user.setFacebookAccessToken(loginResult.getAccessToken().getToken());
                user.setFacebookID(loginResult.getAccessToken().getUserId());

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, birthday, gender");

                GraphRequest request = new GraphRequest(loginResult.getAccessToken(), "me", parameters, null, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                            JSONObject objResponse = response.getJSONObject();
                            try {
                                if (objResponse.has("first_name"))
                                    user.setFirstname(objResponse.getString("first_name"));
                                if (objResponse.has("last_name"))
                                    user.setLastname(objResponse.getString("last_name"));
                                if (objResponse.has("email"))
                                    user.setEmail(objResponse.getString("email"));
                                if (objResponse.has("birthday"))
                                    user.setBirthday((Date)objResponse.get("birthday"));
                                if (objResponse.has("gender"))
                                    user.setGender(objResponse.getString("gender"));
                            } catch (Exception e) {
                            }
                        }

                        DCApiManager.getInstance().loginUSer(user, new DCResponseListener<DCAuthToken>() {
                            @Override
                            public void onFailure(DCError loginError) {
                                DCApiManager.getInstance().registerUser(user, new DCResponseListener<DCAuthToken>() {
                                    @Override
                                    public void onFailure(DCError registrationError) {
                                        DCAuthenticationActivity.this.onError(registrationError);
                                    }

                                    @Override
                                    public void onResponse(DCAuthToken object) {
                                        handleAuthentication(object);
                                    }
                                });
                            }
                            @Override
                            public void onResponse(DCAuthToken object) {
                                handleAuthentication(object);
                            }
                        });
                    }
                });
                request.executeAsync();
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException exception) {
                if (exception != null && exception.getMessage() != null) {
                    DCAuthenticationActivity.this.onError(new DCError(exception.getMessage()));
                }
            }
        });

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
    }

    public void onTwitterLogin() {
        if (twitterAuthClient == null)
            twitterAuthClient = new TwitterAuthClient();

        twitterAuthClient.authorize(this, new Callback<TwitterSession>() {
            @Override
            public void success(final Result<TwitterSession> authResult) {
                TwitterApiClient client = TwitterCore.getInstance().getApiClient();
                client.getAccountService().verifyCredentials(false, true, true).enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        if (result != null) {
                            final User twitterUser = result.data;
                            final DCUser user = new DCUser();

                            user.setEmail(twitterUser.email);
                            user.setTwitterID(Long.toString(authResult.data.getUserId()));
                            user.setTwitterAccessToken(authResult.data.getAuthToken().token);
                            user.setTwitterAccessTokenSecret(authResult.data.getAuthToken().secret);
                            user.setFirstname(twitterUser.name);

                            DCApiManager.getInstance().loginUSer(user, new DCResponseListener<DCAuthToken>() {
                                @Override
                                public void onFailure(DCError error) {
                                    DCApiManager.getInstance().registerUser(user, new DCResponseListener<DCAuthToken>() {
                                        @Override
                                        public void onFailure(DCError error) {
                                            DCAuthenticationActivity.this.onError(error);
                                        }

                                        @Override
                                        public void onResponse(DCAuthToken object) {
                                            handleAuthentication(object);
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(DCAuthToken object) {
                                    handleAuthentication(object);
                                }
                            });
                        }
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        if (exception != null && exception.getMessage() != null) {
                            onError(new DCError(exception.getMessage()));
                        }
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                if (exception != null && exception.getMessage() != null) {
                    onError(new DCError(exception.getMessage()));
                }
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
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                final DCUser user = new DCUser();
                user.setEmail(acct.getEmail());
                user.setFirstname(acct.getGivenName());
                user.setLastname(acct.getFamilyName());
                user.setGoogleID(acct.getId());
                user.setGoogleAccessToken(acct.getIdToken());

                DCApiManager.getInstance().loginUSer(user, new DCResponseListener<DCAuthToken>() {
                    @Override
                    public void onFailure(DCError error) {
                        DCApiManager.getInstance().registerUser(user, new DCResponseListener<DCAuthToken>() {
                            @Override
                            public void onFailure(DCError error) {
                                onError(error);
                            }

                            @Override
                            public void onResponse(DCAuthToken object) {
                                handleAuthentication(object);
                            }
                        });
                    }

                    @Override
                    public void onResponse(DCAuthToken object) {
                        handleAuthentication(object);
                    }
                });
            }
        } else {
            if (result.getStatus().getStatusMessage() != null)
                onError(new DCError(result.getStatus().getStatusMessage()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }


    public void loginUser(DCUser user) {
        DCApiManager.getInstance().loginUSer(user, new DCResponseListener<DCAuthToken>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);
            }

            @Override
            public void onResponse(DCAuthToken object) {
                handleAuthentication(object);
            }
        });
    }

    public void signupUser(DCUser user) {
        DCApiManager.getInstance().registerUser(user, new DCResponseListener<DCAuthToken>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);
            }

            @Override
            public void onResponse(DCAuthToken object) {
                handleAuthentication(object);
            }
        });
    }

    //TODO: Redirect to dashboard!
    private void handleAuthentication(DCAuthToken token) {
        DCSession.getInstance().setAuthToken(token);
        if (DCSession.getInstance().getAuthToken() != null && DCSession.getInstance().getAuthToken().isValid()) {
            final FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
            transaction.replace(R.id.fragment_container, new DCWelcomeFragment());
            transaction.commit();
        }
    }
}
