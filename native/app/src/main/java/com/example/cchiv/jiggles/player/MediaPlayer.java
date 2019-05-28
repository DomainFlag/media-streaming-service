package com.example.cchiv.jiggles.player;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.example.cchiv.jiggles.model.player.PlayerContent;
import com.example.cchiv.jiggles.model.player.PlayerState;
import com.example.cchiv.jiggles.model.player.content.Track;
import com.example.cchiv.jiggles.player.core.AlphaPlayer;
import com.example.cchiv.jiggles.player.players.LocalPlayer;
import com.example.cchiv.jiggles.player.players.SpotifyPlayer;
import com.google.android.exoplayer2.Player;

public class MediaPlayer extends AlphaPlayer implements SpotifyPlayer.SpotifyResolvedCallback,
        AlphaPlayer.PlayerStateChanged {

    private static final String TAG = "MediaPlayer";

    public interface PlayerNotificationManager {
        void onManagePlayerNotification(Notification notification);
    };

    private PlayerContent playerContent = null;

    private MediaSessionPlayer mediaSessionPlayer;

    private PlayerStateChanged playerStateChanged;
    private PlayerNotificationManager playerNotificationManager;

    private AlphaPlayer player = null;

    private SparseArray<AlphaPlayer> players = new SparseArray<>();

    public MediaPlayer(Context context, PlayerNotificationManager playerNotificationManager, PlayerStateChanged playerStateChanged) {
        super(context, -1, null);

        this.playerNotificationManager = playerNotificationManager;
        this.playerStateChanged = playerStateChanged;

        this.mediaSessionPlayer = new MediaSessionPlayer(context,this);
        this.mediaSessionPlayer.createMediaSession();

        AlphaPlayer spotifyPlayer = new SpotifyPlayer(context, this, this);
        this.players.append(spotifyPlayer.getIdentifier(), spotifyPlayer);

        AlphaPlayer localPlayer = new LocalPlayer(context, this);
        this.players.append(localPlayer.getIdentifier(), localPlayer);
    }

    public void requestPlayerFocus(AlphaPlayer alphaPlayer) {
        int key = alphaPlayer.getIdentifier();

        AlphaPlayer alphaPlayingPlayer = this.players.get(key);
        if(alphaPlayingPlayer != alphaPlayer) {
            this.player = alphaPlayer;
        }
    }

    @Override
    public Player getPlayer() {
        if(player != null) {
            return player.getPlayer();
        }

        return null;
    }

    @Override
    public void resolve(PlayerState playerState) {
        this.player.resolve(playerState);
    }

    @Override
    public void toggle(boolean state) {
        this.player.toggle(state);
    }

    @Override
    public void seek(long position) {
        this.player.seek(position);
    }

    @Override
    public void skip(boolean next) {
        this.player.skip(next);
    }

    @Override
    public void repeat(int repeatMode) {
        this.player.repeat(repeatMode);
    }

    @Override
    public void shuffle(boolean shuffle) {
        this.player.shuffle(shuffle);
    }

    public void release() {
        this.player = null;

        for(int g = 0; g < this.players.size(); g++) {
            int key = this.players.keyAt(g);

            this.players.get(key).release();
        }

        this.players.clear();
    }

    @Override
    public int getRepeatMode(int repeatMode) {
        return 0;
    }

    @Override
    public int getLocalRepeatMode(int repeatMode) {
        return 0;
    }

    public int getPlaybackState() {
        return mediaSessionPlayer.getState();
    }

    @Override
    public void onSpotifyResolvedCallback(@NonNull PlayerContent playerContent, boolean isPaused) {
        boolean contentEqual = false;

        PlayerState playerState = getPlayerState();
        Track track = playerState.getPlayerContent().getTrack(playerState.getPosition());

        if(this.playerContent != null)
            contentEqual = track.getUri().equals(playerContent.getTrack(0).getUri());

//        if(this.playerContent == null) {
//            onPlayerStateChanged(playerContent, !isPaused);
//        } else if(contentEqual) {
//            onPlayerStateChanged(playerContent, !isPaused);
//        } else if(!isPaused) {
//            if(localAlphaPlayer.getState())
//                localAlphaPlayer.toggle(false);
//
//            onPlayerStateChanged(playerContent, true);
//        }
    }

    @Override
    public void onSpotifyResolvedCallback(@NonNull Bitmap bitmap) {
        PlayerState playerState = getPlayerState();
        PlayerContent playerContent = playerState.getPlayerContent();

        Track track = playerContent.getTrack(playerState.getPosition());
        if(!track.local) {
            track.setBitmap(bitmap);

            onPlayerStateChanged(playerState);
        }
    }

    @Override
    public void onPlayerStateChanged(PlayerState playerState) {
        if(this.getPlayerState().getPlaybackState() != playerState.getPlaybackState()) {
            mediaSessionPlayer.setState(this.getPlayerState().getPlaybackState());
        }

        Notification notification = mediaSessionPlayer.buildNotificationPlayer(playerState);
        playerNotificationManager.onManagePlayerNotification(notification);

        this.setPlayerState(playerState);
    }
}

