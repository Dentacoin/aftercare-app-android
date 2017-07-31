package com.dentacoin.dentacare.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by Atanas Chervarov on 7/30/17.
 */

public class DCFonts {
    private static final String TAG = DCFonts.class.getSimpleName();

    public static final String FONT_LATO_LIGHT = "fonts/Lato-Light.ttf";
    public static final String FONT_LATO_REGULAR = "fonts/Lato-Regular.ttf";

    private static final Hashtable<String, Typeface> fonts = new Hashtable<>();

    public static Typeface getFont(Context context, String name) {
        Typeface typeface = fonts.get(name);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), name);
            fonts.put(name, typeface);
        }
        return typeface;
    }
}
