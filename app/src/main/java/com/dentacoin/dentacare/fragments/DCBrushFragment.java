package com.dentacoin.dentacare.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCDashboardActivity;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.utils.DCTutorialManager;
import com.dentacoin.dentacare.utils.Routine;
import com.dentacoin.dentacare.utils.Tutorial;
import com.dentacoin.dentacare.utils.Voice;
import com.dentacoin.dentacare.widgets.DCDashboardTeeth;
import com.dentacoin.dentacare.widgets.DCSoundManager;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCBrushFragment extends DCDashboardFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = super.onCreateView(inflater, container, savedInstance);
        dtDashboardTeeth.setVisibility(View.VISIBLE);
        timerDashboard.setProgressVisibility(View.GONE);
        timerDashboard.setTimerDisplayTextSize(R.dimen.timer_text_size_big_teeth);

        return view;
    }

    @Override
    protected DCConstants.DCActivityType getType() {
        return DCConstants.DCActivityType.BRUSH;
    }

    @Override
    public void onDashboardUpdated(DCDashboard dashboard) {
        setItem(dashboard.getBrush());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DCDashboardActivity) {
            ((DCDashboardActivity) context).setBrush(this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DCDashboardActivity) {
            ((DCDashboardActivity) activity).setBrush(this);
        }
    }

    private void showSpotlightTutorial(Tutorial tutorial, float x, float y) {
        SimpleTarget qrTarget = new SimpleTarget.Builder(getActivity())
                .setPoint(x, y)
                .setRadius(140f) // radius of the Target
                .setDescription(getString(tutorial.getResourceId())) // description
                .build();

        Spotlight.with(getActivity())
                .setOverlayColor(getResources().getColor(R.color.blackTransparent80)) // background overlay color
                .setDuration(1000L) // duration of Spotlight emerging and disappearing in ms
                .setAnimation(new DecelerateInterpolator(2f)) // animation of Spotlight
                .setTargets(qrTarget) // set targets. see below for more info
                .setOnSpotlightEndedListener(new OnSpotlightEndedListener() { // callback when Spotlight ends
                    @Override
                    public void onEnded() {
                        DCTutorialManager.getInstance().showNext();
                    }
                })
                .start(); // start Spotlight

        DCSharedPreferences.setShownTutorial(tutorial, true);
    }

    @Override
    public void showTutorial(final Tutorial tutorial) {
        super.showTutorial(tutorial);
        if (tutorial != null) {
            switch (tutorial) {
                case LAST_ACTIVITY_TIME:
                    timerDashboardLast.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            timerDashboardLast.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int[] location = new int[2];
                            timerDashboardLast.getLocationInWindow(location);
                            float oneX = location[0] + timerDashboardLast.getWidth() / 2f;
                            float oneY = location[1] + timerDashboardLast.getHeight() / 2f;
                            showSpotlightTutorial(tutorial, oneX, oneY);
                        }
                    });
                    break;
                case LEFT_ACTIVITIES_COUNT:
                    timerDashboardleft.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            timerDashboardleft.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int[] location = new int[2];
                            timerDashboardleft.getLocationInWindow(location);
                            float oneX = location[0] + timerDashboardleft.getWidth() / 2f;
                            float oneY = location[1] + timerDashboardleft.getHeight() / 2f;
                            showSpotlightTutorial(tutorial, oneX, oneY);
                        }
                    });
                    break;
                case DCN_EARNED:
                    timerDashboardEarned.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            timerDashboardEarned.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int[] location = new int[2];
                            timerDashboardEarned.getLocationInWindow(location);
                            float oneX = location[0] + timerDashboardEarned.getWidth() / 2f;
                            float oneY = location[1] + timerDashboardEarned.getHeight() / 2f;
                            showSpotlightTutorial(tutorial, oneX, oneY);
                        }
                    });
                    break;
                case DASHBOARD_STATISTICS:
                    rlDashboardArrowHolder.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            rlDashboardArrowHolder.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int[] location = new int[2];
                            rlDashboardArrowHolder.getLocationInWindow(location);
                            float oneX = location[0] + rlDashboardArrowHolder.getWidth() / 2f;
                            float oneY = location[1] + rlDashboardArrowHolder.getHeight() / 2f;
                            showSpotlightTutorial(tutorial, oneX, oneY);
                        }
                    });
                    break;
            }
        }
    }


    private boolean ulvisible;
    private boolean wlvisible;
    private boolean wrvisible;
    private boolean urvisible;
    private boolean pressStopWhenReady;

    @Override
    protected void handleClockTick(long millisUntilFinished) {
        super.handleClockTick(millisUntilFinished);
        float t = (DCConstants.COUNTDOWN_MAX_AMOUNT - millisUntilFinished) / 1000.0f;

        if (t > 0 && t < 30 && !ulvisible) {
            dtDashboardTeeth.fadeIn(DCDashboardTeeth.Quadrant.UL, getResources().getColor(R.color.lightBlueAlpha));
            ulvisible = true;
            DCSoundManager.getInstance().playVoice(getActivity(), Voice.BRUSH_STEP_1);
            setMessage(getString(R.string.message_brush_1));
        }
        else if (t > 30 && t < 60 && !wlvisible) {
            setRecordButtonEnabled(true);
            dtDashboardTeeth.fadeIn(DCDashboardTeeth.Quadrant.WL, getResources().getColor(R.color.lightBlueAlpha));
            wlvisible = true;
            dtDashboardTeeth.fadeOut(DCDashboardTeeth.Quadrant.UL);
            ulvisible = false;
            DCSoundManager.getInstance().playVoice(getActivity(), Voice.BRUSH_STEP_2);
            setMessage(getString(R.string.message_brush_2));
        }
        else if (t > 60 && t < 90 && !wrvisible) {
            dtDashboardTeeth.fadeIn(DCDashboardTeeth.Quadrant.WR, getResources().getColor(R.color.lightBlueAlpha));
            wrvisible = true;
            dtDashboardTeeth.fadeOut(DCDashboardTeeth.Quadrant.WL);
            wlvisible = false;
            DCSoundManager.getInstance().playVoice(getActivity(), Voice.BRUSH_STEP_3);
            setMessage(getString(R.string.message_brush_3));
        }
        else if (t > 90 && t < 120 && !urvisible) {
            dtDashboardTeeth.fadeIn(DCDashboardTeeth.Quadrant.UR, getResources().getColor(R.color.lightBlueAlpha));
            urvisible = true;
            dtDashboardTeeth.fadeOut(DCDashboardTeeth.Quadrant.WR);
            wrvisible = false;
            DCSoundManager.getInstance().playVoice(getActivity(), Voice.BRUSH_STEP_4);
            setMessage(getString(R.string.message_brush_4));
        } else if (t > 120 && !pressStopWhenReady) {
            hideAll();
            pressStopWhenReady = true;
            setMessage(getString(R.string.message_brush_press_stop_when_ready));
            DCSoundManager.getInstance().playVoice(getActivity(), Voice.BRUSH_STOP);
        }
    }

    @Override
    public void startRecording() {
        if (routine != null && Routine.Action.BRUSH_DONE.equals(routine.getAction()))
            return;

        setRecordButtonEnabled(routine == null);
        super.startRecording();
    }

    @Override
    protected void stopRecording() {
        super.stopRecording();
        hideAll();
        pressStopWhenReady = false;
    }

    private void hideAll() {
        if (ulvisible) {
            ulvisible = false;
            dtDashboardTeeth.fadeOut(DCDashboardTeeth.Quadrant.UL);
        }

        if (wlvisible) {
            wlvisible = false;
            dtDashboardTeeth.fadeOut(DCDashboardTeeth.Quadrant.WL);
        }

        if (wrvisible) {
            wrvisible = false;
            dtDashboardTeeth.fadeOut(DCDashboardTeeth.Quadrant.WR);
        }

        if (urvisible) {
            urvisible = false;
            dtDashboardTeeth.fadeOut(DCDashboardTeeth.Quadrant.UR);
        }
    }

    @Override
    public void onRoutineStep(final Routine routine, Routine.Action action) {
        super.onRoutineStep(routine, action);
        switch (action) {
            case BRUSH_READY:
                switch (routine.getType()) {
                    case MORNING:
                        if (isAdded()) {
                            setMessage(getString(R.string.message_morning_routine_2));
                            DCSoundManager.getInstance().playVoice(getActivity(), Voice.BRUSH_MORNING_START);
                        }
                        break;
                    case EVENING:
                        if (isAdded()) {
                            setMessage(getString(R.string.message_evening_brush_start));
                            DCSoundManager.getInstance().playVoice(getActivity(), Voice.BRUSH_EVENING_START);
                        }
                        break;
                }
                break;
            case BRUSH_DONE:
                DCSoundManager.getInstance().playVoice(getActivity(), Voice.BRUSH_DONE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (routine != null)
                            routine.next();
                    }
                }, 2700);
                break;
        }
    }
}
