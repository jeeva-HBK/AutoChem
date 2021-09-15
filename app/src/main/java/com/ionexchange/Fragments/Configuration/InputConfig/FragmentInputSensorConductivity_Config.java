package com.ionexchange.Fragments.Configuration.InputConfig;

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
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsensorCondBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.TemperatureCompensationType;
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
import static com.ionexchange.Others.ApplicationClass.unitArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorConductivity_Config extends Fragment implements DataReceiveCallback {
    private static final String TAG = "FragmentInputSensorCond";
    FragmentInputsensorCondBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    int sensorStatus;
    String inputNumber;
    String sensorName;
    WaterTreatmentDb db;
    InputConfigurationDao dao;

    public FragmentInputSensorConductivity_Config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorConductivity_Config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_cond, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
        userManagement();
        initAdapters();
        mBinding.conSaveFabIsc.setOnClickListener(this::save);
        mBinding.conDeleteFabIsc.setOnClickListener(this::delete);

        mBinding.setCompType("0");
        mBinding.conCompensationAtxtIsc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                mBinding.setCompType(String.valueOf(pos));
            }
        });

        mBinding.conBackArrowIsc.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
    }


    private void delete(View view) {
        if (getPositionFromAtxt(1, getStringValue(mBinding.conCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
            sendDataLinearTemperature(sensorStatus);
        } else {
            sendStandardNaClTemperature(sensorStatus);
        }
    }

    private void save(View view) {
        if (validation()) {
            if (getPositionFromAtxt(1, getStringValue(mBinding.conCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
                sendDataLinearTemperature(sensorStatus);
            } else {
                sendStandardNaClTemperature(sensorStatus);
            }
        }
    }

    void sendDataLinearTemperature(int sensorStatus) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.conInputNumberEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.conSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                "1" + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.conSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.conInputLabelEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.conTempLinkedAtxtIsc), tempLinkedArr) + SPILT_CHAR +
                getDecimalValue(mBinding.conDefaultTempValueTBtn, mBinding.conDefaultTemperatureEdtIsc, 3, mBinding.conDefaultTempDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.conUnitOfMeasureAxtIsc), unitArr) + SPILT_CHAR +
                getDecimalValue(mBinding.conCellConstantEdtIsc, 2, mBinding.conCellConstantDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.conCompensationAtxtIsc), TemperatureCompensationType) + SPILT_CHAR +
                getDecimalValue(mBinding.conCompFactorEdtIsc, 2, mBinding.conCompFactorDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.conSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.conAlarmLowEdtIsc, 6, mBinding.conAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.conAlarmhighEdtIsc, 6, mBinding.conHighAlarmDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.conCalibRequiredAlarmEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.conResetCalibAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus
        );
    }

    void sendStandardNaClTemperature(int sensorStatus) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.conInputNumberEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.conSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                "1" + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.conSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.conInputLabelEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.conTempLinkedAtxtIsc), tempLinkedArr) + SPILT_CHAR +
                getDecimalValue(mBinding.conDefaultTempValueTBtn, mBinding.conDefaultTemperatureEdtIsc, 3, mBinding.conDefaultTempDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.conUnitOfMeasureAxtIsc), unitArr) + SPILT_CHAR +
                getDecimalValue(mBinding.conCellConstantEdtIsc, 2, mBinding.conCellConstantDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.conCompensationAtxtIsc), TemperatureCompensationType) + SPILT_CHAR +
                getStringValue(3, mBinding.conSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.conAlarmLowEdtIsc, 6, mBinding.conAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.conAlarmhighEdtIsc, 6, mBinding.conHighAlarmDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.conCalibRequiredAlarmEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.conResetCalibAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus
        );
    }

    boolean validation() {
        if (isFieldEmpty(mBinding.conInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Input Label Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.conSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Sensor Activation Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.conSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.conCalibRequiredAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Calibration Required Alarm Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.conResetCalibAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Reset Calibration Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.conAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm Low Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.conAlarmhighEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm High Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.conCellConstantEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Cell Constant Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.conCompensationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Comp Type Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.conCompFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Temperature Compensation Factor Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.conDefaultTemperatureEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Temperature Value Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.conUnitOfMeasureAxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Unit Of Measurement Cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.conTempLinkedAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Temperature Linked Cannot be Empty");
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.conSmoothingFactorEdtIsc)) > 100) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor Should be less than 100");
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.conCalibRequiredAlarmEdtIsc)) > 365) {
            mAppClass.showSnackBar(getContext(), "Calibration required Alarm Should be less than 365");
            return false;
        } else if (Float.parseFloat(getDecimalValue(mBinding.conAlarmLowEdtIsc, 6, mBinding.conAlarmLowDeciIsc, 2)) >=
                Float.parseFloat(getDecimalValue(mBinding.conAlarmhighEdtIsc, 6, mBinding.conHighAlarmDeciIsc, 2))) {
            mAppClass.showSnackBar(getContext(), "Alarm High Should be Greater Than Alarm Low");
            return false;
        } else if (Float.parseFloat(getDecimalValue(mBinding.conDefaultTempValueTBtn, mBinding.conAlarmLowEdtIsc, 3, mBinding.conAlarmLowDeciIsc, 2)) > -20) {
            mAppClass.showSnackBar(getContext(), "Default Temperature Value should be -20°C to 500°C");
            return false;
        }


        return true;
    }

    private void initAdapters() {
        mBinding.conSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.conSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.conTempLinkedAtxtIsc.setAdapter(getAdapter(tempLinkedArr, getContext()));
        mBinding.conUnitOfMeasureAxtIsc.setAdapter(getAdapter(unitArr, getContext()));
        mBinding.conResetCalibAtxtIsc.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.conCompensationAtxtIsc.setAdapter(getAdapter(TemperatureCompensationType, getContext()));
        mBinding.conSeqNumberAtxtIsc.setAdapter(getAdapter(sensorSequenceNumber, getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, inputNumber));
        } else {
            mBinding.conInputNumberEdtIsc.setText(inputNumber);
            mBinding.conSensorTypeAtxtIsc.setText(sensorName);
            mBinding.conDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.conSaveTxtIsc.setText("ADD");
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

    private void handleResponse(String[] spiltData) {
        if (spiltData[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (spiltData[0].equals(READ_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mBinding.conInputNumberEdtIsc.setText(spiltData[3]);
                    mBinding.conSensorTypeAtxtIsc.setText(mBinding.conSensorTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[4])).toString());
                    mBinding.conSeqNumberAtxtIsc.setText(mBinding.conSeqNumberAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[5])).toString());
                    mBinding.conSensorActivationAtxtIsc.setText(mBinding.conSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[6])).toString());
                    mBinding.conInputLabelEdtIsc.setText(spiltData[7]);
                    mBinding.conTempLinkedAtxtIsc.setText(mBinding.conTempLinkedAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[8])).toString());

                    mBinding.conDefaultTempValueTBtn.setChecked((spiltData[9].substring(0, 1).equals("+")));
                    mBinding.conDefaultTemperatureEdtIsc.setText(spiltData[9].substring(1, 4));
                    mBinding.conDefaultTempDeciIsc.setText(spiltData[9].substring(5, 7));

                    mBinding.conUnitOfMeasureAxtIsc.setText(mBinding.conUnitOfMeasureAxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[10])).toString());
                    mBinding.conCellConstantEdtIsc.setText(spiltData[11].substring(0, 2));
                    mBinding.conCellConstantDeciIsc.setText(spiltData[11].substring(3, 5));
                    mBinding.conCompensationAtxtIsc.setText(mBinding.conCompensationAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[12])).toString());
                    mBinding.conCompFactorEdtIsc.setEnabled(spiltData[12].equals("0"));
                    mBinding.conCompFactorDeciIsc.setEnabled(spiltData[12].equals("0"));
                    if (spiltData[12].equals("0")) {
                        mBinding.conCompFactorEdtIsc.setText(spiltData[13].substring(0, 2));
                        mBinding.conCompFactorDeciIsc.setText(spiltData[13].substring(3, 5));
                        mBinding.conSmoothingFactorEdtIsc.setText(spiltData[14]);

                        mBinding.conAlarmLowEdtIsc.setText(spiltData[15].substring(0, 6));
                        mBinding.conAlarmLowDeciIsc.setText(spiltData[15].substring(7, 9));

                        mBinding.conAlarmhighEdtIsc.setText(spiltData[16].subSequence(0, 6));
                        mBinding.conHighAlarmDeciIsc.setText(spiltData[16].subSequence(7, 9));

                        mBinding.conCalibRequiredAlarmEdtIsc.setText(spiltData[17]);
                        mBinding.conResetCalibAtxtIsc.setText(mBinding.conResetCalibAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[18])).toString());
                    } else {
                        mBinding.conSmoothingFactorEdtIsc.setText(spiltData[13]);

                        mBinding.conAlarmLowEdtIsc.setText(spiltData[14].substring(0, 6));
                        mBinding.conAlarmLowDeciIsc.setText(spiltData[14].substring(7, 9));

                        mBinding.conAlarmhighEdtIsc.setText(spiltData[15].subSequence(0, 6));
                        mBinding.conHighAlarmDeciIsc.setText(spiltData[15].subSequence(7, 9));

                        mBinding.conCalibRequiredAlarmEdtIsc.setText(spiltData[16]);
                        mBinding.conResetCalibAtxtIsc.setText(mBinding.conResetCalibAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[17])).toString());
                    }

                    initAdapters();
                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
                //   {*0$ 04$ 1$ 0*}
            } else if (spiltData[0].equals(WRITE_PACKET)) {
                if (spiltData[3].equals(RES_SUCCESS)) {
                    conductivityEntity(Integer.parseInt(spiltData[2]));
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (spiltData[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }
        } else {
            Log.e(TAG, "handleResponse: Wrong Packet");
        }
    }


    public void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void conductivityEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.conInputNumberEdtIsc)), "0",
                                0, "0", "0", "0", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                mBinding.conBackArrowIsc.performClick();
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.conInputNumberEdtIsc)),
                                mBinding.conSensorTypeAtxtIsc.getText().toString(),
                                0, getStringValue(0, mBinding.conInputLabelEdtIsc),
                                getDecimalValue(mBinding.conAlarmLowEdtIsc, 6, mBinding.conAlarmLowDeciIsc, 2),
                                getDecimalValue(mBinding.conAlarmhighEdtIsc, 6, mBinding.conHighAlarmDeciIsc, 2), 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }
    }

    void userManagement() {
        switch (userType) {
            case 1: // Basic
                mBinding.conInputLabelTilSic.setEnabled(false);
                mBinding.conTempLinkedTilIsc.setEnabled(false);
                mBinding.conDefaultTemperatureValueTilIsc.setEnabled(false);
                mBinding.conUnitOfMeasureTilIsc.setEnabled(false);
                mBinding.conCellConstantTilIsc.setEnabled(false);
                mBinding.conLowAlarmRootIsc.setEnabled(false);
                mBinding.conHighAlarmTilIsc.setEnabled(false);
                mBinding.conCalibRequiredAlarmTilIsc.setEnabled(false);
                mBinding.conResetCalibTilIsc.setEnabled(false);
                mBinding.conSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.conRow7Isc.setVisibility(View.GONE);

                break;

            case 2:
                //View
                mBinding.conCellConstantEdtIsc.setEnabled(false);
                mBinding.conCompFactorRootIsc.setEnabled(false);
                mBinding.conCompFactorRootIsc.setEnabled(false);
                mBinding.conSmoothingFactorTilIsc.setEnabled(false);
                mBinding.conSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.conDeleteLayoutIsc.setVisibility(View.GONE);

                break;
        }
    }
}
