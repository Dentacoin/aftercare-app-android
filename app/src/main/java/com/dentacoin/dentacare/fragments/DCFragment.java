package com.dentacoin.dentacare.fragments;

import android.app.Activity;
import android.app.Fragment;

import com.dentacoin.dentacare.activities.DCActivity;
import com.dentacoin.dentacare.model.DCError;

/**
 * Created by Atanas Chervarov on 7/29/17.
 * Basic Fragment
 */

public class DCFragment extends Fragment {
    protected static final String TAG = DCFragment.class.getSimpleName();

    public void onError(DCError error) {
        if (error != null && getActivity() != null) {
            Activity activity = getActivity();
            if (activity instanceof DCActivity) {
                ((DCActivity) activity).onError(error);
            }
        }
    }

    public final DCLoadingFragment showLoading() {
        if (getActivity() instanceof DCActivity) {
            return ((DCActivity) getActivity()).showLoading();
        }
        return null;
    }

    public final void hideLoading() {
        if (getActivity() instanceof DCActivity) {
            ((DCActivity) getActivity()).hideLoading();
        }
    }
}
