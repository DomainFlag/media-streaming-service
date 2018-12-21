package com.example.cchiv.jiggles.messaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.FeedItem;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    private static final String MESSAGING_CHANNEL_ID = "MESSAGING_CHANNEL_ID";
    private static final int MESSAGING_ID = 5121;

    private static final Gson gson = new Gson();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Decoding the feed item
        FeedItem feedItem = FeedItem.decodeMessage(remoteMessage, gson);

        if(feedItem != null) {
            // Setting up the notification
            Notification notification = buildNotification(feedItem);
            createNotificationChannel();

            // Notifying user
            NotificationManagerCompat.from(this).notify(MESSAGING_ID, notification);
        }

        super.onMessageReceived(remoteMessage);
    }

    private Notification buildNotification(FeedItem feedItem) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MESSAGING_CHANNEL_ID);

        return builder
                .setSmallIcon(R.drawable.ic_logo_round)
                .setContentTitle(feedItem.getAuthor().getName())
                .setContentText(feedItem.getContent())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        // Notification support for devices running on Oreo and up
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;

            // Creating the notification channel
            NotificationChannel channel = new NotificationChannel(MESSAGING_CHANNEL_ID, getPackageName(), importance);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onNewToken(String s) {
        Log.v(TAG, "Token created " + s);

        super.onNewToken(s);
    }
}
