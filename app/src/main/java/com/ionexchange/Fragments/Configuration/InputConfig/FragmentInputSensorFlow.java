
package com.ionexchange.Fragments.Configuration.InputConfig;

import static com.ionexchange.Activity.BaseActivity.dismissProgress;
import static com.ionexchange.Activity.BaseActivity.showProgress;
import static com.ionexchange.Others.ApplicationClass.FlowanalogType;
import static com.ionexchange.Others.ApplicationClass.bleedRelay;
import static com.ionexchange.Others.ApplicationClass.flowAlarmMode;
import static com.ionexchange.Others.ApplicationClass.flowMeterTypeArr;
import static com.ionexchange.Others.ApplicationClass.flowUnitArr;
import static com.ionexchange.Others.ApplicationClass.flowmeterSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.getDecimalValue;
import static com.ionexchange.Others.ApplicationClass.getPosition;
import static com.ionexchange.Others.ApplicationClass.getPositionFromAtxt;
import static com.ionexchange.Others.ApplicationClass.getStringValue;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.isFieldEmpty;
import static com.ionexchange.Others.ApplicationClass.mainConfigurationDao;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.resetFlowTotalArr;
import static com.ionexchange.Others.ApplicationClass.scheduleResetArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.totalAlarmMode;
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
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.Others.EventLogDemo;
import com.ionexchange.R;
import com.ionexchange.Singleton.ApiService;
import com.ionexchange.Singleton.SharedPref;
import com.ionexchange.databinding.FragmentInputsensorFlowBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentInputSensorFlow extends Fragment implements DataReceiveCallback {
    FragmentInputsensorFlowBinding mBinding;
    ApplicationClass mAppClass;
    private static final String TAG = "FragmentInputSensorFlow";
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus, flowmeterType = 0;
    int packetId;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    String sequenceNumber;
    OutputConfigurationDao output_dao;
    String[] outputNames;
    String writePacket;

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

        inputNumber = getArguments().getString("inputNumber");
        sensorName = getArguments().getString("sensorName");
        sensorStatus = getArguments().getInt("sensorStatus");
        sequenceNumber = getArguments().getString("sequenceNo");
        flowmeterType = getArguments().getInt("sequenceType");

        output_dao = db.outputConfigurationDao();
        checkUser();
        initAdapter();
        mActivity = (BaseActivity) getActivity();
        mBinding.flowFlowMeterTypeAtxtIsc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBinding.flowVolumeRateunitEdtIsc.setText("");
                mBinding.flowVolumeRateunitDeciIsc.setText("");
                mBinding.setFlowMeterType(String.valueOf(i));
                switch (i) {
                    case 0:
                        mBinding.flowHighAlarmDeciIsc.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                        break;
                    case 1:
                        mBinding.flowHighAlarmDeciIsc.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        break;
                    case 2:
                        mBinding.flowHighAlarmDeciIsc.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                        mBinding.flowKFactorDeciIsc.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        break;
                    case 3:
                        mBinding.flowHighAlarmDeciIsc.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                        mBinding.flowKFactorDeciIsc.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                        mBinding.flowFeedVolumeDeciIsc.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        break;
                }
            }
        });

        mBinding.orpBackArrowIsc.setOnClickListener(v -> {
            mAppClass.popStackBack(getActivity());
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
                /*mBinding.flowResetFlowTotalTilIsc.setEnabled(false);
                mBinding.flowTotalizerAlarmTilIsc.setEnabled(false);
                mBinding.flowTotalizerAlarmDeciIsc.setEnabled(false);*/
                mBinding.flowScheduleResetTilIsc.setEnabled(false);
                mBinding.flowCalibrationRequiredEdtIsc.setImeOptions(EditorInfo.IME_ACTION_DONE);
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

                // paddle wheel
                mBinding.flowVolumeRateunitTilIsc.setEnabled(false);
                mBinding.flowVolumeRateunitDeciIsc.setEnabled(false);
                mBinding.flowKFactorTilIsc.setEnabled(false);

                // feed monitor
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
                if (validAnalog()) {
                    showProgress();
                    sendAnalogPacket(2);
                    packetId = 0;
                }
                break;
            case 1:
                if (validContactor()) {
                    showProgress();
                    sendContactorPacket(2);
                    packetId = 1;
                }
                break;
            case 2:
                if (validPaddleWheel()) {
                    showProgress();
                    sendPaddleWheelPacket(2);
                    packetId = 2;
                }
                break;
            case 3:
                if (validation3()) {
                    showProgress();
                    sendFeedMonitorPacket(2);
                    packetId = 3;
                }
                break;
        }
    }

    private void save(View view) {
        switch (Integer.parseInt(getPositionFromAtxt(0, getStringValue(mBinding.flowFlowMeterTypeAtxtIsc), flowMeterTypeArr))) {
            case 0:
                if (validAnalog()) {
                    showProgress();
                    sendAnalogPacket(1);
                    packetId = 0;
                }
                break;
            case 1:
                if (validContactor()) {
                    showProgress();
                    sendContactorPacket(1);
                    packetId = 1;
                }
                break;
            case 2:
                if (validPaddleWheel()) {
                    showProgress();
                    sendPaddleWheelPacket(1);
                    packetId = 2;
                }
                break;
            case 3:
                if (validation3()) {
                    showProgress();
                    sendFeedMonitorPacket(1);
                    packetId = 3;
                }
                break;
        }
    }

    private boolean validation3() {
        if (isFieldEmpty(mBinding.flowInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.input_name_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.sensor_activation_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowMeterTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowmeter_type_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowTotalizerAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.totalizer_vali));
            return false;
        } else if (Long.valueOf(mBinding.flowTotalizerAlarmEdtIsc.getText().toString()) > 1000000) {
            mBinding.flowTotalizerAlarmEdtIsc.setError(getString(R.string.totalizer_feedmonitor_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowResetFlowTotalAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.reset_flowtotal_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowScheduleResetAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.schedule_reset_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowUnitAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowunit_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowVolumeRateunitEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.rateunit_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowSetFlowTotalEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.setflowtotal_vali));
            return false;
        } else if (Long.valueOf(mBinding.flowSetFlowTotalEdtIsc.getText().toString()) > 1000000000) {
            mAppClass.showSnackBar(getContext(), getString(R.string.setflowtotalmax_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_low_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowHighAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_high_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowTotalAlarmModeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.totalalarmmode_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmModeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowalarmmode_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmDelayEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowalarmdelay_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmClearEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowalarmclear_validation));
            return false;
        } else if (Integer.parseInt(mBinding.flowAlarmClearEdtIsc.getText().toString()) < 1 ||
                Integer.parseInt(mBinding.flowAlarmClearEdtIsc.getText().toString()) > 100000) {
            mAppClass.showSnackBar(getContext(), "Flow Alarm Clear Values between 1 - 100000");
            return false;
        } else if (isFieldEmpty(mBinding.flowOutputRelayEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.output_link_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowFeedVolumeEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.volume_vali));
            return false;
        } else if (Integer.parseInt(mBinding.flowFeedVolumeEdtIsc.getText().toString()) == 0) {
            if (isFieldEmpty(mBinding.flowFeedVolumeDeciIsc)) {
                mAppClass.showSnackBar(getContext(), "Volume decimal values should be greater then 001");
                return false;
            } else if (Integer.parseInt(mBinding.flowFeedVolumeDeciIsc.getText().toString()) == 0) {
                mAppClass.showSnackBar(getContext(), "Volume decimal values should be greater then 001");
                return false;
            }
        } else if (Integer.parseInt(mBinding.flowFeedVolumeEdtIsc.getText().toString()) > 1000) {
            mAppClass.showSnackBar(getContext(), "Volume values should be less than 1000");
            return false;
        } else if (getStringValue(mBinding.flowAlarmDelayEdtIsc).length() != 4) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowalarmdelay_valid));
            return false;
        } else if (getStringValue(mBinding.flowAlarmDelayEdtIsc).length() == 4 && Integer.parseInt(getStringValue(mBinding.flowAlarmDelayEdtIsc).substring(0, 2)) > 60) {
            mAppClass.showSnackBar(getContext(), "Invalid Minutes MM:SS");
            return false;
        } else if (getStringValue(mBinding.flowAlarmDelayEdtIsc).length() == 4 && Integer.parseInt(getStringValue(mBinding.flowAlarmDelayEdtIsc).substring(2, 4)) > 60) {
            mAppClass.showSnackBar(getContext(), "Invalid Seconds MM:SS");
            return false;
        }
        return true;
    }

    private boolean validPaddleWheel() {
        if (isFieldEmpty(mBinding.flowInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.input_name_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.sensor_activation_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowMeterTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowmeter_type_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowTotalizerAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.totalizer_validation));
            return false;
        } else if (Long.valueOf(mBinding.flowTotalizerAlarmEdtIsc.getText().toString()) > 2000000000) {
            mBinding.flowTotalizerAlarmEdtIsc.setError(getString(R.string.totalizer_analog_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowResetFlowTotalAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.reset_flowtotal_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowScheduleResetAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.schedule_reset_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowUnitAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowunit_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowVolumeRateunitEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.rateunit_vali));
            return false;
        } else if (!getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr).equals("3") &&
                (Long.valueOf(mBinding.flowVolumeRateunitEdtIsc.getText().toString()) == 0 ||
                        Long.valueOf(mBinding.flowVolumeRateunitEdtIsc.getText().toString()) > 100000)) {
            mBinding.flowVolumeRateunitEdtIsc.setError(getString(R.string.rateunit_valida));
            return false;
        } else if (isFieldEmpty(mBinding.flowSetFlowTotalEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.setflowtotal_vali));
            return false;
        } else if (Long.valueOf(mBinding.flowSetFlowTotalEdtIsc.getText().toString()) > 1000000000) {
            mAppClass.showSnackBar(getContext(), getString(R.string.setflowtotalmax_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_low_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowHighAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_high_validation));
            return false;
        } else if (Float.parseFloat(getDecimalValue(mBinding.flowAlarmLowEdtIsc, 10, mBinding.flowAlarmLowDeciIsc, 2)) >=
                Float.parseFloat(getDecimalValue(mBinding.flowHighAlarmEdtIsc, 10, mBinding.flowHighAlarmDeciIsc, 2))) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_limit_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowKFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.kfactor_validation));
            return false;
        } else if (getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr).equals("3") &&
                (Integer.parseInt(mBinding.flowKFactorEdtIsc.getText().toString()) == 0 ||
                        Integer.parseInt(mBinding.flowKFactorEdtIsc.getText().toString()) > 1000000)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.kfactor_m3_validation));
            return false;
        }
        if (getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr).equals("2") &&
                Integer.parseInt(mBinding.flowKFactorEdtIsc.getText().toString()) == 0) {
            if (isFieldEmpty(mBinding.flowKFactorDeciIsc)) {
                mAppClass.showSnackBar(getContext(), "K factor decimal values should be greater then 01");
                return false;
            } else if (Integer.parseInt(mBinding.flowKFactorDeciIsc.getText().toString()) == 0) {
                mAppClass.showSnackBar(getContext(), "K factor decimal values should be greater then 01");
                return false;
            }
        }
        if (getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr).equals("2") &&
                Integer.parseInt(mBinding.flowKFactorEdtIsc.getText().toString()) > 100000) {
            mAppClass.showSnackBar(getContext(), "K factor values should be less than 100000");
            return false;
        }
        if (!(getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr).equals("2") ||
                getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr).equals("3")) &&
                Integer.parseInt(mBinding.flowKFactorEdtIsc.getText().toString()) > 1000000) {
            mAppClass.showSnackBar(getContext(), "K Factor should be 1 to 100000");
            return false;
        }
        return true;
    }

    private boolean validContactor() {
        if (isFieldEmpty(mBinding.flowInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.input_name_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.sensor_activation_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowMeterTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowmeter_type_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowTotalizerAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.totalizer_vali));
            return false;
        } else if (Long.valueOf(mBinding.flowTotalizerAlarmEdtIsc.getText().toString()) > 2000000000) {
            mBinding.flowTotalizerAlarmEdtIsc.setError(getString(R.string.totalizer_analog_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowResetFlowTotalAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.reset_flowtotal_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowScheduleResetAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.schedule_reset_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowUnitAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowunit_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowVolumeRateunitEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.volume_vali));
            return false;
        } else if (!getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr).equals("3") &&
                (Long.valueOf(mBinding.flowVolumeRateunitEdtIsc.getText().toString()) == 0 ||
                        Long.valueOf(mBinding.flowVolumeRateunitEdtIsc.getText().toString()) > 100000)) {
            mBinding.flowVolumeRateunitEdtIsc.setError(getString(R.string.volume_valida));
            return false;
        } else if (isFieldEmpty(mBinding.flowSetFlowTotalEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.setflowtotal_vali));
            return false;
        } else if (Long.valueOf(mBinding.flowSetFlowTotalEdtIsc.getText().toString()) > 1000000000) {
            mAppClass.showSnackBar(getContext(), getString(R.string.setflowtotalmax_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_low_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowHighAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_high_validation));
            return false;
        } else if (Float.parseFloat(getDecimalValue(mBinding.flowAlarmLowEdtIsc, 10, mBinding.flowAlarmLowDeciIsc, 2)) >=
                Float.parseFloat(getDecimalValue(mBinding.flowHighAlarmEdtIsc, 10, mBinding.flowHighAlarmDeciIsc, 2))) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_limit_validation));
            return false;
        } else if (getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr).equals("3") &&
                Integer.parseInt(mBinding.flowVolumeRateunitEdtIsc.getText().toString()) == 0) {
            if (isFieldEmpty(mBinding.flowVolumeRateunitDeciIsc)) {
                mAppClass.showSnackBar(getContext(), "Volume decimal values should be greater then 001");
                return false;
            } else if (Integer.parseInt(mBinding.flowVolumeRateunitDeciIsc.getText().toString()) == 0) {
                mAppClass.showSnackBar(getContext(), "Volume decimal values should be greater then 001");
                return false;
            }
        } else if (getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr).equals("3") &&
                (Integer.parseInt(mBinding.flowVolumeRateunitEdtIsc.getText().toString()) > 1000)) {
            mAppClass.showSnackBar(getContext(), "Volume values should be less than 1000");
            return false;
        }
        return true;
    }

    private boolean validAnalog() {
        if (isFieldEmpty(mBinding.flowInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.input_name_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.sensor_activation_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowMeterTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowmeter_type_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowTotalizerAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.totalizer_vali));
            return false;
        } else if (Long.valueOf(mBinding.flowTotalizerAlarmEdtIsc.getText().toString()) > 2000000000) {
            mBinding.flowTotalizerAlarmEdtIsc.setError(getString(R.string.totalizer_analog_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowResetFlowTotalAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.reset_flowtotal_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowScheduleResetAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.schedule_reset_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowFlowUnitAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowunit_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowVolumeRateunitEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.rateunit_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowSetFlowTotalEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.setflowtotal_vali));
            return false;
        } else if (Long.valueOf(mBinding.flowSetFlowTotalEdtIsc.getText().toString()) > 1000000000) {
            mAppClass.showSnackBar(getContext(), getString(R.string.setflowtotalmax_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_low_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowHighAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_high_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowCalibrationRequiredEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.calibration_alarm_vali));
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.flowCalibrationRequiredEdtIsc)) > 365) {
            mAppClass.showSnackBar(getContext(), getString(R.string.calibration_alarm_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowResetCalibrationEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.reset_calibration_validation));
            return false;
        } else if (isFieldEmpty(mBinding.flowSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.smoothing_factor_validation));
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.flowSmoothingFactorEdtIsc)) > 90) {
            mAppClass.showSnackBar(getContext(), getString(R.string.smoothing_factor_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowMinEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowmin_vali));
            return false;
        } else if (Integer.parseInt(mBinding.flowMinEdtIsc.getText().toString()) > 1000000) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowmini_vali));
            return false;
        } else if (isFieldEmpty(mBinding.flowMaxEdtIsc)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowmax_vali));
            return false;
        } else if (Integer.parseInt(mBinding.flowMaxEdtIsc.getText().toString()) > 1000000) {
            mAppClass.showSnackBar(getContext(), getString(R.string.flowmaxi_vali));
            return false;
        } else if (mBinding.flowAnalogTypeAtxt.getText().toString().equals("")) {
            mAppClass.showSnackBar(getContext(), "Select the Analog Type");
            return false;
        }

        return true;
    }

    private void sendFeedMonitorPacket(int sensorStatus) {

        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
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
                formDigits(2, (Integer.parseInt(getPosition(2, getStringValue(mBinding.flowOutputRelayEdtIsc), outputNames)) + 1) + "") + SPILT_CHAR +
                getDecimalValue(mBinding.flowTotalizerAlarmEdtIsc, 10, mBinding.flowTotalizerAlarmDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowResetFlowTotalAtxtIsc), resetFlowTotalArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowSetFlowTotalEdtIsc, 10, mBinding.flowSetFlowTotalDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowScheduleResetAtxtIsc), scheduleResetArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowAlarmLowEdtIsc, 10, mBinding.flowAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.flowHighAlarmEdtIsc, 10, mBinding.flowHighAlarmDeciIsc, 2) + SPILT_CHAR +
                sensorStatus;
        mAppClass.sendPacket(this, writePacket);
    }

    private void sendPaddleWheelPacket(int sensorStatus) {

        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
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
                getDecimalValue(mBinding.flowTotalizerAlarmEdtIsc, 10, mBinding.flowTotalizerAlarmDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowResetFlowTotalAtxtIsc), resetFlowTotalArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowSetFlowTotalEdtIsc, 10, mBinding.flowSetFlowTotalDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.flowScheduleResetAtxtIsc), scheduleResetArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowAlarmLowEdtIsc, 10, mBinding.flowAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.flowHighAlarmEdtIsc, 10, mBinding.flowHighAlarmDeciIsc, 2) + SPILT_CHAR +
                sensorStatus;
        mAppClass.sendPacket(this, writePacket);
    }

    private void sendContactorPacket(int sensorStatus) {

        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
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
                sensorStatus;
        mAppClass.sendPacket(this, writePacket);
    }

    private void sendAnalogPacket(int sensorStatus) {
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.flowInputNumberEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.flowSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowFlowMeterTypeAtxtIsc), flowMeterTypeArr) + SPILT_CHAR +
                sequenceNumber + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowAnalogTypeAtxt), FlowanalogType) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.flowInputLabelEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(0, getStringValue(mBinding.flowFlowUnitAtxtIsc), flowUnitArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowVolumeRateunitEdtIsc, 4, mBinding.flowVolumeRateunitDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.flowMaxEdtIsc, 7, mBinding.flowMaxDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.flowMinEdtIsc, 7, mBinding.flowMinDeciIsc, 2) + SPILT_CHAR +
                getStringValue(3, mBinding.flowSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.flowTotalizerAlarmEdtIsc, 10, mBinding.flowTotalizerAlarmDeciIsc, 2) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.flowResetFlowTotalAtxtIsc), resetFlowTotalArr) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.flowScheduleResetAtxtIsc), scheduleResetArr) + SPILT_CHAR +
                getDecimalValue(mBinding.flowSetFlowTotalEdtIsc, 10, mBinding.flowSetFlowTotalDeciIsc, 2) + SPILT_CHAR +
                getStringValue(10, mBinding.flowAlarmLowEdtIsc) + "." + getStringValue(2, mBinding.flowAlarmLowDeciIsc) + SPILT_CHAR +
                getStringValue(10, mBinding.flowHighAlarmEdtIsc) + "." + getStringValue(2, mBinding.flowHighAlarmDeciIsc) + SPILT_CHAR +
                getStringValue(3, mBinding.flowCalibrationRequiredEdtIsc) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.flowResetCalibrationEdtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus;
        mAppClass.sendPacket(this, writePacket);
    }

    private void initAdapter() {
        mBinding.flowFlowMeterTypeAtxtIsc.setAdapter(getAdapter(flowMeterTypeArr, getContext()));
        mBinding.flowSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.flowSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.flowFlowUnitAtxtIsc.setAdapter(getAdapter(flowUnitArr, getContext()));
        mBinding.flowScheduleResetAtxtIsc.setAdapter(getAdapter(scheduleResetArr, getContext()));
        mBinding.flowResetCalibrationEdtIsc.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.flowResetFlowTotalAtxtIsc.setAdapter(getAdapter(resetFlowTotalArr, getContext()));
        mBinding.flowSeqNumberAtxtIsc.setAdapter(getAdapter(flowmeterSequenceNumber, getContext()));
        mBinding.flowTotalAlarmModeAtxtIsc.setAdapter(getAdapter(totalAlarmMode, getContext()));
        mBinding.flowAlarmModeAtxtIsc.setAdapter(getAdapter(flowAlarmMode, getContext()));
        mBinding.flowAnalogTypeAtxt.setAdapter(getAdapter(FlowanalogType, getContext()));
        List<OutputConfigurationEntity> outputNameList = output_dao.getOutputHardWareNoConfigurationEntityList(1, 14);
        outputNames = new String[14];
        if (!outputNameList.isEmpty()) {
            for (int i = 0; i < outputNameList.size(); i++) {
                outputNames[i] = "Output- " + outputNameList.get(i).getOutputHardwareNo() + " (" + outputNameList.get(i).getOutputLabel() + ")";
            }
        }
        if (outputNames.length == 0) {
            outputNames = bleedRelay;
        }
        mBinding.flowOutputRelayEdtIsc.setAdapter(getAdapter(outputNames, getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.setFlowMeterType(Integer.toString(flowmeterType));
        if (sensorName == null) {
            showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, inputNumber));
        } else {
            mBinding.flowInputNumberEdtIsc.setText(inputNumber);
            mBinding.flowSensorTypeAtxtIsc.setText(sensorName);
            mBinding.flowDeleteLayout.setVisibility(View.GONE);
            mBinding.flowSeqNumberAtxtIsc.setText(mBinding.flowSeqNumberAtxtIsc.getAdapter().getItem(Integer.parseInt(sequenceNumber)).toString());
            mBinding.flowFlowMeterTypeAtxtIsc.setText(mBinding.flowFlowMeterTypeAtxtIsc.getAdapter().getItem(flowmeterType).toString());
            mBinding.saveTxt.setText("ADD");
            mBinding.flowFlowMeterTypeAtxtIsc.setAdapter(getAdapter(flowMeterTypeArr, getContext()));
        }
    }

    @Override
    public void OnDataReceive(String data) {
        dismissProgress();
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        } else if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        } else if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        } else if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), "TimeOut");
        } else if (data != null) {
            handleResponse(data.split("\\*")[1].split("\\$"));
        }
    }

    private void handleResponse(String[] splitData) {
        try {
            if (splitData[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
                if (splitData[0].equals(READ_PACKET)) {
                    if (splitData[2].equals(RES_SUCCESS)) {
                        try {
                            mBinding.setFlowMeterType(splitData[5]);
                            // Alarm Low/Alarm High
                            mBinding.flowInputNumberEdtIsc.setText(splitData[3]);
                            mBinding.flowSensorTypeAtxtIsc.setText(mBinding.flowSensorTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                            mBinding.flowFlowMeterTypeAtxtIsc.setText(mBinding.flowFlowMeterTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[5])).toString());
                            sequenceNumber = splitData[6];
                            mBinding.flowSeqNumberAtxtIsc.setText(mBinding.flowSeqNumberAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[6])).toString());

                            mBinding.flowFlowMeterTypeAtxtIsc.setAdapter(getAdapter(flowMeterTypeArr, getContext()));
                            mBinding.flowVolumeRateunitEdtIsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                            mBinding.flowVolumeRateunitDeciIsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
                            // Analog Flow Meter
                            if (splitData[5].equals("0")) {
                                mBinding.flowAnalogTypeAtxt.setText(mBinding.flowAnalogTypeAtxt.getAdapter().getItem(Integer.parseInt(splitData[7])).toString());
                                mBinding.flowSensorActivationAtxtIsc.setText(mBinding.flowSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[8])).toString());
                                mBinding.flowInputLabelEdtIsc.setText(splitData[9]);
                                mBinding.flowFlowUnitAtxtIsc.setText(mBinding.flowFlowUnitAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[10])).toString());
                                mBinding.flowVolumeRateunitEdtIsc.setText(splitData[11].substring(0, 4));
                                mBinding.flowVolumeRateunitDeciIsc.setText(splitData[11].substring(5, 7));
                                mBinding.flowMaxEdtIsc.setText(splitData[12].substring(0, 7));
                                mBinding.flowMaxDeciIsc.setText(splitData[12].substring(8, 10));
                                mBinding.flowMinEdtIsc.setText(splitData[13].substring(0, 7));
                                mBinding.flowMinDeciIsc.setText(splitData[13].substring(8, 10));
                                mBinding.flowSmoothingFactorEdtIsc.setText(splitData[14]);
                                mBinding.flowTotalizerAlarmEdtIsc.setText(splitData[15].substring(0, 10));
                                mBinding.flowTotalizerAlarmDeciIsc.setText(splitData[15].substring(11, 13));
                                mBinding.flowResetFlowTotalAtxtIsc.setText(mBinding.flowResetFlowTotalAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[16])).toString());
                                mBinding.flowScheduleResetAtxtIsc.setText(mBinding.flowScheduleResetAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[17])).toString());
                                mBinding.flowSetFlowTotalEdtIsc.setText(splitData[18].substring(0, 10));
                                mBinding.flowSetFlowTotalDeciIsc.setText(splitData[18].substring(11, 13));
                                mBinding.flowAlarmLowEdtIsc.setText(splitData[19].substring(0, 10));
                                mBinding.flowAlarmLowDeciIsc.setText(splitData[19].substring(11, 13));
                                mBinding.flowHighAlarmEdtIsc.setText(splitData[20].substring(0, 10));
                                mBinding.flowHighAlarmDeciIsc.setText(splitData[20].substring(11, 13));
                                mBinding.flowCalibrationRequiredEdtIsc.setText(splitData[21]);
                                mBinding.flowResetCalibrationEdtIsc.setText(mBinding.flowResetCalibrationEdtIsc.getAdapter().getItem(Integer.parseInt(splitData[22])).toString());

                                // Contactor
                            } else if (splitData[5].equals("1")) {
                                mBinding.flowVolumeRateunitEdtIsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
                                mBinding.flowInputLabelEdtIsc.setText(splitData[8]);
                                mBinding.flowFlowUnitAtxtIsc.setText(mBinding.flowFlowUnitAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());
                                mBinding.flowVolumeRateunitDeciIsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                                mBinding.flowSensorActivationAtxtIsc.setText(mBinding.flowSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[7])).toString());
                                mBinding.flowVolumeRateunitEdtIsc.setText(splitData[10].substring(0, 7));
                                mBinding.flowVolumeRateunitDeciIsc.setText(splitData[10].substring(8, 11));
                                mBinding.flowTotalizerAlarmEdtIsc.setText(splitData[11].substring(0, 10));
                                mBinding.flowTotalizerAlarmDeciIsc.setText(splitData[11].substring(11, 13));
                                mBinding.flowResetFlowTotalAtxtIsc.setText(mBinding.flowResetFlowTotalAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[12])).toString());
                                mBinding.flowSetFlowTotalEdtIsc.setText(splitData[13].substring(0, 9));
                                mBinding.flowSetFlowTotalDeciIsc.setText(splitData[13].substring(11, 13));
                                mBinding.flowScheduleResetAtxtIsc.setText(mBinding.flowScheduleResetAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[14])).toString());
                                mBinding.flowAlarmLowEdtIsc.setText(splitData[15].substring(0, 10));
                                mBinding.flowAlarmLowDeciIsc.setText(splitData[15].substring(11, 13));
                                mBinding.flowHighAlarmEdtIsc.setText(splitData[16].substring(0, 10));
                                mBinding.flowHighAlarmDeciIsc.setText(splitData[16].substring(11, 13));

                                // Paddle wheel flow meter type
                            } else if (splitData[5].equals("2")) {
                                mBinding.flowSensorActivationAtxtIsc.setText(mBinding.flowSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[7])).toString());
                                mBinding.flowInputLabelEdtIsc.setText(splitData[8]);
                                mBinding.flowFlowUnitAtxtIsc.setText(mBinding.flowFlowUnitAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());
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
                                mBinding.flowAlarmLowEdtIsc.setText(splitData[16].substring(0, 10));
                                mBinding.flowAlarmLowDeciIsc.setText(splitData[16].substring(11, 13));
                                mBinding.flowHighAlarmEdtIsc.setText(splitData[17].substring(0, 10));
                                mBinding.flowHighAlarmDeciIsc.setText(splitData[17].substring(11, 13));

                                // Feed Monitor type
                            } else if (splitData[5].equals("3")) {
                                mBinding.flowSensorActivationAtxtIsc.setText(mBinding.flowSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[7])).toString());
                                mBinding.flowInputLabelEdtIsc.setText(splitData[8]);
                                mBinding.flowFlowUnitAtxtIsc.setText(mBinding.flowFlowUnitAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());
                                mBinding.flowFeedVolumeEdtIsc.setText(splitData[10].substring(0, 7));
                                mBinding.flowFeedVolumeDeciIsc.setText(splitData[10].substring(8, 11));
                                mBinding.flowVolumeRateunitEdtIsc.setText(splitData[11].substring(0, 4));
                                mBinding.flowVolumeRateunitDeciIsc.setText(splitData[11].substring(5, 7));
                                mBinding.flowTotalAlarmModeAtxtIsc.setText(mBinding.flowTotalAlarmModeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[12])).toString());
                                mBinding.flowAlarmModeAtxtIsc.setText(mBinding.flowAlarmModeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[13])).toString());
                                mBinding.flowAlarmDelayEdtIsc.setText(splitData[14]);
                                mBinding.flowAlarmClearEdtIsc.setText(splitData[15]);
                                mBinding.flowOutputRelayEdtIsc.setText(mBinding.flowOutputRelayEdtIsc.getAdapter().getItem(Integer.parseInt(splitData[16]) - 1).toString());
                                mBinding.flowTotalizerAlarmEdtIsc.setText(splitData[17].substring(0, 10));
                                mBinding.flowTotalizerAlarmDeciIsc.setText(splitData[17].substring(11, 13));
                                mBinding.flowResetFlowTotalAtxtIsc.setText(mBinding.flowResetFlowTotalAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[18])).toString());
                                mBinding.flowSetFlowTotalEdtIsc.setText(splitData[19].substring(0, 9));
                                mBinding.flowSetFlowTotalDeciIsc.setText(splitData[19].substring(11, 13));
                                mBinding.flowScheduleResetAtxtIsc.setText(mBinding.flowScheduleResetAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[20])).toString());
                                mBinding.flowAlarmLowEdtIsc.setText(splitData[21].substring(0, 10));
                                mBinding.flowAlarmLowDeciIsc.setText(splitData[21].substring(11, 13));
                                mBinding.flowHighAlarmEdtIsc.setText(splitData[22].substring(0, 10));
                                mBinding.flowHighAlarmDeciIsc.setText(splitData[22].substring(11, 13));
                            }
                            initAdapter();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (splitData[2].equals(RES_FAILED)) {
                        mAppClass.showSnackBar(getContext(), getString(R.string.readFailed));
                    }
                    // {*0$ 04$ 1$ 0*}
                } else if (splitData[0].equals(WRITE_PACKET)) {
                    if (splitData[3].equals(RES_SUCCESS)) {
                        flowMeterEntity(Integer.parseInt(splitData[2]));
                        mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                    } else if (splitData[3].equals(RES_FAILED)) {
                        mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                    }
                }
            } else {
                Log.e(TAG, getString(R.string.wrongPack));
            }
        } catch (Exception e) {

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
                                "N/A", "FLOWMETER", 0, "N/A", 1,
                                "N/A", "N/A",
                                "N/A", "N/A", "N/A", 0, STARTPACKET + writePacket + ENDPACKET);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                new EventLogDemo(inputNumber, "FlowMeter", "Input Setting Deleted", SharedPref.read(pref_USERLOGINID, ""),getContext());
                ApiService.getInstance(getContext()).processApiData(READ_PACKET, "04", "Input Setting Deleted - " +
                        SharedPref.read(pref_USERLOGINID, ""));
                mainConfigurationDao.updateAddSensorValue(0, Integer.parseInt(inputNumber));
                break;

            case 0:
            case 1:
                InputConfigurationEntity flowEntityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.flowInputNumberEdtIsc)),
                                mBinding.flowSensorTypeAtxtIsc.getText().toString(), "FLOWMETER", 0,
                                mBinding.flowSeqNumberAtxtIsc.getText().toString(),
                                Integer.parseInt(sequenceNumber), getStringValue(0, mBinding.flowInputLabelEdtIsc),
                                getDecimalValue(mBinding.flowAlarmLowEdtIsc, 10, mBinding.flowAlarmLowDeciIsc, 2),
                                getDecimalValue(mBinding.flowHighAlarmEdtIsc, 10, mBinding.flowHighAlarmDeciIsc, 2),
                                "N/A", "N/A",
                                1, STARTPACKET + writePacket + ENDPACKET);
                List<InputConfigurationEntity> entryFlowList = new ArrayList<>();
                entryFlowList.add(flowEntityUpdate);
                updateToDb(entryFlowList);
                new EventLogDemo(inputNumber, "FlowMeter", "Input Setting Changed", SharedPref.read(pref_USERLOGINID, ""),getContext());
                ApiService.getInstance(getContext()).processApiData(READ_PACKET, "04", "Input Setting Changed - " +
                        SharedPref.read(pref_USERLOGINID, ""));
                mainConfigurationDao.updateAddSensorValue(1, Integer.parseInt(inputNumber));
                break;
        }
        mBinding.orpBackArrowIsc.performClick();
    }
}