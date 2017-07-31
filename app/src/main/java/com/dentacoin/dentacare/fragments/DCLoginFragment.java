package com.dentacoin.dentacare.fragments;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCLoginFragment extends DCFragment implements View.OnClickListener {

    public static final String TAG = DCLoginFragment.class.getSimpleName();
    private ImageView iv_login_logo;
    private ImageView iv_login_back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        iv_login_logo = (ImageView) view.findViewById(R.id.iv_login_logo);
        Animation loginLogoAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.login_logo_animation);
        iv_login_logo.startAnimation(loginLogoAnimation);

        iv_login_back = (ImageView) view.findViewById(R.id.iv_login_back);
        iv_login_back.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_login_back:
                onBackPressed();
                break;
        }
    }
}
