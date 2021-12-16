package com.ionexchange.Singleton;

import static com.ionexchange.Others.ApplicationClass.DB;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.lastKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.triggerWebService;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
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
import com.ionexchange.Database.Dao.UserManagementDao;
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
    int responseTabId = 0;
    String responseTabData = "";

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
            ApplicationClass.httpRequest(mContext, "Mobile/MobileData?Data=", getKeepAliveObject(), Request.Method.POST, new VolleyCallback() {
                @Override
                public void OnSuccess(JSONObject object) {
                    processApiData(object);
                }

                @Override
                public void OnFailure(VolleyError error) {

                }
            });
        } catch (Exception e) {
          //  e.printStackTrace();
        }
        responseTabId = 0;
        responseTabData = "";
        startApiService();
    }

    private void processApiData(JSONObject object) {
        try {
            if (!object.getString("Response").equals("null")) {
                JSONObject responseObject = object.getJSONObject("Response").getJSONObject("DATAS").getJSONObject("RESPONSE_WEB");
                switch (responseObject.getString("JSON_SUB_ID")) {
                    case "02":
                        processUserList(responseObject.getJSONArray("DATA"));
                        break;

                    case "03":
                        processSiteDetails(responseObject.getJSONArray("DATA").getJSONObject(0));
                        break;

                    case "10":
                        processInputConfiguration(responseObject.getJSONArray("DATA").getJSONObject(0));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processInputConfiguration(JSONObject dataObj) {
        // INPUT_NO
        responseTabId = 10;
        // responseTabData = "{*1$04$0$01$00$1$0$PHSensor$0$1$33$10$400$1300$10$0$1*}";
        try {
            responseTabData = getInputSensorConfig(dataObj.getString("INPUT_NO"));
        } catch (JSONException e) {
        //    e.printStackTrace();
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
                userList.add(new UsermanagementEntity(tempUser.getString("USERID"), tempUser.getString("USERNAME"),
                        tempUser.getString("ROLE"), tempUser.getString("PASSWORD"), tempUser.getString("CONTACT"), tempUser.getString("LOGINSTATUS")));
            }
            UserManagementDao dao = DB.userManagementDao();
            dao.insert(userList.toArray(new UsermanagementEntity[0]));
            responseTabId = 02;
            responseTabData = "{*1200$007$01$01PH$02ORP$03Contacting conductivity$04Toroidal conductivity$05ST500(Fluorescence)$06ST500(Turbidity)$07CR-300 CS(Corrosion)$08CR-300 CS(Pitting)$09CR-300 CU(Corrosion)$10CR-300 CU(Pitting)$11ST-590$12ST-588(Fluorescence)$13ST-588(Tagged Polymer)$14ST-500 RO$15Temperature$16Temperature$17Temperature$18Analog4-20mA1$19Analog4-20mA2$20Analog4-20mA3$21Analog4-20mA4$22Analog4-20mA5$23Analog4-20mA6$24Analog0-10V1$25Analog0-10V2$26AnalogFlow Meters2$27AnalogFlow Meters3$28AnalogFlow Meters4$29AnalogFlow Meters5$30DigitalFlow Meters1$31DigitalFlow Meters2$32DigitalFlow Meters3$33DigitalFlow Meters4$34DigitalSensor1$35DigitalSensor2$36DigitalSensor3$37DigitalSensor4$38DigitalSensor5$39DigitalSensor6$40DigitalSensor7$41DigitalSensor8$42TankSensor1$43TankSensor2$44TankSensor3$45TankSensor4$46TankSensor5$47TankSensor6$48TankSensor7$49TankSensor8$50VirtualSensors1$51VirtualSensors2$52VirtualSensors3$53VirtualSensors4$54VirtualSensors5$55VirtualSensors6$56VirtualSensors7$57VirtualSensors8*}";
        } catch (JSONException e) {
         //   e.printStackTrace();
        }
    }

    private void processSiteDetails(JSONObject siteDetailsObject) {
        try {
            SharedPref.write(pref_SITEID, siteDetailsObject.getString("SITE_ID"));
            SharedPref.write(pref_SITENAME, siteDetailsObject.getString("SITE_NAME"));
            SharedPref.write(pref_SITELOCATION, siteDetailsObject.getString("SITE_LOCATION"));
            SharedPref.write(pref_CONTROLLERPASSWORD, siteDetailsObject.getString("CONTROLLER_PASSWORD"));
            SharedPref.write(pref_CONTROLLERISACTIVE, (siteDetailsObject.getString("ISACTIVE").equals("1")));
            responseTabId = 03;
            responseTabData = "ACK";
        } catch (JSONException e) {
           // e.printStackTrace();
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

            dataObject.put("MSG_ID", "001");
            dataObject.put("MSG_SUBID", "02");
            dataObject.put("MSG_FIELD", lastKeepAliveData);
            dataObject.put("LABLE", "");
            dataObject.put("RESPONSE_WEB", "");
            dataObject.put("RESPONSE_TAB", getResponceTab() == null ? "NACK" : getResponceTab());
            dataObject.put("USER_ID", "");
            dataObject.put("LOGIN_STATUS", "");

            finalObject.put("DATAS", dataObject);
        } catch (JSONException e) {
          //  e.printStackTrace();
        }
        return finalObject;
    }

    private JSONObject getResponceTab() {
        try {
            JSONObject responseObj = new JSONObject();
            responseObj.put("JSON_SUB_ID", responseTabId);

            JSONObject dataObj = new JSONObject();
            dataObj.put("REQ", responseTabData);

            JSONArray finalArr = new JSONArray();
            finalArr.put(dataObj);

            responseObj.put("DATA", finalArr);

            return responseObj;
        } catch (JSONException e) {
         //   e.printStackTrace();
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
