package com.dentacoin.dentacare.model;

import android.content.Context;
import android.content.res.Resources;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 8/7/17.
 */

public class DCError {
    public enum DCErrorType {
        UNKNOWN(1001, R.string.error_txt_something_went_wrong),
        VALIDATION(1002, -1),
        NETWORK(1003, R.string.error_txt_something_went_wrong);

        private int code;
        private int resource;

        DCErrorType(int code, int resource) {
            this.code = code;
            this.resource = resource;
        }

        public int getResource() { return resource; }
        public int getCode() { return code; }
    }

    private int code;
    private int resourceId;
    private String message;

    /**
     * Create error from given enum
     * @param type
     */
    public DCError(DCErrorType type) {
        this(type.getCode(), type.getResource(), null);
    }

    /**
     * Create error from given resource
     * @param resource
     */
    public DCError(int resource) {
        this(1001, resource, null);
    }

    /**
     * Create error with given message
     * @param message
     */
    public DCError(String message) {
        this(1001, -1, message);
    }

    /**
     * Create error with code, resource or message
     * @param code
     * @param resourceId
     * @param message
     */
    public DCError(int code, int resourceId, String message) {
        this.code = code;
        this.resourceId = resourceId;
        this.message = message;
    }

    public int getCode() {
        return code;
    }
    
    /**
     * Get message from the string resources
     * @param context
     * @return
     */
    public String getMessage(Context context) {
        if (resourceId != -1) {
            try {
                return context.getString(resourceId);
            } catch (Resources.NotFoundException e) { }
        }

        return message;
    }
}
