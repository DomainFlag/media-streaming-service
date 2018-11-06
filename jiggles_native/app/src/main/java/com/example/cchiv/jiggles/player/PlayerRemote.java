package com.example.cchiv.jiggles.player;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.cchiv.jiggles.dialogs.ConnectivityDialog;
import com.example.cchiv.jiggles.interfaces.OnUpdatePairedDevices;

import java.util.Set;

public class PlayerRemote {

    private static final String TAG = "PlayerRemote";

    private MediaPlayer mediaPlayer;
    private ConnectivityDialog connectivityDialog;
    private RemoteConnection remoteConnection;

    private Context context;

    public PlayerRemote(Context context, ConnectivityDialog connectivityDialog, MediaPlayer mediaPlayer) {
        this.context = context;
        this.connectivityDialog = connectivityDialog;
        this.mediaPlayer = mediaPlayer;
    }

    public void createRemoteConnection() {
        remoteConnection = new RemoteConnection(context, (message, type, size, data) -> {
            // Do something with the data regardless the reading or writing state
            switch (type) {
                case RemoteConnection.MessageConstants.MESSAGE_READ: {
                    if(size > 0) {
                        actOnAction(RemoteProtocol.decodeAction(data));
                    } else Log.v(TAG, "No data");
                    break;
                }
                case RemoteConnection.MessageConstants.MESSAGE_WRITE: {
                    if(size > 0) {
                        actOnAction(RemoteProtocol.decodeAction(data));
                    } else Log.v(TAG, "No data");
                    break;
                }
                case RemoteConnection.MessageConstants.MESSAGE_TOAST: {
                    Log.v(TAG, message);
                    break;
                }
            }
        }, new OnUpdatePairedDevices() {
            @Override
            public void onUpdatePairedDevices(Set<BluetoothDevice> devices) {
                connectivityDialog.onUpdateDialog(devices);
            }

            @Override
            public void onAddPairedDevice(BluetoothDevice device) {
                connectivityDialog.onUpdateDialog(device);
            }

            @Override
            public void onPairedDoneDevice(String message) {
                connectivityDialog.dismiss();

                ((Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                });
            }
        });


        mediaPlayer.attachConnection(remoteConnection);
    }

    public void actOnAction(String action) {
        ((Activity) context).runOnUiThread(() -> {
            if(action.startsWith(RemoteProtocol.ACTION_PAUSE)) {
                mediaPlayer.togglePlayer(false);
            } else if(action.startsWith(RemoteProtocol.ACTION_RESUME)) {
                mediaPlayer.togglePlayer(true);
            } else {
                Log.v(TAG, "Unknown operation");
            }
        });
    }
}
