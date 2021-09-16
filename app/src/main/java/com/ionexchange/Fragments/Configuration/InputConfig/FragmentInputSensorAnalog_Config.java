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
import com.ionexchange.databinding.FragmentInputsensorAnalogBinding;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.analogTypeArr;
import static com.ionexchange.Others.ApplicationClass.analogUnitArr;
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

public class FragmentInputSensorAnalog_Config extends Fragment implements DataReceiveCallback {

    FragmentInputsensorAnalogBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    Integer LowAlarm;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    String sensorSequence; // todo SequenceNumber

    public FragmentInputSensorAnalog_Config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorAnalog_Config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_inputsensor_analog, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
        initAdapter();
        switch (userType) {
            case 1:
                mBinding.analogInputNumber.setEnabled(false);
                mBinding.analogInputLabel.setEnabled(false);
                mBinding.analogSensorType.setEnabled(false);
                mBinding.analogAnalogType.setEnabled(false);
                mBinding.analogUnit.setEnabled(false);
                mBinding.analogMinValue.setEnabled(false);
                mBinding.analogMaxValue.setEnabled(false);
                mBinding.analogSmoothingFactor.setVisibility(View.GONE);
                mBinding.analogAlarmLow.setEnabled(false);
                mBinding.analogAlarmHigh.setEnabled(false);
                mBinding.analogCalibAlarmRequired.setEnabled(false);
                mBinding.analogResetCalibration.setEnabled(false);
                mBinding.analogSensorActivation.setVisibility(View.GONE);
                mBinding.analogRow6Isc.setVisibility(View.GONE);
                break;

            case 2:
                mBinding.analogInputNumber.setEnabled(false);
                mBinding.analogSensorType.setEnabled(false);
                mBinding.analogAnalogType.setEnabled(false);
                mBinding.analogUnit.setEnabled(false);
                mBinding.analogSmoothingFactor.setEnabled(false);
                mBinding.analogSensorActivation.setVisibility(View.GONE);
                mBinding.analogDeleteLayoutIsc.setVisibility(View.GONE);
                break;
        }

        mBinding.analogSaveFabIsc.setOnClickListener(this::save);
        mBinding.analogDeleteFabIsc.setOnClickListener(this::delete);

