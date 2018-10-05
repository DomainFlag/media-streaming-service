package com.example.cchiv.jiggles.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
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
import android.widget.Toast;

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
            AvailableDevicesDialog availableDevicesDialog = new AvailableDevicesDialog(this, bluetoothDevice -> {
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), String.valueOf(size), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        break;
                    }
                    case JigglesConnection.MessageConstants.MESSAGE_WRITE: {
                        if(size > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), String.valueOf(size), Toast.LENGTH_LONG).show();
                                }
                            });
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
                    availableDevicesDialog.onUpdateDialog(devices);
                }

                @Override
                public void onAddPairedDevice(BluetoothDevice device) {
                    availableDevicesDialog.onUpdateDialog(device);
                }
            });
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

        TextView textTrackView = (TextView) findViewById(R.id.player_track);
        textTrackView.setText(track.getName());

        Album album = track.getAlbum();
        if(album != null) {
            Artist artist = album.getArtist();
            if(artist != null) {
                TextView textArtistView = (TextView) findViewById(R.id.player_artist);
                textArtistView.setText(artist.getName());
            }

            Bitmap artwork = album.getArt();
            if(artwork != null)
                updateLayoutArtwork(artwork);
        }
    }

    public void updateLayoutArtwork(Bitmap artwork) {
        ((ImageView) findViewById(R.id.player_thumbnail)).setImageBitmap(artwork);

        Palette palette = Palette.from(artwork)
                .generate();

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

        public interface OnBluetoothDeviceSelect {
            void onBluetoothDeviceSelect(BluetoothDevice bluetoothDevice);
        }

        private static final String TAG = "AvailableDevicesDialog";

        private ArrayAdapter<String> arrayAdapter;

        private OnBluetoothDeviceSelect onBluetoothDeviceSelect;

        private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();

        public AvailableDevicesDialog() {
            super();
        }

        public AvailableDevicesDialog(Context context, OnBluetoothDeviceSelect onBluetoothDeviceSelect) {
            super();

            this.onBluetoothDeviceSelect = onBluetoothDeviceSelect;
            arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new ArrayList<>());
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.dialog_devices_layout, null, false);
            builder.setView(inflatedView);

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
}
