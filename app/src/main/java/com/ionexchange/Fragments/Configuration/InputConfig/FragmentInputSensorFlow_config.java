
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
import com.ionexchange.databinding.FragmentInputsensorFlowBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.flowAlarmMode;
import static com.ionexchange.Others.ApplicationClass.flowMeterTypeArr;
import static com.ionexchange.Others.ApplicationClass.flowUnitArr;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.getDecimalValue;
import static com.ionexchange.Others.ApplicationClass.getPositionFromAtxt;
import static com.ionexchange.Others.ApplicationClass.getStringValue;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.isFieldEmpty;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.resetFlowTotalArr;
import static com.ionexchange.Others.ApplicationClass.scheduleResetArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.sensorSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.totalAlarmMode;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorFlow_config extends Fragment implements DataReceiveCallback {
    FragmentInputsensorFlowBinding mBinding;
    ApplicationClass mAppClass;
    private static final String TAG = "FragmentInputSensorFlow";
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    int packetId;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    String sequenceNumber = "1"; // todo sequenceNumber

    public FragmentInputSensorFlow_config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorFlow_config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_flow, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
        mBinding.setFlowMeterType("1");
        checkUser();
        initAdapter();
        mActivity = (BaseActivity) getActivity();
        mBinding.flowFlowMeterTypeAtxtIsc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBinding.flowVolumeRateunitEdtIsc.setText("");
                mBinding.flowVolumeRateunitDeciIsc.setText("");
                mBinding.setFlowMeterType(String.valueOf(i));
            }
        });

        mBinding.orpBackArrowIsc.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
        mBinding.flowSaveFab.setOnClickListener(this::save);
        mBinding.flowDeleteFab.setOnClickListener(this::delete);
    }

    private void checkUser() {
        switch (userType) {
            case 1: // Basic
                // root
                mBinding.flowInputNumberTilIsc.setEnabled(false);
                mBinding.flowInputLabelTilIsc.setEnabled(false);
                mBinding.flowSensorTypeTilIsc.setEnabled(false);
                mBinding.flowFlowUnitTilIsc.setEnabled(false);
                mBinding.flowTotalizerAlarmTilIsc.setEnabled(false);
                mBinding.flowTotalizerAlarmDeciIsc.setEnabled(false);

                mBinding.flowResetFlowTotalTilIsc.setEnabled(false);
                mBinding.flowScheduleResetTilIsc.setEnabled(false);
                mBinding.flowFlowMeterTypeTilIsc.setEnabled(false);
                mBinding.flowVolumeRateunitTilIsc.setEnabled(false);
                mBinding.flowVolumeRateunitDeciIsc.setEnabled(false);
                mBinding.flowAlarmLowTilIsc.setEnabled(false);
                mBinding.flowAlarmLowDeciIsc.setEnabled(false);
                mBinding.flowHighAlarmTilIsc.setEnabled(false);
                mBinding.flowHighAlarmDeciIsc.setEnabled(false);
                mBinding.flowSetFlowTotalTilIsc.setEnabled(false);
                mBinding.flowSetFlowTotalDeciIsc.setEnabled(false);

                /* flowMeter type */
                // analog
                mBinding.flowMinEdtTilIsc.setEnabled(false);
                mBinding.flowMinDeciIsc.setEnabled(false);
                mBinding.flowMaxTilIsc.setEnabled(false);
                mBinding.flowMaxDeciIsc.setEnabled(false);
                mBinding.flowCalibrationRequiredTilIsc.setEnabled(false);
                mBinding.flowResetCalibrationTilIsc.setEnabled(false);
                mBinding.flowSmoothingFactorTilIsc.setVisibility(View.GONE);
                mBinding.flowSensorActivationTilIsc.setVisibility(View.GONE);

                // paddle wheel
                mBinding.flowKFactorTilIsc.setEnabled(false);

                // feed monitor
                mBinding.flowVolumeRateunitDeciIsc.setEnabled(false);
                mBinding.flowTotalizerAlarmTilIsc.setEnabled(false);
                mBinding.flowTotalizerAlarmDeciIsc.setEnabled(false);
                mBinding.flowOutPutRelayTilIsc.setEnabled(false);
                mBinding.flowAlarmModeTilIsc.setEnabled(false);
                mBinding.flowAlarmClearTilIsc.setEnabled(false);
                mBinding.flowAlarmDelayTilIsc.setEnabled(false);

                mBinding.flowRow8Isc.setVisibility(View.GONE);
                break;
            case 2: // Intermediate
                // root
                mBinding.flowInputNumberTilIsc.setEnabled(false);
                mBinding.flowSensorTypeTilIsc.setEnabled(false);
                mBinding.flowSensorActivationTilIsc.setEnabled(false);
                mBinding.flowSensorTypeTilIsc.setEnabled(false);
                mBinding.flowFlowMeterTypeTilIsc.setEnabled(false);
                mBinding.flowFlowUnitTilIsc.setEnabled(false);
                mBinding.flowResetFlowTotalTilIsc.setEnabled(false);
                mBinding.flowTotalizerAlarmTilIsc.setEnabled(false);
                mBinding.flowTotalizerAlarmDeciIsc.setEnabled(false);
                mBinding.flowScheduleResetTilIsc.setEnabled(false);

                /* flowMeter type */
                // analog
                mBinding.flowVolumeRateunitTilIsc.setEnabled(false);
                mBinding.flowVolumeRateunitDeciIsc.setEnabled(false);
                mBinding.flowMinEdtTilIsc.setEnabled(false);
                mBinding.flowMinDeciIsc.setEnabled(false);
                mBinding.flowMaxTilIsc.setEnabled(false);
                mBinding.flowMaxDeciIsc.setEnabled(false);
                mBinding.flowSmoothingFactorTilIsc.setEnabled(false);

                // contactor
                mBinding.flowVolumeRateunitTilIsc.setEnabled(false);
                mBinding.flowVolumeRateunitDeciIsc.setEnabled(false);

                //paddle wheel
                mBinding.flowVolumeRateunitTilIsc.setEnabled(false);
                mBinding.flowVolumeRateunitDeciIsc.setEnabled(false);
                mBinding.flowKFactorTilIsc.setEnabled(false);

                //feed monitor
                mBinding.flowAlarmModeTilIsc.setEnabled(false);
                mBinding.flowTotalAlarmModeTilIsc.setEnabled(false);
                mBinding.flowAlarmDelayTilIsc.setEnabled(false);
                mBinding.flowAlarmClearTilIsc.setEnabled(false);

                mBinding.flowDeleteLayout.setVisibility(View.GONE);
                break;
        }
    }

    private void delete(View view) {
        switch (Integer.parseInt(getPositionFromAtxt(0, getStringValue(mBinding.flowFlowMeterTypeAtxtIsc), flowMeterTypeArr))) {
            case 0:
                if (validation()) {
                    sendAnalogPacket(2);
                    packetId = 0;
                }
                break;
            case 1:
                if (validContactor()) {
                    sendContactorPacket(2);
                    packetId = 1;
                }
                break;
            case 2:
                if (validation2()) {
                    sendPaddleWheelPacket(2);
                    packetId = 2;
                }
                break;
            case 3:
                if (validation3()) {
                    sendFeedMonitorPacket(2);
                    packetId = 3;
                }
                break;
        }
    }

    private void save(View view) {
        switch (Integer.parseInt(getPositionFromAtxt(0, getStringValue(mBinding.flowFlowMeterTypeAtxtIsc), flowMeterTypeArr))) {
            case 0:
                if (validation()) {
                    sendAnalogPacket(sensorStatus);
                    packetId = 0;
                }
                break;
            case 1:
                if (validContactor()) {
                    sendContactorPacket(sensorStatus);
                    packetId = 1;
                }
                break;
            case 2:
                if (validation2()) {
                    sendPaddleWheelPacket(sensorStatus);
                    packetId = 2;
                }
                break;
            case 3:
                if (validation3()) {
                    sendFeedMonitorPacket(sensorStatus);
                    packetId = 3;
                }
                break;
        }
    }

    private boolean validation3() {
        if (isFieldEmpty(mBinding.flowInputNumberEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "InputNumber cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowSensorTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Input Type cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowMeterTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Flow Meter Type cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Sensor Activation cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Input Label cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowUnitAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Flow Unit cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowFeedVolumeEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Volume cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowVolumeRateunitEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Rate Unit cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowTotalAlarmModeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Total Alarm Mode cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmModeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Flow Alarm Mode cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmDelayEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Flow Alarm Mode cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmClearEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Flow Alarm Clear cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowOutputRelayEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Output Relay Link cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowTotalizerAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Totalizer Alarm cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowResetFlowTotalAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Reset Flow Total cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowSetFlowTotalEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Set Flow Total cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowScheduleResetAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Schedule Reset cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm Low cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowHighAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm High cannot be Empty");
            return false;
        }
        return true;
    }

    private boolean validation2() {
        if (isFieldEmpty(mBinding.flowInputNumberEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "InputNumber cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowSensorTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Sensor Type cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowMeterTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Flow Meter Type cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Sensor Activation cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowUnitAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Flow Unit cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowVolumeRateunitEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Rate Unit cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowKFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "K Factor cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowTotalizerAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Totalizer Alarm cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowResetFlowTotalAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Reset Flow cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowSetFlowTotalEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "set Flow Total cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowScheduleResetAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Schedule Reset cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowHighAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm high cannot be Empty");
            return false;
        }
        return true;
    }

    private boolean validContactor() {
        if (isFieldEmpty(mBinding.flowFlowMeterTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "FlowMeter Type cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Sensor Activation cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowUnitAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Flow Unit cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowVolumeRateunitEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Volume cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowTotalizerAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Totalizer Alarm cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowResetFlowTotalAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "ResetFlowTotal cannot be Empty ");
            return false;
        } else if (isFieldEmpty(mBinding.flowSetFlowTotalEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Set Flow Total cannot be Empty ");
            return false;
        } else if (isFieldEmpty(mBinding.flowScheduleResetAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Schedule Reset cannot be Empty ");
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowHighAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm high cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.flowVolumeRateunitEdtIsc.getText().toString()) > 7) {
            mAppClass.showSnackBar(getContext(), "Volume should less than 7 digit");
            return false;
        }
        return true;
    }

    private boolean validation() {
        if (isFieldEmpty(mBinding.flowFlowMeterTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Flow Meter Type cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Sensor Activation cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Input Label cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowUnitAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Flow Unit cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowVolumeRateunitEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Rate unit cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowMaxEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Flow max cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowMinEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "flow min cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "smoothing Factor cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowTotalizerAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Totalizer Alarm cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowResetFlowTotalAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Reset Flow Total cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowScheduleResetAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Schedule Reset cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowSetFlowTotalEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "SetFlowTotal cannot be Empty ");
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowHighAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm high cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowCalibrationRequiredEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Calibartion Required cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.flowResetCalibrationEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Calibartion Required cannot be Empty");
            return false;
        }
        return true;
    }

    private void sendFeedMonitorPacket(int sensorStatus) {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.flowInputNumberEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.flowSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowFlowMeterTypeAtxtIsc), flowMeterTypeArr) + SPILT_CHAR +
                sequenceNumber + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.flowInputLabelEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowFeedVolumeEdtIsc, 7, mBinding.flowFeedVolumeDeciIsc, 3) + SPILT_CHAR +
                getDecimalValue(mBinding.flowVolumeRateunitEdtIsc, 4, mBinding.flowVolumeRateunitDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowTotalAlarmModeAtxtIsc), totalAlarmMode) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowAlarmModeAtxtIsc), flowAlarmMode) + SPILT_CHAR +
                getStringValue(4, mBinding.flowAlarmDelayEdtIsc) + SPILT_CHAR +
                getStringValue(6, mBinding.flowAlarmClearEdtIsc) + SPILT_CHAR +
                getStringValue(2, mBinding.flowOutputRelayEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.flowTotalizerAlarmEdtIsc, 7, mBinding.flowTotalizerAlarmDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowResetFlowTotalAtxtIsc), resetFlowTotalArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowSetFlowTotalEdtIsc, 10, mBinding.flowSetFlowTotalDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowScheduleResetAtxtIsc), scheduleResetArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowAlarmLowEdtIsc, 10, mBinding.flowAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.flowHighAlarmEdtIsc, 10, mBinding.flowHighAlarmDeciIsc, 2) + SPILT_CHAR +
                sensorStatus);
    }

    private void sendPaddleWheelPacket(int sensorStatus) {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.flowInputNumberEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.flowSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowFlowMeterTypeAtxtIsc), flowMeterTypeArr) + SPILT_CHAR +
                sequenceNumber + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.flowInputLabelEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowVolumeRateunitEdtIsc, 4, mBinding.flowVolumeRateunitDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.flowKFactorEdtIsc, 7, mBinding.flowKFactorDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.flowTotalizerAlarmEdtIsc, 7, mBinding.flowTotalizerAlarmDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowResetFlowTotalAtxtIsc), resetFlowTotalArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowSetFlowTotalEdtIsc, 10, mBinding.flowSetFlowTotalDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.flowScheduleResetAtxtIsc), scheduleResetArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowAlarmLowEdtIsc, 10, mBinding.flowAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.flowHighAlarmEdtIsc, 10, mBinding.flowHighAlarmDeciIsc, 2) + SPILT_CHAR +
                sensorStatus);
    }

    private void sendContactorPacket(int sensorStatus) {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.flowInputNumberEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.flowSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowFlowMeterTypeAtxtIsc), flowMeterTypeArr) + SPILT_CHAR +
                sequenceNumber + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.flowSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.flowInputLabelEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowVolumeRateunitEdtIsc, 7, mBinding.flowVolumeRateunitDeciIsc, 3) + SPILT_CHAR +
                getDecimalValue(mBinding.flowTotalizerAlarmEdtIsc, 10, mBinding.flowTotalizerAlarmDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowResetFlowTotalAtxtIsc), resetFlowTotalArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowSetFlowTotalEdtIsc, 10, mBinding.flowSetFlowTotalDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.flowScheduleResetAtxtIsc), scheduleResetArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowAlarmLowEdtIsc, 10, mBinding.flowAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.flowHighAlarmEdtIsc, 10, mBinding.flowHighAlarmDeciIsc, 2) + SPILT_CHAR +
                sensorStatus);
    }

    private void sendAnalogPacket(int sensorStatus) {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.flowInputNumberEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.flowSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowFlowMeterTypeAtxtIsc), flowMeterTypeArr) + SPILT_CHAR +
                sequenceNumber + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.flowInputLabelEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowVolumeRateunitEdtIsc, 4, mBinding.flowVolumeRateunitDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.flowMaxEdtIsc, 7, mBinding.flowMaxDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.flowMinEdtIsc, 7, mBinding.flowMinDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.flowSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.flowTotalizerAlarmEdtIsc, 7, mBinding.flowTotalizerAlarmDeciIsc, 2) + SPILT_CHAR +
                getStringValue(4, mBinding.flowResetFlowTotalAtxtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.flowScheduleResetAtxtIsc), scheduleResetArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowSetFlowTotalEdtIsc, 10, mBinding.flowSetFlowTotalDeciIsc, 2) + SPILT_CHAR +
                getStringValue(10, mBinding.flowAlarmLowEdtIsc) + "." + getStringValue(2, mBinding.flowAlarmLowDeciIsc) + SPILT_CHAR +
                getStringValue(10, mBinding.flowHighAlarmEdtIsc) + "." + getStringValue(2, mBinding.flowHighAlarmDeciIsc) + SPILT_CHAR +
                getStringValue(3, mBinding.flowCalibrationRequiredEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.flowResetCalibrationEdtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus);
    }

    private void initAdapter() {
        mBinding.flowFlowMeterTypeAtxtIsc.setText("Contactor");
        mBinding.flowFlowMeterTypeAtxtIsc.setAdapter(getAdapter(flowMeterTypeArr, getContext()));
        mBinding.flowSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.flowSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.flowFlowUnitAtxtIsc.setAdapter(getAdapter(flowUnitArr, getContext()));
        mBinding.flowScheduleResetAtxtIsc.setAdapter(getAdapter(scheduleResetArr, getContext()));
        mBinding.flowResetCalibrationEdtIsc.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.flowResetFlowTotalAtxtIsc.setAdapter(getAdapter(resetFlowTotalArr, getContext()));
        mBinding.flowSeqNumberAtxtIsc.setAdapter(getAdapter(sensorSequenceNumber, getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, inputNumber));
        } else {
            mBinding.flowInputNumberEdtIsc.setText(inputNumber);
            mBinding.flowSensorTypeAtxtIsc.setText(sensorName);
            mBinding.flowDeleteLayout.setVisibility(View.GONE);
            mBinding.saveTxt.setText("ADD");
        }
    }

    @Override
    public void OnDataReceive(String data) {
        mActivity.dismissProgress();
        if (data != null) {
            handleResponse(data.split("\\*")[1].split("\\$"));
        }
    }

    private void handleResponse(String[] splitData) {
        // {*1$ 04$ 0$ 07$ 06$ 0$ 0$ 0$ analog$ 0$ 10.00$ 11.00$ 120$ 12.00$ 13.00$ 100$ 0$ 0*}
        if (splitData[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.setFlowMeterType(splitData[5]);

                    // Alarm Low/Alarm High
                    mBinding.flowInputNumberEdtIsc.setText(splitData[3]);
                    mBinding.flowSensorTypeAtxtIsc.setText(mBinding.flowSensorTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                    mBinding.flowFlowMeterTypeAtxtIsc.setText(mBinding.flowFlowMeterTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[5])).toString());
                    mBinding.flowSeqNumberAtxtIsc.setText(mBinding.flowSeqNumberAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[6])).toString());
                    mBinding.flowSensorActivationAtxtIsc.setText(mBinding.flowSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[7])).toString());
                    mBinding.flowInputLabelEdtIsc.setText(splitData[8]);
                    mBinding.flowFlowUnitAtxtIsc.setText(mBinding.flowFlowUnitAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());

                    // Analog Flow Meter
                    if (splitData[5].equals("0")) {
                        mBinding.flowVolumeRateunitEdtIsc.setText(splitData[10]);
                        mBinding.flowMinEdtIsc.setText(splitData[11].substring(0, 7));
                        mBinding.flowMinDeciIsc.setText(splitData[11].substring(9, 10));
                        mBinding.flowMaxEdtIsc.setText(splitData[12].substring(0, 7));
                        mBinding.flowMaxDeciIsc.setText(splitData[12].substring(9, 10));
                        mBinding.flowSmoothingFactorEdtIsc.setText(splitData[13]);
                        mBinding.flowTotalizerAlarmEdtIsc.setText(splitData[14].substring(0, 10));
                        mBinding.flowTotalizerAlarmDeciIsc.setText(splitData[14].substring(11, 13));
                        mBinding.flowResetFlowTotalAtxtIsc.setText(mBinding.flowResetFlowTotalAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[15])).toString());
                        mBinding.flowScheduleResetAtxtIsc.setText(mBinding.flowScheduleResetAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[16])).toString());
                        mBinding.flowSetFlowTotalEdtIsc.setText(splitData[17].substring(0, 9));
                        mBinding.flowSetFlowTotalDeciIsc.setText(splitData[17].substring(11, 13));
                        mBinding.flowAlarmLowEdtIsc.setText(splitData[18].substring(0, 9));
                        mBinding.flowAlarmLowDeciIsc.setText(splitData[18].substring(11, 13));
                        mBinding.flowHighAlarmEdtIsc.setText(splitData[19].substring(0, 9));
                        mBinding.flowHighAlarmDeciIsc.setText(splitData[19].substring(11, 13));
                        mBinding.flowCalibrationRequiredEdtIsc.setText(splitData[20]);
                        mBinding.flowResetCalibrationEdtIsc.setText(mBinding.flowResetCalibrationEdtIsc.getAdapter().getItem(Integer.parseInt(splitData[21])).toString());

                        // Contactor
                    } else if (splitData[5].equals("1")) {
                        mBinding.flowVolumeRateunitEdtIsc.setText(splitData[10].substring(0, 6));
                        mBinding.flowVolumeRateunitDeciIsc.setText(splitData[10].substring(8, 11));
                        mBinding.flowTotalizerAlarmEdtIsc.setText(splitData[11].substring(0, 10));
                        mBinding.flowTotalizerAlarmDeciIsc.setText(splitData[11].substring(11, 13));
                        mBinding.flowResetFlowTotalAtxtIsc.setText(mBinding.flowResetFlowTotalAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[12])).toString());
                        mBinding.flowSetFlowTotalEdtIsc.setText(splitData[13].substring(0, 9));
                        mBinding.flowSetFlowTotalDeciIsc.setText(splitData[13].substring(11, 13));
                        mBinding.flowScheduleResetAtxtIsc.setText(mBinding.flowScheduleResetAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[14])).toString());
                        mBinding.flowAlarmLowEdtIsc.setText(splitData[15].substring(0, 9));
                        mBinding.flowAlarmLowDeciIsc.setText(splitData[15].substring(11, 13));
                        mBinding.flowHighAlarmEdtIsc.setText(splitData[16].substring(0, 9));
                        mBinding.flowHighAlarmDeciIsc.setText(splitData[16].substring(11, 13));

                        // Paddle wheel flow meter type
                    } else if (splitData[5].equals("2")) {
                        mBinding.flowVolumeRateunitEdtIsc.setText(splitData[10].substring(0, 4));
                        mBinding.flowVolumeRateunitDeciIsc.setText(splitData[10].substring(5, 7));
                        mBinding.flowKFactorEdtIsc.setText(splitData[11].substring(0, 7));
                        mBinding.flowKFactorDeciIsc.setText(splitData[11].substring(8, 10));
                        mBinding.flowTotalizerAlarmEdtIsc.setText(splitData[12].substring(0, 10));
                        mBinding.flowTotalizerAlarmDeciIsc.setText(splitData[12].substring(11, 13));
                        mBinding.flowResetFlowTotalAtxtIsc.setText(mBinding.flowResetFlowTotalAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[13])).toString());
                        mBinding.flowSetFlowTotalEdtIsc.setText(splitData[14].substring(0, 9));
                        mBinding.flowSetFlowTotalDeciIsc.setText(splitData[14].substring(11, 13));
                        mBinding.flowScheduleResetAtxtIsc.setText(mBinding.flowScheduleResetAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[15])).toString());
                        mBinding.flowAlarmLowEdtIsc.setText(splitData[16].substring(0, 9));
                        mBinding.flowAlarmLowDeciIsc.setText(splitData[16].substring(11, 13));
                        mBinding.flowHighAlarmEdtIsc.setText(splitData[17].substring(0, 9));
                        mBinding.flowHighAlarmDeciIsc.setText(splitData[17].substring(11, 13));

                        // Feed Monitor type
                    } else if (splitData[5].equals("3")) {
                        mBinding.flowFeedVolumeEdtIsc.setText(splitData[9].substring(0, 7));
                        mBinding.flowFeedVolumeDeciIsc.setText(splitData[9].substring(9, 11));
                        mBinding.flowVolumeRateunitEdtIsc.setText(splitData[10].substring(0, 4));
                        mBinding.flowVolumeRateunitDeciIsc.setText(splitData[10].substring(6, 7));
                        mBinding.flowTotalAlarmModeAtxtIsc.setText(mBinding.flowTotalAlarmModeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[11])).toString());
                        mBinding.flowTotalAlarmModeAtxtIsc.setText(mBinding.flowTotalAlarmModeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[12])).toString());
                        mBinding.flowAlarmClearEdtIsc.setText(splitData[13]);
                        mBinding.flowOutputRelayEdtIsc.setText(splitData[14]);
                        mBinding.flowTotalizerAlarmEdtIsc.setText(splitData[12].substring(0, 10));
                        mBinding.flowTotalizerAlarmDeciIsc.setText(splitData[12].substring(11, 13));
                        mBinding.flowResetFlowTotalAtxtIsc.setText(mBinding.flowResetFlowTotalAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[13])).toString());
                        mBinding.flowSetFlowTotalEdtIsc.setText(splitData[14].substring(0, 9));
                        mBinding.flowSetFlowTotalDeciIsc.setText(splitData[14].substring(11, 13));
                        mBinding.flowScheduleResetAtxtIsc.setText(mBinding.flowScheduleResetAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[15])).toString());
                        mBinding.flowAlarmLowEdtIsc.setText(splitData[16].substring(0, 9));
                        mBinding.flowAlarmLowDeciIsc.setText(splitData[16].substring(11, 13));
                        mBinding.flowHighAlarmEdtIsc.setText(splitData[17].substring(0, 9));
                        mBinding.flowHighAlarmDeciIsc.setText(splitData[17].substring(11, 13));
                    }
                    initAdapter();
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Response Failed");
                }
                // {*0$ 04$ 1$ 0*}
            } else if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[3].equals(RES_SUCCESS)) {
                    flowMeterEntity(Integer.parseInt(splitData[2]));
                    mAppClass.showSnackBar(getContext(), "Write Success");
                } else if (splitData[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Write Failed");
                }
            }
        } else {
            Log.e(TAG, "handleResponse: Received Wrong Packet");
        }
    }

    public void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void flowMeterEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.flowInputNumberEdtIsc)),
                                "0", 0, "0", "0",
                                "0", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                mBinding.orpBackArrowIsc.performClick();
                break;

            case 0:
            case 1:
                InputConfigurationEntity flowEntityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.flowInputNumberEdtIsc)),
                                mBinding.flowSensorTypeAtxtIsc.getText().toString(),
                                0, getStringValue(0, mBinding.flowInputLabelEdtIsc),
                                getDecimalValue(mBinding.flowAlarmLowEdtIsc, 10, mBinding.flowAlarmLowDeciIsc, 2),
                                getDecimalValue(mBinding.flowHighAlarmEdtIsc, 10, mBinding.flowHighAlarmDeciIsc, 2),
                                1);
                List<InputConfigurationEntity> entryFlowList = new ArrayList<>();
                entryFlowList.add(flowEntityUpdate);
                updateToDb(entryFlowList);
                break;
        }

    }
}
