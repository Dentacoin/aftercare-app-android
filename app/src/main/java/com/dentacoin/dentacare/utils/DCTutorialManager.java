package com.dentacoin.dentacare.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.dentacoin.dentacare.R;
import com.github.florent37.viewtooltip.ViewTooltip;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Atanas Chervarov on 10/12/17.
 */

public class DCTutorialManager {

    public enum TUTORIAL {
        TOTAL_DCN(R.string.tutorial_txt_total_dcn),
        LAST_ACTIVITY_TIME(R.string.tutorial_txt_last_activity_time),
        LEFT_ACTIVITIES_COUNT(R.string.tutorial_txt_left_activities_count),
        DCN_EARNED(R.string.tutorial_txt_earned_dcn),
        EDIT_PROFILE(R.string.tutorial_txt_click_edit_profile),
        COLLECT_DCN(R.string.tutorial_txt_collect_dcn),
        GOALS(R.string.tutorial_txt_goals_menu),
        DASHBOARD_STATISTICS(R.string.tutorial_txt_dashboard_arrow),
        EMERGENCY_MENU(R.string.tutorial_txt_emergency_menu),
        QR_CODE(R.string.tutorial_txt_qr_code),
        EMERGENCY_TOOTH(R.string.tutorial_txt_sick_tooth);

        private int resourceId;
        TUTORIAL(int resourceId) { this.resourceId = resourceId; }
        public int getResourceId() { return resourceId; }
    }

    private static DCTutorialManager instance;

    private Map<TUTORIAL, ViewTooltip.TooltipView> tutorials;

    public static synchronized DCTutorialManager getInstance() {
        if (instance == null)
            instance = new DCTutorialManager();

        return instance;
    }

    private DCTutorialManager() {
        tutorials = new HashMap<>();
    }

    public void showTutorial(Context context, View view, TUTORIAL tutorial, ViewTooltip.ALIGN align, ViewTooltip.Position position) {
        showTutorial(context, view, tutorial, align, position, null, null, 30, 20, 30, 30);
    }

    public void showTutorial(Context context, View view, final TUTORIAL tutorial, ViewTooltip.ALIGN align, ViewTooltip.Position position, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        showTutorial(context, view, tutorial, align, position, null, null, paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public void showTutorial(Context context, View view, final TUTORIAL tutorial, ViewTooltip.ALIGN align, ViewTooltip.Position position, final ViewTooltip.ListenerDisplay displayListener, final ViewTooltip.ListenerHide hideListener, int paddingLef, int paddingTop, int paddingRight, int paddingBottom) {
        if (view == null || tutorials.containsKey(tutorial) || DCSharedPreferences.getShownTutorials().contains(tutorial.name()))
            return;

        ViewTooltip.TooltipView tooltipView = ViewTooltip.on(view)
                .autoHide(false, 200)
                .clickToHide(true)
                .align(align)
                .position(position)
                .text(context.getString(tutorial.getResourceId()))
                .textColor(Color.WHITE)
                .color(context.getResources().getColor(R.color.pink))
                .corner(30)
                .padding(paddingLef, paddingTop, paddingRight, paddingBottom)
                .onDisplay(new ViewTooltip.ListenerDisplay() {
                    @Override
                    public void onDisplay(View view) {
                        if (displayListener != null)
                            displayListener.onDisplay(view);

                        DCSharedPreferences.setShownTutorial(tutorial, true);
                    }
                })
                .onHide(new ViewTooltip.ListenerHide() {
                    @Override
                    public void onHide(View view) {
                        if (hideListener != null)
                            hideListener.onHide(view);

                        tutorials.remove(tutorial);
                    }
                })
                .show();

        tutorials.put(tutorial, tooltipView);
    }

    public void hideTutorial(TUTORIAL tutorial) {
        if (tutorials.containsKey(tutorial)) {
            tutorials.get(tutorial).remove();
        }
    }

    public void clear() {
        if (tutorials != null) {
            for (ViewTooltip.TooltipView tutorial : tutorials.values()) {
                tutorial.remove();
            }
            tutorials.clear();
        }
    }
}