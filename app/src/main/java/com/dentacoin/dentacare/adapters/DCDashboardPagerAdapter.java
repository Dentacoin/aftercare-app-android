package com.dentacoin.dentacare.adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCBrushFragment;
import com.dentacoin.dentacare.fragments.DCFlossFragment;
import com.dentacoin.dentacare.fragments.DCRinseFragment;

/**
 * Created by Atanas Chervarov on 8/18/17.
 */

public class DCDashboardPagerAdapter extends FragmentPagerAdapter {

    private static final int DASHBOARDS_COUNT = 3;
    private String flossTitle;
    private String brushTitle;
    private String rinseTitle;

    public DCDashboardPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        flossTitle = context.getString(R.string.dashboard_tab_floss);
        brushTitle = context.getString(R.string.dashboard_tab_brush);
        rinseTitle = context.getString(R.string.dashboard_tab_rinse);
    }

    @Override
    public int getCount() {
        return DASHBOARDS_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DCBrushFragment();
            case 1:
                return new DCFlossFragment();
            case 2:
                return new DCRinseFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return brushTitle;
            case 1:
                return flossTitle;
            case 2:
                return rinseTitle;
        }
        return null;
    }
}
