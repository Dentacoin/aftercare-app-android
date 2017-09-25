package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.widgets.DCButton;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 9/5/17.
 */

public class DCProfileEditFragment extends DCFragment implements View.OnClickListener {

    public static final String TAG = DCProfileEditFragment.class.getSimpleName();

    private DCButton btnProfileUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        btnProfileUpdate = (DCButton) view.findViewById(R.id.btn_profile_update);
        btnProfileUpdate.setOnClickListener(this);
        setupUI();
        return view;
    }

    private void setupUI() {
        DCUser user = DCSession.getInstance().getUser();
        if (user != null) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_profile_update:
                Snacky.builder().setActivty(getActivity()).warning().setText(R.string.error_not_implemented).show();
                break;
        }
    }
}
