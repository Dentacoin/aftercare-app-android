package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCAuthenticationActivity;
import com.dentacoin.dentacare.widgets.DCButton;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCSignupFragment extends DCFragment implements View.OnClickListener {

    public static final String TAG = DCSignupFragment.class.getSimpleName();

    private ImageView ivSignupBack;
    private SimpleDraweeView sdvSignupProfileImage;

    private DCButton btnSignupFacebook;
    private DCButton btnSignupTwitter;
    private DCButton btnSignupGoogle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        ivSignupBack = (ImageView) view.findViewById(R.id.iv_signup_back);
        ivSignupBack.setOnClickListener(this);
        sdvSignupProfileImage = (SimpleDraweeView) view.findViewById(R.id.sdv_signup_profile_image);
        sdvSignupProfileImage.setOnClickListener(this);

        btnSignupFacebook = (DCButton) view.findViewById(R.id.btn_signup_facebook);
        btnSignupFacebook.setOnClickListener(this);

        btnSignupTwitter = (DCButton) view.findViewById(R.id.btn_signup_twitter);
        btnSignupTwitter.setOnClickListener(this);

        btnSignupGoogle = (DCButton) view.findViewById(R.id.btn_signup_google);
        btnSignupGoogle.setOnClickListener(this);
        
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_signup_back:
                onBackPressed();
                break;
            case R.id.sdv_signup_profile_image:
                pickAvatar();
                break;
            case R.id.btn_signup_facebook:
                ((DCAuthenticationActivity)getActivity()).onFacebookLogin();
                break;
            case R.id.btn_signup_twitter:
                ((DCAuthenticationActivity)getActivity()).onTwitterLogin();
                break;
            case R.id.btn_signup_google:
                ((DCAuthenticationActivity)getActivity()).onGoogleLogin();
                break;
        }
    }

    private void pickAvatar() {
        //TODO: pickup picture from gallery
    }
}
