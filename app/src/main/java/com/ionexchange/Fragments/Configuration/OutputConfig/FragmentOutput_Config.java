package com.ionexchange.Fragments.Configuration.OutputConfig;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentOutputConfigBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.bleedRelay;
import static com.ionexchange.Others.ApplicationClass.doseTypeArr;
import static com.ionexchange.Others.ApplicationClass.findDecimal;
import static com.ionexchange.Others.ApplicationClass.flowMeters;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.functionMode;
import static com.ionexchange.Others.ApplicationClass.inputAnalogSensors;
import static com.ionexchange.Others.ApplicationClass.interlockChannel;
import static com.ionexchange.Others.ApplicationClass.modeAnalog;
import static com.ionexchange.Others.ApplicationClass.modeInhibitor;
import static com.ionexchange.Others.ApplicationClass.modeSensor;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.ApplicationClass.validDecimalField;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_OUTPUT_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentOutput_Config extends Fragment implements DataReceiveCallback {
    FragmentOutputConfigBinding mBinding;
    // DummyBinding mBinding;
    ApplicationClass mAppClass;
    WaterTreatmentDb db;
    OutputConfigurationDao dao;
    int outputSensorNo;

    String lInhibitorContinuous = "layoutInhibitorContinuous", lInhibitorBleed = "layoutInhibitorBleedDown", lInhibitorWaterFlow = "layoutInhibitorWaterFlow",
            lSensorOnOFF = "layoutSensorOnOff", lSensorPid = "layoutSensorPID", lAnalogMain = "layoutAnalogMain", lAnalogTest = "layoutAnalogTest", lAnalogDisable = "layoutAnalogDisable",
            currentFunctionMode = "", analogMode = "3";
    private static final String TAG = "FragmentOutput_Config";

    public FragmentOutput_Config(int outputSensorNo) {
        this.outputSensorNo = outputSensorNo;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_output_config, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        db = WaterTreatmentDb.getDatabase(getContext());
        Log.e(TAG, "onViewCreated: " + outputSensorNo);
        dao = db.outputConfigurationDao();

        initAdapter();
        enableDisabled();
        mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(1).toString());
        if (outputSensorNo < 15) {
            enableInhibitorLayout();
        } else {
            enableAnalogLayout();
        }

        initAdapter();
        mBinding.funtionModeOsATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                switch (pos) {
                    case 0:
                        enableDisabled();
                        break;
                    case 1:
                        if (outputSensorNo < 15) {
                            enableInhibitorLayout();
                        } else {
                            enableAnalogLayout();
                        }
                        break;
                    case 2:
                        enableSensorLayout();
                        break;
                }
            }
        });

        mBinding.modeOsATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                switch (currentFunctionMode) {
                    case "Inhibitor":
                        switch (pos) {
                            case 0:
                                enableContinuous();
                                break;
                            case 1:
                                enableBleed();
                                break;
                            case 2:
                                enableWater();
                                break;
                        }
                        break;

                    case "Sensor":
                        switch (pos) {
                            case 0:
                                enableOnOff();
                                break;
                            case 1:
                                enablePID();
                                break;
                            case 2:
                                enableFuzzy();
                                break;
                        }
                        break;

                    case "Analog":
                        if (pos == 0) {
                            enableAnalogDisable();
                        } else if (pos == 2) {
                            enableAnalogTest();
                        } else {
                            enableAnalogMain();
                        }
                        break;

                }
            }
        });

        mBinding.saveFabOutput.setOnClickListener(this::save);
        mBinding.saveLayoutOutput.setOnClickListener(this::save);
        checkUser();
    }

    // TODO: 30-08-2021 OutputScreen Should completely change
    private void checkUser() {

        switch (userType) {
            case 1:
                if (outputSensorNo < 15) {
                    Toast.makeText(mAppClass, "Digital Output", Toast.LENGTH_SHORT).show();
                    mBinding.outputLabelOs.setEnabled(false);
                    mBinding.functionModeOs.setEnabled(false);
                    mBinding.modeOs.setEnabled(false);
                    mBinding.outputInterLockChannelOs.setEnabled(false);
                    mBinding.outputActivateChannelOs.setEnabled(false);
                    mBinding.setFunctionMode("BASIC");
                    mBinding.outputRow3InhibitorCont.setVisibility(View.GONE);
                    mBinding.outputRow4InhibitorBleed.setVisibility(View.GONE);
                    mBinding.outputRow5InhibitorBleed.setVisibility(View.GONE);
                    mBinding.outputRow6InhibitorWater.setVisibility(View.GONE);
                    mBinding.outputRow7InhibitorWater.setVisibility(View.GONE);
                    mBinding.outputRow8SensorOnOff.setVisibility(View.GONE);
                    mBinding.outputRow9SensorOnOff.setVisibility(View.GONE);
                    mBinding.outputRow10SensorOnOff.setVisibility(View.GONE);
                    mBinding.outputRow11SensorPID.setVisibility(View.GONE);
                    mBinding.outputRow12SensorPID.setVisibility(View.GONE);
                    mBinding.outputRow13SensorPID.setVisibility(View.GONE);
                    mBinding.outputRow14SensorPID.setVisibility(View.GONE);
                    mBinding.outputRow17AnalogTest.setVisibility(View.GONE);
                    mBinding.outputRow18AnalogDisabled.setVisibility(View.GONE);

                } else {
                    Toast.makeText(mAppClass, "Analog Output", Toast.LENGTH_SHORT).show();
                    mBinding.outputLabelOs.setEnabled(false);
                    mBinding.outputRow2.setVisibility(View.GONE);
                    mBinding.functionModeOs.setEnabled(false);
                    mBinding.modeOs.setEnabled(false);
                    mBinding.linkOutOutputMain.setEnabled(false);
                    mBinding.minmAOutputMain.setEnabled(false);
                    mBinding.maxmAOutputMain.setEnabled(false);
                    mBinding.minValueOutputMain.setEnabled(false);
                    mBinding.maxValueOutputMain.setEnabled(false);
                    mBinding.linkOutAnalogTest.setEnabled(false);
                    mBinding.fixedValueAnalogTest.setEnabled(false);
                }
                mBinding.outputRowSave.setVisibility(View.GONE);
                break;

            case 2:
                if (outputSensorNo > 15) {
                    mBinding.functionModeOs.setEnabled(false);
                    mBinding.modeOs.setEnabled(false);
                    mBinding.outputRow2.setVisibility(View.GONE);
                    mBinding.linkOutOutputMain.setEnabled(false);
                    mBinding.minmAOutputMain.setEnabled(false);
                    mBinding.maxmAOutputMain.setEnabled(false);
                }
                break;
        }
    }

    private void enableAnalogMain() {
        mBinding.setFunctionMode(lAnalogMain);
        mBinding.linkOutAnalogOsATXT.setAdapter(getAdapter(inputAnalogSensors));
    }

    private void enableAnalogTest() {
        mBinding.setFunctionMode(lAnalogTest);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(2).toString());
        mBinding.linkOutAnalogTestTie.setAdapter(getAdapter(inputAnalogSensors));
        mBinding.modeOsATXT.setAdapter(getAdapter(modeAnalog));
    }

    private void enableAnalogDisable() {
        mBinding.setFunctionMode(lAnalogDisable);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeAnalog));
    }

    /*Save*/
    private void save(View view) {
        if (commonValidation()) {
            switch (currentFunctionMode) {
                case "Inhibitor":
                    switch (getPosition(0, toString(mBinding.modeOsATXT), modeInhibitor)) {
                        case "0":
                            if (validation()) {
                                sendContinuous();
                            }
                            break;
                        case "1":
                            if (validation1()) {
                                sendBleedBlow();
                            }
                            break;
                        case "2":
                            if (validation2()) {
                                sendWaterMeter();
                            }
                            break;
                    }
                    break;

                case "Sensor":
                    switch (getPosition(0, toString(mBinding.modeOsATXT), modeSensor)) {
                        case "0":
                            if (validation3()) {
                                sendOnOFf();
                            }

                            break;
                        case "1":
                            if (validation4()) {
                                sendPID();
                            }
                            break;
                        case "2":
                            if (validation5()) {
                                sendFuzzy();
                            }
                            break;
                    }
                    break;

                case "Analog":
                    switch (getPosition(0, toString(mBinding.modeOsATXT), modeAnalog)) {
                        case "0":
                            sendAnalogDisable();
                            break;
                        case "1":
                        case "3":
                        case "4":
                            if (validation6()) {
                                sendAnalogValue();
                            }
                            break;
                        case "2":
                            if (validation7())
                                sendAnalogTest();
                            break;
                    }
                    break;
            }
        }
    }

    /*AnalogTest*/
    private void sendAnalogTest() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR + toString(2, outputSensorNo) + SPILT_CHAR +
                analogMode + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeAnalog) + SPILT_CHAR +
                getPosition(2, toString(mBinding.linkOutAnalogTestTie), inputAnalogSensors) + SPILT_CHAR +
                toString(4, mBinding.fixedValueTie));
    }

    /*AnalogDisable*/
    private void sendAnalogDisable() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR + toString(2, outputSensorNo) + SPILT_CHAR +
                analogMode + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeAnalog) + SPILT_CHAR + "00");
    }

    /*analogValue*/
    private void sendAnalogValue() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR + toString(2, outputSensorNo) + SPILT_CHAR +
                analogMode + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeAnalog) + SPILT_CHAR +
                getPosition(2, toString(mBinding.linkOutAnalogOsATXT), inputAnalogSensors) + SPILT_CHAR +
                toString(6, mBinding.minmAAnalogOsATXT) + SPILT_CHAR +
                toString(6, mBinding.maxmAAnalogOsATXT) + SPILT_CHAR +
                toString(6, mBinding.minValueAnalogOsATXT) + SPILT_CHAR +
                toString(6, mBinding.maxValueAnalogOsATXT));
    }

    /*fuzzy*/
    private void sendFuzzy() {
        // Still in Development
    }

    /*PID*/
    private void sendPID() {
        // Sensor - PID
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR + toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                getPosition(2, toString(mBinding.linkInputPidOsATXT), inputAnalogSensors) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeSensor) + SPILT_CHAR +
                toString(6, mBinding.setPointPidOsATXT) + SPILT_CHAR +
                toString(6, mBinding.gainPidOsATXT) + SPILT_CHAR +
                toString(2, mBinding.integeralPidOsATXT) + SPILT_CHAR +
                toString(2, mBinding.derivativePidOsATXT) + SPILT_CHAR +
                toString(2, mBinding.resetPidPidOsATXT) + SPILT_CHAR +
                toString(2, mBinding.outputMinPidOsATXT) + SPILT_CHAR +
                toString(2, mBinding.outputMaxPidOsATXT) + SPILT_CHAR +
                getPosition(0, toString(mBinding.doseTypePidOsATXT), doseTypeArr) + SPILT_CHAR +
                toString(2, mBinding.inputMinPidOsATXT) + SPILT_CHAR +
                toString(2, mBinding.inputMaxPidOsATXT) + SPILT_CHAR +
                toString(2, mBinding.lockOutDelayPidOsATXT) + SPILT_CHAR +
                toString(6, mBinding.safetyMinPidOsATXT) + SPILT_CHAR +
                toString(6, mBinding.safetyMaxPidOsATXT));
    }

    /*OnOff*/
    private void sendOnOFf() {
        // Sensor - On/Off
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR + toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                getPosition(2, toString(mBinding.linkInputOnOffOsATXT), inputAnalogSensors) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeSensor) + SPILT_CHAR +
                toString(4, mBinding.targetPPMOnOFfOsATXT) + SPILT_CHAR +
                getPosition(0, toString(mBinding.doseTypeSensorOsATXT), doseTypeArr) + SPILT_CHAR +
                toString(5, mBinding.hysteresisSensorOsATXT) + SPILT_CHAR +
                toString(3, mBinding.dutyCycleSensorOsATXT) + SPILT_CHAR +
                toString(7, mBinding.lockOutTimeDelaySensorOsATXT) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.safetyMaxSensorOsATXT) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.safetyMinSensorOsATXT));
    }

    /*WaterMeter*/
    private void sendWaterMeter() {
        // Water Meter / BioCide
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR + toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeInhibitor) + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.flowMeterInputWaterOsATXT), flowMeters)) + 1) + "") + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.bleedRelayTie), bleedRelay)) + 1) + "") + SPILT_CHAR +
                toString(4, mBinding.flowRateWaterOsATXT) + SPILT_CHAR + toString(3, mBinding.targetPPMWaterOsATXT) + SPILT_CHAR +
                toString(4, mBinding.concentrationWaterOsATXT) + SPILT_CHAR + toString(3, mBinding.GravityWaterOsATXT));
    }

    /*Bleed Blow*/
    private void sendBleedBlow() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR + toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeInhibitor) + SPILT_CHAR +
                formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.LinkbleedBleedOsATXT), bleedRelay)) + 1) + "") + SPILT_CHAR +
                toString(10, mBinding.bleedFlowBleedOsATXT) + SPILT_CHAR +
                toString(13, mBinding.flowRateBleedOsATXT) + SPILT_CHAR +
                toString(4, mBinding.targetppmBleedOsATXT) + SPILT_CHAR +
                toString(3, mBinding.concentrationBleedOsATXT) + SPILT_CHAR +
                toString(3, mBinding.specificBleedOsATXT));
    }

    /*WriteData*/
    private void sendContinuous() {
        // Con - {*1234# 0# 06# 01# Output1# 30# 30# 1# 0# 125# 322# 212*}
        mAppClass.sendPacket(this,
                DEVICE_PASSWORD + SPILT_CHAR +
                        CONN_TYPE + SPILT_CHAR +
                        WRITE_PACKET + SPILT_CHAR +
                        PCK_OUTPUT_CONFIG + SPILT_CHAR +
                        toString(2, outputSensorNo) + SPILT_CHAR +
                        getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                        toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                        formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                        formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 1) + "") + SPILT_CHAR +
                        getPosition(0, toString(mBinding.modeOsATXT), modeInhibitor) + SPILT_CHAR +
                        toString(12, mBinding.flowRateContOsATXT) + SPILT_CHAR +
                        toString(12, mBinding.doseRateContOsATXT) + SPILT_CHAR +
                        toString(4, mBinding.dosePeriodOsATXT));
        Log.e(TAG, "sendContinuous: ");
    }

    /*EnableOnOff*/
    private void enableOnOff() {
        mBinding.setFunctionMode(lSensorOnOFF);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeSensor));
        mBinding.linkInputOnOffOsATXT.setAdapter(getAdapter(inputAnalogSensors));
        mBinding.doseTypeSensorOsATXT.setAdapter(getAdapter(doseTypeArr));
        mBinding.linkInputPidOsATXT.setAdapter(getAdapter(inputAnalogSensors));
        mBinding.doseTypePidOsATXT.setAdapter(getAdapter(doseTypeArr));
    }


    void enableDisabled() {
        String[] empty = {};
        mBinding.modeOsATXT.setAdapter(getAdapter(empty));
        Log.e(TAG, "enableDisabled: ");
    }

    private void enablePID() {
        mBinding.setFunctionMode(lSensorPid);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(1).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeSensor));
        mBinding.doseTypePidOsATXT.setAdapter(getAdapter(doseTypeArr));
        mBinding.linkInputPidOsATXT.setAdapter(getAdapter(inputAnalogSensors));
    }

    private void enableFuzzy() {

    }

    private void enableWater() {
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(2).toString());
        mBinding.setFunctionMode(lInhibitorWaterFlow);
        mBinding.modeOsATXT.setAdapter(getAdapter(modeInhibitor));
        mBinding.bleedRelayTie.setAdapter(getAdapter(bleedRelay));
        mBinding.flowMeterInputWaterOsATXT.setAdapter(getAdapter(flowMeters));
    }

    private void enableBleed() {
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(1).toString());
        mBinding.setFunctionMode(lInhibitorBleed);
        mBinding.LinkbleedBleedOsATXT.setAdapter(getAdapter(bleedRelay));
        mBinding.modeOsATXT.setAdapter(getAdapter(modeInhibitor));
    }

    private void enableContinuous() {
        mBinding.setFunctionMode(lInhibitorContinuous);
    }

    private void enableInhibitorLayout() {
        currentFunctionMode = "Inhibitor";
        mBinding.setFunctionMode(lInhibitorContinuous);
        mBinding.modeOsATXT.setAdapter(getAdapter(modeInhibitor));
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeInhibitor));
    }

    private void enableSensorLayout() {
        currentFunctionMode = "Sensor";
        mBinding.setFunctionMode(lSensorOnOFF);
        mBinding.linkInputOnOffOsATXT.setAdapter(getAdapter(inputAnalogSensors));
        mBinding.doseTypeSensorOsATXT.setAdapter(getAdapter(doseTypeArr));
        mBinding.linkInputPidOsATXT.setAdapter(getAdapter(inputAnalogSensors));
        mBinding.doseTypePidOsATXT.setAdapter(getAdapter(doseTypeArr));
        mBinding.modeOsATXT.setAdapter(getAdapter(modeSensor));
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeSensor));
    }

    private void enableAnalogLayout() {
        currentFunctionMode = "Analog";
        mBinding.setFunctionMode(lAnalogDisable);
        mBinding.modeOsATXT.setAdapter(getAdapter(modeAnalog));
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeAnalog));
    }

    private String getPosition(int digit, String string, String[] strArr) {
        String j = null;
        for (int i = 0; i < strArr.length; i++) {
            if (string.equals(strArr[i])) {
                j = String.valueOf(i);
            }
        }
        return formDigits(digit, j);
    }

    private String toString(int digits, EditText editText) {
        return formDigits(digits, editText.getText().toString());
    }

    private String toString(int digits, int value) {
        return formDigits(digits, String.valueOf(value));
    }

    private String toString(TextInputEditText editText) {
        return editText.getText().toString();
    }

    private String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    private void initAdapter() {
        mBinding.interLockChannelOsATXT.setAdapter(getAdapter(interlockChannel));
        mBinding.activateChannelOsATXT.setAdapter(getAdapter(interlockChannel));
        if (outputSensorNo < 15) {
            functionMode = new String[]{"Disable", "Inhibitor", "Sensor"};
            mBinding.funtionModeOsATXT.setAdapter(getAdapter(functionMode));
        } else {
            functionMode = new String[]{"Disable", "Analog"};
            mBinding.funtionModeOsATXT.setAdapter(getAdapter(functionMode));
        }
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_OUTPUT_CONFIG + SPILT_CHAR + formDigits(2, outputSensorNo + ""));
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleResponse(data.split("\\*")[1].split("\\$"));
        }
    }

    private void handleResponse(String[] splitData) {
        // Read - Inhibitor -  {*1$ 06# 0$ 01$ Output1$ 30$ 30$ 1$ 0$ 125$ 322$ 212*}
        // Read - BleedBlow -  {*1$ 06# 0$ 03$ Output3$ 30$ 30$ 1$ 1$ 01$ 01$ 23$ 52$ 232$ 10$ 52*}
        // Read - Water/Flow -
        if (splitData[1].equals(PCK_OUTPUT_CONFIG)) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.outputLabelOsEDT.setText(splitData[5]);
                    mBinding.interLockChannelOsATXT.setText(mBinding.interLockChannelOsATXT.getAdapter().getItem(Integer.parseInt(splitData[6]) - 1).toString());
                    mBinding.activateChannelOsATXT.setText(mBinding.interLockChannelOsATXT.getAdapter().getItem(Integer.parseInt(splitData[7]) - 1).toString());
                    if (outputSensorNo > 14) {
                        if (splitData[4].equalsIgnoreCase("3")) {
                            mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(1).toString());
                        } else {
                            mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                        }
                    } else {
                        mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                    }
                    switch (splitData[4]) {
                        case "0": // Disable
                            // FIXME: 05-08-2021 TODO
                            break;
                        case "1": // Inhibitor
                            enableInhibitorLayout();
                            if (splitData[8].equals("0")) {// Continuious
                                enableContinuous();
                                mBinding.flowRateContOsATXT.setText(splitData[9]);
                                mBinding.doseRateContOsATXT.setText(splitData[10]);
                                mBinding.dosePeriodOsATXT.setText(splitData[11]);
                            } else if (splitData[8].equals("1")) { // Bleed/Blow
                                enableBleed();
                                mBinding.LinkbleedBleedOsATXT.setText(mBinding.LinkbleedBleedOsATXT.getAdapter().getItem(Integer.parseInt(splitData[9]) - 1).toString());
                                mBinding.bleedFlowBleedOsATXT.setText(splitData[10]);
                                mBinding.flowRateBleedOsATXT.setText(splitData[11]);
                                mBinding.targetppmBleedOsATXT.setText(splitData[12]);
                                mBinding.concentrationBleedOsATXT.setText(splitData[13]);
                                mBinding.specificBleedOsATXT.setText(splitData[14]);
                                mBinding.LinkbleedBleedOsATXT.setAdapter(getAdapter(bleedRelay));
                            } else if (splitData[8].equals("2")) { // Water/Meter
                                enableWater();
                                mBinding.flowMeterInputWaterOsATXT.setText(mBinding.flowMeterInputWaterOsATXT.getAdapter().getItem(Integer.parseInt(splitData[9]) - 1).toString());
                                mBinding.bleedRelayTie.setText(mBinding.bleedRelayTie.getAdapter().getItem(Integer.parseInt(splitData[10]) - 1).toString());
                                mBinding.flowRateWaterOsATXT.setText(splitData[11]);
                                mBinding.targetPPMWaterOsATXT.setText(splitData[12]);
                                mBinding.concentrationWaterOsATXT.setText(splitData[13]);
                                mBinding.GravityWaterOsATXT.setText(splitData[14]);
                                mBinding.flowMeterInputWaterOsATXT.setAdapter(getAdapter(flowMeters));
                                mBinding.bleedRelayTie.setAdapter(getAdapter(bleedRelay));
                            }
                            break;
                        case "2": // Sensor
                            enableSensorLayout();
                            if (splitData[9].equals("0")) { // On/Off
                                enableOnOff();
                                mBinding.linkInputOnOffOsATXT.setText(mBinding.linkInputOnOffOsATXT.getAdapter().getItem(Integer.parseInt(splitData[8])).toString());
                                mBinding.targetPPMOnOFfOsATXT.setText(splitData[10]);
                                mBinding.doseTypeSensorOsATXT.setText(mBinding.doseTypeSensorOsATXT.getAdapter().getItem(Integer.parseInt(splitData[11])).toString());
                                mBinding.hysteresisSensorOsATXT.setText(splitData[12]);
                                mBinding.dutyCycleSensorOsATXT.setText(splitData[13]);
                                mBinding.lockOutTimeDelaySensorOsATXT.setText(splitData[14]);
                                mBinding.safetyMaxSensorOsATXT.setText(splitData[15].substring(0, 4) + "." + splitData[15].substring(4, 6));
                                mBinding.safetyMinSensorOsATXT.setText(splitData[16].substring(0, 4) + "." + splitData[16].substring(4, 6));

                                mBinding.doseTypeSensorOsATXT.setAdapter(getAdapter(doseTypeArr));
                                mBinding.linkInputOnOffOsATXT.setAdapter(getAdapter(inputAnalogSensors));
                            } else if (splitData[9].equals("1")) { // PID
                                enablePID();
                                mBinding.linkInputPidOsATXT.setText(mBinding.linkInputPidOsATXT.getAdapter().getItem(Integer.parseInt(splitData[8])).toString());
                                mBinding.setPointPidOsATXT.setText(splitData[10]);
                                mBinding.gainPidOsATXT.setText(splitData[11]);
                                mBinding.integeralPidOsATXT.setText(splitData[12]);
                                mBinding.derivativePidOsATXT.setText(splitData[13]);
                                mBinding.resetPidPidOsATXT.setText(splitData[14]);
                                mBinding.outputMinPidOsATXT.setText(splitData[15]);
                                mBinding.outputMaxPidOsATXT.setText(splitData[16]);
                                mBinding.doseTypePidOsATXT.setText(mBinding.doseTypePidOsATXT.getAdapter().getItem(Integer.parseInt(splitData[17])).toString());
                                mBinding.inputMinPidOsATXT.setText(splitData[18]);
                                mBinding.inputMaxPidOsATXT.setText(splitData[19]);
                                mBinding.lockOutDelayPidOsATXT.setText(splitData[20]);
                                mBinding.safetyMaxPidOsATXT.setText(splitData[21]);
                                mBinding.safetyMinPidOsATXT.setText(splitData[22]);

                                mBinding.doseTypePidOsATXT.setAdapter(getAdapter(doseTypeArr));
                                mBinding.linkInputPidOsATXT.setAdapter(getAdapter(inputAnalogSensors));
                            } else if (splitData[9].equals("2")) {
                                enableFuzzy();
                                // FIXME: 05-08-2021 Still Development
                            }
                            break;

                        case "3": // Analog
                            enableAnalogLayout();
                            if (splitData[8].equals("0")) {
                                enableAnalogDisable();
                            } else if (splitData[8].equals("2")) {
                                enableAnalogTest();
                                mBinding.linkOutAnalogTestTie.setText(mBinding.linkOutAnalogTestTie.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());
                                mBinding.fixedValueTie.setText(splitData[10]);
                                mBinding.linkOutAnalogTestTie.setAdapter(getAdapter(inputAnalogSensors));
                            } else {
                                enableAnalogMain();
                                mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(Integer.parseInt(splitData[8])).toString());
                                mBinding.linkOutAnalogOsATXT.setText(mBinding.linkOutAnalogOsATXT.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());
                                mBinding.minmAAnalogOsATXT.setText(splitData[10]);
                                mBinding.maxmAAnalogOsATXT.setText(splitData[11]);
                                mBinding.minValueAnalogOsATXT.setText(splitData[12]);
                                mBinding.maxValueAnalogOsATXT.setText(splitData[13]);
                                mBinding.linkOutAnalogOsATXT.setAdapter(getAdapter(inputAnalogSensors));
                            }
                            mBinding.modeOsATXT.setAdapter(getAdapter(modeAnalog));
                            break;
                    }
                    initAdapter();
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.readFailed));
                }

            } else if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                    outputConfigurationEntity();
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            }
        } else {
            Log.e(TAG, "handleResponse: ");
        }
        checkUser();
    }

    private Boolean isEmpty(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            editText.requestFocus();
            return true;
        }
        return false;
    }

    private String toStringSplit(int digits, int digitPoint, EditText editText) {
        if (editText.getText().toString().split("\\.").length == 1) {
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
    }

    boolean validation() {
        if (isEmpty(mBinding.flowRateContOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Flow Rate cannot be Empty");
            return false;
        } else if (validDecimalField(mBinding.flowRateContOsATXT, 9, 2) == 1) {
            mBinding.flowRateContOsATXT.setError("Invalid value format, Required format is XXXXXXXXX.XX");
            return false;
        } else if (isEmpty(mBinding.doseRateContOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Dose Rate cannot be Empty");
            return false;
        } else if (validDecimalField(mBinding.flowRateContOsATXT, 9, 2) == 1) {
            mBinding.doseRateContOsATXT.setError("Invalid value format, Required format is XXXXXXXXX.XX");
            return false;

        } else if (isEmpty(mBinding.dosePeriodOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Dose Periods cannot be Empty");
            return false;
        }
        return true;
    }


    private boolean validation1() {
        if (isEmpty(mBinding.flowRateBleedOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Pump Flow Rate cannot be Empty");
            return false;
        } else if (!toString(mBinding.flowRateBleedOsATXT).contains(".")) {
            mBinding.flowRateBleedOsATXT.setError("Required Decimal Value");
            return false;

        } else if (isEmpty(mBinding.targetppmBleedOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Target PPM cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.concentrationBleedOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Concentration cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.LinkbleedBleedOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Choose any relay output to link");
            return false;
        } else if (isEmpty(mBinding.specificBleedOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Specific Gravity cannot be Empty");
            return false;

        } else if (isEmpty(mBinding.bleedFlowBleedOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Bleed Flow Rate cannot be Empty");
            return false;
        } else if (toString(mBinding.bleedFlowBleedOsATXT).contains(".")) {
            mBinding.bleedFlowBleedOsATXT.setError("Required Decimal Value");
            return false;
        }

        return true;
    }

    private boolean validation2() {
        if (isEmpty(mBinding.flowRateWaterOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Flow Meter cannot be Empty");
            return false;
        } else if (!toString(mBinding.flowRateWaterOsATXT).contains(".")) {
            mBinding.flowRateWaterOsATXT.setError("Required Decimal Value");
            return false;

        } else if (isEmpty(mBinding.targetPPMWaterOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Target PPM cannot be Empty");
            return false;
        } else if (!toString(mBinding.targetPPMWaterOsATXT).contains(".")) {
            mBinding.targetPPMWaterOsATXT.setError("Required Decimal Value");
            return false;
        } else if (isEmpty(mBinding.concentrationWaterOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Concentration cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.bleedRelayTie)) {
            mAppClass.showSnackBar(getContext(), "Choose any relay output to link");
            return false;
        } else if (isEmpty(mBinding.GravityWaterOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Specific Gravity  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.flowMeterInputWaterOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Please select Flow Meter Input");
            return false;
        }
        return true;
    }

    private boolean validation3() {
        if (isEmpty(mBinding.targetPPMOnOFfOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Set Point cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.doseTypeSensorOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Dose Type cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.hysteresisSensorOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Hysteresis cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.dutyCycleSensorOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Duty Cycle cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.safetyMinSensorOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Safety Min cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.safetyMaxSensorOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Safety max  cannot be Empty");
            return false;
        } else if ((!mBinding.safetyMinSensorOsATXT.getText().toString().contains(".") && mBinding.safetyMinSensorOsATXT.getText().toString().length() > 4)
                || (mBinding.safetyMinSensorOsATXT.getText().toString().contains(".") && findDecimal(mBinding.safetyMinSensorOsATXT) == 1)) {
            mAppClass.showSnackBar(getContext(), "Safety Min decimal format like XXXX.XX");
            return false;
        } else if ((!mBinding.safetyMaxSensorOsATXT.getText().toString().contains(".") && mBinding.safetyMaxSensorOsATXT.getText().toString().length() > 4)
                || (mBinding.safetyMaxSensorOsATXT.getText().toString().contains(".") && findDecimal(mBinding.safetyMaxSensorOsATXT) == 1)) {
            mAppClass.showSnackBar(getContext(), "Safety Max decimal format like XXXX.XX");
            return false;
        } else if (isEmpty(mBinding.lockOutTimeDelaySensorOsATXT)) {
            mAppClass.showSnackBar(getContext(), "LockOut Time Delay cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.linkInputOnOffOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Link Input Sensor cannot be Empty");
            return false;
        }
        return true;
    }

    private boolean validation4() {
        if (isEmpty(mBinding.setPointPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Set Point cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.gainPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Gain cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.integeralPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Integral Time cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.derivativePidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Derivative Time cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.inputMinPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Input min  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.inputMaxPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Input Max  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.outputMinPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Output Min  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.outputMaxPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Output Max cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.safetyMinPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Safety Min cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.safetyMaxPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Safety Max cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.resetPidPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Reset Pid Integral cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.lockOutDelayPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Lockout Delay  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.doseTypePidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Please select Dose Type");
            return false;
        } else if (isEmpty(mBinding.linkInputPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Please select Link Input Sensor");
            return false;
        }

        return true;
    }

    private boolean validation5() {
        return true;
    }

    private boolean validation6() {
        if (isEmpty(mBinding.minmAAnalogOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Min Analog cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.maxmAAnalogOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Max Analog cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.minValueAnalogOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Min Value cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.maxValueAnalogOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Max Value  cannot be Empty");
            return false;
        }

        return true;
    }

    private boolean validation7() {
        if (isEmpty(mBinding.fixedValueTie)) {
            mAppClass.showSnackBar(getContext(), "Fixed value cannot be Empty");
            return false;
        }

        return true;
    }


    private boolean commonValidation() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output Label  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.interLockChannelOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Please select the Interlock Channel");
            return false;
        } else if (isEmpty(mBinding.activateChannelOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Please select the Activate Channel");
            return false;
        } else if (isEmpty(mBinding.modeOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Please select the Mode");
            return false;
        }
        return true;
    }

    public void updateToDb(List<OutputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        OutputConfigurationDao dao = db.outputConfigurationDao();
        dao.insert(entryList.toArray(new OutputConfigurationEntity[0]));
    }

    public void outputConfigurationEntity() {
        OutputConfigurationEntity entityUpdate = new OutputConfigurationEntity
                (outputSensorNo, toString(0, mBinding.outputLabelOsEDT),
                        mBinding.funtionModeOsATXT.getText().toString(),
                        mBinding.modeOsATXT.getText().toString());
        List<OutputConfigurationEntity> entryListUpdate = new ArrayList<>();
        entryListUpdate.add(entityUpdate);
        updateToDb(entryListUpdate);


    }
}