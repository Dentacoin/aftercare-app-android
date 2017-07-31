package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCSignupFragment extends DCFragment implements View.OnClickListener {

    public static final String TAG = DCSignupFragment.class.getSimpleName();

    private ImageView ivSignupBack;
    private SimpleDraweeView sdvSignupProfileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        ivSignupBack = (ImageView) view.findViewById(R.id.iv_signup_back);
        ivSignupBack.setOnClickListener(this);
        sdvSignupProfileImage = (SimpleDraweeView) view.findViewById(R.id.sdv_signup_profile_image);
        sdvSignupProfileImage.setOnClickListener(this);

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
                return;
        }
    }

    private void pickAvatar() {
        //TODO: pickup picture from gallery
    }
}
