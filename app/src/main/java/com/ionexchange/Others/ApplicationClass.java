package com.ionexchange.Others;

import static android.util.Patterns.IP_ADDRESS;
import static com.ionexchange.Others.TCP.ACTION_MyIntentService;
import static com.ionexchange.Others.TcpServer.ACTION_MyIntent;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.ionexchange.Database.Dao.DefaultLayoutConfigurationDao;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.Dao.TimerConfigurationDao;
import com.ionexchange.Database.Dao.UserManagementDao;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.DefaultLayoutConfigurationEntity;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.KeepAliveCurrentEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputKeepAliveEntity;
import com.ionexchange.Database.Entity.TimerConfigurationEntity;
import com.ionexchange.Database.Entity.UsermanagementEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static android.util.Patterns.IP_ADDRESS;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.TCP.ACTION_MyIntentService;

/* Created by Jeeva on 13/07/2021 */
public class ApplicationClass extends Application {
    private static final String TAG = "ApplicationClass";

    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    public static int userType = 3; // 0 - None | 1 - Basic | 2 - intermediate | 3 - Advanced

    public static String[] sensorActivationArr = {"ENABLE", "DISABLE"},
            roleType = {"None", "Basic", "Intermediate", "Advanced"},
            sensorTypeArr = {"Sensor", "Temperature", "Modbus", "Analog Input", "Flow/Water Meter",
                    "Digital Input", "Tank Level",},
            inputTypeArr = {"pH", "ORP", "Temperature", "Flow/Water Meter", "Contacting Conductivity",
                    "Toroidal Conductivity", "Analog Input", "Tank Level", "Digital Input", "Modbus Sensor"},
            analogInputArr = {"Analog Input", "pH", "ORP", "Temperature", "Flow/Water Meter",
                    "Contacting Conductivity", "Toroidal Conductivity", "Tank Level"},
            bufferArr = {"Auto", "Manual"},
            tempLinkedArr = {"None", "Temperature 1", "Temperature 2", "Temperature 3"},
            resetCalibrationArr = {"No Reset", "Reset"},
            unitArr = {"ÂµS/cm", "mS/cm", "S/cm"},
            resetFlowTotalArr = {"No reset", "Reset"},
            sensorSequenceNumber = {"1-Sensor", "2-Sensor", "3-Sensor", "4-Sensor", "5-Sensor", "6-Sensor"},
            levelsensorSequenceNumber = {"None", "Tank Level - 1", "Tank Level - 2", "Tank Level - 3", "Tank Level - 4",
                    "Tank Level - 5", "Tank Level - 6","Tank Level - 7","Tank Level - 8"},
            digitalsensorSequenceNumber = {"None", "Digital Sensor - 1", "Digital Sensor - 2", "Digital Sensor - 3", "Digital Sensor - 4",
                    "Digital Sensor - 5", "Digital Sensor - 6","Digital Sensor - 7","Digital Sensor - 8"},
            flowmeterSequenceNumber = {"None", "Flow Meter - 1", "Flow Meter - 2", "Flow Meter - 3", "Flow Meter - 4",
                    "Flow Meter - 5", "Flow Meter - 6","Flow Meter - 7","Flow Meter - 8"},
            analogSequenceNumber = {"None", "Analog - 1", "Analog - 2", "Analog - 3", "Analog - 4",
                    "Analog - 5", "Analog - 6","Analog - 7","Analog - 8"},
            typeOfValueRead = {"None", "Fluorescence", "Turbidity", "Corrosion rate", "Pitting rate", "Fluorescence", "Tagged Polymer"},
            flowMeterTypeArr = {"Analog", "Contactor", "Paddle Wheel", "Feed Monitor"},
            flowUnitArr = {"Volume", "Gallons", "Litres", "Cubic Meters", "Millions of Gallons"},
            scheduleResetArr = {"No Schedule Reset", "Daily", "Monthly", "Annually"},
            totalAlarmMode = {"Interlock", "Maintain"},
            flowAlarmMode = {"Disable", "Interlock", "Maintain"},

            digitalArr = {"NC", "NO"},
            modBusTypeArr = {"ST500", "CR300 CS", "CR-300 CU", "ST-590", "ST-588", "ST-500 RO"},
            modBusUnitArr = {"ppb", "ppm", "mpy"},
            analogTypeArr = {"(4-20mA)", "(0-10V)"},
            analogUnitArr = {"mA", "V"},

