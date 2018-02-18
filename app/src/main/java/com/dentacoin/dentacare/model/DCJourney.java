package com.dentacoin.dentacare.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Atanas Chervarov on 15.02.18.
 */

public class DCJourney implements Serializable {

    private Date startDate;
    private int targetDays;
    private int tolerance;
    private boolean completed;
    private int day;
    private int skipped;
    private DCRoutine lastRoutine;

    /** Retrieve the journey's start date */
    public Date getStartDate() { return startDate; }

    /** Get the journey's target day */
    public int getTargetDays() { return targetDays; }

    /** The amount of routines that can be skipped */
    public int getTolerance() { return tolerance; }

    /** If the routine has completed */
    public boolean isCompleted() { return completed; }

    /** Retrieve current day */
    public int getDay() { return day; }

    /** Retrieve amount of skipped routines */
    public int getSkipped() { return skipped; }

    public boolean isFailed() {
        return skipped > tolerance;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public DCRoutine getLastRoutine() { return lastRoutine; }


}
