package com.example.cchiv.jiggles.player.players;

import android.content.Context;
import android.media.audiofx.Visualizer;
import android.net.Uri;

import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Store;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.listeners.PlayerAnalyticsListener;
import com.example.cchiv.jiggles.player.listeners.PlayerEventListener;
import com.example.cchiv.jiggles.player.protocol.RemotePlayer;
import com.example.cchiv.jiggles.player.tools.DataFetcher;
import com.example.cchiv.jiggles.player.tools.RemoteDataSourceFactory;
import com.example.cchiv.jiggles.utilities.VisualizerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.PipedInputStream;

public class LocalPlayer extends AlphaPlayer {

    private static final String TAG = "LocalPlayer";

    private VisualizerView visualizerView = null;
    private DataFetcher dataFetcher = null;

    private SimpleExoPlayer exoPlayer = null;
    private RemotePlayer remotePlayer;

    public LocalPlayer(Context context, PlayerStateChanged playerStateChanged) {
        super(context, playerStateChanged);

        this.remotePlayer = new RemotePlayer(getContext(), this);
    }

    private void setPlayer() {
        if(exoPlayer != null)
            release();

        DefaultAllocator allocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setAllocator(allocator)
                .setBufferDurationsMs(400000, 400000, 1500, 1500)
                .createDefaultLoadControl();
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector();

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());

        exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, defaultTrackSelector, loadControl);
        exoPlayer.addAnalyticsListener(new PlayerAnalyticsListener() {
            @Override
            public void onPositionDiscontinuity(EventTime eventTime, int reason) {
                if(reason == ExoPlayer.DISCONTINUITY_REASON_PERIOD_TRANSITION ||
                        reason == ExoPlayer.DISCONTINUITY_REASON_SEEK_ADJUSTMENT) {
                    getStore().setPosition(exoPlayer.getCurrentWindowIndex());

                    getPlayerStateChanged().onPlayerStateChanged(getStore(), exoPlayer.getPlayWhenReady());
                }
            }

            @Override
            public void onSeekProcessed(EventTime eventTime) {
                if(remotePlayer != null) {
//                    remotePlayer.onStateChanged(RemotePlayer.RemoteAction.ACTION_SEEK, eventTime.currentPlaybackPositionMs);
                }
            }

            @Override
            public void onAudioSessionId(EventTime eventTime, int audioSessionId) {
                setUpVisualizer(visualizerView, audioSessionId);
            }
        });

        exoPlayer.addListener(new PlayerEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                // Changing the state for the remote player
                if(remotePlayer != null && remotePlayer.isEnabled()) {
                    if((playbackState == Player.STATE_READY) && playWhenReady) {
                        remotePlayer.onStateChanged(RemotePlayer.RemoteAction.ACTION_RESUME, -1);
                    } else if(playbackState == Player.STATE_READY) {
                        remotePlayer.onStateChanged(RemotePlayer.RemoteAction.ACTION_PAUSE, -1);
                    }
                }

                triggerMediaSession(playbackState, playWhenReady);
            }
        });
    }

    public boolean getState() {
        return exoPlayer != null && exoPlayer.getPlayWhenReady();

    }

    public RemotePlayer getRemotePlayer() {
        return remotePlayer;
    }

    private void triggerMediaSession(int playbackState, boolean playWhenReady) {
        boolean state = false;
        if((playbackState == Player.STATE_READY) && playWhenReady) {
            state = true;
        } else if((playbackState == Player.STATE_READY)) {
            state = false;
        }

        getPlayerStateChanged().onPlayerStateChanged(getStore(), state);
    }

    // Setting up stream source
    public void setStreamSource(DataFetcher dataFetcher) {
        if(this.dataFetcher == null || this.dataFetcher != dataFetcher) {
            this.dataFetcher = dataFetcher;
        } else return;

        if(exoPlayer == null)
            setPlayer();

        PipedInputStream pipedInputStream = dataFetcher.getPipedInputStream();

        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        RemoteDataSourceFactory remoteDataSourceFactory =
                new RemoteDataSourceFactory(defaultBandwidthMeter, pipedInputStream);

        DataSpec dataSpec = new DataSpec(
                Uri.parse("bytes:///data_stream"), DataSpec.FLAG_ALLOW_CACHING_UNKNOWN_LENGTH
        );

        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(remoteDataSourceFactory);
        MediaSource source = factory.createMediaSource(dataSpec.uri);

        exoPlayer.prepare(source);
        exoPlayer.setPlayWhenReady(true);
    }

    // Setting up all tracks of an album in a queue
    private ConcatenatingMediaSource setMultipleSources(ExtractorMediaSource.Factory factory, Album album) {
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();

        for(Track track : album.getTracks()) {
            MediaSource mediaSource = factory.createMediaSource(Uri.parse(track.getUri()));

            concatenatingMediaSource.addMediaSource(mediaSource);
        }

        return concatenatingMediaSource;
    }

    // Setting up the source
    public void setSource(Store store) {
        setStore(store);

        if(exoPlayer == null)
            setPlayer();

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), getContext().getPackageName()));

        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(defaultDataSourceFactory);
        ConcatenatingMediaSource concatenatingMediaSource = setMultipleSources(factory, store.getAlbums().get(0));

        exoPlayer.prepare(concatenatingMediaSource);
        exoPlayer.seekToDefaultPosition(store.getPosition());
        exoPlayer.setPlayWhenReady(true);

        getPlayerStateChanged().onPlayerStateChanged(store, true);
    }

    // Attaching the visualizer view
    public void setVisualizer(VisualizerView visualizerView) {
        this.visualizerView = visualizerView;
    }

    // Setting up visualizer view for stream
    private void setUpVisualizer(VisualizerView visualizerView, int audioSessionId) {
        if(visualizerView != null && audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
            Visualizer visualizer = new Visualizer(audioSessionId);
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {}

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                    visualizerView.onUpdateFftData(bytes);
                }
            }, Visualizer.getMaxCaptureRate() / 2, false, true);

            visualizer.setEnabled(true);
        }
    }

    public Track getCurrentTrack() {
        return getStore().getTrack();
    }

    public SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    @Override
    public void resolve(String uri) {}

    @Override
    public void resolve(Store store) {
        setSource(store);
    }

    @Override
    public void toggle(boolean state) {
        if(exoPlayer != null)
            exoPlayer.setPlayWhenReady(state);
    }

    @Override
    public void seek(long position) {
        if(exoPlayer != null)
            exoPlayer.seekTo(position);
    }

    @Override
    public void release() {
        if(exoPlayer != null) {
            exoPlayer.release();

            exoPlayer = null;
        }

        if(remotePlayer != null) {
            remotePlayer.release();

            remotePlayer = null;
        }
    }
}