package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.drawee.view.SimpleDraweeView;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 9/5/17.
 */

public class DCProfileOverviewFragment extends DCFragment implements View.OnClickListener {

    private SimpleDraweeView sdvProfileAvatar;
    private DCTextView tvProfileFullname;
    private DCTextView tvProfileEmail;
    private DCTextView tvProfileAddress;
    private DCTextView tvProfileAge;
    private DCTextView tvProfileGender;
    private DCButton btnProfileEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_profile_overview, container, false);

        sdvProfileAvatar = (SimpleDraweeView) view.findViewById(R.id.sdv_profile_avatar);
        tvProfileFullname = (DCTextView) view.findViewById(R.id.tv_profile_fullname);
        tvProfileEmail = (DCTextView) view.findViewById(R.id.tv_profile_email);
        tvProfileAddress = (DCTextView) view.findViewById(R.id.tv_profile_address);
        tvProfileAge = (DCTextView) view.findViewById(R.id.tv_profile_age);
        tvProfileGender = (DCTextView) view.findViewById(R.id.tv_profile_gender);
        btnProfileEdit = (DCButton) view.findViewById(R.id.btn_profile_edit);
        btnProfileEdit.setOnClickListener(this);

        setupUI(DCSession.getInstance().getUser());

        return view;
    }

    private void setupUI(DCUser user) {
        if (user != null) {
            sdvProfileAvatar.setImageURI(user.getAvatarUrl(getActivity()));
            tvProfileFullname.setText(user.getFullName());
            tvProfileEmail.setText(user.getEmail());
            //TODO: address, gender, age
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_profile_edit:
                //TODO: show edit fragment
                Snacky.builder().setActivty(getActivity()).error().setText("Not yet implemented").show();
                break;
        }
    }
}
