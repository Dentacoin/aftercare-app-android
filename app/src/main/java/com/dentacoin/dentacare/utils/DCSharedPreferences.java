package com.dentacoin.dentacare.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import static com.dentacoin.dentacare.utils.DCSharedPreferences.DCSharedKey.SHOWED_TUTORIALS;

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
        GOALS("GOALS"),
        GOALS_REACHED("GOALS_REACHED"),
        COLLECTED("COLLECTED"),
        SHOWED_TUTORIALS("SHOWED_TUTORIALS");

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

    public static Set<String> getShownTutorials() {
        return getInstance().preferences.getStringSet(SHOWED_TUTORIALS.getKey(), new HashSet<String>());
    }

    public static void setShownTutorial(DCTutorialManager.TUTORIAL tutorial, boolean shown) {
        Set<String> shownTutorials = getShownTutorials();
        HashSet<String> tutorials = new HashSet<>();
        tutorials.addAll(shownTutorials);

        if (shown) {
            tutorials.add(tutorial.name());
        } else {
            tutorials.remove(tutorial.name());
        }

        getInstance().preferences.edit().putStringSet(SHOWED_TUTORIALS.getKey(), tutorials).apply();
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
        removeKey(DCSharedKey.COLLECTED);
        removeKey(DCSharedKey.GOALS_REACHED);
        removeKey(DCSharedKey.SHOWED_TUTORIALS);
    }
}
