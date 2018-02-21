package com.dentacoin.dentacare.model;

import com.dentacoin.dentacare.utils.DCConstants;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCDashboardItem {
    private int lastTime;
    private int left;

    DCDashboardPeriod daily;
    DCDashboardPeriod weekly;
    DCDashboardPeriod monthly;

    public int getLastTime() { return lastTime; }
    public int getLeft() { return left; }

    public DCDashboardPeriod getDaily() { return daily; }
    public DCDashboardPeriod getWeekly() { return weekly; }
    public DCDashboardPeriod getMonthly() { return monthly; }

    public DCDashboardPeriod getPeriod(DCConstants.DCStatisticsType type) {
        switch (type) {
            case DAILY:
                return daily;
            case WEEKLY:
                return weekly;
            default:
                return monthly;
        }
    }
}
