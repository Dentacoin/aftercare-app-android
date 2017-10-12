package com.dentacoin.dentacare.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.Hashtable;

/**
 * Created by Atanas Chervarov on 7/30/17.
 */

public class DCFonts {
    private static final String TAG = DCFonts.class.getSimpleName();

    public static final String FONT_LATO_LIGHT = "fonts/Lato-Light.ttf";
    public static final String FONT_LATO_REGULAR = "fonts/Lato-Regular.ttf";
    public static final String FONT_LATO_BOLD = "fonts/Lato-Bold.ttf";

    private static final Hashtable<String, Typeface> fonts = new Hashtable<>();

    public static Typeface getFont(Context context, String name) {
        Typeface typeface = fonts.get(name);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), name);
            fonts.put(name, typeface);
        }
        return typeface;
    }

    public static void overrideFonts(final Context context, final View view, final Typeface typeface) {
        try {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View child = viewGroup.getChildAt(i);
                    overrideFonts(context, child, typeface);
                }
            } else if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            }
        } catch (Exception e) {

        }
    }
}
