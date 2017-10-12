package com.dentacoin.dentacare.fragments;

import android.content.Context;

import com.dentacoin.dentacare.activities.DCDashboardActivity;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCTutorialManager;
import com.github.florent37.viewtooltip.ViewTooltip;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCBrushFragment extends DCDashboardFragment {

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
}
