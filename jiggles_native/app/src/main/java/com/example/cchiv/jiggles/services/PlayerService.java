package com.example.cchiv.jiggles.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.cchiv.jiggles.data.ContentContract;
import com.example.cchiv.jiggles.model.Collection;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.MediaPlayer;
import com.example.cchiv.jiggles.player.PlayerMediaSession;

public class PlayerService extends Service implements MediaPlayer.OnStateChanged {

    private static final String TAG = "PlayerService";

    private static final int SERVICE_NOTIFICATION_ID = 9921;

    public static final String RESOURCE_IDENTIFIER = "RESOURCE_IDENTIFIER";

    public MediaPlayer mediaPlayer;
    public PlayerMediaSession playerMediaSession;

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer(this);
        playerMediaSession = new PlayerMediaSession(this, mediaPlayer);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        Bundle bundle = intent.getExtras();
        String resource = bundle.getString(RESOURCE_IDENTIFIER, null);
        Log.v(TAG, resource);

        if(resource != null) {
            Cursor cursor = getContentResolver().query(ContentContract.CONTENT_COLLECTION_URI,
                    null,
                    ContentContract.TrackEntry._ID + "=?",
                    new String[] { resource },
                    null, null);

            Collection collection = Collection.parseCursor(cursor);
            playerMediaSession.createMediaSession();

            mediaPlayer.setPlayer(null, playerMediaSession);
            mediaPlayer.setSource(collection, false);

            setForegroundService(mediaPlayer.getCurrentTrack());
        }

        return START_STICKY;
    }

    public class PlayerBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    public void setForegroundService(Track track) {
        Notification notification = playerMediaSession
                .buildNotificationPlayer(track);

        startForeground(SERVICE_NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onRelease() {
        mediaPlayer.release();
        playerMediaSession.setActive(false);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        stopSelf();
    }

    @Override
    public void onDestroy() {
        onRelease();

        stopSelf();
    }

    @Override
    public void onTrackChanged(Track track) {}

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
