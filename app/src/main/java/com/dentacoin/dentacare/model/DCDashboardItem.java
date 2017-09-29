package com.dentacoin.dentacare.model;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCDashboardItem {
    private int lastTime;
    private int earned;
    private int left;

    DCDashboardPeriod daily;
    DCDashboardPeriod weekly;
    DCDashboardPeriod monthly;

    public int getLastTime() { return lastTime; }
    public int getEarned() { return earned; }
    public int getLeft() { return left; }

    public DCDashboardPeriod getDaily() { return daily; }
    public DCDashboardPeriod getWeekly() { return weekly; }
    public DCDashboardPeriod getMonthly() { return monthly; }
}
