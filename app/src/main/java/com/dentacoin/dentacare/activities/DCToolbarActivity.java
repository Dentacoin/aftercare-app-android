package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.widgets.DCTextView;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCToolbarActivity extends DCActivity {

    private Toolbar toolbar;
    private ActionBar actionBar;
    private FrameLayout container;
    private DCTextView actionbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        container = (FrameLayout) findViewById(R.id.container);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            View customActionbarView = View.inflate(this, R.layout.view_actionbar, null);
            actionBar.setCustomView(customActionbarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            actionbarTitle = (DCTextView) customActionbarView.findViewById(R.id.tv_actionbar_title);
        }
    }

    /**
     * Set the action bar title
     * @param resourceId
     */
    protected final void setActionBarTitle(int resourceId) {
        if (actionbarTitle != null)
            actionbarTitle.setText(resourceId);
    }

    /**
     * Inflates a given view and adds it to the container
     * @param resourceId
     */
    protected final void addContentView(int resourceId) {
        container.addView(View.inflate(this, resourceId, null));
    }
}
