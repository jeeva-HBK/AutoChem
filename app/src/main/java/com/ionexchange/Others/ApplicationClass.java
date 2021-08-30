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
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.R;

import java.util.regex.Matcher;

import static android.util.Patterns.IP_ADDRESS;
import static com.ionexchange.Others.TCP.ACTION_MyIntentService;

/* Created by Jeeva on 13/07/2021 */
public class ApplicationClass extends Application {
    private static final String TAG = "ApplicationClass";

    public static int userType = 0; // 0 - None | 1 - Basic | 2 - intermediate | 3 - Advanced

    public static String[] sensorActivationArr = {"ENABLE", "DISABLE"},
            inputTypeArr = {"pH", "ORP", "Temp", "Flow/Water Meter", "Conductivity", "Toroidal", "Analog Input", "Tank Level", "Digital Sensor", "Modbus Sensor"},
            bufferArr = {"Auto", "Manual"},
            tempLinkedArr = {"None", "Temperature 1", "Temperature 2", "Temperature 3"},
            resetCalibrationArr = {"No Reset", "Reset"},
            unitArr = {" Ã‚ÂµS/cm", " mS/cm", "S/cm"},
            typeOfValueRead = {"None", "Fluorescence value", "Turbidity Value", "Corrosion rate", "Pitting rate", "Fluorescence value", "Tagged Polymer value"},

    flowMeterTypeArr = {"Analog Flow Meter", "Flow Meter Contactor", "Paddle Wheel", "Feed Monitor"},
            flowUnitArr = {"Volume", "Gallons", "Litres", "Cubic Meters", "Millions of Gallons"},
            scheduleResetArr = {"No Schedule Reset", "Daily", "Monthly", "Annually"},

    digitalArr = {"NC", "NO"},
            modBusTypeArr = {"ST500", "CR300CS", "CR-300 CU", "ST-590", "ST-588", "ST-500 RO"},
            modBusUnitArr = {"ppb", "ppm", "mpy"},
            analogTypeArr = {"(4-20mA)", "(0 Ã¢â‚¬â€œ 10V)"},
            analogUnitArr = {"ma", "V"},

    calculationArr = {"Difference", "Ratio", "Total", "% Difference"},
            sensorsViArr = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45"},
            interlockChannel = {"Digital Input - 1", "Digital Input - 2", "Digital Input - 3", "Digital Input - 4", "Digital Input - 5", "Digital Input - 6", "Digital Input - 7", "Digital Input - 8", "Tank Level - 1", "Tank Level - 2", "Tank Level - 3", "Tank Level - 4", "Tank Level - 5", "Tank Level - 6", "Tank Level - 7", "Tank Level - 8"},
            functionMode, modeInhibitor = {"Continuous", "Bleed/Blow Down", "Water Meter/Biocide"},
            modeSensor = {"On/Off", "PID", "Fuzzy"}, modeAnalog = {"Disable", "Probe", "Test", "Pump Status", "Dosing"},
            flowMeters = {"Flow Meter 1", "Flow Meter 2", "Flow Meter 3", "Flow Meter 4", "Flow Meter 5", "Flow Meter 6", "Flow Meter 7", "Flow Meter 8"},
            bleedRelay = {"Output 01", "Output 02", "Output 03", "Output 04", "Output 05", "Output 06", "Output 07", "Output 08", "Output 09", "Output 10", "Output 11", "Output 12", "Output 13", "Output 14"},

    inputSensors = {"Input 1", "Input 2", "Input 3", "Input 4", "Input 5", "Input 6", "Input 7", "Input 8", "Input 9", "Input 10", "Input 11", "Input 12", "Input 13", "Input 14", "Input 15", "Input 16", "Input 17", "Input 18", "Input 19", "Input 20", "Input 21", "Input 22", "Input 23", "Input 24",
            "Input 25", "Input 26", "Input 27", "Input 28", "Input 29", "Input 30", "Input 31", "Input 32", "Input 33", "Input 34", "Input 35", "Input 36", "Input 37", "Input 38", "Input 39", "Input 40", "Input 41", "Input 42", "Input 43", "Input 44", "Input 45", "Input 46", "Input 47", "Input 48", "Input 49", "Input 50", "Input 51", "Input 52"},
            doseTypeArr = {"Below", "Above"},
            inputAnalogSensors = {"Input 1", "Input 2", "Input 3", "Input 4", "Input 5", "Input 6", "Input 7", "Input 8", "Input 9", "Input 10", "Input 11", "Input 12","Input 13"},
    OutputBleedFlowRate = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14"},

    TemperatureCompensationType = {"Linear temperature Compensation","standard NaCl temperature compensation"},

    timerOutputMode = {"Timer", "Timer Flow", "Timer Disabled"},
    timerFlowSensor = {"Flow Sensor - 1", "Flow Sensor - 2", "Flow Sensor - 3", "Flow Sensor - 4", " Flow Sensor - 5", "Flow Sensor - 6",
            "Flow Sensor - 7", "Flow Sensor - 8"},
    accessoryTimerMode = {"Timer Safety", "Timer Safety Flow", "Disabled"},
    accessoryType = {" ON Before", "OFF Before", "ON After", " OFF After", "ON With", "OFF with"};


    /* Static Variables */
    static String mIPAddress = "192.168.1.107", Packet;
    static int mPortNumber = 9760;

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


    public static String formDigits(int digits, String value) {
        String finalDigits = null;
        switch (digits) {
            case 0:
                // Return without Forming digits
                finalDigits = value;
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

            case 7:
                finalDigits = ("0000000" + value).substring(value.length());
                break;

            case 12:
                finalDigits = ("000000000000" + value).substring(value.length());
                break;
        }
        return finalDigits;
    }

    public static Integer findDecimal(EditText editText) {
        int throwError = 0;
        String[] findDecimalEdtTxt = editText.getText().toString().split("\\.");
        try {
            if (!editText.getText().toString().contains(".") && editText.getText().toString().length() > 4) {
                return 1;
            }else if (findDecimalEdtTxt[0].length() > 4) {
                return 1;
            } else if (findDecimalEdtTxt[1].isEmpty()) {
                return 1;
            } else if (findDecimalEdtTxt[1].length() > 2) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throwError = 1;
        }
        return throwError;
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

    public static Boolean isValidIp(String ip) {
        Matcher matcher = IP_ADDRESS.matcher(ip);
        return matcher.matches();
    }

    public static Boolean isValidPort(String ports) {
        try {
            int port = Integer.parseInt(ports);
            return port >= 0000 && port <= 65535;
        } catch (Exception e) {
            return false;
        }
    }

    public static int validDecimalField(EditText editText, int prefixDigit, int suffixDigit) {
        int throwError = 0;
        String[] findDecimalEdtTxt = editText.getText().toString().split("\\.");
        try {
            if (findDecimalEdtTxt[0].length() > prefixDigit) {
                return 1;
            } else if (findDecimalEdtTxt[1].isEmpty()) {
                return 1;
            } else if (findDecimalEdtTxt[1].length() > suffixDigit) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throwError = 1;
        }
        return throwError;
    }
}
