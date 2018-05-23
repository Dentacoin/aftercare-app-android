package com.dentacoin.dentacare.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCFriend;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;

/**
 * Created by Atanas Chervarov on 22.05.18.
 */
public class DCFriendStatisticsFragment extends DCFragment {

    private static final String KEY_FRIEND = "KEY_FRIEND";

    private DCFriend friend;

    public static Fragment create(DCFriend friend) {
        DCFriendStatisticsFragment fragment = new DCFriendStatisticsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_FRIEND, friend);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_friend_statistics, container, false);
        if (getArguments() != null && getArguments().getSerializable(KEY_FRIEND) instanceof DCFriend) {
            friend = (DCFriend) getArguments().getSerializable(KEY_FRIEND);
        }
        loadStatistics();
        return view;
    }

    private void loadStatistics() {
        DCApiManager.getInstance().getFriendStatistics(friend.getId(), new DCResponseListener<DCDashboard>() {
            @Override
            public void onFailure(DCError error) {
                Log.d(TAG, "test");
            }

            @Override
            public void onResponse(DCDashboard object) {
                Log.d(TAG, "test");
            }
        });
    }
}
