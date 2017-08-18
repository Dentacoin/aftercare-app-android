package com.dentacoin.dentacare.adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.dentacoin.dentacare.fragments.DCDashboardFragment;

/**
 * Created by Atanas Chervarov on 8/18/17.
 */

public class DCDashboardPagerAdapter extends FragmentPagerAdapter {

    private static final int DASHBOARDS_COUNT = 3;

    public DCDashboardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return DASHBOARDS_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return new DCDashboardFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "FLOSS";
            case 1:
                return "BRUSH";
            case 2:
                return "RINSE";
        }
        return null;
    }
}
