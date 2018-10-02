package com.dentacoin.dentacare.fragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCAuthenticationActivity;
import com.dentacoin.dentacare.widgets.DCTextView;


/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCAuthenticationFragment extends DCFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);
        view.findViewById(R.id.btn_auth_civic).setOnClickListener(v -> ((DCAuthenticationActivity) getActivity()).onCivicLogin());
        view.findViewById(R.id.btn_auth_facebook).setOnClickListener(v -> ((DCAuthenticationActivity) getActivity()).onFacebookLogin());
        view.findViewById(R.id.btn_auth_google).setOnClickListener(v -> ((DCAuthenticationActivity) getActivity()).onGoogleLogin());
        view.findViewById(R.id.btn_auth_twitter).setOnClickListener(v -> ((DCAuthenticationActivity) getActivity()).onTwitterLogin());
        DCTextView tvAuthEmailLogin = view.findViewById(R.id.tv_auth_email_login);
        tvAuthEmailLogin.setOnClickListener(v -> ((DCAuthenticationActivity) getActivity()).showLoginFragment());
        tvAuthEmailLogin.setPaintFlags(tvAuthEmailLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        return view;
    }
}
