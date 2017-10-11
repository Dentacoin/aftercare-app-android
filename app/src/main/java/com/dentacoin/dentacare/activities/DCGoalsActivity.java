package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.DCGoalsAdapter;
import com.dentacoin.dentacare.adapters.viewholders.DCGoalItemViewHolder;
import com.dentacoin.dentacare.fragments.DCGoalDialogFragment;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.utils.DCGoalsDataProvider;
import com.dentacoin.dentacare.utils.IDCGoalsObserver;

import java.util.ArrayList;

/**
 * Created by Atanas Chervarov on 10/3/17.
 */

public class DCGoalsActivity extends DCToolbarActivity implements SwipeRefreshLayout.OnRefreshListener, DCGoalItemViewHolder.IDCGoalItemListener, IDCGoalsObserver {

    private SwipeRefreshLayout srlGoals;
    private RecyclerView rvGoals;
    private LinearLayout llGoalsNoGoals;
    private DCGoalsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_goals);
        setActionBarTitle(R.string.goals_hdl_goals);

        srlGoals = (SwipeRefreshLayout) findViewById(R.id.srl_goals);
        srlGoals.setOnRefreshListener(this);
        rvGoals = (RecyclerView) findViewById(R.id.rv_goals);
        llGoalsNoGoals = (LinearLayout) findViewById(R.id.ll_goals_no_goals);

        adapter = new DCGoalsAdapter(this);
        rvGoals.setAdapter(adapter);
        rvGoals.setLayoutManager(new GridLayoutManager(this, 3));

        DCGoalsDataProvider.getInstance().updateGoals(true);
    }

    @Override
    public void onRefresh() {
        DCGoalsDataProvider.getInstance().updateGoals(true);
    }

    @Override
    public void onGoalItemClicked(DCGoal item) {
        DCGoalDialogFragment goalFragment = new DCGoalDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(DCGoalDialogFragment.KEY_REACHED_GOAL, item);
        goalFragment.setArguments(arguments);
        goalFragment.show(getFragmentManager(), DCGoalDialogFragment.TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        DCGoalsDataProvider.getInstance().addObserver(this);
    }

    @Override
    public void onPause() {
        DCGoalsDataProvider.getInstance().removeObserver(this);
        super.onPause();
    }

    @Override
    public void onGoalsUpdated(ArrayList<DCGoal> goals) {
        srlGoals.setRefreshing(false);
        adapter.setItems(goals);
        if (adapter.getItemCount() > 0) {
            llGoalsNoGoals.setVisibility(View.GONE);
        } else {
            llGoalsNoGoals.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGoalAchieved(DCGoal goal) {
        DCGoalDialogFragment goalFragment = new DCGoalDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(DCGoalDialogFragment.KEY_REACHED_GOAL, goal);
        goalFragment.setArguments(arguments);
        goalFragment.show(getFragmentManager(), DCGoalDialogFragment.TAG);
    }

    @Override
    public void onGoalsError(DCError error) {
        onError(error);
        srlGoals.setRefreshing(false);
    }
}