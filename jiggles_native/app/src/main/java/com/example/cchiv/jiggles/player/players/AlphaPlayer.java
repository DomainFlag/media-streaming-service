package com.example.cchiv.jiggles.player.players;

import android.content.Context;

import com.example.cchiv.jiggles.model.Store;
import com.example.cchiv.jiggles.player.MediaSessionPlayer;

public abstract class AlphaPlayer {

    public interface PlayerStateChanged {
        void onPlayerStateChanged(Store store, Boolean state);
    }

    public boolean state = false;

    private Context context;
    private Store store = null;
    private MediaSessionPlayer mediaSessionPlayer;
    private PlayerStateChanged playerStateChanged;

    public AlphaPlayer(Context context, MediaSessionPlayer mediaSessionPlayer, PlayerStateChanged playerStateChanged) {
        this.context = context;
        this.mediaSessionPlayer = mediaSessionPlayer;
        this.playerStateChanged = playerStateChanged;
    }

    public PlayerStateChanged getPlayerStateChanged() {
        return playerStateChanged;
    }

    public Context getContext() {
        return context;
    }

    public Store getStore() {
        return store;
    }

    public int getMediaPlayerState() {
        return mediaSessionPlayer.getState();
    };

    public void setStore(Store store) {
        this.store = store;
    }

    public abstract void resolve(String uri);
    public abstract void resolve(Store store);
    public abstract void toggle(boolean state);
    public abstract void seek(long position);
    public abstract void skip(boolean next);
    public abstract void release();
}
