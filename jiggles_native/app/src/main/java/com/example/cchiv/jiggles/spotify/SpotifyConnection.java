package com.example.cchiv.jiggles.spotify;

import android.content.Context;
import android.util.Log;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.interfaces.OnTrackStateChanged;
import com.example.cchiv.jiggles.player.MediaSessionPlayer;
import com.example.cchiv.jiggles.services.PlayerService;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

public class SpotifyConnection {

    private static final String TAG = "SpotifyConnection";

    public interface SpotifyResolvedCallback {
        void onSpotifyResolvedCallback(com.example.cchiv.jiggles.model.Track track);
    }

    private static SpotifyAppRemote spotifyAppRemote = null;

    private static PlayerService playerService;
    private MediaSessionPlayer mediaSessionPlayer;
    private static OnTrackStateChanged onTrackStateChanged;

    private Context context;

    public SpotifyConnection(Context context, MediaSessionPlayer mediaSessionPlayer) {
        this.context = context;
        this.playerService = (PlayerService) context;
        this.onTrackStateChanged = (OnTrackStateChanged) context;
        this.mediaSessionPlayer = mediaSessionPlayer;
    }

    public void connect() {
        if(spotifyAppRemote != null)
            return;

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(Constants.CLIENT_ID)
                        .setRedirectUri(Constants.REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        SpotifyConnection.spotifyAppRemote = spotifyAppRemote;

                        resolve();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);
                    }
                });
    }


    private void resolve() {
        spotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    Track track = playerState.track;

                    if(track != null) {
                        com.example.cchiv.jiggles.model.Track trackDecoded = new com.example
                                .cchiv.jiggles.model.Track(track.name, track.uri);

                        trackDecoded.setAlbumName(track.album.name);
                        trackDecoded.setArtistName(track.artist.name);
                    }
                });
    }

    public void play(String uri, SpotifyResolvedCallback spotifyResolvedCallback) {
        if(spotifyAppRemote != null) {
            spotifyAppRemote.getPlayerApi().play(uri);

            spotifyAppRemote
                    .getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(playerState -> {
                        Track track = playerState.track;

                        if(track != null) {
                            com.example.cchiv.jiggles.model.Track trackDecoded = new com.example
                                    .cchiv.jiggles.model.Track(track.name, track.uri);

                            trackDecoded.setAlbumName(track.album.name);
                            trackDecoded.setArtistName(track.artist.name);

                            spotifyAppRemote.getImagesApi().getImage(track.imageUri).setResultCallback(bitmap -> {
                                trackDecoded.setBitmap(bitmap);

                                spotifyResolvedCallback.onSpotifyResolvedCallback(trackDecoded);
                            });
                        }
                    });
        }
    }

    public void toggle(boolean state) {
        if(spotifyAppRemote != null) {
            if(state)
                spotifyAppRemote.getPlayerApi().resume();
            else spotifyAppRemote.getPlayerApi().pause();
        }
    }
}
