package com.ionexchange.Others;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.R;

import static com.ionexchange.Others.TCP.ACTION_MyIntentService;

/* Created by Jeeva on 13/07/2021 */
public class ApplicationClass extends Application {
    private static final String TAG = "ApplicationClass";

    public static String[] sensorActivationArr = {"ENABLE", "DISABLE"},
            inputTypeArr = {"pH", "ORP", "Temp", "Flow/Water Meter", "Conductivity", "Toroidal", "Analog Input", "Tank Level", "Digital Sensor", "Modbus Sensor"},
            bufferArr = {"Auto", "Manual"},
            tempLinkedArr = {"None", "Temperature 1", "Temperature 2", "Temperature 3"},
            resetCalibrationArr = {"No Reset", "Reset"},
            unitArr = {" µS/cm", " mS/cm", "S/cm"};

    /* Static Variables */
    static String mIPAddress = "192.168.1.104", Packet;
    static int mPortNumber = 6000;

    public static CountDownTimer packetTimeOut;
    Context mContext;
    public TCP tcp;

    DataReceiveCallback listener;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String data;
                if (packetTimeOut != null) {
                    packetTimeOut.cancel();
                }
                if (listener != null) {
                    data = intent.getStringExtra("received_data");
                    if (data.contains("restart")) {
                        return;
                    }
                    listener.OnDataReceive(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                try {
                    mContext = activity;
                    registerReceiver();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                unregisterReceiver();
            }
        });
    }


    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_MyIntentService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        mContext.registerReceiver(receiver, intentFilter);
    }

    public void unregisterReceiver() {
        mContext.unregisterReceiver(receiver);
    }


    public String formDigits(int digits, String value) {
        String finalDigits = null;
        switch (digits) {
            case 0:
                // Return without Forming digits
                finalDigits = (value).substring(value.length());
                break;
            case 1:
                finalDigits = ("0" + value).substring(value.length());
                break;
            case 2:
                finalDigits = ("00" + value).substring(value.length());
                break;

            case 3:
                finalDigits = ("000" + value).substring(value.length());
                break;

            case 4:
                finalDigits = ("0000" + value).substring(value.length());
                break;

            case 5:
                finalDigits = ("00000" + value).substring(value.length());
                break;

            case 6:
                finalDigits = ("000000" + value).substring(value.length());
                break;
        }
        return finalDigits;
    }

    public void sendPacket(final DataReceiveCallback listener, String packet) {
        tcp = new TCP();
        this.listener = listener;
        if (packetTimeOut != null) {
            packetTimeOut.cancel();
        }
        packetTimeOut = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Intent intentResponse = new Intent();
                intentResponse.setAction(ACTION_MyIntentService);
                intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                intentResponse.putExtra("news", "DEVICE_TIMEOUT");
                mContext.sendBroadcast(intentResponse);
            }
        };
        if (!packet.equals("")) {
            packetTimeOut.start();
        }
        Intent mServiceIntent = new Intent(mContext,
                TCP.class);
        mServiceIntent.putExtra("dataPacket", packet);
        mContext.startService(mServiceIntent);
    }

    public void castFrag(FragmentManager parentFragmentManager, int host, Fragment fragment) {
        parentFragmentManager.beginTransaction().replace(host, fragment).commit();
    }

    public void showSnackBar(Context context, String message) {
        Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(R.id.cod), message, Snackbar.LENGTH_SHORT);
        TextView tv = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

}
