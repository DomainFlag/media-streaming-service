package com.example.cchiv.jiggles.player;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.example.cchiv.jiggles.dialogs.ConnectivityDialog;
import com.example.cchiv.jiggles.interfaces.OnManageStreamData;
import com.example.cchiv.jiggles.interfaces.OnUpdatePairedDevices;
import com.example.cchiv.jiggles.player.protocol.Connection;
import com.example.cchiv.jiggles.player.protocol.RemoteConnection;
import com.example.cchiv.jiggles.player.protocol.RemoteProtocol;
import com.google.android.exoplayer2.Player;

import java.util.Set;

public class PlayerRemote implements ConnectivityDialog.OnBluetoothDeviceSelect,
        OnManageStreamData, OnUpdatePairedDevices {

    private static final String TAG = "PlayerRemote";

    public interface OnUpdateInterface {
        void onUpdateInterface(BluetoothDevice bluetoothDevice);
    }

    private OnUpdateInterface onUpdateInterface;
    private MediaPlayer mediaPlayer;
    private ConnectivityDialog connectivityDialog;
    private RemoteConnection remoteConnection;

    private Context context;

    public PlayerRemote(Context context, MediaPlayer mediaPlayer) {
        this.context = context;
        this.onUpdateInterface = (OnUpdateInterface) context;
        this.mediaPlayer = mediaPlayer;

        this.mediaPlayer.onAttachRemotePlayer(this);
    }

    public void createRemoteConnection() {
        connectivityDialog = new ConnectivityDialog();
        connectivityDialog.onAttach(context);
        connectivityDialog.onAttachBluetoothDeviceListener(this);
        connectivityDialog.show(((Activity) context).getFragmentManager(), TAG);

        remoteConnection = new RemoteConnection(context, this);
        remoteConnection.startServerThread();
    }

    @Override
    public void onBluetoothDeviceSelect(BluetoothDevice bluetoothDevice) {
        remoteConnection.startRemoteClient(bluetoothDevice);
    }

    public void onStateChanged(int playbackState, boolean playWhenReady) {
        String identifier = Connection.Message.generateIdentifier();

        if((playbackState == Player.STATE_READY) && playWhenReady) {
            remoteConnection.writeStream(RemoteProtocol.createResumeAction(identifier));
        } else if(playbackState == Player.STATE_READY) {
            remoteConnection.writeStream(RemoteProtocol.createPauseAction(identifier));
        }
    }

    /**
     * Received data
     * @param size
     * @param data
     */
    @Override
    public void onManageStreamData(Context context, byte[] data, int size) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Connection.Chunk chunk = RemoteProtocol.decodeChunk(data, size);

                if(chunk != null) {
                    String action = chunk.getHeader(RemoteProtocol.ACTIONS.HEADER);

                    if(action.equals(RemoteProtocol.ACTIONS.ACTION_RESUME)) {
                        mediaPlayer.togglePlayer(true);
                    } else if(action.equals(RemoteProtocol.ACTIONS.ACTION_PAUSE)) {
                        mediaPlayer.togglePlayer(false);
                    } else {
                        Log.v(TAG, "Undefined action - " + action);
                    }
                }
            }
        });
    }

    @Override
    public void onUpdatePairedDevices(Set<BluetoothDevice> devices) {
        connectivityDialog.onUpdateDialog(devices);
    }

    @Override
    public void onUpdatePairedDevices(BluetoothDevice device) {
        connectivityDialog.onUpdateDialog(device);
    }

    @Override
    public void onUpdateInterface(Context context, BluetoothDevice device) {
        ((Activity) context).runOnUiThread(() -> {
            connectivityDialog.dismiss();

            onUpdateInterface.onUpdateInterface(device);
        });
    }
}
