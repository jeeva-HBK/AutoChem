package com.ionexchange.Singleton;

import static com.ionexchange.Others.ApplicationClass.DB;
import static com.ionexchange.Others.ApplicationClass.alertKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.inputKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.modBusTypeArr;
import static com.ionexchange.Others.ApplicationClass.outputKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.triggerWebService;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
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

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.UserManagementDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.UsermanagementEntity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Interface.VolleyCallback;
import com.ionexchange.Others.ApplicationClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiService {
    private static ApiService apiService;
    static Context mContext;
    private static final String TAG = "API";
    String responseTabId = "00";
    String responseTabData = "";
    String packetType;

    JSONObject dataObj;
    JSONArray finalArr;


    private ApiService() {
    }

    public static ApiService getInstance(Context context) {
        if (apiService == null) {
            apiService = new ApiService();
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
            }, 3000);
        }
    }

    private void postData() {
        try {
            ApplicationClass.httpRequest(mContext, "Mobile/MobileData?Data=", getKeepAliveObject(),
                    Request.Method.POST, new VolleyCallback() {
                        @Override
                        public void OnSuccess(JSONObject object) {
                            processApiData(object);
                        }

                        @Override
                        public void OnFailure(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseTabId = "00";
        responseTabData = "";
        startApiService();
    }

    private void processApiData(JSONObject object) {
        finalArr = new JSONArray();
        try {
            if (!object.getString("Response").equals("null")) {
                JSONObject responseObject = object.getJSONObject("Response")
                        .getJSONObject("DATAS").getJSONObject("RESPONSE_WEB");
                packetType = responseObject.getString("PACKET_TYPE");
                if (responseObject.getString("PACKET_TYPE").equals(WRITE_PACKET)) {
                    switch (responseObject.getString("JSON_SUB_ID")) {
                        case "02":
                            processUserList(responseObject.getJSONArray("DATA").
                                    getJSONObject(0).getJSONArray("REQ"));
                            break;
                        case "03":
                            processSiteDetails(responseObject.getJSONArray("DATA").
                                    getJSONObject(0).getJSONArray("REQ"));
                            break;
                        case "04":
                            writeInputConfiguration(responseObject.getJSONArray("DATA").
                                    getJSONObject(0).getJSONArray("REQ"));
                            break;

                        case "05":
                            writeOutputConfiguration(responseObject.getJSONArray("DATA").
                                    getJSONObject(0).getJSONArray("REQ"));
                            break;

                    }
                } else if (responseObject.getString("PACKET_TYPE").equals(READ_PACKET)) {
                    switch (responseObject.getString("JSON_SUB_ID")) {
                        case "02":
                            processUserList(responseObject.getJSONArray("DATA").
                                    getJSONObject(0).getJSONArray("REQ"));
                            break;
                        case "03":
                            processSiteDetails(responseObject.getJSONArray("DATA").
                                    getJSONObject(0).getJSONArray("REQ"));
                            break;
                        case "04":
                            readInputConfiguration();
                            break;
                        case "05":
                            readOutputConfiguration(responseObject.getJSONArray("DATA").
                                    getJSONObject(0).getJSONArray("REQ"));
                            break;
                        case "10":

                            break;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void processDefaultConfiguration(JSONObject data) {
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
    }

    public void readInputConfiguration() {

        responseTabId = "04";
        InputConfigurationDao inputDao = DB.inputConfigurationDao();
        for (int i = 0; i < inputDao.getConfigSensor().size(); i++) {
            dataObj = new JSONObject();
            try {
                dataObj.put("INPUTNO", inputDao.getConfigSensor().get(i).hardwareNo);
                dataObj.put("REQ", inputDao.getWritePacket(inputDao.getConfigSensor().get(i).hardwareNo));
                dataObj.put("NAME_LABEL", "");
                dataObj.put("LEFT_LABEL", "");
                dataObj.put("RIGHT_LABEL", "");
                dataObj.put("SEQUENCE_NO", "");
                dataObj.put("UNIT", "");
                dataObj.put("TYPE", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finalArr.put(dataObj);
        }

    }

    private void writeInputConfiguration(JSONArray jsonArray) {
        try {
            ApplicationClass.getInstance().sendPacket(new DataReceiveCallback() {
                @Override
                public void OnDataReceive(String data) {
                    responseTabId = "04";
                    responseTabData = "ACK";
                    try {
                        String[] splitData = jsonArray.getJSONObject(0).getString("REQ").
                                split("\\*")[1].split(RES_SPILT_CHAR);
                        int hardWareNo = Integer.parseInt(jsonArray.getJSONObject(0).getString("INPUTNO"));
                        String inputLabel = jsonArray.getJSONObject(0).getString("NAME_LABEL");
                        String lowAlarm = jsonArray.getJSONObject(0).getString("LEFT_LABEL");
                        String highAlarm = jsonArray.getJSONObject(0).getString("RIGHT_LABEL");
                        int seqNo = Integer.parseInt(jsonArray.getJSONObject(0).getString("SEQUENCE_NO"));
                        String unit = jsonArray.getJSONObject(0).getString("UNIT");
                        int type = Integer.parseInt(jsonArray.getJSONObject(0).getString("TYPE"));

                        String[] typeArr = {"0-None", "1-Fluorescence", "2-Turbidity Value", "3-Corrosion rate", "4-Pitting rate"
                                , "5-Fluorescence value(ST588)", "6-Tagged Polymer value"};

                        String sensorType = "SENSOR";
                        String sequenceName = "";
                        int signalType = 0;
                        if (hardWareNo < 4) {
                            sensorType = "SENSOR";
                            sequenceName = "SENSOR";
                        } else if (hardWareNo < 14) {
                            sensorType = "MODBUS";
                            sequenceName = modBusTypeArr[seqNo] + typeArr[type];
                        } else if (hardWareNo < 17) {
                            sensorType = "SENSOR";
                            sequenceName = "Temperature" + seqNo;
                        } else if (hardWareNo < 25) {
                            sensorType = "Analog";
                            sequenceName = seqNo < 6 ? "4-20mA" : "0-10mA";
                        } else if (hardWareNo < 33) {
                            sensorType = "FLOWMETER";
                            sequenceName = "FLOWMETER" + seqNo;
                        } else if (hardWareNo < 41) {
                            sensorType = "DIGITAL";
                            signalType = 1;
                            sequenceName = "DIGITAL" + seqNo;
                        } else {
                            sensorType = "TANK";
                            signalType = 1;
                            sequenceName = "TANK" + seqNo;
                        }

                        InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                                (hardWareNo, inputTypeArr[Integer.parseInt(splitData[5])],
                                        sensorType, signalType, sequenceName, seqNo, inputLabel,
                                        lowAlarm, highAlarm, unit, typeArr[type], 1,
                                        jsonArray.getJSONObject(0).getString("REQ"));
                        List<InputConfigurationEntity> inputentryList = new ArrayList<>();
                        inputentryList.add(entityUpdate);
                        updateInputDB(inputentryList);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, jsonArray.getJSONObject(0).getString("REQ"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateInputDB(List<InputConfigurationEntity> entryList) {
        InputConfigurationDao dao = DB.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void readOutputConfiguration(JSONArray jsonArray) {
        responseTabId = "05";
        OutputConfigurationDao outputDao = DB.outputConfigurationDao();
        try {
            String hardWareNo = jsonArray.getJSONObject(0).getString("INPUTNO");
            dataObj = new JSONObject();
            dataObj.put("INPUTNO", hardWareNo);
            dataObj.put("REQ", outputDao.getWritePacket(Integer.parseInt(hardWareNo)));
            dataObj.put("NAME_LABEL", "");
            dataObj.put("LEFT_LABEL", "");
            dataObj.put("RIGHT_LABEL", "");
            dataObj.put("SEQUENCE_NO", "");
            dataObj.put("UNIT", "");
            dataObj.put("TYPE", "");
            finalArr.put(dataObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeOutputConfiguration(JSONArray jsonArray) {
        try {
            ApplicationClass.getInstance().sendPacket(new DataReceiveCallback() {
                @Override
                public void OnDataReceive(String data) {
                    responseTabId = "05";
                    responseTabData = "ACK";
                }
            }, jsonArray.getJSONObject(0).getString("REQ"));
        } catch (JSONException e) {
            e.printStackTrace();
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
                        tempUser.getString("LOGINSTATUS")));
            }
            UserManagementDao dao = DB.userManagementDao();
            dao.insert(userList.toArray(new UsermanagementEntity[0]));
            responseTabId = "02";
            responseTabData = "{*1$0$14$01000$02010$03040$04050$05090$06091$07092$08093$09094$10095" +
                    "$11096$12097$13098$14099$15021$16022$17023$18061$19062$20063$21064" +
                    "$22065$23066$24067$25068$26031$27032$28033$29034$30035$31036$32037" +
                    "$33038$34081$35082$36083$37084$38085$39086$40087$41088$42071$43072" +
                    "$44073$45074$46075$47076$48077$49078*}";
            dataObj = new JSONObject();
            dataObj.put("INPUTNO", "");
            dataObj.put("REQ", responseTabData);
            dataObj.put("NAME_LABEL", "");
            dataObj.put("LEFT_LABEL", "");
            dataObj.put("RIGHT_LABEL", "");
            dataObj.put("SEQUENCE_NO", "");
            dataObj.put("UNIT", "");
            dataObj.put("TYPE", "");
            finalArr.put(dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processSiteDetails(JSONArray siteDetailsObject) {
        try {

            SharedPref.write(pref_SITEID, siteDetailsObject.getJSONObject(0).getString("SITE_ID"));
            SharedPref.write(pref_SITENAME, siteDetailsObject.getJSONObject(0).getString("SITE_NAME"));
            SharedPref.write(pref_SITELOCATION, siteDetailsObject.getJSONObject(0).getString("SITE_LOCATION"));
            SharedPref.write(pref_CONTROLLERPASSWORD, siteDetailsObject.getJSONObject(0).getString("CONTROLLER_PASSWORD"));
            SharedPref.write(pref_CONTROLLERISACTIVE, (siteDetailsObject.getJSONObject(0).getString("ISACTIVE").equals("1")));
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
            finalArr.put(dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
            responseTabData = "NACK";
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
            dataObject.put("USER_ID", "");
            dataObject.put("LOGIN_STATUS", "");

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
            responseObj.put("PACKET_TYPE", packetType);
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

}