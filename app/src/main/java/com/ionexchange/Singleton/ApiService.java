package com.ionexchange.Singleton;

import static com.ionexchange.Others.ApplicationClass.DB;
import static com.ionexchange.Others.ApplicationClass.alertKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.inputDAO;
import static com.ionexchange.Others.ApplicationClass.inputKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.modBusTypeArr;
import static com.ionexchange.Others.ApplicationClass.outputDAO;
import static com.ionexchange.Others.ApplicationClass.outputKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.triggerWebService;
import static com.ionexchange.Others.ApplicationClass.typeArr;
import static com.ionexchange.Others.ApplicationClass.userManagementDao;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.OUTPUT_CONTROL_CONFIG;
import static com.ionexchange.Others.PacketControl.PCK_DIAGNOSTIC;
import static com.ionexchange.Others.PacketControl.PCK_GENERAL;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.PCK_LOCKOUT;
import static com.ionexchange.Others.PacketControl.PCK_OUTPUT_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;
import static com.ionexchange.Singleton.SharedPref.pref_CONTROLLERISACTIVE;
import static com.ionexchange.Singleton.SharedPref.pref_CONTROLLERPASSWORD;
import static com.ionexchange.Singleton.SharedPref.pref_MACADDRESS;
import static com.ionexchange.Singleton.SharedPref.pref_SITEID;
import static com.ionexchange.Singleton.SharedPref.pref_SITELOCATION;
import static com.ionexchange.Singleton.SharedPref.pref_SITENAME;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINID;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINPASSWORDCHANED;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINSTATUS;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ionexchange.Database.Dao.AlarmLogDao;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.TimerConfigurationDao;
import com.ionexchange.Database.Dao.UserManagementDao;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.TimerConfigurationEntity;
import com.ionexchange.Database.Entity.UsermanagementEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Interface.VolleyCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.Others.EventLogDemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiService implements DataReceiveCallback {
    private static ApiService apiService;
    static Context mContext;
    private static final String TAG = "API";
    String responseTabId = "00";
    String responseTabData = "";
    String packetType = "1";
    public static String tempString = "0";
    String diagnosticsDataOne = "";
    String diagnosticsDataTwo = "";
    String diagnosticsDataThree = "";
    String diagnosticsDataFour = "";
    String diagnosticsDataFive = "";
    String diagnosticsDataSix = "";
    JSONObject responseObject;
    JSONObject dataObj;
    JSONArray finalArr;
    String jsonSubID = "00";
    String[] splitTimer;
    JSONObject timerJson;

    int weekly;

    /* JSON Structure of API Communication - Response */
    /*{
        "Response": {
        "JSON_ID": "00",
                "DU_MAC": "45b64335276aab8b",
                "DEVICE_MAC": "4C:EB:D6:73:64:BE",
                "TIMESTAMP": "1644471278",
                "DATAS": {
            "ALERT_RESPONSE": "",
                    "OUTPUT_RESPONSE": "",
                    "MSG_FIELD": "",
                    "LABLE": "",
                    "RESPONSE_WEB": {
                "JSON_SUB_ID": "07",
                        "PACKET_TYPE": "0$0",
                        "DATA": [
                {
                        "INPUTNO": "50",
                        "REQ": "{*1234$1$0$05$50$0$vir$0$02$01$1$-2000.00$01$-2000.00$+2000.00$021$-2000.00$+2000.00$0$cc$1*}",
                        "NAME_LABEL": "vir",
                        "LEFT_LABEL": "-2000.00",
                        "RIGHT_LABEL": "+2000.00",
                        "SEQUENCE_NO": null,
                        "UNIT": "cc",
                        "TYPE": "01",
                        "EVENT_TYPE": null
                }
        ]
            },
            "RESPONSE_TAB": {
                "JSON_SUB_ID": null,
                        "PACKET_TYPE": null,
                        "DATA": null
            },
            "USER_ID": "US0001",
                    "LOGIN_STATUS": "1"
        }
    }
    }*/

    private ApiService() {
    }

    public static ApiService getInstance(Context context) {
        if (apiService == null) {
            apiService = new ApiService();
            apiService.processApiData("1", "00", "");
        }
        mContext = context;
        return apiService;
    }

    public void startApiService() {
        if (triggerWebService.get()) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    postData();
                }
            }, 5000);
        }
    }

    private void postData() {
        try {
            ApplicationClass.httpRequest(mContext, "Mobile/MobileData?Data=", getKeepAliveObject(),
                    Request.Method.POST, new VolleyCallback() {
                        @Override
                        public void OnSuccess(JSONObject object) {
                            try {
                                responseObject = object.getJSONObject("Response")
                                        .getJSONObject("DATAS").getJSONObject("RESPONSE_WEB");
                                String[] spiltData = responseObject.getString("PACKET_TYPE").split("\\$");
                                packetType = spiltData[0];
                                tempString = spiltData[1];
                                jsonSubID = responseObject.getString("JSON_SUB_ID");
                                processApiData(packetType, jsonSubID, "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void OnFailure(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        startApiService();

    }

    public void processApiData(String packetType, String jsonSubID, String eventType) {
        if (!jsonSubID.equals("99")) {
            finalArr = new JSONArray();
        }
        try {
            if (packetType.equals(WRITE_PACKET)) {
                switch (jsonSubID) {
                    case "00":
                        responseTabId = "00";
                        responseTabData = "";
                        if (SharedPref.read(pref_USERLOGINPASSWORDCHANED, "").equals("passwordChanged")) {
                            for (int i = 0; i < userManagementDao.getUsermanagementEntity().size(); i++) {
                                dataObj = new JSONObject();
                                dataObj.put("INPUTNO", "");
                                dataObj.put("REQ", userManagementDao.getUsermanagementEntity().get(i).userId + "#"
                                        + userManagementDao.getUsermanagementEntity().get(i).userPassword);
                                dataObj.put("NAME_LABEL", "");
                                dataObj.put("LEFT_LABEL", "");
                                dataObj.put("RIGHT_LABEL", "");
                                dataObj.put("SEQUENCE_NO", "");
                                dataObj.put("UNIT", "");
                                dataObj.put("TYPE", "");
                                dataObj.put("EVENT_TYPE", eventType);
                                finalArr.put(dataObj);
                            }
                        } else {
                            dataObj = new JSONObject();
                            dataObj.put("INPUTNO", "");
                            dataObj.put("REQ", "");
                            dataObj.put("NAME_LABEL", "");
                            dataObj.put("LEFT_LABEL", "");
                            dataObj.put("RIGHT_LABEL", "");
                            dataObj.put("SEQUENCE_NO", "");
                            dataObj.put("UNIT", "");
                            dataObj.put("TYPE", "");
                            dataObj.put("EVENT_TYPE", eventType);
                            finalArr.put(dataObj);
                        }
                        break;
                    case "02":
                         processUserList(responseObject.getJSONArray("DATA").
                                getJSONObject(0).getJSONArray("REQ"));
                       /* responseTabId = "02";
                        dataObj = new JSONObject();
                        dataObj.put("INPUTNO", "");
                        dataObj.put("REQ", "");
                        dataObj.put("NAME_LABEL", "");
                        dataObj.put("LEFT_LABEL", "");
                        dataObj.put("RIGHT_LABEL", "");
                        dataObj.put("SEQUENCE_NO", "");
                        dataObj.put("UNIT", "");
                        dataObj.put("TYPE", "");
                        dataObj.put("EVENT_TYPE", eventType);
                        finalArr.put(dataObj);*/
                        break;

                    case "03":
                        processSiteDetails(responseObject.getJSONArray("DATA").
                                getJSONObject(0).getJSONArray("REQ"), 1);
                        break;
                    case "04":
                        writeInputConfiguration(responseObject.getJSONArray("DATA").
                                getJSONObject(0));
                        break;

                    case "05":
                        writeOutputConfiguration(responseObject.getJSONArray("DATA").
                                getJSONObject(0));
                        break;

                    case "06":
                        weekly = 1;
                        writTimerConfiguration(responseObject.getJSONArray("DATA").
                                getJSONObject(0));
                        break;

                    case "07":
                        writeVirtualConfiguration(responseObject.getJSONArray("DATA").
                                getJSONObject(0));
                        break;

                    case "08": // todo : Should Change
                        writeLockOutAck(responseObject.getJSONArray("DATA").
                                getJSONObject(0));
                        break;

                    case "09": // todo : Should Change
                        writeLockOut(responseObject.getJSONArray("DATA").getJSONObject(0));
                        break;

                    case "10": // todo : Should Change
                        writeDiagnosticSweep(responseObject.getJSONArray("DATA").getJSONObject(0));
                        break;
                }
            } else if (packetType.equals(READ_PACKET)) {
                switch (jsonSubID) {
                    case "00":
                        responseTabId = "00";
                        responseTabData = "";
                        if (SharedPref.read(pref_USERLOGINPASSWORDCHANED, "").equals("passwordChanged")) {
                            for (int i = 0; i < userManagementDao.getUsermanagementEntity().size(); i++) {
                                dataObj = new JSONObject();
                                dataObj.put("INPUTNO", "");
                                dataObj.put("REQ", userManagementDao.getUsermanagementEntity().get(i).userId + "#"
                                        + userManagementDao.getUsermanagementEntity().get(i).userPassword);
                                dataObj.put("NAME_LABEL", "");
                                dataObj.put("LEFT_LABEL", "");
                                dataObj.put("RIGHT_LABEL", "");
                                dataObj.put("SEQUENCE_NO", "");
                                dataObj.put("UNIT", "");
                                dataObj.put("TYPE", "");
                                dataObj.put("EVENT_TYPE", eventType);
                                finalArr.put(dataObj);
                            }
                        } else {
                            dataObj = new JSONObject();
                            dataObj.put("INPUTNO", "");
                            dataObj.put("REQ", "");
                            dataObj.put("NAME_LABEL", "");
                            dataObj.put("LEFT_LABEL", "");
                            dataObj.put("RIGHT_LABEL", "");
                            dataObj.put("SEQUENCE_NO", "");
                            dataObj.put("UNIT", "");
                            dataObj.put("TYPE", "");
                            dataObj.put("EVENT_TYPE", eventType);
                            finalArr.put(dataObj);
                        }
                        break;
                    case "02":
                        processUserList(responseObject.getJSONArray("DATA").
                                getJSONObject(0).getJSONArray("REQ"));

                        break;
                    case "03":
                        processSiteDetails(responseObject.getJSONArray("DATA").
                                getJSONObject(0).getJSONArray("REQ"), 1);

                        break;
                    case "04":
                        readInputConfiguration(eventType);
                        break;
                    case "05":
                        readOutputConfiguration(eventType);
                        break;
                    case "06":
                        readTimerConfiguration(eventType);
                        break;
                    case "07":
                        readVirtualConfiguration(eventType);
                        break;
                    case "08":
                        readDiagnostics();
                        break;
                }
            }

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }

    private void writeDiagnosticSweep(JSONObject data) {
        try {
            String[] spiltData = data.getString("REQ").split("\\*")[1].split("\\$");
            // ex req -> {*0$11$05$0*}
            if (spiltData[0].equals(WRITE_PACKET)) {
                if (spiltData[1].equals(PCK_DIAGNOSTIC)) {
                    if (spiltData[3].equals(RES_SUCCESS)) {
                        DB.alarmLogDao().updateLockAlarm(Integer.parseInt(spiltData[2]), "0");
                        new EventLogDemo("", "", "Diagnostic Sweep Acknowledged by #", SharedPref.read(pref_USERLOGINID, ""), mContext);
                        ApiService.getInstance(mContext).processApiData("1", "00", ("Diagnostic Sweep Acknowledged by #" + SharedPref.read(pref_USERLOGINID, "")));
                        dataObj.put("INPUTNO", "");
                        dataObj.put("REQ", "ACK");
                        dataObj.put("NAME_LABEL", "");
                        dataObj.put("LEFT_LABEL", "");
                        dataObj.put("RIGHT_LABEL", "");
                        dataObj.put("SEQUENCE_NO", "");
                        dataObj.put("UNIT", "");
                        dataObj.put("TYPE", "");
                        dataObj.put("EVENT_TYPE", "");
                        finalArr.put(dataObj);
                    } else {
                        dataObj.put("INPUTNO", "");
                        dataObj.put("REQ", "ACK");
                        dataObj.put("NAME_LABEL", "");
                        dataObj.put("LEFT_LABEL", "");
                        dataObj.put("RIGHT_LABEL", "");
                        dataObj.put("SEQUENCE_NO", "");
                        dataObj.put("UNIT", "");
                        dataObj.put("TYPE", "");
                        dataObj.put("EVENT_TYPE", "");
                        finalArr.put(dataObj);
                        // do nothing
                    }
                } else {
                    nack();
                }
            } else {
                nack();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void writeLockOut(JSONObject data) {
        try {
            ApplicationClass.getInstance().sendPacket(new DataReceiveCallback() {
                @Override
                public void OnDataReceive(String data) {
                    if (data != null) {
                        if (data.equals("Timeout")) {
                            String[] spiltData = data.split("\\*")[1].split("\\$");
                            if (spiltData[0].equals(WRITE_PACKET)) {
                                if (spiltData[1].equals(OUTPUT_CONTROL_CONFIG)) {
                                    if (spiltData[2].equals(RES_SUCCESS)) {
                                        try {
                                            dataObj = new JSONObject();
                                            dataObj.put("INPUTNO", "");
                                            dataObj.put("REQ", "ACK");
                                            dataObj.put("NAME_LABEL", "");
                                            dataObj.put("LEFT_LABEL", "");
                                            dataObj.put("RIGHT_LABEL", "");
                                            dataObj.put("SEQUENCE_NO", "");
                                            dataObj.put("UNIT", "");
                                            dataObj.put("TYPE", "");
                                            dataObj.put("EVENT_TYPE", "");
                                            finalArr.put(dataObj);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        nack();
                                    }
                                } else {
                                    nack();
                                }
                            } else {
                                nack();
                            }
                        } else {
                            nack();
                        }
                    } else {
                        nack();
                    }
                }
            }, data.getString("REQ").substring(2, data.getString("REQ").length() - 2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void writeLockOutAck(JSONObject jsonObject) {
        try {
            ApplicationClass.getInstance().sendPacket(new DataReceiveCallback() {
                @Override
                public void OnDataReceive(String data) {
                    String[] splitData = data.split("\\*")[1].split("\\$");
                    if (splitData[0].equals(WRITE_PACKET)) {
                        if (splitData[1].equals(PCK_LOCKOUT)) {
                            if (splitData[2].equals("1")) {
                                // processApiData("1", "00", ("LockOut Alarm Acknowledged by #" + SharedPref.read(pref_USERLOGINID, "")));
                                // Ack
                                try {
                                    String hNo = jsonObject.getString("REQ").split("\\*")[1].split("\\$")[4];
                                    AlarmLogDao alarmLogDao = DB.alarmLogDao();
                                    alarmLogDao.updateLockAlarm(Integer.parseInt(hNo), "0");
                                    dataObj = new JSONObject();
                                    dataObj.put("INPUTNO", "");
                                    dataObj.put("REQ", "ACK");
                                    dataObj.put("NAME_LABEL", "");
                                    dataObj.put("LEFT_LABEL", "");
                                    dataObj.put("RIGHT_LABEL", "");
                                    dataObj.put("SEQUENCE_NO", "");
                                    dataObj.put("UNIT", "");
                                    dataObj.put("TYPE", "");
                                    dataObj.put("EVENT_TYPE", "");
                                    finalArr.put(dataObj);
                                    new EventLogDemo("", "", "LockOut Alarm Acknowledged by #", jsonObject.getString("EVENT_TYPE"), mContext);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // nAck
                                nack();
                            }
                        } else {
                            // nAck
                            nack();
                        }
                    } else {
                        // nAck
                        nack();
                    }
                }
            }, jsonObject.getString("REQ").substring(2, jsonObject.getString("REQ").length() - 2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void nack() {
        try {
            dataObj = new JSONObject();
            dataObj.put("INPUTNO", "");
            dataObj.put("REQ", "NACK");
            dataObj.put("NAME_LABEL", "");
            dataObj.put("LEFT_LABEL", "");
            dataObj.put("RIGHT_LABEL", "");
            dataObj.put("SEQUENCE_NO", "");
            dataObj.put("UNIT", "");
            dataObj.put("TYPE", "");
            dataObj.put("EVENT_TYPE", "");
            finalArr.put(dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void readDiagnostics() {
        frameDiagnosticsPacket("0");
    }

    public void frameDiagnosticsPacket(String setId) {
        ApplicationClass.getInstance().sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR +
                READ_PACKET + SPILT_CHAR + PCK_DIAGNOSTIC + SPILT_CHAR + setId);
    }

   /* private void processDefaultConfiguration(JSONObject data) {
        responseTabId = "12";
        try {
            responseTabData = "{*1$0$14$01000$02010$03040$04050$05090$06091$07092$08093$09094$10095" +
                    "$11096$12097$13098$14099$15021$16022$17023$18061$19062$20063$21064" +
                    "$22065$23066$24067$25068$26031$27032$28033$29034$30035$31036$32037" +
                    "$33038$34081$35082$36083$37084$38085$39086$40087$41088$42071$43072" +
                    "$44073$45074$46075$47076$48077$49078*}";
        } catch (Exception e) {
            //    e.printStackTrace();
        }
    }

    private void processInputConfiguration(JSONObject dataObj) {
        // INPUT_NO
        responseTabId = "10";
        // responseTabData = "{*1$04$0$01$00$1$0$PHSensor$0$1$33$10$400$1300$10$0$1*}";
        try {
            responseTabData = getInputSensorConfig(dataObj.getString("INPUT_NO"));
        } catch (JSONException e) {
            //    e.printStackTrace();
        }
    }*/

    //inputConfiguration
    public void readInputConfiguration(String eventType) {
        responseTabId = "04";
        InputConfigurationDao inputDao = DB.inputConfigurationDao();
        for (int i = 0; i < inputDao.getInputConfigurationEntityList().size(); i++) {
            dataObj = new JSONObject();
            try {
                dataObj.put("INPUTNO", inputDao.getInputConfigurationEntityList().get(i).hardwareNo);
                dataObj.put("REQ", inputDao.getInputConfigurationEntityList().get(i).writePacket);
                dataObj.put("NAME_LABEL", inputDao.getInputConfigurationEntityList().get(i).inputLabel);
                dataObj.put("LEFT_LABEL", inputDao.getInputConfigurationEntityList().get(i).subValueOne);
                dataObj.put("RIGHT_LABEL", inputDao.getInputConfigurationEntityList().get(i).subValueTwo);
                dataObj.put("SEQUENCE_NO", inputDao.getInputConfigurationEntityList().get(i).inputSequenceNumber);
                dataObj.put("UNIT", inputDao.getInputConfigurationEntityList().get(i).unit);
                dataObj.put("TYPE", inputDao.getInputConfigurationEntityList().get(i).inputsequenceName);
                dataObj.put("EVENT_TYPE", eventType);
            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    dataObj.put("INPUTNO", "");
                    dataObj.put("REQ", "NACK");
                    dataObj.put("NAME_LABEL", "");
                    dataObj.put("LEFT_LABEL", "");
                    dataObj.put("RIGHT_LABEL", "");
                    dataObj.put("SEQUENCE_NO", "");
                    dataObj.put("UNIT", "");
                    dataObj.put("TYPE", "");
                    dataObj.put("EVENT_TYPE", "");
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }


            }
            finalArr.put(dataObj);

        }

    }

    private void writeInputConfiguration(JSONObject jsonObject) {
        try {
            ApplicationClass.getInstance().sendPacket(new DataReceiveCallback() {
                @Override
                public void OnDataReceive(String data) {
                    dataObj = new JSONObject();
                    responseTabId = "04";
                    try {
                        String[] splitData = jsonObject.getString("REQ").
                                split("\\*")[1].split(RES_SPILT_CHAR);
                        String[] splitValidation = data.split("\\*")[1].split(RES_SPILT_CHAR);
                        if (splitValidation[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
                            int hardWareNo = Integer.parseInt(jsonObject.getString("INPUTNO"));
                            if (splitValidation[3].equals(RES_SUCCESS)) {
                                String inputLabel = jsonObject.getString("NAME_LABEL");
                                String lowAlarm = jsonObject.getString("LEFT_LABEL");
                                String highAlarm = jsonObject.getString("RIGHT_LABEL");
                                int seqNo = Integer.parseInt(jsonObject.getString("SEQUENCE_NO"));
                                String unit = jsonObject.getString("UNIT");
                                String type = jsonObject.getString("TYPE");
                                String sensorType = "SENSOR";
                                String sequenceName = "";
                                int flagValue = 0;
                                int signalType = 0;
                                if (hardWareNo == 1) {
                                    sensorType = "SENSOR";
                                    sequenceName = "pH";
                                    flagValue = Integer.parseInt(splitData[splitData.length - 1]);
                                } else if (hardWareNo == 2) {
                                    sensorType = "SENSOR";
                                    sequenceName = "ORP";
                                    flagValue = Integer.parseInt(splitData[splitData.length - 1]);
                                    unit = "mV";
                                } else if (hardWareNo == 3) {
                                    sensorType = "SENSOR";
                                    sequenceName = "Contacting Conductivity";
                                    flagValue = Integer.parseInt(splitData[splitData.length - 1]);
                                } else if (hardWareNo == 4) {
                                    sensorType = "SENSOR";
                                    sequenceName = "Toroidal Conductivity";
                                    flagValue = Integer.parseInt(splitData[splitData.length - 1]);
                                } else if (hardWareNo < 14) {
                                    sensorType = "MODBUS";
                                    int modbustype = Integer.parseInt(jsonObject.getString("TYPE"));
                                    type = typeArr[modbustype];
                                    sequenceName = modBusTypeArr[seqNo] + typeArr[modbustype];
                                    flagValue = Integer.parseInt(splitData[splitData.length - 1]);
                                } else if (hardWareNo < 17) {
                                    sensorType = "SENSOR";
                                    sequenceName = "Temperature -" + seqNo;
                                    flagValue = Integer.parseInt(splitData[splitData.length - 1]);
                                    unit = "°C";
                                } else if (hardWareNo < 25) {
                                    sensorType = "Analog";
                                    sequenceName = seqNo < 6 ? sensorType + " - " + seqNo + "(4-20mA)" : sensorType + " - " + seqNo + "(0-10mA)";
                                    flagValue = Integer.parseInt(splitData[splitData.length - 1]);
                                } else if (hardWareNo < 33) {
                                    sensorType = "FLOWMETER";
                                    sequenceName = "Flow Meter - " + seqNo;
                                    flagValue = Integer.parseInt(splitData[splitData.length - 1]);
                                } else if (hardWareNo < 41) {
                                    sensorType = "DIGITAL";
                                    signalType = 1;
                                    sequenceName = "Digital Sensor -" + seqNo;
                                    flagValue = Integer.parseInt(splitData[splitData.length - 2]);
                                } else {
                                    sensorType = "TANK";
                                    signalType = 1;
                                    sequenceName = "Tank Level -" + seqNo;
                                    flagValue = Integer.parseInt(splitData[splitData.length - 2]);
                                }
                               if(hardWareNo != 0) {
                                   InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                                           (hardWareNo, inputTypeArr[Integer.parseInt(splitData[5])],
                                                   sensorType, signalType, sequenceName, seqNo, inputLabel,
                                                   lowAlarm, highAlarm, unit, type, flagValue,
                                                   jsonObject.getString("REQ"));
                                   List<InputConfigurationEntity> inputentryList = new ArrayList<>();
                                   inputentryList.add(entityUpdate);
                                   updateInputDB(inputentryList);
                               }
                                dataObj.put("INPUTNO", formDigits(2, String.valueOf(hardWareNo)));
                                dataObj.put("REQ", "ACK");
                                dataObj.put("NAME_LABEL", "");
                                dataObj.put("LEFT_LABEL", "");
                                dataObj.put("RIGHT_LABEL", "");
                                dataObj.put("SEQUENCE_NO", "");
                                dataObj.put("UNIT", "");
                                dataObj.put("TYPE", "");
                                dataObj.put("EVENT_TYPE", "");
                                finalArr.put(dataObj);
                            } else {
                                dataObj.put("INPUTNO", formDigits(2, String.valueOf(hardWareNo)));
                                dataObj.put("REQ", "NACK");
                                dataObj.put("NAME_LABEL", "");
                                dataObj.put("LEFT_LABEL", "");
                                dataObj.put("RIGHT_LABEL", "");
                                dataObj.put("SEQUENCE_NO", "");
                                dataObj.put("UNIT", "");
                                dataObj.put("TYPE", "");
                                dataObj.put("EVENT_TYPE", "");
                                finalArr.put(dataObj);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        sendNack();
                    }

                }

            }, jsonObject.getString("REQ").substring(2, jsonObject.getString("REQ").length() - 2));


            Log.e(TAG, "writeInputConfiguration: " + jsonObject.getString("REQ"));
        } catch (Exception e) {
             sendNack();
        }
        finalArr.put(dataObj);

    }

    private void sendNack() {
        try {
            dataObj = new JSONObject();
            dataObj.put("INPUTNO", "");
            dataObj.put("REQ", "NACK");
            dataObj.put("NAME_LABEL", "");
            dataObj.put("LEFT_LABEL", "");
            dataObj.put("RIGHT_LABEL", "");
            dataObj.put("SEQUENCE_NO", "");
            dataObj.put("UNIT", "");
            dataObj.put("TYPE", "");
            dataObj.put("EVENT_TYPE", "");
            finalArr.put(dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
            sendNack();
        }
    }

    //outputConfiguration
    public void readOutputConfiguration(String eventType) {
        responseTabId = "05";
        OutputConfigurationDao outputDao = DB.outputConfigurationDao();
        try {
            for (int i = 0; i < outputDao.getOutputConfigurationEntityList().size(); i++) {
                dataObj = new JSONObject();
                dataObj.put("INPUTNO", outputDao.getOutputConfigurationEntityList().get(i).outputHardwareNo);
                dataObj.put("REQ", outputDao.getOutputConfigurationEntityList().get(i).writePacket);
                dataObj.put("NAME_LABEL", "");
                dataObj.put("LEFT_LABEL", "");
                dataObj.put("RIGHT_LABEL", "");
                dataObj.put("SEQUENCE_NO", "");
                dataObj.put("UNIT", "");
                dataObj.put("TYPE", "");
                dataObj.put("EVENT_TYPE", eventType);
                finalArr.put(dataObj);
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                dataObj.put("INPUTNO", "");
                dataObj.put("REQ", "NACK");
                dataObj.put("NAME_LABEL", "");
                dataObj.put("LEFT_LABEL", "");
                dataObj.put("RIGHT_LABEL", "");
                dataObj.put("SEQUENCE_NO", "");
                dataObj.put("UNIT", "");
                dataObj.put("TYPE", "");
                dataObj.put("EVENT_TYPE", "");
                finalArr.put(dataObj);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }

    }

    private void writeOutputConfiguration(JSONObject jsonObject) {
        try {
            ApplicationClass.getInstance().sendPacket(new DataReceiveCallback() {
                @Override
                public void OnDataReceive(String data) {
                    responseTabId = "05";
                    try {
                        String[] splitData = jsonObject.getString("REQ").
                                split("\\*")[1].split(RES_SPILT_CHAR);
                        String[] splitValidation = data.split("\\*")[1].split(RES_SPILT_CHAR);
                        int hardWareNo = Integer.parseInt(jsonObject.getString("INPUTNO"));
                        if (splitValidation[1].equals(PCK_OUTPUT_CONFIG) && splitValidation[2].equals(RES_SUCCESS)) {
                            String inputLabel = jsonObject.getString("NAME_LABEL");
                            String lowAlarm = jsonObject.getString("LEFT_LABEL");
                            String highAlarm = jsonObject.getString("RIGHT_LABEL");
                            if (hardWareNo > 14 && !highAlarm.isEmpty()) {
                                if (highAlarm.contains("I")) {
                                    highAlarm = "Input- " + highAlarm.substring(1) + " (" + inputDAO.getInputLabel(Integer.parseInt(highAlarm.substring(1))) + ")";
                                } else {
                                    highAlarm = "Output- " + highAlarm.substring(1) + " (" + outputDAO.getOutputLabel(Integer.parseInt(highAlarm.substring(1))) + ")";
                                }
                            }
                            dataObj.put("INPUTNO", jsonObject.getString("INPUTNO"));
                            dataObj.put("REQ", "ACK");
                            dataObj.put("NAME_LABEL", "");
                            dataObj.put("LEFT_LABEL", "");
                            dataObj.put("RIGHT_LABEL", "");
                            dataObj.put("SEQUENCE_NO", "");
                            dataObj.put("UNIT", "");
                            dataObj.put("TYPE", "");
                            dataObj.put("EVENT_TYPE", "");
                            finalArr.put(dataObj);
                            OutputConfigurationEntity entityUpdate = new OutputConfigurationEntity
                                    (hardWareNo, "Output-" + hardWareNo + "(" + inputLabel + ")", inputLabel,
                                            lowAlarm,
                                            highAlarm, jsonObject.getString("REQ"));
                            List<OutputConfigurationEntity> entryListUpdate = new ArrayList<>();
                            entryListUpdate.add(entityUpdate);
                            updateOutPutDB(entryListUpdate);
                        } else {
                            dataObj.put("INPUTNO", jsonObject.getString("INPUTNO"));
                            dataObj.put("REQ", "NACK");
                            dataObj.put("NAME_LABEL", "");
                            dataObj.put("LEFT_LABEL", "");
                            dataObj.put("RIGHT_LABEL", "");
                            dataObj.put("SEQUENCE_NO", "");
                            dataObj.put("UNIT", "");
                            dataObj.put("TYPE", "");
                            dataObj.put("EVENT_TYPE", "");
                            finalArr.put(dataObj);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, jsonObject.getString("REQ").substring(2, jsonObject.getString("REQ").length() - 2));
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                dataObj.put("INPUTNO", "");
                dataObj.put("REQ", "NACK");
                dataObj.put("NAME_LABEL", "");
                dataObj.put("LEFT_LABEL", "");
                dataObj.put("RIGHT_LABEL", "");
                dataObj.put("SEQUENCE_NO", "");
                dataObj.put("UNIT", "");
                dataObj.put("TYPE", "");
                dataObj.put("EVENT_TYPE", "");
                finalArr.put(dataObj);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }
    }

    //virtualConfiguration
    private void readVirtualConfiguration(String eventType) {
        responseTabId = "07";
        VirtualConfigurationDao virtualConfigurationDao = DB.virtualConfigurationDao();
        for (int i = 0; i < virtualConfigurationDao.getVirtualConfigurationEntityList().size(); i++) {
            dataObj = new JSONObject();
            try {
                dataObj.put("INPUTNO", virtualConfigurationDao.getVirtualConfigurationEntityList().get(i).hardwareNo);
                dataObj.put("REQ", virtualConfigurationDao.getVirtualConfigurationEntityList().get(i).writePacket);
                dataObj.put("NAME_LABEL", "");
                dataObj.put("LEFT_LABEL", "");
                dataObj.put("RIGHT_LABEL", "");
                dataObj.put("SEQUENCE_NO", "");
                dataObj.put("UNIT", "");
                dataObj.put("TYPE", "");
                dataObj.put("EVENT_TYPE", eventType);
            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    dataObj.put("INPUTNO", "");
                    dataObj.put("REQ", "NACK");
                    dataObj.put("NAME_LABEL", "");
                    dataObj.put("LEFT_LABEL", "");
                    dataObj.put("RIGHT_LABEL", "");
                    dataObj.put("SEQUENCE_NO", "");
                    dataObj.put("UNIT", "");
                    dataObj.put("TYPE", "");
                    dataObj.put("EVENT_TYPE", "");
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
            finalArr.put(dataObj);
        }
    }

    private void writeVirtualConfiguration(JSONObject jsonObject) {
        try {
            ApplicationClass.getInstance().sendPacket(new DataReceiveCallback() {
                @Override
                public void OnDataReceive(String data) {
                    responseTabId = "07";
                    try {
                        int hardWareNo = Integer.parseInt(jsonObject.getString("INPUTNO"));
                        String virtualLabel = jsonObject.getString("NAME_LABEL");
                        String lowAlarm = jsonObject.getString("LEFT_LABEL");
                        String highAlarm = jsonObject.getString("RIGHT_LABEL");
                        int sensor_type = Integer.parseInt(jsonObject.getString("TYPE"));
                        String type = inputTypeArr[sensor_type];
                        String unit = jsonObject.getString("UNIT");
                        VirtualConfigurationEntity entityUpdate = new VirtualConfigurationEntity
                                (hardWareNo, "Virtual", 0, virtualLabel,
                                        type, lowAlarm, highAlarm, unit, jsonObject.getString("REQ"));
                        List<VirtualConfigurationEntity> virtualEntryList = new ArrayList<>();
                        virtualEntryList.add(entityUpdate);
                        updateVirtualDB(virtualEntryList);
                        dataObj.put("INPUTNO", String.valueOf(hardWareNo));
                        dataObj.put("REQ", "ACK");
                        dataObj.put("NAME_LABEL", "");
                        dataObj.put("LEFT_LABEL", "");
                        dataObj.put("RIGHT_LABEL", "");
                        dataObj.put("SEQUENCE_NO", "");
                        dataObj.put("UNIT", "");
                        dataObj.put("TYPE", "");
                        dataObj.put("EVENT_TYPE", "");
                        finalArr.put(dataObj);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            dataObj.put("INPUTNO", "");
                            dataObj.put("REQ", "NACK");
                            dataObj.put("NAME_LABEL", "");
                            dataObj.put("LEFT_LABEL", "");
                            dataObj.put("RIGHT_LABEL", "");
                            dataObj.put("SEQUENCE_NO", "");
                            dataObj.put("UNIT", "");
                            dataObj.put("TYPE", "");
                            dataObj.put("EVENT_TYPE", "");
                            finalArr.put(dataObj);
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }

                    }
                }
            }, jsonObject.getString("REQ").substring(2, jsonObject.getString("REQ").length() - 2));

        } catch (JSONException e) {
            e.printStackTrace();
            try {
                dataObj.put("INPUTNO", "");
                dataObj.put("REQ", "NACK");
                dataObj.put("NAME_LABEL", "");
                dataObj.put("LEFT_LABEL", "");
                dataObj.put("RIGHT_LABEL", "");
                dataObj.put("SEQUENCE_NO", "");
                dataObj.put("UNIT", "");
                dataObj.put("TYPE", "");
                dataObj.put("EVENT_TYPE", "");
                finalArr.put(dataObj);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }
    }

    //timerConfiguration
    private void readTimerConfiguration(String eventType) {
        responseTabId = "06";
        TimerConfigurationDao timerConfigurationDao = DB.timerConfigurationDao();
        for (int i = 0; i < timerConfigurationDao.geTimerConfigurationEntityList().size(); i++) {
            dataObj = new JSONObject();
            try {
                dataObj.put("INPUTNO", timerConfigurationDao.geTimerConfigurationEntityList().get(i).timerNo);
                dataObj.put("REQ", timerConfigurationDao.geTimerConfigurationEntityList().get(i).getMainTimerPacket() + "#" +
                        timerConfigurationDao.geTimerConfigurationEntityList().get(i).getWeekOnePacket() + "#" +
                        timerConfigurationDao.geTimerConfigurationEntityList().get(i).getWeekTwoPacket() + "#" +
                        timerConfigurationDao.geTimerConfigurationEntityList().get(i).getWeekThreePacket() + "#" +
                        timerConfigurationDao.geTimerConfigurationEntityList().get(i).getWeekFourPacket());
                dataObj.put("NAME_LABEL", "");
                dataObj.put("LEFT_LABEL", "");
                dataObj.put("RIGHT_LABEL", "");
                dataObj.put("SEQUENCE_NO", "");
                dataObj.put("UNIT", "");
                dataObj.put("TYPE", "");
                dataObj.put("EVENT_TYPE", eventType);
                finalArr.put(dataObj);
            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    dataObj.put("INPUTNO", "");
                    dataObj.put("REQ", "NACK");
                    dataObj.put("NAME_LABEL", "");
                    dataObj.put("LEFT_LABEL", "");
                    dataObj.put("RIGHT_LABEL", "");
                    dataObj.put("SEQUENCE_NO", "");
                    dataObj.put("UNIT", "");
                    dataObj.put("TYPE", "");
                    dataObj.put("EVENT_TYPE", "");
                    finalArr.put(dataObj);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }

        }

    }

    public void timerFramePacket(String packet) {
        ApplicationClass.getInstance().sendPacket(this, packet);
    }

    private void writTimerConfiguration(JSONObject jsonObject) {
        try {
            splitTimer = jsonObject.getString("REQ").split("#");
            timerJson = new JSONObject();
            timerJson = jsonObject;
            timerFramePacket(splitTimer[0].substring(2, splitTimer[0].length() - 2));
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                dataObj.put("INPUTNO", "");
                dataObj.put("REQ", "NACK");
                dataObj.put("NAME_LABEL", "");
                dataObj.put("LEFT_LABEL", "");
                dataObj.put("RIGHT_LABEL", "");
                dataObj.put("SEQUENCE_NO", "");
                dataObj.put("UNIT", "");
                dataObj.put("TYPE", "");
                dataObj.put("EVENT_TYPE", "");
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }
    }

    @Override
    public void OnDataReceive(String data) {
        if (data.equals("FailedToConnect")) {
        } else if (data.equals("pckError")) {
            Log.e(TAG, "pckError: ");
        } else if (data.equals("sendCatch")) {
            Log.e(TAG, "sendCatch: ");
        } else if (data.equals("Timeout")) {
            Log.e(TAG, "Timeout: ");
        } else if (data != null) {
            handleResponse(data.split("\\*")[1].split(RES_SPILT_CHAR), data);
        }
    }

    public void handleResponse(String[] splitData, String data) {
        //diagnosticsData
        if (splitData[1].equals("11")) {
            switch (splitData[3]) {
                case "0":
                    diagnosticsDataOne = data;
                    Log.e(TAG, "handleResponse: " + data);
                    frameDiagnosticsPacket("1");
                    break;
                case "1":
                    diagnosticsDataTwo = data;
                    Log.e(TAG, "handleResponse: " + data);
                    frameDiagnosticsPacket("2");
                    break;
                case "2":
                    diagnosticsDataThree = data;
                    Log.e(TAG, "handleResponse: " + data);
                    frameDiagnosticsPacket("3");
                    break;
                case "3":
                    diagnosticsDataFour = data;
                    Log.e(TAG, "handleResponse: " + data);
                    frameDiagnosticsPacket("4");
                    break;
                case "4":
                    diagnosticsDataFive = data;
                    Log.e(TAG, "handleResponse: " + data);
                    frameDiagnosticsPacket("5");
                    break;
                case "5":
                    diagnosticsDataSix = data;
                    responseTabId = "08";
                    responseTabData = diagnosticsDataOne + "#" + diagnosticsDataTwo + "#" + diagnosticsDataThree + "#" +
                            diagnosticsDataFour + "#" + diagnosticsDataFive + "#" + diagnosticsDataSix;
                    dataObj = new JSONObject();
                    try {
                        dataObj.put("INPUTNO", "");
                        dataObj.put("REQ", responseTabData);
                        dataObj.put("NAME_LABEL", "");
                        dataObj.put("LEFT_LABEL", "");
                        dataObj.put("RIGHT_LABEL", "");
                        dataObj.put("SEQUENCE_NO", "");
                        dataObj.put("UNIT", "");
                        dataObj.put("TYPE", "");
                        dataObj.put("EVENT_TYPE", "");
                        finalArr.put(dataObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
        if (splitData[1].equals("08") || splitData[1].equals("09")) {
            try {
                if (splitData[0].equals("0") && splitData[1].equals("08") && splitData[2].equals("0")) {
                    timerFramePacket(splitTimer[1].substring(2, splitTimer[1].length() - 2));
                    weekly = 1;

                } else if (splitData[0].equals("0") && splitData[1].equals("09") && splitData[2].equals("0")) {
                    switch (weekly) {
                        case 1:
                            weekly = 2;
                            timerFramePacket(splitTimer[2].substring(2, splitTimer[2].length() - 2));

                            break;
                        case 2:
                            weekly = 3;
                            timerFramePacket(splitTimer[3].substring(2, splitTimer[3].length() - 2));

                            break;
                        case 3:
                            weekly = 4;
                            timerFramePacket(splitTimer[4].substring(2, splitTimer[4].length() - 2));
                            break;
                        case 4:
                            responseTabId = "06";
                            weekly = 0;
                            int timerNo = Integer.parseInt(timerJson.getString("INPUTNO"));
                            String timerName = timerJson.getString("NAME_LABEL");
                            String lowAlarm = timerJson.getString("LEFT_LABEL");
                            String highAlarm = timerJson.getString("RIGHT_LABEL");
                            String[] splitTimer = timerJson.getString("REQ").split("#");
                            TimerConfigurationEntity timerConfigurationEntity = new TimerConfigurationEntity
                                    (timerNo, timerName, lowAlarm, highAlarm, splitTimer[0], splitTimer[1], splitTimer[2], splitTimer[3], splitTimer[4]);
                            List<TimerConfigurationEntity> entryListUpdate = new ArrayList<>();
                            entryListUpdate.add(timerConfigurationEntity);
                            updateTimerDB(entryListUpdate);
                            dataObj.put("INPUTNO", String.valueOf(timerNo));
                            dataObj.put("REQ", "ACK");
                            dataObj.put("NAME_LABEL", "");
                            dataObj.put("LEFT_LABEL", "");
                            dataObj.put("RIGHT_LABEL", "");
                            dataObj.put("SEQUENCE_NO", "");
                            dataObj.put("UNIT", "");
                            dataObj.put("TYPE", "");
                            dataObj.put("EVENT_TYPE", "");
                            finalArr.put(dataObj);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    dataObj.put("INPUTNO", "");
                    dataObj.put("REQ", "NACK");
                    dataObj.put("NAME_LABEL", "");
                    dataObj.put("LEFT_LABEL", "");
                    dataObj.put("RIGHT_LABEL", "");
                    dataObj.put("SEQUENCE_NO", "");
                    dataObj.put("UNIT", "");
                    dataObj.put("TYPE", "");
                    dataObj.put("EVENT_TYPE", "");
                    finalArr.put(dataObj);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();

                }
            }

        }

    }

    private String getInputSensorConfig(String inputNo) {
        final String[] mData = {"NACK"};
        ApplicationClass.getInstance().sendPacket(new DataReceiveCallback() {
            @Override
            public void OnDataReceive(String data) {
                if (ApplicationClass.getInstance().isValidPck(READ_PACKET, data, mContext)) {
                    mData[0] = data;
                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, inputNo));
        return mData[0];
    }

    private void processUserList(JSONArray userArr) {
        List<UsermanagementEntity> userList = new ArrayList<>();
        try {
            for (int i = 0; i < userArr.length(); i++) {
                JSONObject tempUser = userArr.getJSONObject(i);
                userList.add(new UsermanagementEntity(tempUser.getString("USERID"),
                        tempUser.getString("USERNAME"),
                        tempUser.getInt("ROLE"),
                        tempUser.getString("PASSWORD"),
                        tempUser.getString("CONTACT"),
                        "",
                        "",
                        tempUser.getString("LOGINSTATUS")));

                //tempUser.getString("UPDATED_BY")
                // tempUser.getString("UPDATED_DATE")
            }
            UserManagementDao dao = DB.userManagementDao();
            dao.insert(userList.toArray(new UsermanagementEntity[0]));
            responseTabId = "02";
            responseTabData = "ACK";
            dataObj = new JSONObject();
            dataObj.put("INPUTNO", "");
            dataObj.put("REQ", responseTabData);
            dataObj.put("NAME_LABEL", "");
            dataObj.put("LEFT_LABEL", "");
            dataObj.put("RIGHT_LABEL", "");
            dataObj.put("SEQUENCE_NO", "");
            dataObj.put("UNIT", "");
            dataObj.put("TYPE", "");
            dataObj.put("EVENT_TYPE", "");
            finalArr.put(dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                dataObj.put("INPUTNO", "");
                dataObj.put("REQ", "NACK");
                dataObj.put("NAME_LABEL", "");
                dataObj.put("LEFT_LABEL", "");
                dataObj.put("RIGHT_LABEL", "");
                dataObj.put("SEQUENCE_NO", "");
                dataObj.put("UNIT", "");
                dataObj.put("TYPE", "");
                dataObj.put("EVENT_TYPE", "");
                finalArr.put(dataObj);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }
    }

    private void processSiteDetails(JSONArray siteDetailsObject, int pck) {
        try {
            SharedPref.write(pref_SITEID, siteDetailsObject.getJSONObject(0).getString("SITE_ID"));
            SharedPref.write(pref_SITENAME, siteDetailsObject.getJSONObject(0).getString("SITE_NAME"));
            SharedPref.write(pref_SITELOCATION, siteDetailsObject.getJSONObject(0).getString("SITE_LOCATION"));
            SharedPref.write(pref_CONTROLLERPASSWORD, siteDetailsObject.getJSONObject(0).getString("CONTROLLER_PASSWORD"));
            SharedPref.write(pref_CONTROLLERISACTIVE, (siteDetailsObject.getJSONObject(0).getString("ISACTIVE").equals("1")));

             if (pck == 1) {
                responseTabId = "03";
                responseTabData = "ACK";
                dataObj = new JSONObject();
                dataObj.put("INPUTNO", "");
                dataObj.put("REQ", responseTabData);
                dataObj.put("NAME_LABEL", "");
                dataObj.put("LEFT_LABEL", "");
                dataObj.put("RIGHT_LABEL", "");
                dataObj.put("SEQUENCE_NO", "");
                dataObj.put("UNIT", "");
                dataObj.put("TYPE", "");
                dataObj.put("EVENT_TYPE", "");
                finalArr.put(dataObj);
             } else {
                ApplicationClass.getInstance().sendPacket(new DataReceiveCallback() {
                    @Override
                    public void OnDataReceive(String data) {
                        if (data != null) {
                            if (data.equals("Timeout")) {
                                String[] spiltData = data.split("\\*")[1].split("\\$");
                                if (spiltData[0].equals(WRITE_PACKET)) {
                                    if (spiltData[2].equals(RES_SUCCESS)) {
                                        new EventLogDemo("0", "Site", "General settings changed by #", SharedPref.read(pref_USERLOGINID, ""), mContext);
                                    } else {
                                        nack();
                                    }
                                } else {
                                    nack();
                                }
                            } else {
                                nack();
                            }
                        } else {
                            nack();
                        }

                        try {
                            responseTabId = "03";
                            responseTabData = "ACK";
                            dataObj = new JSONObject();
                            dataObj.put("INPUTNO", "");
                            dataObj.put("REQ", responseTabData);
                            dataObj.put("NAME_LABEL", "");
                            dataObj.put("LEFT_LABEL", "");
                            dataObj.put("RIGHT_LABEL", "");
                            dataObj.put("SEQUENCE_NO", "");
                            dataObj.put("UNIT", "");
                            dataObj.put("TYPE", "");
                            dataObj.put("EVENT_TYPE", "");
                            finalArr.put(dataObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, DEVICE_PASSWORD + SPILT_CHAR +
                        CONN_TYPE + SPILT_CHAR +
                        WRITE_PACKET + SPILT_CHAR +
                        PCK_GENERAL + SPILT_CHAR +
                        siteDetailsObject.getJSONObject(0).getString("SITE_ID") + SPILT_CHAR +
                        siteDetailsObject.getJSONObject(0).getString("SITE_NAME") + SPILT_CHAR +
                        siteDetailsObject.getJSONObject(0).getString("CONTROLLER_PASSWORD") + SPILT_CHAR +
                        (siteDetailsObject.getJSONObject(0).getString("ISACTIVE").equals("1") ? "1" : "0") + SPILT_CHAR +
                        siteDetailsObject.getJSONObject(0).getString("SITE_LOCATION") + SPILT_CHAR +
                        siteDetailsObject.getJSONObject(0).getString("ALARM_DELAY") + SPILT_CHAR +
                        "0" + SPILT_CHAR + siteDetailsObject.getJSONObject(0).getString("RTC"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                dataObj.put("INPUTNO", "");
                dataObj.put("REQ", "NACK");
                dataObj.put("NAME_LABEL", "");
                dataObj.put("LEFT_LABEL", "");
                dataObj.put("RIGHT_LABEL", "");
                dataObj.put("SEQUENCE_NO", "");
                dataObj.put("UNIT", "");
                dataObj.put("TYPE", "");
                dataObj.put("EVENT_TYPE", "");
                finalArr.put(dataObj);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }
    }

    /* Forming JSONObject for Api Communication */
    private JSONObject getKeepAliveObject() {
        JSONObject finalObject = new JSONObject();
        JSONObject dataObject = new JSONObject();
        try {
            finalObject.put("JSON_ID", "01");
            finalObject.put("DU_MAC", Settings.System.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
            finalObject.put("DEVICE_MAC", SharedPref.read(pref_MACADDRESS, "N/A"));
            finalObject.put("TIMESTAMP", System.currentTimeMillis());
            dataObject.put("ALERT_RESPONSE", alertKeepAliveData);
            dataObject.put("OUTPUT_RESPONSE", outputKeepAliveData);
            dataObject.put("MSG_FIELD", inputKeepAliveData);
            dataObject.put("LABLE", "");

            dataObject.put("RESPONSE_WEB", "");
            dataObject.put("RESPONSE_TAB", getResponceTab() == null ? "NACK" : getResponceTab());
            dataObject.put("USER_ID", SharedPref.read(pref_USERLOGINID, ""));
            dataObject.put("LOGIN_STATUS", SharedPref.read(pref_USERLOGINSTATUS,0));
            finalObject.put("DATAS", dataObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return finalObject;
    }

    private JSONObject getResponceTab() {
        try {
            JSONObject responseObj = new JSONObject();
            responseObj.put("JSON_SUB_ID", responseTabId);
            responseObj.put("PACKET_TYPE", packetType + "$" + tempString);
            responseObj.put("DATA", finalArr);
            return responseObj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isValidPck(String pckType, String data, Context context) {
        if (data.equals("FailedToConnect")) {
            return false;
        } else if (data.equals("pckError")) {
            return false;
        } else if (data.equals("sendCatch")) {
            return false;
        } else if (data.equals("Timeout")) {
            return false;
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

    private void updateInputDB(List<InputConfigurationEntity> entryList) {
        InputConfigurationDao dao = DB.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    private void updateVirtualDB(List<VirtualConfigurationEntity> entryList) {
        VirtualConfigurationDao dao = DB.virtualConfigurationDao();
        dao.insert(entryList.toArray(new VirtualConfigurationEntity[0]));
    }

    private void updateTimerDB(List<TimerConfigurationEntity> entryList) {
        TimerConfigurationDao dao = DB.timerConfigurationDao();
        dao.insert(entryList.toArray(new TimerConfigurationEntity[0]));
    }

    public void updateOutPutDB(List<OutputConfigurationEntity> entryList) {
        OutputConfigurationDao dao = DB.outputConfigurationDao();
        dao.insert(entryList.toArray(new OutputConfigurationEntity[0]));
    }

}
