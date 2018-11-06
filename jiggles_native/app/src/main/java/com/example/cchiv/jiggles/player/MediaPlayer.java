package com.example.cchiv.jiggles.player;

import android.content.Context;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Collection;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.listeners.PlayerAnalyticsListener;
import com.example.cchiv.jiggles.player.listeners.PlayerEventListener;
import com.example.cchiv.jiggles.utilities.VisualizerView;
import com.google.android.exoplayer2.C;
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

public class MediaPlayer {

    private static final String TAG = "MediaPlayer";

    public interface OnStateChanged {
        void onTrackChanged(Track track);
    }

    private Context context;

    private OnStateChanged onStateChanged;

    private Collection collection;

    private VisualizerView visualizerView = null;
    private Visualizer visualizer = null;

    public boolean playerPlaybackState = true;
    public boolean playerPlaybackAction = true;

    private SimpleExoPlayer exoPlayer = null;
    private RemoteConnection remoteConnection = null;

    public MediaPlayer(Context context) {
        this.context = context;
        this.onStateChanged = ((OnStateChanged) context);
    }

    public void attachConnection(RemoteConnection remoteConnection) {
        this.remoteConnection = remoteConnection;
    }

    public Track getCurrentTrack() {
        int index = exoPlayer.getCurrentWindowIndex();

        return collection.getTrack(index);
    }

    public void setPlayer(PlayerView playerView, PlayerMediaSession playerMediaSession) {
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, defaultTrackSelector);

        exoPlayer.addAnalyticsListener(new PlayerAnalyticsListener() {
            @Override
            public void onPositionDiscontinuity(EventTime eventTime, int reason) {
                if(reason == ExoPlayer.DISCONTINUITY_REASON_PERIOD_TRANSITION ||
                        reason == ExoPlayer.DISCONTINUITY_REASON_SEEK_ADJUSTMENT) {
                    onStateChanged.onTrackChanged(getCurrentTrack());
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

                if(remoteConnection != null) {
                    byte[] action;
                    if(playWhenReady) {
                        action = RemoteProtocol.createResumeAction();
                    } else {
                        action = RemoteProtocol.createPauseAction();
                    }

                    remoteConnection.write(action, action.length);
                }

                if((playbackState == Player.STATE_READY) && playWhenReady) {
                    playerMediaSession.setState(PlaybackStateCompat.STATE_PLAYING);
                } else if((playbackState == Player.STATE_READY)) {
                    playerMediaSession.setState(PlaybackStateCompat.STATE_PAUSED);
                }

                playerMediaSession.buildNotificationPlayer(getCurrentTrack());
            }
        });

//        playerView.setPlayer(exoPlayer);
    }

    public void prepareExoPlayerFromByteArray(Collection collection) {
        this.collection = collection;

        DataFetcher dataFetcher = new DataFetcher(collection.getTracks().get(0));
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

    private ConcatenatingMediaSource setMultipleSources(ExtractorMediaSource.Factory factory, Album album) {
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();

        for(Track track : album.getTracks()) {
            MediaSource mediaSource = factory.createMediaSource(Uri.parse(track.getUri()));

            concatenatingMediaSource.addMediaSource(mediaSource);
        }

        return concatenatingMediaSource;
    }

    public void setSource(Collection collection, boolean wholeContent) {
        this.collection = collection;

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getPackageName()));

        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(defaultDataSourceFactory);
        if(wholeContent) {
            ConcatenatingMediaSource concatenatingMediaSource = setMultipleSources(factory, collection.getAlbums().get(0));

            exoPlayer.prepare(concatenatingMediaSource);
        } else {
            MediaSource mediaSource = factory.createMediaSource(
                    Uri.parse(collection.getTracks().get(0).getUri()));

            exoPlayer.prepare(mediaSource);
        }

        exoPlayer.setPlayWhenReady(true);
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
        exoPlayer.setPlayWhenReady(play);
    }

    public void changeSeeker(long value) {
        exoPlayer.seekTo(value);
    }

    public void detachPlayer(PlayerView playerView) {
        PlayerView.switchTargetView(exoPlayer, playerView, null);
    }

    public void togglePlayback(boolean play) {
        // Only change playback state when not in(pause state but resume action)
        if(playerPlaybackState || !play) {
            playerPlaybackAction = play;

            exoPlayer.setPlayWhenReady(play);
        }
    }

    public void release() {
        exoPlayer.release();
//        remoteConnection.release();
    }

    public void releaseVisualizer() {
        visualizer.release();
    }

    public Collection getCollection() {
        return collection;
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