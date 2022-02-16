package com.ionexchange.Others;

import static android.content.Intent.ACTION_BATTERY_CHANGED;
import static android.util.Patterns.IP_ADDRESS;
import static com.ionexchange.Database.WaterTreatmentDb.DB_NAME;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Singleton.SharedPref.pref_CONTROLLERISACTIVE;
import static com.ionexchange.Singleton.SharedPref.pref_CONTROLLERPASSWORD;
import static com.ionexchange.Singleton.SharedPref.pref_SITEID;
import static com.ionexchange.Singleton.SharedPref.pref_SITELOCATION;
import static com.ionexchange.Singleton.SharedPref.pref_SITENAME;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.BLE.BluetoothHelper;
import com.ionexchange.Database.Dao.DefaultLayoutConfigurationDao;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.MainConfigurationDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.Dao.TimerConfigurationDao;
import com.ionexchange.Database.Dao.UserManagementDao;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.DefaultLayoutConfigurationEntity;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.KeepAliveCurrentEntity;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputKeepAliveEntity;
import com.ionexchange.Database.Entity.TimerConfigurationEntity;
import com.ionexchange.Database.Entity.UsermanagementEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Interface.VolleyCallback;
import com.ionexchange.R;
import com.ionexchange.Singleton.ApiService;
import com.ionexchange.Singleton.KeepAlive;
import com.ionexchange.Singleton.SharedPref;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;

/*  Created by Jeeva on 13/07/2021  */
public class ApplicationClass extends Application {
    private static final String TAG = "ApplicationClass";
    public static final String LOG_DIVIDER = " <----------------------------------------------------------------------------------------------------------------------------> ";
    private static final String API = "API";
    private static final String API1 = "SERVER";

    public static String defaultPassword = "123456";
    public static String[] sensorActivationArr = {"ENABLE", "DISABLE"},
            roleType = {"None", "Basic", "Intermediate", "Advanced"},
            sensorTypeArr = {"Sensor", "Temperature", "Modbus", "Analog Input", "Flow/Water Meter",
                    "Digital Input", "Tank Level",},
            inputTypeArr = {"pH", "ORP", "Temperature", "Flow/Water Meter", "Contacting Conductivity",
                    "Toroidal Conductivity", "Analog Input", "Tank Level", "Digital Input", "Modbus Sensor"},
            analogInputArr = {"Analog Input", "pH", "ORP", "Temperature", "Flow/Water Meter",
                    "Contacting Conductivity", "Toroidal Conductivity", "Tank Level", "Modbus Sensor"},
            bufferArr = {"Auto", "Manual"},
            tempLinkedArr = {"None", "Temperature 1", "Temperature 2", "Temperature 3"},
            resetCalibrationArr = {"No Reset", "Reset"},
            unitArr = {"µS/cm", "mS/cm", "S/cm"},
            resetFlowTotalArr = {"No reset", "Reset"},
            sensorSequenceNumber = {"1-Sensor", "2-Sensor", "3-Sensor", "4-Sensor", "5-Sensor", "6-Sensor"},
            levelsensorSequenceNumber = {"None", "Tank Level - 1", "Tank Level - 2", "Tank Level - 3", "Tank Level - 4",
                    "Tank Level - 5", "Tank Level - 6", "Tank Level - 7", "Tank Level - 8"},
            digitalsensorSequenceNumber = {"None", "Digital Sensor - 1", "Digital Sensor - 2", "Digital Sensor - 3", "Digital Sensor - 4",
                    "Digital Sensor - 5", "Digital Sensor - 6", "Digital Sensor - 7", "Digital Sensor - 8"},
            totalTimeArr = {"NC", "NO"},
            flowmeterSequenceNumber = {"None", "Flow Meter - 1", "Flow Meter - 2", "Flow Meter - 3", "Flow Meter - 4",
                    "Flow Meter - 5", "Flow Meter - 6", "Flow Meter - 7", "Flow Meter - 8"},
            analogSequenceNumber = {"None", "Analog - 1", "Analog - 2", "Analog - 3", "Analog - 4",
                    "Analog - 5", "Analog - 6", "Analog - 7", "Analog - 8"},
            typeOfValueRead = {"None", "Fluorescence", "Turbidity", "Corrosion rate", "Pitting rate", "Fluorescence", "Tagged Polymer"},
            flowMeterTypeArr = {"Analog", "Contactor", "Paddle Wheel", "Feed Monitor"},
            flowUnitArr = {"Volume", "Gallons", "Litres", "Cubic Meters", "Millions of Gallons"},
            scheduleResetArr = {"No Schedule Reset", "Daily", "Monthly", "Annually"},
            totalAlarmMode = {"Interlock", "Maintain"},
            flowAlarmMode = {"Disable", "Interlock", "Maintain"},

