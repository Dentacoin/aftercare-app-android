package com.dentacoin.dentacare.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.dentacoin.dentacare.R;
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

    public String[] getErrors() {
        return errors;
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
            String key = errors[0];
            for (ErrorKey errorKey : errorKeys) {
                if (errorKey.key.compareTo(key) == 0) {
                    return errorKey.getString(context);
                }
            }
            return key;
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

    public boolean isType(@NonNull String string) {
        if (getErrors() != null && getErrors().length > 0) {
            return string.equals(getErrors()[0]);
        }
        return false;
    }


    private static final ErrorKey errorKeys[] = {
            ErrorKey.ERROR_PASSWORD_SHORT,
            ErrorKey.ERROR_INVALID_CREDENTIALS,
            ErrorKey.ERROR_INVALID_GENDER,
            ErrorKey.ERROR_INVALID_COUNTRY,
            ErrorKey.ERROR_INVALID_BIRTHDAY,
            ErrorKey.ERROR_INVALID_AVATAR,
            ErrorKey.ERROR_INVALID_USER,
            ErrorKey.ERROR_MISSING_EMAIL,
            ErrorKey.ERROR_INVALID_EMAIL,
            ErrorKey.ERROR_MISSING_PASSWORD,
            ErrorKey.ERROR_EMAIL_TAKEN,
            ErrorKey.ERROR_FB_ID,
            ErrorKey.ERROR_FB_TOKEN,
            ErrorKey.ERROR_G_ID,
            ErrorKey.ERROR_G_TOKEN,
            ErrorKey.ERROR_TW_ID,
            ErrorKey.ERROR_TW_TOKEN,
            ErrorKey.ERROR_TW_SECRET,
            ErrorKey.ERROR_CANCELED_AUTH,
            ErrorKey.ERROR_CANT_WITHDRAW,
            ErrorKey.ERROR_ALREADY_CONFIRMED,
            ErrorKey.ERROR_TOO_MANY_VERIFY,
            ErrorKey.ERROR_EMAIL_NOT_CONFIRMED,
            ErrorKey.ERROR_INVALID_CAPTCHA,
            ErrorKey.ERROR_USER_DELETE,
            ErrorKey.ERROR_MISSING_CAPTCHA_ID,
            ErrorKey.ERROR_MISSING_CAPTCHA,
            ErrorKey.ERROR_JOURNEY_NOT_STARTED,
            ErrorKey.ERROR_DELETE_CHILD
    };

    public enum ErrorKey {
        ERROR_PASSWORD_SHORT("password_short", R.string.server_error_password),
        ERROR_INVALID_CREDENTIALS("invalid_email_password", R.string.server_error_invalid_credentials),
        ERROR_INVALID_GENDER("invalid_gender", R.string.server_error_invalid_gender),
        ERROR_INVALID_COUNTRY("invalid_country", R.string.server_error_country),
        ERROR_INVALID_BIRTHDAY("invalid_birthday", R.string.server_error_birth_date),
        ERROR_INVALID_AVATAR("invalid_avatar", R.string.server_error_invalid_avatar),
        ERROR_INVALID_USER("user_not_exist", R.string.server_error_user_not_exist),
        ERROR_MISSING_EMAIL("missing_email", R.string.server_error_no_email),
        ERROR_INVALID_EMAIL("invalid_email", R.string.server_error_invalid_email),
        ERROR_MISSING_PASSWORD("missing_password", R.string.server_error_no_password),
        ERROR_EMAIL_TAKEN("email_already_registered", R.string.server_error_email_registered),
        ERROR_FB_ID("missing_facebook_id", R.string.server_error_fb_id),
        ERROR_FB_TOKEN("missing_facebook_token", R.string.server_error_fb_token),
        ERROR_G_ID("missing_google_id", R.string.server_error_google_id),
        ERROR_G_TOKEN("missing_google_token", R.string.server_error_google_token),
        ERROR_TW_ID("missing_twitter_id", R.string.server_error_twitter_id),
        ERROR_TW_TOKEN("missing_twitter_token", R.string.server_error_twitter_token),
        ERROR_TW_SECRET("missing_twitter_token_secret", R.string.server_error_twitter_token_secret),
        ERROR_CANCELED_AUTH("canceled_authentication_by_the_user", R.string.server_error_auth_canceled),
        ERROR_CANT_WITHDRAW("user_cannot_withdraw", R.string.server_error_cannot_withdraw),
        ERROR_ALREADY_CONFIRMED("user_already_confirmed", R.string.server_error_email_confirmed),
        ERROR_TOO_MANY_VERIFY("too_many_requests", R.string.server_error_too_many_verify),
        ERROR_EMAIL_NOT_CONFIRMED("email_not_confirmed", R.string.server_error_email_not_confirmed),
        ERROR_INVALID_CAPTCHA("invalid_captcha_code", R.string.server_error_invalid_captcha),
        ERROR_USER_DELETE("error_deleting_user", R.string.server_error_delete_user),
        ERROR_MISSING_CAPTCHA_ID("missing_captcha_id", R.string.server_error_no_captcha_id),
        ERROR_MISSING_CAPTCHA("missing_captcha_code", R.string.server_error_no_captcha),
        ERROR_JOURNEY_NOT_STARTED("not_started_yet", R.string.server_error_journey_not_started),
        ERROR_DELETE_CHILD("error_delete_child", R.string.server_error_delete_child);

        String key;
        int resourceId;

        ErrorKey(String key, int resourceId) {
            this.key = key;
            this.resourceId = resourceId;
        }

        public String getKey() {
            return key;
        }

        public String getString(Context context) {
            if (resourceId > 0) {
                return context.getString(resourceId);
            } else {
                return key;
            }
        }
    }
}
