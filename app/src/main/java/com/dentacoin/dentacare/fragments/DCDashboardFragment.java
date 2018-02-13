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
import android.support.design.widget.CoordinatorLayout;
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
import com.dentacoin.dentacare.utils.Music;
import com.dentacoin.dentacare.utils.Routine;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCDashboardTeeth;
import com.dentacoin.dentacare.widgets.DCSoundManager;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.dentacoin.dentacare.widgets.DCTimerView;

import java.util.Date;

/**
 * Created by Atanas Chervarov on 8/18/17.
 */

public abstract class DCDashboardFragment extends DCFragment implements IDCDashboardObserver, View.OnClickListener, IDCTutorial, Routine.IRoutineListener, DCSoundManager.IDCSoundListener {
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
    private LinearLayout llDashboardHolder;

    protected boolean trackingTime = false;
    protected Routine routine;

    protected CountDownTimer timer;
    protected DCActivityRecord record;
    private LinearLayout llDashboardStatistics;
    private DCTextView tvDashboardMessageContainer;
    float dashboardHolderPadding;
    private CoordinatorLayout.LayoutParams dashboardParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    private float tHeight;
    private float tWidth;
    private float ratio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        llBottomStatistics = view.findViewById(R.id.ll_bottom_statistics);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomStatistics);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        ivDashboardDownArrow = view.findViewById(R.id.iv_dashboard_down_arrow);
        ivDashboardUpArrow = view.findViewById(R.id.iv_dashboard_up_arrow);
        timerDashboard = view.findViewById(R.id.timer_dashboard);
        timerDashboardLast = view.findViewById(R.id.timer_dashboard_last);
        timerDashboardleft = view.findViewById(R.id.timer_dashboard_left);
        timerDashboardEarned = view.findViewById(R.id.timer_dashboard_earned);
        btnDashboardRecord = view.findViewById(R.id.btn_dashboard_record);
        btnDashboardRecord.setOnClickListener(this);
        tvDashboardStatisticsTitle = view.findViewById(R.id.tv_dashboard_statistics_title);
        btnDashboardDaily = view.findViewById(R.id.btn_dashboard_daily);
        btnDashboardDaily.setOnClickListener(this);
        btnDashboardWeekly = view.findViewById(R.id.btn_dashboard_weekly);
        btnDashboardWeekly.setOnClickListener(this);
        btnDashboardMonthly = view.findViewById(R.id.btn_dashboard_monthly);
        btnDashboardMonthly.setOnClickListener(this);
        timerDashboardTimes = view.findViewById(R.id.timer_dashboard_times);
        timerDashboardTimeLeft = view.findViewById(R.id.timer_dashboard_time_left);
        timerDashboardAverageTime = view.findViewById(R.id.timer_dashboard_average_time);
        rlDashboardArrowHolder = view.findViewById(R.id.rl_dashboard_arrow_holder);
        rlDashboardArrowHolder.setOnClickListener(this);
        dtDashboardTeeth = view.findViewById(R.id.dt_dashboard_teeth);
        dtDashboardTeeth.setVisibility(View.GONE);
        llDashboardStatistics = view.findViewById(R.id.ll_dashboard_statistics);
        llDashboardStatistics.setVisibility(View.VISIBLE);
        tvDashboardMessageContainer = view.findViewById(R.id.tv_dashboard_message_container);
        tvDashboardMessageContainer.setVisibility(View.GONE);
        tvDashboardMessageContainer.setText("");
        rlTimerHolder = view.findViewById(R.id.rl_timer_holder);
        llDashboardHolder = view.findViewById(R.id.ll_dashboard_holder);

        final Resources r = getResources();
        dashboardHolderPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, r.getDisplayMetrics());

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

        tHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 375.4f, getResources().getDisplayMetrics());
        tWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 227.1f, getResources().getDisplayMetrics());

        ratio = tHeight / tWidth;

        rlTimerHolder.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                updateTeethView();
            }
        });

        DCSoundManager.getInstance().setListener(this);

        return view;
    }

    abstract protected DCConstants.DCActivityType getType();

    protected void setItem(DCDashboardItem dashboardItem) {
        if (dashboardItem != null) {
            this.dashboardItem = dashboardItem;
            updateView();
        }
    }

    protected void updateTeethView() {
        if (rlTimerHolder != null) {
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

                scaleY = DCUtils.round(scaleY, 3);
                scaleX = DCUtils.round((height * scaleY) / (ratio * width), 3);

                if (scaleY != dtDashboardTeeth.getScaleY() || scaleX != dtDashboardTeeth.getScaleX()) {
                    dtDashboardTeeth.setScaleY(scaleY);
                    dtDashboardTeeth.setScaleX((scaleX));
                    dtDashboardTeeth.invalidate();
                }
            }
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
        updateView();
    }

    protected void setRecordButtonEnabled(boolean enabled) {
        if (btnDashboardRecord != null) {
            btnDashboardRecord.setEnabled(enabled);
        }
    }

    protected void updateView() {
        if (!isAdded())
            return;

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

            if (trackingTime || routine != null) {

                if (trackingTime) {
                    btnDashboardRecord.setText(getString(R.string.dashboard_btn_stop));
                    btnDashboardRecord.setSelected(true);
                } else {
                    btnDashboardRecord.setText(getString(R.string.dashboard_btn_start));
                    btnDashboardRecord.setSelected(false);
                }

                dashboardParams.setMargins(0, 0, 0, 0);
                llBottomStatistics.setVisibility(View.GONE);
                llDashboardStatistics.setVisibility(View.GONE);
                tvDashboardMessageContainer.clearAnimation();
                tvDashboardMessageContainer.setVisibility(View.VISIBLE);
            } else {
                dashboardParams.setMargins(0, 0, 0, (int)dashboardHolderPadding);
                llBottomStatistics.setVisibility(View.VISIBLE);
                btnDashboardRecord.setSelected(false);
                timerDashboard.setSecondaryProgress(0);
                timerDashboard.setProgress(1000);
                timerDashboard.setTimerDisplay(DCUtils.secondsToTime(0));
                llDashboardStatistics.setVisibility(View.VISIBLE);
                tvDashboardMessageContainer.clearAnimation();
                tvDashboardMessageContainer.setVisibility(View.GONE);
            }

            llDashboardHolder.setLayoutParams(dashboardParams);
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
        resumeRecording();
    }

    @Override
    public void onPause() {
        DCDashboardDataProvider.getInstance().removeObserver(this);
        pauseRecording();
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

        playMusic();
        nextStep();
        trackingTime = true;
        record = new DCActivityRecord();
        record.setType(getType());
        record.setStartTime(new Date());

        timer = new CountDownTimer(DCConstants.COUNTDOWN_MAX_AMOUNT, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                handleClockTick(millisUntilFinished);
                DCDashboardFragment.this.milisUntilFinished = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                stopRecording();
            }
        };

        timer.start();
        updateView();
        toggleRecordView(trackingTime || routine != null);
    }

    protected void toggleRecordView(boolean inRecord) {
        if (getActivity() != null) {
            ((DCDashboardActivity)getActivity()).toggleRecordMode(inRecord);
        }
    }

    protected void stopRecording() {
        if (trackingTime) {
            DCSoundManager.getInstance().cancelSounds();
            nextStep();
        }

        trackingTime = false;

        if (timer != null) {
            timer.cancel();
        }

        timer = null;
        milisUntilFinished = 0;

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
        toggleRecordView(trackingTime || routine != null);

        if (routine == null)
            stopMusic();
    }

    protected boolean paused = false;
    protected long milisUntilFinished = 0;

    protected void pauseRecording() {
        if (trackingTime && !paused) {
            paused = true;
            timer.cancel();
            timer = null;
        }
    }

    protected void resumeRecording() {
        if (paused) {
            paused = false;
            timer = new CountDownTimer(milisUntilFinished, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    handleClockTick(millisUntilFinished);
                    DCDashboardFragment.this.milisUntilFinished = millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    stopRecording();
                }
            };
            timer.start();
            updateView();
        }
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

    public void nextStep() {
        if (routine != null)
            routine.next();
    }

    @Override
    public void onRoutineStart(Routine routine) {
        this.routine = routine;
        updateView();
        toggleRecordView(routine!=null);
        playMusic();
    }

    @Override
    public void onRoutineStep(Routine routine, Routine.Action action) {
        this.routine = routine;
        updateView();
    }

    @Override
    public void onRoutineEnd(Routine routine) {
        this.routine = null;
        updateView();
        toggleRecordView(trackingTime || routine != null);
        stopMusic();
    }

    public void playMusic() {
        if (getActivity() != null && !DCSoundManager.getInstance().isMusicPlaying()) {
            DCSoundManager.getInstance().playMusic(getActivity(), Music.getRandomSong());
        }
    }

    public void stopMusic() {
        DCSoundManager.getInstance().cancelMusic();
    }

    @Override
    public void onMusicEnded(Music music) {
        if (trackingTime || routine != null) {
            playMusic();
        }
    }
}
