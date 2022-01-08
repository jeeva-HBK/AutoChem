package com.ionexchange.BLE;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface BluetoothScannerCallback {
    void OnScanCompleted(List<BluetoothDevice> devices);

    void SearchResult(BluetoothDevice device);

    void OnDeviceFoundUpdate(List<BluetoothDevice> devices);
}
