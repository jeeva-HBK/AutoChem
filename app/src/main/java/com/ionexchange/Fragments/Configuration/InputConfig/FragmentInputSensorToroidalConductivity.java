package com.ionexchange.Fragments.Configuration.InputConfig;

import static com.ionexchange.Activity.BaseActivity.dismissProgress;
import static com.ionexchange.Activity.BaseActivity.showProgress;
import static com.ionexchange.Others.ApplicationClass.TemperatureCompensationType;
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
import static com.ionexchange.Others.ApplicationClass.tempLinkedArr;
import static com.ionexchange.Others.ApplicationClass.toStringValue;
import static com.ionexchange.Others.ApplicationClass.unitArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.ENDPACKET;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.STARTPACKET;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINID;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
import com.ionexchange.databinding.FragmentInputsensorToraidalconductivityBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentInputSensorToroidalConductivity extends Fragment implements DataReceiveCallback {
    private static final String TAG = "FragmentInputSensorCond";
    FragmentInputsensorToraidalconductivityBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    int sensorStatus;
    String inputNumber;
    String sensorName;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    String writePacket;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_toraidalconductivity, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
        inputNumber = getArguments().getString("inputNumber");
        sensorName = getArguments().getString("sensorName");
        sensorStatus = getArguments().getInt("sensorStatus");
        userManagement();
        initAdapters();
        mBinding.candSaveFabIsc.setOnClickListener(this::save);
        mBinding.candDeleteFabIsc.setOnClickListener(this::delete);
        mBinding.candBackArrowIsc.setOnClickListener(v -> {
            mAppClass.popStackBack(getActivity());
        });
        mBinding.candCompensationAtxtIsc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0) {
                    mBinding.candCompFactorEdtIsc.setEnabled(true);
                    mBinding.candCompFactorDeciIsc.setEnabled(true);
                } else {
                    mBinding.candCompFactorEdtIsc.setEnabled(false);
                    mBinding.candCompFactorDeciIsc.setEnabled(false);
                }
            }
        });
    }

    private void delete(View view) {
        if (validation()) {
            showProgress();
            if (getPositionFromAtxt(1, getStringValue(mBinding.candCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
                sendDataLinearTemperature(2);
            } else {
                sendStandardNaClTemperature(2);
            }
        }
    }

    private void save(View view) {
        if (validation()) {
            showProgress();
            if (getPositionFromAtxt(1, getStringValue(mBinding.candCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
                sendDataLinearTemperature(1);
            } else {
                sendStandardNaClTemperature(1);
            }
        }
    }

    void sendDataLinearTemperature(int sensorStatus) {
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.candInputNumberEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.candSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                "1" + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.candSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.candInputLabelEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.candTempLinkedAtxtIsc), tempLinkedArr) + SPILT_CHAR +
                getDecimalValue(mBinding.candTempValueTBtn, mBinding.candTemperatureEdtIsc, 3, mBinding.candTempDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.candUnitOfMeasureAtxtIsc), unitArr) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.candCompensationAtxtIsc), TemperatureCompensationType) + SPILT_CHAR +
                getDecimalValue(mBinding.candCompFactorEdtIsc, 2, mBinding.candCompFactorDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.candSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.candLowAlarmEdtIsc, 7, mBinding.candAlarmlowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.candHighAlarmEdtIsc, 7, mBinding.candHighAlarmDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.candCalibRequiredAlarmEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.candResetCalibAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus;
        mAppClass.sendPacket(this, writePacket);
    }

    void sendStandardNaClTemperature(int sensorStatus) {

        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.candInputNumberEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.candSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                "1" + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.candSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.candInputLabelEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.candTempLinkedAtxtIsc), tempLinkedArr) + SPILT_CHAR +
                getDecimalValue(mBinding.candTempValueTBtn, mBinding.candTemperatureEdtIsc, 3, mBinding.candTempDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.candUnitOfMeasureAtxtIsc), unitArr) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.candCompensationAtxtIsc), TemperatureCompensationType) + SPILT_CHAR +
                getStringValue(3, mBinding.candSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.candLowAlarmEdtIsc, 7, mBinding.candAlarmlowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.candHighAlarmEdtIsc, 7, mBinding.candHighAlarmDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.candCalibRequiredAlarmEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.candResetCalibAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus;
        mAppClass.sendPacket(this, writePacket);
    }

    private void initAdapters() {
        mBinding.candSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.candSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.candTempLinkedAtxtIsc.setAdapter(getAdapter(tempLinkedArr, getContext()));
        mBinding.candUnitOfMeasureAtxtIsc.setAdapter(getAdapter(unitArr, getContext()));
        mBinding.candResetCalibAtxtIsc.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.candCompensationAtxtIsc.setAdapter(getAdapter(TemperatureCompensationType, getContext()));
        mBinding.candSeqNumberAtxtIsc.setAdapter(getAdapter(sensorSequenceNumber, getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + "04");
        } else {
            mBinding.candInputNumberEdtIsc.setText(inputNumber);
            mBinding.candSensorTypeAtxtIsc.setText(sensorName);
            mBinding.candDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.candSaveIsc.setText("ADD");
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

    private void handleResponse(String[] spiltData) {

        if (spiltData[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (spiltData[0].equals(READ_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    try {
                    mBinding.candInputNumberEdtIsc.setText(spiltData[3]);
                    mBinding.candSensorTypeAtxtIsc.setText(mBinding.candSensorTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[4])).toString());
                    mBinding.candSeqNumberAtxtIsc.setText(mBinding.candSeqNumberAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[5])).toString());
                    mBinding.candSensorActivationAtxtIsc.setText(mBinding.candSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[6])).toString());
                    mBinding.candInputLabelEdtIsc.setText(spiltData[7]);
                    mBinding.candTempLinkedAtxtIsc.setText(mBinding.candTempLinkedAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[8])).toString());

                    mBinding.candTempValueTBtn.setChecked(spiltData[9].substring(0, 1).equals("+"));
                    mBinding.candTemperatureEdtIsc.setText(spiltData[9].substring(1, 4));
                    mBinding.candTempDeciIsc.setText(spiltData[9].substring(5, 7));

                    mBinding.candUnitOfMeasureAtxtIsc.setText(mBinding.candUnitOfMeasureAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[10])).toString());
                    mBinding.candCompensationAtxtIsc.setText(mBinding.candCompensationAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[11])).toString());
                    mBinding.candCompFactorEdtIsc.setEnabled(spiltData[11].equals("0"));
                    mBinding.candCompFactorDeciIsc.setEnabled(spiltData[11].equals("0"));
                    switch (userType) {
                        case 1:
                        case 2:
                            mBinding.candCompFactorEdtIsc.setEnabled(false);
                            mBinding.candCompFactorDeciIsc.setEnabled(false);
                            break;
                    }
                    if (spiltData[11].equals("0")) {
                        mBinding.candCompFactorEdtIsc.setText(spiltData[12].substring(0,2));
                        mBinding.candCompFactorDeciIsc.setText(spiltData[12].substring(3,5));

                        mBinding.candSmoothingFactorEdtIsc.setText(spiltData[13]);

                        mBinding.candLowAlarmEdtIsc.setText(spiltData[14].substring(0,7));
                        mBinding.candAlarmlowDeciIsc.setText(spiltData[14].substring(8,10));

                        mBinding.candHighAlarmEdtIsc.setText(spiltData[15].substring(0,7));
                        mBinding.candHighAlarmDeciIsc.setText(spiltData[15].substring(8,10));

                        mBinding.candCalibRequiredAlarmEdtIsc.setText(spiltData[16]);
                        mBinding.candResetCalibAtxtIsc.setText(mBinding.candResetCalibAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[17])).toString());
                    } else {
                        mBinding.candSmoothingFactorEdtIsc.setText(spiltData[12]);

                        mBinding.candLowAlarmEdtIsc.setText(spiltData[13].substring(0,7));
                        mBinding.candAlarmlowDeciIsc.setText(spiltData[13].substring(8,10));

                        mBinding.candHighAlarmEdtIsc.setText(spiltData[14].substring(0,7));
                        mBinding.candHighAlarmDeciIsc.setText(spiltData[14].substring(8,10));

                        mBinding.candCalibRequiredAlarmEdtIsc.setText(spiltData[15]);
                        mBinding.candResetCalibAtxtIsc.setText(mBinding.candResetCalibAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[16])).toString());
                    }
                    initAdapters();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }

            } else if (spiltData[0].equals(WRITE_PACKET)) {
                if (spiltData[3].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                    tankLevelEntity(Integer.valueOf(spiltData[2]));
                } else if (spiltData[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            }

        } else {
            Log.e(TAG, getString(R.string.wrongPack));
        }
    }

    boolean validation() {
        if (isFieldEmpty(mBinding.candInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.input_name_validation));
            return false;
        } else if (isFieldEmpty(mBinding.candSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.sensor_activation_validation));
            return false;
        } else if (isFieldEmpty(mBinding.candTempLinkedAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.temp_sensor_linked_valid));
            return false;
        } else if (isFieldEmpty(mBinding.candSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.smoothing_factor_validation));
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.candSmoothingFactorEdtIsc)) > 90) {
            mBinding.candSmoothingFactorEdtIsc.setError(getString(R.string.smoothing_factor_vali));
            return false;
        } else if (isFieldEmpty(mBinding.candResetCalibAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.reset_calibration_validation));
            return false;
        }  else if (isFieldEmpty(mBinding.candLowAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_low_validation));
            return false;
        } else if (isFieldEmpty(mBinding.candHighAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_high_validation));
            return false;
        } else if (isFieldEmpty(mBinding.candCalibRequiredAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.calibration_alarm_vali));
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.candCalibRequiredAlarmEdtIsc)) > 365) {
            mBinding.candCalibRequiredAlarmEdtIsc.setError(getString(R.string.calibration_alarm_validation));
            return false;
        } else if (isFieldEmpty(mBinding.candCompensationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.compensation_type_validation));
            return false;
        } else if (isFieldEmpty(mBinding.candUnitOfMeasureAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.unit_validation));
            return false;
        } else if (Float.parseFloat(getDecimalValue(mBinding.candLowAlarmEdtIsc, 4, mBinding.candAlarmlowDeciIsc, 2)) >=
                Float.parseFloat(getDecimalValue(mBinding.candHighAlarmEdtIsc, 4, mBinding.candHighAlarmDeciIsc, 2))) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_limit_validation));
            return false;
        } else if (isFieldEmpty(mBinding.candTemperatureEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Default Temperature value Cannot be Empty");
            return false;
        }
        if(mBinding.candTempValueTBtn.isChecked() && Integer.parseInt(getStringValue(3, mBinding.candTemperatureEdtIsc)) > 500) {
            mAppClass.showSnackBar(getContext(), getString(R.string.temp_limit_validation));
            return false;
        } else if(!mBinding.candTempValueTBtn.isChecked() && Integer.parseInt(getStringValue(3, mBinding.candTemperatureEdtIsc)) > 20) {
            mAppClass.showSnackBar(getContext(), getString(R.string.temp_limit_validation));
            return false;
        }
        if (getPositionFromAtxt(0, toStringValue(mBinding.candCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
            if (isFieldEmpty(mBinding.candCompFactorEdtIsc)) {
                mAppClass.showSnackBar(getContext(), getString(R.string.compensation_factor_validation));
                return false;
            } else if(Integer.parseInt(mBinding.candCompFactorEdtIsc.getText().toString()) > 20){
                mAppClass.showSnackBar(getContext(), getString(R.string.compensation_factor_maxvalidation));
                return false;
            }
        }

        return true;
    }

    public void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void tankLevelEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.candInputNumberEdtIsc)),
                                "N/A", "SENSOR", 0, "N/A",
                                1, "N/A", "N/A", "N/A", "N/A", "N/A", 0,STARTPACKET + writePacket + ENDPACKET);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                new EventLogDemo(inputNumber,"Toroidal Conductivity","Input Setting Deleted",
                        SharedPref.read(pref_USERLOGINID, ""),getContext());
                ApiService.getInstance(getContext()).processApiData(READ_PACKET, "04", "Input Setting Deleted - " +
                        SharedPref.read(pref_USERLOGINID, ""));
                mainConfigurationDao.updateAddSensorValue(0, Integer.parseInt(inputNumber));
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.candInputNumberEdtIsc)),
                                mBinding.candSensorTypeAtxtIsc.getText().toString(), "SENSOR", 0,
                                mBinding.candSensorTypeAtxtIsc.getText().toString(),
                                1, getStringValue(0, mBinding.candInputLabelEdtIsc),
                                getDecimalValue(mBinding.candLowAlarmEdtIsc, 7, mBinding.candAlarmlowDeciIsc, 2),
                                getDecimalValue(mBinding.candHighAlarmEdtIsc, 7, mBinding.candHighAlarmDeciIsc, 2), getStringValue(mBinding.candUnitOfMeasureAtxtIsc), "N/A", 1,
                                STARTPACKET + writePacket + ENDPACKET);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                new EventLogDemo(inputNumber,"Toroidal Conductivity","Input Setting Changed",
                        SharedPref.read(pref_USERLOGINID, ""),getContext());
                ApiService.getInstance(getContext()).processApiData(READ_PACKET, "04", "Input Setting Changed - " +
                        SharedPref.read(pref_USERLOGINID, ""));
                mainConfigurationDao.updateAddSensorValue(1, Integer.parseInt(inputNumber));
                break;
        }
        mBinding.candBackArrowIsc.performClick();
    }

    void userManagement() {
        switch (userType) {
            case 1:
                mBinding.candInputLabelEdtIsc.setEnabled(false);
                mBinding.candTempLinkedAtxtIsc.setEnabled(false);
                mBinding.candDefaultTemperatureValueTilIsc.setEnabled(false);
                mBinding.candTempDeciIsc.setEnabled(false);
                mBinding.candUnitOfMeasureTilIsc.setEnabled(false);
                mBinding.phLowAlarmTilIsc.setEnabled(false);
                mBinding.candAlarmlowDeciIsc.setEnabled(false);
                mBinding.candTempDeciIsc.setEnabled(false);
                mBinding.candHighalarmTilIsc.setEnabled(false);
                mBinding.candSmoothingFactorTilIsc.setVisibility(View.GONE);
                mBinding.candHighAlarmDeciIsc.setEnabled(false);
                mBinding.candCalibRequiredAlarmTilIsc.setEnabled(false);
                mBinding.candResetCalibTilIsc.setEnabled(false);
                mBinding.candSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.candRow5Isc.setVisibility(View.GONE);
                mBinding.conRow7.setVisibility(View.GONE);
                break;

            case 2:
                mBinding.candInputNumberTilIsc.setEnabled(false);
                mBinding.candSensorTypeTilIsc.setEnabled(false);
                mBinding.candCompensationTilIsc.setEnabled(false);
                mBinding.candCompFactorTilIsc.setEnabled(false);
                mBinding.candCompFactorDeciIsc.setEnabled(false);
                mBinding.candSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.candSmoothingFactorTilIsc.setEnabled(false);
                mBinding.candDeleteLayoutIsc.setVisibility(View.GONE);
                break;

        }
    }
}
