package com.dentacoin.dentacare.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dentacoin.dentacare.R;

import java.util.Calendar;

/**
 * Created by Atanas Chervarov on 11/2/17.
 */

public class DCLocalNotificationsManager {

    private static final String TAG = DCLocalNotificationsManager.class.getSimpleName();

    public enum Notification {
        DAILY_BRUSHING(DCSharedPreferences.DCSharedKey.DAILY_BRUSHING, -1, R.string.notifications_txt_daily_brushing_1),
        CHANGE_BRUSH(DCSharedPreferences.DCSharedKey.CHANGE_BRUSH, -1, R.string.notifications_txt_change_brush_1),
        VISIT_DENTIST(DCSharedPreferences.DCSharedKey.VISIT_DENTIST, -1, R.string.notifications_txt_visit_dentist),
        COLLECT_DENTACOIN(DCSharedPreferences.DCSharedKey.COLLECT_DENTACOIN, -1, R.string.notifications_txt_collect_dentacoin),
        REMINDER_TO_VISIT(DCSharedPreferences.DCSharedKey.REMINDER_TO_VISIT, -1, R.string.notifications_txt_reminder_to_visit),

        HEALTHY_HABIT_1(DCSharedPreferences.DCSharedKey.HEALTHY_HABIT_1, R.string.notifications_hdl_healthy_habits_1, R.string.notifications_txt_healthy_habits_1),
        HEALTHY_HABIT_2(DCSharedPreferences.DCSharedKey.HEALTHY_HABIT_2, R.string.notifications_hdl_healthy_habits_2, R.string.notifications_txt_healthy_habits_2),
        HEALTHY_HABIT_3(DCSharedPreferences.DCSharedKey.HEALTHY_HABIT_3, R.string.notifications_hdl_healthy_habits_3, R.string.notifications_txt_healthy_habits_3),
        HEALTHY_HABIT_4(DCSharedPreferences.DCSharedKey.HEALTHY_HABIT_4, R.string.notifications_hdl_healthy_habits_4, R.string.notifications_txt_healthy_habits_4),
        HEALTHY_HABIT_5(DCSharedPreferences.DCSharedKey.HEALTHY_HABIT_5, R.string.notifications_hdl_healthy_habits_5, R.string.notifications_txt_healthy_habits_5),
        HEALTHY_HABIT_6(DCSharedPreferences.DCSharedKey.HEALTHY_HABIT_6, R.string.notifications_hdl_healthy_habits_6, R.string.notifications_txt_healthy_habits_6),
        HEALTHY_HABIT_7(DCSharedPreferences.DCSharedKey.HEALTHY_HABIT_7, R.string.notifications_hdl_healthy_habits_7, R.string.notifications_txt_healthy_habits_7),
        HEALTHY_HABIT_8(DCSharedPreferences.DCSharedKey.HEALTHY_HABIT_8, R.string.notifications_hdl_healthy_habits_8, R.string.notifications_txt_healthy_habits_8),
        HEALTHY_HABIT_9(DCSharedPreferences.DCSharedKey.HEALTHY_HABIT_9, R.string.notifications_hdl_healthy_habits_9, R.string.notifications_txt_healthy_habits_9),
        HEALTHY_HABIT_10(DCSharedPreferences.DCSharedKey.HEALTHY_HABIT_10, R.string.notifications_hdl_healthy_habits_10, R.string.notifications_txt_healthy_habits_10);

        private int titleId;
        private int messageId;
        private DCSharedPreferences.DCSharedKey key;

        Notification(DCSharedPreferences.DCSharedKey key, int titleId, int messageId) {
            this.key = key;
            this.titleId = titleId;
            this.messageId = messageId;
        }

        public DCSharedPreferences.DCSharedKey getKey() {
            return key;
        }

        public int getTitleId() { return titleId; }
        public int getMessageId() { return messageId; }
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

    public void scheduleNotifications(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 5);

        scheduleNotification(alarmManager, context, Notification.HEALTHY_HABIT_1, calendar.getTimeInMillis());
        calendar.add(Calendar.MINUTE, 2);
        scheduleNotification(alarmManager, context, Notification.HEALTHY_HABIT_2, calendar.getTimeInMillis());
        calendar.add(Calendar.MINUTE, 2);
        scheduleNotification(alarmManager, context, Notification.HEALTHY_HABIT_3, calendar.getTimeInMillis());
        calendar.add(Calendar.MINUTE, 2);
        scheduleNotification(alarmManager, context, Notification.HEALTHY_HABIT_4, calendar.getTimeInMillis());
        calendar.add(Calendar.MINUTE, 2);
        scheduleNotification(alarmManager, context, Notification.HEALTHY_HABIT_5, calendar.getTimeInMillis());
        calendar.add(Calendar.MINUTE, 2);
        scheduleNotification(alarmManager, context, Notification.HEALTHY_HABIT_6, calendar.getTimeInMillis());
        calendar.add(Calendar.MINUTE, 2);
        scheduleNotification(alarmManager, context, Notification.HEALTHY_HABIT_7, calendar.getTimeInMillis());
        calendar.add(Calendar.MINUTE, 2);
        scheduleNotification(alarmManager, context, Notification.HEALTHY_HABIT_8, calendar.getTimeInMillis());
        calendar.add(Calendar.MINUTE, 2);
        scheduleNotification(alarmManager, context, Notification.HEALTHY_HABIT_9, calendar.getTimeInMillis());
        calendar.add(Calendar.MINUTE, 2);
        scheduleNotification(alarmManager, context, Notification.HEALTHY_HABIT_10, calendar.getTimeInMillis());
    }

    public void scheduleNotification(AlarmManager alarmManager, Context context, Notification notification, long milliseconds) {

        Intent intent = new Intent(context, DCAlarmReceiver.class);
        intent.putExtra(DCAlarmReceiver.KEY_NOTIFICATION, notification.name());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        if (DCSharedPreferences.getBoolean(notification.getKey(), false)) {
            try {
                alarmManager.cancel(pendingIntent);
                Log.d(TAG, "Unscheduling notification '" + notification.name() + "' - user disabled");
            } catch (Exception e) {
                Log.d(TAG, "Skipping schedule of notification '" + notification.name() + "' - user disabled");
                e.printStackTrace();
            }
            return;
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent);
    }
}
