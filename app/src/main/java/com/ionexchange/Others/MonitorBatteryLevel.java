package com.ionexchange.Others;

import static com.ionexchange.Others.PacketControl.CHARGE_CONTROL_PACKET;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ionexchange.Interface.DataReceiveCallback;

public class MonitorBatteryLevel extends BroadcastReceiver implements DataReceiveCallback {

    ApplicationClass mAppClass;
    Context mContext;


    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra("level", 0);
        mAppClass = (ApplicationClass) context.getApplicationContext();
        mContext = context;
        if (level < 60) {
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                    WRITE_PACKET + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + CHARGE_CONTROL_PACKET + SPILT_CHAR +
                    "1" + SPILT_CHAR
            );
        } else if (level > 98) {

            mAppClass.sendPacket(this, "");
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                    WRITE_PACKET + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + CHARGE_CONTROL_PACKET + SPILT_CHAR +
                    "0" + SPILT_CHAR
            );
        }
    }

    @Override
    public void OnDataReceive(String data) {

    }
}
