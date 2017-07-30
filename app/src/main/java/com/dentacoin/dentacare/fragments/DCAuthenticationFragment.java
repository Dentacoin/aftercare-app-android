package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCAuthenticationFragment extends DCFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.fragment_authentication, container, false);
        //TODO: implement ButterKnife
        //Add all fragment views
    }
}
