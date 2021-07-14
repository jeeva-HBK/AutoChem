package com.ionexchange.Others;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.ionexchange.Others.TCP.RECEIVED_DATA;

public class ApplicationClass extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "ApplicationClass";
    Context mContext;
    DataListener mTCPDataListener;
    CountDownTimer mPacketTimeout;

    BroadcastReceiver mTCPDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String data;
                data = intent.getStringExtra(RECEIVED_DATA);
                if (mPacketTimeout != null) {
                    mPacketTimeout.cancel();
                }
                if (mTCPDataListener != null) {
                    // Without CRC
                    mTCPDataListener.OnDataReceived(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(this);

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated: ");
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.e(TAG, "onActivityStarted: ");
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.e(TAG, "onActivityResumed: ");
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.e(TAG, "onActivityPaused: ");
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.e(TAG, "onActivityStopped: ");
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Log.e(TAG, "onActivitySaveInstanceState: ");
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.e(TAG, "onActivityDestroyed: ");
    }


    public void sendPacket(final DataListener listener, String packet) {
        this.mTCPDataListener = listener;
        if (mPacketTimeout != null) {
            mPacketTimeout.cancel();
        }
        mPacketTimeout = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Toast.makeText(mContext, "Request TimedOut", Toast.LENGTH_SHORT).show();
                listener.OnDataReceived("timeOut");
            }
        };
        if (!packet.equals("")) {
            //  mPacketTimeout.start();
        }
        Intent mServiceIntent = new Intent(mContext,
                TCP.class);
        mServiceIntent.putExtra("test", packet);
        mContext.startService(mServiceIntent);
    }

    public interface DataListener {
        void OnDataReceived(String data);
    }
}
