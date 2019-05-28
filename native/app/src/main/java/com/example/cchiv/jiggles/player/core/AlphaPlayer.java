package com.example.cchiv.jiggles.player.core;

import android.content.Context;

import com.example.cchiv.jiggles.model.player.PlayerState;
import com.google.android.exoplayer2.Player;

public abstract class AlphaPlayer {

    public interface PlayerStateChanged {
        void onPlayerStateChanged(PlayerState playerState);
    }

    private Context context;
    private PlayerState playerState = null;
    private PlayerStateChanged playerStateChanged;

    private final int identifier;

    public AlphaPlayer(Context context, int identifier, PlayerStateChanged playerStateChanged) {
        this.context = context;
        this.identifier = identifier;
        this.playerStateChanged = playerStateChanged;
    }

    public Context getContext() {
        return context;
    }

    public PlayerStateChanged getPlayerStateChanged() {
        return playerStateChanged;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public abstract Player getPlayer();
    public abstract void resolve(PlayerState playerState);
    public abstract void toggle(boolean state);
    public abstract void seek(long position);
    public abstract void skip(boolean next);
    public abstract void repeat(int repeatMode);
    public abstract void shuffle(boolean shuffle);
    public abstract void release();

    public abstract int getRepeatMode(int repeatMode);
    public abstract int getLocalRepeatMode(int repeatMode);
}
