package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCAuthenticationActivity;
import com.dentacoin.dentacare.widgets.DCButton;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCLoginFragment extends DCFragment implements View.OnClickListener {

    public static final String TAG = DCLoginFragment.class.getSimpleName();
    private ImageView ivLoginLogo;
    private ImageView ivLoginBack;

    private DCButton btnLoginFacebook;
    private DCButton btnLoginTwitter;
    private DCButton btnLoginGoogle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ivLoginLogo = (ImageView) view.findViewById(R.id.iv_login_logo);
        Animation loginLogoAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.login_logo_animation);
        ivLoginLogo.startAnimation(loginLogoAnimation);

        ivLoginBack = (ImageView) view.findViewById(R.id.iv_login_back);
        ivLoginBack.setOnClickListener(this);

        btnLoginFacebook = (DCButton) view.findViewById(R.id.btn_login_facebook);
        btnLoginFacebook.setOnClickListener(this);

        btnLoginGoogle = (DCButton) view.findViewById(R.id.btn_login_google);
        btnLoginGoogle.setOnClickListener(this);

        btnLoginTwitter = (DCButton) view.findViewById(R.id.btn_login_twitter);
        btnLoginTwitter.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_login_back:
                onBackPressed();
                break;
            case R.id.btn_login_facebook:
                ((DCAuthenticationActivity) getActivity()).onFacebookLogin();
                break;
            case R.id.btn_login_twitter:
                ((DCAuthenticationActivity) getActivity()).onTwitterLogin();
            case R.id.btn_login_google:
                ((DCAuthenticationActivity) getActivity()).onGoogleLogin();
        }
    }
}
