package com.dentacoin.dentacare.model;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCDashboard {

    private int earnedDCN;
    private int pendingDCN;

    private DCDashboardItem brush;
    private DCDashboardItem flossed;
    private DCDashboardItem rinsed;

    public int getTotalDCN() { return earnedDCN + pendingDCN; }
    public int getEarnedDCN() { return earnedDCN; }
    public int getPendingDCN() { return pendingDCN; }

    public DCDashboardItem getBrush() { return brush; }
    public DCDashboardItem getFlossed() { return flossed; }
    public DCDashboardItem getRinsed() { return rinsed; }

}