package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 28.01.18.
 */

public class DCTablLayout extends TabLayout {
    private ViewPager viewPager;

    public DCTablLayout(Context context) {
        super(context);
        init(null);
    }

    public DCTablLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(null);
    }


    private Drawable icon;
    private Drawable iconSelected;

    private void init(AttributeSet attrs) {
        icon = getContext().getResources().getDrawable(R.drawable.page_indicator);
        iconSelected = getContext().getResources().getDrawable(R.drawable.page_indicator_selected);

        addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabs();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabs();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                updateTabs();
            }
        });
    }

    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        super.setupWithViewPager(viewPager);
        this.viewPager = viewPager;
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.getAdapter().registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    setupLayout();
                }
            });
            setupLayout();
        }
    }

    private void setupLayout() {
        LinearLayout tabStrip = ((LinearLayout)getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        updateTabs();
    }

    private void updateTabs() {
        if (viewPager != null && viewPager.getAdapter() != null) {
            for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {
                Tab tab = getTabAt(i);

                if (tab != null) {
                    if (i == viewPager.getCurrentItem()) {
                        tab.setIcon(iconSelected);
                    } else {
                        tab.setIcon(icon);
                    }
                }
            }
        }
    }
}
