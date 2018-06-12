package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCActivity;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.drawee.view.SimpleDraweeView;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 31.05.18.
 */
public class DCInvitationDialogFragment extends DCDialogFragment {

    private static final String KEY_USER = "KEY_USER";
    private static final String KEY_TOKEN = "KEY_TOKEN";

    public static DCInvitationDialogFragment create(String token, DCUser user) {
        DCInvitationDialogFragment fragment = new DCInvitationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TOKEN, token);
        bundle.putSerializable(KEY_USER, user);
        fragment.setArguments(bundle);
        return fragment;
    }

    private SimpleDraweeView sdvAvatar;
    private DCTextView tvName;
    private DCButton btnDecline;
    private DCButton btnAccept;

    private DCUser user;
    private String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_invitation, container);

        sdvAvatar = view.findViewById(R.id.sdv_avatar);
        sdvAvatar.getHierarchy().setPlaceholderImage(getResources().getDrawable(R.drawable.welcome_avatar_holder));

        tvName = view.findViewById(R.id.tv_name);
        btnDecline = view.findViewById(R.id.btn_decline);
        btnAccept = view.findViewById(R.id.btn_accept);

        btnAccept.setOnClickListener(v -> onAccept());
        btnDecline.setOnClickListener(v -> onDecline());

        if (getArguments() != null && getArguments().getSerializable(KEY_USER) instanceof DCUser) {
            user = (DCUser) getArguments().getSerializable(KEY_USER);
            token = getArguments().getString(KEY_TOKEN);
            if (user != null) {
                sdvAvatar.setImageURI(user.getAvatarUrl(getActivity()));
                tvName.setText(user.getFullName());
            }
        }

        return view;
    }

    private void onAccept() {
        if (token != null) {
            DCApiManager.getInstance().approveInvitation(token, new DCResponseListener<Void>() {
                @Override
                public void onFailure(DCError error) {
                    if (getActivity() instanceof DCActivity) {
                        ((DCActivity) getActivity()).onError(error);
                    }
                    dismiss();
                }

                @Override
                public void onResponse(Void object) {
                    Snacky.builder().setActivty(getActivity())
                            .success()
                            .setText(R.string.invites_txt_friend_request_success)
                            .setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                            .show();
                    dismiss();
                }
            });
        } else {
            if (getActivity() instanceof DCActivity) {
                ((DCActivity) getActivity()).onError(new DCError(R.string.error_txt_something_went_wrong));
            }
            dismiss();
        }
    }

    private void onDecline() {
        dismiss();
    }
}