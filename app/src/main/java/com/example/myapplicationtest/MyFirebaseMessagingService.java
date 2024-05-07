package com.example.myapplicationtest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.CommonNotificationBuilder;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if (message.getNotification() != null) {

            Intent intent = new Intent(this, TelaPrincipalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
                            .setSmallIcon(R.drawable.resource_super)
                            .setColor(getColor(R.color.yellow))
                            .setContentTitle(message.getNotification().getTitle())
                            .setContentText(message.getNotification().getBody())
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

}
