package com.example.cchiv.jiggles.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.cchiv.jiggles.data.ContentContract;
import com.example.cchiv.jiggles.data.ContentContract.AlbumEntry;
import com.example.cchiv.jiggles.data.ContentContract.TrackEntry;
import com.example.cchiv.jiggles.model.player.PlayerContent;
import com.example.cchiv.jiggles.model.player.PlayerState;
import com.example.cchiv.jiggles.player.MediaPlayer;
import com.example.cchiv.jiggles.player.core.AlphaPlayer;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

import java.util.ArrayList;
import java.util.List;

public class PlayerService extends Service implements
        AlphaPlayer.PlayerStateChanged, MediaPlayer.PlayerNotificationManager {

    private static final String TAG = "PlayerService";

    public static final int SERVICE_NOTIFICATION_ID = 9921;

    private static final int LOADER_SERVICE_COLLECTION_ID = 1021;

    public static final String RESOURCE_SOURCE = "RESOURCE_SOURCE";
    public static final String RESOURCE_LOCAL = "RESOURCE_LOCAL";
    public static final String RESOURCE_REMOTE = "RESOURCE_REMOTE";

    public static final String RESOURCE_TYPE = "RESOURCE_TYPE";
    public static final String RESOURCE_IDENTIFIER = "RESOURCE_IDENTIFIER";
    public static final String RESOURCE_PARENT_TYPE = "RESOURCE_PARENT_TYPE";
    public static final String RESOURCE_PARENT_IDENTIFIER = "RESOURCE_PARENT_IDENTIFIER";

    public List<PlayerConnection.PlayerServiceConnection> listeners = new ArrayList<>();

    public MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer(this, this, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                String resourceSource = bundle.getString(RESOURCE_SOURCE, null);

                if(resourceSource != null) {
                    switch(resourceSource) {
                        case RESOURCE_LOCAL : {
                            resolveLocalMedia(bundle);

                            break;
                        }
                        case RESOURCE_REMOTE : {
                            resolveRemoteMedia(bundle);

                            break;
                        }
                        default : {
                            Log.v(TAG, "Unknown resource type");
                        }
                    }
                } else {
                    Log.v(TAG, "Unknown command");
                }
            }
        }

        return START_STICKY;
    }

    public void resolveRemoteMedia(Bundle bundle) {
        String resource = bundle.getString(RESOURCE_IDENTIFIER, null);

//        if(resource != null)
//            mediaPlayer.resolve(resource);
    }

    public void resolveLocalMedia(Bundle bundle) {
        String resourceId = bundle.getString(RESOURCE_IDENTIFIER, null);
        String resourceType = bundle.getString(RESOURCE_TYPE, TrackEntry._ID);
        String resourceParentId = bundle.getString(RESOURCE_PARENT_IDENTIFIER, null);
        String resourceParentType = bundle.getString(RESOURCE_PARENT_TYPE, AlbumEntry._ID);

        if(resourceId != null && resourceParentId != null) {
            Bundle loaderBundle = new Bundle();
            loaderBundle.putString(JigglesLoader.BUNDLE_URI_KEY, ContentContract.CONTENT_COLLECTION_URI.toString());
            loaderBundle.putString(JigglesLoader.BUNDLE_SELECTION_KEY, resourceParentType + "=?");
            loaderBundle.putStringArray(JigglesLoader.BUNDLE_SELECTION_ARGS_KEY, new String[] { resourceParentId });

            JigglesLoader.AsyncTaskContentLoader<PlayerContent> asyncTaskContentLoader =
                    new JigglesLoader.AsyncTaskContentLoader<>(this, loaderBundle, PlayerContent::parseCursor);

            asyncTaskContentLoader.registerListener(LOADER_SERVICE_COLLECTION_ID, (loader, store) -> {
                if(store != null) {
//                    store.setPosition(resourceId, resourceType);
//
//                    mediaPlayer.setStore(store);
                }
            });

            asyncTaskContentLoader.forceLoad();
        }
    }

    public void onAttachCallbackListener(PlayerConnection.PlayerServiceConnection playerServiceConnection) {
        listeners.add(playerServiceConnection);
    }

    public void onDetachCallbackListener(PlayerConnection.PlayerServiceConnection playerServiceConnection) {
        listeners.remove(playerServiceConnection);
    }

    public void release() {
        mediaPlayer.release();
    }

    public void requestCallbackCall() {
        PlayerState playerState = mediaPlayer.getPlayerState();

        onPlayerStateChanged(playerState);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void onPlayerStateChanged(PlayerState playerState) {
        for(PlayerConnection.PlayerServiceConnection listener : listeners) {
            listener.onPlayerServiceCallback(this);
        }
    }

    @Override
    public void onManagePlayerNotification(Notification notification) {
        startForeground(SERVICE_NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerBinder();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        stopSelf();
    }

    @Override
    public void onDestroy() {
        release();

        stopSelf();
    }

    public class PlayerBinder extends Binder {

        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
