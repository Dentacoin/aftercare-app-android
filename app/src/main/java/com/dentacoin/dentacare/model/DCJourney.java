package com.dentacoin.dentacare.model;

import com.dentacoin.dentacare.utils.Routine;

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
    public boolean isCompleted() {
        return completed && skipped <= tolerance;
    }

    /** Retrieve current day */
    public int getDay() {
        return Math.min(day, 90);
    }

    /** Retrieve amount of skipped routines */
    public int getSkipped() {
        return Math.max(0, skipped);
    }

    public boolean isFailed() {
        return skipped > tolerance;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public DCRoutine getLastRoutine() { return lastRoutine; }

    public boolean canStartRoutine() {
        if (lastRoutine == null)
            return true;

        Routine.Type appropriateType = Routine.getAppropriateRoutineTypeForNow();

        if (appropriateType != null && appropriateType != lastRoutine.getType())
            return true;
        else if (appropriateType != null && appropriateType == lastRoutine.getType() && !lastRoutine.wasToday())
            return true;

        return false;
    }

    public int getRoutinesLeftForToday() {
        if (lastRoutine != null) {
            if (lastRoutine.getType() == Routine.Type.MORNING && lastRoutine.wasToday()) {
                return 1;
            } else if (lastRoutine.getType() == Routine.Type.EVENING && lastRoutine.wasToday()) {
                return 0;
            }
        }
        return 2;
    }
}