    digitalArr = {"NC", "NO"},
            modBusTypeArr = {"ST500", "CR300 CS", "CR-300 CU", "ST-590", "ST-588", "ST-500 RO"},
            modBusUnitArr = {"ppb", "ppm", "mpy", "ntu"},
            analogTypeArr = {"(4-20mA)", "(0-10V)"},
            typeArr = {"None", "Fluorescence", "Turbidity Value", "Corrosion rate", "Pitting rate"
                    , "Fluorescence value(ST588)", "Tagged Polymer value"},
            analogUnitArr = {"mA", "V"},

    calculationArr = {"Difference", "Ratio", "Total", "% Difference"},
            sensorArr = {"1", "2", "3", "4"}, temperatureArr = {"15", "16", "17"},
            modbusArr = {"5", "6", "7", "8", "9", "10", "11", "12", "13", "14"},
            analogArr = {"18", "19", "20", "21", "22", "23", "24", "25"},
            flowmeterArr = {"26", "27", "28", "29", "30", "31", "32", "33"},
            digitalSensorArr = {"34", "35", "36", "37", "38", "39", "40", "41"},
            tankArr = {"42", "43", "44", "45", "46", "47", "48", "49"},
            sensorsViArr = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49"},
            interlockChannel = {"None", "Digital Input - 1", "Digital Input - 2", "Digital Input - 3", "Digital Input - 4", "Digital Input - 5", "Digital Input - 6", "Digital Input - 7", "Digital Input - 8", "Tank Level - 1", "Tank Level - 2", "Tank Level - 3", "Tank Level - 4", "Tank Level - 5", "Tank Level - 6", "Tank Level - 7", "Tank Level - 8"},

    functionMode,
            fMode = {"Disable", "Inhibitor", "sensor", "Analog"},
            modeInhibitor = {"Continuous", "Bleed/Blow Down", "Water Meter/Biocide"},
            modeSensor = {"On/Off", "PID", "Fuzzy"}, modeAnalog = {"Disable", "Probe", "Test", "Pump Status", "Dosing"},
            flowMeters = {"Flow Meter 1", "Flow Meter 2", "Flow Meter 3", "Flow Meter 4", "Flow Meter 5", "Flow Meter 6", "Flow Meter 7", "Flow Meter 8"},
            bleedRelay = {"Output 01", "Output 02", "Output 03", "Output 04", "Output 05", "Output 06", "Output 07", "Output 08", "Output 09", "Output 10", "Output 11", "Output 12", "Output 13", "Output 14"},

    inputSensors = {"Input 1", "Input 2", "Input 3", "Input 4", "Input 5", "Input 6", "Input 7", "Input 8", "Input 9", "Input 10", "Input 11", "Input 12", "Input 13", "Input 14", "Input 15", "Input 16", "Input 17", "Input 18", "Input 19", "Input 20", "Input 21", "Input 22", "Input 23", "Input 24",
            "Input 25", "Input 26", "Input 27", "Input 28", "Input 29", "Input 30", "Input 31", "Input 32", "Input 33", "Input 34", "Input 35", "Input 36", "Input 37", "Input 38", "Input 39", "Input 40", "Input 41", "Input 42", "Input 43", "Input 44", "Input 45"},
            doseTypeArr = {"Below", "Above"},
            inputAnalogSensors = {"Input 1", "Input 2", "Input 3", "Input 4", "Input 5", "Input 6", "Input 7", "Input 8", "Input 9", "Input 10", "Input 11", "Input 12", "Input 13"},
            OutputBleedFlowRate = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14"},

    TemperatureCompensationType = {"Linear", "Standard NaCl"},

    timerOutputMode = {"Timer", "Timer Flow", "Timer Disabled"},
            timerFlowSensor = {"Flow Sensor - 1", "Flow Sensor - 2", "Flow Sensor - 3", "Flow Sensor - 4", "Flow Sensor - 5", "Flow Sensor - 6",
                    "Flow Sensor - 7", "Flow Sensor - 8"},
            accessoryTimerMode = {"Timer Safety", "Timer Safety Flow", "Disabled"},
            accessoryType = {" ON Before", "OFF Before", "ON After", " OFF After", " ON With", " OFF with"},
            outputStatusarr = {"Disabled", "Auto OFF", "Auto ON", "Manual OFF", "Manual ON", "Force OFF", "Force ON", "Manual ON for", "Analog Output"},
            outputControl = {"Disabled", "Auto", "Force OFF", "Force ON", "Manual ON for"},
    // outputControlShortForm = {"â’¹", "A OFF", "A ON", "M OFF", "M ON", "F OFF", "F ON", "M ON for"};
    outputControlShortForm = {"D", "A", "F̶", "F", "M for"},

