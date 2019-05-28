package com.example.cchiv.jiggles.player.protocol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.dialogs.ConnectivityDialog;
import com.example.cchiv.jiggles.interfaces.OnManageStreamData;
import com.example.cchiv.jiggles.interfaces.OnUpdatePairedDevices;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.model.player.content.Track;
import com.example.cchiv.jiggles.player.players.LocalPlayer;
import com.example.cchiv.jiggles.player.protocol.builder.Message;
import com.example.cchiv.jiggles.player.protocol.builder.Messenger;
import com.example.cchiv.jiggles.player.protocol.builder.Protocol;

import java.io.IOException;
import java.util.Set;

public class RemotePlayer implements ConnectivityDialog.OnBluetoothDeviceSelect,
        OnManageStreamData, OnUpdatePairedDevices {

    public class RemoteAction {
        public static final int ACTION_RESUME = 0;
        public static final int ACTION_PAUSE = 1;
        public static final int ACTION_SEEK = 2;
    }

    private static final String TAG = "RemotePlayer";

    public interface OnUpdateInterface {
        void onUpdateInterface(BluetoothDevice bluetoothDevice, boolean isActive);
    }

    private OnUpdateInterface onUpdateInterface = null;
    private LocalPlayer localPlayer;
    private ConnectivityDialog connectivityDialog;
    private RemoteConnection remoteConnection;
    private Messenger messenger;

    private boolean active = false;

    private Context context;

    public RemotePlayer(Context context, LocalPlayer localPlayer) {
        this.context = context;
        this.messenger = new Messenger(context, this);
        this.localPlayer = localPlayer;
    }

    public void onAttachUpdateInterfaceCallback(Context context) {
        this.onUpdateInterface = (OnUpdateInterface) context;
    }

    public void createRemoteConnection() {
        active = true;

        BluetoothAdapter.getDefaultAdapter();

//        connectivityDialog = new ConnectivityDialog();
//        connectivityDialog.onAttach(context);
//        connectivityDialog.onAttachBluetoothDeviceListener(this);
//        connectivityDialog.show(((Activity) context).getFragmentManager(), TAG);

        remoteConnection = new RemoteConnection(context, this);
//        remoteConnection.startServerThread();
    }

    public LocalPlayer getLocalPlayer() {
        return localPlayer;
    }

    public boolean isEnabled() {
        return active;
    }

    @Override
    public void onBluetoothDeviceSelect(BluetoothDevice bluetoothDevice) {
        remoteConnection.startRemoteClient(bluetoothDevice);
    }

    public void release() {
        active = false;

        if(remoteConnection != null)
            remoteConnection.release();

        if(onUpdateInterface != null) {
            ((Activity) context).runOnUiThread(() -> {
                onUpdateInterface.onUpdateInterface(null, true);
            });
        }
    }

    public void releasePlayer() {
        localPlayer.release();
    }

    public void onStateChanged(int actionType, long value) {
        String identifier = Message.generateIdentifier();

        switch(actionType) {
            case RemoteAction.ACTION_RESUME : {
                remoteConnection.writeStream(Protocol.createResumeAction(identifier));
                break;
            }
            case RemoteAction.ACTION_PAUSE : {
                remoteConnection.writeStream(Protocol.createPauseAction(identifier));
                break;
            }
            case RemoteAction.ACTION_SEEK : {
                remoteConnection.writeStream(Protocol.createSeekAction(identifier, value));
                break;
            }
        }
    }

    public void onManageMessage(Message message) {
        if(message != null) {
            String action = message.getAction();

            Activity activity = (Activity) context;
            switch(action) {
                case Protocol.ACTIONS.ACTION_STREAM : {
                    String type = message.getType();

                    switch(type) {
                        case Protocol.CONTENT.TYPE.TYPE_RAW : {
                            Bitmap bitmap = Protocol.decodeStreamImageMessage(message);

                            ((Activity) context).runOnUiThread(() -> {
                                ImageView imageView = ((Activity) context).findViewById(R.id.player_thumbnail);
                                imageView.setImageBitmap(bitmap);

//                                 localPlayer.getCurrentTrack().setBitmap(bitmap);
                            });

                            break;
                        }
                        case Protocol.CONTENT.TYPE.TYPE_AUDIO : {
                            Log.v(TAG, "Streaming audio - " + action + " with type - " + type + " successfully");

                            break;
                        }
                        default : {
                            Log.v(TAG, "Undefined action - " + action + " with type - " + type);
                            break;
                        }
                    }

                    break;
                }
                case Protocol.ACTIONS.ACTION_METADATA : {
                    Track track = Protocol.decodeJSONTrackMessage(message);

                    if(track != null) {
                        activity.runOnUiThread(() -> {
//                            localPlayer.setStore(new PlayerContent(track));

                            ((TextView) activity.findViewById(R.id.player_track)).setText(track.getName());
                            ((TextView) activity.findViewById(R.id.player_artist)).setText(track.getArtistName());
                        });
                    }

                    break;
                }
                case Protocol.ACTIONS.ACTION_RESUME : {
                    ((Activity) context).runOnUiThread(() -> {
                        localPlayer.toggle(true);
                    });

                    break;
                }
                case Protocol.ACTIONS.ACTION_PAUSE : {
                    ((Activity) context).runOnUiThread(() -> {
                        localPlayer.toggle(false);
                    });

                    break;
                }
                case Protocol.ACTIONS.ACTION_SEEK : {
                    ((Activity) context).runOnUiThread(() -> {
                        localPlayer.seek(Protocol.decodeSeekerMessage(message));
                    });

                    break;
                }
                default:
                    Log.v(TAG, "Undefined action - " + action);
                    break;
            }
        }
    }

    /**
     * Received streamed data
     */
    @Override
    public void onManageStreamData(Context context, byte[] data, int size) {
        messenger.resolvePacket(data, size);
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
    public void onNotifyInterface(Context context, BluetoothDevice device) {
        if(onUpdateInterface != null) {
            ((Activity) context).runOnUiThread(() -> {
                connectivityDialog.dismiss();

                onUpdateInterface.onUpdateInterface(device, true);
            });
        }

        if(remoteConnection.getConnectionType() == RemoteConnection.CONNECTION_CLIENT) {
//            Track track = localPlayer.getCurrentTrack();
            Track track = null;
            if(track.local) {
                Image image = track.getAlbum().getArt();

                if(image != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(image.getUrl().toString()));

                        Protocol.encodeJSONMessage(remoteConnection, track);
                        Protocol.encodeStreamMessage(remoteConnection, bitmap);
                        Protocol.encodeStreamAudioMessage(remoteConnection, track.getUri());
                    } catch(IOException e) {
                        Log.v(TAG, e.toString());
                    }
                }
            }
        }
    }
}
