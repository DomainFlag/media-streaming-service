package com.example.cchiv.jiggles.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.data.ContentContract;
import com.example.cchiv.jiggles.data.ContentContract.TrackEntry;
import com.example.cchiv.jiggles.interfaces.OnUpdatePairedDevices;
import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Artist;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.utilities.JigglesConnection;
import com.example.cchiv.jiggles.utilities.JigglesLoader;
import com.example.cchiv.jiggles.utilities.JigglesProtocol;
import com.example.cchiv.jiggles.utilities.PlayerUtilities;
import com.example.cchiv.jiggles.utilities.Tools;
import com.example.cchiv.jiggles.utilities.VisualizerView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class PlayerActivity extends AppCompatActivity implements PlayerUtilities.OnStateChanged {

    private static final String TAG = "PlayerActivity";

    private static final String NOTIFICATION_PLAYER_CONTROLLER = "NOTIFICATION_PLAYER_CONTROLLER";
    private static final int NOTIFICATION_PLAYER_ID = 721;

    private static final int TRACK_LOADER_ID = 221;

    private boolean utilitiesToggle = false;

    private NotificationManager notificationManager;
    private static MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder playbackStateCompatBuilder;

    private JigglesConnection jigglesConnection = null;
    private PlayerUtilities playerUtilities = null;

    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        createMediaSession();

        Intent intent = getIntent();
        String trackId = intent.getStringExtra("trackId");

        if(trackId == null)
            finish();

        playerView = findViewById(R.id.player);
        playerView.setControllerShowTimeoutMs(-1);

        LoaderManager loaderManager = getSupportLoaderManager();
        JigglesLoader jigglesLoader = new JigglesLoader<>(this,
                (JigglesLoader.OnPostLoaderCallback<Track>) this::setUpActivity,
                cursor -> {
                    if(cursor.moveToNext())
                        return Track.parseCursor(cursor);
                    else return null;
                }
        );

        Bundle args = new Bundle();
        args.putString(JigglesLoader.BUNDLE_URI_KEY, ContentContract.CONTENT_COLLECTION_URI.toString());
        args.putString(JigglesLoader.BUNDLE_SELECTION_KEY, TrackEntry._ID + "=?");
        args.putStringArray(JigglesLoader.BUNDLE_SELECTION_ARGS_KEY, new String[] { trackId });

        loaderManager.initLoader(TRACK_LOADER_ID, args, jigglesLoader).forceLoad();
    }

    public void setUpActivity(Track track) {
        if(track == null)
            finish();

        VisualizerView visualizerView = findViewById(R.id.player_visualizer);
        playerUtilities = new PlayerUtilities(this, playerView, visualizerView);

        setPlayer(track);

        updateUserInterface(track);
        setBluetoothConnection();
    }

    public void actOnAction(String action) {
        runOnUiThread(() -> {
            Log.v(TAG, action);
            if(action.startsWith(JigglesProtocol.ACTION_PAUSE)) {
                playerUtilities.togglePlayer(false);
            } else if(action.startsWith(JigglesProtocol.ACTION_RESUME)) {
                playerUtilities.togglePlayer(true);
            } else {
                Log.v(TAG, "Unknown operation");
            }
        });
    }

    public void setBluetoothConnection() {
        // Server - Client
        findViewById(R.id.player_share).setOnClickListener((view) -> {
            AvailableDevicesDialog availableDevicesDialog = new AvailableDevicesDialog();
            availableDevicesDialog.onAttachBluetoothDeviceListener(bluetoothDevice -> {
                if(jigglesConnection != null) {
                    jigglesConnection.onPairDevice(bluetoothDevice);
                }
            });
            availableDevicesDialog.show(getFragmentManager(),  "availableDevicesDialog");

            jigglesConnection = new JigglesConnection(this, (message, type, size, data) -> {
                // Do something with the data regardless the reading or writing state
                switch (type) {
                    case JigglesConnection.MessageConstants.MESSAGE_READ: {
                        if(size > 0) {
                            actOnAction(JigglesProtocol.decodeAction(data));
                        } else Log.v(TAG, "No data");
                        break;
                    }
                    case JigglesConnection.MessageConstants.MESSAGE_WRITE: {
                        if(size > 0) {
                            actOnAction(JigglesProtocol.decodeAction(data));
                        } else Log.v(TAG, "No data");
                        break;
                    }
                    case JigglesConnection.MessageConstants.MESSAGE_TOAST: {
                        Log.v(TAG, message);
                        break;
                    }
                }
            }, new OnUpdatePairedDevices() {
                @Override
                public void onUpdatePairedDevices(Set<BluetoothDevice> devices) {
                    availableDevicesDialog.onUpdateDialog(devices);
                }

                @Override
                public void onAddPairedDevice(BluetoothDevice device) {
                    availableDevicesDialog.onUpdateDialog(device);
                }

                @Override
                public void onPairedDoneDevice(String message) {
                    availableDevicesDialog.dismiss();

                    runOnUiThread(() -> {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                    });
                }
            });

            attachListener(jigglesConnection);
        });
    }

    public void updateUserInterface(Track track){
        findViewById(R.id.player_lyrics).setOnClickListener((view) -> {
            // Do something later with lyrics
        });

        findViewById(R.id.player_back).setOnClickListener((view) -> {
            finish();
        });

        View utilities = findViewById(R.id.player_utilities);
        ImageView menu = findViewById(R.id.player_menu);
        menu.setOnClickListener((view) -> {
            if(utilitiesToggle) {
                menu.setAlpha(1.0f);

                utilities.setVisibility(View.GONE);
            } else {
                menu.setAlpha(0.6f);

                utilities.setVisibility(View.VISIBLE);
            }

            utilitiesToggle = !utilitiesToggle;
        });

        TextView textTrackView = (TextView) findViewById(R.id.player_track);
        textTrackView.setText(track.getName());

        Album album = track.getAlbum();
        if(album != null) {
            Artist artist = album.getArtist();
            if(artist != null) {
                TextView textArtistView = findViewById(R.id.player_artist);
                textArtistView.setText(artist.getName());
            }

            updateLayoutArtwork(album.getArt());
        }
    }

    public void updateLayoutArtwork(Image artwork) {
        ImageView thumbnail = findViewById(R.id.player_thumbnail);

        Picasso
                .get()
                .load(artwork.getUri())
                .into(thumbnail);

        int darkVibrantColor = artwork.getColor();
        int color = ContextCompat.getColor(this, R.color.primaryTextColor);

        Tools.setStatusBarColor(this, darkVibrantColor);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {
                        darkVibrantColor,
                        color
                }
        );

        View view = findViewById(R.id.player_background);
        ViewCompat.setBackground(view, gradientDrawable);

        playerView.setShutterBackgroundColor(ContextCompat.getColor(this, R.color.visualizerClearColor));
    }

    public void setPlayer(Track track) {
        playerUtilities.setSource(track);
    }

    public void attachListener(JigglesConnection jigglesConnection) {
        playerUtilities.attachConnection(jigglesConnection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(jigglesConnection != null)
            jigglesConnection.onSearchPairedDevices(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaSessionCompat.setActive(false);

        if(jigglesConnection != null) {
            jigglesConnection.release();
        }

        if(playerUtilities != null) {
            playerUtilities.release();
            notificationManager.cancelAll();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return super.onCreateDialog(id);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        togglePlayer(true);
    }

    public void togglePlayer(boolean playbackState) {
        if(playerUtilities != null)
            playerUtilities.togglePlayback(playbackState);
    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == Player.STATE_READY) && playWhenReady){
            playbackStateCompatBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    playerUtilities.getExoPlayer().getCurrentPosition(), 1f);
        } else if((playbackState == Player.STATE_READY)) {
            playbackStateCompatBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    playerUtilities.getExoPlayer().getCurrentPosition(), 1f);
        }

        Track track = playerUtilities.getTrack();

        mediaSessionCompat.setPlaybackState(playbackStateCompatBuilder.build());
        buildNotificationPlayer(playbackStateCompatBuilder.build(), track);
    }

    private class JigglesSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            Log.v(TAG, "onPlay");
            playerUtilities.togglePlayback(true);
        }

        @Override
        public void onPause() {
            Log.v(TAG, "onPause");
            playerUtilities.togglePlayback(false);
        }

        @Override
        public void onSkipToPrevious() {
            Log.v(TAG, "toPrevious");
            playerUtilities.changeSeeker(0);
        }
    }

    private void createMediaSession() {
        mediaSessionCompat = new MediaSessionCompat(this, TAG);

        mediaSessionCompat.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setMediaButtonReceiver(null);

        playbackStateCompatBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSessionCompat.setPlaybackState(playbackStateCompatBuilder.build());
        mediaSessionCompat.setCallback(new JigglesSessionCallback());
        mediaSessionCompat.setActive(true);
    }

    private void buildNotificationPlayer(PlaybackStateCompat state, Track track) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_PLAYER_CONTROLLER);

        int toggleIcon;
        if(state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            toggleIcon = R.drawable.exo_controls_pause;
        } else {
            toggleIcon = R.drawable.exo_controls_play;
        }

        NotificationCompat.Action playbackAction = new NotificationCompat.Action(
                toggleIcon, "Toggle playback",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new NotificationCompat
                .Action(R.drawable.exo_controls_previous, "Restart",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (this, 0, new Intent(this, PlayerActivity.class), 0);

        Image art = track.getAlbum().getArt();
        Bitmap largeIcon = null;
        try {
            largeIcon = MediaStore.Images.Media.getBitmap(this.getContentResolver(), art.getUri());
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }

        Notification notification = builder
                .setContentTitle(track.getName())
                .setColorized(true)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0))
                .setColor(art.getColor())
                .setContentText(track.getArtist().getName())
                .setSmallIcon(R.drawable.ic_microphone)
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(contentPendingIntent)
                .addAction(restartAction)
                .addAction(playbackAction)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                        .setShowActionsInCompactView(0,1))
                .build();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_PLAYER_CONTROLLER,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_LOW);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(NOTIFICATION_PLAYER_ID, notification);
    }


    public static class AvailableDevicesDialog extends DialogFragment {

        public interface OnBluetoothDeviceSelect {
            void onBluetoothDeviceSelect(BluetoothDevice bluetoothDevice);
        }

        private static final String TAG = "AvailableDevicesDialog";

        private Context context;

        private ArrayAdapter<String> arrayAdapter;

        private OnBluetoothDeviceSelect onBluetoothDeviceSelect;

        private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();

        public AvailableDevicesDialog() {
            super();
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            this.context = context;
        }

        public void onAttachBluetoothDeviceListener(OnBluetoothDeviceSelect onBluetoothDeviceSelect) {
            this.onBluetoothDeviceSelect = onBluetoothDeviceSelect;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.dialog_devices_layout, null, false);
            builder.setView(inflatedView);

            arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new ArrayList<>());

            ListView listView = inflatedView.findViewById(R.id.devices_list);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener((adapterView, view, index, l) -> {
                BluetoothDevice bluetoothDevice = bluetoothDevices.get(index);

                onBluetoothDeviceSelect.onBluetoothDeviceSelect(bluetoothDevice);
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> getDialog().dismiss());

            return builder.create();
        }

        public void onUpdateDialog(Set<BluetoothDevice> devices) {
            bluetoothDevices.addAll(devices);

            for(BluetoothDevice device : devices)
                arrayAdapter.add(device.getName());

            arrayAdapter.notifyDataSetChanged();
        }

        public void onUpdateDialog(BluetoothDevice device) {
            bluetoothDevices.add(device);
            arrayAdapter.add(device.getName());

            arrayAdapter.notifyDataSetChanged();
        }
    }

    public static class MediaPlayerReceiver extends BroadcastReceiver {

        public MediaPlayerReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mediaSessionCompat, intent);
        }
    }
}