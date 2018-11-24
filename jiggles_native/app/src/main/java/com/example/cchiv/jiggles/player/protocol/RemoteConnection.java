package com.example.cchiv.jiggles.player.protocol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.cchiv.jiggles.interfaces.OnManageStreamData;
import com.example.cchiv.jiggles.interfaces.OnSearchPairedDevices;
import com.example.cchiv.jiggles.interfaces.OnUpdatePairedDevices;
import com.example.cchiv.jiggles.player.PlayerRemote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class RemoteConnection implements OnSearchPairedDevices {

    private static final String TAG = "RemoteConnection";

    private OnManageStreamData onManageStreamData;
    private OnUpdatePairedDevices onUpdatePairedDevices;

    private static final String UUID_IDENTIFIER = "74911d97-529f-4d4c-9aa9-20445d526923";
    private static final int REQUEST_ENABLE_BT = 1201;
    private BluetoothAdapter mBluetoothAdapter;

    private Context context;

    private ConnectedThread connectedThread = null;
    private ClientThread clientThread;
    private RemoteThread remoteThread;

    private boolean receiverRegistered = false;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent and update the UI with the new device
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                onUpdatePairedDevices.onUpdatePairedDevices(device);
            } else {
                Log.v(TAG, "Action not found");
            }
        }
    };

    public RemoteConnection(Context context, PlayerRemote playerRemote) {
        this.context = context;
        this.onManageStreamData = (OnManageStreamData) playerRemote;
        this.onUpdatePairedDevices = (OnUpdatePairedDevices) playerRemote;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        searchPairedDevices();
    }

    /**
     * Starting server thread
     */
    public void startServerThread() {
        remoteThread = new RemoteThread();
        remoteThread.start();
    }

    /**
     * Starting client thread(one remains server the other one becomes client)
     * @param bluetoothDevice connected bluetooth device
     */
    public void startRemoteClient(BluetoothDevice bluetoothDevice) {
        // Stopping the server thread
        remoteThread.setRunning(false);

        clientThread = new ClientThread(bluetoothDevice);
        clientThread.start();
    }

    public void discover() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1000);
        context.startActivity(discoverableIntent);
    }

    public void syncAudioData() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null) {
            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Log.v(TAG, "The bluetooth is enabled");
                searchPairedDevices();

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                mBluetoothAdapter.cancelDiscovery();
            }
        } else {
            Log.v(TAG, "Couldn't get the bluetooth adapter");
        }
    }

    @Override
    public void onSearchPairedDevices(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            searchPairedDevices();

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            receiverRegistered = true;
            context.registerReceiver(mReceiver, filter);

            mBluetoothAdapter.cancelDiscovery();
        }
    }

    private void searchPairedDevices() {
        if(mBluetoothAdapter != null) {
            Set<BluetoothDevice> pairedBluetoothDevices = mBluetoothAdapter.getBondedDevices();

            onUpdatePairedDevices.onUpdatePairedDevices(pairedBluetoothDevices);
        }
    }

    public void release() {
        if(receiverRegistered) {
            context.unregisterReceiver(mReceiver);

            receiverRegistered = false;
        }

        if(remoteThread != null)
            remoteThread.cancel();

        if(clientThread != null)
            clientThread.cancel();

        if(connectedThread != null)
            connectedThread.cancel();
    }

    // Client
    public class ClientThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        private ClientThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                Log.v(TAG, "Create the RfcommSocket for attempting to connect with " + device.getName());
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(UUID_IDENTIFIER));
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }

            mmSocket = tmp;
        }

        @Override
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // Updating the UI that the pairing went successfully
            onUpdatePairedDevices.onUpdateInterface(context, mmDevice);

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    // Server
    private class RemoteThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        private boolean running = true;

        private RemoteThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;

            try {
                Log.v(TAG, "Listen to Rfcomm for anything");
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(context.getApplicationInfo().name,
                        UUID.fromString(UUID_IDENTIFIER));
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }

            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;

            // Keep listening until exception occurs or a socket is returned.
            while(running) {
                try {
                    socket = mmServerSocket.accept(50);
                } catch(IOException e) {
                    if(!running) {
                        Log.v(TAG, "Server was closed");
                    } else {
                        Log.v(TAG, "Socket's accept() method failed");
                    }
                }

                try {
                    if(socket != null) {
                        // Cancel discovery because it otherwise slows down the connection.
                        mBluetoothAdapter.cancelDiscovery();

                        // A connection was accepted. Perform work associated with
                        // the connection in a separate thread.
                        onUpdatePairedDevices.onUpdateInterface(context, socket.getRemoteDevice());

                        connectedThread = new ConnectedThread(socket);
                        connectedThread.start();

                        mmServerSocket.close();
                        break;
                    } else {
                        // Do something
                    }
                } catch(IOException e) {
                    Log.v(TAG, e.toString());
                }
            }
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    public void writeStream(byte[] data) {
        if(connectedThread != null)
            connectedThread.write(data);
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        // mmBuffer store for the stream
        private byte[] mmBuffer;

        private ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }

            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while(true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);

                    if(numBytes > 0)
                        Log.v(TAG, "Read " + String.valueOf(numBytes));

                    // Send the obtained chunk to the UI activity.
                    onManageStreamData.onManageStreamData(context, mmBuffer, numBytes);
                } catch(IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the client thread to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                Log.v(TAG, "Notification written successfully");
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
