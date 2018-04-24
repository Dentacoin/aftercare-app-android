package com.dentacoin.dentacare.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.DCDashboardPagerAdapter;
import com.dentacoin.dentacare.fragments.DCBrushFragment;
import com.dentacoin.dentacare.fragments.DCFlossFragment;
import com.dentacoin.dentacare.fragments.DCGoalDialogFragment;
import com.dentacoin.dentacare.fragments.DCMessageFragment;
import com.dentacoin.dentacare.fragments.DCRinseFragment;
import com.dentacoin.dentacare.fragments.DCRoutineCompletedFragment;
import com.dentacoin.dentacare.fragments.DCWelcomeFragment;
import com.dentacoin.dentacare.fragments.IDCFragmentInterface;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.model.DCJourney;
import com.dentacoin.dentacare.model.DCRoutine;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.utils.AudibleMessage;
import com.dentacoin.dentacare.utils.DCDashboardDataProvider;
import com.dentacoin.dentacare.utils.DCGoalsDataProvider;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.utils.DCTutorialManager;
import com.dentacoin.dentacare.utils.IDCDashboardObserver;
import com.dentacoin.dentacare.utils.IDCGoalsObserver;
import com.dentacoin.dentacare.utils.Routine;
import com.dentacoin.dentacare.utils.Tutorial;
import com.dentacoin.dentacare.widgets.DCVIewPager;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

import java.util.ArrayList;
import java.util.Calendar;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 8/10/17.
 */

public class DCDashboardActivity extends DCDrawerActivity implements IDCFragmentInterface, IDCDashboardObserver, IDCGoalsObserver, Routine.IRoutineListener {

    private TabLayout tlDashboardTabs;
    private DCVIewPager vpDashboardPager;
    private TickerView tvDashboardDcnTotal;
    private LinearLayout llDashboardDcnTotal;
    private View vSeparator;

