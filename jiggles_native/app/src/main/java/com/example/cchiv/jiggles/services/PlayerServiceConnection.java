package com.example.cchiv.jiggles.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.MediaPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerServiceConnection implements ServiceConnection {

    private static final String TAG = "PlayerServiceConnection";

    private Context context;

    private PlayerService.OnCallbackListener onCallbackListener;

    public interface OnCallbackConnectionComplete {
        void onCallbackConnectionComplete();
    }

    private OnCallbackConnectionComplete onCallbackConnectionComplete;

    private PlayerService playerService = null;
    private PlayerView playerView = null;

    public PlayerServiceConnection(Context context) {
        this.context = context;
        this.onCallbackListener = (PlayerService.OnCallbackListener) context;
        this.onCallbackConnectionComplete = (OnCallbackConnectionComplete) context;
    }

    public PlayerServiceConnection(Context context, PlayerView playerView) {
        this(context);
        this.playerView = playerView;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        PlayerService.PlayerBinder playerBinder = (PlayerService.PlayerBinder) iBinder;

        playerService = playerBinder.getService();
        playerService.onAttachCallbackListener(onCallbackListener);

        if(playerView != null) {
            playerView.setPlayer(getMediaPlayer().getExoPlayer());
        }

        onCallbackConnectionComplete.onCallbackConnectionComplete();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.v(TAG, "Disconnected: " + componentName.getClassName());
    }

    public void onStartService(Bundle bundle) {
        Intent serviceIntent = new Intent(context, PlayerService.class);
        if(bundle != null)
            serviceIntent.putExtras(bundle);

        context.startService(serviceIntent);
        context.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    public void onAttachPlayerView(PlayerView playerView) {
        this.playerView = playerView;
    }

    public void release() {
        onDetachPlayerView();

        playerService.onDetachCallbackListener(onCallbackListener);
        playerService.onRelease();
    }

    public MediaPlayer getMediaPlayer() {
        return playerService.getMediaPlayer();
    }

    public void onDetachPlayerView() {
        if(playerView != null) {
            MediaPlayer mediaPlayer = getMediaPlayer();

            PlayerView.switchTargetView(mediaPlayer.getExoPlayer(), playerView, null);
        }
    }

    public Track getCurrentTrack() {
        if(playerService != null)
            return playerService.getCurrentTrack();
        else return null;
    }
}
