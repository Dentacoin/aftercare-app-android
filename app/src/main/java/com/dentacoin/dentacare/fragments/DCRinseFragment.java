package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCActivityRecord;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCDashboardDataProvider;
import com.dentacoin.dentacare.utils.DCGoalsDataProvider;
import com.dentacoin.dentacare.utils.DCUtils;
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
        if (trackingTime)
            return;

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
        trackingTime = false;

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
            DCSoundManager.getInstance().playVoice(getActivity(), DCSoundManager.VOICE.RINSE_EVENING_2);
            setMessage(getString(R.string.message_rinse_1));
            rinseShown1 = true;
        }
        else if (t > 15 && t < 20 && !rinseShown2) {
            DCSoundManager.getInstance().playVoice(getActivity(), DCSoundManager.VOICE.RINSE_EVENING_3);
            setMessage(getString(R.string.message_rinse_2));
            rinseShown2 = true;
        }
        else if (t < 1 && !rinseShown3) {
            DCSoundManager.getInstance().playVoice(getActivity(), DCSoundManager.VOICE.RINSE_EVENING_4);
            setMessage(getString(R.string.message_rinse_3));
            rinseShown3 = true;
        }
    }
}
