package com.example.cchiv.jiggles.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cchiv.jiggles.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConnectivityDialog extends DialogFragment {

    public interface OnBluetoothDeviceSelect {
        void onBluetoothDeviceSelect(BluetoothDevice bluetoothDevice);
    }

    private static final String TAG = "AvailableDevicesDialog";

    private Context context;

    private ArrayAdapter<String> arrayAdapter = null;

    private OnBluetoothDeviceSelect onBluetoothDeviceSelect;

    private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    private List<String> dialogList = new ArrayList<>();

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

        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, dialogList);

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

        if(arrayAdapter != null) {
            for(BluetoothDevice device : devices) {
                arrayAdapter.add(device.getName());
            }

            arrayAdapter.notifyDataSetChanged();
        } else {
            for(BluetoothDevice device : devices) {
                dialogList.add(device.getName());
            }
        }
    }

    public void onUpdateDialog(BluetoothDevice device) {
        bluetoothDevices.add(device);

        if(arrayAdapter != null) {
            arrayAdapter.add(device.getName());

            arrayAdapter.notifyDataSetChanged();
        } else {
            dialogList.add(device.getName());
        }
    }
}