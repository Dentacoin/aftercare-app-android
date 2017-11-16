package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCActivity;
import com.dentacoin.dentacare.activities.DCProfileActivity;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.drawee.view.SimpleDraweeView;

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

        loadUser();

        return view;
    }

    private void loadUser() {
        if (DCSession.getInstance().getUser() != null) {
            setupUI(DCSession.getInstance().getUser());
        } else {
            DCApiManager.getInstance().getUser(new DCResponseListener<DCUser>() {
                @Override
                public void onFailure(DCError error) {
                    onError(error);
                }

                @Override
                public void onResponse(DCUser object) {
                    setupUI(DCSession.getInstance().getUser());
                }
            });
        }
    }

    private void setupUI(DCUser user) {
        if (user != null) {
            sdvProfileAvatar.setImageURI(user.getAvatarUrl(getActivity()));
            tvProfileFullname.setText(user.getFullName());
            tvProfileEmail.setText(user.getEmail());

            tvProfileAddress.setVisibility(View.GONE);
            tvProfileAge.setVisibility(View.GONE);
            tvProfileGender.setVisibility(View.GONE);

            if (user.getFullAddress() != null) {
                tvProfileAddress.setText(user.getFullAddress());
                tvProfileAddress.setVisibility(View.VISIBLE);
            }

            if (user.getAge() != null) {
                tvProfileAge.setText(getString(R.string.profile_txt_years_old, Integer.toString(user.getAge())));
                tvProfileAge.setVisibility(View.VISIBLE);
            }

            if (user.getGender() != null) {
                tvProfileGender.setVisibility(View.VISIBLE);

                switch (user.getGender()) {
                    case DCUser.GENDER_MALE:
                        tvProfileGender.setText(R.string.gender_male);
                        break;
                    case DCUser.GENDER_FEMALE:
                        tvProfileGender.setText(R.string.gender_female);
                        break;
                    default:
                        tvProfileGender.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_profile_edit:
                ((DCProfileActivity) getActivity()).editProfile();
                break;
        }
    }
}
