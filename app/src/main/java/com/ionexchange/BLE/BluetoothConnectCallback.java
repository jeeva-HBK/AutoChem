package com.ionexchange.BLE;

public interface BluetoothConnectCallback {
    public void OnConnectSuccess();
    public void OnConnectFailed(Exception e);
}
