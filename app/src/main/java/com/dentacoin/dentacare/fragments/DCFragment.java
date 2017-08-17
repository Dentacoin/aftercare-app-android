package com.dentacoin.dentacare.fragments;

import android.app.Fragment;

/**
 * Created by Atanas Chervarov on 7/29/17.
 * Basic Fragment
 */

public class DCFragment extends Fragment {
    protected static final String TAG = DCFragment.class.getSimpleName();

    protected void onBackPressed() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}
