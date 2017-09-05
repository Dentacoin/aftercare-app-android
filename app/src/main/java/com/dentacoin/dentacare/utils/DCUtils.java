package com.dentacoin.dentacare.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;

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

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        v.draw(canvas);
        return bitmap;
    }

    public static void openURL(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }
}
