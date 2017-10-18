package com.dentacoin.dentacare.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCDashboardActivity;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCTutorialManager;
import com.dentacoin.dentacare.widgets.DCDashboardTeeth;
import com.dentacoin.dentacare.widgets.DCSoundManager;
import com.github.florent37.viewtooltip.ViewTooltip;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCBrushFragment extends DCDashboardFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = super.onCreateView(inflater, container, savedInstance);
        dtDashboardTeeth.setVisibility(View.VISIBLE);
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
            ((DCDashboardActivity) context).setTutorialListener(this);
        }
    }

    @Override
    public void showTutorials() {
        super.showTutorials();
        if (isAdded() && getActivity() != null) {
            DCTutorialManager.getInstance().showTutorial(getActivity(), timerDashboardLast, DCTutorialManager.TUTORIAL.LAST_ACTIVITY_TIME, ViewTooltip.ALIGN.CENTER, ViewTooltip.Position.TOP);
            DCTutorialManager.getInstance().showTutorial(getActivity(), timerDashboardleft, DCTutorialManager.TUTORIAL.LEFT_ACTIVITIES_COUNT, ViewTooltip.ALIGN.START, ViewTooltip.Position.TOP);
            DCTutorialManager.getInstance().showTutorial(getActivity(), timerDashboardEarned, DCTutorialManager.TUTORIAL.DCN_EARNED, ViewTooltip.ALIGN.CENTER, ViewTooltip.Position.BOTTOM);
            DCTutorialManager.getInstance().showTutorial(getActivity(), rlDashboardArrowHolder, DCTutorialManager.TUTORIAL.DASHBOARD_STATISTICS, ViewTooltip.ALIGN.CENTER, ViewTooltip.Position.TOP);
        }
    }

    @Override
    public void hideTutorials() {
        super.hideTutorials();
        DCTutorialManager.getInstance().hideTutorial(DCTutorialManager.TUTORIAL.LAST_ACTIVITY_TIME);
        DCTutorialManager.getInstance().hideTutorial(DCTutorialManager.TUTORIAL.LEFT_ACTIVITIES_COUNT);
        DCTutorialManager.getInstance().hideTutorial(DCTutorialManager.TUTORIAL.DCN_EARNED);
        DCTutorialManager.getInstance().hideTutorial(DCTutorialManager.TUTORIAL.DASHBOARD_STATISTICS);
    }

    private boolean ulvisible;
    private boolean wlvisible;
    private boolean wrvisible;
    private boolean urvisible;
    private boolean youAreDone;

    @Override
    protected void handleClockTick(long millisUntilFinished) {
        super.handleClockTick(millisUntilFinished);
        float t = (DCConstants.COUNTDOWN_MAX_AMOUNT - millisUntilFinished) / 1000.0f;
        youAreDone = false;

        if (t > 0 && t < 30 && !ulvisible) {
            dtDashboardTeeth.fadeIn(DCDashboardTeeth.Quadrant.UL, getResources().getColor(R.color.lightBlueAlpha));
            ulvisible = true;
            DCSoundManager.getInstance().playSound(getActivity(), DCSoundManager.SOUND.BRUSH_EVENING_2);
        }
        else if (t > 30 && t < 60 && !wlvisible) {
            dtDashboardTeeth.fadeIn(DCDashboardTeeth.Quadrant.WL, getResources().getColor(R.color.lightBlueAlpha));
            wlvisible = true;
            dtDashboardTeeth.fadeOut(DCDashboardTeeth.Quadrant.UL);
            ulvisible = false;
            DCSoundManager.getInstance().playSound(getActivity(), DCSoundManager.SOUND.BRUSH_EVENING_3);
        }
        else if (t > 60 && t < 90 && !wrvisible) {
            dtDashboardTeeth.fadeIn(DCDashboardTeeth.Quadrant.WR, getResources().getColor(R.color.lightBlueAlpha));
            wrvisible = true;
            dtDashboardTeeth.fadeOut(DCDashboardTeeth.Quadrant.WL);
            wlvisible = false;
            DCSoundManager.getInstance().playSound(getActivity(), DCSoundManager.SOUND.BRUSH_EVENING_4);
        }
        else if (t > 90 && t < 120 && !urvisible) {
            dtDashboardTeeth.fadeIn(DCDashboardTeeth.Quadrant.UR, getResources().getColor(R.color.lightBlueAlpha));
            urvisible = true;
            dtDashboardTeeth.fadeOut(DCDashboardTeeth.Quadrant.WR);
            wrvisible = false;
            DCSoundManager.getInstance().playSound(getActivity(), DCSoundManager.SOUND.BRUSH_EVENING_5);
            youAreDone = true;
        } else if (t > 120) {
            hideAll();
            youAreDone = true;
        }
    }

    @Override
    protected void stopRecording() {
        super.stopRecording();
        hideAll();

        if (youAreDone)
            DCSoundManager.getInstance().playSound(getActivity(), DCSoundManager.SOUND.BRUSH_EVENING_6);

        youAreDone = false;
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
}
