package com.dentacoin.dentacare.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import com.dentacoin.dentacare.utils.DCErrorType;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 8/7/17.
 */

public class DCError {
    private int code;
    private String[] errors;

    private DCErrorType type;
    private int resourceId = -1;

    public DCError (int resourceId) {
        this.code = 1001;
        this.resourceId = resourceId;
    }

    public DCError (String message) {
        this.code = 1001;
        this.errors = new String[] { message };
    }

    /**
     * Create error with a given error message
     * @param code
     * @param message
     */
    public DCError(int code, String message) {
        this.code = code;
        errors = new String[] { message };
    }

    /**
     * Create error with given type
     * @param type
     */
    public DCError(DCErrorType type) {
        this.code = type.getCode();
        this.type = type;
    }

    /**
     * Get error code
     * @return
     */
    public int getCode() {
        return code;
    }


    /**
     * Get error message
     * @param context
     * @return
     */
    public String getMessage(Context context) {
        if (errors != null && errors.length > 0) {
            return errors[0];
        }
        else if (type != null && type.getResource() != -1) {
            try {
                return context.getString(type.getResource());
            } catch (Resources.NotFoundException e) { }
        }
        else if (resourceId != -1) {
            try {
                return context.getString(resourceId);
            } catch (Resources.NotFoundException e) { }
        }
        return null;
    }

    public boolean show(Activity activity, int duration) {
        String message = getMessage(activity);
        if (message != null) {
            Snacky.builder().setActivty(activity).setText(message).error().setDuration(duration > 0 ? duration : Snacky.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * Shows snacky error with the error message
     * @param activity
     * @return returns false if no message is provided
     */
    public boolean show(Activity activity) {
        return show(activity, 0);
    }

    public boolean isType(DCErrorType type) {
        return code == type.getCode();
    }
}
