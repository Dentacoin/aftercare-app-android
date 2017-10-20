package com.dentacoin.dentacare.fragments;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCUtils;
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

    @Override
    protected void handleClockTick(long millisUntilFinished) {
        super.handleClockTick(millisUntilFinished);
        float t = (DCConstants.COUNTDOWN_MAX_AMOUNT - millisUntilFinished) / 1000.0f;
        if (t > 0 && t < 30 && !floss_1) {
            floss_1 = true;
            DCSoundManager.getInstance().playVoice(getActivity(),  DCSoundManager.VOICE.FLOSS_EVENING_2);
            setMessage(getString(R.string.message_floss_4));
        }
        else if (t > 115 && !floss_2) {
            floss_2 = true;
            DCSoundManager.getInstance().playVoice(getActivity(), DCSoundManager.VOICE.BRUSH_EVENING_3);
            setMessage(getString(R.string.message_floss_6));
        }
    }

    @Override
    protected void stopRecording() {
        super.stopRecording();
        floss_1 = false;
        floss_2 = false;
    }
}
