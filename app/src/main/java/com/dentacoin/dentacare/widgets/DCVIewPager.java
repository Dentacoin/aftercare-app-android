package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Atanas Chervarov on 9/29/17.
 */

public class DCVIewPager extends ViewPager {

    private boolean swipeEnabled = true;

    public DCVIewPager(Context context) {
        super(context);
        init();
    }

    public DCVIewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        this.swipeEnabled = swipeEnabled;
    }

    private void init() {
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (swipeEnabled) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (swipeEnabled) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }
}
