package com.dentacoin.dentacare.fragments;

import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.utils.DCConstants;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCRinseFragment extends DCDashboardFragment {

    @Override
    protected DCConstants.DCActivityType getType() {
        return DCConstants.DCActivityType.RINSE;
    }

    @Override
    public void onDashboardUpdated(DCDashboard dashboard) {
        setItem(dashboard.getRinsed());
    }
}
