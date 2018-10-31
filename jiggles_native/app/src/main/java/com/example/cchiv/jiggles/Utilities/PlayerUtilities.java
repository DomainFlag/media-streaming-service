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
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class PlayerUtilities {

    private static final String TAG = "PlayerUtilities";

    private Context context;

    public interface OnStateChanged {
        void onStateChanged(boolean playWhenReady, int playbackState);
    }

    private OnStateChanged onStateChanged;

    private Track track;

    private JigglesConnection jigglesConnection = null;
    private VisualizerView visualizerView = null;
    private Visualizer visualizer = null;

    public boolean playerPlaybackState = true;
    public boolean playerPlaybackAction = true;

    private SimpleExoPlayer exoPlayer;

    public PlayerUtilities(Context context, PlayerView playerView) {
        this.context = context;
        this.onStateChanged = (OnStateChanged) context;

        setPlayer(playerView);
    }

    public PlayerUtilities(Context context, PlayerView playerView, VisualizerView visualizerView) {
        this(context, playerView);

        this.visualizerView = visualizerView;
    }

    public void attachConnection(JigglesConnection jigglesConnection) {
        this.jigglesConnection = jigglesConnection;
    }

    public void setPlayer(PlayerView playerView) {
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(context);
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, defaultTrackSelector);
        playerView.setPlayer(exoPlayer);

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

                onStateChanged.onStateChanged(playWhenReady, playbackState);
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
    }

    public void prepareExoPlayerFromByteArray(Track track) {
        this.track = track;

        DataFetcher dataFetcher = new DataFetcher(track);
        PipedInputStream pipedInputStream = dataFetcher.getPipedInputStream();
        dataFetcher.start();

        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();

        CustomDataSourceFactory customDataSource = new CustomDataSourceFactory(defaultBandwidthMeter, pipedInputStream);

        DataSpec dataSpec = new DataSpec(
                Uri.parse("bytes:///data"), DataSpec.FLAG_ALLOW_CACHING_UNKNOWN_LENGTH
        );

        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(customDataSource);
        MediaSource source = factory.createMediaSource(dataSpec.uri);

        exoPlayer.prepare(source);
        exoPlayer.setPlayWhenReady(true);
    }

    public void setSource(Track track) {
        this.track = track;

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getPackageName()));

        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(defaultDataSourceFactory);
        MediaSource mediaSource = factory.createMediaSource(
                Uri.parse(track.getUri()));

        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
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

    public void changeSeeker(long value) {
        exoPlayer.seekTo(value);
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

    public Track getTrack() {
        return track;
    }

    public SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    public class CustomDataSource implements DataSource {

        private final TransferListener<? super CustomDataSource> mTransferListener;
        private PipedInputStream mInputStream;
        private Uri mUri;
        private long mBytesRemaining = -1;


        public CustomDataSource(TransferListener<? super CustomDataSource> transferListener, PipedInputStream inputStream) {
            mTransferListener = transferListener;
            mInputStream = inputStream;
        }

        @Override
        public long open(DataSpec dataSpec) {
            mUri = dataSpec.uri;

            if(mTransferListener != null) {
                mTransferListener.onTransferStart(this, dataSpec);
            }

            return C.LENGTH_UNSET;
        }

        private void computeBytesRemaining() throws IOException {
            mBytesRemaining = mInputStream.available();
            if(mBytesRemaining == 0) {
                mBytesRemaining = C.LENGTH_UNSET;
            }
        }

        private int getBytesToRead(int readLength) {
            if(mBytesRemaining == C.LENGTH_UNSET) {
                return readLength;
            }

            return (int) Math.min(mBytesRemaining, readLength);
        }

        @Override
        public int read(byte[] buffer, int offset, int readLength) throws IOException {
            Log.v(TAG, String.valueOf(offset));
            computeBytesRemaining();

            if(readLength == 0) {
                return 0;
            } else if(mBytesRemaining == 0) {
                return C.RESULT_END_OF_INPUT;
            }

            int bytesRead = -1, bytesToRead = getBytesToRead(readLength);
            try {
                bytesRead = mInputStream.read(buffer, offset, bytesToRead);
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }

            if(bytesRead == -1) {
                return C.RESULT_END_OF_INPUT;
            }

            if(mTransferListener != null) {
                mTransferListener.onBytesTransferred(this, bytesRead);
            }

            return bytesRead;
        }

        @Nullable
        @Override
        public Uri getUri() {
            return mUri;
        }

        @Override
        public void close() {
            try {
                if(mInputStream != null) {
                    mInputStream.close();
                }
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            } finally {
                if(mTransferListener != null) {
                    mTransferListener.onTransferEnd(this);
                }
            }
        }
    }

    public class CustomDataSourceFactory implements DataSource.Factory {

        private TransferListener<? super DataSource> mTransferListener;
        private PipedInputStream mInputStream;

        public CustomDataSourceFactory(TransferListener<? super DataSource> listener, PipedInputStream inputStream) {
            mTransferListener = listener;
            mInputStream = inputStream;
        }

        @Override
        public CustomDataSource createDataSource() {
            return new CustomDataSource(mTransferListener, mInputStream);
        }
    }

    public class DataFetcher extends Thread {

        private Track track;

        private PipedInputStream pipedInputStream;
        private PipedOutputStream pipedOutputStream;

        public DataFetcher(Track track) {
            this.pipedInputStream = new PipedInputStream();
            this.pipedOutputStream = new PipedOutputStream();
            this.track = track;

            try {
                this.pipedInputStream.connect(this.pipedOutputStream);
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }
        }

        public PipedInputStream getPipedInputStream() {
            return pipedInputStream;
        }

        @Override
        public void run() {
            File file = new File(track.getUri());

            try {
                FileInputStream fileInputStream = new FileInputStream(file);

                byte[] data = new byte[1024];

                int len = fileInputStream.read(data, 0, 1024);
                while(len != -1) {
                    pipedOutputStream.write(data, 0, len);

                    len = fileInputStream.read(data, 0, 1024);
                }

                fileInputStream.close();
            } catch(FileNotFoundException e) {
                Log.v(TAG, e.toString());
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }
        }
    }
}