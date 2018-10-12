package com.example.cchiv.jiggles.utilities;

import android.content.Context;
import android.media.audiofx.Visualizer;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;

import com.example.cchiv.jiggles.model.Track;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

public class PlayerUtilities {

    private static final String TAG = "PlayerUtilities";

    private Context context;

    private JigglesConnection jigglesConnection = null;
    private VisualizerView visualizerView = null;
    private Visualizer visualizer = null;

    public boolean playerPlaybackState = true;
    public boolean playerPlaybackAction = true;

    private SimpleExoPlayer exoPlayer;

    private byte[] data = null;

    public PlayerUtilities(Context context) {
        this.context = context;
    }

    public PlayerUtilities(Context context, VisualizerView visualizerView) {
        this(context);
        this.visualizerView = visualizerView;
    }

    public void fillContainer(byte[] data) {
        this.data = data;
    };

    public int sampleNewData(byte[] buffer) {
        System.arraycopy(this.data, 0, buffer, 0, this.data.length);
        this.data = null;

        return buffer.length;
    }

    public void attachConnection(JigglesConnection jigglesConnection) {
        this.jigglesConnection = jigglesConnection;
    }

    public void setUpPlayer(PlayerView playerView, Track track) {
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(context);
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, defaultTrackSelector);
        playerView.setPlayer(exoPlayer);

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getPackageName()));

        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(defaultDataSourceFactory);
        MediaSource mediaSource = factory.createMediaSource(
                Uri.parse(track.getPath()));
        exoPlayer.prepare(mediaSource);

        try {
            DataSource dataSource = new DataSource() {
                @Override
                public long open(DataSpec dataSpec) throws IOException {
                    return 0;
                }

                @Override
                public int read(byte[] buffer, int offset, int readLength) throws IOException {
                    return sampleNewData(buffer);
                }

                @Nullable
                @Override
                public Uri getUri() {
                    return null;
                }

                @Override
                public void close() throws IOException {

                }
            };
            DataSpec dataSpec = new DataSpec(null, DataSpec.FLAG_ALLOW_CACHING_UNKNOWN_LENGTH);
            dataSource.open(dataSpec);
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }

        exoPlayer.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int
                    playbackState) {

            }

            @Override
            public void onTimelineChanged(EventTime eventTime, int reason) {

            }

            @Override
            public void onPositionDiscontinuity(EventTime eventTime, int reason) {

            }

            @Override
            public void onSeekStarted(EventTime eventTime) {

            }

            @Override
            public void onSeekProcessed(EventTime eventTime) {

            }

            @Override
            public void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters
                    playbackParameters) {

            }

            @Override
            public void onRepeatModeChanged(EventTime eventTime, int repeatMode) {

            }

            @Override
            public void onShuffleModeChanged(EventTime eventTime, boolean shuffleModeEnabled) {

            }

            @Override
            public void onLoadingChanged(EventTime eventTime, boolean isLoading) {

            }

            @Override
            public void onPlayerError(EventTime eventTime, ExoPlaybackException error) {

            }

            @Override
            public void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroups,
                                        TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadStarted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo
                    loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {

            }

            @Override
            public void onLoadCompleted(EventTime eventTime, MediaSourceEventListener
                    .LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData
                                                mediaLoadData) {

            }

            @Override
            public void onLoadCanceled(EventTime eventTime, MediaSourceEventListener
                    .LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData
                                               mediaLoadData) {

            }

            @Override
            public void onLoadError(EventTime eventTime, MediaSourceEventListener.LoadEventInfo
                    loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData,
                                    IOException error, boolean wasCanceled) {

            }

            @Override
            public void onDownstreamFormatChanged(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {

            }

            @Override
            public void onUpstreamDiscarded(EventTime eventTime, MediaSourceEventListener
                    .MediaLoadData mediaLoadData) {

            }

            @Override
            public void onMediaPeriodCreated(EventTime eventTime) {

            }

            @Override
            public void onMediaPeriodReleased(EventTime eventTime) {

            }

            @Override
            public void onReadingStarted(EventTime eventTime) {

            }

            @Override
            public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs, long
                    totalBytesLoaded, long bitrateEstimate) {

            }

            @Override
            public void onViewportSizeChange(EventTime eventTime, int width, int height) {

            }

            @Override
            public void onNetworkTypeChanged(EventTime eventTime, @Nullable NetworkInfo
                    networkInfo) {

            }

            @Override
            public void onMetadata(EventTime eventTime, Metadata metadata) {

            }

            @Override
            public void onDecoderEnabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {

            }

            @Override
            public void onDecoderInitialized(EventTime eventTime, int trackType, String
                    decoderName, long initializationDurationMs) {

            }

            @Override
            public void onDecoderInputFormatChanged(EventTime eventTime, int trackType, Format format) {

            }

            @Override
            public void onDecoderDisabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {

            }

            @Override
            public void onAudioSessionId(EventTime eventTime, int audioSessionId) {
                setUpVisualizer(visualizerView, audioSessionId);
            }

            @Override
            public void onAudioUnderrun(EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

            }

            @Override
            public void onDroppedVideoFrames(EventTime eventTime, int droppedFrames, long elapsedMs) {

            }

            @Override
            public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

            }

            @Override
            public void onRenderedFirstFrame(EventTime eventTime, Surface surface) {

            }

            @Override
            public void onDrmKeysLoaded(EventTime eventTime) {

            }

            @Override
            public void onDrmSessionManagerError(EventTime eventTime, Exception error) {

            }

            @Override
            public void onDrmKeysRestored(EventTime eventTime) {

            }

            @Override
            public void onDrmKeysRemoved(EventTime eventTime) {

            }
        });

        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray
                    trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                // Don't change the state when ( resume state but pause state )
                if(!playerPlaybackState || playerPlaybackAction)
                    playerPlaybackState = playWhenReady;

                if(jigglesConnection != null) {
                    byte[] action;
                    if(playWhenReady) {
                        action = JigglesProtocol.createResumeAction();
                    } else {
                        action = JigglesProtocol.createPauseAction();
                    }

                    jigglesConnection.write(action, action.length);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        exoPlayer.setPlayWhenReady(playerPlaybackState);
    }

    private void setUpVisualizer(VisualizerView visualizerView, int audioSessionId) {
        if(visualizerView != null && audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
            visualizer = new Visualizer(audioSessionId);
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

    public void togglePlayer(boolean play) {
        exoPlayer.setPlayWhenReady(play);
    }

    public void togglePlayback(boolean play) {
        // Only change playback state when not in(pause state but resume action)
        if(playerPlaybackState || !play) {
            playerPlaybackAction = play;

            exoPlayer.setPlayWhenReady(play);

            if(visualizer != null)
                visualizer.setEnabled(play);
        }
    }

    public void release() {
        exoPlayer.release();

        if(visualizer != null)
            visualizer.release();
    }
}
