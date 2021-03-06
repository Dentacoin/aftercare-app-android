package com.dentacoin.dentacare.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.DCGoalsAdapter;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCFriend;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Atanas Chervarov on 22.05.18.
 */
public class DCFriendGoalsFragment extends DCFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_FRIEND = "KEY_FRIEND";
    private DCFriend friend;

    private SwipeRefreshLayout srlGoals;
    private LinearLayout llGoalsNoGoals;
    private DCGoalsAdapter adapter;
    private RecyclerView rvGoals;

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

        srlGoals = view.findViewById(R.id.srl_goals);
        srlGoals.setOnRefreshListener(this);
        rvGoals = view.findViewById(R.id.rv_goals);
        llGoalsNoGoals = view.findViewById(R.id.ll_goals_no_goals);
        adapter = new DCGoalsAdapter(getActivity(), null);
        rvGoals.setAdapter(adapter);
        rvGoals.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        getGoals();
        return view;
    }

    @Override
    public void onRefresh() {
        getGoals();
    }

    private void getGoals() {
        DCApiManager.getInstance().getFriendGoals(friend.getId(), new DCResponseListener<DCGoal[]>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);
                srlGoals.setRefreshing(false);
                llGoalsNoGoals.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onResponse(DCGoal[] object) {
                srlGoals.setRefreshing(false);
                ArrayList<DCGoal> goals = new ArrayList<>();
                if (object != null) {
                    goals.addAll(Arrays.asList(object));
                }
                adapter.setItems(goals);
                llGoalsNoGoals.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        });
    }
}
