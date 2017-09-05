package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.DCDashboardPagerAdapter;
import com.dentacoin.dentacare.fragments.DCWelcomeFragment;
import com.dentacoin.dentacare.fragments.IDCFragmentInterface;
import com.dentacoin.dentacare.utils.DCSharedPreferences;

/**
 * Created by Atanas Chervarov on 8/10/17.
 */

public class DCDashboardActivity extends DCDrawerActivity implements IDCFragmentInterface {

    private TabLayout tlDashboardTabs;
    private ViewPager vpDashboardPager;
    private DCDashboardPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_dashboard);

        setActionBarTitle(R.string.dashboard_hdl_dentacare);

        if (!DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.WELCOME_SCREEN, false)) {
            toolbar.setVisibility(View.GONE);
            getFragmentManager().beginTransaction().add(R.id.container, new DCWelcomeFragment()).commit();
        }

        tlDashboardTabs = (TabLayout) findViewById(R.id.tl_dashboard_tabs);
        vpDashboardPager = (ViewPager) findViewById(R.id.vp_dashboard_pager);
        adapter = new DCDashboardPagerAdapter(getFragmentManager());
        vpDashboardPager.setAdapter(adapter);
        tlDashboardTabs.setupWithViewPager(vpDashboardPager);
    }

    @Override
    public void onFragmentRemoved() {
        toolbar.setVisibility(View.VISIBLE);
    }
}
