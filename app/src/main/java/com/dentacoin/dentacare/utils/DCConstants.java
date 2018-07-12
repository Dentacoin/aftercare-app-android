package com.dentacoin.dentacare.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Atanas Chervarov on 7/26/17.
 */

public class DCConstants {
    //Build types & flavors
    public static final String BUILD_FLAVOR_DEV = "dev";
    public static final String BUILD_FLAVOR_STAGING = "staging";
    public static final String BUILD_FALVOR_LIVE = "live";
    public static final String BUILD_TYPE_DEBUG = "debug";
    public static final String BUILD_TYPE_RELEASE = "release";

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static final int AVATAR_DEFAULT_SIZE_WIDTH = 512;
    public static final int AVATAR_DEFAULT_SIZE_HEIGHT = 512;

    public static final String EMERGENCY_EMAIL = "emergency@dentacoin.com";
    public static final String DENTACARE_WEBSITE = "https://dentacare.dentacoin.com/";
    public static final String DENTACOIN_WEBSITE = "https://www.dentacoin.com/";
    public static final String DENTACARE_GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=com.dentacoin.dentacare";
    public static final String FIREBASE_SHARE_LINK = "https://cx355.app.goo.gl/";
    public static final String IOS_BUNDLE_ID = "com.dentacoin.dentacare-app";

    public static final String REGEX_INVITES = "/invites/.*";

    public static final Pattern ADDRESS_PATTERN = Pattern.compile("^0x[a-fA-F0-9]{40}");
    public static final Pattern IBAN_LONG_PATTERN = Pattern.compile("[A-Z0-9]{35}");
    public static final Pattern IBAN_SHORT_PATTERN = Pattern.compile("[A-Z0-9]{34}");

    public static final SimpleDateFormat DATE_FORMAT_BIRTHDAY = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
    public static final SimpleDateFormat DATE_FORMAT_LAST_ACTIVITY = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);

    public static final int MIN_AGE = 13;
    public static final int MAX_AGE = 99;

    public static final int COUNTDOWN_MAX_AMOUNT_RINSE = 30 * 1000;
    public static final int COUNTDOWN_MAX_AMOUNT = 6 * 60 * 1000;
    public static final int COUNTDOWN_MIN_AMOUNT = 2 * 60 * 1000;

    public static final int DAYS_OF_USE = 90;

    public enum DCActivityType { FLOSS, BRUSH, RINSE }
    public enum DCStatisticsType { DAILY, WEEKLY, MONTHLY }
    public enum DCGoalType { DEFAULT, WEEK, MONTH, YEAR }

    public final static String GENDER_MALE = "male";
    public final static String GENDER_FEMALE = "female";
    public final static String GENDER_UNSPECIFIED = "unspecified";

    public final static String FRIEND_FRIEND = "friend";
    public final static String FRIEND_FAMILY = "family";
    public final static String FRIEND_CHILD = "child";

    public final static String NOTIFICATION_CHANNEL_ID = "Aftercare";
}
