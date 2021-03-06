package com.dentacoin.dentacare.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCActivity;
import com.dentacoin.dentacare.activities.DCEditChildAccountActivity;
import com.dentacoin.dentacare.model.DCChild;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCFriend;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.network.response.DCAuthToken;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.drawee.view.SimpleDraweeView;


/**
 * Created by Atanas Chervarov on 22.05.18.
 */
public class DCFriendInfoFragment extends DCFragment {

    private static final String KEY_FRIEND = "KEY_FRIEND";

    public static Fragment create(DCFriend friend) {
        DCFriendInfoFragment fragment = new DCFriendInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_FRIEND, friend);
        fragment.setArguments(bundle);
        return fragment;
    }

    private DCFriend friend;

    private SimpleDraweeView sdvFriendAvatar;
    private DCTextView tvFriendFullname;
    private DCTextView tvFriendEmail;
    private DCTextView tvFriendAge;
    private DCTextView tvFriendLastActivity;
    private DCButton btnChildUseAccount;
    private DCButton btnChildEdit;
    private DCButton btnFriendDelete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_friend_info, container, false);
        if (getArguments() != null && getArguments().getSerializable(KEY_FRIEND) instanceof DCFriend) {
            friend = (DCFriend) getArguments().getSerializable(KEY_FRIEND);
        }

        sdvFriendAvatar = view.findViewById(R.id.sdv_friend_avatar);
        tvFriendFullname = view.findViewById(R.id.tv_friend_fullname);
        tvFriendEmail = view.findViewById(R.id.tv_friend_email);
        tvFriendAge = view.findViewById(R.id.tv_friend_age);
        tvFriendLastActivity = view.findViewById(R.id.tv_friend_last_activity);

        btnChildUseAccount = view.findViewById(R.id.btn_child_use_account);
        btnChildUseAccount.setVisibility(friend.isChild() ? View.VISIBLE : View.GONE);
        btnChildUseAccount.setOnClickListener(v -> onUseAccount());

        btnChildEdit = view.findViewById(R.id.btn_child_edit);
        btnChildEdit.setVisibility(friend.isChild() ? View.VISIBLE : View.GONE);
        btnChildEdit.setOnClickListener(v -> onEditAccount());

        btnFriendDelete = view.findViewById(R.id.btn_friend_delete);
        btnFriendDelete.setVisibility(friend.isChild() ? View.GONE : View.VISIBLE);
        btnFriendDelete.setOnClickListener(v -> onDeleteAccount());

        sdvFriendAvatar.setImageURI(friend.getAvatarUrl(getActivity()));

        if (friend.isChild()) {
            sdvFriendAvatar.getHierarchy().setPlaceholderImage(R.drawable.baseline_face_black_48);
        } else {
            sdvFriendAvatar.getHierarchy().setPlaceholderImage(R.drawable.welcome_avatar_holder);
        }

        tvFriendFullname.setText(friend.getFullName());

        tvFriendAge.setVisibility(View.GONE);
        if (friend.getAge() != null) {
            tvFriendAge.setText(getString(R.string.profile_txt_years_old, friend.getAge().toString()));
            tvFriendAge.setVisibility(View.VISIBLE);
        }

        tvFriendLastActivity.setText(R.string.txt_no_activity_yet);
        if (friend.getLastActivity() != null) {
            tvFriendLastActivity.setText(getString(R.string.txt_last_active_on, DCConstants.DATE_FORMAT_LAST_ACTIVITY.format(friend.getLastActivity())));
        }

        tvFriendEmail.setVisibility(View.GONE);
        if (friend.getEmail() != null) {
            tvFriendEmail.setText(friend.getEmail());
            tvFriendEmail.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void onUseAccount() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.child_hdl_use_account)
                .setMessage(R.string.child_txt_use_account)
                .setNegativeButton(R.string.txt_cancel, (dialog, i) -> {
                    dialog.dismiss();
                })
                .setPositiveButton(R.string.txt_ok, (dialog, i) -> {
                    DCLoadingFragment loadingFragment = showLoading();
                    DCApiManager.getInstance().loginAsChild(friend.getId(), new DCResponseListener<DCAuthToken>() {
                        @Override
                        public void onFailure(DCError error) {
                            loadingFragment.dismissAllowingStateLoss();
                            onError(error);
                        }

                        @Override
                        public void onResponse(DCAuthToken token) {
                            loadingFragment.dismissAllowingStateLoss();
                            if (token != null && token.isValid()) {
                                ((DCActivity) getActivity()).onAccountSwitch(token);
                            }
                        }
                    });
                })
                .create()
                .show();

    }

    private void onEditAccount() {
        DCChild child = new DCChild(friend.getId(), friend.getFirstname(), friend.getBirthyear());
        getActivity().startActivityForResult(DCEditChildAccountActivity.createIntent(getActivity(), child), DCActivity.REQUEST_CODE_EDIT_CHILD);
    }

    public void onDeleteAccount() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.friend_hdl_delete)
                .setPositiveButton(R.string.txt_ok, (dialog, i) -> {
                    DCLoadingFragment loadingFragment = showLoading();
                    DCApiManager.getInstance().deleteFriend(friend.getId(), new DCResponseListener<Void>() {
                        @Override
                        public void onFailure(DCError error) {
                            onError(error);
                            loadingFragment.dismissAllowingStateLoss();
                        }

                        @Override
                        public void onResponse(Void object) {
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                            loadingFragment.dismissAllowingStateLoss();
                        }
                    });
                })
                .setNegativeButton(R.string.txt_cancel, (dialog, i) -> {
                    dialog.dismiss();
                })
                .create()
                .show();

    }
}
