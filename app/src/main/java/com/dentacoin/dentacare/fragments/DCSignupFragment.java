package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCSignupFragment extends DCFragment implements View.OnClickListener {

    public static final String TAG = DCSignupFragment.class.getSimpleName();

    private ImageView ivSignupBack;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        ivSignupBack = (ImageView) view.findViewById(R.id.iv_signup_back);
        ivSignupBack.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_signup_back:
                onBackPressed();
                break;
        }
    }
}
