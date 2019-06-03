package com.example.cchiv.jiggles.player;

import android.app.Notification;
import android.content.Context;
import android.util.SparseArray;

import com.example.cchiv.jiggles.model.player.PlayerState;
import com.example.cchiv.jiggles.player.players.AlphaPlayer;
import com.example.cchiv.jiggles.player.players.LocalPlayer;
import com.example.cchiv.jiggles.player.players.SpotifyPlayer;
import com.google.android.exoplayer2.Player;

public class MediaPlayer extends AlphaPlayer implements AlphaPlayer.PlayerStateChanged {

    private static final String TAG = "MediaPlayer";

    public interface PlayerNotificationManager {
        void onManagePlayerNotification(Notification notification);
    };

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

        PlayerState playerState = new PlayerState();
        setPlayerState(playerState);

        AlphaPlayer spotifyPlayer = new SpotifyPlayer(context, this);
        spotifyPlayer.setPlayerState(playerState);
        players.append(spotifyPlayer.getIdentifier(), spotifyPlayer);

        AlphaPlayer localPlayer = new LocalPlayer(context, this);
        localPlayer.setPlayerState(playerState);
        players.append(localPlayer.getIdentifier(), localPlayer);
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

    public void requestPlayerFocus(int identifier) {
        AlphaPlayer alphaPlayer = players.get(identifier);

        requestPlayerFocus(alphaPlayer);
    }

    @Override
    public void requestPlayerFocus(AlphaPlayer alphaPlayer) {
        if(player != null) {
            int key = alphaPlayer.getIdentifier();

            if(key != player.getIdentifier()) {
                player.toggle(false);
            }
        }

        player = alphaPlayer;
        player.resolve(getPlayerState());

        onPlayerStateChanged(alphaPlayer.getPlayerState());
    }

    @Override
    public void onPlayerStateChanged(PlayerState playerState) {
        mediaSessionPlayer.setState(getPlayerState().getPlaybackState());

        Notification notification = mediaSessionPlayer.buildNotificationPlayer(playerState);
        playerNotificationManager.onManagePlayerNotification(notification);

        setPlayerState(playerState);
        playerStateChanged.onPlayerStateChanged(playerState);
    }
}

