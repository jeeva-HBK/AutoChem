package com.ionexchange.Singleton;

import static com.ionexchange.Others.ApplicationClass.DB;
import static com.ionexchange.Others.ApplicationClass.alertKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.inputKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.outputKeepAliveData;
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
    String responseTabId = "00";
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
            ApplicationClass.httpRequest(mContext, "Mobile/MobileData?Data=", getKeepAliveObject(),
                    Request.Method.POST, new VolleyCallback() {
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
        responseTabId = "00";
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

                    case "12":
                        processDefaultConfiguration(responseObject.getJSONArray("DATA").getJSONObject(0));
                        break;
                }
            }
        } catch (Exception e) {
            //   e.printStackTrace();
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
            responseTabId = "12";
            responseTabData = "{*1$0$14$01000$02010$03040$04050$05090$06091$07092$08093$09094$10095" +
                    "$11096$12097$13098$14099$15021$16022$17023$18061$19062$20063$21064" +
                    "$22065$23066$24067$25068$26031$27032$28033$29034$30035$31036$32037" +
                    "$33038$34081$35082$36083$37084$38085$39086$40087$41088$42071$43072" +
                    "$44073$45074$46075$47076$48077$49078*}";
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
            responseTabId = "03";
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
