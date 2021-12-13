package com.ionexchange.BLE;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface BluetoothScannerCallback {
    public void OnScanCompleted(List<BluetoothDevice> devices);

    public void SearchResult(BluetoothDevice device);

    public void OnDeviceFoundUpdate(List<BluetoothDevice> devices);
}
