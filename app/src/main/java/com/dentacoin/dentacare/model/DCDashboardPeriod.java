package com.dentacoin.dentacare.model;

import com.dentacoin.dentacare.utils.DCConstants;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCDashboardPeriod {
    private int times;
    private int left;
    private int average;

    public int getTimes() { return times; }
    public int getLeft() { return left; }
    public int getAverage() { return average; }

    /**
     * Returns progress for given time period
     * @param type
     * @return  0 or 1000
     */
    public int getTimesProgress(DCConstants.DCStatisticsType type) {
        switch (type) {
            case DAILY:
                return Math.round(times * 333.33f);
            case WEEKLY:
                return Math.round (times * 47.61f);
            case MONTHLY:
                return Math.round(times * 11.11f);
        }
        return 0;
    }

    public int getLeftProgress(DCConstants.DCStatisticsType type) {
        switch (type) {
            case DAILY:
                return Math.round(left * 1.85f);
            case WEEKLY:
                return Math.round(left * 0.265f);
            case MONTHLY:
                return Math.round(left * 0.0618f);
        }
        return 0;
    }

    public int getAverageProgress(DCConstants.DCStatisticsType type) {
        return Math.round(average * 5.56f);
    }
}