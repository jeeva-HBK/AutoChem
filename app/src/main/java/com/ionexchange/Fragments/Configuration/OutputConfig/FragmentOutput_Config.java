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
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
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
import static com.ionexchange.Others.ApplicationClass.flowMeterTypeArr;
import static com.ionexchange.Others.ApplicationClass.flowMeters;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.functionMode;
import static com.ionexchange.Others.ApplicationClass.inputAnalogSensors;
import static com.ionexchange.Others.ApplicationClass.interlockChannel;
import static com.ionexchange.Others.ApplicationClass.modeAnalog;
import static com.ionexchange.Others.ApplicationClass.modeInhibitor;
import static com.ionexchange.Others.ApplicationClass.modeSensor;
import static com.ionexchange.Others.ApplicationClass.resetFlowTotalArr;
import static com.ionexchange.Others.ApplicationClass.userType;
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
    ApplicationClass mAppClass;
    int outputSensorNo;
    String[] bleedArr, sensorInputArr;
    String lInhibitorContinuous = "layoutInhibitorContinuous", lInhibitorBleed = "layoutInhibitorBleedDown", lInhibitorWaterFlow = "layoutInhibitorWaterFlow",
            lSensorOnOFF = "layoutSensorOnOff", lSensorPid = "layoutSensorPID", lAnalogMain = "layoutAnalogMain", lAnalogTest = "layoutAnalogTest", lAnalogDisable = "layoutAnalogDisable",
            currentFunctionMode = "", analogMode = "3";
    private static final String TAG = "FragmentOutput_Config";

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
        outputSensorNo = getArguments().getInt("sensorInputNo");
        FragmentOutputSettings_Config.hideToolbar();
        initAdapter();
        mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(1).toString());
        if (outputSensorNo < 15) {
            enableInhibitorLayout();
        } else {
            mBinding.outputLabelOs.setVisibility(View.GONE);
            mBinding.outputRow2.setVisibility(View.GONE);
            enableAnalogLayout();
        }
        bleedArr = getBleedArray();
        sensorInputArr = getSensorInputArray();
        initAdapter();
        mBinding.funtionModeOsATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                mBinding.outputRow2.setVisibility(View.VISIBLE);
                mBinding.modeOs.setEnabled(true);
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

                    case "Disable":

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

        mBinding.backArrowOsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppClass.popStackBack(getActivity());
            }
        });

        mBinding.saveFabOutput.setOnClickListener(this::save);
        checkUser();
    }

    private String[] getSensorInputArray() {
        WaterTreatmentDb DB = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao DAO = DB.inputConfigurationDao();
        List<InputConfigurationEntity> inputNameList = DAO.getInputHardWareNoConfigurationEntityList(1, 13);
        String[] inputNames = new String[13];
        if (!inputNameList.isEmpty()) {
            for (int i = 0; i < inputNameList.size(); i++) {
                inputNames[i] = "Input- " + inputNameList.get(i).getHardwareNo() + " (" + inputNameList.get(i).getInputLabel() + ")";
            }
        }
        if (inputNames.length == 0) {
            inputNames = inputAnalogSensors;
        }
        return inputNames;
    }

    private void checkUser() {
        switch (userType) {
            case 1:
                if (outputSensorNo < 15) {
                    mBinding.outputLabelOs.setEnabled(false);
                    mBinding.functionModeOs.setEnabled(false);
                    mBinding.modeOs.setEnabled(false);
                    mBinding.outputInterLockChannelOs.setEnabled(false);
                    mBinding.outputActivateChannelOs.setEnabled(false);
                    mBinding.setFunctionMode("BASIC");
                    mBinding.outputRow3InhibitorCont.setVisibility(View.GONE);
                    mBinding.bleedOutputRow4Osc.setVisibility(View.GONE);
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
                    mBinding.outputLabelOs.setEnabled(false);
                    mBinding.outputRow2.setVisibility(View.GONE);
                    mBinding.functionModeOs.setEnabled(false);
                    mBinding.modeOs.setEnabled(false);
                    mBinding.analogLinkInputTilOsc.setEnabled(false);
                    mBinding.analogMinMaEdtOsc.setEnabled(false);
                    mBinding.analogMinMaDeciOsc.setEnabled(false);
                    mBinding.analogMaxMaEdtOsc.setEnabled(false);
                    mBinding.analogMaxMaDeciOsc.setEnabled(false);
                    mBinding.analogMinValueEdtOsc.setEnabled(false);
                    mBinding.analogMinValueEdtOsc.setEnabled(false);
                    mBinding.analogMaxValueEdtOsc.setEnabled(false);
                    mBinding.analogMaxValueDeciOsc.setEnabled(false);
                    mBinding.testLinkInputRelayTilOsc.setEnabled(false);
                    mBinding.analogFixedValueEdtOsc.setEnabled(false);
                    mBinding.analogFixedValueDeciOsc.setEnabled(false);
                }
                mBinding.outputRowSave.setVisibility(View.GONE);
                break;

            case 2:
                if (outputSensorNo > 15) {
                    mBinding.functionModeOs.setEnabled(false);
                    mBinding.modeOs.setEnabled(false);
                    mBinding.outputRow2.setVisibility(View.GONE);
                    mBinding.analogLinkInputTilOsc.setEnabled(false);
                    mBinding.analogMinMaEdtOsc.setEnabled(false);
                    mBinding.analogMinMaDeciOsc.setEnabled(false);
                    mBinding.analogMaxMaEdtOsc.setEnabled(false);
                    mBinding.analogMaxMaDeciOsc.setEnabled(false);
                } else {
                    mBinding.outputLabelOs.setEnabled(false);
                    // mBinding.functionModeOs.setEnabled(false);
                    //  mBinding.modeOs.setEnabled(false);
                    mBinding.outputInterLockChannelOs.setEnabled(false);
                    mBinding.outputActivateChannelOs.setEnabled(false);

                    // Inhibitor
                    mBinding.contFlowRateTilOsc.setEnabled(false);
                    mBinding.contFlowRateDeciOsc.setEnabled(false);
                    mBinding.contDoseRateTilOsc.setEnabled(false);
                    mBinding.contDoseRateDeciOsc.setEnabled(false);
                    mBinding.contDosePeriodTilOsc.setEnabled(false);
                    mBinding.bleedPumpFlowRateTilOsc.setEnabled(false);
                    mBinding.bleedPumpFlowRateDeciOsc.setEnabled(false);
                    mBinding.bleedBleedFlowRateTilOsc.setEnabled(false);
                    mBinding.bleedBleedFlowrateDeciOsc.setEnabled(false);
                    mBinding.bleedTargetPPMTilOsc.setEnabled(false);
                    mBinding.bleedTargetPPMDeciOsc.setEnabled(false);
                    mBinding.bleedLinkBleedRelayTilOsc.setEnabled(false);
                    mBinding.bleedSpecificGravityTilOsc.setEnabled(false);
                    mBinding.bleedSpecificGravityDeciOsc.setEnabled(false);
                    mBinding.bleedConcentrationTilOsc.setEnabled(false);
                    mBinding.waterFlowMeterTypeTilOsc.setEnabled(false);
                    mBinding.waterPumpFlowRateTilOsc.setEnabled(false);
                    mBinding.waterPumpFlowRateDeciOsc.setEnabled(false);
                    mBinding.waterTargetPPMTilOsc.setEnabled(false);
                    mBinding.waterTargetPPMDeciOsc.setEnabled(false);
                    mBinding.waterConcentrationTilOsc.setEnabled(false);
                    mBinding.waterBleedRelayTilOsc.setEnabled(false);
                    mBinding.waterSpecificGravityTilOsc.setEnabled(false);
                    mBinding.waterSpecificGravityDeciOsc.setEnabled(false);
                    mBinding.waterFlowMeterInputTilOsc.setEnabled(false);

                    //sensor
                    mBinding.sensorLinkInputSensorTilOsc.setEnabled(false);
                    mBinding.sensorSetPointTilOsc.setEnabled(false);
                    mBinding.sensorSetPointDeciOsc.setEnabled(false);
                    mBinding.sensorDoseTypeTilOsc.setEnabled(false);
                    mBinding.sensorDutyCycleTilOsc.setEnabled(false);
                    mBinding.sensorSafetyMinTilOsc.setEnabled(false);
                    mBinding.sensorSafetyMaxTilOsc.setEnabled(false);
                    mBinding.sensorLockoutTimeDelayTilOsc.setEnabled(false);
                    mBinding.sensorHysteresisRootOsc.setEnabled(false);
                    mBinding.pidSetPointTilOsc.setEnabled(false);
                    mBinding.pidSetPointDeciOsc.setEnabled(false);
                    mBinding.pidGainTilOsc.setEnabled(false);
                    mBinding.pidGainDeciOsc.setEnabled(false);
                    mBinding.pidIntegeralTimeTilOsc.setEnabled(false);
                    mBinding.pidIntegeralTimeDeciOsc.setEnabled(false);
                    mBinding.pidDerivativeTimeTilOsc.setEnabled(false);
                    mBinding.pidDerivativeTimeDeciOsc.setEnabled(false);
                    mBinding.pidInputMinTilOsc.setEnabled(false);
                    mBinding.pidInputMaxTilOsc.setEnabled(false);
                    mBinding.pidMinOutputTilOsc.setEnabled(false);
                    mBinding.pidMaxOutputTilOsc.setEnabled(false);
                    mBinding.pidSafetyMinTilOsc.setEnabled(false);
                    mBinding.pidSafetyMaxTilOsc.setEnabled(false);
                    mBinding.pidResetPidTilOsc.setEnabled(false);
                    mBinding.pidLockoutDelayTilOsc.setEnabled(false);
                    mBinding.pidDoseTypeTilOsc.setEnabled(false);
                    mBinding.pidLinkInputTilOsc.setEnabled(false);
                }
                break;
        }
    }

    private void enableAnalogMain() {
        mBinding.setFunctionMode(lAnalogMain);
        mBinding.analogLinkInputAtxtOsc.setAdapter(getAdapter(sensorInputArr));
    }

    private void enableAnalogTest() {
        mBinding.setFunctionMode(lAnalogTest);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(2).toString());
        mBinding.testLinkInputRelayEdtOsc.setAdapter(getAdapter(sensorInputArr));
        mBinding.modeOsATXT.setAdapter(getAdapter(modeAnalog));
    }

    private void enableAnalogDisable() {
        mBinding.setFunctionMode(lAnalogDisable);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.modeOs.setEnabled(true);
        mBinding.modeOsATXT.setAdapter(getAdapter(modeAnalog));
    }

    /*Save*/
    private void save(View view) {
        if (outputSensorNo < 15) {
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
                    case "Disabled":
                        sendRelayDisable();
                        break;
                }
            }

        } else {
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
        }

    }

    /*Relay_Disabled*/
    private void sendRelayDisable() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                "0" + SPILT_CHAR +
                toString(mBinding.outputLabelOsEDT));
    }

    /*AnalogTest*/
    private void sendAnalogTest() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                analogMode + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeAnalog) + SPILT_CHAR +
                getPosition(2, toString(mBinding.testLinkInputRelayEdtOsc), sensorInputArr) + SPILT_CHAR +
                getDecimalValue(mBinding.analogFixedValueEdtOsc, 2, mBinding.analogFixedValueDeciOsc, 2));
    }

    /*AnalogDisable*/
    private void sendAnalogDisable() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                analogMode + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeAnalog));
    }

    /*analogValue*/
    private void sendAnalogValue() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                analogMode + SPILT_CHAR +
                getPosition(1, toString(mBinding.modeOsATXT), modeAnalog) + SPILT_CHAR +
                getPosition(2, toString(mBinding.analogLinkInputAtxtOsc), sensorInputArr) + SPILT_CHAR +
                getDecimalValue(mBinding.analogMinMaEdtOsc, 2, mBinding.analogMinMaDeciOsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.analogMaxMaEdtOsc, 2, mBinding.analogMaxMaDeciOsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.analogMinValueEdtOsc, 2, mBinding.analogMinValueDeciOsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.analogMaxValueEdtOsc, 2, mBinding.analogMaxValueDeciOsc, 2));
    }

    /*fuzzy*/
    private void sendFuzzy() {
        // Still in Development
    }

    /*PID*/
    private void sendPID() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                getPosition(2, toString(mBinding.pidLinkInputAtxtOsc), sensorInputArr) + SPILT_CHAR +
                getPosition(1, toString(mBinding.modeOsATXT), modeSensor) + SPILT_CHAR +
                getDecimalValue(mBinding.pidSetPointEdtOsc, 3, mBinding.pidSetPointDeciOsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.pidGainEdtOsc, 4, mBinding.pidGainDeciOsc, 3) + SPILT_CHAR +
                getDecimalValue(mBinding.pidIntegeralTimeEdtOsc, 4, mBinding.pidIntegeralTimeDeciOsc, 3) + SPILT_CHAR +
                getDecimalValue(mBinding.pidDerivativeTimeEdtOsc, 4, mBinding.pidDerivativeTimeDeciOsc, 3) + SPILT_CHAR +
                getPosition(1, toString(mBinding.pidResetPidAtxtOsc), resetFlowTotalArr) + SPILT_CHAR +
                toString(3, mBinding.pidMinOutputEdtOsc) + SPILT_CHAR +
                toString(3, mBinding.pidMaxOutputEdtOsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.pidDoseTypeAtxtOsc), doseTypeArr) + SPILT_CHAR +
                toString(6, mBinding.pidInputMinEdtOsc) + SPILT_CHAR +
                toString(6, mBinding.pidInputMaxEdtOsc) + SPILT_CHAR +
                toString(5, mBinding.pidLockoutDelayEdtOsc) + SPILT_CHAR +
                toString(6, mBinding.pidSafetyMaxEdtOsc) + SPILT_CHAR +
                toString(6, mBinding.pidSafetyMinEdtOsc));
    }

    /*OnOff*/
    private void sendOnOFf() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                getPosition(2, toString(mBinding.sensorLinkInputSensorAtxtOsc), sensorInputArr) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeSensor) + SPILT_CHAR +
                getDecimalValue(mBinding.sensorSetPointEdtOsc, 3, mBinding.sensorSetPointDeciOsc, 2) + SPILT_CHAR +
                getPosition(0, toString(mBinding.sensorDoseTypeAtxtOsc), doseTypeArr) + SPILT_CHAR +
                toString(5, mBinding.sensorHysteresisEdtOsc) + SPILT_CHAR +
                toString(3, mBinding.sensorDutyCycleEdtOsc) + SPILT_CHAR +
                toString(5, mBinding.sensorLockoutTimeDelayEdtOsc) + SPILT_CHAR +
                toString(6, mBinding.sensorSafetyMaxEdtOsc) + SPILT_CHAR +
                toString(6, mBinding.sensorSafetyMinEdtOsc));
    }

    /* WaterMeter */
    private void sendWaterMeter() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(1, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                getPosition(1, toString(mBinding.modeOsATXT), modeInhibitor) + SPILT_CHAR +
                getPosition(2, toString(mBinding.waterFlowMeterTypeAtxtOsc), flowMeterTypeArr) + SPILT_CHAR +
                (Integer.parseInt(getPosition(1, toString(mBinding.waterFlowMeterInputAtxtOsc), flowMeters)) + 1) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.waterBleedRelayAtxtOsc), bleedArr)) + 1) + SPILT_CHAR +
                getDecimalValue(mBinding.waterPumpFlowRateEdtOsc, 9, mBinding.waterPumpFlowRateDeciOsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.waterTargetPPMEdtOsc, 7, mBinding.waterTargetPPMDeciOsc, 2) + SPILT_CHAR +
                toString(3, mBinding.waterConcentrationEdtOsc) + SPILT_CHAR +
                getDecimalValue(mBinding.waterSpecificGravityEdtOsc, 1, mBinding.waterSpecificGravityDeciOsc, 3));
    }

    /* Bleed Blow */
    private void sendBleedBlow() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeInhibitor) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.bleedLinkBleedRelayAtxtOsc), bleedArr)) + 1) + SPILT_CHAR +
                getDecimalValue(mBinding.bleedBleedFlowRateEdtOsc, 6, mBinding.bleedBleedFlowrateDeciOsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.bleedPumpFlowRateEdtOsc, 9, mBinding.bleedPumpFlowRateDeciOsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.bleedTargetPPMEdtOsc, 7, mBinding.bleedTargetPPMDeciOsc, 2) + SPILT_CHAR +
                toString(3, mBinding.bleedConcentrationEdtOsc) + SPILT_CHAR +
                getDecimalValue(mBinding.bleedSpecificGravityEdtOsc, 1, mBinding.bleedSpecificGravityDeciOsc, 3));
    }

    /* Continuous */
    private void sendContinuous() {
        mAppClass.sendPacket(this,
                DEVICE_PASSWORD + SPILT_CHAR +
                        CONN_TYPE + SPILT_CHAR +
                        WRITE_PACKET + SPILT_CHAR +
                        PCK_OUTPUT_CONFIG + SPILT_CHAR +
                        toString(2, outputSensorNo) + SPILT_CHAR +
                        getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                        toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                        (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                        (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                        getPosition(0, toString(mBinding.modeOsATXT), modeInhibitor) + SPILT_CHAR +
                        getDecimalValue(mBinding.contFlowRateEdtOsc, 9, mBinding.contFlowRateDeciOsc, 2) + SPILT_CHAR +
                        getDecimalValue(mBinding.contDoseRateEdtOsc, 9, mBinding.contDoseRateDeciOsc, 2) + SPILT_CHAR +
                        toString(4, mBinding.contDosePeriodEdtOsc));
    }

    private void enableOnOff() {
        mBinding.modeOs.setEnabled(true);
        mBinding.setFunctionMode(lSensorOnOFF);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeSensor));
        mBinding.sensorLinkInputSensorAtxtOsc.setAdapter(getAdapter(sensorInputArr));
        mBinding.sensorDoseTypeAtxtOsc.setAdapter(getAdapter(doseTypeArr));
        mBinding.pidLinkInputAtxtOsc.setAdapter(getAdapter(sensorInputArr));
        mBinding.pidDoseTypeAtxtOsc.setAdapter(getAdapter(doseTypeArr));
    }


    void enableDisabled() {
        currentFunctionMode = "Disabled";
        mBinding.modeOs.setEnabled(false);
        mBinding.modeOsATXT.setText("");
        mBinding.setFunctionMode("Disabled");
        mBinding.outputRow2.setVisibility(View.GONE);
        Log.e(TAG, "enableDisabled: ");
    }

    private void enablePID() {
        mBinding.modeOs.setEnabled(true);
        mBinding.setFunctionMode(lSensorPid);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(1).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeSensor));
        mBinding.pidDoseTypeAtxtOsc.setAdapter(getAdapter(doseTypeArr));
        mBinding.pidLinkInputAtxtOsc.setAdapter(getAdapter(sensorInputArr));
        mBinding.pidResetPidAtxtOsc.setAdapter(getAdapter(resetFlowTotalArr));
    }

    private void enableFuzzy() {
        mAppClass.showSnackBar(getContext(), "Still in Development");
    }

    private void enableWater() {
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(2).toString());
        mBinding.setFunctionMode(lInhibitorWaterFlow);
        mBinding.modeOs.setEnabled(true);
        mBinding.modeOsATXT.setAdapter(getAdapter(modeInhibitor));
        mBinding.waterBleedRelayAtxtOsc.setAdapter(getAdapter(bleedArr));
        mBinding.waterFlowMeterTypeAtxtOsc.setAdapter(getAdapter(flowMeterTypeArr));
        mBinding.waterFlowMeterInputAtxtOsc.setAdapter(getAdapter(flowMeters));
    }


    private void enableBleed() {
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(1).toString());
        mBinding.setFunctionMode(lInhibitorBleed);
        mBinding.modeOs.setEnabled(true);
        mBinding.bleedLinkBleedRelayAtxtOsc.setAdapter(getAdapter(bleedArr));
        mBinding.modeOsATXT.setAdapter(getAdapter(modeInhibitor));
    }

    String[] getBleedArray() {
        WaterTreatmentDb DB = WaterTreatmentDb.getDatabase(getContext());
        OutputConfigurationDao DAO = DB.outputConfigurationDao();
        List<OutputConfigurationEntity> outputNameList = DAO.getOutputHardWareNoConfigurationEntityList(1, 14);
        String[] outputNames = new String[14];
        if (!outputNameList.isEmpty()) {
            for (int i = 0; i < outputNameList.size(); i++) {
                outputNames[i] = "Output- " + outputNameList.get(i).getOutputHardwareNo() + " (" + outputNameList.get(i).getOutputLabel() + ")";
            }
        }
        if (outputNames.length == 0) {
            outputNames = bleedRelay;
        }
        return outputNames;
    }

    private void enableContinuous() {
        mBinding.setFunctionMode(lInhibitorContinuous);
        mBinding.modeOs.setEnabled(true);
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
        mBinding.sensorLinkInputSensorAtxtOsc.setAdapter(getAdapter(sensorInputArr));
        mBinding.sensorDoseTypeAtxtOsc.setAdapter(getAdapter(doseTypeArr));
        mBinding.pidLinkInputAtxtOsc.setAdapter(getAdapter(sensorInputArr));
        mBinding.pidDoseTypeAtxtOsc.setAdapter(getAdapter(doseTypeArr));
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

    String getDecimalValue(TextInputEditText prefixEdittext, int prefixDigit, EditText suffixEdittext, int suffixDigit) {
        return toString(prefixDigit, prefixEdittext) + "." + toString(suffixDigit, suffixEdittext);
    }

    String getPlusMinusValue(ToggleButton toggleButton, TextInputEditText prefixEdt, int prefixDigit, EditText suffixEdt, int suffixDigit) {
        return (toggleButton.isChecked() ? "+" : "-") + toString(prefixDigit, prefixEdt) + "." + toString(suffixDigit, suffixEdt);
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
        if (splitData[1].equals(PCK_OUTPUT_CONFIG)) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    if (outputSensorNo > 14) {
                        //  if (splitData[4].equalsIgnoreCase("3")) {
                        mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(1).toString());
                        //   } else {
                        //        mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                        //   }
                    } else {
                        mBinding.outputLabelOsEDT.setText(splitData[5]);
                        mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                        if (!splitData[4].equals("0")) {
                            mBinding.interLockChannelOsATXT.setText(mBinding.interLockChannelOsATXT.getAdapter().getItem(Integer.parseInt(splitData[6]) - 30).toString());
                            mBinding.activateChannelOsATXT.setText(mBinding.interLockChannelOsATXT.getAdapter().getItem(Integer.parseInt(splitData[7]) - 30).toString());
                        }
                    }
                    switch (splitData[4]) {
                        case "0": // Disable
                            enableDisabled();
                            break;
                        case "1": // Inhibitor
                            enableInhibitorLayout();
                            if (splitData[8].equals("0")) { // Continuious
                                enableContinuous();
                                mBinding.contFlowRateEdtOsc.setText(splitData[9].substring(0, 9));
                                mBinding.contFlowRateDeciOsc.setText(splitData[9].substring(10, 12));

                                mBinding.contDoseRateEdtOsc.setText(splitData[10].substring(0, 9));
                                mBinding.contDoseRateDeciOsc.setText(splitData[10].substring(10, 12));

                                mBinding.contDosePeriodEdtOsc.setText(splitData[11]);

                            } else if (splitData[8].equals("1")) { // Bleed/Blow
                                enableBleed();
                                mBinding.bleedLinkBleedRelayAtxtOsc.setText(mBinding.bleedLinkBleedRelayAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[9]) - 1).toString());

                                mBinding.bleedBleedFlowRateEdtOsc.setText(splitData[10].substring(0, 6));
                                mBinding.bleedBleedFlowrateDeciOsc.setText(splitData[10].substring(7, 9));

                                mBinding.bleedPumpFlowRateEdtOsc.setText(splitData[11].substring(0, 9));
                                mBinding.bleedPumpFlowRateDeciOsc.setText(splitData[11].substring(10, 12));

                                mBinding.bleedTargetPPMEdtOsc.setText(splitData[12].substring(0, 7));
                                mBinding.bleedTargetPPMDeciOsc.setText(splitData[12].substring(8, 10));

                                mBinding.bleedConcentrationEdtOsc.setText(splitData[13]);

                                mBinding.bleedSpecificGravityEdtOsc.setText(splitData[14].substring(0, 1));
                                mBinding.bleedSpecificGravityDeciOsc.setText(splitData[14].substring(2, 5));

                                mBinding.bleedLinkBleedRelayAtxtOsc.setAdapter(getAdapter(bleedArr));

                            } else if (splitData[8].equals("2")) { // Water/Meter
                                enableWater();
                                mBinding.waterFlowMeterTypeAtxtOsc.setText(mBinding.waterFlowMeterTypeAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());
                                mBinding.waterFlowMeterInputAtxtOsc.setText(mBinding.waterFlowMeterInputAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[10]) - 1).toString());
                                mBinding.waterBleedRelayAtxtOsc.setText(mBinding.waterBleedRelayAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[11]) - 1).toString());

                                mBinding.waterPumpFlowRateEdtOsc.setText(splitData[12].substring(0, 9));
                                mBinding.waterPumpFlowRateDeciOsc.setText(splitData[12].substring(10, 12));

                                mBinding.waterTargetPPMEdtOsc.setText(splitData[13].substring(0, 7));
                                mBinding.waterTargetPPMDeciOsc.setText(splitData[13].substring(8, 10));

                                mBinding.waterConcentrationEdtOsc.setText(splitData[14]);

                                mBinding.waterSpecificGravityEdtOsc.setText(splitData[15].substring(0, 1));
                                mBinding.waterSpecificGravityDeciOsc.setText(splitData[15].substring(2, 5));

                                mBinding.waterFlowMeterInputAtxtOsc.setAdapter(getAdapter(flowMeters));
                                mBinding.waterBleedRelayAtxtOsc.setAdapter(getAdapter(bleedArr));
                                mBinding.waterFlowMeterTypeAtxtOsc.setAdapter(getAdapter(flowMeterTypeArr));
                            }
                            break;
                        case "2": // Sensor
                            enableSensorLayout();
                            if (splitData[9].equals("0")) { // On/Off
                                enableOnOff();
                                mBinding.sensorLinkInputSensorAtxtOsc.setText(mBinding.sensorLinkInputSensorAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[8])).toString());
                                mBinding.sensorSetPointEdtOsc.setText(splitData[10].subSequence(0, 3));
                                mBinding.sensorSetPointDeciOsc.setText(splitData[10].subSequence(4, 6));
                                mBinding.sensorDoseTypeAtxtOsc.setText(mBinding.sensorDoseTypeAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[11])).toString());
                                mBinding.sensorHysteresisEdtOsc.setText(splitData[12]);
                                mBinding.sensorDutyCycleEdtOsc.setText(splitData[13]);
                                mBinding.sensorLockoutTimeDelayEdtOsc.setText(splitData[14]);
                                mBinding.sensorSafetyMaxEdtOsc.setText(splitData[15]);
                                mBinding.sensorSafetyMinEdtOsc.setText(splitData[16]);

                                mBinding.sensorDoseTypeAtxtOsc.setAdapter(getAdapter(doseTypeArr));
                                mBinding.sensorLinkInputSensorAtxtOsc.setAdapter(getAdapter(sensorInputArr));
                            } else if (splitData[9].equals("1")) { // PID
                                enablePID();
                                mBinding.pidLinkInputAtxtOsc.setText(mBinding.pidLinkInputAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[8])).toString());
                                mBinding.pidSetPointEdtOsc.setText(splitData[10].substring(0, 3));
                                mBinding.pidSetPointDeciOsc.setText(splitData[10].substring(4, 6));

                                mBinding.pidGainEdtOsc.setText(splitData[11].substring(0, 4));
                                mBinding.pidGainDeciOsc.setText(splitData[11].substring(5, 8));

                                mBinding.pidIntegeralTimeEdtOsc.setText(splitData[12].substring(0, 4));
                                mBinding.pidIntegeralTimeDeciOsc.setText(splitData[12].substring(5, 8));

                                mBinding.pidDerivativeTimeEdtOsc.setText(splitData[13].substring(0, 4));
                                mBinding.pidDerivativeTimeDeciOsc.setText(splitData[13].substring(5, 8));

                                mBinding.pidResetPidAtxtOsc.setText(mBinding.pidResetPidAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[14])).toString());
                                mBinding.pidMinOutputEdtOsc.setText(splitData[15]);
                                mBinding.pidMaxOutputEdtOsc.setText(splitData[16]);
                                mBinding.pidDoseTypeAtxtOsc.setText(mBinding.pidDoseTypeAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[17])).toString());
                                mBinding.pidInputMinEdtOsc.setText(splitData[18]);
                                mBinding.pidInputMaxEdtOsc.setText(splitData[19]);
                                mBinding.pidLockoutDelayEdtOsc.setText(splitData[20]);
                                mBinding.pidSafetyMaxEdtOsc.setText(splitData[21]);
                                mBinding.pidSafetyMinEdtOsc.setText(splitData[22]);

                                mBinding.pidDoseTypeAtxtOsc.setAdapter(getAdapter(doseTypeArr));
                                mBinding.pidLinkInputAtxtOsc.setAdapter(getAdapter(sensorInputArr));

                                mBinding.pidResetPidAtxtOsc.setAdapter(getAdapter(resetFlowTotalArr));
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
                                mBinding.testLinkInputRelayEdtOsc.setText(mBinding.testLinkInputRelayEdtOsc.getAdapter().getItem(Integer.parseInt(splitData[6])).toString());
                                mBinding.analogFixedValueEdtOsc.setText(splitData[7].substring(0, 2));
                                mBinding.analogFixedValueDeciOsc.setText(splitData[7].substring(3, 5));
                                mBinding.testLinkInputRelayEdtOsc.setAdapter(getAdapter(sensorInputArr));
                            } else {
                                enableAnalogMain();
                                mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(Integer.parseInt(splitData[5])).toString());
                                mBinding.analogLinkInputAtxtOsc.setText(mBinding.analogLinkInputAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[6])).toString());

                                mBinding.analogMinMaEdtOsc.setText(splitData[7].substring(0, 2));
                                mBinding.analogMinMaDeciOsc.setText(splitData[7].substring(3, 5));

                                mBinding.analogMaxMaEdtOsc.setText(splitData[8].substring(0, 2));
                                mBinding.analogMaxMaDeciOsc.setText(splitData[8].substring(3, 5));

                                mBinding.analogMinValueEdtOsc.setText(splitData[9].substring(0, 2));
                                mBinding.analogMinValueDeciOsc.setText(splitData[9].substring(3, 5));

                                mBinding.analogMaxValueEdtOsc.setText(splitData[10].substring(0, 2));
                                mBinding.analogMaxValueDeciOsc.setText(splitData[10].substring(3, 5));

                                mBinding.analogLinkInputAtxtOsc.setAdapter(getAdapter(sensorInputArr));
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

    boolean validation() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output Label cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.contFlowRateEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Flow Rate cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.contDoseRateEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Dose Rate cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.contDosePeriodEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Dose Periods cannot be Empty");
            return false;
        }
        return true;
    }


    private boolean validation1() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output Label cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.bleedBleedFlowRateEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Bleed Flow Rate cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.bleedTargetPPMEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Target PPM cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.bleedConcentrationEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Concentration cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.bleedLinkBleedRelayAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Choose any relay output to link");
            return false;
        } else if (isEmpty(mBinding.bleedSpecificGravityEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Specific Gravity cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.bleedPumpFlowRateEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Pump Flow Rate cannot be Empty");
            return false;
        }
        return true;
    }

    private boolean validation2() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output label cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.waterFlowMeterTypeAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Please select Flow Meter Type");
            return false;
        } else if (isEmpty(mBinding.waterTargetPPMEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Target PPM cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.waterConcentrationEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Concentration cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.waterBleedRelayAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Choose any relay output to link");
            return false;
        } else if (isEmpty(mBinding.waterSpecificGravityEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Specific Gravity  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.waterFlowMeterInputAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Please select Flow Meter Input");
            return false;
        }
        return true;
    }

    private boolean validation3() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output label cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorSetPointEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Set Point cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorDoseTypeAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Dose Type cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorHysteresisEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Hysteresis cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorDutyCycleEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Duty Cycle cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorSafetyMinEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Safety Min cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorSafetyMaxEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Safety max  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorLockoutTimeDelayEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "LockOut Time Delay cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorLinkInputSensorAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Link Input Sensor cannot be Empty");
            return false;
        }
        return true;
    }

    private boolean validation4() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output label cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidSetPointEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Set Point cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidGainEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Gain cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidIntegeralTimeEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Integral Time cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidDerivativeTimeEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Derivative Time cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidInputMinEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Input min  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidInputMaxEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Input Max  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidMinOutputEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Output Min  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidMinOutputEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Output Max cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidSafetyMinEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Safety Min cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidSafetyMaxEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Safety Max cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidResetPidAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Reset Pid Integral cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidLockoutDelayEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Lockout Delay  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidDoseTypeAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Please select Dose Type");
            return false;
        } else if (isEmpty(mBinding.pidLinkInputAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Please select Link Input Sensor");
            return false;
        }

        return true;
    }

    private boolean validation5() {
        return true;
    }

    private boolean validation6() {
        if (isEmpty(mBinding.analogMinMaEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Min Analog cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogMaxMaEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Max Analog cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogMinValueEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Min Value cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogMaxValueEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Max Value  cannot be Empty");
            return false;
        }

        return true;
    }

    private boolean validation7() {
        if (isEmpty(mBinding.analogFixedValueEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Fixed value cannot be Empty");
            return false;
        }

        return true;
    }


    private boolean commonValidation() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output Label  cannot be Empty");
            return false;
        }

        if (!currentFunctionMode.equals("Disabled")) {
            if (isEmpty(mBinding.interLockChannelOsATXT)) {
                mAppClass.showSnackBar(getContext(), "Please select the Interlock Channel");
                return false;
            } else if (isEmpty(mBinding.activateChannelOsATXT)) {
                mAppClass.showSnackBar(getContext(), "Please select the Activate Channel");
                return false;
            } else if (isEmpty(mBinding.modeOsATXT)) {
                mAppClass.showSnackBar(getContext(), "Please select the Mode");
                return false;
            }
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
                (outputSensorNo, "Output- " + outputSensorNo + "(" + toString(mBinding.outputLabelOsEDT) + ")", toString(0, mBinding.outputLabelOsEDT),
                        mBinding.funtionModeOsATXT.getText().toString(),
                        (currentFunctionMode.equals("Disabled") ? "" : mBinding.modeOsATXT.getText().toString()));
        List<OutputConfigurationEntity> entryListUpdate = new ArrayList<>();
        entryListUpdate.add(entityUpdate);
        updateToDb(entryListUpdate);
    }
}