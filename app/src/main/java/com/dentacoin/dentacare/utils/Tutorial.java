package com.dentacoin.dentacare.utils;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 20.02.18.
 */

public enum Tutorial {
    TOTAL_DCN(R.string.tutorial_txt_total_dcn),
    LAST_ACTIVITY_TIME(R.string.tutorial_txt_last_activity_time),
    LEFT_ACTIVITIES_COUNT(R.string.tutorial_txt_left_activities_count),
    DCN_EARNED(R.string.tutorial_txt_earned_dcn),
    EDIT_PROFILE(R.string.tutorial_txt_click_edit_profile),
    COLLECT_DCN(R.string.tutorial_txt_collect_dcn),
    WITHDRAWS(R.string.tutorial_txt_withdraws),
    GOALS(R.string.tutorial_txt_goals_menu),
    DASHBOARD_STATISTICS(R.string.tutorial_txt_dashboard_arrow),
    EMERGENCY_MENU(R.string.tutorial_txt_emergency_menu),
    QR_CODE(R.string.tutorial_txt_qr_code),
    EMERGENCY_TOOTH(R.string.tutorial_txt_sick_tooth);

    private int resourceId;
    public int getResourceId() { return resourceId; }

    Tutorial(int resourceId) { this.resourceId = resourceId; }
}