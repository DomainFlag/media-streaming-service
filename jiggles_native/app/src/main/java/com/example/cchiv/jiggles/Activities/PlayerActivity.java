package com.example.cchiv.jiggles.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.interfaces.OnUpdatePairedDevices;
import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Artist;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.utilities.ItemScanner;
import com.example.cchiv.jiggles.utilities.JigglesConnection;
import com.example.cchiv.jiggles.utilities.PlayerUtilities;
import com.example.cchiv.jiggles.utilities.VisualizerView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerActivity extends AppCompatActivity {

    private final static String TAG = "PlayerActivity";

    private boolean utilitiesToggle = false;

    private JigglesConnection jigglesConnection = null;
    private PlayerUtilities playerUtilities = null;

    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        int albumIndex = intent.getIntExtra("albumIndex", -1);
        int trackIndex = intent.getIntExtra("trackIndex", -1);

        if(albumIndex == -1 || trackIndex == -1)
            finish();

        Track track = ItemScanner.getTrack(this, albumIndex, trackIndex);

        playerView = findViewById(R.id.player);

        VisualizerView visualizerView = findViewById(R.id.player_visualizer);
        playerUtilities = new PlayerUtilities(this, visualizerView);

        setPlayer(track);

        updateUserInterface(track);
        setBluetoothConnection();
    }

    public void setBluetoothConnection() {
        // Server - Client
        findViewById(R.id.player_share).setOnClickListener((view) -> {
            AvailableDevicesDialog availableDevicesDialog = new AvailableDevicesDialog();
            FragmentManager fragmentManager = getFragmentManager();
            availableDevicesDialog.show(fragmentManager,  "availableDevicesDialog");

            jigglesConnection = new JigglesConnection(this, (message, type, size, data) -> {
                // Do something with the data regardless the reading or writing state
                switch (type) {
                    case JigglesConnection.MessageConstants.MESSAGE_READ: {
                        if(size > 0) {
                            // Do something
                        }
                        break;
                    }
                    case JigglesConnection.MessageConstants.MESSAGE_WRITE: {
                        if(size > 0) {
                            // Do something
                        }
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

                }

                @Override
                public void onAddPairedDevice(BluetoothDevice device) {

                }
            });

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Wait 15s
                        Thread.sleep(25000);

                        int len = 30;
                        byte[] message = new byte[len];

                        jigglesConnection.write(message, len);
                    } catch(InterruptedException e) {
                        Log.v(TAG, e.toString());
                    }
                }
            });
            thread.run();
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
                menu.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.pleasureColor)));
                utilities.setVisibility(View.GONE);
            } else {
                menu.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.iconsTextColor)));
                utilities.setVisibility(View.VISIBLE);
            }

            utilitiesToggle = !utilitiesToggle;
        });

        BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.background);
        Palette palette = Palette.from(bitmapDrawable.getBitmap())
                .generate();

        Album album = track.getAlbum();
        Artist artist = album.getArtist();

        TextView textArtistView = (TextView) findViewById(R.id.player_artist);
        textArtistView.setText(artist.getName());

        TextView textTrackView = (TextView) findViewById(R.id.player_track);
        textTrackView.setText(track.getName());

        Bitmap artwork = album.getArt();
        if(artwork != null)
            ((ImageView) findViewById(R.id.player_thumbnail)).setImageBitmap(artwork);

        int defaultColor = ContextCompat.getColor(this, R.color.iconsTextColor);
        int lightVibrantColor = palette.getDarkVibrantColor(defaultColor);
        int color = ContextCompat.getColor(this, R.color.primaryTextColor);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {
                        lightVibrantColor,
                        color
                }
        );

        View view = findViewById(R.id.player_background);
        ViewCompat.setBackground(view, gradientDrawable);

        playerView.setShutterBackgroundColor(ContextCompat.getColor(this, R.color.visualizerClearColor));
    }

    public void setPlayer(Track track) {
        playerView.setControllerShowTimeoutMs(-1);
        playerUtilities.setUpPlayer(playerView, track);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, String.valueOf(resultCode));
        if(jigglesConnection != null)
            jigglesConnection.onSearchPairedDevices(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(jigglesConnection != null) {
            jigglesConnection.release();
        }

        if(playerUtilities != null)
            playerUtilities.release();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return super.onCreateDialog(id);
    }

    @Override
    protected void onPause() {
        super.onPause();

        togglePlayer(false);
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

    public static class AvailableDevicesDialog extends DialogFragment {

        private List<String> devices = new ArrayList<>();
        private ListView listView;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.dialog_devices_layout, null, false);
            builder.setView(inflatedView);

            ListView listView = inflatedView.findViewById(R.id.devices_list);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, devices);
            listView.setAdapter(arrayAdapter);

            return builder.create();
        }

        public void onUpdateDialog(Set<BluetoothDevice> devices) {
            for(BluetoothDevice device : devices)
                this.devices.add(device.getName());
        }

        public void onUpdateDialog(BluetoothDevice device) {
            devices.add(device.getName());
        }
    }
}
