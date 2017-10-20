package com.dentacoin.dentacare.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.dentacoin.dentacare.LaunchActivity;
import com.dentacoin.dentacare.R;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Atanas Chervarov on 10/16/17.
 */

public class DCNotificationJob extends Job {

    public static final String TAG = "NOTIFICATION_TAG";

    private final static String KEY_TITLE = "KEY_TITLE";
    private final static String KEY_MESSAGE = "KYE_MESSAGE";

    public enum NOTIFICATION {
        DAILY_BRUSHING,
        CHANGE_BRUSH,
        VISIT_DENTIST,
        COLLECT_DENTACOIN,
        REMINDER_TO_VISIT,
        HEALTHY_HABIT;

        public boolean withTag(String tag) {
            if (name().compareTo(tag) == 0)
                return true;

            return false;
        }
    }

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        PersistableBundleCompat bundleCompat = params.getExtras();

        String title = bundleCompat.getString(KEY_TITLE, "Dentacare");
        String message = bundleCompat.getString(KEY_MESSAGE, "");

        Intent launchIntent = new Intent(getContext(), LaunchActivity.class);

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, launchIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext())
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.notification_icon)
                .setAutoCancel(true)
                .setSound(Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.notification))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(DCSharedPreferences.getNotificationId(), notificationBuilder.build());
        return Result.SUCCESS;
    }

    public static void scheduleAll(Context context) {
        schedule(context, NOTIFICATION.DAILY_BRUSHING);
        schedule(context, NOTIFICATION.CHANGE_BRUSH);
        schedule(context, NOTIFICATION.VISIT_DENTIST);
        schedule(context, NOTIFICATION.COLLECT_DENTACOIN);
        schedule(context, NOTIFICATION.REMINDER_TO_VISIT);
        schedule(context, NOTIFICATION.HEALTHY_HABIT);
    }

    public static void schedule(Context context, NOTIFICATION notification) {
        String title = "Dentacare";
        String mesage = "";
        long delay = 0;
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);


        switch (notification) {
            case DAILY_BRUSHING:
                mesage = context.getString(R.string.notifications_txt_daily_brushing_1);
                calendar.set(Calendar.HOUR_OF_DAY, 18);
                calendar.set(Calendar.MINUTE, 14);
                calendar.set(Calendar.SECOND, 0);

                if (calendar.getTimeInMillis() > now.getTime()) {
                    delay = calendar.getTimeInMillis() - now.getTime();
                } else {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    delay = calendar.getTimeInMillis() - now.getTime();
                }

                

                break;
            case CHANGE_BRUSH:
                mesage = context.getString(R.string.notifications_txt_change_brush_1);
                delay = 50000;
                break;
            default:
                return;
        }

        PersistableBundleCompat bundleCompat = new PersistableBundleCompat();
        bundleCompat.putString(KEY_TITLE, title);
        bundleCompat.putString(KEY_MESSAGE, mesage);

        new JobRequest.Builder(notification.name())
                .setExtras(bundleCompat)
                .setExact(delay)
                .build()
                .schedule();

    }
}
