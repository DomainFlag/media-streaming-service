package com.example.cchiv.jiggles.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.cchiv.jiggles.data.ContentContract;
import com.example.cchiv.jiggles.data.ContentContract.TrackEntry;
import com.example.cchiv.jiggles.model.Collection;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.MediaPlayer;
import com.example.cchiv.jiggles.player.PlayerMediaSession;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

import java.util.ArrayList;
import java.util.List;

public class PlayerService extends Service implements MediaPlayer.OnTrackStateChanged {

    private static final String TAG = "PlayerService";

    public static final int SERVICE_NOTIFICATION_ID = 9921;

    private static final int LOADER_SERVICE_COLLECTION_ID = 1021;

    public interface OnCallbackListener {
        void onCallbackListener(Track track, int playbackStateCompat);
    }

    public static final String RESOURCE_IDENTIFIER = "RESOURCE_IDENTIFIER";
    public static final String RESOURCE_TYPE = "RESOURCE_TYPE";

    public List<OnCallbackListener> listeners = new ArrayList<>();

    public MediaPlayer mediaPlayer;
    public PlayerMediaSession playerMediaSession;

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();

        mediaPlayer = new MediaPlayer(this);
        playerMediaSession = new PlayerMediaSession(this, mediaPlayer);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show();

        if(intent != null) {
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                String resourceId = bundle.getString(RESOURCE_IDENTIFIER, null);
                String resourceType = bundle.getString(RESOURCE_TYPE, TrackEntry._ID);

                if(resourceId != null && resourceType != null) {
                    Bundle loaderBundle = new Bundle();
                    loaderBundle.putString(JigglesLoader.BUNDLE_URI_KEY, ContentContract.CONTENT_COLLECTION_URI.toString());
                    loaderBundle.putString(JigglesLoader.BUNDLE_SELECTION_KEY, resourceType + "=?");
                    loaderBundle.putStringArray(JigglesLoader.BUNDLE_SELECTION_ARGS_KEY, new String[] { resourceId });

                    JigglesLoader.AsyncTaskContentLoader<Collection> asyncTaskContentLoader =
                            new JigglesLoader.AsyncTaskContentLoader<>(this, loaderBundle, Collection::parseCursor);

                    asyncTaskContentLoader.registerListener(LOADER_SERVICE_COLLECTION_ID, (loader, collection) -> {
                        playerMediaSession.createMediaSession();

                        mediaPlayer.setPlayer(playerMediaSession);
                        mediaPlayer.setSource(collection);

                        Track track = getCurrentTrack();
                        setForegroundService(track);
                    });

                    asyncTaskContentLoader.forceLoad();
                }
            }
        }

        return START_STICKY;
    }

    public class PlayerBinder extends Binder {
        public PlayerService getService() {
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
        return new PlayerBinder();
    }

    public void onAttachCallbackListener(OnCallbackListener onCallbackListener) {
        listeners.add(onCallbackListener);
    }

    public void onDetachCallbackListener(OnCallbackListener onCallbackListener) {
        listeners.remove(onCallbackListener);
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

    public void onTrackNotifyAll(Track track) {
        for(OnCallbackListener listener : listeners) {
            listener.onCallbackListener(track, playerMediaSession.getState());
        }
    }

    @Override
    public void onTrackStateChanged(Track track) {
        onTrackNotifyAll(track);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public PlayerMediaSession getPlayerMediaSession() {
        return playerMediaSession;
    }

    public Track getCurrentTrack() {
        return mediaPlayer.getCurrentTrack();
    }

    public int getPlaybackStateCompat() {
        return playerMediaSession.getState();
    }
}
