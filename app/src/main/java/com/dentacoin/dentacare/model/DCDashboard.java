package com.dentacoin.dentacare.model;

import com.dentacoin.dentacare.utils.DCConstants;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCDashboard {

    private int totalDCN;

    private DCDashboardItem brush;
    private DCDashboardItem flossed;
    private DCDashboardItem rinsed;

    public int getTotalDCN() { return totalDCN; }

    public DCDashboardItem getBrush() { return brush; }
    public DCDashboardItem getFlossed() { return flossed; }
    public DCDashboardItem getRinsed() { return rinsed; }

}