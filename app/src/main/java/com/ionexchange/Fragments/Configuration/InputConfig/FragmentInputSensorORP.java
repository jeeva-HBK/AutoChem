package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.Others.EventLogDemo;
import com.ionexchange.R;
import com.ionexchange.Singleton.ApiService;
import com.ionexchange.Singleton.SharedPref;
import com.ionexchange.databinding.FragmentInputsensorOrpBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Activity.BaseActivity.dismissProgress;
import static com.ionexchange.Activity.BaseActivity.showProgress;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.getDecimalValue;
import static com.ionexchange.Others.ApplicationClass.getPositionFromAtxt;
import static com.ionexchange.Others.ApplicationClass.getStringValue;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.isFieldEmpty;
import static com.ionexchange.Others.ApplicationClass.mainConfigurationDao;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.sensorSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.ENDPACKET;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.STARTPACKET;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINID;

public class FragmentInputSensorORP extends Fragment implements DataReceiveCallback {
    FragmentInputsensorOrpBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    String writePacket;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_orp, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        inputNumber = getArguments().getString("inputNumber");
        sensorName = getArguments().getString("sensorName");
        sensorStatus = getArguments().getInt("sensorStatus");
        dao = db.inputConfigurationDao();
        initAdapter();
        changeUI();
        mBinding.orpSaveFabIsc.setOnClickListener(this::save);

