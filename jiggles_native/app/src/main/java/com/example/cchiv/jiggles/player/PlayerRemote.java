package com.example.cchiv.jiggles.player;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.dialogs.ConnectivityDialog;
import com.example.cchiv.jiggles.interfaces.OnManageStreamData;
import com.example.cchiv.jiggles.interfaces.OnUpdatePairedDevices;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.protocol.Connection;
import com.example.cchiv.jiggles.player.protocol.RemoteConnection;
import com.example.cchiv.jiggles.player.protocol.RemoteProtocol;
import com.google.android.exoplayer2.Player;

import java.io.IOException;
import java.util.Set;

public class PlayerRemote implements ConnectivityDialog.OnBluetoothDeviceSelect,
        OnManageStreamData, OnUpdatePairedDevices, Connection.OnConnectionCallback {

    private static final String TAG = "PlayerRemote";

    public interface OnUpdateInterface {
        void onUpdateInterface(BluetoothDevice bluetoothDevice);
    }

    private OnUpdateInterface onUpdateInterface;
    private MediaPlayer mediaPlayer;
    private ConnectivityDialog connectivityDialog;
    private RemoteConnection remoteConnection;
    private Connection connection;

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
        Connection.Chunk chunk = RemoteProtocol.decodeChunk(data, size);

        if(chunk != null) {
            String action = chunk.getHeader(RemoteProtocol.ACTIONS.HEADER);

            if(action == null) {
                return;
            }

            switch(action) {
                case RemoteProtocol.ACTIONS.ACTION_RESUME :
                    ((Activity) context).runOnUiThread(() -> {
                        mediaPlayer.togglePlayer(true);
                    });
                    break;
                case RemoteProtocol.ACTIONS.ACTION_PAUSE :
                    ((Activity) context).runOnUiThread(() -> {
                        mediaPlayer.togglePlayer(false);
                    });
                    break;
                case RemoteProtocol.ACTIONS.ACTION_STREAM :
                    connection.resolve(chunk);
                    break;
                default:
                    Log.v(TAG, "Undefined action - " + action);
                    break;
            }
        }
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
    public void onConnectionImageCallback(Bitmap bitmap) {
        ((Activity) context).runOnUiThread(() -> {
            ImageView imageView = ((Activity) context).findViewById(R.id.player_thumbnail);
            imageView.setImageBitmap(bitmap);
        });
    }

    @Override
    public void onUpdateInterface(Context context, BluetoothDevice device) {
        connection = new Connection(this);

        if(remoteConnection.getConnectionType() == RemoteConnection.CONNECTION_CLIENT) {
            Log.v(TAG, "Started media transfer");

            Track track = mediaPlayer.getCurrentTrack();
            if(track.local) {
                Image image = track.getAlbum().getArt();

                if(image != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(image.getUrl().toString()));

                        Connection.Message.encodeStreamMessage(remoteConnection, bitmap);
                    } catch(IOException e) {
                        Log.v(TAG, e.toString());
                    }
                }
            }
        }

        ((Activity) context).runOnUiThread(() -> {
            connectivityDialog.dismiss();

            onUpdateInterface.onUpdateInterface(device);
        });
    }
}
