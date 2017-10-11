package com.dentacoin.dentacare.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCAuthenticationActivity;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextInputEditText;
import com.dentacoin.dentacare.widgets.DCTextInputLayout;
import com.dentacoin.dentacare.widgets.DCTextView;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCLoginFragment extends DCFragment implements View.OnClickListener, View.OnFocusChangeListener {

    public static final String TAG = DCLoginFragment.class.getSimpleName();
    private ImageView ivLoginLogo;

    private DCButton btnLoginFacebook;
    private DCButton btnLoginTwitter;
    private DCButton btnLoginGoogle;
    private DCButton btnLogin;

    private DCTextInputLayout tilLoginEmail;
    private DCTextInputEditText tietLoginEmail;
    private DCTextInputLayout tilLoginPassword;
    private DCTextInputEditText tietLoginPassword;
    private DCTextView tvLoginForgotPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ivLoginLogo = (ImageView) view.findViewById(R.id.iv_login_logo);

        Resources r = getResources();
        float translationPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());

        AlphaAnimation logoAlphaAnimation = new AlphaAnimation(0, 1);
        logoAlphaAnimation.setDuration(500);

        TranslateAnimation logoTranslateAnimation = new TranslateAnimation(0, 0, translationPx, 0);
        logoTranslateAnimation.setDuration(500);

        AnimationSet logoAnimationSet = new AnimationSet(true);
        logoAnimationSet.addAnimation(logoAlphaAnimation);
        logoAnimationSet.addAnimation(logoTranslateAnimation);

        ivLoginLogo.startAnimation(logoAnimationSet);

        btnLoginFacebook = (DCButton) view.findViewById(R.id.btn_login_facebook);
        btnLoginFacebook.setOnClickListener(this);

        btnLoginGoogle = (DCButton) view.findViewById(R.id.btn_login_google);
        btnLoginGoogle.setOnClickListener(this);

        btnLoginTwitter = (DCButton) view.findViewById(R.id.btn_login_twitter);
        btnLoginTwitter.setOnClickListener(this);

        btnLogin = (DCButton) view.findViewById(R.id.btn_login_login);
        btnLogin.setOnClickListener(this);

        tilLoginEmail = (DCTextInputLayout) view.findViewById(R.id.til_login_email);
        tietLoginEmail = (DCTextInputEditText) view.findViewById(R.id.tiet_login_email);
        tietLoginEmail.setOnFocusChangeListener(this);

        tilLoginPassword = (DCTextInputLayout) view.findViewById(R.id.til_login_password);
        tietLoginPassword = (DCTextInputEditText) view.findViewById(R.id.tiet_login_password);

        tvLoginForgotPassword = (DCTextView) view.findViewById(R.id.tv_login_forgot_password);
        tvLoginForgotPassword.setOnClickListener(this);

        return view;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.tiet_login_email:
                if (TextUtils.isEmpty(tietLoginEmail.getText().toString())) {
                    tilLoginEmail.setErrorEnabled(true);
                    tilLoginEmail.setError(getString(R.string.error_txt_email_required));
                }
                else if (!DCUtils.isValidEmail(tietLoginEmail.getText().toString())) {
                    tilLoginEmail.setErrorEnabled(true);
                    tilLoginEmail.setError(getString(R.string.error_txt_email_not_valid));
                }
                else {
                    tilLoginEmail.setErrorEnabled(false);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_login:
                login();
                break;
            case R.id.btn_login_facebook:
                ((DCAuthenticationActivity) getActivity()).onFacebookLogin();
                break;
            case R.id.btn_login_twitter:
                ((DCAuthenticationActivity) getActivity()).onTwitterLogin();
                break;
            case R.id.btn_login_google:
                ((DCAuthenticationActivity) getActivity()).onGoogleLogin();
                break;
            case R.id.tv_login_forgot_password:
                ((DCAuthenticationActivity) getActivity()).showPasswordResetFragment();
                break;
        }
    }

    private void login() {
        if (validate()) {
            final DCUser user = new DCUser();
            user.setEmail(tietLoginEmail.getText().toString());
            user.setPassword(tietLoginPassword.getText().toString());
            ((DCAuthenticationActivity) getActivity()).loginUser(user);
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(tietLoginEmail.getText().toString())) {
            onError(new DCError(R.string.error_txt_email_required));
            return false;
        }
        else if (!DCUtils.isValidEmail(tietLoginEmail.getText().toString())) {
            onError(new DCError(R.string.error_txt_email_not_valid));
            return false;
        }

        return true;
    }
}