        mBinding.backArrowIsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
            }
        });
    }


    private void delete(View view) {
        sendData(2);
    }


    private void save(View view) {
        if (validField()) {
            sendData(sensorStatus);
        }
    }

    void sendData(int sensorStatus) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + "0" + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.analogInputNumberTie) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.analogSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                "1" + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.analogTypeTie), analogTypeArr) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.analogSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.analogInputLabelTie) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.analogUnitMeasurementTie), analogUnitArr) + SPILT_CHAR +
                getStringValue(2, mBinding.analogMinValueTie) + "." + getStringValue(2, mBinding.analogMinValueIsc) + SPILT_CHAR +
                getStringValue(2, mBinding.analogMaxValueTie) + "." + getStringValue(2, mBinding.analogMaxValueIsc) + SPILT_CHAR +
                getStringValue(3, mBinding.analogSmoothingFactorTie) + SPILT_CHAR +
                getStringValue(2, mBinding.analogAlarmLowTie) + "." + getStringValue(2, mBinding.lowAlarmMinValueIsc) + SPILT_CHAR +
                getStringValue(2, mBinding.analogHighLowTie) + "." + getStringValue(2, mBinding.highAlarmMinValueIsc) + SPILT_CHAR +
                getStringValue(3, mBinding.analogCalibrationRequiredAlarmTie) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.analogResetCalibrationTie), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus);
    }

    private void initAdapter() {
        mBinding.analogSensorTypeTie.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.analogSensorActivationTie.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.analogTypeTie.setAdapter(getAdapter(analogTypeArr, getContext()));
        mBinding.analogUnitMeasurementTie.setAdapter(getAdapter(analogUnitArr, getContext()));
        mBinding.analogResetCalibrationTie.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.analogSequenceNumberTie.setAdapter(getAdapter(sensorSequenceNumber, getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + inputNumber);
        } else {
            mBinding.analogInputNumberTie.setText(inputNumber);
            mBinding.analogSensorTypeTie.setText(sensorName);
            mBinding.analogDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.analogSaveTxtIsc.setText("ADD");
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

    private void handleResponse(String[] data) {
        if (data[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (data[0].equals(READ_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {
                    mBinding.analogInputNumberTie.setText(data[3]);
                    mBinding.analogSensorTypeTie.setText(mBinding.analogSensorTypeTie.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    mBinding.analogSequenceNumberTie.setText(mBinding.analogSequenceNumberTie.getAdapter().getItem(Integer.parseInt(data[5])).toString());
                    mBinding.analogTypeTie.setText(mBinding.analogTypeTie.getAdapter().getItem(Integer.parseInt(data[6])).toString());
                    mBinding.analogSensorActivationTie.setText(mBinding.analogSensorActivationTie.getAdapter().getItem(Integer.parseInt(data[7])).toString());
                    mBinding.analogInputLabelTie.setText(data[8]);
                    mBinding.analogUnitMeasurementTie.setText(mBinding.analogUnitMeasurementTie.getAdapter().getItem(Integer.parseInt(data[9])).toString());
                    mBinding.analogMinValueTie.setText(data[10].substring(0, 2));
                    mBinding.analogMinValueIsc.setText(data[10].substring(3, 5));
                    mBinding.analogMaxValueTie.setText(data[11].substring(0, 2));
                    mBinding.analogMaxValueIsc.setText(data[11].substring(3, 5));
                    mBinding.analogSmoothingFactorTie.setText(data[12]);
                    mBinding.analogAlarmLowTie.setText(data[13].substring(0, 2));
                    mBinding.lowAlarmMinValueIsc.setText(data[13].substring(3, 5));
                    mBinding.analogHighLowTie.setText(data[14].substring(0, 2));
                    mBinding.highAlarmMinValueIsc.setText(data[14].substring(3, 5));
                    mBinding.analogCalibrationRequiredAlarmTie.setText(data[15]);
                    mBinding.analogResetCalibrationTie.setText(mBinding.analogResetCalibrationTie.getAdapter().getItem(Integer.parseInt(data[16])).toString());
                    initAdapter();
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
            } else if (data[0].equals(WRITE_PACKET)) {
                if (data[3].equals(RES_SUCCESS)) {
                    analogEntity(Integer.parseInt(data[2]));
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (data[3].equals(RES_FAILED)) {

                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), "Received Wrong Pack !");
        }
    }


    private boolean validField() {
        if (isFieldEmpty(mBinding.analogInputLabelTie)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.analogTypeTie)) {
            mAppClass.showSnackBar(getContext(), "Analog Type cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.analogUnitMeasurementTie)) {
            mAppClass.showSnackBar(getContext(), "Unit cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.analogMinValueTie)) {
            mAppClass.showSnackBar(getContext(), "Min Value cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.analogMaxValueTie)) {
            mAppClass.showSnackBar(getContext(), "Max Value cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.analogAlarmLowTie)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.analogHighLowTie)) {
            mAppClass.showSnackBar(getContext(), "Alarm High cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.analogCalibrationRequiredAlarmTie)) {
            mAppClass.showSnackBar(getContext(), "Calibration cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.analogSmoothingFactorTie)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.analogResetCalibrationTie)) {
            mAppClass.showSnackBar(getContext(), "Reset Calibration cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.analogSensorActivationTie)) {
            mAppClass.showSnackBar(getContext(), "Sensor Activation cannot be Empty");
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.analogCalibrationRequiredAlarmTie)) > 365) {
            mBinding.analogCalibrationRequiredAlarmTie.setError("Should be less than 365");
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.analogSmoothingFactorTie)) > 100) {
            mBinding.analogSmoothingFactorTie.setError("Should be less than 100");
            return false;
        } else if (Float.parseFloat(getDecimalValue(mBinding.analogAlarmLowTie, 2, mBinding.lowAlarmMinValueIsc, 2)) >=
                Float.parseFloat(getDecimalValue(mBinding.analogHighLowTie, 2, mBinding.highAlarmMinValueIsc, 2))) {
            mAppClass.showSnackBar(getContext(), "Alarm High Should be Greater Than Alarm Low");
            return false;
        } else if (Float.parseFloat(getDecimalValue(mBinding.analogMinValueTie, 2, mBinding.analogMinValueIsc, 2)) >=
                Float.parseFloat(getDecimalValue(mBinding.analogMaxValueTie, 2, mBinding.analogMaxValueIsc, 2))) {
            mAppClass.showSnackBar(getContext(), "Max Value Should be Greater Than Min Value");
            return false;
        }
        return true;
    }

    public void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void analogEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.analogInputNumberTie)), "0",
                                0, "0", "0", "0", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                mBinding.backArrowIsc.performClick();
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.analogInputNumberTie)),
                                mBinding.analogSensorTypeTie.getText().toString(),
                                0, getStringValue(0, mBinding.analogInputLabelTie),
                                getDecimalValue(mBinding.analogAlarmLowTie, 2, mBinding.lowAlarmMinValueIsc, 2),
                                getDecimalValue(mBinding.analogHighLowTie, 2, mBinding.highAlarmMinValueIsc, 2), 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }
    }
}
