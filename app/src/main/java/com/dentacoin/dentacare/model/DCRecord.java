package com.dentacoin.dentacare.model;

import com.dentacoin.dentacare.utils.DCConstants;
import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCRecord {

    @Expose() private Date startTime;
    @Expose() private Date endTime;
    @Expose() private String type;

    public Date getStartTime() { return startTime; }
    public Date getEndTime() { return endTime; }

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
}
