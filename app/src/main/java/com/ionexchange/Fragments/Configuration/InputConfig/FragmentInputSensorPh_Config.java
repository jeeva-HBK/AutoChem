package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsensorPhBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.bufferArr;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.getDecimalValue;
import static com.ionexchange.Others.ApplicationClass.getPositionFromAtxt;
import static com.ionexchange.Others.ApplicationClass.getStringValue;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.isFieldEmpty;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.sensorSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.tempLinkedArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorPh_Config extends Fragment implements DataReceiveCallback {
    FragmentInputsensorPhBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    WaterTreatmentDb db;
    InputConfigurationDao dao;

    private static final String TAG = "FragmentInputSensor";

    String inputNumber, sensorName;
    int sensorStatus;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_ph, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (BaseActivity) getActivity();
        mAppClass = (ApplicationClass) getActivity().getApplication();
        init();
    }

    void init() {
        // initializing DB
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();

        inputNumber = getArguments().getString("inputNumber");
        sensorName = getArguments().getString("sensorName");
        sensorStatus = getArguments().getInt("sensorStatus");

        // initializing ATXT adapters
        mBinding.phSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.phSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.phBufferTypeAtxtIsc.setAdapter(getAdapter(bufferArr, getContext()));
        mBinding.phTempLinkedAtxtIsc.setAdapter(getAdapter(tempLinkedArr, getContext()));
        mBinding.phResetCalibrationAtxtIsc.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.phSeqNumberAtxtIsc.setAdapter(getAdapter(sensorSequenceNumber, getContext()));

        // change UI for userRole
        changeUI(userType);

        // initializing clickable's listeners
        mBinding.phSaveFabIsc.setOnClickListener(this::save);
        mBinding.phDeleteFabIsc.setOnClickListener(this::delete);

        // Back btn
        mBinding.phBackArrowIsc.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });

    }

    void changeUI(int userRole) {
        switch (userRole) {
            case 1: // Basic
                mBinding.phTemperatureSensorLinkedTilIsc.setVisibility(View.GONE);
                mBinding.phRow6Isc.setVisibility(View.GONE);
                mBinding.phInputNumberTilIsc.setEnabled(false);
                mBinding.phInputLabelTilIsc.setEnabled(false);
                mBinding.phSensorTypeTilIsc.setEnabled(false);
                mBinding.phBufferTypeTilIsc.setEnabled(false);
                mBinding.phCalibrationRequiredAlarmTilIsc.setEnabled(false);
                mBinding.phSmoothingFactorTilIsc.setVisibility(View.GONE);
                mBinding.phSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.phLowAlarmTilIsc.setEnabled(false);
                mBinding.phAlarmLowDeciIsc.setEnabled(false);
                mBinding.phHighAlarmTilIsc.setEnabled(false);
                mBinding.phHighAlarmDeciIsc.setEnabled(false);
                mBinding.phDefaultTemperatureValueTilIsc.setEnabled(false);
                mBinding.phTempDeciIsc.setEnabled(false);
                mBinding.phTempValueTBtn.setEnabled(false);
                mBinding.phResetCalibrationTilIsc.setEnabled(false);
                break;

            case 2: // Intermediate
                mBinding.phInputNumberTilIsc.setEnabled(false);
                mBinding.phSensorTypeTilIsc.setEnabled(false);
                mBinding.phTemperatureSensorLinkedTilIsc.setEnabled(false);
                mBinding.phSmoothingFactorTilIsc.setEnabled(false);
                mBinding.phSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.phDeleteLayoutIsc.setVisibility(View.GONE);
                break;
        }
    }

    void delete(View view) {
        sendData(2);
    }

    void save(View view) {
        if (validField()) {
            sendData(sensorStatus);
        }
    }

    void sendData(int sensorStatus) {
        mActivity.showProgress();
        // Write -> {* 1234$ 0$ 0$ 04$ 01$ 00$ 1$ 0$ pHSensor$ 0$ 1$ +220.00$ 090$ 07.25$ 12.50$ 300$ 1$ 1 *}
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.phInputNumberEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.phSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                "1" + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.phSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.phInputLabelEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.phBufferTypeAtxtIsc), bufferArr) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.phTempLinkedAtxtIsc), tempLinkedArr) + SPILT_CHAR +
                getDecimalValue(mBinding.phTempValueTBtn, mBinding.phTemperatureEdtIsc, 3, mBinding.phTempDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.phSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.phAlarmLowEdtIsc, 2, mBinding.phAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.phAlarmhighEdtIsc, 2, mBinding.phHighAlarmDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.phCalibrationRequiredEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.phResetCalibrationAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus
        );
    }

    boolean validField() {

        if (isFieldEmpty(mBinding.phInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.input_name_validation));
            return false;
        } else if (isFieldEmpty(mBinding.phSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.sensor_activation_validation));
            return false;
        } else if (isFieldEmpty(mBinding.phTemperatureEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Temperature value cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.phAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_low_validation));
            return false;
        } else if (isFieldEmpty(mBinding.phAlarmhighEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_high_validation));
            return false;
        } else if (isFieldEmpty(mBinding.phSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.smoothing_factor_validation));
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.phSmoothingFactorEdtIsc)) > 100) {
            mBinding.phSmoothingFactorEdtIsc.setError(getString(R.string.smoothing_factor_vali));
            return false;
        } else if (isFieldEmpty(mBinding.phCalibrationRequiredEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.calibration_alarm_vali));
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.phCalibrationRequiredEdtIsc)) > 365) {
            mBinding.phCalibrationRequiredEdtIsc.setError(getString(R.string.calibration_alarm_validation));
            return false;
        } else if (isFieldEmpty(mBinding.phResetCalibrationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.reset_calibration_validation));
            return false;
        } else if (isFieldEmpty(mBinding.phBufferTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.buffertype_validation));
            return false;
        } else if (isFieldEmpty(mBinding.phTempLinkedAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.temp_sensor_linked_valid));
            return false;
        } else if (Float.parseFloat(getDecimalValue(mBinding.phAlarmLowEdtIsc, 4, mBinding.phAlarmLowDeciIsc, 2)) >=
                Float.parseFloat(getDecimalValue(mBinding.phAlarmhighEdtIsc, 4, mBinding.phHighAlarmDeciIsc, 2))) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_limit_validation));
            return false;
        }
        return true;
    }

    @Override
    public void OnDataReceive(String data) {
        mActivity.dismissProgress();
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.timeout));
        }
        if (data != null) {
            handleResponce(data.split("\\*")[1].split(RES_SPILT_CHAR));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, inputNumber));
        } else {
            mBinding.phInputNumberEdtIsc.setText(inputNumber);
            mBinding.phSensorTypeAtxtIsc.setText(sensorName);
            mBinding.phDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.phSaveTxtIsc.setText("ADD");
        }
    }

    void handleResponce(String[] splitData) {
        mActivity.dismissProgress();
        if (splitData[1].equals("04")) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.phInputNumberEdtIsc.setText(splitData[3]);

                    mBinding.phSensorTypeAtxtIsc.setText(mBinding.phSensorTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                    mBinding.phSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr, getContext()));

                    // splitData[5] - sequenceNumber

                    mBinding.phSensorActivationAtxtIsc.setText(mBinding.phSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[6])).toString());
                    mBinding.phSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr, getContext()));

                    mBinding.phInputLabelEdtIsc.setText(splitData[7]);

                    mBinding.phBufferTypeAtxtIsc.setText(mBinding.phBufferTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[8])).toString());
                    mBinding.phBufferTypeAtxtIsc.setAdapter(getAdapter(bufferArr, getContext()));

                    mBinding.phTempLinkedAtxtIsc.setText(mBinding.phTempLinkedAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());
                    mBinding.phTempLinkedAtxtIsc.setAdapter(getAdapter(tempLinkedArr, getContext()));

                    mBinding.phTempValueTBtn.setChecked((splitData[10].substring(0, 1)).equals("+"));

                    mBinding.phTemperatureEdtIsc.setText(splitData[10].substring(1, 4));
                    mBinding.phTempDeciIsc.setText(splitData[10].substring(5, 7));

                    mBinding.phSmoothingFactorEdtIsc.setText(splitData[11]);

                    mBinding.phAlarmLowEdtIsc.setText(splitData[12].substring(0, 2));
                    mBinding.phAlarmLowDeciIsc.setText(splitData[12].substring(3, 5));

                    mBinding.phAlarmhighEdtIsc.setText(splitData[13].substring(0, 2));
                    mBinding.phHighAlarmDeciIsc.setText(splitData[13].substring(3, 5));

                    mBinding.phCalibrationRequiredEdtIsc.setText(splitData[14]);

                    mBinding.phResetCalibrationAtxtIsc.setText(mBinding.phResetCalibrationAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[15])).toString());
                    mBinding.phResetCalibrationAtxtIsc.setAdapter(getAdapter(resetCalibrationArr, getContext()));

                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.readFailed));
                }

            } else if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[3].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                    pHEntity(Integer.parseInt(splitData[2]));

                } else if (splitData[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), getString(R.string.wrongPack));
        }
    }

    void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    void pHEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.phInputNumberEdtIsc)),
                                "N/A","SENSOR" , "N/A",
                                1, "N/A", "N/A", "N/A", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                mBinding.phBackArrowIsc.performClick();
                break;
            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.phInputNumberEdtIsc)),
                                mBinding.phSensorTypeAtxtIsc.getText().toString(),"SENSOR",mBinding.phSensorTypeAtxtIsc.getText().toString(),
                                1, getStringValue(0, mBinding.phInputLabelEdtIsc),
                                getStringValue(2, mBinding.phAlarmLowEdtIsc) + "." + getStringValue(2, mBinding.phAlarmLowDeciIsc),
                                getStringValue(2, mBinding.phAlarmhighEdtIsc) + "." + getStringValue(2, mBinding.phHighAlarmDeciIsc), 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }
    }
}