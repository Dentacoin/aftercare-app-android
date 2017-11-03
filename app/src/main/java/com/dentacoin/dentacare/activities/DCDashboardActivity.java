package com.dentacoin.dentacare.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.DCDashboardPagerAdapter;
import com.dentacoin.dentacare.fragments.DCBrushFragment;
import com.dentacoin.dentacare.fragments.DCFlossFragment;
import com.dentacoin.dentacare.fragments.DCGoalDialogFragment;
import com.dentacoin.dentacare.fragments.DCMessageFragment;
import com.dentacoin.dentacare.fragments.DCRinseFragment;
import com.dentacoin.dentacare.fragments.DCWelcomeFragment;
import com.dentacoin.dentacare.fragments.IDCFragmentInterface;
import com.dentacoin.dentacare.model.DCActivityRecord;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCDashboardDataProvider;
import com.dentacoin.dentacare.utils.DCGoalsDataProvider;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.utils.DCTutorialManager;
import com.dentacoin.dentacare.utils.IDCDashboardObserver;
import com.dentacoin.dentacare.utils.IDCGoalsObserver;
import com.dentacoin.dentacare.utils.IDCTutorial;
import com.dentacoin.dentacare.utils.Routine;
import com.dentacoin.dentacare.widgets.DCVIewPager;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.google.gson.JsonSyntaxException;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 8/10/17.
 */

public class DCDashboardActivity extends DCDrawerActivity implements IDCFragmentInterface, IDCDashboardObserver, IDCGoalsObserver, DCMessageFragment.IDCMessageFragmentListener, Routine.IRoutineListener {

    private TabLayout tlDashboardTabs;
    private DCVIewPager vpDashboardPager;
    private TickerView tvDashboardDcnTotal;
    private LinearLayout llDashboardDcnTotal;
    private View vSeparator;

    private DCDashboardPagerAdapter adapter;
    private boolean syncWarningVisible = false;
    private boolean inRecord = false;

    private IDCTutorial tutorialListener;
    private Routine routine;

    private DCFlossFragment floss;
    private DCBrushFragment brush;
    private DCRinseFragment rinse;

    public void setFloss(DCFlossFragment floss) {
        this.floss = floss;
    }

    public void setBrush(DCBrushFragment brush) {
        this.brush = brush;
    }

    public void setRinse(DCRinseFragment rinse) {
        this.rinse = rinse;
    }

    public void setTutorialListener(IDCTutorial tutorialListener) {
        this.tutorialListener = tutorialListener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_dashboard);

        setActionBarTitle(R.string.dashboard_hdl_dentacare);
        tvDashboardDcnTotal = (TickerView) findViewById(R.id.tv_dashboard_dcn_total);
        tvDashboardDcnTotal.setCharacterList(TickerUtils.getDefaultNumberList());

        tlDashboardTabs = (TabLayout) findViewById(R.id.tl_dashboard_tabs);
        vpDashboardPager = (DCVIewPager) findViewById(R.id.vp_dashboard_pager);
        llDashboardDcnTotal = (LinearLayout) findViewById(R.id.ll_dashboard_dcn_total);
        llDashboardDcnTotal.setOnClickListener(this);

        vSeparator = findViewById(R.id.v_separator);

