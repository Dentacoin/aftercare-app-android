package com.dentacoin.dentacare.fragments;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCDashboardActivity;
import com.dentacoin.dentacare.model.DCActivityRecord;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCDashboardItem;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCDashboardDataProvider;
import com.dentacoin.dentacare.utils.DCGoalsDataProvider;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.utils.IDCDashboardObserver;
import com.dentacoin.dentacare.utils.IDCTutorial;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCDashboardTeeth;
import com.dentacoin.dentacare.widgets.DCSoundManager;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.dentacoin.dentacare.widgets.DCTimerView;

import java.util.Date;

/**
 * Created by Atanas Chervarov on 8/18/17.
 */

public abstract class DCDashboardFragment extends DCFragment implements IDCDashboardObserver, View.OnClickListener, IDCTutorial {
    private DCDashboardItem dashboardItem;

    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout llBottomStatistics;
    private ImageView ivDashboardDownArrow;
    private ImageView ivDashboardUpArrow;
    boolean reverseAnimStarted = false;

    protected DCTimerView timerDashboard;
    protected DCTimerView timerDashboardLast;
    protected DCTimerView timerDashboardleft;
    protected DCTimerView timerDashboardEarned;
    private DCButton btnDashboardRecord;
    private DCTextView tvDashboardStatisticsTitle;
    private DCButton btnDashboardDaily;
    private DCButton btnDashboardWeekly;
    private DCButton btnDashboardMonthly;
    private DCTimerView timerDashboardTimes;
    private DCTimerView timerDashboardTimeLeft;
    private DCTimerView timerDashboardAverageTime;
    protected RelativeLayout rlDashboardArrowHolder;
    protected DCDashboardTeeth dtDashboardTeeth;
    private DCConstants.DCStatisticsType selectedStatistics = DCConstants.DCStatisticsType.DAILY;
    private RelativeLayout rlTimerHolder;

