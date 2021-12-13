package com.ionexchange.Activity;

import static com.ionexchange.Others.ApplicationClass.lastKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.triggerWebService;
import static com.ionexchange.Singleton.SharedPref.pref_MACADDRESS;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ionexchange.Adapters.BluetoothListAdapter;
import com.ionexchange.BLE.BluetoothConnectCallback;
import com.ionexchange.BLE.BluetoothDataCallback;
import com.ionexchange.BLE.BluetoothHelper;
import com.ionexchange.BLE.BluetoothScannerCallback;
import com.ionexchange.Interface.ItemClickListener;
import com.ionexchange.Interface.VolleyCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.Singleton.SharedPref;
import com.ionexchange.databinding.ActivityConnectionBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConnectionActivity extends AppCompatActivity implements BluetoothDataCallback, ItemClickListener {
    ApplicationClass mAppClass;
    private static final String TAG = "ConnectionActivity";
    ActivityConnectionBinding mBinding;

    Context mContext;
    SharedPreferences preferences;
    ArrayList<String> deviceList;
    ArrayList<Map<String, String>> mDeviceList;
    List<BluetoothDevice> scannedDevices;
    BluetoothDevice mBleDevice;
    boolean dataReceived = false;
    boolean isVisible = false;
    BluetoothListAdapter bluetoothListAdapter;
    AlertDialog dispenseAlert, panAlert;
    ImageView iv;
    TextView tv;

    private ActivityResultContracts.RequestMultiplePermissions requestMultiplePermissionsContract;
    private ActivityResultLauncher<String[]> multiplePermissionActivityResultLauncher;

    final String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(ConnectionActivity.this, R.layout.activity_connection);
        mAppClass = (ApplicationClass) getApplication();
        /* mBinding = DataBindingUtil.setContentView(this, R.layout.activity_connection);

        mAppClass = (ApplicationClass) getApplication();

        if (preferences.getBoolean("prefLoggedIn", false)) {
            proceedToBaseAct();
        }
        try {
            mBinding.ipAdressEdt.append(preferences.getString("prefIp", ""));
            mBinding.portEdt.append(preferences.getString("prefPort", ""));
        } catch (Exception e) {

        }

        mActivity = new BaseActivity();
        mBinding.button.setOnClickListener(View -> {
            if (validateField()) {
                editor.putBoolean("prefLoggedIn", true);
                editor.putString("prefIp", mBinding.ipAdressEdt.getText().toString());
                editor.putString("prefPort", mBinding.portEdt.getText().toString());
                editor.putString("prefPassword", mBinding.passwordEdt.getText().toString());
                editor.apply();
                mIPAddress = mBinding.ipAdressEdt.getText().toString();
                mPortNumber = Integer.parseInt(mBinding.portEdt.getText().toString());
                DEVICE_PASSWORD = mBinding.passwordEdt.getText().toString();
                proceedToBaseAct();
            }
        });*/
        mContext = getApplicationContext();
        requestMultiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        multiplePermissionActivityResultLauncher = registerForActivityResult(requestMultiplePermissionsContract, isGranted -> {
            if (isGranted.containsValue(false)) {
                Log.d("PERMISSIONS", "At least one of the permissions was not granted, launching again...");
                multiplePermissionActivityResultLauncher.launch(PERMISSIONS);
            } else if (isGranted.containsValue(true)) {
                Log.e(TAG, "onViewCreated" + "pGranted");
                init();
            }
        });

        askPermissions(PERMISSIONS);
        mBinding.txtConnect.setOnClickListener((view1 -> {
            if (mBinding.txtConnect.getText().toString().equals("Rescan")) {
                mBinding.txtConnect.setText("Scanning..");
                startScan();
            }
        }));
    }

    private void askPermissions(String[] permission) {
        if (!hasPermissions(permission)) {
            Log.d("PERMISSIONS", "Launching multiple contract permission launcher for ALL required permissions");
            multiplePermissionActivityResultLauncher.launch(permission);
        } else {
            Toast.makeText(getApplicationContext(), "Permissions Granted", Toast.LENGTH_SHORT).show();
            init();
        }
    }

    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission is not granted: " + permission);
                    return false;
                }
                Log.d("PERMISSIONS", "Permission already granted: " + permission);
            }
            return true;
        }
        return false;
    }

    void init() {
        mBinding.deviceMacAddress.setText(getBluetoothMacAddress());
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mBinding.btnScan.setAlpha(.5f);
        mBinding.btnScan.setEnabled(false);
        mDeviceList = new ArrayList<>();
        deviceList = new ArrayList<>();
        scannedDevices = new ArrayList<>();
        mBinding.rvBluetoothList.setLayoutManager(new LinearLayoutManager(mContext));
        startScan();
    }

    private String getBluetoothMacAddress() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress();
    }



    private void startScan() {
        deviceList.clear();
        // mBinding.btnScan.setText(R.string.scanning);
        mBinding.btnScan.setEnabled(false);
        BluetoothHelper helper = BluetoothHelper.getInstance(this);
        helper.turnOn();
        helper.disConnect();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    helper.scanBLE(new BluetoothScannerCallback() {
                        @Override
                        public void OnScanCompleted(List<BluetoothDevice> devices) {
                            if (devices.size() == 0) {
                                Log.e(TAG, "OnScanCompleted: Refreshing");
                                stopScan();
                                startScan();
                                mBinding.txtConnect.setText("Scanning..");
                            }
                        }

                        @Override
                        public void SearchResult(BluetoothDevice device) {
                            Log.e(TAG, "SearchResult: " + device);
                        }

                        @Override
                        public void OnDeviceFoundUpdate(List<BluetoothDevice> devices) {
                            scannedDevices.clear();
                            scannedDevices.addAll(devices);
                            for (BluetoothDevice device : devices) {
                                String listItem = (device.getName() == null ? "unknown error" : device.getName())
                                        + "\n" + (device.getAddress() == null ? "unknown error" : device.getAddress());
                                if (!deviceList.contains(listItem)) {
                                    deviceList.add(listItem);
                                    updateRV(deviceList);
                                }
                            }
                            // mActivity.dismissProgress();
                            mBinding.txtConnect.setText("Rescan");
                        }
                    });
                } catch (Exception e) {
                    mBinding.txtConnect.setText("Rescan");
                    stopScan();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stopScan() {
        mBinding.txtConnect.setText("SCAN");
        mBinding.txtConnect.setEnabled(true);
        BluetoothHelper helper = BluetoothHelper.getInstance(this);
        helper.stopScan();
    }

    private void updateRV(ArrayList<String> deviceList) {
        try {
            List<String> list = new ArrayList<>();
            if (!preferences.getString("savedMac", "").equals("")
                    && preferences.getString("savedMac", "") != null) {
                JSONArray arr = new JSONArray(preferences.getString("savedMac", ""));
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    list.add(obj.getString("mac"));
                }
            }
            bluetoothListAdapter = new BluetoothListAdapter(deviceList, list, this);
            mBinding.rvBluetoothList.setAdapter(bluetoothListAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnDataReceived(String data) {
        Log.e(TAG, "OnDataReceived: " + data);
    }

    @Override
    public void OnDataReceivedError(Exception e) {
        Log.e(TAG, "OnDataReceivedError: " + e);
    }

    private void sendPacket(String packet) {
        mAppClass.sendPacket(this::OnDataReceived, packet);
    }

    @Override
    public void OnItemClick(int pos) {
        stopScan();
        mBinding.txtConnect.setText("Connecting");
        // mActivity.showProgress();
        BluetoothHelper helper = BluetoothHelper.getInstance(this);
        helper.disConnect();
        mBleDevice = scannedDevices.get(pos);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    helper.connectBLE(mContext, mBleDevice, new BluetoothConnectCallback() {
                        @Override
                        public void OnConnectSuccess() {
                            Log.e(TAG, "OnConnectSuccess: ");
                            startApp(mBleDevice.getAddress());
                            /*try {
                             */
                            /*runOnUiThread(new Runnable() {
                                    int i = 0;
                                    @Override
                                    public void run() {
                                        while (i < 5) {
                                            if (!dataReceived) {
                                                sendPacket("01");
                                            }
                                            i++;
                                        }
                                    }
                                });*/
                            /*
                            } catch (Exception e) {
                                Log.e(TAG, "OnConnectSuccess: Catch");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //  mActivity.dismissProgress();
                                        //  mAppClass.showSnackBar(mContext, "Error Occurred");
                                        mBinding.txtConnect.setText("Rescan");
                                        stopScan();
                                    }
                                });
                                e.printStackTrace();
                            }*/
                        }

                        @Override
                        public void OnConnectFailed(Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stopScan();
                                    mBinding.txtConnect.setText("Rescan");
                                    Log.e(TAG, "OnConnectSuccess: Failed");
                                }
                            });
                        }
                    });
                } catch (
                        Exception e) {
                    stopScan();
                    mBinding.txtConnect.setText("Rescan");
                    e.printStackTrace();
                }
            }
        });
    }

    private void startApp(String macAddress) {
        SharedPref.write(pref_MACADDRESS, macAddress);
        startActivity(new Intent(this, BaseActivity.class));
        triggerWebService.set(true);
    }

    @Override
    public void onSaveClicked(String mac) {

    }

    @Override
    public void onUnSave(String mac) {

    }

}
