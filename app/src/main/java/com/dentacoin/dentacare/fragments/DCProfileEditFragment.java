package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 9/5/17.
 */

public class DCProfileEditFragment extends DCFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        return view;
    }
}
