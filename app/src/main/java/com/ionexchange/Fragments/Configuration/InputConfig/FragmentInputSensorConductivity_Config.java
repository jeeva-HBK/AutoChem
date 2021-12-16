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
import com.ionexchange.Others.EventLogDemo;
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
import static com.ionexchange.Others.ApplicationClass.toStringValue;
import static com.ionexchange.Others.ApplicationClass.unitArr;
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

//created by Silambu
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
        inputNumber = getArguments().getString("inputNumber");
        sensorName = getArguments().getString("sensorName");
        sensorStatus = getArguments().getInt("sensorStatus");
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
        mAppClass.popStackBack(getActivity());
        });
    }


    private void delete(View view) {
        if (getPositionFromAtxt(1, getStringValue(mBinding.conCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
            sendDataLinearTemperature(2);
        } else {
            sendStandardNaClTemperature(2);
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
                getPositionFromAtxt(2, getStringValue(mBinding.conSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR + "1" + SPILT_CHAR +
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
                getPositionFromAtxt(2, getStringValue(mBinding.conSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR + "1" + SPILT_CHAR +
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
            mAppClass.showSnackBar(getContext(), getString(R.string.input_name_validation));
            return false;
        } else if (isFieldEmpty(mBinding.conSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.sensor_activation_validation));
            return false;
        } else if (isFieldEmpty(mBinding.conSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.smoothing_factor_validation));
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.conSmoothingFactorEdtIsc)) > 90) {
            mAppClass.showSnackBar(getContext(), getString(R.string.smoothing_factor_vali));
            return false;
        } else if (isFieldEmpty(mBinding.conCalibRequiredAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.calibration_alarm_vali));
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.conCalibRequiredAlarmEdtIsc)) > 365) {
            mAppClass.showSnackBar(getContext(), getString(R.string.calibration_alarm_validation));
            return false;
        } else if (isFieldEmpty(mBinding.conResetCalibAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.reset_calibration_validation));
            return false;
        } else if (isFieldEmpty(mBinding.conAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_low_validation));
            return false;
        } else if (isFieldEmpty(mBinding.conAlarmhighEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_high_validation));
            return false;
        } else if (isFieldEmpty(mBinding.conCellConstantEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.call_constant_validation));
            return false;
        } else if(Integer.parseInt(mBinding.conCellConstantEdtIsc.getText().toString()) > 10){
            mAppClass.showSnackBar(getContext(), getString(R.string.call_constant_maxvalidation));
            return false;
        } else if (isFieldEmpty(mBinding.conCompensationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.compensation_type_validation));
            return false;
        } else if (isFieldEmpty(mBinding.conDefaultTemperatureEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.temperature_validation));
            return false;
        } else if (isFieldEmpty(mBinding.conUnitOfMeasureAxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.unit_validation));
            return false;
        } else if (isFieldEmpty(mBinding.conTempLinkedAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.temperature_linked_validation));
            return false;
        } else if (Float.parseFloat(getDecimalValue(mBinding.conAlarmLowEdtIsc, 6, mBinding.conAlarmLowDeciIsc, 2)) >=
                Float.parseFloat(getDecimalValue(mBinding.conAlarmhighEdtIsc, 6, mBinding.conHighAlarmDeciIsc, 2))) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_limit_validation));
            return false;
        }
        if(mBinding.conDefaultTempValueTBtn.isChecked() && Integer.parseInt(getStringValue(3, mBinding.conDefaultTemperatureEdtIsc)) > 500) {
            mAppClass.showSnackBar(getContext(), getString(R.string.temp_limit_validation));
            return false;
        } else if(!mBinding.conDefaultTempValueTBtn.isChecked() && Integer.parseInt(getStringValue(3, mBinding.conDefaultTemperatureEdtIsc)) > 20) {
            mAppClass.showSnackBar(getContext(), getString(R.string.temp_limit_validation));
            return false;
        }
        if (getPositionFromAtxt(0, toStringValue(mBinding.conCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
            if (isFieldEmpty(mBinding.conCompFactorEdtIsc)) {
                mAppClass.showSnackBar(getContext(), getString(R.string.compensation_factor_validation));
                return false;
            } else if(Integer.parseInt(mBinding.conCompFactorEdtIsc.getText().toString()) > 20){
                mAppClass.showSnackBar(getContext(), getString(R.string.compensation_factor_maxvalidation));
                return false;
            }
            /*if (Integer.parseInt(mBinding.conCompFactorEdtIsc.getText().toString()) == 20) {
                if(!isFieldEmpty(mBinding.conCompFactorDeciIsc) && Integer.parseInt(mBinding.conCompFactorDeciIsc.getText().toString()) > 0) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.compensation_factor_decimal_validation));
                    return false;
                }
            }*/
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
                    switch (userType) {
                        case 1:
                        case 2:
                            mBinding.conCompFactorEdtIsc.setEnabled(false);
                            mBinding.conCompFactorDeciIsc.setEnabled(false);
                            break;
                    }
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
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
                //   {*0$ 04$ 1$ 0*}
            } else if (spiltData[0].equals(WRITE_PACKET)) {
                if (spiltData[3].equals(RES_SUCCESS)) {
                    conductivityEntity(Integer.parseInt(spiltData[2]));
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                    new EventLogDemo(inputNumber,"Temperature","Input Setting Changed",getContext());
                } else if (spiltData[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            }
        } else {
            Log.e(TAG, getString(R.string.wrongPack));
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
                        (Integer.parseInt(getStringValue(2, mBinding.conInputNumberEdtIsc)), "N/A",
                                "SENSOR", 0, "N/A",
                                1, "N/A", "N/A", "N/A", "N/A", "N/A", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                mBinding.conBackArrowIsc.performClick();
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.conInputNumberEdtIsc)),
                                mBinding.conSensorTypeAtxtIsc.getText().toString(), "SENSOR", 0,
                                mBinding.conSensorTypeAtxtIsc.getText().toString(),
                                1, getStringValue(0, mBinding.conInputLabelEdtIsc),
                                getDecimalValue(mBinding.conAlarmLowEdtIsc, 6, mBinding.conAlarmLowDeciIsc, 2),
                                getDecimalValue(mBinding.conAlarmhighEdtIsc, 6, mBinding.conHighAlarmDeciIsc, 2), getStringValue(mBinding.conUnitOfMeasureAxtIsc), "N/A", 1);
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
                mBinding.conDefaultTempDeciIsc.setEnabled(false);
                mBinding.conDefaultTempValueTBtn.setEnabled(false);
                mBinding.conUnitOfMeasureTilIsc.setEnabled(false);
                mBinding.conCellConstantTilIsc.setEnabled(false);
                mBinding.conCellConstantDeciIsc.setEnabled(false);
                mBinding.conLowAlarmTilIsc.setEnabled(false);
                mBinding.conAlarmLowDeciIsc.setEnabled(false);
                mBinding.conCompensationTilIsc.setVisibility(View.GONE);
                mBinding.conCompFactorRootIsc.setVisibility(View.GONE);
                mBinding.conSmoothingFactorTilIsc.setVisibility(View.GONE);
                mBinding.conHighAlarmTilIsc.setEnabled(false);
                mBinding.conHighAlarmDeciIsc.setEnabled(false);
                mBinding.conCalibRequiredAlarmTilIsc.setEnabled(false);
                mBinding.conResetCalibTilIsc.setEnabled(false);
                mBinding.conRow7Isc.setVisibility(View.GONE);
                mBinding.conSensorActivationTilIsc.setVisibility(View.GONE);
                break;

            case 2: // View
                mBinding.conSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.conCellConstantEdtIsc.setEnabled(false);
                mBinding.conCellConstantDeciIsc.setEnabled(false);
                mBinding.conCompFactorTilIsc.setEnabled(false);
                mBinding.conCompFactorDeciIsc.setEnabled(false);
                mBinding.conCompensationTilIsc.setEnabled(false);
                mBinding.conSmoothingFactorTilIsc.setEnabled(false);
                mBinding.conDeleteLayoutIsc.setVisibility(View.GONE);
                break;
        }
    }
}
