package com.example.cchiv.jiggles.interfaces;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.Set;

public interface OnUpdatePairedDevices {
    void onUpdatePairedDevices(Set<BluetoothDevice> devices);
    void onUpdatePairedDevices(BluetoothDevice device);
    void onNotifyInterface(Context context, BluetoothDevice bluetoothDevice);
}
