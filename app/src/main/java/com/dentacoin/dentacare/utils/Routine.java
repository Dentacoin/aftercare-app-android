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
        /** from 5 to 17*/
        MORNING(5, 12, new Action[] { Action.BRUSH_READY, Action.BRUSH, Action.BRUSH_DONE, Action.RINSE_READY, Action.RINSE, Action.RINSE_DONE}),                                                           //Morning routine from 2am to 11am
        /** from 17 to 02*/
        EVENING(17, 9, new Action[] { Action.FLOSS_READY, Action.FLOSS, Action.FLOSS_DONE, Action.BRUSH_READY, Action.BRUSH, Action.BRUSH_DONE, Action.RINSE_READY, Action.RINSE, Action.RINSE_DONE});     //Evening routine from 17pm to 24pm

        private int fromHourOfDay;
        private int addHours;
        private Action[] actions;

        Type(int fromHourOfDay, int addHours, Action[] actions) {
            this.fromHourOfDay = fromHourOfDay;
            this.addHours = addHours;
            this.actions = actions;
        }

        public boolean inTimeFrame(int hour) {
            Calendar target = Calendar.getInstance();
            target.set(Calendar.HOUR_OF_DAY, hour);
            target.set(Calendar.MINUTE, 0);
            target.set(Calendar.SECOND, 0);

            Calendar from = Calendar.getInstance();
            from.set(Calendar.HOUR_OF_DAY, fromHourOfDay);
            from.set(Calendar.MINUTE, 0);
            from.set(Calendar.SECOND, 0);

            Calendar to = Calendar.getInstance();
            to.set(Calendar.HOUR_OF_DAY, fromHourOfDay);
            to.set(Calendar.MINUTE, 0);
            to.set(Calendar.SECOND, 0);
            to.add(Calendar.HOUR_OF_DAY, addHours);

            return ((target.compareTo(from) >= 0) && (target.compareTo(to) <= 0));
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
