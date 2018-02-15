package com.dentacoin.dentacare.utils;

import com.dentacoin.dentacare.model.DCRecord;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public interface IDCDashboardObserver {
    void onDashboardUpdated(DCDashboard dashboard);
    void onDashboardError(DCError error);
    void onSyncNeeded(DCRecord[] records);
    void onSyncSuccess();
}
