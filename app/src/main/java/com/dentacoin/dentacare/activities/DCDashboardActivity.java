package com.dentacoin.dentacare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.DCDashboardPagerAdapter;
import com.dentacoin.dentacare.fragments.DCGoalDialogFragment;
import com.dentacoin.dentacare.fragments.DCWelcomeFragment;
import com.dentacoin.dentacare.fragments.IDCFragmentInterface;
import com.dentacoin.dentacare.model.DCActivityRecord;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.utils.DCDashboardDataProvider;
import com.dentacoin.dentacare.utils.DCGoalsDataProvider;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.utils.IDCDashboardObserver;
import com.dentacoin.dentacare.utils.IDCGoalsObserver;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.dentacoin.dentacare.widgets.DCVIewPager;

import java.util.ArrayList;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 8/10/17.
 */

public class DCDashboardActivity extends DCDrawerActivity implements IDCFragmentInterface, IDCDashboardObserver, IDCGoalsObserver {

    private TabLayout tlDashboardTabs;
    private DCVIewPager vpDashboardPager;
    private DCTextView tvDashboardDcnTotal;
    private LinearLayout llDashboardDcnTotal;

    private DCDashboardPagerAdapter adapter;
    private boolean syncWarningVisible = false;
    private boolean inRecord = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_dashboard);

        setActionBarTitle(R.string.dashboard_hdl_dentacare);

        if (!DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.WELCOME_SCREEN, false)) {
            toolbar.setVisibility(View.GONE);
            getFragmentManager().beginTransaction().add(R.id.container, new DCWelcomeFragment()).commit();
        }

        tvDashboardDcnTotal = (DCTextView) findViewById(R.id.tv_dashboard_dcn_total);
        tlDashboardTabs = (TabLayout) findViewById(R.id.tl_dashboard_tabs);
        vpDashboardPager = (DCVIewPager) findViewById(R.id.vp_dashboard_pager);
        llDashboardDcnTotal = (LinearLayout) findViewById(R.id.ll_dashboard_dcn_total);
        llDashboardDcnTotal.setOnClickListener(this);

        adapter = new DCDashboardPagerAdapter(getFragmentManager());
        vpDashboardPager.setAdapter(adapter);
        tlDashboardTabs.setupWithViewPager(vpDashboardPager);
        vpDashboardPager.setCurrentItem(1);
        DCDashboardDataProvider.getInstance().updateDashboard(true);
    }

    @Override
    public void onFragmentRemoved() {
        toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        DCDashboardDataProvider.getInstance().addObserver(this);
        DCDashboardDataProvider.getInstance().updateDashboard(true);
        DCGoalsDataProvider.getInstance().addObserver(this);
        DCGoalsDataProvider.getInstance().updateGoals(true);
    }

    @Override
    public void onPause() {
        DCDashboardDataProvider.getInstance().removeObserver(this);
        DCGoalsDataProvider.getInstance().removeObserver(this);
        super.onPause();
    }

    @Override
    public void onDashboardUpdated(DCDashboard dashboard) {
        tvDashboardDcnTotal.setText(getString(R.string.txt_dcn, dashboard.getTotalDCN()));
    }

    @Override
    public void onDashboardError(DCError error) {
        onError(error);
    }

    @Override
    public void onSyncNeeded(DCActivityRecord[] records) {
        if (records != null && !syncWarningVisible) {
            syncWarningVisible = true;
            Snacky.builder().setActivty(this).setText(R.string.dashboard_warning_sync_needed).warning().setAction(R.string.txt_yes, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DCDashboardDataProvider.getInstance().sync(false);
                }
            }).setDuration(Snacky.LENGTH_INDEFINITE).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    syncWarningVisible = false;
                }
            }).show();
        }
    }

    @Override
    public void onSyncSuccess() {
        Snacky.builder().setActivty(this).setText(R.string.dashboard_success_sync).success().show();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.ll_dashboard_dcn_total:
                if (!inRecord) {
                    final Intent collectIntent = new Intent(this, DCCollectActivity.class);
                    startActivity(collectIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
        }
    }

    public void toggleRecordMode(boolean inRecord) {
        this.inRecord = inRecord;
        if (inRecord) {
            toolbar.setVisibility(View.GONE);
            tlDashboardTabs.setVisibility(View.GONE);
            vpDashboardPager.setSwipeEnabled(false);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            toolbar.setVisibility(View.VISIBLE);
            tlDashboardTabs.setVisibility(View.VISIBLE);
            vpDashboardPager.setSwipeEnabled(true);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onGoalsUpdated(ArrayList<DCGoal> goals) {
    }

    @Override
    public void onGoalAchieved(DCGoal goal) {
        DCGoalDialogFragment goalFragment = new DCGoalDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(DCGoalDialogFragment.KEY_GOAL, goal);
        goalFragment.setArguments(arguments);
        goalFragment.show(getFragmentManager(), DCGoalDialogFragment.TAG);
    }

    @Override
    public void onGoalsError(DCError error) {
    }
}
