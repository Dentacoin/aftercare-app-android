package com.dentacoin.dentacare.utils;

import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCJourney;
import com.dentacoin.dentacare.model.DCRoutine;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public interface IDCDashboardObserver {
    void onJourneyUpdated(DCJourney journey);
    void onJourneyError(DCError error);
    void onDashboardUpdated(DCDashboard dashboard);
    void onDashboardError(DCError error);
    void onSyncNeeded(DCRoutine[] routines);
    void onSyncSuccess();
}