        adapter = new DCDashboardPagerAdapter(getFragmentManager());
        vpDashboardPager.setAdapter(adapter);
        tlDashboardTabs.setupWithViewPager(vpDashboardPager);
        vpDashboardPager.setCurrentItem(1);
        vpDashboardPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                hideTutorials();
            }
        });

        if (!DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.WELCOME_SCREEN, false)) {
            toolbar.setVisibility(View.GONE);
            getFragmentManager().beginTransaction().add(R.id.container, new DCWelcomeFragment(), DCWelcomeFragment.TAG).commit();
        } else {
            showTutorials();
        }

        DCDashboardDataProvider.getInstance().updateDashboard(true);
    }

    @Override
    public void onFragmentRemoved() {
        toolbar.setVisibility(View.VISIBLE);
        showTutorials();
        showMessage(1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        DCDashboardDataProvider.getInstance().addObserver(this);
        DCDashboardDataProvider.getInstance().updateDashboard(true);
        DCGoalsDataProvider.getInstance().addObserver(this);
        DCGoalsDataProvider.getInstance().updateGoals(true);
        updateDaysCounter();
        showMessage(1000);
    }

    private void showMessage(long delay) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Fragment wellcomeFragment = getFragmentManager().findFragmentByTag(DCWelcomeFragment.TAG);
                Fragment goalFragment = getFragmentManager().findFragmentByTag(DCGoalDialogFragment.TAG);

                if ((wellcomeFragment != null && wellcomeFragment.isVisible()) ||
                    (goalFragment != null && goalFragment.isVisible()) || inRecord) {
                    return;
                }

                if (DCMessageFragment.shouldShowMessage()) {
                    DCMessageFragment.MESSAGE message = DCMessageFragment.getProperMessage();
                    if (message != null) {
                        DCMessageFragment messageFragment = DCMessageFragment.create(DCDashboardActivity.this, message);
                        messageFragment.show(getFragmentManager(), DCMessageFragment.TAG);
                        messageFragment.setListener(DCDashboardActivity.this);
                    }
                }
            }
        }, delay);
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
        onError(error, 3000);
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
        hideTutorials();
    }

    public void toggleRecordMode(boolean inRecord) {
        if (this.inRecord == inRecord && routine == null)
            return;

        this.inRecord = inRecord || routine != null;

        if (this.inRecord) {
            toolbar.setVisibility(View.GONE);
            tlDashboardTabs.setVisibility(View.GONE);
            llDashboardDcnTotal.setVisibility(View.GONE);
            vSeparator.setVisibility(View.GONE);
            vpDashboardPager.setSwipeEnabled(false);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            toolbar.setVisibility(View.VISIBLE);
            tlDashboardTabs.setVisibility(View.VISIBLE);
            llDashboardDcnTotal.setVisibility(View.VISIBLE);
            vSeparator.setVisibility(View.VISIBLE);
            vpDashboardPager.setSwipeEnabled(true);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        hideTutorials();
    }

    @Override
    public void onGoalsUpdated(ArrayList<DCGoal> goals) {

    }

    @Override
    public void onGoalAchieved(DCGoal goal) {
        Fragment fragment = getFragmentManager().findFragmentByTag(DCMessageFragment.TAG);
        if (routine != null || (fragment != null && fragment.isVisible())) {
            return;
        }

        DCGoalDialogFragment goalFragment = new DCGoalDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(DCGoalDialogFragment.KEY_GOAL, goal);
        goalFragment.setArguments(arguments);
        goalFragment.show(getFragmentManager(), DCGoalDialogFragment.TAG);
        hideTutorials();
    }

    @Override
    public void onGoalsError(DCError error) {
    }

    @Override
    public void showTutorials() {
        DCTutorialManager.getInstance().showTutorial(this, tvDashboardDcnTotal, DCTutorialManager.TUTORIAL.TOTAL_DCN, ViewTooltip.ALIGN.CENTER, ViewTooltip.Position.BOTTOM);
        if (tutorialListener != null) {
            tutorialListener.showTutorials();
        }
    }

    @Override
    public void hideTutorials() {
        DCTutorialManager.getInstance().hideTutorial(DCTutorialManager.TUTORIAL.TOTAL_DCN);
        if (tutorialListener != null) {
            tutorialListener.hideTutorials();
        }
    }

    @Override
    public void onStartRoutine(Routine routine) {
        this.routine = routine;

        if (routine != null) {
            routine.setListener(this);
            routine.start();
        }
    }

    private void showProperFragment(Routine.Action action) {
        if (action == null)
            return;

        switch (action) {
            case FLOSS_READY:
                vpDashboardPager.setCurrentItem(0);
                if (floss != null)
                    floss.onRoutineStart(routine);
                break;
            case BRUSH_READY:
                vpDashboardPager.setCurrentItem(1);
                if (brush != null)
                    brush.onRoutineStart(routine);
                break;
            case RINSE_READY:
                vpDashboardPager.setCurrentItem(2);
                if (rinse != null)
                    rinse.onRoutineStart(routine);
                break;
        }
    }

    @Override
    public void onRoutineStart(Routine routine) {
        Log.d(TAG, "onRoutineStart()");
        if (routine.getType() != null && routine.getType().getActions() != null && routine.getType().getActions().length > 0) {
            Routine.Action action = routine.getType().getActions()[0];
            showProperFragment(action);
        }
    }

    @Override
    public void onRoutineStep(final Routine routine, Routine.Action action) {
        Log.d(TAG, "onRoutineStep(): " + action.name());

        showProperFragment(action);

        if (brush != null)
            brush.onRoutineStep(routine, action);
        if (floss != null)
            floss.onRoutineStep(routine, action);
        if (rinse != null)
            rinse.onRoutineStep(routine, action);
    }

    @Override
    public void onRoutineEnd(Routine routine) {
        Log.d(TAG, "onRoutineEnd()");
        if (brush != null)
            brush.onRoutineEnd(routine);
        if (floss != null)
            floss.onRoutineEnd(routine);
        if (rinse != null)
            rinse.onRoutineEnd(routine);

        this.routine = null;

        vpDashboardPager.setCurrentItem(1);
    }

    private void updateDaysCounter() {
        int day = DCSharedPreferences.loadInt(DCSharedPreferences.DCSharedKey.DAYS_COUNTER);
        Calendar today = Calendar.getInstance();
        String json = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.LAST_DATE_ADDED_DAYS);
        if (json != null) {
            try {
                Date lastDate = DCApiManager.gson.fromJson(json, Date.class);
                Calendar then = Calendar.getInstance();
                then.setTime(lastDate);

                if (today.get(Calendar.YEAR) == then.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR)) {
                    return;
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return;
            }
        }

        day += 1;

        if (day > DCConstants.DAYS_OF_USE) {
            day = 1;
        }

        DCSharedPreferences.saveInt(DCSharedPreferences.DCSharedKey.DAYS_COUNTER, day);
        DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.LAST_DATE_ADDED_DAYS, DCApiManager.gson.toJson(today.getTime()));
    }
}
