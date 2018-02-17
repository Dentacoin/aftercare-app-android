package com.dentacoin.dentacare.model;

import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.Routine;
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

    public DCRoutine(Routine.Type type) {
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

    public void setType(Routine.Type routineType) {
        switch (routineType) {
            case MORNING:
                this.type = "morning";
                break;
            case EVENING:
                type = "evening";
                break;
        }
    }

    public Routine.Type getType() {
        if (type != null) {
            if (type.equals("morning"))
                return Routine.Type.MORNING;
            else if (type.equals("evening"))
                return Routine.Type.EVENING;
        }
        //Defaults to morning
        return Routine.Type.MORNING;
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

    public boolean isValid() {
        if (type != null && records != null && records.length > 0) {
            int time = 0;
            for (int i = 0; i < records.length; i++) {
                time += records[i].getTime();
            }
            if (type.equals("morning")) {
                return time >= 60;
            } else if (type.equals("evening")) {
                return time >= 90;
            }
        }
        return false;
    }


}
