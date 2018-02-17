package com.dentacoin.dentacare.utils;

import com.dentacoin.dentacare.model.DCRecord;
import com.dentacoin.dentacare.model.DCRoutine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Atanas Chervarov on 10/31/17.
 */

public class Routine {

    public interface IRoutineListener {
        void onRoutineStart(Routine routine);
        void onRoutineStep(Routine routine, Action action);
        void onRoutineEnd(Routine routine);
    }

    public enum Action {
        BRUSH_READY,
        BRUSH,
        BRUSH_DONE,
        RINSE_READY,
        RINSE,
        RINSE_DONE,
        FLOSS_READY,
        FLOSS,
        FLOSS_DONE
    }

    public enum Type {
        MORNING(5, 17, new Action[] { Action.BRUSH_READY, Action.BRUSH, Action.BRUSH_DONE, Action.RINSE_READY, Action.RINSE, Action.RINSE_DONE}),                                                           //Morning routine from 2am to 11am
        EVENING(17, 24, new Action[] { Action.FLOSS_READY, Action.FLOSS, Action.FLOSS_DONE, Action.BRUSH_READY, Action.BRUSH, Action.BRUSH_DONE, Action.RINSE_READY, Action.RINSE, Action.RINSE_DONE});     //Evening routine from 17pm to 24pm

        private int fromHourOfDay;
        private int toHourOfDay;
        private Action[] actions;

        Type(int fromHourOfDay, int toHourOfDay, Action[] actions) {
            this.fromHourOfDay = fromHourOfDay;
            this.toHourOfDay = toHourOfDay;
            this.actions = actions;
        }

        public boolean inTimeFrame(int hour) {
            return hour >= fromHourOfDay && hour <= toHourOfDay;
        }

        public Action[] getActions() {
            return actions;
        }
    }

    public static Type getAppropriateRoutineTypeForNow() {
        Type[] types = { Type.MORNING, Type.EVENING };

        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        for (Type type : types) {
            if (type.inTimeFrame(hourOfDay)) {
                return type;
            }
        }

        return null;
    }

    private ArrayList<Action> actions;
    private IRoutineListener listener;
    private Type type;
    private Action action;
    private int earned = 0;
    private DCRoutine requestObject;

    public DCRoutine getRequestObject() {
        return requestObject;
    }

    public Type getType() {
        return type;
    }

    public void setListener(IRoutineListener listener) {
        this.listener = listener;
    }

    public int getEarned() {
        return earned;
    }

    public void addToEarned(int value) {
        earned += value;
    }

    /**
     * Returns the current action of the routine
     * @return
     */
    public Action getAction() {
        return action;
    }

    public Routine(Type type) {
        this.type = type;
        actions = new ArrayList<>();
        requestObject = new DCRoutine(type);

        if (type.getActions() != null) {
            this.actions.addAll(Arrays.asList(type.getActions()));
        }
    }

    public Routine(Type type, Action[] actions) {
        this(type);
        if (actions != null) {
            this.actions.addAll(Arrays.asList(actions));
        }
    }

    public void start() {
        if (listener != null) {
            listener.onRoutineStart(this);
        }
        requestObject.setStartTime(new Date());
        next();
    }

    public void next() {
        requestObject.setEndTime(new Date());
        if (actions.size() == 0) {
            if (listener != null) {
                listener.onRoutineEnd(this);
                listener = null;
            }
            return;
        }

        action = actions.get(0);
        actions.remove(action);

        if (listener != null) {
            listener.onRoutineStep(this, action);
        }
    }

    public void addRecord(DCRecord record) {
        if (requestObject != null)
            requestObject.addRecord(record);
    }
}
