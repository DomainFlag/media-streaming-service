package com.example.cchiv.jiggles.player;

import android.content.Context;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.example.cchiv.jiggles.interfaces.OnTrackStateChanged;
import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Store;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.listeners.PlayerAnalyticsListener;
import com.example.cchiv.jiggles.player.listeners.PlayerEventListener;
import com.example.cchiv.jiggles.player.protocol.RemotePlayer;
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
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class MediaPlayer {

    private static final String TAG = "MediaPlayer";

    private Context context;

    private OnTrackStateChanged onTrackStateChanged;

    private Store store;

    private MediaSessionPlayer mediaSessionPlayer = null;
    private VisualizerView visualizerView = null;
    private Visualizer visualizer = null;

    private DataFetcher dataFetcher = null;

    public boolean playerPlaybackState = true;
    public boolean playerPlaybackAction = true;

    private SimpleExoPlayer exoPlayer = null;
    private RemotePlayer remotePlayer = null;

    public MediaPlayer(Context context) {
        this.context = context;
        this.onTrackStateChanged = ((OnTrackStateChanged) context);
    }

    public void onAttachRemotePlayer(RemotePlayer remotePlayer) {
        this.remotePlayer = remotePlayer;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Track getCurrentTrack() {
        if(exoPlayer != null) {
            int position = exoPlayer.getCurrentWindowIndex();

            return store.getTrack(position);
        }

        return null;
    }

    public Album getCurrentAlbum() {
        if(exoPlayer != null) {
            int position = exoPlayer.getCurrentWindowIndex();

            return store.getTrack(position).getAlbum();
        }

        return null;
    }

    public void setPlayer(MediaSessionPlayer mediaSessionPlayer) {
        this.mediaSessionPlayer = mediaSessionPlayer;

        if(exoPlayer != null)
            release();

        DefaultAllocator allocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setAllocator(allocator)
                .setBufferDurationsMs(400000, 400000, 1500, 1500)
                .createDefaultLoadControl();
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector();

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context);

        exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, defaultTrackSelector, loadControl);
        exoPlayer.addAnalyticsListener(new PlayerAnalyticsListener() {
            @Override
            public void onPositionDiscontinuity(EventTime eventTime, int reason) {
                if(reason == ExoPlayer.DISCONTINUITY_REASON_PERIOD_TRANSITION ||
                        reason == ExoPlayer.DISCONTINUITY_REASON_SEEK_ADJUSTMENT) {
                    onTrackStateChanged.onTrackStateChanged(getCurrentTrack());

                    mediaSessionPlayer.buildNotificationPlayer(getCurrentTrack());
                }
            }

            @Override
            public void onSeekProcessed(EventTime eventTime) {
                if(remotePlayer != null) {
                    remotePlayer.onStateChanged(RemotePlayer.RemoteAction.ACTION_SEEK, eventTime.currentPlaybackPositionMs);
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
                // Don't change the state when ( resume state but pause state )
                if(!playerPlaybackState || playerPlaybackAction)
                    playerPlaybackState = playWhenReady;

                if(remotePlayer != null) {
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

    public void triggerMediaSession(int playbackState, boolean playWhenReady) {
        if((playbackState == Player.STATE_READY) && playWhenReady) {
            mediaSessionPlayer.setState(PlaybackStateCompat.STATE_PLAYING);
        } else if((playbackState == Player.STATE_READY)) {
            mediaSessionPlayer.setState(PlaybackStateCompat.STATE_PAUSED);
        }

        Track track = getCurrentTrack();
        mediaSessionPlayer.buildNotificationPlayer(track);
        onTrackStateChanged.onTrackStateChanged(getCurrentTrack());
    }

    public void setStreamSource(DataFetcher dataFetcher) {
        if(this.dataFetcher == null || this.dataFetcher != dataFetcher) {
            this.dataFetcher = dataFetcher;
        } else return;

        PipedInputStream pipedInputStream = dataFetcher.getPipedInputStream();

        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        CustomDataSourceFactory customDataSource = new CustomDataSourceFactory(defaultBandwidthMeter, pipedInputStream);

        DataSpec dataSpec = new DataSpec(
                Uri.parse("bytes:///data_stream"), DataSpec.FLAG_ALLOW_CACHING_UNKNOWN_LENGTH
        );

        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(customDataSource);
        MediaSource source = factory.createMediaSource(dataSpec.uri);

        exoPlayer.prepare(source);
        exoPlayer.setPlayWhenReady(true);
    }

    private ConcatenatingMediaSource setMultipleSources(ExtractorMediaSource.Factory factory, Album album) {
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();

        for(Track track : album.getTracks()) {
            MediaSource mediaSource = factory.createMediaSource(Uri.parse(track.getUri()));

            concatenatingMediaSource.addMediaSource(mediaSource);
        }

        return concatenatingMediaSource;
    }

    public void setSource(Store store, int defaultPosition) {
        this.store = store;

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getPackageName()));

        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(defaultDataSourceFactory);
        ConcatenatingMediaSource concatenatingMediaSource = setMultipleSources(factory, store.getAlbums().get(0));

        exoPlayer.prepare(concatenatingMediaSource);
        exoPlayer.seekToDefaultPosition(defaultPosition);
        exoPlayer.setPlayWhenReady(true);

        onTrackStateChanged.onTrackStateChanged(getCurrentTrack());
    }

    public void setVisualizer(VisualizerView visualizer) {
        this.visualizerView = visualizer;
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
        if(exoPlayer != null) {
            exoPlayer.setPlayWhenReady(play);

            triggerMediaSession(Player.STATE_READY, play);
        }
    }

    public void changeSeeker(long value) {
        if(exoPlayer != null)
            exoPlayer.seekTo(value);
    }

    public void detachPlayer(PlayerView playerView) {
        PlayerView.switchTargetView(exoPlayer, playerView, null);
    }

    public void togglePlayback(boolean play) {
        // Only change playback state when not in(pause state but resume action)
        if(exoPlayer != null && (playerPlaybackState || !play)) {
            playerPlaybackAction = play;

            exoPlayer.setPlayWhenReady(play);
        }
    }

    public void release() {
        if(exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    public void releaseVisualizer() {
        visualizer.release();
    }

    public Store getStore() {
        return store;
    }

    public SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    public class CustomDataSource implements DataSource {

        private TransferListener<? super CustomDataSource> mTransferListener;
        private ByteArrayOutputStream mByteArrayOutputStream;
        private PipedInputStream mInputStream;
        private byte[] mCachedStream;
        private Uri mUri;
        private boolean mDataTransfer = false;
        private long mBytesRemaining = -1;

        private long playerPos = 0;

        public CustomDataSource(TransferListener<? super CustomDataSource> transferListener, PipedInputStream inputStream) {
            mByteArrayOutputStream = new ByteArrayOutputStream();
            mTransferListener = transferListener;
            mInputStream = inputStream;
        }

        @Override
        public long open(DataSpec dataSpec) {
            mUri = dataSpec.uri;
            playerPos = dataSpec.position;

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
            computeBytesRemaining();

            if(readLength == 0) {
                return 0;
            } else if(mBytesRemaining == 0) {
                return C.RESULT_END_OF_INPUT;
            }

            int bytesRead = -1, bytesToRead = getBytesToRead(readLength);
            try {
                if(!mDataTransfer)
                    bytesRead = mInputStream.read(buffer, offset, bytesToRead);
                else {
                    if(playerPos != mCachedStream.length) {
                        bytesToRead = Math.min(bytesToRead, mCachedStream.length - (int) playerPos);

                        System.arraycopy(mCachedStream, (int) playerPos, buffer, offset, bytesToRead);

                        playerPos += bytesToRead;
                        bytesRead = bytesToRead;
                    }
                }

                if(!mDataTransfer && bytesRead != -1) {
                    mByteArrayOutputStream.write(buffer, offset, bytesRead);
                }
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }

            if(bytesRead == -1) {
                if(!mDataTransfer) {
                    mInputStream.close();
                    mDataTransfer = true;

                    setCachedStream(mByteArrayOutputStream);
                }

                return C.RESULT_END_OF_INPUT;
            }

            if(mTransferListener != null) {
                mTransferListener.onBytesTransferred(this, bytesRead);
            }

            return bytesRead;
        }

        private void setCachedStream(ByteArrayOutputStream byteArrayOutputStream) {
            mCachedStream = byteArrayOutputStream.toByteArray();
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

    public static class DataFetcher extends Thread {

        private Track track;

        private PipedInputStream pipedInputStream = new PipedInputStream();
        private PipedOutputStream pipedOutputStream = new PipedOutputStream();

        public DataFetcher() {
            try {
                pipedInputStream.connect(this.pipedOutputStream);
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }
        }

        public DataFetcher(Track track) {
            this();

            this.track = track;
        }

        public PipedInputStream getPipedInputStream() {
            return pipedInputStream;
        }

        public void write(byte[] data, int offset, int len) {
            try {
                pipedOutputStream.write(data, offset, len);
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }
        }

        public void write(byte[] data) {
            try {
                pipedOutputStream.write(data);
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }
        }

        @Override
        public void run() {
            File file = new File(track.getUri());

            try {
                FileInputStream fileInputStream = new FileInputStream(file);

                byte[] data = new byte[1024];

                int len = fileInputStream.read(data, 0, 1024);
                while(len != -1) {
                    write(data, 0, len);

                    len = fileInputStream.read(data, 0, 1024);
                }

                pipedOutputStream.close();

                fileInputStream.close();
            } catch(FileNotFoundException e) {
                Log.v(TAG, e.toString());
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }
        }
    }
}