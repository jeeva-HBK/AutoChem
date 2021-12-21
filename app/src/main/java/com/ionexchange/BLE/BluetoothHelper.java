package com.ionexchange.BLE;

import static com.ionexchange.Activity.BaseActivity.msDismissProgress;
import static com.ionexchange.Others.ApplicationClass.bleConnected;
import static com.ionexchange.Others.ApplicationClass.lastKeepAliveData;
import static com.ionexchange.Others.PacketControl.ENDPACKET;
import static com.ionexchange.Others.PacketControl.STARTPACKET;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.Singleton.KeepAlive;

import java.util.ArrayList;
import java.util.List;

public class BluetoothHelper implements SerialListener {
    private static final String TAG = "BLE";
    private static final String TAG1 = "Bluetooth";
    private static BluetoothHelper helper;
    private static String END_CHAR = "}";
    BluetoothConnectCallback connectCallback;
    private BluetoothScannerCallback callBack;
    private Boolean isRegistered = false;
    private Activity mActivity;
    private BluetoothAdapter mAdapter;
    private String searchParam = "";
    private SearchType searchType;
    private int mScanDeviceType;
    private DataReceiveCallback dataCallback, mTempCallback;
    private List<BluetoothDevice> result;
    private StringBuilder dataBuilder;
    private ConnectStatus mConnectStatus = ConnectStatus.NOTCONNECTED;
    private BluetoothDevice mConnectedDevice;
    private Context mContext;
    private boolean isConnected = false,
            dataReceived = false;
    private String mConnectPacket, mExpectedResponse;
    private BroadcastReceiver scanResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getType() == mScanDeviceType) {
                    if (searchType != null) {
                        switch (searchType) {
                            case ByName:
                                if (device.getName().equals(searchParam)) {
                                    if (!result.contains(device)) {
                                        result.add(device);
                                        callBack.SearchResult(device);
                                    }
                                }
                                break;
                            case ByMAC:
                                if (device.getAddress().equals(searchParam)) {
                                    if (!result.contains(device)) {
                                        result.add(device);
                                        callBack.SearchResult(device);
                                    }
                                }
                                break;
                        }
                    } else {
                        if (!result.contains(device)) {
                            result.add(device);
                            callBack.OnDeviceFoundUpdate(result);
                        }
                    }
                }
            }
            if (intent.getAction().equals((BluetoothAdapter.ACTION_DISCOVERY_FINISHED))) {
                unRegisterBluetoothReciver();
                mAdapter.cancelDiscovery();
                callBack.OnScanCompleted(result);
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                    //   sendBroadcast(new Intent().putExtra("Received_Data", "DISCONNECTED").setAction("DEMOACTION").addCategory(Intent.CATEGORY_DEFAULT));
                }
            }
        }
    };

    private CountDownTimer mConnectionListener = new CountDownTimer(Long.MAX_VALUE, 1000) {
        @Override
        public void onTick(long l) {
            if (isConnected) {
                return;
            }
            mConnectionListener.cancel();
            reconnect();
        }

        @Override
        public void onFinish() {

        }
    };

    private BluetoothHelper(Activity mActivity) {
        this.mActivity = mActivity;
        BluetoothManager mManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        if (mManager != null) {
            mAdapter = mManager.getAdapter();
        }
        result = new ArrayList<>();
        dataBuilder = new StringBuilder();
    }

    public static BluetoothHelper getInstance(Activity mActivity) {
        if (helper == null) {
            helper = new BluetoothHelper(mActivity);
        }
        return helper;
    }

    public static BluetoothHelper getInstance() {
        if (helper != null) {
            return helper;
        } else {
            return null;
        }
    }

    public String getConnectedBluetoothMac() {
        if (mConnectedDevice != null) {
            return mConnectedDevice.getAddress();
        } else {
            return "";
        }
    }

    public ConnectStatus getConnectionStatus() {
        return mConnectStatus;
    }

    private void reconnect() {
        try {
            disConnect();
            CountDownTimer packetTimeout = new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    dataCallback = mTempCallback;
                    mConnectStatus = ConnectStatus.NOTCONNECTED;
                    mConnectionListener.start();
                }
            };

            connectBLE(mContext, mConnectedDevice, new BluetoothConnectCallback() {
                @Override
                public void OnConnectSuccess() {
                    try {
                        packetTimeout.start();
                        mTempCallback = getDataCallback();

                        sendDataBLE(new DataReceiveCallback() {
                            @Override
                            public void OnDataReceive(String data) {
                                dataCallback = mTempCallback;
                                mConnectionListener.start();
                                if (data.contains(mExpectedResponse)) {
                                    isConnected = true;
                                    bleConnected.set(true);
                                    mConnectStatus = ConnectStatus.CONNECTED;
                                    packetTimeout.cancel();
                                } else {
                                    isConnected = false;
                                    bleConnected.set(false);
                                    mConnectStatus = ConnectStatus.NOTCONNECTED;
                                    mConnectionListener.start();
                                }

                            }
                        }, mConnectPacket);

                    } catch (Exception e) {
                        dataCallback = mTempCallback;
                        mConnectionListener.start();
                        e.printStackTrace();
                    }
                }

                @Override
                public void OnConnectFailed(Exception e) {
                    dataCallback = mTempCallback;
                    mConnectionListener.start();
                }
            });
        } catch (Exception e) {
            dataCallback = mTempCallback;
            mConnectionListener.start();
            e.printStackTrace();
        }
    }

    public void autoReconnect(String requestPacket, String expectedResponse) {
        this.mConnectPacket = requestPacket;
        this.mExpectedResponse = expectedResponse;
        if (mConnectionListener != null) {
            mConnectionListener.start();
        }
    }

    public void turnOn() {
        if (mAdapter != null)
            if (!mAdapter.isEnabled())
                mAdapter.enable();
    }

    public boolean isConnected() {
        SerialSocket socket = SerialSocket.getInstance();
        return socket.isConnected();
    }

    public void setConnected(boolean connectStatus) {
        if (connectStatus) {
            isConnected = true;
            bleConnected.set(true);
            mConnectStatus = ConnectStatus.CONNECTED;
        } else {
            isConnected = false;
            bleConnected.set(false);
            mConnectStatus = ConnectStatus.NOTCONNECTED;
        }

    }

    private void scan(BluetoothScannerCallback callBack) throws Exception {
        if (callBack == null) {
            throw new Exception("BluetoothScannerCallback is null");
        }
        if (mAdapter == null) {
            throw new Exception("Bluetooth adapter not initialized");
        }
        result.clear();
        this.callBack = callBack;
        mAdapter.startDiscovery();
        registerReceiverForBluetooth();
    }

    public void stopScan() {
        unRegisterBluetoothReciver();
        mAdapter.cancelDiscovery();
    }

    public void scanBLE(BluetoothScannerCallback callBack) throws Exception {
        mScanDeviceType = BluetoothDevice.DEVICE_TYPE_LE;
        this.searchType = null;
        scan(callBack);
    }

    public void scanBLE(BluetoothScannerCallback callBack, SearchType searchType, String param) throws Exception {
        mScanDeviceType = BluetoothDevice.DEVICE_TYPE_LE;
        this.searchParam = param;
        this.searchType = searchType;
        scan(callBack);
    }

    public void scanCLASSIC(BluetoothScannerCallback callBack) throws Exception {
        mScanDeviceType = BluetoothDevice.DEVICE_TYPE_CLASSIC;
        this.searchType = null;
        scan(callBack);
    }

    public void scanCLASSIC(BluetoothScannerCallback callBack, SearchType searchType, String param) throws Exception {
        mScanDeviceType = BluetoothDevice.DEVICE_TYPE_CLASSIC;
        this.searchParam = param;
        this.searchType = searchType;
        scan(callBack);
    }

    public void connectBLE(Context context, BluetoothDevice device, BluetoothConnectCallback connectCallback) throws Exception {
        mConnectStatus = ConnectStatus.CONNECTING;
        this.mContext = context;
        this.mConnectedDevice = device;
        this.connectCallback = connectCallback;
        SerialSocket socket = SerialSocket.getInstance();
        socket.connect(context, this, device);
    }


    public void sendDataBLE(DataReceiveCallback callback, String data) throws Exception {

        data = STARTPACKET + data + ENDPACKET;
        this.dataCallback = callback;
        SerialSocket socket = SerialSocket.getInstance();
        dataReceived = false;
        socket.write(this, data.getBytes());

        Log.e("Config --> ", data);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!dataReceived) {
                    msDismissProgress();
                }
            }
        }, 7000);
    }

    public DataReceiveCallback getDataCallback() {
        return this.dataCallback;
    }

    public void setDataCallback(DataReceiveCallback callback) {
        if (callback != null) {
            this.dataCallback = callback;
        }
    }

    private void registerReceiverForBluetooth() {
        if (!isRegistered) {
            isRegistered = true;
            IntentFilter DeviceFilter;
            DeviceFilter = new IntentFilter();
            DeviceFilter.addAction(BluetoothDevice.ACTION_FOUND);
            DeviceFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            DeviceFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            mActivity.registerReceiver(scanResult, DeviceFilter);
        }
    }

    private void unRegisterBluetoothReciver() {
        if (isRegistered) {
            isRegistered = false;
            mActivity.unregisterReceiver(scanResult);
        }
    }

    @Override
    public void onSerialConnect() {
        bleConnected.set(true);
        connectCallback.OnConnectSuccess();
    }

    @Override
    public void onSerialConnectError(Exception e) {
        connectCallback.OnConnectFailed(e);
    }

    @Override
    public void onSerialRead(byte[] data) {
        // Developer driven method
        // If you want to implement your method comment this and add new method
        // refer sendDataToCallback function on sending callbacks
        /*sendDataToCallback(new String(data));*/

        sendDataToCallback(new String(data));
    }

    public void disConnect() {
        SerialSocket socket = SerialSocket.getInstance();
        socket.disconnect();
    }

    private void sendDataToCallback(final String data) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataBuilder.append(data);
                if (dataBuilder.toString().contains(STARTPACKET) && dataBuilder.toString().contains(ENDPACKET)) {
                    /*indexOfSplit = dataBuilder.indexOf(END_CHAR);
                    String framedData = dataBuilder.toString().substring(0, indexOfSplit + (END_CHAR.length()));
                    String data = framedData.split("PSIPS")[1].split("PSIPE")[0];
                    String[] splitted = data.split(";");
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < splitted.length; i++) {
                        if (i != splitted.length - 1) {
                            builder.append(splitted[i]);
                            builder.append(";");
                        }
                    }
                    if (dataCallback != null) {
                           if (UtilMethods.checkCRC(builder.toString(), splitted[splitted.length - 1])) {
                            dataCallback.OnDataReceived(builder.toString());
                        } else {
                            dataCallback.OnDataReceivedError(new Exception("Invalid CRC"));
                        }
                        dataCallback.OnDataReceive(framedData);
                        Log.e(TAG, "Received <--" + framedData);
                    } else {
                        Toast.makeText(mContext, "Please Restart and Try Again", Toast.LENGTH_SHORT).show();
                    }
                    String excessData = dataBuilder.toString().substring(indexOfSplit + (END_CHAR.length()));
                    dataBuilder.setLength(0);
                    dataBuilder.append(excessData);*/

                    String mData = data;
                        String[] splitData = mData.split("\\*")[1].split("\\$");
                        if (splitData[0].equals("0")) { // ConfigurationPackets
                            String configPacket = "{*" + mData.substring(4);
                            dataReceived = true;
                            Log.e("Config <-", configPacket);
                            if (dataCallback != null) {
                                dataCallback.OnDataReceive(configPacket);
                            }
                        } else if (splitData[0].equals("1")) { // KeepAlivePackets
                            lastKeepAliveData = "{*" + mData.substring(4);
                            Log.e("keepAlive <-", lastKeepAliveData);
                            KeepAlive.getInstance().processKeepAlive(lastKeepAliveData, ApplicationClass.mContext);
                        }
                    }
            }
        });
    }

    @Override
    public void onSerialIoError(Exception e) {
        // reconnect();
        e.printStackTrace();
        dataCallback.OnDataReceive("FailedToConnect");
    }

    @Override
    public void onDisconnected() {
        if(isConnected) {
            BaseActivity.kickOut();
        }
        isConnected = false;
        bleConnected.set(false);
        Log.e(TAG, "onDisconnected: ");

    }

    public enum SearchType {
        ByName,
        ByMAC
    }

}