    eventLogArr = {"General settings changed", "Input Setting Changed",
            "Output Setting Changed", "Timer setting changed"},

    alarmArr = {"Low Alarm", "High Alarm", "Safety Low Alarm",
            "Safety High Alarm", "Calibration Required Alarm", "Totalizer Alarm",
            "DI Alarm", "Flow Verify Alarm", "Lockout Alarm", "Low Range Alarm(Virtual Input)", "High Range Alarm(Virtual Input)", "Diagnostic Sweep"},


    FlowanalogType = {"Analog - 1", "Analog - 2", "Analog - 3", "Analog - 4", "Analog - 5", "Analog - 6", "Analog - 7", "Analog - 8"};

    /* Static Variables */
    public static String mIPAddress = "", TabletIPAddress = "", Packet, Acknowledge;
    public static String macAddress; // Mac address of the unit controller
    //static String mIPAddress = "192.168.2.37", Packet;
    public static int mPortNumber;
    public static CountDownTimer packetTimeOut;
    public static Context mContext;

    //  public TCP tcp;
    public static WaterTreatmentDb DB;
    public static InputConfigurationDao inputDAO;
    public static OutputConfigurationDao outputDAO;
    public static VirtualConfigurationDao virtualDAO;
    public static TimerConfigurationDao timerDAO;
    public static UserManagementDao userManagementDao;
    public static KeepAliveCurrentValueDao keepaliveDAO;
    public static MainConfigurationDao mainConfigurationDao;
    public static DefaultLayoutConfigurationDao defaultLayoutConfigurationDao;
    OutputKeepAliveDao outputKeepAliveDao;

    // WebService
    private static final int httpRequestTimeout = 3000;
    public static int userType;
    public static RequestQueue requestQueue;

    // public final static String baseURL = "http://192.168.1.82/WaterIOT.API/api/";
    //public final static String baseURL = "http://192.168.1.56/WaterIOT.API/api/";

    //public final static String baseURL = "http://183.82.35.93/WaterIOT.API/api/";
    //public final static String baseURL = "http://192.168.1.10/WaterIOT.API/api/";

    public final static String baseURL = "http://183.82.35.93/Water.API/api/";

    public static ObservableBoolean triggerWebService = new ObservableBoolean(false);
    public static ObservableBoolean bleConnected = new ObservableBoolean(false);

    Handler handler;
    DataReceiveCallback listener;
    public static String lastKeepAliveData = "", trendDataCollector = "",
            inputKeepAliveData = "", outputKeepAliveData = "", alertKeepAliveData = "";
    static ApplicationClass mAppclass;

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

