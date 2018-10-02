package com.example.cchiv.jiggles.interfaces;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

public interface OnUpdatePairedDevices {
    void onUpdatePairedDevices(Set<BluetoothDevice> devices);
    void onAddPairedDevice(BluetoothDevice device);
}
