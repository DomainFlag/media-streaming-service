package com.example.cchiv.jiggles.player;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.media.session.PlaybackStateCompat;

import com.example.cchiv.jiggles.interfaces.OnTrackStateChanged;
import com.example.cchiv.jiggles.model.Store;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.players.AlphaPlayer;
import com.example.cchiv.jiggles.player.players.LocalPlayer;
import com.example.cchiv.jiggles.player.players.SpotifyPlayer;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;

public class MediaPlayer implements SpotifyPlayer.SpotifyResolvedCallback,
        AlphaPlayer.PlayerStateChanged {

    private static final String TAG = "MediaPlayer";

    public interface PlayerNotificationManager {
        void onManagePlayerNotification(Notification notification);
    };

    private OnTrackStateChanged onTrackStateChanged;

    private Store store = null;

    private MediaSessionPlayer mediaSessionPlayer;
    private PlayerNotificationManager playerNotificationManager;

    // Any remote player - Spotify.
    private SpotifyPlayer remoteAlphaPlayer;

    // Local common player
    private LocalPlayer localAlphaPlayer;

    public MediaPlayer(Context context) {
        this.playerNotificationManager = ((PlayerNotificationManager) context);
        this.onTrackStateChanged = ((OnTrackStateChanged) context);

        mediaSessionPlayer = new MediaSessionPlayer(context,this);
        mediaSessionPlayer.createMediaSession();

        remoteAlphaPlayer = new SpotifyPlayer(context, mediaSessionPlayer, this, this);
        localAlphaPlayer = new LocalPlayer(context, mediaSessionPlayer, this);
    }

    public Track getCurrentTrack() {
        if(store != null)
            return store.getTrack();

        return null;
    }

    public void setResource(String resource) {
        if(remoteAlphaPlayer != null)
            remoteAlphaPlayer.resolve(resource);
    }

    public void setStore(Store store) {
        this.store = store;

        remoteAlphaPlayer.toggle(false);

        localAlphaPlayer.resolve(store);
    }

    public ExoPlayer getExoPlayer() {
        if(localAlphaPlayer != null)
            return localAlphaPlayer.getExoPlayer();

        return null;
    }

    public LocalPlayer getLocalAlphaPlayer() {
        return localAlphaPlayer;
    }

    public SpotifyPlayer getRemoteAlphaPlayer() {
        return remoteAlphaPlayer;
    }

    public Player getCurrentPlayer() {
        if(localAlphaPlayer != null && localAlphaPlayer.state) {
            return localAlphaPlayer.getExoPlayer();
        }

        if(remoteAlphaPlayer != null && remoteAlphaPlayer.state){
            return remoteAlphaPlayer;
        }

        return null;
    };

    public void detachTop() {

    }

    public void togglePlayer(boolean play) {
        if(this.store != null) {
            if(this.store.getTrack().local) {
                localAlphaPlayer.toggle(play);
            } else {
                remoteAlphaPlayer.toggle(play);
            }
        }
    }

    public void seekPlayer(long position) {
        if(this.store != null) {
            if(this.store.getTrack().local) {
                localAlphaPlayer.seek(position);
            } else {
                remoteAlphaPlayer.seek(position);
            }
        }
    }

    public void seekWindow(boolean next) {
        if(this.store != null) {
            if(this.store.getTrack().local) {
                localAlphaPlayer.skip(next);
            } else {
                remoteAlphaPlayer.skip(next);
            }
        }
    }

    public void release() {
        if(remoteAlphaPlayer != null) {
            remoteAlphaPlayer.release();
            remoteAlphaPlayer = null;
        }

        if(localAlphaPlayer != null) {
            localAlphaPlayer.release();
            localAlphaPlayer = null;
        }
    }

    public int getPlaybackState() {
        return mediaSessionPlayer.getState();
    }

    public Store getStore() {
        return store;
    }

    @Override
    public void onSpotifyResolvedCallback(@NonNull Store store, boolean isPaused) {
        boolean contentEqual = false;
        if(this.store != null)
            contentEqual = this.store.getTrack().getUri().equals(store.getTrack().getUri());

        remoteAlphaPlayer.state = !isPaused;

        if(this.store == null) {
            onPlayerStateChanged(store, !isPaused);
        } else if(contentEqual) {
            onPlayerStateChanged(store, !isPaused);
        } else if(!isPaused) {
            if(localAlphaPlayer.getState())
                localAlphaPlayer.toggle(false);

            onPlayerStateChanged(store, true);
        }
    }

    @Override
    public void onSpotifyResolvedCallback(@NonNull Bitmap bitmap) {
        if(!this.store.getTrack().local) {
            this.store.getTrack().setBitmap(bitmap);

            onPlayerStateChanged(this.store, null);
        }
    }

    @Override
    public void onPlayerStateChanged(Store store, Boolean playbackState) {
        this.store = store;

        if(playbackState != null) {
            int state = playbackState ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;

            mediaSessionPlayer.setState(state);
        }

        onTrackStateChanged.onTrackStateChanged(getCurrentTrack());

        Notification notification = mediaSessionPlayer.buildNotificationPlayer(store);
        playerNotificationManager.onManagePlayerNotification(notification);
    }
}