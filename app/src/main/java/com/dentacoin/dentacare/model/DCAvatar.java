package com.dentacoin.dentacare.model;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Atanas Chervarov on 8/6/17.
 */

public class DCAvatar {
    private String avatar_id;
    private String avatar_random_id;
    private String xhdpi_link;
    private String hdpi_link;
    private String mdpi_link;
    private String ldpi_link;

    public String getAvatarUrl(Context context) {
        if (context != null) {
            final int densityDpi = context.getResources().getDisplayMetrics().densityDpi;

            if ((densityDpi > DisplayMetrics.DENSITY_XXXHIGH ||
                    densityDpi > DisplayMetrics.DENSITY_XXHIGH ||
                    densityDpi > DisplayMetrics.DENSITY_XHIGH) && xhdpi_link != null) {
                return xhdpi_link;
            } else if (densityDpi > DisplayMetrics.DENSITY_HIGH && hdpi_link != null) {
                return hdpi_link;
            } else if (densityDpi > DisplayMetrics.DENSITY_MEDIUM && mdpi_link != null) {
                return mdpi_link;
            } else {
                return ldpi_link;
            }
        }
        return null;
    }
}