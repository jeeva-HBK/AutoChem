package com.ionexchange.BLE;

public interface BluetoothDataCallback {
    public void OnDataReceived(String data);

    public void OnDataReceivedError(Exception e);
}
