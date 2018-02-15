package com.dentacoin.dentacare.model;

import com.dentacoin.dentacare.utils.DCConstants;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Atanas Chervarov on 15.02.18.
 */

public class DCRoutine implements Serializable {

    @Expose() private Date startTime;
    @Expose() private Date endTime;
    @Expose() private String type;
    @Expose() private DCRecord[] records;

    @Expose(serialize = false, deserialize = true)
    private int earnedDCN;

    public DCRoutine(DCConstants.DCRoutineType type) {
        setType(type);
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getEarnedDCN() {
        return earnedDCN;
    }

    public void setType(DCConstants.DCRoutineType routineType) {
        switch (routineType) {
            case MORNING:
                this.type = "morning";
                break;
            case EVENING:
                type = "evening";
                break;
        }
    }

    public void addRecord(DCRecord record) {
        if (record != null) {
            ArrayList<DCRecord> recordsArray = new ArrayList<>();
            if (records != null) {
                recordsArray.addAll(Arrays.asList(records));
            }
            recordsArray.add(record);
            records = recordsArray.toArray(new DCRecord[recordsArray.size()]);
        }
    }
}
