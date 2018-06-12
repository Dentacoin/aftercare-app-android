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
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCDashboardActivity;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCDashboardItem;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCJourney;
import com.dentacoin.dentacare.model.DCRecord;
import com.dentacoin.dentacare.model.DCRoutine;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCDashboardDataProvider;
import com.dentacoin.dentacare.utils.DCGoalsDataProvider;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.utils.DCTutorialManager;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.utils.IDCDashboardObserver;
import com.dentacoin.dentacare.utils.IDCTutorial;
import com.dentacoin.dentacare.utils.Music;
import com.dentacoin.dentacare.utils.Routine;
import com.dentacoin.dentacare.utils.Tutorial;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCDashboardTeeth;
import com.dentacoin.dentacare.widgets.DCSoundManager;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.dentacoin.dentacare.widgets.DCTimerView;

import java.util.Date;

import de.mateware.snacky.Snacky;

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
    private DCJourney journey;

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

    private LinearLayout llDashboardMusicControls;
    private DCButton btnDashboardSkipPrev;
    private DCButton btnDashboardPlay;
    private DCButton btnDashboardPause;
    private DCButton btnDashboardSkipNext;

    protected boolean trackingTime = false;
    protected Routine routine;

    protected CountDownTimer timer;
    protected DCRecord record;
    private LinearLayout llDashboardStatistics;
    private DCTextView tvDashboardMessageContainer;
    float dashboardHolderPadding;
    private CoordinatorLayout.LayoutParams dashboardParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private float tHeight;
    private float tWidth;

    private Animation scaleDownAnimation;
    private Animation scaleUpAnimation;
    private AnimationSet btnAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scaleDownAnimation = new ScaleAnimation(1.0f, 0.75f, 1.0f, 0.75f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleDownAnimation.setDuration(50);

        scaleUpAnimation = new ScaleAnimation(0.75f, 1.0f, 0.75f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUpAnimation.setDuration(50);
        scaleUpAnimation.setStartOffset(50);

        btnAnimation = new AnimationSet(true);
        btnAnimation.addAnimation(scaleDownAnimation);
        btnAnimation.addAnimation(scaleUpAnimation);
    }

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
        timerDashboardleft.setTitle(getString(R.string.dashboard_lbl_routines_left));
        timerDashboardleft.setTimerDisplay(Integer.toString(2));
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

        llDashboardMusicControls = view.findViewById(R.id.ll_dashboard_music_controls);
        btnDashboardSkipPrev = view.findViewById(R.id.btn_dashboard_skip_prev);
        btnDashboardPlay = view.findViewById(R.id.btn_dashboard_play);
        btnDashboardPause = view.findViewById(R.id.btn_dashboard_pause);
        btnDashboardSkipNext = view.findViewById(R.id.btn_dashboard_skip_next);
        llDashboardMusicControls.setVisibility(View.GONE);

        btnDashboardSkipPrev.setOnClickListener(v -> onMusicSkipPrev());
        btnDashboardSkipNext.setOnClickListener(v -> onMusicSkipNext());
        btnDashboardPlay.setOnClickListener(v -> onMusicPlay());
        btnDashboardPause.setOnClickListener(v -> onMusicPause());


        final Resources r = getResources();
        dashboardHolderPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, r.getDisplayMetrics());

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
        tWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 237.1f, getResources().getDisplayMetrics());

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
            if (rlTimerHolder.getHeight() != 0) {
                float containerHeight = (float) rlTimerHolder.getHeight();
                 if (containerHeight != 0) {
                     float scaleY = 1.0f;
                     float scaleX = 1.0f;

                     if (containerHeight > tHeight) {
                         scaleY = containerHeight / tHeight;
                         scaleX = scaleY;
                     } else if (containerHeight < tHeight) {
                         scaleY = 1.0f;
                         scaleX = (containerHeight / tHeight);
                     }

                     if (dtDashboardTeeth.getScaleX() != scaleX || dtDashboardTeeth.getScaleY() != scaleY) {
                         dtDashboardTeeth.setScaleY(scaleY);
                         dtDashboardTeeth.setScaleX(scaleX);
                         dtDashboardTeeth.invalidate();
                     }
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

        if (journey != null) {
            timerDashboardEarned.setTimerDisplay(Integer.toString(journey.getDay()) + "/" + Integer.toString(journey.getTargetDays()));
            timerDashboardleft.setTimerDisplay(Integer.toString(journey.getRoutinesLeftForToday()));
        }

        if (dashboardItem != null) {
            timerDashboardLast.setTimerDisplay(DCUtils.secondsToTime(dashboardItem.getLastTime()));
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
                    btnDashboardRecord.setText(getString(R.string.dashboard_btn_start_brush));
                    tvDashboardStatisticsTitle.setText(getString(R.string.dashboard_lbl_brush_statistics));
                    timerDashboardTimes.setTitle(getString(R.string.dashboard_lbl_times_brushed));
                    break;
                case RINSE:
                    timerDashboardLast.setTitle(getString(R.string.dashboard_lbl_last_rinse));
                    btnDashboardRecord.setText(getString(R.string.dashboard_btn_start_rinse));
                    tvDashboardStatisticsTitle.setText(getString(R.string.dashboard_lbl_rinse_statistics));
                    timerDashboardTimes.setTitle(getString(R.string.dashboard_lbl_times_rinsed));
                    break;
                default:
                    timerDashboardLast.setTitle(getString(R.string.dashboard_lbl_last_floss));
                    btnDashboardRecord.setText(getString(R.string.dashboard_btn_start_floss));
                    tvDashboardStatisticsTitle.setText(getString(R.string.dashboard_lbl_floss_statistics));
                    timerDashboardTimes.setTitle(getString(R.string.dashboard_lbl_times_flossed));
                    break;
            }

            if (journey != null && journey.canStartRoutine()) {
                Routine.Type routineType = Routine.getAppropriateRoutineTypeForNow();
                if (routineType != null) {
                    switch (routineType) {
                        case EVENING:
                            btnDashboardRecord.setText(getString(R.string.btn_evening));
                            break;
                        case MORNING:
                            btnDashboardRecord.setText(getString(R.string.btn_morning));
                            break;
                    }
                }
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
                toggleMusicControls(true);
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
                toggleMusicControls(false);
            }

            llDashboardHolder.setLayoutParams(dashboardParams);
        }
    }

    protected void toggleMusicControls(boolean visible) {
        if (visible && DCSoundManager.getInstance().isMusicEnabled()) {
            llDashboardMusicControls.setVisibility(View.VISIBLE);

            if (DCSoundManager.getInstance().isMusicPlaying()) {
                btnDashboardPause.setVisibility(View.VISIBLE);
                btnDashboardPlay.setVisibility(View.GONE);
            } else {
                btnDashboardPlay.setVisibility(View.VISIBLE);
                btnDashboardPause.setVisibility(View.GONE);
            }
        } else {
            llDashboardMusicControls.setVisibility(View.GONE);
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
        DCDashboardDataProvider.getInstance().updateJourney(false);
        DCTutorialManager.getInstance().subscribe(this);
        resumeRecording();
    }

    @Override
    public void onPause() {
        DCDashboardDataProvider.getInstance().removeObserver(this);
        DCTutorialManager.getInstance().unsubscribe(this);
        pauseRecording();
        super.onPause();
    }

    protected void toggleRecording() {
        if (trackingTime)
            stopRecording();
        else if (routine == null && journey != null && journey.canStartRoutine()) {
            if (getActivity() instanceof DCDashboardActivity) {
                ((DCDashboardActivity) getActivity()).startRoutine(Routine.getAppropriateRoutineTypeForNow());
            }
        } else {
            startRecording();
        }
    }


    protected void startRecording() {
        if (trackingTime)
            return;

        playMusic();
        nextStep();
        trackingTime = true;
        record = new DCRecord();
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

        if (record != null) {
            record.setEndTime(new Date());

            if (routine != null)
                routine.addRecord(record);

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
    public void onJourneyUpdated(DCJourney journey) {
        this.journey = journey;
        updateView();
    }

    @Override
    public void onJourneyError(DCError error) {
    }

    @Override
    public void onDashboardError(DCError error) {
        //Override me
    }

    @Override
    public void onSyncNeeded(DCRoutine[] routines) {
        //Override me
    }

    @Override
    public void onSyncSuccess() {
        //Override me
    }

    @Override
    public void showTutorial(Tutorial tutorial) {
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
    public void onRoutineStep(final Routine routine, Routine.Action action) {
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
            DCSoundManager.getInstance().skipNext(getActivity());
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

    public void showEmailNotificaitonSent() {
        if (DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.SHOW_EMAIL_VERIFICATION, false) && DCSession.getInstance().getUser() != null) {
            DCSharedPreferences.saveBoolean(DCSharedPreferences.DCSharedKey.SHOW_EMAIL_VERIFICATION, false);
            Snacky.builder().setActivty(getActivity())
                    .success()
                    .setText(getString(R.string.signup_txt_verification_sent, DCSession.getInstance().getUser().getEmail()))
                    .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                    .show();
        }
    }


    private void onMusicSkipPrev() {
        btnDashboardSkipPrev.setScaleY(1.0f);
        btnDashboardSkipPrev.setScaleX(1.0f);
        btnDashboardSkipPrev.startAnimation(btnAnimation);
        DCSoundManager.getInstance().skipPrev(getActivity());
        updateView();
    }

    private void onMusicSkipNext() {
        btnDashboardSkipNext.setScaleX(1.0f);
        btnDashboardSkipNext.setScaleY(1.0f);
        btnDashboardSkipNext.startAnimation(btnAnimation);
        DCSoundManager.getInstance().skipNext(getActivity());
        updateView();
    }

    private void onMusicPlay() {
        btnDashboardPause.setScaleX(1.0f);
        btnDashboardPause.setScaleY(1.0f);
        btnDashboardPlay.setScaleX(1.0f);
        btnDashboardPlay.setScaleX(1.0f);
        btnDashboardPause.startAnimation(btnAnimation);
        btnDashboardPlay.startAnimation(btnAnimation);
        DCSoundManager.getInstance().resumeMusic();
        updateView();
    }

    private void onMusicPause() {
        btnDashboardPause.setScaleX(1.0f);
        btnDashboardPause.setScaleY(1.0f);
        btnDashboardPlay.setScaleX(1.0f);
        btnDashboardPlay.setScaleX(1.0f);
        btnDashboardPause.startAnimation(btnAnimation);
        btnDashboardPlay.startAnimation(btnAnimation);
        DCSoundManager.getInstance().pauseMusic();
        updateView();
    }
}
