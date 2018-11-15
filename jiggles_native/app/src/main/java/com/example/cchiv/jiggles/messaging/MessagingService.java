package com.example.cchiv.jiggles.messaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Message;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    private static final String MESSAGING_CHANNEL_ID = "MESSAGING_CHANNEL_ID";
    private static final int MESSAGING_ID = 5121;

    public MessagingService() {
        super();

        String id = FirebaseInstanceId.getInstance().getId();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        Message message = Message.decodeMessage(data);

//        ContentValues contentValues = Message.parseValues(message);
//        getContentResolver().insert(NotificationEntry.CONTENT_URI, contentValues);

        Notification notification = buildNotification(message);
        createNotificationChannel();

        NotificationManagerCompat.from(this).notify(MESSAGING_ID, notification);

        super.onMessageReceived(remoteMessage);
    }

    private Notification buildNotification(Message message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MESSAGING_CHANNEL_ID);

        return builder
                .setSmallIcon(R.drawable.ic_microphone)
                .setContentTitle("New message")
                .setContentText(message.getType())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;

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
