package com.ionexchange.Fragments.Configuration.GeneralConfig;

import static com.ionexchange.Activity.BaseActivity.dismissProgress;
import static com.ionexchange.Activity.BaseActivity.showProgress;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_GENERAL;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;
import static com.ionexchange.Singleton.SharedPref.pref_CONTROLLERISACTIVE;
import static com.ionexchange.Singleton.SharedPref.pref_SITEID;
import static com.ionexchange.Singleton.SharedPref.pref_SITELOCATION;
import static com.ionexchange.Singleton.SharedPref.pref_SITENAME;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINID;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.Others.EventLogDemo;
import com.ionexchange.R;
import com.ionexchange.Singleton.SharedPref;
import com.ionexchange.databinding.FragmentCommonsettingsBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

// created by Silambu
public class FragmentSiteSettings_Config extends Fragment implements DataReceiveCallback {
    FragmentCommonsettingsBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    private static final String TAG = "FragmentCommonSettings";


    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_commonsettings, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        mBinding.saveLayoutCommonSettings.setOnClickListener(this::onCLick);
        mBinding.saveFabCommonSettings.setOnClickListener(this::onCLick);
        mBinding.sitePasswordCommonSettingsEDT.setText(Settings.System.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID));

        mBinding.refresh.setOnClickListener(view1 -> {
            readData();
        });

        mBinding.sync.setOnClickListener(view1 -> {
            if (!mBinding.alarmDelayEdtIsc.getText().toString().equals("")) {
                // should check 59_Min
                showProgress();
                mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                        CONN_TYPE + SPILT_CHAR +
                        WRITE_PACKET + SPILT_CHAR +
                        PCK_GENERAL + SPILT_CHAR +
                        toString(0, mBinding.siteIdCommonSettingsEDT) + SPILT_CHAR +
                        toString(0, mBinding.siteNameCommonSettingsEDT) + SPILT_CHAR +
                        "1234" + SPILT_CHAR +
                        getRadio(mBinding.radioGroup, mBinding.enableSite) + SPILT_CHAR +
                        toString(0, mBinding.siteLocationCommonSettingsEDT) + SPILT_CHAR +
                        toString(2, mBinding.alarmDelayEdtIsc) + toString(2, mBinding.alarmDelayDeciIsc) + SPILT_CHAR +
                        getRadio(mBinding.radioGroup2, mBinding.fahrenheit) + SPILT_CHAR +
                        toString(2, mBinding.duHours) + toString(2, mBinding.duMM) +
                        toString(2, mBinding.duSS) + toString(1, mBinding.duNN) +
                        toString(2, mBinding.duDD) +
                        toString(2, mBinding.dumonth) +
                        toString(2, mBinding.duYYYY));
            }
        });
    }


    private void onCLick(View view) {
        if (validateFields()) {
            showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                    CONN_TYPE + SPILT_CHAR +
                    WRITE_PACKET + SPILT_CHAR +
                    PCK_GENERAL + SPILT_CHAR +
                    toString(0, mBinding.siteIdCommonSettingsEDT) + SPILT_CHAR +
                    toString(0, mBinding.siteNameCommonSettingsEDT) + SPILT_CHAR +
                    "1234" + SPILT_CHAR +
                    getRadio(mBinding.radioGroup, mBinding.enableSite) + SPILT_CHAR +
                    toString(0, mBinding.siteLocationCommonSettingsEDT) + SPILT_CHAR +
                    toString(2, mBinding.alarmDelayEdtIsc) + toString(2, mBinding.alarmDelayDeciIsc) + SPILT_CHAR +
                    getRadio(mBinding.radioGroup2, mBinding.fahrenheit) + SPILT_CHAR +
                    toString(2, mBinding.Hours) + toString(2, mBinding.MM) +
                    toString(2, mBinding.SS) + toString(1, mBinding.NN) +
                    toString(2, mBinding.DD) +
                    toString(2, mBinding.month) +
                    toString(2, mBinding.YYYY));
        }
    }

    private String getRadio(RadioGroup radioGroup, RadioButton zeroValue) {
        if (radioGroup.getCheckedRadioButtonId() == zeroValue.getId()) {
            return "1";
        }
        return "0";
    }

    public String toString(int digit, EditText editText) {
        return mAppClass.formDigits(digit, editText.getText().toString());
    }

    private boolean validateFields() {
        if (isEmpty(mBinding.siteIdCommonSettingsEDT)) {
            mAppClass.showSnackBar(getContext(), "Site Id Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.siteNameCommonSettingsEDT)) {
            mAppClass.showSnackBar(getContext(), "Site Name  Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.siteLocationCommonSettingsEDT)) {
            mAppClass.showSnackBar(getContext(), "Site Location  Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sitePasswordCommonSettingsEDT)) {
            mAppClass.showSnackBar(getContext(), "Site Password  Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmDelayEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "AlarmDelay  Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmDelayDeciIsc)){
            mAppClass.showSnackBar(getContext(), "AlarmDelay  Cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.alarmDelayEdtIsc.getText().toString()) > 59) {
            mAppClass.showSnackBar(getContext(), "AlarmDelay should be less than 59:59");
            return false;
        } else if (Integer.parseInt(mBinding.alarmDelayDeciIsc.getText().toString()) > 59) {
            mAppClass.showSnackBar(getContext(), "AlarmDelay should be less than 59:59");
            return false;
        } else if (isEmpty(mBinding.Hours)) {
            mAppClass.showSnackBar(getContext(), "Hours  Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.MM)) {
            mAppClass.showSnackBar(getContext(), "Minutes  Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.SS)) {
            mAppClass.showSnackBar(getContext(), "Seconds  Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.NN)) {
            mAppClass.showSnackBar(getContext(), "Day Of Week  Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.DD)) {
            mAppClass.showSnackBar(getContext(), "Date Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.month)) {
            mAppClass.showSnackBar(getContext(), "Month  Cannot be Empty");
            return false;
        } else if (mBinding.sitePasswordCommonSettingsEDT.getText().length() < 4) {
            mAppClass.showSnackBar(getContext(), "Invalid Site Password ");
            return false;
        } else if (isEmpty(mBinding.YYYY)) {
            mAppClass.showSnackBar(getContext(), "Year  Cannot be Empty");
            return false;
        } else if (mBinding.YYYY.getText().length() < 2) {
            mAppClass.showSnackBar(getContext(), "Invalid Year");
            return false;
        }
        return true;
    }

    private Boolean isEmpty(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        readData();
        setSiteDetailsFromPref();
    }

    private void setSiteDetailsFromPref() {
        // Du RTC
        mBinding.duHours.setText(new SimpleDateFormat("HH").format(Calendar.getInstance().getTime()));
        mBinding.duMM.setText(new SimpleDateFormat("mm").format(Calendar.getInstance().getTime()));
        mBinding.duSS.setText(new SimpleDateFormat("ss").format(Calendar.getInstance().getTime()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding.duNN.setText(new SimpleDateFormat("u").format(Calendar.getInstance().getTime()));
        }
        mBinding.duDD.setText(new SimpleDateFormat("dd").format(Calendar.getInstance().getTime()));
        mBinding.dumonth.setText(new SimpleDateFormat("MM").format(Calendar.getInstance().getTime()));
        mBinding.duYYYY.setText(new SimpleDateFormat("yy").format(Calendar.getInstance().getTime()));

        mBinding.siteIdCommonSettingsEDT.setText(SharedPref.read(pref_SITEID, ""));
        mBinding.siteNameCommonSettingsEDT.setText(SharedPref.read(pref_SITENAME, ""));
        mBinding.siteLocationCommonSettingsEDT.setText(SharedPref.read(pref_SITELOCATION, ""));
        // mBinding.sitePasswordCommonSettingsEDT.setText(SharedPref.read(pref_CONTROLLERPASSWORD, ""));

        if (SharedPref.read(pref_CONTROLLERISACTIVE, false)) {
            mBinding.enableSite.setChecked(true);
        } else {
            mBinding.disableSite.setChecked(true);
        }
    }

    /*private void loca() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            status.startResolutionForResult(getActivity(), 199);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }*/

    private void readData() {
        showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_GENERAL);
    }

    @Override
    public void OnDataReceive(String data) {
        dismissProgress();
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        } else if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        } else if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        } else if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), "TimeOut, unable to get device RTC");
        } else if (data != null) {
            handleResponce(data.split("\\*")[1].split("\\$"));
        }
    }

    private void handleResponce(String[] splitData) {
        // Read - Res - {*1# 03# 0# 007# Russia# 12341# 1# chennai# 30# 0# 120435022072020*}
        // Write - Res - {*0#03#0*}
        try {
            if (splitData[1].equals(PCK_GENERAL)) {
                // READ_Response
                if (splitData[0].equals(READ_PACKET)) {
                    if (splitData[2].equals(RES_SUCCESS)) {
                        /* mBinding.siteIdCommonSettingsEDT.setText(splitData[3]);
                        mBinding.siteNameCommonSettingsEDT.setText(splitData[4]);
                        mBinding.sitePasswordCommonSettingsEDT.setText(splitData[5]);*/
                        /* if (splitData[6].equals("0")) {
                            mBinding.disableSite.setChecked(true);
                        } else if (splitData[6].equals("1")) {
                            mBinding.enableSite.setChecked(true);
                        }*/
                        //mBinding.siteLocationCommonSettingsEDT.setText(splitData[7]);
                        mBinding.alarmDelayEdtIsc.setText(splitData[8].substring(0,2));
                        mBinding.alarmDelayDeciIsc.setText(splitData[8].substring(2,4));
                        /*if (splitData[9].equals("0")) {
                            mBinding.celsius.setChecked(true);
                        } else if (splitData[9].equals("1")) {
                            mBinding.fahrenheit.setChecked(true);
                        }*/
                        mBinding.Hours.setText(splitData[10].substring(0, 2));
                        mBinding.MM.setText(splitData[10].substring(2, 4));
                        mBinding.SS.setText(splitData[10].substring(4, 6));
                        mBinding.NN.setText(splitData[10].substring(6, 7));
                        mBinding.DD.setText(splitData[10].substring(7, 9));
                        mBinding.month.setText(splitData[10].substring(9, 11));
                        mBinding.YYYY.setText(splitData[10].substring(11, 13));

                        mBinding.duHours.setText(new SimpleDateFormat("HH").format(Calendar.getInstance().getTime()));
                        mBinding.duMM.setText(new SimpleDateFormat("mm").format(Calendar.getInstance().getTime()));
                        mBinding.duSS.setText(new SimpleDateFormat("ss").format(Calendar.getInstance().getTime()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mBinding.duNN.setText(new SimpleDateFormat("u").format(Calendar.getInstance().getTime()));
                        }
                        mBinding.duDD.setText(new SimpleDateFormat("dd").format(Calendar.getInstance().getTime()));
                        mBinding.dumonth.setText(new SimpleDateFormat("MM").format(Calendar.getInstance().getTime()));
                        mBinding.duYYYY.setText(new SimpleDateFormat("yy").format(Calendar.getInstance().getTime()));

                    } else if (splitData[2].equals(RES_FAILED)) {
                        mAppClass.showSnackBar(getContext(), String.valueOf(R.string.readFailed));
                    }
                } else if (splitData[0].equals(WRITE_PACKET)) {
                    if (splitData[2].equals(RES_SUCCESS)) {
                        mAppClass.showSnackBar(getContext(), "Write Success");
                        new EventLogDemo("0", "Site", "General settings changed by #", SharedPref.read(pref_USERLOGINID, ""), getContext());
                        mAppClass.showSnackBar(getContext(), "Reading data from device");
                        readData();
                    } else if (splitData[2].equals(RES_FAILED)) {
                        mAppClass.showSnackBar(getContext(), "Write Failed");
                    }
                }
            } else {
                mAppClass.showSnackBar(getContext(), String.valueOf(R.string.wrongPack));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
