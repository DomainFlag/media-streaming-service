package com.example.cchiv.jiggles.player.players;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.model.player.PlayerContent;
import com.example.cchiv.jiggles.model.player.PlayerState;
import com.example.cchiv.jiggles.player.core.AlphaPlayer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Repeat;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

public class SpotifyPlayer extends AlphaPlayer implements Player {

    public interface SpotifyResolvedCallback {
        void onSpotifyResolvedCallback(@NonNull PlayerContent playerContent, boolean isPaused);
        void onSpotifyResolvedCallback(@NonNull Bitmap bitmap);
    }

    private static final String TAG = "SpotifyPlayer";

    private static final int PLAYER_ID = 1521;

    private static SpotifyAppRemote spotifyAppRemote = null;

    private List<EventListener> listeners = new ArrayList<>();

    public SpotifyPlayer(Context context, PlayerStateChanged playerStateChanged, SpotifyResolvedCallback spotifyResolvedCallback) {
        super(context, PLAYER_ID, playerStateChanged);

        SpotifyPlayer.connect(this, spotifyResolvedCallback);
    }

    private static void connect(SpotifyPlayer spotifyPlayer, SpotifyResolvedCallback spotifyResolvedCallback) {
        if(spotifyAppRemote != null && spotifyAppRemote.isConnected())
            return;

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(Constants.CLIENT_ID)
                        .setRedirectUri(Constants.REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(spotifyPlayer.getContext(), connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        SpotifyPlayer.spotifyAppRemote = spotifyAppRemote;

                        spotifyAppRemote
                                .getPlayerApi()
                                .subscribeToPlayerState()
                                .setEventCallback(playerState -> {
                                    SpotifyPlayer.resolveSpotifyConnection(spotifyPlayer, playerState, spotifyResolvedCallback);
                                });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.v(TAG, throwable.getMessage() + "; trying to re-establish connection");

                        SpotifyPlayer.connect(spotifyPlayer, spotifyResolvedCallback);
                    }
                });
    }

    private static void resolveSpotifyConnection(SpotifyPlayer spotifyPlayer, com.spotify.protocol.types.PlayerState playerState,
                                                 SpotifyResolvedCallback spotifyResolvedCallback) {
        Track track = playerState.track;

        if(track != null) {
            PlayerContent playerContent = PlayerContent.resolveRemoteStore(
                    track.name,
                    track.uri,
                    track.album.name,
                    track.artist.name
            );

            long lastPlaybackPosition = System.currentTimeMillis();
            spotifyPlayer.getPlayerState().setLastPlaybackPosition(lastPlaybackPosition);

            spotifyPlayer.getPlayerState().setPlaybackPosition(playerState.playbackPosition);
            spotifyPlayer.getPlayerState().setDuration(track.duration);
            spotifyPlayer.getPlayerState().setPlayerContent(playerContent);

            spotifyResolvedCallback.onSpotifyResolvedCallback(playerContent, playerState.isPaused);
            SpotifyPlayer.resolveImageUri(track.imageUri, spotifyResolvedCallback);
        }
    }

    private static void resolveImageUri(ImageUri imageUri, SpotifyResolvedCallback spotifyResolvedCallback) {
        if(spotifyAppRemote != null) {
            spotifyAppRemote
                    .getImagesApi()
                    .getImage(imageUri)
                    .setResultCallback(spotifyResolvedCallback::onSpotifyResolvedCallback)
                    .setErrorCallback(errorCallback -> {
                        Log.v(TAG, errorCallback.getCause().getMessage() + "; trying to fetch the image again");

                        resolveImageUri(imageUri, spotifyResolvedCallback);
                    });
        }
    }

    @Override
    public Player getPlayer() {
        return this;
    }

    @Override
    public void resolve(PlayerState playerState) {
        PlayerContent playerContent = playerState.getPlayerContent();

        if(spotifyAppRemote != null && playerContent != null && playerContent.getUri() != null) {
            spotifyAppRemote.getPlayerApi().play(playerContent.getUri());
        }
    }

    @Override
    public void toggle(boolean state) {
        if(spotifyAppRemote != null) {
            if(state) {
                spotifyAppRemote.getPlayerApi().resume();
            } else {
                spotifyAppRemote.getPlayerApi().pause();
            }
        }
    }

    @Override
    public void seek(long position) {
        if(spotifyAppRemote != null)
            spotifyAppRemote.getPlayerApi().seekTo(position);
    }

    @Override
    public void skip(boolean next) {
        if(spotifyAppRemote != null) {
            if(next) {
                spotifyAppRemote.getPlayerApi().skipNext();
            } else {
                spotifyAppRemote.getPlayerApi().skipPrevious();
            }
        }
    }

    @Override
    public void repeat(int repeatMode) {
        if(spotifyAppRemote != null) {
            int localRepeatMode = this.getLocalRepeatMode(repeatMode);

            spotifyAppRemote.getPlayerApi().setRepeat(localRepeatMode);
        }
    }

    @Override
    public void shuffle(boolean shuffle) {
        if(spotifyAppRemote != null) {
            spotifyAppRemote.getPlayerApi().setShuffle(shuffle);
        }
    }

    @Nullable
    @Override
    public VideoComponent getVideoComponent() {
        return null;
    }

    @Nullable
    @Override
    public TextComponent getTextComponent() {
        return null;
    }

    @Override
    public void addListener(EventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(EventListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public int getPlaybackState() {
        return getPlayerState().getPlaybackState();
    }

    @Nullable
    @Override
    public ExoPlaybackException getPlaybackError() {
        return null;
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        this.toggle(playWhenReady);

        for(EventListener listener : this.listeners) {
            listener.onPlayerStateChanged(playWhenReady, Player.STATE_READY);
        }
    }

    @Override
    public boolean getPlayWhenReady() {
        return getPlayerState().isPlaying;
    }

    @Override
    public void setRepeatMode(int repeatMode) {

    }

    @Override
    public int getRepeatMode() {
        return Player.REPEAT_MODE_ALL;
    }

    @Override
    public void setShuffleModeEnabled(boolean shuffleModeEnabled) {
        spotifyAppRemote.getPlayerApi().setShuffle(shuffleModeEnabled);
    }

    @Override
    public boolean getShuffleModeEnabled() {
        // TODO(21) shuffle
        return false;
    }

    @Override
    public boolean isLoading() {
        return false;
    }

    @Override
    public void seekToDefaultPosition() {

    }

    @Override
    public void seekToDefaultPosition(int windowIndex) {

    }

    @Override
    public void seekTo(long positionMs) {
        Log.v(TAG, "Position:" + String.valueOf(positionMs));
    }

    @Override
    public void seekTo(int windowIndex, long positionMs) {
        seek(positionMs);
    }

    @Override
    public void setPlaybackParameters(@Nullable PlaybackParameters playbackParameters) {
    }

    @Override
    public PlaybackParameters getPlaybackParameters() {
        return new PlaybackParameters(1.0f);
    }

    @Override
    public void stop() {
        Log.v(TAG, "STOP");
    }

    @Override
    public void stop(boolean reset) {
        Log.v(TAG, "RESET");
    }

    @Override
    public void release() {
        Log.v(TAG, "RELEASE");
    }

    @Override
    public int getRepeatMode(int repeatMode) {
        switch(repeatMode) {
            case Repeat.ONE :
                return Player.REPEAT_MODE_ONE;

            case Repeat.ALL :
                return Player.REPEAT_MODE_ALL;

            default :
                return Player.REPEAT_MODE_OFF;
        }
    }

    @Override
    public int getLocalRepeatMode(int repeatMode) {
        switch(repeatMode) {
            case Player.REPEAT_MODE_ONE : {
                return Repeat.ONE;
            }
            case Player.REPEAT_MODE_ALL : {
                return Repeat.ALL;
            }
            default :
                return Repeat.OFF;
        }
    }

    @Override
    public int getRendererCount() {
        return 1;
    }

    @Override
    public int getRendererType(int index) {
        return Renderer.STATE_ENABLED;
    }

    @Override
    public TrackGroupArray getCurrentTrackGroups() {
        return new TrackGroupArray(new TrackGroup(Format.createAudioSampleFormat(
                "21",
                "mp3",
                "All I need",
                44100,
                20000,
                2,
                441000,
                null,
                null,
                C.SELECTION_FLAG_AUTOSELECT,
                null
        )));
    }

    @Override
    public TrackSelectionArray getCurrentTrackSelections() {
        TrackSelection[] trackSelectionArray = new TrackSelection[0];

        return new TrackSelectionArray(trackSelectionArray);
    }

    @Nullable
    @Override
    public Object getCurrentManifest() {
        return null;
    }

    @Override
    public Timeline getCurrentTimeline() {

        return new Timeline() {
            @Override
            public int getWindowCount() {
                return 1;
            }

            @Override
            public Window getWindow(int windowIndex, Window window, boolean setTag, long defaultPositionProjectionUs) {
                window.firstPeriodIndex = 0;
                window.lastPeriodIndex = -1;
                window.defaultPositionUs = 0;
                window.durationUs = getPlayerState().getDuration() * 1000;
                window.isSeekable = true;

                return window;
            }

            @Override
            public int getPeriodCount() {
                return 0;
            }

            @Override
            public Period getPeriod(int periodIndex, Period period, boolean setIds) {
                return null;
            }

            @Override
            public int getIndexOfPeriod(Object uid) {
                return 0;
            }
        };
    }

    @Override
    public int getCurrentPeriodIndex() {
        return -1;
    }

    @Override
    public int getCurrentWindowIndex() {
        return 0;
    }

    @Override
    public int getNextWindowIndex() {
        return 0;
    }

    @Override
    public int getPreviousWindowIndex() {
        return 0;
    }

    @Nullable
    @Override
    public Object getCurrentTag() {
        return null;
    }

    @Override
    public long getDuration() {
        return this.getPlayerState().getDuration();
    }

    @Override
    public long getCurrentPosition() {
        PlayerState playerState = this.getPlayerState();

        long playbackPosition = playerState.getPlaybackPosition();

        if(getPlayerState().isPlaying) {
            long current, offset;

            current = System.currentTimeMillis();
            offset = current - playerState.getLastPlaybackPosition();

            playerState.setPlaybackPosition(playerState.getPlaybackPosition() + offset);
            playerState.setLastPlaybackPosition(current);
        }

        return playbackPosition;
    }

    @Override
    public long getBufferedPosition() {
        return 0;
    }

    @Override
    public int getBufferedPercentage() {
        return 0;
    }

    @Override
    public boolean isCurrentWindowDynamic() {
        return false;
    }

    @Override
    public boolean isCurrentWindowSeekable() {
        return true;
    }

    @Override
    public boolean isPlayingAd() {
        return false;
    }

    @Override
    public int getCurrentAdGroupIndex() {
        return 0;
    }

    @Override
    public int getCurrentAdIndexInAdGroup() {
        return 0;
    }

    @Override
    public long getContentPosition() {
        return this.getPlayerState().getPlaybackPosition();
    }
}