            calculationArr = {"Difference", "Ratio", "Total", "% Difference"},
            sensorArr = {"1", "2", "3", "4"}, temperatureArr = {"15", "16", "17"},
            modbusArr = {"5", "6", "7", "8", "9", "10", "11", "12", "13", "14"},
            analogArr = {"18", "19", "20", "21", "22", "23", "24", "25"},
            flowmeterArr = {"26", "27", "28", "29", "30", "31", "32", "33"},
            digitalSensorArr = {"34", "35", "36", "37", "38", "39", "40", "41"},
            tankArr = {"42", "43", "44", "45", "46", "47", "48", "49"},
            sensorsViArr = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49"},
            interlockChannel = {"Digital Input - 1", "Digital Input - 2", "Digital Input - 3", "Digital Input - 4", "Digital Input - 5", "Digital Input - 6", "Digital Input - 7", "Digital Input - 8", "Tank Level - 1", "Tank Level - 2", "Tank Level - 3", "Tank Level - 4", "Tank Level - 5", "Tank Level - 6", "Tank Level - 7", "Tank Level - 8"},
            functionMode,
            fMode = {"Disable","Inhibitor", "sensor", "Analog"},
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
       outputStatusarr = {"Disabled", "Auto OFF", "Auto ON", "Manual OFF", "Manual ON", "Force OFF", "Force ON", "Manual ON for","Analog Output"},
            outputControl = {"Disabled", "Auto OFF", "Auto ON", "Manual OFF", "Manual ON", "Force OFF", "Force ON", "Manual ON for"};
    /* Static Variables */
    public static String mIPAddress = "", Packet, Acknowledge;
    public static String macAddress; // Mac address of the unit controller
    //static String mIPAddress = "192.168.2.37", Packet;
    public static int mPortNumber = 9760;
    public static CountDownTimer packetTimeOut;
    Context mContext;
    public TCP tcp;

