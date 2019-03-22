package com.example.cchiv.jiggles.player.players;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.model.Store;
import com.example.cchiv.jiggles.player.MediaSessionPlayer;
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
import com.spotify.protocol.types.Track;

public class SpotifyPlayer extends AlphaPlayer implements Player {

    private static final String TAG = "SpotifyPlayer";

    public interface SpotifyResolvedCallback {
        void onSpotifyResolvedCallback(@NonNull Store store, boolean isPaused);
        void onSpotifyResolvedCallback(@NonNull Bitmap bitmap);
    }

    private static SpotifyAppRemote spotifyAppRemote = null;

    private EventListener listener = null;

    public SpotifyPlayer(Context context, MediaSessionPlayer mediaSessionPlayer, PlayerStateChanged playerStateChanged,
                         SpotifyResolvedCallback spotifyResolvedCallback) {
        super(context, mediaSessionPlayer, playerStateChanged);

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

                                        if(getStore() == null || getStore() != null && !track.uri.equals(getStore().getTrack().getUri())) {
                                            setStore(store);

                                            resolveImageUri(track.imageUri, spotifyResolvedCallback);
                                        } else {
                                            store = getStore();
                                        }

                                        store.lastPlaybackPosition = System.currentTimeMillis();
                                        store.playbackPosition = playerState.playbackPosition;
                                        store.duration = track.duration;

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
            if(state) {
                spotifyAppRemote.getPlayerApi().resume();
            } else {
                spotifyAppRemote.getPlayerApi().pause();
            }

            this.state = state;

            this.listener.onPlayerStateChanged(state, Player.STATE_READY);
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
        this.listener = listener;
    }

    @Override
    public void removeListener(EventListener listener) {
        Log.v(TAG, "Listener is removed");
    }

    @Override
    public int getPlaybackState() {
        return Player.STATE_READY;
    }

    @Nullable
    @Override
    public ExoPlaybackException getPlaybackError() {
        return null;
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        if(this.listener != null) {
            toggle(playWhenReady);
        }
    }

    @Override
    public boolean getPlayWhenReady() {
        return state;
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

    }

    @Override
    public boolean getShuffleModeEnabled() {
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
        TrackSelection trackSelectionArray[] = new TrackSelection[0];

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
                window.durationUs = getStore().duration * 1000;
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
        return 0;
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
        return getStore().duration;
    }

    @Override
    public long getCurrentPosition() {
        // Current Position of playback
        long playbackPosition = getStore().playbackPosition;

        if(state) {
            long current, offset;

            current = System.currentTimeMillis();
            offset = current - getStore().lastPlaybackPosition;

            getStore().playbackPosition += offset;

            getStore().lastPlaybackPosition = current;
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
        return getStore().playbackPosition;
    }
}
