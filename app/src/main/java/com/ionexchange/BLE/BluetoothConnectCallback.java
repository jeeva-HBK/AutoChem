package com.ionexchange.BLE;

public interface BluetoothConnectCallback {
    void OnConnectSuccess();

    void OnConnectFailed(Exception e);
}