    public static WaterTreatmentDb DB;
    public static InputConfigurationDao inputDAO;
    public static OutputConfigurationDao outputDAO;
    public static VirtualConfigurationDao virtualDAO;
    public static TimerConfigurationDao timerDAO;
    public static UserManagementDao userManagementDao;

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
        initDB();
        setDefaultDb();
        setCurrentValueDb();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                try {
                    mContext = activity;
                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = preferences.edit();
                    if (preferences != null) {
                        mIPAddress = preferences.getString("prefIp", "");
                        mPortNumber = Integer.parseInt(preferences.getString("prefPort", ""));
                    }
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
        try {
            mContext.unregisterReceiver(receiver);
        } catch (Exception e) { e.printStackTrace(); }
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
        } catch (Exception e) { Log.e("TAG", "navigateToBundle: " + e); }
    }

    public void navigateToBundle(FragmentActivity activity, int fragmentIDinNavigation, Bundle b) {
        try {
            Navigation.findNavController((Activity) activity, R.id.nav_host_frag).navigate(fragmentIDinNavigation, b);
        } catch (Exception e) { Log.e("TAG", "navigateToBundle: " + e); }
    }

    public void popStackBack(FragmentActivity activity) {
        Navigation.findNavController((Activity) activity, R.id.nav_host_frag).popBackStack();
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

    private void initDB() {
        DB = WaterTreatmentDb.getDatabase(getApplicationContext());
        /*Input_DB*/
        inputDAO = DB.inputConfigurationDao();
        if (inputDAO.getInputConfigurationEntityList().isEmpty()) {
            String sensorType = "SENSOR";
            for (int i = 1; i < 50; i++) {
                if(i < 5){
                    sensorType = "SENSOR";
                }else if(i > 4 && i < 15){
                    sensorType = "MODBUS";
                }else if(i > 14 && i < 18){
                    sensorType = "SENSOR";
                }else if(i > 17 && i < 26){
                    sensorType = "Analog";
                }else if(i > 25 && i < 34){
                    sensorType = "FLOWMETER";
                }else if(i > 33 && i < 42){
                    sensorType = "DIGITAL";
                }else if(i > 41 && i < 50){
                    sensorType = "TANK";
                }

                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (i, "N/A", sensorType, 0,"N/A",0, "N/A",
                                "N/A", "N/A", "N/A", "N/A",0);
                List<InputConfigurationEntity> inputentryList = new ArrayList<>();
                inputentryList.add(entityUpdate);
                updateInputDB(inputentryList);
            }
        }

        /*Output_DB*/
        outputDAO = DB.outputConfigurationDao();
        if (outputDAO.getOutputConfigurationEntityList().isEmpty()) {
            for (int i = 1; i < 23; i++) {
                OutputConfigurationEntity entityUpdate = new OutputConfigurationEntity
                        (i, "output-" + i, "N/A",
                                "N/A",
                                "N/A");
                List<OutputConfigurationEntity> outputEntryList = new ArrayList<>();
                outputEntryList.add(entityUpdate);
                updateOutPutDB(outputEntryList);
            }
        }

        /*Virtual_DB*/
        virtualDAO = DB.virtualConfigurationDao();
        if (virtualDAO.getVirtualConfigurationEntityList().isEmpty()) {
            for (int i = 50; i <= 57; i++) {
                VirtualConfigurationEntity entityUpdate = new VirtualConfigurationEntity
                        (i, "virtual-" + (i - 49), 0, "N/A",
                                "N/A", "N/A","N/A");
                List<VirtualConfigurationEntity> virtualEntryList = new ArrayList<>();
                virtualEntryList.add(entityUpdate);
                updateVirtualDB(virtualEntryList);
            }
        }

        /*Timer_DB*/
        timerDAO = DB.timerConfigurationDao();
        if (timerDAO.geTimerConfigurationEntityList().isEmpty()) {
            for (int i = 1; i < 7; i++) {
                TimerConfigurationEntity entityUpdate = new TimerConfigurationEntity
                        (i, "N/A",
                                "N/A",
                                "N/A", 0, 0, "N/A");
                List<TimerConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateTimerDB(entryListUpdate);
            }
        }

        /*User Management*/
        userManagementDao = DB.userManagementDao();
        if (userManagementDao.getUsermanagementEntity().isEmpty()) {

            UsermanagementEntity adminEntityUpdate = new UsermanagementEntity(1, "admin", 3, "12345");
            UsermanagementEntity userEntityUpdate = new UsermanagementEntity(2, "user", 1, "54321");

            List<UsermanagementEntity> entryListUpdate = new ArrayList<>();
            entryListUpdate.add(adminEntityUpdate);
            entryListUpdate.add(userEntityUpdate);
            updateUsermanagement(entryListUpdate);
        }
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

    void setDefaultDb() {
        DefaultLayoutConfigurationDao dao;
        WaterTreatmentDb dB;
        dB = WaterTreatmentDb.getDatabase(getApplicationContext());
        dao = dB.defaultLayoutConfigurationDao();
        if (dao.getDefaultLayoutConfigurationEntityList().isEmpty()) {
            for (int i = 1; i < 6; i++) {
                DefaultLayoutConfigurationEntity entityUpdate = new DefaultLayoutConfigurationEntity
                        (i, i, 0, macAddress, 1);
                List<DefaultLayoutConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                insertToDb(entryListUpdate);
            }
            dao.update(1, 1);
        }
    }

    void setCurrentValueDb() {
        KeepAliveCurrentValueDao dao;
        OutputKeepAliveDao outputKeepAliveDao;
        WaterTreatmentDb dB;
        dB = WaterTreatmentDb.getDatabase(getApplicationContext());
        dao = dB.keepAliveCurrentValueDao();
        if (dao.getKeepAliveList() != null) {
            for (int i = 1; i < 54; i++) {
                KeepAliveCurrentEntity keepAliveCurrentEntity =
                        new KeepAliveCurrentEntity(i, "N/A");
                List<KeepAliveCurrentEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(keepAliveCurrentEntity);
                insertKeepAliveDb(entryListUpdate);
            }
        }


        outputKeepAliveDao = dB.outputKeepAliveDao();
        if (outputKeepAliveDao.getOutputLiveList() != null) {
            for (int i = 1; i < 25; i++) {
                OutputKeepAliveEntity outputKeepAliveEntity =
                        new OutputKeepAliveEntity(i, "N/A");
                List<OutputKeepAliveEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(outputKeepAliveEntity);
                insertOutputKeepAliveDb(entryListUpdate);
            }
        }


    }

    public void insertToDb(List<DefaultLayoutConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getApplicationContext());
        DefaultLayoutConfigurationDao dao = db.defaultLayoutConfigurationDao();
        dao.insert(entryList.toArray(new DefaultLayoutConfigurationEntity[0]));
    }

    public void insertKeepAliveDb(List<KeepAliveCurrentEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getApplicationContext());
        KeepAliveCurrentValueDao dao = db.keepAliveCurrentValueDao();
        dao.insert(entryList.toArray(new KeepAliveCurrentEntity[0]));
    }

    private boolean isValidPck(String pckType, String data, Context context) {
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
}
