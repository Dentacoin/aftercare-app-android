package com.dentacoin.dentacare.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Atanas Chervarov on 8/16/17.
 */

public class DCSharedPreferences {

    public enum DCSharedKey {
        AUTH_TOKEN("AUTH_TOKEN");

        private String key;

        DCSharedKey(String key) {
            this.key = key;
        }

        public String getKey() { return key; }
    }

    private static DCSharedPreferences instance;
    private SharedPreferences preferences;

    private static DCSharedPreferences getInstance() {
        if (instance == null)
            instance = new DCSharedPreferences();
        return instance;
    }

    public static void initialize(Context context) {
        getInstance().preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static boolean saveString(DCSharedKey key, String value) {
        return getInstance().preferences.edit().putString(key.getKey(), value).commit();
    }

    public static String loadString(DCSharedKey key) {
        return getInstance().preferences.getString(key.getKey(), null);
    }

    public static boolean removeString(DCSharedKey key) {
        return  getInstance().preferences.edit().remove(key.getKey()).commit();
    }
}
