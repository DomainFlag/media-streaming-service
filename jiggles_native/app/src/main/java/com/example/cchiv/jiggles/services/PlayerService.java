package com.example.cchiv.jiggles.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.example.cchiv.jiggles.data.ContentContract;
import com.example.cchiv.jiggles.data.ContentContract.AlbumEntry;
import com.example.cchiv.jiggles.data.ContentContract.TrackEntry;
import com.example.cchiv.jiggles.interfaces.OnTrackStateChanged;
import com.example.cchiv.jiggles.model.Store;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.MediaPlayer;
import com.example.cchiv.jiggles.player.MediaSessionPlayer;
import com.example.cchiv.jiggles.spotify.SpotifyConnection;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

import java.util.ArrayList;
import java.util.List;

public class PlayerService extends Service implements OnTrackStateChanged {

    private static final String TAG = "PlayerService";

    public static final int SERVICE_NOTIFICATION_ID = 9921;

    private static final int LOADER_SERVICE_COLLECTION_ID = 1021;

    public interface OnCallbackListener {
        void onCallbackListener(Track track, int playbackStateCompat);
    }

    public static final String RESOURCE_SOURCE = "RESOURCE_SOURCE";
    public static final String RESOURCE_LOCAL = "RESOURCE_LOCAL";
    public static final String RESOURCE_REMOTE = "RESOURCE_REMOTE";


    public static final String RESOURCE_TYPE = "RESOURCE_TYPE";
    public static final String RESOURCE_IDENTIFIER = "RESOURCE_IDENTIFIER";
    public static final String RESOURCE_PARENT_TYPE = "RESOURCE_PARENT_TYPE";
    public static final String RESOURCE_PARENT_IDENTIFIER = "RESOURCE_PARENT_IDENTIFIER";

    public List<OnCallbackListener> listeners = new ArrayList<>();

    private Track track = null;

    public SpotifyConnection spotifyConnection;
    public MediaPlayer mediaPlayer;
    public MediaSessionPlayer mediaSessionPlayer;

    private int state = -1;

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer(this);

        mediaSessionPlayer = new MediaSessionPlayer(this,  mediaPlayer);
        mediaSessionPlayer.createMediaSession();

        spotifyConnection = new SpotifyConnection(this, mediaSessionPlayer);
        spotifyConnection.connect();
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
                }
            }
        }

        return START_STICKY;
    }

    public void resolveRemoteMedia(Bundle bundle) {
        String resourceId = bundle.getString(RESOURCE_IDENTIFIER, null);

        if(resourceId != null)
            spotifyConnection.play(resourceId, track -> {
                this.track = track;

                if(state == -1)
                    state = PlaybackStateCompat.STATE_PLAYING;

                mediaSessionPlayer.setState(state);

                setForegroundService(track);
                onTrackNotifyAll(track);
            });
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

            JigglesLoader.AsyncTaskContentLoader<Store> asyncTaskContentLoader =
                    new JigglesLoader.AsyncTaskContentLoader<>(this, loaderBundle, Store::parseCursor);

            asyncTaskContentLoader.registerListener(LOADER_SERVICE_COLLECTION_ID, (loader, store) -> {
                if(store != null) {
                    mediaPlayer.setPlayer(mediaSessionPlayer);
                    mediaPlayer.setSource(store, store.getPosition(resourceId, resourceType));

                    Track track = getCurrentTrack();
                    setForegroundService(track);
                }
            });

            asyncTaskContentLoader.forceLoad();
        }
    }

    public void togglePlayer(boolean state) {
        this.state = state ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;

        if(!state) {
            mediaSessionPlayer.setState(PlaybackStateCompat.STATE_PAUSED);
        } else {
            mediaSessionPlayer.setState(PlaybackStateCompat.STATE_PLAYING);
        }

        mediaSessionPlayer.buildNotificationPlayer(track);
        onTrackStateChanged(track);

        spotifyConnection.toggle(state);
    }

    public class PlayerBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    public void setForegroundService(Track track) {
        Notification notification = mediaSessionPlayer
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
        mediaSessionPlayer.setActive(false);
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
            listener.onCallbackListener(track, mediaSessionPlayer.getState());
        }
    }

    @Override
    public void onTrackStateChanged(Track track) {
        onTrackNotifyAll(track);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public MediaSessionPlayer getMediaSessionPlayer() {
        return mediaSessionPlayer;
    }

    public Track getCurrentTrack() {
        return mediaPlayer.getCurrentTrack();
    }

    public int getPlaybackStateCompat() {
        return mediaSessionPlayer.getState();
    }

    public SpotifyConnection getSpotifyConnection() {
        return spotifyConnection;
    }
}