        mBinding.orpDeleteFabIsc.setOnClickListener(this::delete);
        mBinding.orpBackArrowIsc.setOnClickListener(v -> {
            mAppClass.popStackBack(getActivity());
        });
    }

    private void delete(View view) {
        sendData(2);
    }

    private void save(View view) {
        if (validation()) {
            sendData(1);
        }
    }

    void changeUI() {
        switch (userType) {
            case 1:
                mBinding.orpRow5Isc.setVisibility(View.GONE);
                mBinding.orpSensorActTilIsc.setVisibility(View.GONE);
                mBinding.orpInputNumberTilIsc.setEnabled(false);
                mBinding.orpInputLabelTilIsc.setEnabled(false);
                mBinding.orpSensorTypeTilIsc.setEnabled(false);
                mBinding.orpAlarmLowTilIsc.setEnabled(false);
                mBinding.orpAlarmLowDeciIsc.setEnabled(false);
                mBinding.orpAlarmLowTBtn.setEnabled(false);
                mBinding.orpAlarmHighTilIsc.setEnabled(false);
                mBinding.orpAlarmHighDeciIsc.setEnabled(false);
                mBinding.orpAlarmHighTBtn.setEnabled(false);
                mBinding.orpSmoothingFactorTilIsc.setVisibility(View.GONE);
                mBinding.orpCalibrationAlarmRequiredTilIsc.setEnabled(false);
                mBinding.orpResetCalibrationTilIsc.setEnabled(false);
                break;

            case 2:
                mBinding.orpSmoothingFactorTilIsc.setEnabled(false);
                mBinding.orpSensorActTilIsc.setVisibility(View.GONE);
                mBinding.orpDeleteLayoutIsc.setVisibility(View.GONE);
                mBinding.orpCalibrationAlarmRequiredEdtIsc.setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;
        }
    }

    void sendData(int sensorStatus) {
        showProgress();
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.orpInputNumberEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.orpSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                "1" + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.orpSensorActAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.orpInputLabelEdtIsc) + SPILT_CHAR +
                getStringValue(3, mBinding.orpSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.orpAlarmLowTBtn, mBinding.orpAlarmLowEdtIsc, 4, mBinding.orpAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.orpAlarmHighTBtn, mBinding.orpAlarmHighEdtIsc, 4, mBinding.orpAlarmHighDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.orpCalibrationAlarmRequiredEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.orpResetCalibrationAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus;
        mAppClass.sendPacket(this, writePacket);
    }

    private void initAdapter() {
        mBinding.orpSensorActAtxtIsc.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.orpSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.orpResetCalibrationAtxtIsc.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.orpSeqNumberAtxtIsc.setAdapter(getAdapter(sensorSequenceNumber, getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, inputNumber));
        } else {
            mBinding.orpInputNumberEdtIsc.setText(inputNumber);
            mBinding.orpSensorTypeAtxtIsc.setText(sensorName);
            mBinding.orpDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.orpSavTxtIsc.setText("ADD");
        }
    }

    @Override
    public void OnDataReceive(String data) {
        dismissProgress();
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(),  getString(R.string.connection_failed));
        } else if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(),  getString(R.string.connection_failed));
        } else if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(),  getString(R.string.connection_failed));
        } else if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), "TimeOut");
        } else if (data != null) {
            handleResponse(data.split("\\*")[1].split("\\$"));
        }
    }

    private void handleResponse(String[] data) {
        if (data[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (data[0].equals(READ_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {
                    try {
                    mBinding.orpInputNumberEdtIsc.setText(data[3]);
                    mBinding.orpSensorTypeAtxtIsc.setText(mBinding.orpSensorTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    mBinding.orpSeqNumberAtxtIsc.setText(mBinding.orpSeqNumberAtxtIsc.getAdapter().getItem(Integer.parseInt(data[5])).toString());

                    mBinding.orpSensorActAtxtIsc.setText(mBinding.orpSensorActAtxtIsc.getAdapter().getItem(Integer.parseInt(data[6])).toString());

                    mBinding.orpInputLabelEdtIsc.setText(data[7]);
                    mBinding.orpSmoothingFactorEdtIsc.setText(data[8]);

                    mBinding.orpAlarmLowTBtn.setChecked((data[9].substring(0, 1)).equals("+"));
                    mBinding.orpAlarmLowEdtIsc.setText(data[9].substring(1, 5));
                    mBinding.orpAlarmLowDeciIsc.setText(data[9].substring(6, 8));

                    mBinding.orpAlarmHighTBtn.setChecked(data[10].substring(0, 1).equals("+"));
                    mBinding.orpAlarmHighEdtIsc.setText(data[10].substring(1, 5));
                    mBinding.orpAlarmHighDeciIsc.setText(data[10].substring(6, 8));

                    mBinding.orpCalibrationAlarmRequiredEdtIsc.setText(data[11]);
                    mBinding.orpResetCalibrationAtxtIsc.setText(mBinding.orpResetCalibrationAtxtIsc.getAdapter().getItem(Integer.parseInt(data[12])).toString());
                    initAdapter();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.readFailed));
                }
                // *0$ 04$ 0$ *0}
            } else if (data[0].equals(WRITE_PACKET)) {
                if (data[3].equals(RES_SUCCESS)) {
                    orpEntity(Integer.parseInt(data[2]));

                    mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                } else if (data[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), getString(R.string.wrongPack));
        }

    }

    boolean validation() {
        if (isFieldEmpty(mBinding.orpInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.input_name_validation));
            return false;
        } else if (isFieldEmpty(mBinding.orpSensorActAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.sensor_activation_validation));
            return false;
        } else if (isFieldEmpty(mBinding.orpAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_low_validation));
            return false;
        } else if(Integer.parseInt(getStringValue(4, mBinding.orpAlarmLowEdtIsc)) > 2000) {
            mAppClass.showSnackBar(getContext(), getString(R.string.orp_alarm_low_validation));
            return false;
        } else if (isFieldEmpty(mBinding.orpAlarmHighEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_high_validation));
            return false;
        } else if(Integer.parseInt(getStringValue(4, mBinding.orpAlarmHighEdtIsc)) > 2000) {
            mAppClass.showSnackBar(getContext(), getString(R.string.orp_alarm_high_validation));
            return false;
        } else if (isFieldEmpty(mBinding.orpResetCalibrationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.reset_calibration_validation));
            return false;
        } else if (isFieldEmpty(mBinding.orpCalibrationAlarmRequiredEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.calibration_alarm_vali));
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.orpCalibrationAlarmRequiredEdtIsc)) > 365) {
            mBinding.orpCalibrationAlarmRequiredEdtIsc.setError(getString(R.string.calibration_alarm_validation));
            return false;
        } else if (isFieldEmpty(mBinding.orpSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.smoothing_factor_validation));
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.orpSmoothingFactorEdtIsc)) > 90) {
            mBinding.orpSmoothingFactorEdtIsc.setError(getString(R.string.smoothing_factor_vali));
            return false;
        } else if (Float.parseFloat(getDecimalValue(mBinding.orpAlarmLowTBtn, mBinding.orpAlarmLowEdtIsc, 4, mBinding.orpAlarmLowDeciIsc, 2)) >=
                Float.parseFloat(getDecimalValue(mBinding.orpAlarmHighTBtn, mBinding.orpAlarmHighEdtIsc, 4, mBinding.orpAlarmHighDeciIsc, 2))) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_limit_validation));
            return false;
        }

        return true;
    }

    public void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void orpEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.orpInputNumberEdtIsc)), "N/A",
                                "SENSOR", 0, "N/A",
                                1, "N/A", "N/A", "N/A", "N/A",STARTPACKET + writePacket + ENDPACKET,
                                0,"N/A");
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                new EventLogDemo(inputNumber,"ORP","Input Setting Deleted", SharedPref.read(pref_USERLOGINID, ""),getContext());
                ApiService.getInstance(getContext()).processApiData(READ_PACKET, "04", "Input Setting Deleted - " +
                        SharedPref.read(pref_USERLOGINID, ""));
                mainConfigurationDao.updateAddSensorValue(0, Integer.parseInt(inputNumber));
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.orpInputNumberEdtIsc)),
                                mBinding.orpSensorTypeAtxtIsc.getText().toString(),
                                "SENSOR", 0, mBinding.orpSensorTypeAtxtIsc.getText().toString(),
                                1, getStringValue(0, mBinding.orpInputLabelEdtIsc),
                                getDecimalValue(mBinding.orpAlarmLowTBtn, mBinding.orpAlarmLowEdtIsc, 4, mBinding.orpAlarmLowDeciIsc, 2),
                                getDecimalValue(mBinding.orpAlarmHighTBtn, mBinding.orpAlarmHighEdtIsc, 4, mBinding.orpAlarmHighDeciIsc, 2), "mV","N/A", 1,
                                STARTPACKET + writePacket + ENDPACKET);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                new EventLogDemo(inputNumber,"ORP","Input Setting Changed", SharedPref.read(pref_USERLOGINID, ""),getContext());
                ApiService.getInstance(getContext()).processApiData(READ_PACKET, "04", "Input Setting Changed - " +
                        SharedPref.read(pref_USERLOGINID, ""));
                mainConfigurationDao.updateAddSensorValue(1, Integer.parseInt(inputNumber));
                break;
        }
        mBinding.orpBackArrowIsc.performClick();
    }
}