    private DCDashboardPagerAdapter adapter;
    private boolean syncWarningVisible = false;
    private boolean inRecord = false;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_dashboard);

        setActionBarTitle(R.string.dashboard_hdl_dentacare);
        tvDashboardDcnTotal = findViewById(R.id.tv_dashboard_dcn_total);
        tvDashboardDcnTotal.setCharacterList(TickerUtils.getDefaultNumberList());

        tlDashboardTabs = findViewById(R.id.tl_dashboard_tabs);
        vpDashboardPager = findViewById(R.id.vp_dashboard_pager);
        llDashboardDcnTotal = findViewById(R.id.ll_dashboard_dcn_total);
        llDashboardDcnTotal.setOnClickListener(this);

        vSeparator = findViewById(R.id.v_separator);

        adapter = new DCDashboardPagerAdapter(this, getFragmentManager());
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
            }
        });

        if (!DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.WELCOME_SCREEN, false)) {
            toolbar.setVisibility(View.GONE);
            getFragmentManager().beginTransaction().add(R.id.container, new DCWelcomeFragment(), DCWelcomeFragment.TAG).commit();
        } else {
            DCTutorialManager.getInstance().showNext();
        }

        DCDashboardDataProvider.getInstance().updateDashboard(true);
    }

    @Override
    public void onFragmentRemoved() {
        toolbar.setVisibility(View.VISIBLE);
        DCTutorialManager.getInstance().showNext();
    }

    @Override
    public void onResume() {
        super.onResume();
        DCDashboardDataProvider.getInstance().addObserver(this);
        DCDashboardDataProvider.getInstance().updateDashboard(true);
        DCDashboardDataProvider.getInstance().updateJourney(true);

        DCGoalsDataProvider.getInstance().addObserver(this);
        DCGoalsDataProvider.getInstance().updateGoals(true);

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
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
    public void onSyncNeeded(DCRoutine[] routines) {
        if (routines != null && !syncWarningVisible) {
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
    }

    @Override
    public void onGoalsError(DCError error) {
    }

    @Override
    public void showTutorial(final Tutorial tutorial) {
        super.showTutorial(tutorial);

        if (tutorial != null) {
            switch (tutorial) {
                case TOTAL_DCN:
                    tvDashboardDcnTotal.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            tvDashboardDcnTotal.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int[] location = new int[2];
                            tvDashboardDcnTotal.getLocationInWindow(location);
                            float oneX = location[0] + tvDashboardDcnTotal.getWidth() / 2f;
                            float oneY = location[1] + tvDashboardDcnTotal.getHeight() / 2f;

                            SimpleTarget qrTarget = new SimpleTarget.Builder(DCDashboardActivity.this)
                                    .setPoint(oneX, oneY)
                                    .setRadius(120f) // radius of the Target
                                    .setDescription(getString(tutorial.getResourceId())) // description
                                    .build();

                            Spotlight.with(DCDashboardActivity.this)
                                    .setOverlayColor(getResources().getColor(R.color.blackTransparent80)) // background overlay color
                                    .setDuration(500L) // duration of Spotlight emerging and disappearing in ms
                                    .setAnimation(new DecelerateInterpolator(2f)) // animation of Spotlight
                                    .setTargets(qrTarget) // set targets. see below for more info
                                    .setOnSpotlightEndedListener(new OnSpotlightEndedListener() { // callback when Spotlight ends
                                        @Override
                                        public void onEnded() {
                                            DCTutorialManager.getInstance().showNext();
                                        }
                                    })
                                    .start(); // start Spotlight

                            DCSharedPreferences.setShownTutorial(Tutorial.TOTAL_DCN, true);
                        }
                    });
                    break;
            }
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


    @Override
    public void onJourneyUpdated(DCJourney journey) {
        if (journey != null) {
            if (journey.isCompleted()) {
                showCompletedJourneyPopup();
            } else if (journey.isFailed()) {
                showFailedJourneyPopup();
            } else if (journey.canStartRoutine()) {
                showDailyJourneyPopup(journey);
            }
        }
    }

    @Override
    public void onJourneyError(DCError error) {
        if (error != null && error.isType("not_started_yet")) {
            showStartJourneyPopup();
        }
    }

    private boolean canShowPopup() {
        Fragment messageFragment = getFragmentManager().findFragmentByTag(DCMessageFragment.TAG);
        Fragment welcomeFragment = getFragmentManager().findFragmentByTag(DCWelcomeFragment.TAG);
        Fragment goalFragment = getFragmentManager().findFragmentByTag(DCGoalDialogFragment.TAG);

        if ((messageFragment != null && messageFragment.isVisible()) ||
            (welcomeFragment != null && welcomeFragment.isVisible()) ||
            (goalFragment != null && goalFragment.isVisible()) || inRecord) {
            return false;
        }
        return true;
    }

    private boolean popupShown = false;
    private void showStartJourneyPopup() {
        if (canShowPopup() && !popupShown) {
            final Routine.Type type = Routine.getAppropriateRoutineTypeForNow();
            if (type != null) {
                popupShown = true;
                DCMessageFragment.create(
                        getString(R.string.journey_hdl_start),
                        getString(R.string.journey_sub_hdl_start),
                        getString(R.string.journey_txt_start),
                        getString(R.string.journey_btn_start),
                        null,
                        new DCMessageFragment.IDCMessageFragment() {
                            @Override
                            public void onButtonClicked() {
                                DCDashboardActivity.this.startRoutine(type);
                                popupShown = false;
                            }

                            @Override
                            public void onCancel() {
                                popupShown = false;
                            }
                        }).show(getFragmentManager(), DCMessageFragment.TAG);
            }
        }
    }

    private void showCompletedJourneyPopup() {
        if (canShowPopup() && !popupShown && DCDashboardDataProvider.getInstance().shouldShowCompletedJourneyPopup()) {
            popupShown = true;

            DCDashboardDataProvider.getInstance().setShownCompletedJourneyPopup();

            DCMessageFragment.create(
                    getString(R.string.journey_hdl_completed),
                    getString(R.string.journey_sub_hdl_completed),
                    getString(R.string.journey_txt_completed),
                    getString(R.string.journey_btn_completed),
                    null,
                    new DCMessageFragment.IDCMessageFragment() {
                        @Override
                        public void onButtonClicked() {
                            final Intent collectIntent = new Intent(DCDashboardActivity.this, DCCollectActivity.class);
                            startActivity(collectIntent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            popupShown = false;
                        }

                        @Override
                        public void onCancel() {
                            popupShown = false;
                        }
                    }
            ).show(getFragmentManager(), DCMessageFragment.TAG);
        }
    }

    private void showFailedJourneyPopup() {
        if (canShowPopup() && !popupShown) {
            final Routine.Type type = Routine.getAppropriateRoutineTypeForNow();
            if (type != null) {
                popupShown = true;
                DCMessageFragment.create(
                        getString(R.string.journey_hdl_failed),
                        getString(R.string.journey_sub_hdl_failed),
                        getString(R.string.journey_txt_failed),
                        getString(R.string.journey_btn_failed),
                        null,
                        new DCMessageFragment.IDCMessageFragment() {
                            @Override
                            public void onButtonClicked() {
                                DCDashboardActivity.this.startRoutine(type);
                                popupShown = false;
                            }

                            @Override
                            public void onCancel() {
                                popupShown = false;
                            }
                        }
                ).show(getFragmentManager(), DCMessageFragment.TAG);
            }
        }
    }

    private void showDailyJourneyPopup(DCJourney journey) {
        if (canShowPopup() && journey != null && !popupShown && DCDashboardDataProvider.getInstance().shouldShowPopup()) {
            String button = getString(R.string.btn_routine);

            final Routine.Type type = Routine.getAppropriateRoutineTypeForNow();
            if (type != null) {
                switch (type) {
                    case MORNING:
                        Calendar calendar = Calendar.getInstance();
                        if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
                            button = getString(R.string.btn_morning);
                        }
                        break;
                    case EVENING:
                        button = getString(R.string.btn_evening);
                        break;
                }

                AudibleMessage message = AudibleMessage.getAppropriateGreeting();
                String title = getString(R.string.journey_hdl_daily, Integer.toString(journey.getDay()), Integer.toString(journey.getTargetDays()));
                String subTitle = getString(R.string.journey_sub_hdl_daily, Integer.toString(journey.getSkipped()), Integer.toString(journey.getTolerance()));
                popupShown = true;

                DCDashboardDataProvider.getInstance().onJourneyPopupShown();

                DCMessageFragment.create(
                        title,
                        subTitle,
                        message.getMessage(this),
                        button,
                        message.getVoices(),
                        new DCMessageFragment.IDCMessageFragment() {
                            @Override
                            public void onButtonClicked() {
                                DCDashboardActivity.this.startRoutine(type);
                                popupShown = false;
                            }

                            @Override
                            public void onCancel() {
                                popupShown = false;
                            }
                        }
                ).show(getFragmentManager(), DCMessageFragment.TAG);
            }
        }
    }

    public void startRoutine(Routine.Type type) {
        if (type != null) {
            routine = new Routine(type);
            routine.setListener(this);
            routine.start();
        }
    }

    public void completeRoutine(Routine completedRoutine) {
        if (completedRoutine != null) {
            DCDashboardDataProvider.getInstance().addRoutine(completedRoutine.getRequestObject(), new DCResponseListener<DCRoutine>() {
                @Override
                public void onFailure(DCError error) {
                    onError(error);
                }

                @Override
                public void onResponse(DCRoutine object) {
                    if (object != null) {
                        DCRoutineCompletedFragment.create(object).show(getFragmentManager(), DCRoutineCompletedFragment.TAG);
                    }
                }
            });
        }
    }
}
