package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.util.Log;
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
import com.ionexchange.databinding.FragmentInputsensorTempBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorTemp_config extends Fragment implements DataReceiveCallback {
    private static final String TAG = "FragmentInputSensorTemp";
    FragmentInputsensorTempBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    String sensorSequence = "1"; // todo sequenceNumber

    public FragmentInputSensorTemp_config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorTemp_config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_temp, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
        mBinding.temSeqNumberAtxtIsc.setText(sensorSequence);
        switch (userType) {
            case 1:
                mBinding.tempInputLabelTilIsc.setEnabled(false);
                mBinding.tempDefaultTemperatureValueTilIsc.setEnabled(false);
                mBinding.tempTempDeciIsc.setEnabled(false);
                mBinding.tempTempValueTBtn.setEnabled(false);

                mBinding.tempLowAlarmEdtIsc.setEnabled(false);
                mBinding.tempLowAlarmDeciIsc.setEnabled(false);
                mBinding.tempLowAlarmTBtn.setEnabled(false);

                mBinding.tempHighAlarmEdtIsc.setEnabled(false);
                mBinding.tempHighAlarmDeciIsc.setEnabled(false);
                mBinding.tempHighAlarmTBtn.setEnabled(false);

                mBinding.tempCalibRequiredAlarmEdtIsc.setEnabled(false);
                mBinding.tempResetCalibAtxtIsc.setEnabled(false);

                mBinding.tempSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.tempSmoothingFactorTilIsc.setVisibility(View.GONE);

                mBinding.tempRow5Isc.setVisibility(View.GONE);
                break;

            case 2:
                mBinding.tempDefaultTemperatureValueTilIsc.setEnabled(false);
                mBinding.tempTempDeciIsc.setEnabled(false);
                mBinding.tempTempValueTBtn.setEnabled(false);

                mBinding.tempSmoothingFactorTilIsc.setEnabled(false);

                mBinding.tempSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.tempDeleteLayoutIsc.setVisibility(View.GONE);
                break;
        }

        initAdapter();
        mBinding.tempSaveFabIsc.setOnClickListener(this::save);
        mBinding.tempDeleteFabSic.setOnClickListener(this::delete);
        mBinding.tempBackArrowIsc.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
    }

    private void delete(View view) {
        sendData(2);
    }

    private void save(View view) {
        if (validation()) {
            sendData(sensorStatus);
        }
    }

    void sendData(int sensorStatus) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.tempInputNumberEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.temSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.tempSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.tempInputLabelEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.tempTempValueTBtn, mBinding.tempTemperatureEdtIsc, 3, mBinding.tempTempDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.tempSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.tempLowAlarmTBtn, mBinding.tempLowAlarmEdtIsc, 3, mBinding.tempLowAlarmDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.tempHighAlarmTBtn, mBinding.tempHighAlarmEdtIsc, 3, mBinding.tempHighAlarmDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.tempCalibRequiredAlarmEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.tempResetCalibAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus);
    }

    private void initAdapter() {
        mBinding.temSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.tempSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.tempResetCalibAtxtIsc.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.temSeqNumberAtxtIsc.setAdapter(getAdapter(sensorSequenceNumber, getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, inputNumber));
        } else {
            mBinding.tempInputNumberEdtIsc.setText(inputNumber);
            mBinding.temSensorTypeAtxtIsc.setText(sensorName);
            mBinding.tempDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.tempSaveTxtIsc.setText("ADD");
        }
    }

    @Override
    public void OnDataReceive(String data) {
        mActivity.dismissProgress();
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), "TimeOut");
        }
        if (data != null) {
            handleResponse(data.split("\\*")[1].split("\\$"));
        }
    }

    private void handleResponse(String[] splitData) {
        if (splitData[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.tempInputNumberEdtIsc.setText(splitData[3]);
                    mBinding.temSensorTypeAtxtIsc.setText(mBinding.temSensorTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                    mBinding.temSeqNumberAtxtIsc.setText(mBinding.temSeqNumberAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[5])).toString());
                    mBinding.tempSensorActivationAtxtIsc.setText(mBinding.tempSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[6])).toString());
                    mBinding.tempInputLabelEdtIsc.setText(splitData[7]);
                    mBinding.tempTempValueTBtn.setChecked((splitData[8].substring(0, 1)).equals("+"));
                    mBinding.tempTemperatureEdtIsc.setText(splitData[8].substring(1, 4));
                    mBinding.tempTempDeciIsc.setText(splitData[8].substring(5, 6));

                    mBinding.tempSmoothingFactorEdtIsc.setText(splitData[9]);

                    mBinding.tempLowAlarmTBtn.setChecked(splitData[10].substring(0, 1).equals("+"));
                    mBinding.tempLowAlarmEdtIsc.setText(splitData[10].substring(1, 4));
                    mBinding.tempLowAlarmDeciIsc.setText(splitData[10].substring(5, 6));

                    mBinding.tempHighAlarmTBtn.setChecked(splitData[11].substring(0, 1).equals("+"));
                    mBinding.tempHighAlarmEdtIsc.setText(splitData[11].substring(1, 4));
                    mBinding.tempHighAlarmDeciIsc.setText(splitData[11].substring(5, 6));

                    mBinding.tempCalibRequiredAlarmEdtIsc.setText(splitData[12]);
                    mBinding.tempResetCalibAtxtIsc.setText(mBinding.tempResetCalibAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[13])).toString());

                    initAdapter();
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
            } else if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[3].equals(RES_SUCCESS)) {
                    temperatureEntity(Integer.parseInt(splitData[2]));
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }
        } else {
            Log.e(TAG, "handleResponse: WRONG_PACK");
        }
    }

    boolean validation() {

        if (isFieldEmpty(mBinding.tempInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Input Label Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tempTemperatureEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Default Temperature Value Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tempSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Sensor Activation Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tempLowAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm Low Factor Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tempHighAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm High Factor Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tempCalibRequiredAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Calibration Alarm Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tempSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tempResetCalibAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Reset Calibration Cannot be Empty");
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.tempCalibRequiredAlarmEdtIsc)) > 365) {
            mBinding.tempCalibRequiredAlarmEdtIsc.setError("Should be less than 365");
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.tempSmoothingFactorEdtIsc)) > 100) {
            mBinding.tempSmoothingFactorEdtIsc.setError("Should be less than 100");
            return false;
        } else if (Float.parseFloat(getDecimalValue(mBinding.tempLowAlarmTBtn, mBinding.tempLowAlarmEdtIsc, 3, mBinding.tempLowAlarmDeciIsc, 2)) >=
                Float.parseFloat(getDecimalValue(mBinding.tempHighAlarmTBtn, mBinding.tempHighAlarmEdtIsc, 3, mBinding.tempHighAlarmDeciIsc, 2))) {
            mAppClass.showSnackBar(getContext(), "Alarm High Should be Greater Than Alarm Low");
            return false;
        }
        return true;
    }


    public void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void temperatureEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.tempInputNumberEdtIsc)), "0", 0, "0", "0", "0", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                mBinding.tempBackArrowIsc.performClick();
                break;
            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.tempInputNumberEdtIsc)),
                                mBinding.temSensorTypeAtxtIsc.getText().toString(),
                                0, getStringValue(0, mBinding.tempInputLabelEdtIsc),
                                getDecimalValue(mBinding.tempLowAlarmTBtn, mBinding.tempLowAlarmEdtIsc, 3, mBinding.tempLowAlarmDeciIsc, 2),
                                getDecimalValue(mBinding.tempHighAlarmTBtn, mBinding.tempHighAlarmEdtIsc, 3, mBinding.tempHighAlarmDeciIsc, 2), 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }

    }
}
