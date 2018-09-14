package com.example.cchiv.jiggles.Activities;

import android.content.res.AssetManager;
import android.media.audiofx.Visualizer;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.Utilities.VisualizerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
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
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    private final static String TAG = "PlayerActivity";

    private Visualizer visualizer = null;
    private SimpleExoPlayer exoPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        scan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        exoPlayer.release();
        visualizer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();

        visualizer.setEnabled(false);
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(exoPlayer != null && visualizer != null) {
            visualizer.setEnabled(true);
            exoPlayer.setPlayWhenReady(true);
        }
    }

    public void setPlayer(String fileName) {
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(this);
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, defaultTrackSelector);

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getPackageName()));

        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(defaultDataSourceFactory);
        MediaSource mediaSource = factory.createMediaSource(
                Uri.parse("asset:///samples/" + fileName));
        exoPlayer.prepare(mediaSource);

        PlayerView playerView = findViewById(R.id.player);
        playerView.setPlayer(exoPlayer);

        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int playbackState) {

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
            public void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters) {

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
            public void onLoadCompleted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {

            }

            @Override
            public void onLoadCanceled(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {

            }

            @Override
            public void onLoadError(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {

            }

            @Override
            public void onDownstreamFormatChanged(EventTime eventTime, MediaSourceEventListener
                    .MediaLoadData mediaLoadData) {

            }

            @Override
            public void onUpstreamDiscarded(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {

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
            public void onDecoderEnabled(EventTime eventTime, int trackType, DecoderCounters
                    decoderCounters) {

            }

            @Override
            public void onDecoderInitialized(EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {

            }

            @Override
            public void onDecoderInputFormatChanged(EventTime eventTime, int trackType, Format
                    format) {

            }

            @Override
            public void onDecoderDisabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {

            }

            @Override
            public void onAudioSessionId(EventTime eventTime, int audioSessionId) {
                startVisualizer(audioSessionId);
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
    }

    public void startVisualizer(int session) {
        if(session != C.AUDIO_SESSION_ID_UNSET) {
            VisualizerView visualizerView = findViewById(R.id.player_visualizer);

            visualizer = new Visualizer(session);
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {}

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                    visualizerView.onUpdateFftData(bytes);
                }
            }, Visualizer.getMaxCaptureRate() / 2, false, true);
        }

        visualizer.setEnabled(true);
    }

    public ArrayList<String> scan() {
        AssetManager assetManager = getAssets();
        try {
            String[] files = assetManager.list("samples");
            for(String file : files) {
                setPlayer(file);
            }
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }

        return null;
    }
}
