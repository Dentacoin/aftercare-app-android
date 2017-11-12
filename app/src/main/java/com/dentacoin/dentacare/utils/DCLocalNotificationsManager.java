package com.dentacoin.dentacare.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dentacoin.dentacare.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Atanas Chervarov on 11/2/17.
 */

public class DCLocalNotificationsManager {

    private static final String TAG = DCLocalNotificationsManager.class.getSimpleName();

    private static final long INTERVAL_24H = 24 * 60 * 60 * 1000;
    private static final long INTERVAL_3_MONTHS = 3 * 30 * 24 * 60 * 60 * 1000;
    private static final long INTERVAL_4_MONTHS = 4 * 30 * 24 * 60 * 60 * 1000;

    public enum HealthyHabit {
        HABIT_1(R.string.notifications_hdl_healthy_habits_1, R.string.notifications_txt_healthy_habits_1),
        HABIT_2(R.string.notifications_hdl_healthy_habits_2, R.string.notifications_txt_healthy_habits_2),
        HABIT_3(R.string.notifications_hdl_healthy_habits_3, R.string.notifications_txt_healthy_habits_3),
        HABIT_4(R.string.notifications_hdl_healthy_habits_4, R.string.notifications_txt_healthy_habits_4),
        HABIT_5(R.string.notifications_hdl_healthy_habits_5, R.string.notifications_txt_healthy_habits_5),
        HABIT_6(R.string.notifications_hdl_healthy_habits_6, R.string.notifications_txt_healthy_habits_6),
        HABIT_7(R.string.notifications_hdl_healthy_habits_7, R.string.notifications_txt_healthy_habits_7),
        HABIT_8(R.string.notifications_hdl_healthy_habits_8, R.string.notifications_txt_healthy_habits_8),
        HABIT_9(R.string.notifications_hdl_healthy_habits_9, R.string.notifications_txt_healthy_habits_9),
        HABIT_10(R.string.notifications_hdl_healthy_habits_10, R.string.notifications_txt_healthy_habits_10);

        private int titleId;
        private int messageId;

        HealthyHabit(int titleId, int messageId) {
            this.titleId = titleId;
            this.messageId = messageId;
        }

        public int getTitleId() { return titleId; }
        public int getMessageId() { return messageId; }

        public static HealthyHabit getRandomHabit() {
            HealthyHabit[] habits = values();
            Random random = new Random();
            int index = random.nextInt(habits.length-1);
            return habits[index];
        }
    }

    public enum Notification {
        DAILY_BRUSHING(101, DCSharedPreferences.DCSharedKey.DAILY_BRUSHING, -1, R.string.notifications_txt_daily_brushing_1),
        CHANGE_BRUSH(102, DCSharedPreferences.DCSharedKey.CHANGE_BRUSH, -1, R.string.notifications_txt_change_brush_1),
        VISIT_DENTIST(103, DCSharedPreferences.DCSharedKey.VISIT_DENTIST, -1, R.string.notifications_txt_visit_dentist),
        COLLECT_DENTACOIN(104, DCSharedPreferences.DCSharedKey.COLLECT_DENTACOIN, -1, R.string.notifications_txt_collect_dentacoin),
        REMINDER_TO_VISIT(105, DCSharedPreferences.DCSharedKey.REMINDER_TO_VISIT, -1, R.string.notifications_txt_reminder_to_visit),
        HEALTHY_HABIT(201, DCSharedPreferences.DCSharedKey.HEALTHY_HABIT);

        private int tag;
        private int titleId = -1;
        private int messageId = -1;
        private DCSharedPreferences.DCSharedKey key;

        Notification(int tag, DCSharedPreferences.DCSharedKey key, int titleId, int messageId) {
            this.tag = tag;
            this.key = key;
            this.titleId = titleId;
            this.messageId = messageId;
        }

        Notification(int tag, DCSharedPreferences.DCSharedKey key) {
            this.tag = tag;
            this.key = key;
        }

        public DCSharedPreferences.DCSharedKey getKey() {
            return key;
        }

        public int getTitleId() { return titleId; }
        public int getMessageId() { return messageId; }
        public int getTag() { return tag; }
    }

    private static DCLocalNotificationsManager instance;

