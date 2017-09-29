package com.dentacoin.dentacare.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.dentacoin.dentacare.network.DCApiManager;
import com.google.gson.JsonSyntaxException;

/**
 * Created by Atanas Chervarov on 8/16/17.
 */

public class DCSharedPreferences {

    public enum DCSharedKey {
        AUTH_TOKEN("AUTH_TOKEN"),
        USER("USER"),
        WELCOME_SCREEN("WELCOME_SCREEN"),
        DEFAULT_WALLET("DEFAULT_WALLET"),
        DASHBOARD("DASHBOARD"),
        RECORDS("RECORDS"),
        COLLECTED("COLLECTED");

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

    public static boolean saveBoolean(DCSharedKey key, boolean value) {
        return getInstance().preferences.edit().putBoolean(key.getKey(), value).commit();
    }

    public static boolean getBoolean(DCSharedKey key, boolean defaultValue) {
        return getInstance().preferences.getBoolean(key.getKey(), defaultValue);
    }

    public static boolean saveString(DCSharedKey key, String value) {
        return getInstance().preferences.edit().putString(key.getKey(), value).commit();
    }

    public static String loadString(DCSharedKey key) {
        return getInstance().preferences.getString(key.getKey(), null);
    }

    public static boolean removeKey(DCSharedKey key) {
        return  getInstance().preferences.edit().remove(key.getKey()).commit();
    }

    public static int loadInt(DCSharedKey key) {
        return getInstance().preferences.getInt(key.getKey(), 0);
    }

    public static boolean saveInt(DCSharedKey key, int value) {
        return getInstance().preferences.edit().putInt(key.getKey(), value).commit();
    }


    /**
     * Cleans all saved user data
     */
    public static void clean() {
        removeKey(DCSharedKey.AUTH_TOKEN);
        removeKey(DCSharedKey.USER);
        removeKey(DCSharedKey.WELCOME_SCREEN);
        removeKey(DCSharedKey.DEFAULT_WALLET);
        removeKey(DCSharedKey.DASHBOARD);
        removeKey(DCSharedKey.RECORDS);
    }
}
