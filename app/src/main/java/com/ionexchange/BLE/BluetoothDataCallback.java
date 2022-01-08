package com.ionexchange.BLE;

public interface BluetoothDataCallback {
    void OnDataReceived(String data);

    void OnDataReceivedError(Exception e);
}
