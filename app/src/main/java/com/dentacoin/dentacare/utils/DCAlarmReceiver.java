package com.dentacoin.dentacare.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.dentacoin.dentacare.LaunchActivity;
import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 11/2/17.
 */

public class DCAlarmReceiver extends BroadcastReceiver {

    public static final String TAG = DCAlarmReceiver.class.getSimpleName();
    public static final String KEY_NOTIFICATION = "KEY_NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationKey = intent.getStringExtra(KEY_NOTIFICATION);
        if (notificationKey != null) {
            try {
                DCLocalNotificationsManager.Notification notification = DCLocalNotificationsManager.Notification.valueOf(notificationKey);
                showNotification(context, notification);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void showNotification(Context context, DCLocalNotificationsManager.Notification notification) {
        if (notification != null) {

            String title = "";
            String body = "";

            if (notification.getTitleId() != -1) {
                title = context.getString(notification.getTitleId());
            }

            if (notification.getMessageId() != -1) {
                body = context.getString(notification.getMessageId());
            }

            if (notification == DCLocalNotificationsManager.Notification.HEALTHY_HABIT) {
                DCLocalNotificationsManager.HealthyHabit habit = DCLocalNotificationsManager.HealthyHabit.getRandomHabit();
                title = context.getString(habit.getTitleId());
                body = context.getString(habit.getMessageId());
            }

            DCUtils.createNotificationChannel(context, DCConstants.NOTIFICATION_CHANNEL_ID);

            Intent intent = new Intent(context, LaunchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, DCConstants.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setSmallIcon(R.drawable.notification_icon)
                    .setAutoCancel(true)
                    .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(DCSharedPreferences.getNotificationId(), notificationBuilder.build());
        }
    }
}
