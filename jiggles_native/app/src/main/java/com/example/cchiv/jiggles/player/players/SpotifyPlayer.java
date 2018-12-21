package com.example.cchiv.jiggles.player.players;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.model.Store;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Track;

public class SpotifyPlayer extends AlphaPlayer {

    private static final String TAG = "SpotifyPlayer";

    public interface SpotifyResolvedCallback {
        void onSpotifyResolvedCallback(@NonNull Store store, boolean isPaused);
        void onSpotifyResolvedCallback(@NonNull Bitmap bitmap);
    }

    private static SpotifyAppRemote spotifyAppRemote = null;

    public SpotifyPlayer(Context context, PlayerStateChanged playerStateChanged,
                         SpotifyResolvedCallback spotifyResolvedCallback) {
        super(context, playerStateChanged);

        connect(spotifyResolvedCallback);
    }

    private void connect(SpotifyResolvedCallback spotifyResolvedCallback) {
        if(spotifyAppRemote != null)
            return;

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(Constants.CLIENT_ID)
                        .setRedirectUri(Constants.REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(getContext(), connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        SpotifyPlayer.spotifyAppRemote = spotifyAppRemote;

                        spotifyAppRemote
                                .getPlayerApi()
                                .subscribeToPlayerState()
                                .setEventCallback(playerState -> {
                                    Track track = playerState.track;

                                    if(track != null) {
                                        Store store = Store.resolveRemoteStore(
                                                track.name,
                                                track.uri,
                                                track.album.name,
                                                track.artist.name
                                        );

                                        if(getStore() == null || getStore() != null &&
                                                !track.uri.equals(getStore().getTrack().getUri())) {
                                            setStore(store);

                                            resolveImageUri(track.imageUri, spotifyResolvedCallback);
                                        } else {
                                            store = getStore();
                                        }

                                        spotifyResolvedCallback.onSpotifyResolvedCallback(store, playerState.isPaused);
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);
                    }
                });
    }

    public void resolveImageUri(ImageUri imageUri, SpotifyResolvedCallback spotifyResolvedCallback) {
        if(spotifyAppRemote != null) {
            spotifyAppRemote
                    .getImagesApi()
                    .getImage(imageUri)
                    .setResultCallback(spotifyResolvedCallback::onSpotifyResolvedCallback);
        }
    }

    @Override
    public void resolve(Store store) {}

    @Override
    public void resolve(String uri) {
        if(spotifyAppRemote != null) {
            spotifyAppRemote.getPlayerApi().play(uri);
        }
    }

    @Override
    public void toggle(boolean state) {
        if(spotifyAppRemote != null) {
            if(state)
                spotifyAppRemote.getPlayerApi().resume();
            else spotifyAppRemote.getPlayerApi().pause();
        }
    }

    @Override
    public void seek(long position) {
        if(spotifyAppRemote != null)
            spotifyAppRemote.getPlayerApi().seekTo(position);
    }

    @Override
    public void release() {

    }
}
