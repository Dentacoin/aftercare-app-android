package com.dentacoin.dentacare.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCFriend;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;

/**
 * Created by Atanas Chervarov on 22.05.18.
 */
public class DCFriendGoalsFragment extends DCFragment {

    private static final String KEY_FRIEND = "KEY_FRIEND";
    private DCFriend friend;

    public static Fragment create(DCFriend friend) {
        DCFriendGoalsFragment fragment = new DCFriendGoalsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_FRIEND, friend);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_friend_goals, container, false);
        if (getArguments() != null && getArguments().getSerializable(KEY_FRIEND) instanceof DCFriend) {
            friend = (DCFriend) getArguments().getSerializable(KEY_FRIEND);
        }

        getGoals();
        return view;
    }

    private void getGoals() {
        DCApiManager.getInstance().getFriendGoals(friend.getId(), new DCResponseListener<DCGoal[]>() {
            @Override
            public void onFailure(DCError error) {
                Log.d(TAG, "test");
            }

            @Override
            public void onResponse(DCGoal[] object) {
                Log.d(TAG, "test");
            }
        });
    }
}
