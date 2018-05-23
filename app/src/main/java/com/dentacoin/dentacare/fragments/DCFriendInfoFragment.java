package com.dentacoin.dentacare.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCFriend;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_friend_info, container, false);
        if (getArguments() != null && getArguments().getSerializable(KEY_FRIEND) instanceof DCFriend) {
            friend = (DCFriend) getArguments().getSerializable(KEY_FRIEND);
        }

        return view;
    }
}
