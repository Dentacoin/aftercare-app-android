package com.dentacoin.dentacare.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCDashboardActivity;
import com.dentacoin.dentacare.model.DCActivityRecord;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCDashboardDataProvider;
import com.dentacoin.dentacare.utils.DCGoalsDataProvider;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.utils.Routine;
import com.dentacoin.dentacare.utils.Voice;
import com.dentacoin.dentacare.widgets.DCSoundManager;

import java.util.Date;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCRinseFragment extends DCDashboardFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = super.onCreateView(inflater, container, savedInstance);
        timerDashboard.setSecondaryProgress(1000);
        return view;
    }

    @Override
    protected DCConstants.DCActivityType getType() {
        return DCConstants.DCActivityType.RINSE;
    }

    @Override
    public void onDashboardUpdated(DCDashboard dashboard) {
        setItem(dashboard.getRinsed());
    }

    @Override
    protected void startRecording() {
        if (trackingTime || (routine != null && Routine.Action.RINSE_DONE.equals(routine.getAction())))
            return;

        playMusic();
        nextStep();
        trackingTime = true;
        record = new DCActivityRecord();
        record.setType(getType());
        record.setStartTime(new Date());

        timer = new CountDownTimer(DCConstants.COUNTDOWN_MAX_AMOUNT_RINSE, 100) {
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

    @Override
    protected void stopRecording() {

        if (trackingTime) {
            DCSoundManager.getInstance().cancelSounds();
            nextStep();
        }

        trackingTime = false;
        routine = null;

        if (timer != null) {
            timer.cancel();
        }

        timer = null;

        if (record != null) {
            record.setEndTime(new Date());
            DCDashboardDataProvider.getInstance().addActivityRecord(record);

            if (record.getTime() >= 29) {
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

        rinseShown1 = false;
        rinseShown2 = false;
        rinseShown3 = false;

        updateView();

        if (routine == null)
            stopMusic();
    }

    private boolean rinseShown1 = false;
    private boolean rinseShown2 = false;
    private boolean rinseShown3 = false;

    @Override
    protected void handleClockTick(long millisUntilFinished) {
        float t = millisUntilFinished / 1000.0f;
        if (timerDashboard != null) {
            timerDashboard.setSecondaryProgress(Math.round(t * 33.33f));
            timerDashboard.setTimerDisplay(DCUtils.secondsToTime((int)t));
        }

        if (t > 20 && t < 30 && !rinseShown1) {
            DCSoundManager.getInstance().playVoice(getActivity(), Voice.RINSE_STEP_1);
            setMessage(getString(R.string.message_rinse_1));
            rinseShown1 = true;
        }
        else if (t > 15 && t < 20 && !rinseShown2) {
            DCSoundManager.getInstance().playVoice(getActivity(), Voice.RINSE_STEP_2);
            setMessage(getString(R.string.message_rinse_2));
            rinseShown2 = true;
        }
        else if (t < 1 && !rinseShown3) {
            DCSoundManager.getInstance().playVoice(getActivity(), Voice.RINSE_STOP);
            setMessage(getString(R.string.message_rinse_3));
            rinseShown3 = true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DCDashboardActivity) {
            ((DCDashboardActivity) context).setRinse(this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DCDashboardActivity) {
            ((DCDashboardActivity) activity).setRinse(this);
        }
    }

    @Override
    public void onRoutineStep(final Routine routine, Routine.Action action) {
        super.onRoutineStep(routine, action);
        switch (action) {
            case RINSE_READY:
                switch (routine.getType()) {
                    case MORNING:
                        setMessage(getString(R.string.message_morning_routine_3));
                        DCSoundManager.getInstance().playVoice(getActivity(), Voice.RINSE_MORNING_START);
                        break;
                    case EVENING:
                        setMessage(getString(R.string.message_evening_rinse_start));
                        DCSoundManager.getInstance().playVoice(getActivity(), Voice.RINSE_EVENING_START);
                        break;
                }
                break;
            case RINSE_DONE:
                DCRoutineCompletedFragment routineCompletedFragment = DCRoutineCompletedFragment.create(routine.getType());
                routineCompletedFragment.show(getFragmentManager(), DCRoutineCompletedFragment.TAG);
                routine.next();
                break;
        }
    }
}
