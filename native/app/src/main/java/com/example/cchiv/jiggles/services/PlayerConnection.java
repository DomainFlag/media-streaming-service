package com.example.cchiv.jiggles.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.cchiv.jiggles.player.MediaPlayer;

public class PlayerConnection implements ServiceConnection {

    public interface PlayerServiceConnection {
        void onPlayerServiceCallback(PlayerService playerService);
    }

    private static final String TAG = "PlayerConnection";

    private Context context;

    private PlayerServiceConnection playerServiceConnection;
    private PlayerService playerService = null;

    public PlayerConnection(Context context) {
        this.context = context;
        this.playerServiceConnection = (PlayerServiceConnection) context;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        PlayerService.PlayerBinder playerBinder = (PlayerService.PlayerBinder) iBinder;

        playerService = playerBinder.getService();
        playerService.onAttachCallbackListener(playerServiceConnection);

        playerServiceConnection.onPlayerServiceCallback(playerService);
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

    public void release() {
        playerService.onDetachCallbackListener(playerServiceConnection);

        context.unbindService(this);
    }

    public MediaPlayer getMediaPlayer() {
        if(playerService != null)
            return playerService.getMediaPlayer();

        return null;
    }

    public void requestCallbackCall() {
        playerService.requestCallbackCall();
    }
}