    protected boolean trackingTime = false;
    protected CountDownTimer timer;
    protected DCActivityRecord record;
    private LinearLayout llDashboardStatistics;
    private DCTextView tvDashboardMessageContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.ll_bottom_statistics));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        ivDashboardDownArrow = (ImageView) view.findViewById(R.id.iv_dashboard_down_arrow);
        ivDashboardUpArrow = (ImageView) view.findViewById(R.id.iv_dashboard_up_arrow);
        llBottomStatistics = (LinearLayout) view.findViewById(R.id.ll_bottom_statistics);
        timerDashboard = (DCTimerView) view.findViewById(R.id.timer_dashboard);
        timerDashboardLast = (DCTimerView) view.findViewById(R.id.timer_dashboard_last);
        timerDashboardleft = (DCTimerView) view.findViewById(R.id.timer_dashboard_left);
        timerDashboardEarned = (DCTimerView) view.findViewById(R.id.timer_dashboard_earned);
        btnDashboardRecord = (DCButton) view.findViewById(R.id.btn_dashboard_record);
        btnDashboardRecord.setOnClickListener(this);
        tvDashboardStatisticsTitle = (DCTextView) view.findViewById(R.id.tv_dashboard_statistics_title);
        btnDashboardDaily = (DCButton) view.findViewById(R.id.btn_dashboard_daily);
        btnDashboardDaily.setOnClickListener(this);
        btnDashboardWeekly = (DCButton) view.findViewById(R.id.btn_dashboard_weekly);
        btnDashboardWeekly.setOnClickListener(this);
        btnDashboardMonthly = (DCButton) view.findViewById(R.id.btn_dashboard_monthly);
        btnDashboardMonthly.setOnClickListener(this);
        timerDashboardTimes = (DCTimerView) view.findViewById(R.id.timer_dashboard_times);
        timerDashboardTimeLeft = (DCTimerView) view.findViewById(R.id.timer_dashboard_time_left);
        timerDashboardAverageTime = (DCTimerView) view.findViewById(R.id.timer_dashboard_average_time);
        rlDashboardArrowHolder = (RelativeLayout) view.findViewById(R.id.rl_dashboard_arrow_holder);
        rlDashboardArrowHolder.setOnClickListener(this);
        dtDashboardTeeth = (DCDashboardTeeth) view.findViewById(R.id.dt_dashboard_teeth);
        dtDashboardTeeth.setVisibility(View.GONE);
        llDashboardStatistics = (LinearLayout) view.findViewById(R.id.ll_dashboard_statistics);
        llDashboardStatistics.setVisibility(View.VISIBLE);
        tvDashboardMessageContainer = (DCTextView) view.findViewById(R.id.tv_dashboard_message_container);
        tvDashboardMessageContainer.setVisibility(View.GONE);
        tvDashboardMessageContainer.setText("");
        rlTimerHolder = (RelativeLayout) view.findViewById(R.id.rl_timer_holder);

        final Resources r = getResources();
        final float tHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 375.4f, r.getDisplayMetrics());
        final float tWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 227.1f, r.getDisplayMetrics());
        final float coef = tHeight / tWidth;

        rlTimerHolder.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (rlTimerHolder.getHeight() != 0 && dtDashboardTeeth.getHeight() != 0) {
                    float scaleX = dtDashboardTeeth.getScaleX();
                    float scaleY = dtDashboardTeeth.getScaleY();

                    float pHeight = (float) rlTimerHolder.getHeight();
                    float pWidth = (float) rlTimerHolder.getWidth();
                    float height = (float) dtDashboardTeeth.getHeight();
                    float width = (float) dtDashboardTeeth.getWidth();
                    if (pHeight > height) {
                        scaleY = pHeight / height;
                    } else if (pHeight < height) {
                        scaleY = height / pHeight;
                    }

                    scaleY = DCUtils.round(scaleY, 2);
                    scaleX = DCUtils.round((height * scaleY) / (coef * width), 2);

                    if (scaleY != dtDashboardTeeth.getScaleY() ||scaleX != dtDashboardTeeth.getScaleX()) {
                        dtDashboardTeeth.setScaleY(scaleY);
                        dtDashboardTeeth.setScaleX((scaleX));
                        dtDashboardTeeth.invalidate();
                    }
                }
            }
        });

        ivDashboardDownArrow.setAlpha(0.0f);
        ivDashboardUpArrow.setAlpha(1.0f);

        Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
        final TransitionDrawable drawableTransition = new TransitionDrawable(new Drawable[] { getResources().getDrawable(R.drawable.rectangle_gradient_blue), transparentDrawable });

        reverseAnimStarted = false;
        llBottomStatistics.setBackground(drawableTransition);
        drawableTransition.setCrossFadeEnabled(true);
        drawableTransition.startTransition(0);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        drawableTransition.startTransition(50);
                        reverseAnimStarted = false;
                        break;
                    default:
                        if (!reverseAnimStarted) {
                            reverseAnimStarted = true;
                            drawableTransition.reverseTransition(250);
                        }
                        hideTutorials();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                ivDashboardUpArrow.setAlpha(1 - slideOffset);
                ivDashboardDownArrow.setAlpha(slideOffset);
                ivDashboardUpArrow.setScaleY((-2 * slideOffset) + 1);
                ivDashboardDownArrow.setScaleY(-1 * ((-2 * slideOffset) + 1));
            }
        });

        setSelectedStatistics(DCConstants.DCStatisticsType.DAILY);
        return view;
    }

    abstract protected DCConstants.DCActivityType getType();

    protected void setItem(DCDashboardItem dashboardItem) {
        if (dashboardItem != null) {
            this.dashboardItem = dashboardItem;
            updateView();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dashboard_daily:
                setSelectedStatistics(DCConstants.DCStatisticsType.DAILY);
                break;
            case R.id.btn_dashboard_weekly:
                setSelectedStatistics(DCConstants.DCStatisticsType.WEEKLY);
                break;
            case R.id.btn_dashboard_monthly:
                setSelectedStatistics(DCConstants.DCStatisticsType.MONTHLY);
                break;
            case R.id.btn_dashboard_record:
                toggleRecording();
                break;
            case R.id.rl_dashboard_arrow_holder:
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
        }
    }

    protected void setSelectedStatistics(DCConstants.DCStatisticsType type) {
        selectedStatistics = type;
        switch (selectedStatistics) {
            case WEEKLY:
                btnDashboardWeekly.setSelected(true);
                btnDashboardDaily.setSelected(false);
                btnDashboardMonthly.setSelected(false);
                break;
            case MONTHLY:
                btnDashboardWeekly.setSelected(false);
                btnDashboardDaily.setSelected(false);
                btnDashboardMonthly.setSelected(true);
                break;
            default:
                btnDashboardWeekly.setSelected(false);
                btnDashboardDaily.setSelected(true);
                btnDashboardMonthly.setSelected(false);
                break;
        }

        updateView();
    }



    protected void updateView() {
        if (dashboardItem != null) {
            timerDashboardLast.setTimerDisplay(DCUtils.secondsToTime(dashboardItem.getLastTime()));
            timerDashboardleft.setTimerDisplay(Integer.toString(dashboardItem.getLeft()));
            timerDashboardEarned.setTimerDisplay(Integer.toString(dashboardItem.getEarned()));

            switch (selectedStatistics) {
                case WEEKLY:
                    timerDashboardTimes.setTimerDisplay(Integer.toString(dashboardItem.getWeekly().getTimes()));
                    timerDashboardTimeLeft.setTimerDisplay(DCUtils.secondsToTime(dashboardItem.getWeekly().getLeft()));
                    timerDashboardAverageTime.setTimerDisplay(DCUtils.secondsToTime(dashboardItem.getWeekly().getAverage()));
                    break;
                case MONTHLY:
                    timerDashboardTimes.setTimerDisplay(Integer.toString(dashboardItem.getMonthly().getTimes()));
                    timerDashboardTimeLeft.setTimerDisplay(DCUtils.secondsToTime(dashboardItem.getMonthly().getLeft()));
                    timerDashboardAverageTime.setTimerDisplay(DCUtils.secondsToTime(dashboardItem.getMonthly().getAverage()));
                    break;
                default:
                    timerDashboardTimes.setTimerDisplay(Integer.toString(dashboardItem.getDaily().getTimes()));
                    timerDashboardTimeLeft.setTimerDisplay(DCUtils.secondsToTime(dashboardItem.getDaily().getLeft()));
                    timerDashboardAverageTime.setTimerDisplay(DCUtils.secondsToTime(dashboardItem.getDaily().getAverage()));
                    break;
            }

            switch (getType()) {
                case BRUSH:
                    timerDashboardLast.setTitle(getString(R.string.dashboard_lbl_last_brush));
                    timerDashboardleft.setTitle(getString(R.string.dashboard_lbl_brush_left));
                    btnDashboardRecord.setText(getString(R.string.dashboard_btn_start_brush));
                    tvDashboardStatisticsTitle.setText(getString(R.string.dashboard_lbl_brush_statistics));
                    timerDashboardTimes.setTitle(getString(R.string.dashboard_lbl_times_brushed));
                    break;
                case RINSE:
                    timerDashboardLast.setTitle(getString(R.string.dashboard_lbl_last_rinse));
                    timerDashboardleft.setTitle(getString(R.string.dashboard_lbl_rinse_left));
                    btnDashboardRecord.setText(getString(R.string.dashboard_btn_start_rinse));
                    tvDashboardStatisticsTitle.setText(getString(R.string.dashboard_lbl_rinse_statistics));
                    timerDashboardTimes.setTitle(getString(R.string.dashboard_lbl_times_rinsed));
                    break;
                default:
                    timerDashboardLast.setTitle(getString(R.string.dashboard_lbl_last_floss));
                    timerDashboardleft.setTitle(getString(R.string.dashboard_lbl_floss_left));
                    btnDashboardRecord.setText(getString(R.string.dashboard_btn_start_floss));
                    tvDashboardStatisticsTitle.setText(getString(R.string.dashboard_lbl_floss_statistics));
                    timerDashboardTimes.setTitle(getString(R.string.dashboard_lbl_times_flossed));
                    break;
            }

            if (trackingTime) {
                btnDashboardRecord.setText(getString(R.string.dashboard_btn_stop));
                btnDashboardRecord.setSelected(true);
                llBottomStatistics.setVisibility(View.GONE);
                llDashboardStatistics.setVisibility(View.GONE);

                tvDashboardMessageContainer.clearAnimation();
                tvDashboardMessageContainer.setVisibility(View.VISIBLE);
            } else {
                llBottomStatistics.setVisibility(View.VISIBLE);
                btnDashboardRecord.setSelected(false);
                timerDashboard.setSecondaryProgress(0);
                timerDashboard.setProgress(1000);
                timerDashboard.setTimerDisplay(DCUtils.secondsToTime(0));
                llDashboardStatistics.setVisibility(View.VISIBLE);

                tvDashboardMessageContainer.clearAnimation();
                tvDashboardMessageContainer.setVisibility(View.GONE);
            }
            
            ((DCDashboardActivity)getActivity()).toggleRecordMode(trackingTime);
        }
    }

    protected void setMessage(final String message) {
        AlphaAnimation disapearAlphaAnimation = new AlphaAnimation(1f, 0f);
        disapearAlphaAnimation.setDuration(250);
        final AlphaAnimation appearAlpaAnimation = new AlphaAnimation(0f, 1f);
        appearAlpaAnimation.setDuration(250);

        tvDashboardMessageContainer.clearAnimation();

        disapearAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (tvDashboardMessageContainer != null) {
                    tvDashboardMessageContainer.setText(message);
                    tvDashboardMessageContainer.startAnimation(appearAlpaAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        tvDashboardMessageContainer.startAnimation(disapearAlphaAnimation);
    }

    @Override
    public void onResume() {
        super.onResume();
        DCDashboardDataProvider.getInstance().addObserver(this);
        DCDashboardDataProvider.getInstance().updateDashboard(false);
    }

    @Override
    public void onPause() {
        DCDashboardDataProvider.getInstance().removeObserver(this);
        super.onPause();
    }

    protected void toggleRecording() {
        if (trackingTime)
            stopRecording();
        else
            startRecording();
    }



    protected void startRecording() {
        if (trackingTime)
            return;

        trackingTime = true;
        record = new DCActivityRecord();
        record.setType(getType());
        record.setStartTime(new Date());

        timer = new CountDownTimer(DCConstants.COUNTDOWN_MAX_AMOUNT, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                handleClockTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                stopRecording();
            }
        };

        timer.start();
        updateView();
    }

    protected void stopRecording() {
        DCSoundManager.getInstance().cancelSounds();

        trackingTime = false;

        if (timer != null) {
            timer.cancel();
        }

        timer = null;

        if (record != null) {
            record.setEndTime(new Date());
            DCDashboardDataProvider.getInstance().addActivityRecord(record);

            if (record.getTime() > 30) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DCGoalsDataProvider.getInstance().updateGoals(true);
                    }
                }, 1000);
            }
            record = null;
        }

        updateView();
    }

    protected void handleClockTick(long millisUntilFinished) {
        float t = (DCConstants.COUNTDOWN_MAX_AMOUNT - millisUntilFinished) / 1000.0f;
        if (timerDashboard != null) {
            timerDashboard.setSecondaryProgress(Math.round(t * 8.33f));
            timerDashboard.setTimerDisplay(DCUtils.secondsToTime((int)t));
        }
    }

    @Override
    public void onDashboardUpdated(DCDashboard dashboard) {
        //Override me
    }

    @Override
    public void onDashboardError(DCError error) {
        //Override me
    }

    @Override
    public void onSyncNeeded(DCActivityRecord[] records) {
        //Override me
    }

    @Override
    public void onSyncSuccess() {
        //Override me
    }

    @Override
    public void showTutorials() {
    }

    @Override
    public void hideTutorials() {
    }
}
