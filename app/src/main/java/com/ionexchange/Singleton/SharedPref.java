package com.ionexchange.Singleton;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    private static SharedPreferences mSharedPref;

    // General Info
    public static final String pref_MACADDRESS = "MAC_ADDRESS";
    public static final String pref_DEVICEID = "DEVICE_ID";
    public static final String pref_LOGGEDIN ="LOGGED_IN";
    public static final String pref_USERLOGINREQUIRED = "USER_LOGIN_REQUIRED";
    public static final String pref_USERLOGINNAME = "USER_LOGIN_NAME";
    public static final String pref_USERLOGINROLE = "USER_LOGIN_ROLE";
    public static final String pref_USERLOGINID = "USER_LOGIN_ID";

    // Site Info
    public static final String pref_SITEID = "SITE_ID";
    public static final String pref_SITENAME = "SITE_NAME";
    public static final String pref_SITELOCATION = "SITE_LOCATION";
    public static final String pref_CONTROLLERPASSWORD = "CONTROLLER_PASSWORD";
    public static final String pref_CONTROLLERISACTIVE = "IS_ACTIVE";
    public static final String pref_TEMPERATUREUNIT = "TEMPERATURE_UNIT";
    public static final String pref_ALARMDELAY = "ALARM_DELAY";

    private SharedPref() { }

    public static void init(Context context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    // For String
    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    // For boolean
    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    // For int
    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).commit();
    }
}