package com.dentacoin.dentacare.model;

import com.dentacoin.dentacare.utils.DCConstants;

import java.util.Date;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCActivityRecord {

    private Date startTime;
    private Date endTime;
    private String type;
    private boolean reward = false;
    private Integer earnedDCN;

    public Date getStartTime() { return startTime; }
    public Date getEndTime() { return endTime; }

    public boolean isReward() {
        return reward;
    }

    public void setReward(boolean reward) {
        this.reward = reward;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(DCConstants.DCActivityType activityType) {
        switch (activityType) {
            case FLOSS:
                type = "flossed";
                break;
            case BRUSH:
                type = "brush";
                break;
            case RINSE:
                type = "rinsed";
                break;
        }
    }
    
    public int getEarnedDCN() {
        return earnedDCN != null ? earnedDCN : 0;
    }

    /**
     * Returns time from start to end in seconds
     * @return
     */
    public int getTime() {
        if (startTime != null && endTime != null) {
            return (int)((endTime.getTime() - startTime.getTime()) / 1000);
        }
        return 0;
    }

    public boolean equals(DCActivityRecord record) {
        if (record != null) {
            if (type != null && record.getType() != null && type.compareTo(record.getType()) == 0) {
                if (startTime != null && record.getStartTime() != null && startTime.compareTo(record.getStartTime()) == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
