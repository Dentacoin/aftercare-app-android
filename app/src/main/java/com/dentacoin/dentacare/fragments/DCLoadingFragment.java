package com.dentacoin.dentacare.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 10/1/17.
 */

public class DCLoadingFragment extends DialogFragment {

    public static final String TAG = DCLoginFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_loading, container, false);
        return view;
    }
}
