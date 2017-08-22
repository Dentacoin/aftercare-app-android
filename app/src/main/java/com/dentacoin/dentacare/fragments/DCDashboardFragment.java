package com.dentacoin.dentacare.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 8/18/17.
 */

public class DCDashboardFragment extends DCFragment {

    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout llBottomStatistics;

    private ImageView ivDashboardDownArrow;
    private ImageView ivDashboardUpArrow;
    boolean reverseAnimStarted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.ll_bottom_statistics));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        ivDashboardDownArrow = (ImageView) view.findViewById(R.id.iv_dashboard_down_arrow);
        ivDashboardUpArrow = (ImageView) view.findViewById(R.id.iv_dashboard_up_arrow);
        llBottomStatistics = (LinearLayout) view.findViewById(R.id.ll_bottom_statistics);

        ivDashboardDownArrow.setAlpha(0.0f);
        ivDashboardUpArrow.setAlpha(1.0f);

        Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
        final TransitionDrawable drawableTransition = new TransitionDrawable(new Drawable[] { getResources().getDrawable(R.drawable.rectangle_gradient_blue), transparentDrawable });
        llBottomStatistics.setBackground(drawableTransition);
        drawableTransition.setCrossFadeEnabled(true);
        drawableTransition.startTransition(0);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        drawableTransition.startTransition(50);
                        reverseAnimStarted = false;
                        break;
                    default:
                        if (!reverseAnimStarted) {
                            reverseAnimStarted = true;
                            drawableTransition.reverseTransition(250);
                        }
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                ivDashboardUpArrow.setAlpha(1 - slideOffset);
                ivDashboardDownArrow.setAlpha(slideOffset);
                ivDashboardUpArrow.setScaleY((-2 * slideOffset) + 1);
                ivDashboardDownArrow.setScaleY(-1 * ((-2 * slideOffset) + 1));
            }
        });
        return view;
    }
}
