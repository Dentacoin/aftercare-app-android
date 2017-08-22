package com.dentacoin.dentacare.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Atanas Chervarov on 8/14/17.
 */

public class DCUtils {

    public static final String REGEX_NAME = "^[\\p{L} .'-]+$";

    public static boolean isValidName(String name) {
        if (!TextUtils.isEmpty(name)) {
            Pattern pattern = Pattern.compile(REGEX_NAME, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(name);
            return matcher.find();
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        if (!TextUtils.isEmpty(email)) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
        return false;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        if (context != null) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }
}
