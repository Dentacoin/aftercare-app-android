package com.dentacoin.dentacare.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCDashboardActivity;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.Routine;
import com.dentacoin.dentacare.utils.Voice;
import com.dentacoin.dentacare.widgets.DCSoundManager;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCFlossFragment extends DCDashboardFragment {

    @Override
    protected DCConstants.DCActivityType getType() {
        return DCConstants.DCActivityType.FLOSS;
    }

    @Override
    public void onDashboardUpdated(DCDashboard dashboard) {
        setItem(dashboard.getFlossed());
    }

    private boolean floss_1 = false;
    private boolean floss_2 = false;
    private boolean floss_3 = false;

    @Override
    protected void handleClockTick(long millisUntilFinished) {
        super.handleClockTick(millisUntilFinished);
        float t = (DCConstants.COUNTDOWN_MAX_AMOUNT - millisUntilFinished) / 1000.0f;
        if (t > 0 && t < 30 && !floss_1) {
            floss_1 = true;
            DCSoundManager.getInstance().playVoice(getActivity(),  Voice.FLOSS_STEP_1);
            setMessage(getString(R.string.message_floss_4));
        }
        else if(t > 30 && t < 40 && !floss_3) {
            floss_3 = true;
            setRecordButtonEnabled(true);
        }
        else if (t > 115 && !floss_2) {
            floss_2 = true;
            DCSoundManager.getInstance().playVoice(getActivity(), Voice.FLOSS_DONE);
            setMessage(getString(R.string.message_floss_6));
        }
    }

    @Override
    public void startRecording() {
        if (routine != null && Routine.Action.FLOSS_DONE.equals(routine.getAction()))
            return;

        setRecordButtonEnabled(routine == null);
        super.startRecording();
    }

    @Override
    protected void stopRecording() {
        super.stopRecording();
        floss_1 = false;
        floss_2 = false;
        floss_3 = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DCDashboardActivity) {
            ((DCDashboardActivity) context).setFloss(this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DCDashboardActivity) {
            ((DCDashboardActivity) activity).setFloss(this);
        }
    }

    @Override
    public void onRoutineStep(final Routine routine, Routine.Action action) {
        super.onRoutineStep(routine, action);
        switch (action) {
            case FLOSS_READY:
                switch (routine.getType()) {
                    case EVENING:
                        if (isAdded()) {
                            setMessage(getString(R.string.message_floss_1));
                            DCSoundManager.getInstance().playVoice(getActivity(), Voice.FLOSS_START);
                        }
                        break;
                }
                break;
            case FLOSS_DONE:
                DCSoundManager.getInstance().playVoice(getActivity(), Voice.FLOSS_DONE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (routine != null)
                            routine.next();
                    }
                }, 2000);
                break;
        }
    }
}
