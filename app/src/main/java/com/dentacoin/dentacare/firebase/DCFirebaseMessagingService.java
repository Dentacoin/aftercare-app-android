package com.dentacoin.dentacare.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dentacoin.dentacare.LaunchActivity;
import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Atanas Chervarov on 10/15/17.
 */

public class DCFirebaseMessagingService extends FirebaseMessagingService {

    private final static String TAG = DCFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        Log.d(TAG, "remote message received");
        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage) {
        if (remoteMessage != null && remoteMessage.getNotification() != null) {
            String title = getString(R.string.txt_app_name);
            String message = "";

            Intent intent = new Intent(this, LaunchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if (remoteMessage.getNotification().getTitle() != null) {
                title = remoteMessage.getNotification().getTitle();
            }

            if (remoteMessage.getNotification().getBody() != null) {
                message = remoteMessage.getNotification().getBody();
            }


            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setAutoCancel(true)
                    .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification))
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(DCSharedPreferences.getNotificationId(), notificationBuilder.build());
        }
    }
}