    public static synchronized DCLocalNotificationsManager getInstance() {
        if (instance == null) {
            instance = new DCLocalNotificationsManager();
        }
        return instance;
    }


    DCLocalNotificationsManager() {
    }


    /**
     * Returns time in milliseconds for +1 day if the hour & minute has already passed that day
     * @param hourOfDay
     * @param minute
     * @return
     */
    private long getRepeatingNotificationTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (calendar.get(Calendar.HOUR_OF_DAY) > hourOfDay) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        }
        else if (calendar.get(Calendar.HOUR_OF_DAY) == hourOfDay) {
            if (calendar.get(Calendar.MINUTE) >= minute) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                calendar.set(Calendar.MINUTE, minute);
            }
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
        }

        return calendar.getTimeInMillis();
    }

    public void scheduleNotifications(Context context, boolean cancel) {
        if (context == null)
            return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Schedule Daily Brushing reminder for 11:00am each day
        scheduleNotification(alarmManager, context, Notification.DAILY_BRUSHING, getRepeatingNotificationTime(11, 0), INTERVAL_24H, cancel);

        //Schedule Daily Random Healthy habit for 16:00pm each day
        scheduleNotification(alarmManager, context, Notification.HEALTHY_HABIT, getRepeatingNotificationTime(16, 0), INTERVAL_24H, cancel);

        //Schedule Reminder to visit notification for 18:00 for 7 days of inactivity
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        scheduleNotification(alarmManager, context, Notification.REMINDER_TO_VISIT, calendar.getTimeInMillis(), cancel);

        //Schedule change brush reminder
        String firstLogin = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.FIRST_LOGIN_DATE);
        if (firstLogin != null) {
            try {
                Date firstLoginDate = DCConstants.DATE_FORMAT.parse(firstLogin);

                Date now = new Date();
                calendar.setTime(firstLoginDate);
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                calendar.set(Calendar.MINUTE, 0);

                while (calendar.getTime().compareTo(now) < 0) {
                    calendar.add(Calendar.DAY_OF_YEAR,83);
                }

                scheduleNotification(alarmManager, context, Notification.CHANGE_BRUSH, calendar.getTimeInMillis(), INTERVAL_3_MONTHS, cancel);

                //Schedule visit dentist reminder
                calendar.setTime(firstLoginDate);
                calendar.add(Calendar.DAY_OF_YEAR, 14);
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                calendar.set(Calendar.MINUTE, 0);

                while (calendar.getTime().compareTo(now) < 0) {
                    calendar.add(Calendar.DAY_OF_YEAR, 106);
                }

                scheduleNotification(alarmManager, context, Notification.VISIT_DENTIST, calendar.getTimeInMillis(), INTERVAL_4_MONTHS, cancel);
            } catch (Exception e) {
                e.printStackTrace();
                DCSharedPreferences.removeKey(DCSharedPreferences.DCSharedKey.FIRST_LOGIN_DATE);
            }
        }
    }

    public void scheduleNotification(AlarmManager alarmManager, Context context, Notification notification, long triggerAt, boolean cancel) {
        schedule(alarmManager, context, notification, triggerAt, -1, cancel);
    }

    public void scheduleNotification(AlarmManager alarmManager, Context context, Notification notification, long triggerAt, long interval, boolean cancel) {
        schedule(alarmManager, context, notification, triggerAt, interval, cancel);
    }

    private void schedule(AlarmManager alarmManager, Context context, Notification notification, long triggerAt, long interval, boolean cancel) {
        if (alarmManager == null ||context == null || notification == null)
            return;

        Intent intent = new Intent(context, DCAlarmReceiver.class);
        intent.putExtra(DCAlarmReceiver.KEY_NOTIFICATION, notification.name());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notification.getTag(), intent, 0);

        if (DCSharedPreferences.getBoolean(notification.getKey(), false) || cancel) {
            try {
                alarmManager.cancel(pendingIntent);
                Log.d(TAG, "Unscheduling notification '" + notification.name() + "' - user disabled");
            } catch (Exception e) {
                Log.d(TAG, "Skipping schedule of notification '" + notification.name() + "' - user disabled");
                e.printStackTrace();
            }
            return;
        }

        if (interval > 0) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAt, interval, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        }
    }
}
