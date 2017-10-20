package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCAuthenticationActivity;
import com.dentacoin.dentacare.widgets.DCButton;


/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCAuthenticationFragment extends DCFragment implements View.OnClickListener {

    private ImageView ivAuthDentacoinLogo;
    private ImageView ivAuthLogo;
    private DCButton btnAuthSignup;
    private DCButton btnAuthLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);
        ivAuthDentacoinLogo = (ImageView) view.findViewById(R.id.iv_auth_dentacoin_logo);
        ivAuthLogo = (ImageView) view.findViewById(R.id.iv_auth_logo);
        btnAuthSignup = (DCButton) view.findViewById(R.id.btn_auth_signup);
        btnAuthSignup.setOnClickListener(this);
        btnAuthLogin = (DCButton) view.findViewById(R.id.btn_auth_login);
        btnAuthLogin.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_auth_login:
                ((DCAuthenticationActivity) getActivity()).showLoginFragment();
                break;
            case R.id.btn_auth_signup:
                ((DCAuthenticationActivity) getActivity()).showSignupFragment();
                break;
        }
    }
}