    public static ApplicationClass getInstance() {
        if (mAppclass == null) {
            return mAppclass = new ApplicationClass();
        }
        return mAppclass;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initDatabase();
        triggerWebService.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (triggerWebService.get()) {
                    ApiService.getInstance(getApplicationContext()).startApiService();
                }
            }
        });
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                requestQueue = Volley.newRequestQueue(getApplicationContext());
                registerBatteryReceiver();
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                SharedPref.init(getApplicationContext());
                ApiService.getInstance(getApplicationContext());
                KeepAlive.getInstance();

                SharedPref.write(pref_SITEID, SharedPref.read(pref_SITEID,"SITE_0001"));
                SharedPref.write(pref_SITENAME, SharedPref.read(pref_SITENAME,"WT_IOT"));
                SharedPref.write(pref_SITELOCATION, SharedPref.read(pref_SITELOCATION,"NA"));
                SharedPref.write(pref_CONTROLLERPASSWORD, SharedPref.read(pref_CONTROLLERPASSWORD,"1234"));
                SharedPref.write(pref_CONTROLLERISACTIVE, true);
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
                disconnectBle();
                unregisterBatteryReceiver();
            }
        });
    }

    private void disconnectBle() {
        BluetoothHelper helper = BluetoothHelper.getInstance();
        if (helper != null) {
            if (helper.isConnected()) {
                helper.disConnect();
            }
        }
    }

    public boolean havePermission(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public void sendPacket(DataReceiveCallback callback, String packetToSend) {
        try {
            BluetoothHelper helper = BluetoothHelper.getInstance();
            helper.sendDataBLE(callback, packetToSend);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTabletIp() {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ip);
    }

    /*public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_MyIntentService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        mContext.registerReceiver(receiver, intentFilter);
    }

    public void unregisterReceiver() {
        try {
            mContext.unregisterReceiver(receiver);
        } catch (Exception e) { e.printStackTrace(); }
    }*/

    public static void httpRequest(Context mContext, String apiType, @Nullable JSONObject object, final int method, final VolleyCallback callBack) throws Exception {
        if (mContext == null) {
            throw new Exception("Context is null");
        }
        if (apiType == null) {
            throw new Exception("URL is null");
        }
        if (apiType.equals("")) {
            throw new Exception("URL is invalid");
        }
        if (callBack == null) {
            throw new Exception("Callback is null");
        }
        String URL = baseURL + apiType;

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callBack.OnSuccess(response);
                Log.e(API1, " <-- " + response);
            }
        };

        Response.ErrorListener volleyErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.OnFailure(error);
            }
        };

        JsonObjectRequest request = new JsonObjectRequest(method, URL, object, responseListener, volleyErrorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(
                httpRequestTimeout, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Log.e(API1, " --> " + new String(request.getBody()));
        requestQueue.add(request);
    }

    public void registerBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_BATTERY_CHANGED);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        mContext.registerReceiver(new MonitorBatteryLevel(), intentFilter);
    }

    public void unregisterBatteryReceiver() {
        try {
            mContext.unregisterReceiver(new MonitorBatteryLevel());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            case 8:
                finalDigits = ("00000000" + value).substring(value.length());
                break;

            case 9:
                finalDigits = ("000000000" + value).substring(value.length());
                break;
            case 10:
                finalDigits = ("0000000000" + value).substring(value.length());
                break;
            case 11:
                finalDigits = ("00000000000" + value).substring(value.length());
                break;
            case 12:
                finalDigits = ("000000000000" + value).substring(value.length());
                break;
            case 13:
                finalDigits = ("0000000000000" + value).substring(value.length());
                break;
        }
        return finalDigits;
    }

    public static String toStringValue(int digits, EditText editText) {
        return formDigits(digits, editText.getText().toString());
    }

    public static String toStringValue(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    public static String getValueFromArr(String value, String[] arr) {
        return arr[Integer.parseInt(value)];
    }

    public static String getPosition(int digit, String string, String[] strArr) {
        String j = null;
        for (int i = 0; i < strArr.length; i++) {
            if (string.equals(strArr[i])) {
                j = String.valueOf(i);
            }
        }
        return formDigits(digit, j);
    }

    public void navigateTo(FragmentActivity fragAct, int desID) {
        try {
            Navigation.findNavController((Activity) fragAct, R.id.nav_host_frag).navigate(desID);
        } catch (Exception e) {
            Log.e("TAG", "navigateToBundle: " + e);
        }
    }

    public void navigateToBundle(FragmentActivity activity, int fragmentIDinNavigation, Bundle b) {
        try {
            Navigation.findNavController((Activity) activity, R.id.nav_host_frag).navigate(fragmentIDinNavigation, b);
        } catch (Exception e) {
            Log.e("TAG", "navigateToBundle: " + e);
        }
    }

    public void popStackBack(FragmentActivity activity) {
        Navigation.findNavController((Activity) activity, R.id.nav_host_frag).popBackStack();
    }

    /* public void sendPacket(final DataReceiveCallback listener, String packet) {
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
    } */

    public void castFrag(FragmentManager parentFragmentManager, int host, Fragment fragment) {
        parentFragmentManager.beginTransaction().replace(host, fragment).commit();
    }

    public void showSnackBar(Context context, String message) {
        BaseActivity.showSnack(message);
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

    public static String getDecimalValue(ToggleButton toggleButton, TextInputEditText prefixEdt, int prefixDigit, EditText suffixEdt, int suffixDigit) {
        return (toggleButton.isChecked() ? "+" : "-") + getStringValue(prefixDigit, prefixEdt) + "." + getStringValue(suffixDigit, suffixEdt);
    }

    public static String getDecimalValue(TextInputEditText prefixEdt, int prefixDigit, EditText suffixEdt, int suffixDigit) {
        return getStringValue(prefixDigit, prefixEdt) + "." + getStringValue(suffixDigit, suffixEdt);
    }

    public static String getStringValue(int digits, EditText editText) {
        return formDigits(digits, editText.getText().toString());
    }

    public static String getStringValue(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    public static String getStringValue(TextInputEditText editText) {
        return editText.getText().toString();
    }

    public static Boolean isFieldEmpty(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            editText.requestFocus();
            return true;
        }
        editText.setError(null);
        return false;
    }

    public static Boolean isFieldEmpty(AutoCompleteTextView editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        editText.setError(null);
        return false;
    }

    public static String getPositionFromAtxt(int digit, String string, String[] strArr) {
        String j = null;
        for (int i = 0; i < strArr.length; i++) {
            if (string.equals(strArr[i])) {
                j = String.valueOf(i);
            }
        }
        return formDigits(digit, j);
    }

    public static ArrayAdapter<String> getAdapter(String[] strArr, Context context) {
        return new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, strArr);
    }

    public void initDatabase() {
        mContext = getApplicationContext();
        DB = WaterTreatmentDb.getDatabase(getApplicationContext());

        /*Input_DB*/
        inputDAO = DB.inputConfigurationDao();
        if (inputDAO.getInputConfigurationEntityList().isEmpty()) {
            String sensorType = "SENSOR", writePacket = "", sequenceName = "";
            int signalType = 0, sequenceNo = 1;
            int j = 1;
            for (int i = 1; i < 50; i++) {
                if (i < 5) {
                    sensorType = "SENSOR";
                    if (i == 1) {
                        sequenceName = "pH";
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$00$1$0$pH Sensor$0$0$+033.00$000$00.00$14.00$000$0$2*}";
                    } else if (i == 2) {
                        sequenceName = "ORP";
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$01$1$0$ORP Sensor$000$-2000.00$+2000.00$000$0$2*}";
                    } else if (i == 3) {
                        sequenceName = "Contacting Conductivity";
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$04$1$0$Contacting$0$+033.00$0$01.00$0$00.00$000$000000.00$300000.00$000$0$2*}";
                    } else {
                        sequenceName = "Toroidal Conductivity";
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$05$1$0$Toroidial$0$+033.00$0$0$00.00$000$0000000.00$2000000.00$000$0$2*}";
                    }
                } else if (i < 15) {
                    sensorType = "MODBUS";
                    if (i == 5) {
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$09$0$0$1$0$ST 500 - f$0$000.00$300.00$0000000$000$000.00$300.00$000$0$2*}";
                    } else if (i == 6) {
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$09$0$0$2$0$ST 500 - t$3$000.00$150.00$0000000$000$000.00$150.00$000$0$2*}";
                    } else if (i == 7) {
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$09$1$1$3$0$CR 300CS$2$000.00$005.00$0000000$000$000.00$005.00$000$0$2*}";
                    } else if (i == 8) {
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$09$1$1$4$0$CR 300CS$2$000.00$005.00$0000000$000$000.00$005.00$000$0$2*}";
                    } else if (i == 9) {
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$09$2$2$3$0$CR 300CU$2$000.00$005.00$0000000$000$000.00$005.00$000$0$2*}";
                    } else if (i == 10) {
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$09$2$2$4$0$CR 300CU$2$000.00$005.00$0000000$000$000.00$005.00$000$0$2*}";
                    } else if (i == 11) {
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$09$3$3$6$0$ST 590$1$000.00$030.00$0000000$000$000.00$000.00$000$0$2*}";
                    } else if (i == 12) {
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$09$3$4$5$0$ST 588$0$000.00$200.00$0000000$000$000.00$200.00$000$0$2*}";
                    } else if (i == 13) {
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$09$4$4$6$0$ST 588$1$000.00$020.00$0000000$000$000.00$020.00$000$0$2*}";
                    } else {
                        writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$09$5$5$1$0$ST 500$0$000.00$040.00$0000000$000$000.00$040.00$000$0$2*}";
                    }
                    String[] splitmodbusData = writePacket.split("\\*")[1].split(RES_SPILT_CHAR);
                    sequenceName = modBusTypeArr[Integer.parseInt(splitmodbusData[7])] + " - " + typeArr[Integer.parseInt(splitmodbusData[8])];
                    sequenceNo = Integer.parseInt(splitmodbusData[6]);
                } else if (i < 18) {
                    sensorType = "SENSOR";
                    sequenceName = "Temperature -" + j;
                    sequenceNo = j;
                    writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$02$" + j + "$0$Temp " + j + "$+033.00$000$-005.00$+260.00$000$0$2*}";
                    j++;
                    if (i == 17) {
                        j = 1;
                    }
                } else if (i < 26) {
                    sensorType = "Analog";
                    sequenceName = j < 7 ? sensorType + " - " + j + "(4-20mA)" : sensorType + " - " + j + "(0-10mA)";
                    int analogType = j < 7 ? 0 : 1;
                    sequenceNo = j;
                    writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$06$0$" + j + "$" + analogType + "$0$Analog " + j + "$0$04.00$20.00$000$04.00$20.00$000$0$2*}";
                    j++;
                    if (i == 25) {
                        j = 1;
                    }
                } else if (i < 34) {
                    sensorType = "FLOWMETER";
                    sequenceName = "Flow Meter - " + j;
                    sequenceNo = j;
                    writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$03$0$" + j + "$1$0$Flow " + j + "$0$0001.00$0000001.00$0000000.00$000$2000000000.00$0$0$0000000000.00$0000000000.00$2000000000.00$000$0$2*}";
                    j++;
                    if (i == 33) {
                        j = 1;
                    }
                } else if (i < 42) {
                    signalType = 1;
                    sensorType = "DIGITAL";
                    sequenceName = "Digital Sensor - " + j;
                    sequenceNo = j;
                    writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$08$" + j + "$0$Digital " + j + "$Open$Close$0$0$0$0$0$2$00*}";
                    j++;
                    if (i == 41) {
                        j = 1;
                    }
                } else {
                    sensorType = "TANK";
                    signalType = 1;
                    sequenceName = "Tank Level - " + j;
                    sequenceNo = j;
                    writePacket = "{*1234$0$0$04$" + formDigits(2, Integer.toString(i)) + "$07$" + j + "$0$Tank " + j + "$Open$Close$0$0$0$0$0$2$00*}";
                    j++;
                    if (i == 49) {
                        j = 1;
                    }
                }
                String[] splitData = writePacket.split("\\*")[1].split(RES_SPILT_CHAR);
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (i, inputTypeArr[Integer.parseInt(splitData[5])], sensorType, signalType, sequenceName, sequenceNo, "N/A",
                                "N/A", "N/A", "N/A", "N/A", 0, writePacket);
                List<InputConfigurationEntity> inputentryList = new ArrayList<>();
                inputentryList.add(entityUpdate);
                updateInputDB(inputentryList);
            }
        }

        /*Output_DB*/
        outputDAO = DB.outputConfigurationDao();
        if (outputDAO.getOutputConfigurationEntityList().isEmpty()) {
            String defaultwritePacket = "", outputMode = "", outputStatus = "";
            for (int i = 1; i < 23; i++) {
                if (i < 15) {
                    defaultwritePacket = "{*1234$0$0$06$" + formDigits(2, Integer.toString(i)) + "$1$Output" + i + "$34$35$0$000000001.00$000000001.00$0001*}";
                    outputStatus = "Continuous";
                    outputMode = "0001$000000001.00";
                } else {
                    defaultwritePacket = "{*1234$0$0$06$" + formDigits(2, Integer.toString(i)) + "$3$Output" + i + "$1$I01$04.00$20.00$00.00$14.00*}";
                    outputStatus = "Input- 1 (N/A)";
                    outputMode = "Probe";
                }
                OutputConfigurationEntity entityUpdate = new OutputConfigurationEntity
                        (i, "Output-" + i + " (Output" + i + ")", "Output" + i,
                                outputMode,
                                outputStatus, defaultwritePacket);
                List<OutputConfigurationEntity> outputEntryList = new ArrayList<>();
                outputEntryList.add(entityUpdate);
                updateOutPutDB(outputEntryList);
            }
        }

        /*Virtual_DB*/
        virtualDAO = DB.virtualConfigurationDao();
        if (virtualDAO.getVirtualConfigurationEntityList().isEmpty()) {
            for (int i = 50; i <= 57; i++) {
                String defaultwritePacket = "{*1234$0$0$05$" + i + "$0$VirtualInput" +
                        (i - 49) + "$0$01$00$0$01$00$00.00$14.00$000$00.00$14.00$0$0*}";
                VirtualConfigurationEntity entityUpdate = new VirtualConfigurationEntity
                        (i, "Virtual", 0, "VirtualInput" + (i - 49),
                                "pH", "00.00", "14.00", "N/A", defaultwritePacket);
                List<VirtualConfigurationEntity> virtualEntryList = new ArrayList<>();
                virtualEntryList.add(entityUpdate);
                updateVirtualDB(virtualEntryList);
            }
        }

        /*Timer_DB*/
        timerDAO = DB.timerConfigurationDao();
        if (timerDAO.geTimerConfigurationEntityList().isEmpty()) {
            int j = 0;
            for (int i = 0; i < 6; i++) {
                String mainTimerPacket = "{*1234$0$0$08$" + i + "$Timer " + (i + 1) +
                        "$01$0$1$1$0$0$000000$01$0$2$0$0$000000$01$0$3$0$0$000000$01$0$4$0$0$000000$01$0$11203041*}";
                String weekOnePacket = "{*1234$0$0$09$" + i + "$" + formDigits(2, Integer.toString(j)) +
                        "$0$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000*}";
                String weekTwoPacket = "{*1234$0$0$09$" + i + "$" + formDigits(2, Integer.toString(j + 1)) +
                        "$0$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000*}";
                String weekThreePacket = "{*1234$0$0$09$" + i + "$" + formDigits(2, Integer.toString(j + 2)) +
                        "$0$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000*}";
                String weekFourPacket = "{*1234$0$0$09$" + i + "$" + formDigits(2, Integer.toString(j + 3)) +
                        "$0$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000$0$000000$000000*}";
                j = j + 4;
                TimerConfigurationEntity entityUpdate = new TimerConfigurationEntity
                        (i, "Timer " + (i + 1),
                                "Output- 1 (Output1)",
                                "Timer", mainTimerPacket, weekOnePacket,
                                weekTwoPacket, weekThreePacket, weekFourPacket);
                List<TimerConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateTimerDB(entryListUpdate);
            }
        }

        /*User Management*/
        userManagementDao = DB.userManagementDao();
        if (userManagementDao.getUsermanagementEntity().isEmpty()) {
            List<UsermanagementEntity> entryListUpdate = new ArrayList<>();
            /* 0 - NONE | 1 - BASIC | 2 - INTERMEDIATE | 3 - ADVANCED */
            UsermanagementEntity adminEntityUpdate = new UsermanagementEntity("US0001", "SuperAdmin",
                    3, "123456", "0000000000", "", getCurrentDate() + "" + getCurrentTime(), "");

            UsermanagementEntity userEntityUpdate = new UsermanagementEntity("US0002", "DemoUser",
                    2, "123456", "0000000000", "", getCurrentDate() + "" + getCurrentTime(), "");

            entryListUpdate.add(adminEntityUpdate);
            entryListUpdate.add(userEntityUpdate);
            updateUsermanagement(entryListUpdate);
        }

        /*DefaultLayout Configuration*/
        defaultLayoutConfigurationDao = DB.defaultLayoutConfigurationDao();
        if (defaultLayoutConfigurationDao.getDefaultLayoutConfigurationEntityList().isEmpty()) {
            for (int i = 1; i < 6; i++) {
                DefaultLayoutConfigurationEntity entityUpdate = new DefaultLayoutConfigurationEntity
                        (i, i, 0, macAddress, 1);
                List<DefaultLayoutConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                insertToDb(entryListUpdate);
            }
            defaultLayoutConfigurationDao.update(1, 1);
        }

        /*Default Layout*/
        mainConfigurationDao = DB.mainConfigurationDao();
        if (mainConfigurationDao.getMainConfigurationEntityList().isEmpty()) {
            MainConfigurationEntity entityUpdate = new MainConfigurationEntity(
                    1, 1, 1, 1, 1, 1,
                    "Sensor not Added", 0, "N/A", 0);
            List<MainConfigurationEntity> mainEntryList = new ArrayList<>();
            mainEntryList.add(entityUpdate);
            updateMainDB(mainEntryList);
        }

        /*KeepAlive*/
        keepaliveDAO = DB.keepAliveCurrentValueDao();
        if (keepaliveDAO.getKeepAliveList().isEmpty()) {
            for (int i = 1; i <= 57; i++) {
                KeepAliveCurrentEntity keepAliveCurrentEntity =
                        new KeepAliveCurrentEntity(i, "N/A");
                List<KeepAliveCurrentEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(keepAliveCurrentEntity);
                insertKeepAliveDb(entryListUpdate);
            }
        }

        /* Output KeepAlive*/
        outputKeepAliveDao = DB.outputKeepAliveDao();
        if (outputKeepAliveDao.getOutputList().isEmpty()) {
            for (int i = 1; i <= 22; i++) {
                OutputKeepAliveEntity outputKeepAliveEntity =
                        new OutputKeepAliveEntity(i, "N/A", "N/A");
                List<OutputKeepAliveEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(outputKeepAliveEntity);
                insertOutputKeepAliveDb(entryListUpdate);
            }
        }
    }

    public boolean factoryRest() {
        if (DB != null) {
            inputDAO.deleteInputDao();
            outputDAO.deleteOutputDao();
            virtualDAO.deleteVirtualDao();
            timerDAO.deleteTimerDao();
            initDatabase();
        } else {
         return false;
        }
        return true;
    }

    public static String getCurrentDate() {
        Format f = new SimpleDateFormat("dd/MM/yyyy");
        return f.format(new Date());
    }

    public static String getCurrentTrendFormatDate() {
        Format f = new SimpleDateFormat("yyyy/MM/dd");
        return f.format(new Date());
    }

    public static String formatDate(Date date) {
        Format f = new SimpleDateFormat("dd/MM/yyyy");
        return f.format(date);
    }

    public static String getCurrentTrendFormatDate(Date date) {
        Format f = new SimpleDateFormat("yyyy/MM/dd");
        return f.format(date);
    }

    public static String getCurrentTime() {
        Format f = new SimpleDateFormat("HH.mm.ss");
        return f.format(new Date());
    }

    private void updateTimerDB(List<TimerConfigurationEntity> entryList) {
        TimerConfigurationDao dao = DB.timerConfigurationDao();
        dao.insert(entryList.toArray(new TimerConfigurationEntity[0]));
    }

    private void updateVirtualDB(List<VirtualConfigurationEntity> entryList) {
        VirtualConfigurationDao dao = DB.virtualConfigurationDao();
        dao.insert(entryList.toArray(new VirtualConfigurationEntity[0]));
    }

    private void updateInputDB(List<InputConfigurationEntity> entryList) {
        InputConfigurationDao dao = DB.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void updateOutPutDB(List<OutputConfigurationEntity> entryList) {
        OutputConfigurationDao dao = DB.outputConfigurationDao();
        dao.insert(entryList.toArray(new OutputConfigurationEntity[0]));
    }

    public void updateUsermanagement(List<UsermanagementEntity> entryList) {
        UserManagementDao dao = DB.userManagementDao();
        dao.insert(entryList.toArray(new UsermanagementEntity[0]));
    }

    public void insertToDb(List<DefaultLayoutConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getApplicationContext());
        DefaultLayoutConfigurationDao dao = db.defaultLayoutConfigurationDao();
        dao.insert(entryList.toArray(new DefaultLayoutConfigurationEntity[0]));
    }

    private void updateMainDB(List<MainConfigurationEntity> entryList) {
        MainConfigurationDao dao = DB.mainConfigurationDao();
        dao.insert(entryList.toArray(new MainConfigurationEntity[0]));
    }

    public void insertKeepAliveDb(List<KeepAliveCurrentEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getApplicationContext());
        KeepAliveCurrentValueDao dao = db.keepAliveCurrentValueDao();
        dao.insert(entryList.toArray(new KeepAliveCurrentEntity[0]));
    }

    public boolean isValidPck(String pckType, String data, Context context) {
        if (data.equals("FailedToConnect")) {
            showSnackBar(context, getString(R.string.connection_failed));
        } else if (data.equals("pckError")) {
            showSnackBar(context, getString(R.string.connection_failed));
        } else if (data.equals("sendCatch")) {
            showSnackBar(context, getString(R.string.connection_failed));
        } else if (data.equals("Timeout")) {
            showSnackBar(context, getString(R.string.timeout));
        } else if (data.contains("{*")) {
            String[] splitData = data.split("\\*")[1].split(RES_SPILT_CHAR);
            if (splitData[1].equals("10")) {
                if (splitData[0].equals(pckType)) {
                    if (splitData[2].equals(RES_SUCCESS)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void insertOutputKeepAliveDb(List<OutputKeepAliveEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getApplicationContext());
        OutputKeepAliveDao dao = db.outputKeepAliveDao();
        dao.insert(entryList.toArray(new OutputKeepAliveEntity[0]));
    }

    public static String lessThanAWeek() {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, - 6);
        Date sevenDaysAgo = cal.getTime();
        return ApplicationClass.formatDate(sevenDaysAgo);
    }

    public static String lessThanTwoWeek() {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, - 13);
        Date sevenDaysAgo = cal.getTime();
        return ApplicationClass.formatDate(sevenDaysAgo);
    }

    public static String DateformatConversion(String enteredDate) {
        try {

            Date inputDate = new SimpleDateFormat("dd/MM/yyyy").parse(enteredDate);
            enteredDate = new SimpleDateFormat("yyyy/MM/dd").format(inputDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return enteredDate;
    }

    public void importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
            handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    try {
                        String currentDBPath = getDatabasePath(DB_NAME).getAbsolutePath();
                        String backupDBPath =  "ion_exchange_db.db";
                        File currentDB = new File(currentDBPath);
                        File backupDB = new File(sd, backupDBPath);

                        if (currentDB.exists()) {
                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            Log.e(TAG, "importDB: " + currentDBPath);
                            Log.e(TAG, "importDB: " + backupDB);
                            dst.close();
                            Toast.makeText(getApplicationContext(), "Backup Successful!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            handler.postDelayed(r, 3000);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "copyFile: " + e);
        }
    }

    public void exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
            handler = new Handler();
            final Runnable r = new Runnable() {

                public void run() {
                    try {
                        String currentDBPath = getDatabasePath(DB_NAME).getAbsolutePath();
                        String backupDBPath = Environment.getExternalStorageDirectory().getPath( ) + "ion_exchange_db.db";
                        File currentDB = new File(currentDBPath);
                        File backupDB = new File(sd, backupDBPath);

                        if (currentDB.exists()) {
                            FileChannel src = new FileInputStream(backupDB).getChannel();
                            FileChannel dst = new FileOutputStream(currentDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            Log.e(TAG, "importDB: " + currentDBPath);
                            Log.e(TAG, "importDB: " + backupDB);
                            dst.close();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            handler.postDelayed(r, 3000);

            Toast.makeText(getApplicationContext(), "Export Successful!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Export Failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }

}
