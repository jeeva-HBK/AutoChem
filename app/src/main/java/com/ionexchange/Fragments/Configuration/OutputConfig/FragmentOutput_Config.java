package com.ionexchange.Fragments.Configuration.OutputConfig;

import static com.ionexchange.Activity.BaseActivity.dismissProgress;
import static com.ionexchange.Activity.BaseActivity.showProgress;
import static com.ionexchange.Others.ApplicationClass.bleedRelay;
import static com.ionexchange.Others.ApplicationClass.doseTypeArr;
import static com.ionexchange.Others.ApplicationClass.flowMeterTypeArr;
import static com.ionexchange.Others.ApplicationClass.flowMeters;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.functionMode;
import static com.ionexchange.Others.ApplicationClass.getDecimalValue;
import static com.ionexchange.Others.ApplicationClass.inputDAO;
import static com.ionexchange.Others.ApplicationClass.interlockChannel;
import static com.ionexchange.Others.ApplicationClass.modeAnalog;
import static com.ionexchange.Others.ApplicationClass.modeInhibitor;
import static com.ionexchange.Others.ApplicationClass.modeSensor;
import static com.ionexchange.Others.ApplicationClass.resetFlowTotalArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.ApplicationClass.virtualDAO;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.ENDPACKET;
import static com.ionexchange.Others.PacketControl.PCK_OUTPUT_CONFIG;
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
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.Others.EventLogDemo;
import com.ionexchange.R;
import com.ionexchange.Singleton.ApiService;
import com.ionexchange.Singleton.SharedPref;
import com.ionexchange.databinding.FragmentOutputConfigBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentOutput_Config extends Fragment implements DataReceiveCallback {
    FragmentOutputConfigBinding mBinding;
    ApplicationClass mAppClass;
    int outputSensorNo, sensorLength = 2;
    String[] bleedArr, analogInputArr, sensorInputArr, activateChannalsList;
    String lInhibitorContinuous = "layoutInhibitorContinuous", lInhibitorBleed = "layoutInhibitorBleedDown", lInhibitorWaterFlow = "layoutInhibitorWaterFlow",
            lSensorOnOFF = "layoutSensorOnOff", lSensorPid = "layoutSensorPID", lAnalogMain = "layoutAnalogMain", lAnalogTest = "layoutAnalogTest", lAnalogDisable = "layoutAnalogDisable",
            currentFunctionMode = "", analogMode = "3", inputType = "pH";
    private static final String TAG = "FragmentOutput_Config";
    OutputConfigurationDao dao;
    WaterTreatmentDb dB;
    String writePacket;

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
        dB = WaterTreatmentDb.getDatabase(getContext());
        dao = dB.outputConfigurationDao();
        outputSensorNo = getArguments().getInt("sensorInputNo");
        FragmentOutputIndex.hideToolbar();
        initAdapter();
        mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(1).toString());
        if (outputSensorNo < 15) {
            mBinding.functionModeOs.setEnabled(true);
            enableInhibitorLayout();
        } else {
            mBinding.functionModeOs.setEnabled(false);
            mBinding.outputRow2.setVisibility(View.GONE);
            enableAnalogLayout();
        }
        bleedArr = getBleedArray();
        analogInputArr = getAnalogInputArray();
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
                    case 3:
                        enableManual();
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

        mBinding.sensorLinkInputSensorAtxtOsc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setMaxLength();
            }
        });
        mBinding.pidLinkInputAtxtOsc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setMaxLengthPID();
            }
        });
        mBinding.analogLinkInputAtxtOsc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setMaxLengthAnalog();
            }
        });
        mBinding.saveFabOutput.setOnClickListener(this::save);
        checkUser();
    }

    private String[] getAnalogInputArray() {
        WaterTreatmentDb DB = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao DAO = DB.inputConfigurationDao();
        List<InputConfigurationEntity> inputNameList = DAO.getInputHardWareNoConfigurationEntityList(1, 17);
        String[] inputNames = new String[17];
        if (!inputNameList.isEmpty()) {
            for (int i = 0; i < inputNameList.size(); i++) {
                inputNames[i] = "Input- " + inputNameList.get(i).getHardwareNo() + " (" + inputNameList.get(i).getInputLabel() + ")";
            }
        }
        VirtualConfigurationDao DAOV = DB.virtualConfigurationDao();
        List<VirtualConfigurationEntity> virtualinputNameList = DAOV.getVirtualHardWareNoConfigurationEntityList(50, 57);
        String[] vinputNames = new String[8];
        if (!virtualinputNameList.isEmpty()) {
            for (int i = 0; i < virtualinputNameList.size(); i++) {
                vinputNames[i] = "Input- " + virtualinputNameList.get(i).getHardwareNo() + " (" + virtualinputNameList.get(i).getInputLabel() + ")";
            }
        }
        List<OutputConfigurationEntity> outputNameList = dao.getOutputHardWareNoConfigurationEntityList(1, 14);
        String[] outputNames = new String[14];
        if (!outputNameList.isEmpty()) {
            for (int i = 0; i < outputNameList.size(); i++) {
                outputNames[i] = "Output- " + outputNameList.get(i).getOutputHardwareNo() + " (" + outputNameList.get(i).getOutputLabel() + ")";
            }
        }
        List analoginputlist = new ArrayList(Arrays.asList(inputNames));
        analoginputlist.addAll(Arrays.asList(vinputNames));
        analoginputlist.addAll(Arrays.asList(outputNames));
        String[] analogInputList = (String[]) analoginputlist.toArray(new String[0]);
        return analogInputList;
    }

    private String[] getSensorInputArray() {
        WaterTreatmentDb DB = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao DAO = DB.inputConfigurationDao();
        List<InputConfigurationEntity> inputNameList = DAO.getInputHardWareNoConfigurationEntityList(1, 25);
        String[] inputNames = new String[25];
        if (!inputNameList.isEmpty()) {
            for (int i = 0; i < inputNameList.size(); i++) {
                inputNames[i] = "Input- " + inputNameList.get(i).getHardwareNo() + " (" + inputNameList.get(i).getInputLabel() + ")";
            }
        }
        VirtualConfigurationDao DAOV = DB.virtualConfigurationDao();
        List<VirtualConfigurationEntity> virtualinputNameList = DAOV.getVirtualHardWareNoConfigurationEntityList(50, 57);
        String[] vinputNames = new String[8];
        if (!virtualinputNameList.isEmpty()) {
            for (int i = 0; i < virtualinputNameList.size(); i++) {
                vinputNames[i] = "Input- " + virtualinputNameList.get(i).getHardwareNo() + " (" + virtualinputNameList.get(i).getInputLabel() + ")";
            }
        }

        List inputlist = new ArrayList(Arrays.asList(inputNames));
        inputlist.addAll(Arrays.asList(vinputNames));
        String[] inputList = (String[]) inputlist.toArray(new String[0]);
        return inputList;
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
                    mBinding.analogMinValueTBtn.setEnabled(false);
                    mBinding.analogMinValueEdtOsc.setEnabled(false);
                    mBinding.analogMinValueDeciOsc.setEnabled(false);
                    mBinding.analogMaxValueTBtn.setEnabled(false);
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
                    mBinding.testLinkInputRelayTilOsc.setEnabled(false);
                    mBinding.analogFixedValueTilOsc.setEnabled(false);
                    mBinding.analogFixedValueDeciOsc.setEnabled(false);
                } else {
                    mBinding.functionModeOs.setEnabled(false);
                    mBinding.modeOs.setEnabled(false);
                    //mBinding.outputInterLockChannelOs.setEnabled(false);
                    //mBinding.outputActivateChannelOs.setEnabled(false);

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
                    mBinding.sensorSetpointvalueTBtn.setEnabled(false);
                    mBinding.sensorSetPointDeciOsc.setEnabled(false);
                    mBinding.sensorDoseTypeTilOsc.setEnabled(false);
                    mBinding.sensorDutyCycleTilOsc.setEnabled(false);
                    mBinding.sensorSafetyMinTilOsc.setEnabled(false);
                    mBinding.sensorSafetyMinDeciOsc.setEnabled(false);
                    mBinding.sensorSafetyMinTBtn.setEnabled(false);
                    mBinding.sensorSafetyMaxTBtn.setEnabled(false);
                    mBinding.sensorSafetyMaxTilOsc.setEnabled(false);
                    mBinding.sensorSafetyMaxDeciOsc.setEnabled(false);
                    mBinding.sensorLockoutTimeDelayTilOsc.setEnabled(false);
                    mBinding.pidHysteresisTilOsc.setEnabled(false);
                    mBinding.pidHysteresisDeciOsc.setEnabled(false);
                    mBinding.pidHysteresisTBtn.setEnabled(false);
                    mBinding.pidSetpointvalueTBtn.setEnabled(false);
                    mBinding.pidSetPointTilOsc.setEnabled(false);
                    mBinding.pidSetPointDeciOsc.setEnabled(false);
                    mBinding.pidGainTilOsc.setEnabled(false);
                    mBinding.pidGainDeciOsc.setEnabled(false);
                    mBinding.pidIntegeralTimeTilOsc.setEnabled(false);
                    mBinding.pidIntegeralTimeDeciOsc.setEnabled(false);
                    mBinding.pidDerivativeTimeTilOsc.setEnabled(false);
                    mBinding.pidDerivativeTimeDeciOsc.setEnabled(false);
                    mBinding.pidInputMinvalueTBtn.setEnabled(false);
                    mBinding.pidInputMinTilOsc.setEnabled(false);
                    mBinding.pidInputMinDeciOsc.setEnabled(false);
                    mBinding.pidInputMaxvalueTBtn.setEnabled(false);
                    mBinding.pidInputMaxTilOsc.setEnabled(false);
                    mBinding.pidInputMaxDeciOsc.setEnabled(false);
                    mBinding.pidMinOutputTilOsc.setEnabled(false);
                    mBinding.pidMaxOutputTilOsc.setEnabled(false);
                    mBinding.pidSafetyMinvalueTBtn.setEnabled(false);
                    mBinding.pidSafetyMinTilOsc.setEnabled(false);
                    mBinding.pidSafetyMinDeciOsc.setEnabled(false);
                    mBinding.pidSafetyMaxvalueTBtn.setEnabled(false);
                    mBinding.pidSafetyMaxTilOsc.setEnabled(false);
                    mBinding.pidSafetyMaxDeciOsc.setEnabled(false);
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
        mBinding.analogLinkInputAtxtOsc.setAdapter(getAdapter(analogInputArr));
    }

    private void enableAnalogTest() {
        mBinding.setFunctionMode(lAnalogTest);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(2).toString());
        mBinding.testLinkInputRelayEdtOsc.setAdapter(getAdapter(analogInputArr));
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
                                    showProgress();
                                    sendContinuous();
                                }
                                break;
                            case "1":
                                if (validation1()) {
                                    showProgress();
                                    sendBleedBlow();
                                }
                                break;
                            case "2":
                                if (validation2()) {
                                    showProgress();
                                    sendWaterMeter();
                                }
                                break;
                        }
                        break;

                    case "Sensor":
                        switch (getPosition(0, toString(mBinding.modeOsATXT), modeSensor)) {
                            case "0":
                                if (validation3()) {
                                    showProgress();
                                    sendOnOFf();
                                }

                                break;
                            case "1":
                                if (validation4()) {
                                    showProgress();
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
                        showProgress();
                        sendRelayDisable("0");
                        break;
                    case "Manual":
                        showProgress();
                        sendRelayManual("4");
                        break;
                }
            }

        } else {
            switch (getPosition(0, toString(mBinding.modeOsATXT), modeAnalog)) {
                case "0":
                    if (isEmpty(mBinding.outputLabelOsEDT)) {
                        mAppClass.showSnackBar(getContext(), "Output Label  cannot be Empty");
                    } else {
                        showProgress();
                        sendAnalogDisable();
                    }
                    break;
                case "1":
                case "3":
                case "4":
                    if (validation6()) {
                        showProgress();
                        sendAnalogValue();
                    }
                    break;
                case "2":
                    if (validation7()){
                        showProgress();
                        sendAnalogTest();
                    }

                    break;
            }
        }
    }

    /*Relay_Disabled*/
    private void sendRelayDisable(String mode) {
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                mode + SPILT_CHAR +
                toString(mBinding.outputLabelOsEDT);
        mAppClass.sendPacket(this, writePacket);
    }

    /*Relay Manual*/
    private void sendRelayManual(String mode) {
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                mode + SPILT_CHAR +
                toString(mBinding.outputLabelOsEDT) + SPILT_CHAR +
                formDigits(2, "" + getInterlockChannaleHardwareNumber()) + SPILT_CHAR +
                //(Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 34) + SPILT_CHAR +
                formDigits(2, "" + getActiviateChannaleHardwareNumber());
        mAppClass.sendPacket(this,writePacket );
    }

    /*AnalogTest*/
    private void sendAnalogTest() {
        // removed formDigits(2,getLinkInputSensor(mBinding.testLinkInputRelayEdtOsc)) - 24/11
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                analogMode + SPILT_CHAR +
                toString(mBinding.outputLabelOsEDT) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeAnalog) + SPILT_CHAR +
                //"00" + SPILT_CHAR +
                getDecimalValue(mBinding.analogFixedValueEdtOsc, 2, mBinding.analogFixedValueDeciOsc, 2);
        mAppClass.sendPacket(this, writePacket);
    }

    /*AnalogDisable*/
    private void sendAnalogDisable() {
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                analogMode + SPILT_CHAR +
                toString(mBinding.outputLabelOsEDT) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeAnalog);
        mAppClass.sendPacket(this, writePacket);
    }

    /*analogValue*/
    private void sendAnalogValue() {
        String u_minvalue, u_maxvalue, analog_mode = "I";
        if (inputType.equalsIgnoreCase("ORP") || inputType.equals("Temperature")) {
            u_minvalue = getDecimalValue(mBinding.analogMinValueTBtn, mBinding.analogMinValueEdtOsc, sensorLength, mBinding.analogMinValueDeciOsc, 2);
            u_maxvalue = getDecimalValue(mBinding.analogMaxValueTBtn, mBinding.analogMaxValueEdtOsc, sensorLength, mBinding.analogMaxValueDeciOsc, 2);
        } else {
            u_minvalue = getDecimalValue(mBinding.analogMinValueEdtOsc, sensorLength, mBinding.analogMinValueDeciOsc, 2);
            u_maxvalue = getDecimalValue(mBinding.analogMaxValueEdtOsc, sensorLength, mBinding.analogMaxValueDeciOsc, 2);
        }
        analog_mode = toString(mBinding.analogLinkInputAtxtOsc).startsWith("Input") ? "I" : "O";
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                analogMode + SPILT_CHAR +
                toString(mBinding.outputLabelOsEDT) + SPILT_CHAR +
                getPosition(1, toString(mBinding.modeOsATXT), modeAnalog) + SPILT_CHAR +
                analog_mode + formDigits(2, getAnalogLinkInputSensor(mBinding.analogLinkInputAtxtOsc)) + SPILT_CHAR +
                getDecimalValue(mBinding.analogMinMaEdtOsc, 2, mBinding.analogMinMaDeciOsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.analogMaxMaEdtOsc, 2, mBinding.analogMaxMaDeciOsc, 2) + SPILT_CHAR +
                u_minvalue + SPILT_CHAR +
                u_maxvalue;
        mAppClass.sendPacket(this, writePacket);
    }

    /*fuzzy*/
    private void sendFuzzy() {
        // Still in Development
    }

    /*PID*/
    private void sendPID() {
        String u_setPoint, u_safetyMin, u_safetyMax, u_inputMin, u_inputMax;
        if (inputType.equalsIgnoreCase("ORP") || inputType.equals("Temperature")) {
            u_setPoint = getDecimalValue(mBinding.pidSetpointvalueTBtn, mBinding.pidSetPointEdtOsc, sensorLength, mBinding.pidSetPointDeciOsc, 2);
            u_safetyMin = getDecimalValue(mBinding.pidSafetyMinvalueTBtn, mBinding.pidSafetyMinEdtOsc, sensorLength, mBinding.pidSafetyMinDeciOsc, 2);
            u_safetyMax = getDecimalValue(mBinding.pidSafetyMaxvalueTBtn, mBinding.pidSafetyMaxEdtOsc, sensorLength, mBinding.pidSafetyMaxDeciOsc, 2);
            u_inputMin = getDecimalValue(mBinding.pidInputMinvalueTBtn, mBinding.pidInputMinEdtOsc, sensorLength, mBinding.pidInputMinDeciOsc, 2);
            u_inputMax = getDecimalValue(mBinding.pidInputMaxvalueTBtn, mBinding.pidInputMaxEdtOsc, sensorLength, mBinding.pidInputMaxDeciOsc, 2);
        } else {
            u_setPoint = getDecimalValue(mBinding.pidSetPointEdtOsc, sensorLength, mBinding.pidSetPointDeciOsc, 2);
            u_safetyMin = getDecimalValue(mBinding.pidSafetyMinEdtOsc, sensorLength, mBinding.pidSafetyMinDeciOsc, 2);
            u_safetyMax = getDecimalValue(mBinding.pidSafetyMaxEdtOsc, sensorLength, mBinding.pidSafetyMaxDeciOsc, 2);
            u_inputMin = getDecimalValue(mBinding.pidInputMinEdtOsc, sensorLength, mBinding.pidInputMinDeciOsc, 2);
            u_inputMax = getDecimalValue(mBinding.pidInputMaxEdtOsc, sensorLength, mBinding.pidInputMaxDeciOsc, 2);
        }
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                //(Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 34) + SPILT_CHAR +
                formDigits(2, "" + getInterlockChannaleHardwareNumber()) + SPILT_CHAR +
                formDigits(2, "" + getActiviateChannaleHardwareNumber()) + SPILT_CHAR +
                formDigits(2, getLinkInputSensor(mBinding.pidLinkInputAtxtOsc)) + SPILT_CHAR +
                getPosition(1, toString(mBinding.modeOsATXT), modeSensor) + SPILT_CHAR +
                u_setPoint + SPILT_CHAR +
                getDecimalValue(mBinding.pidGainEdtOsc, 4, mBinding.pidGainDeciOsc, 3) + SPILT_CHAR +
                getDecimalValue(mBinding.pidIntegeralTimeEdtOsc, 4, mBinding.pidIntegeralTimeDeciOsc, 3) + SPILT_CHAR +
                getDecimalValue(mBinding.pidDerivativeTimeEdtOsc, 4, mBinding.pidDerivativeTimeDeciOsc, 3) + SPILT_CHAR +
                getPosition(1, toString(mBinding.pidResetPidAtxtOsc), resetFlowTotalArr) + SPILT_CHAR +
                toString(3, mBinding.pidMinOutputEdtOsc) + SPILT_CHAR +
                toString(3, mBinding.pidMaxOutputEdtOsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.pidDoseTypeAtxtOsc), doseTypeArr) + SPILT_CHAR +
                u_inputMin + SPILT_CHAR +
                u_inputMax + SPILT_CHAR +
                toString(5, mBinding.pidLockoutDelayEdtOsc) + SPILT_CHAR +
                u_safetyMax + SPILT_CHAR +
                u_safetyMin;
        mAppClass.sendPacket(this, writePacket);
    }

    /*OnOff*/
    private void sendOnOFf() {
        String u_setPoint, u_safetyMin, u_safetyMax, hystersis;
        if (inputType.equalsIgnoreCase("ORP") || inputType.equals("Temperature")) {
            u_setPoint = getDecimalValue(mBinding.sensorSetpointvalueTBtn, mBinding.sensorSetPointEdtOsc, sensorLength, mBinding.sensorSetPointDeciOsc, 2);
            u_safetyMin = getDecimalValue(mBinding.sensorSafetyMinTBtn, mBinding.sensorSafetyMinEdtOsc, sensorLength, mBinding.sensorSafetyMinDeciOsc, 2);
            u_safetyMax = getDecimalValue(mBinding.sensorSafetyMaxTBtn, mBinding.sensorSafetyMaxEdtOsc, sensorLength, mBinding.sensorSafetyMaxDeciOsc, 2);
            hystersis = getDecimalValue(mBinding.pidHysteresisTBtn, mBinding.pidHysteresisEdtOsc, sensorLength, mBinding.pidHysteresisDeciOsc, 2);
        } else {
            u_setPoint = getDecimalValue(mBinding.sensorSetPointEdtOsc, sensorLength, mBinding.sensorSetPointDeciOsc, 2);
            u_safetyMin = getDecimalValue(mBinding.sensorSafetyMinEdtOsc, sensorLength, mBinding.sensorSafetyMinDeciOsc, 2);
            u_safetyMax = getDecimalValue(mBinding.sensorSafetyMaxEdtOsc, sensorLength, mBinding.sensorSafetyMaxDeciOsc, 2);
            hystersis = getDecimalValue(mBinding.pidHysteresisEdtOsc, sensorLength, mBinding.pidHysteresisDeciOsc, 2);
        }
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                //(Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 34) + SPILT_CHAR +
                formDigits(2, "" + getInterlockChannaleHardwareNumber()) + SPILT_CHAR +
                formDigits(2, "" + getActiviateChannaleHardwareNumber()) + SPILT_CHAR +
                formDigits(2, getLinkInputSensor(mBinding.sensorLinkInputSensorAtxtOsc)) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeSensor) + SPILT_CHAR +
                u_setPoint + SPILT_CHAR +
                getPosition(0, toString(mBinding.sensorDoseTypeAtxtOsc), doseTypeArr) + SPILT_CHAR +
                hystersis + SPILT_CHAR +
                toString(3, mBinding.sensorDutyCycleEdtOsc) + SPILT_CHAR +
                toString(5, mBinding.sensorLockoutTimeDelayEdtOsc) + SPILT_CHAR +
                u_safetyMax + SPILT_CHAR +
                u_safetyMin;
        mAppClass.sendPacket(this, writePacket);
    }

    /* WaterMeter */
    private void sendWaterMeter() {
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(1, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                //(Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 34) + SPILT_CHAR +
                formDigits(2, "" + getInterlockChannaleHardwareNumber()) + SPILT_CHAR +
                formDigits(2, "" + getActiviateChannaleHardwareNumber()) + SPILT_CHAR +
                getPosition(1, toString(mBinding.modeOsATXT), modeInhibitor) + SPILT_CHAR +
                getPosition(1, toString(mBinding.waterFlowMeterTypeAtxtOsc), flowMeterTypeArr) + SPILT_CHAR +
                (Integer.parseInt(getPosition(1, toString(mBinding.waterFlowMeterInputAtxtOsc), flowMeters)) + 1) + SPILT_CHAR +
                formDigits(2,String.valueOf(Integer.parseInt(getPosition(2, toString(mBinding.waterBleedRelayAtxtOsc), bleedArr)) + 1)) + SPILT_CHAR +
                getDecimalValue(mBinding.waterPumpFlowRateEdtOsc, 9, mBinding.waterPumpFlowRateDeciOsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.waterTargetPPMEdtOsc, 7, mBinding.waterTargetPPMDeciOsc, 2) + SPILT_CHAR +
                toString(3, mBinding.waterConcentrationEdtOsc) + SPILT_CHAR +
                getDecimalValue(mBinding.waterSpecificGravityEdtOsc, 1, mBinding.waterSpecificGravityDeciOsc, 3);
        mAppClass.sendPacket(this, writePacket);
    }

    /* Bleed Blow */
    private void sendBleedBlow() {
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                //(Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 34) + SPILT_CHAR +
                formDigits(2, "" + getInterlockChannaleHardwareNumber()) + SPILT_CHAR +
                formDigits(2, "" + getActiviateChannaleHardwareNumber()) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeInhibitor) + SPILT_CHAR +
                formDigits(2,String.valueOf(Integer.parseInt(getPosition(2, toString(mBinding.bleedLinkBleedRelayAtxtOsc), bleedArr)) + 1)) + SPILT_CHAR +
                getDecimalValue(mBinding.bleedBleedFlowRateEdtOsc, 6, mBinding.bleedBleedFlowrateDeciOsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.bleedPumpFlowRateEdtOsc, 9, mBinding.bleedPumpFlowRateDeciOsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.bleedTargetPPMEdtOsc, 7, mBinding.bleedTargetPPMDeciOsc, 2) + SPILT_CHAR +
                toString(3, mBinding.bleedConcentrationEdtOsc) + SPILT_CHAR +
                getDecimalValue(mBinding.bleedSpecificGravityEdtOsc, 1, mBinding.bleedSpecificGravityDeciOsc, 3);
        mAppClass.sendPacket(this, writePacket);
    }

    /* Continuous */
    private void sendContinuous() {
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR +
                toString(2, outputSensorNo) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                //(Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 34) + SPILT_CHAR +
                formDigits(2, "" + getInterlockChannaleHardwareNumber()) + SPILT_CHAR +
                formDigits(2, "" + getActiviateChannaleHardwareNumber()) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeInhibitor) + SPILT_CHAR +
                getDecimalValue(mBinding.contFlowRateEdtOsc, 9, mBinding.contFlowRateDeciOsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.contDoseRateEdtOsc, 9, mBinding.contDoseRateDeciOsc, 2) + SPILT_CHAR +
                toString(4, mBinding.contDosePeriodEdtOsc);
        mAppClass.sendPacket(this, writePacket);
    }

    private String getLinkInputSensor(AutoCompleteTextView sensorLinkInputSensorAtxtOsc) {
        int analoginputHardwareno = Integer.parseInt(getPosition(2, toString(sensorLinkInputSensorAtxtOsc), sensorInputArr)) + 1;
        if (analoginputHardwareno >= 25) {
            analoginputHardwareno = Integer.parseInt(getPosition(2, toString(sensorLinkInputSensorAtxtOsc), sensorInputArr)) + 33;
        }
        return "" + analoginputHardwareno;
    }

    private String getAnalogLinkInputSensor(AutoCompleteTextView sensorLinkInputSensorAtxtOsc) {
        int analoginputHardwareno = 0;
        if (toString(sensorLinkInputSensorAtxtOsc).startsWith("Input")) {
            analoginputHardwareno = Integer.parseInt(getPosition(2, toString(sensorLinkInputSensorAtxtOsc), analogInputArr)) + 1;
            if (analoginputHardwareno >= 18) {
                analoginputHardwareno = Integer.parseInt(getPosition(2, toString(sensorLinkInputSensorAtxtOsc), analogInputArr)) + 33;
            }
        } else {
            analoginputHardwareno = Integer.parseInt(getPosition(2, toString(sensorLinkInputSensorAtxtOsc), analogInputArr)) - 24;
        }
        return "" + analoginputHardwareno;
    }

    private int getInterlockChannaleHardwareNumber() {
        int interlockchannelPos = Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), activateChannalsList));
        int hardwareNo;
        if (interlockchannelPos == 0) {
            hardwareNo = 0;
        } else if (interlockchannelPos <= 16) {
            hardwareNo = interlockchannelPos + 33;
        } else {
            hardwareNo = interlockchannelPos - 16;
        }
        return hardwareNo;
    }

    private int getActiviateChannaleHardwareNumber() {
        int activatechannelPos = Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), activateChannalsList));
        int hardwareNo;
        if (activatechannelPos == 0) {
            hardwareNo = 0;
        } else if (activatechannelPos <= 16) {
            hardwareNo = activatechannelPos + 33;
        } else {
            hardwareNo = activatechannelPos - 16;
        }
        return hardwareNo;
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

    void enableManual() {
        currentFunctionMode = "Manual";
        mBinding.modeOs.setEnabled(false);
        mBinding.modeOsATXT.setText("");
        mBinding.setFunctionMode("Manual");
        mBinding.outputRow2.setVisibility(View.VISIBLE);
        Log.e(TAG, "enableManual: ");
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
        mBinding.outputRow2.setVisibility(View.VISIBLE);
        currentFunctionMode = "Inhibitor";
        mBinding.setFunctionMode(lInhibitorContinuous);
        mBinding.modeOsATXT.setAdapter(getAdapter(modeInhibitor));
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeInhibitor));
    }

    private void enableSensorLayout() {
        mBinding.outputRow2.setVisibility(View.VISIBLE);
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
        List<OutputConfigurationEntity> outputNameList = dao.getOutputHardWareNoConfigurationEntityList(1, 14);
        String[] outputNames = new String[14];
        if (!outputNameList.isEmpty()) {
            for (int i = 0; i < outputNameList.size(); i++) {
                outputNames[i] = "Output- " + outputNameList.get(i).getOutputHardwareNo() + " (" + outputNameList.get(i).getOutputLabel() + ")";
            }
        }
        List digitalinputlist = new ArrayList(Arrays.asList(interlockChannel));
        digitalinputlist.addAll(Arrays.asList(outputNames));
        activateChannalsList = (String[]) digitalinputlist.toArray(new String[0]);
        mBinding.activateChannelOsATXT.setAdapter(getAdapter(activateChannalsList));
        mBinding.interLockChannelOsATXT.setAdapter(getAdapter(activateChannalsList));
        if (outputSensorNo < 15) {
            functionMode = new String[]{"Disable", "Inhibitor", "Sensor", "Manual"};
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
        if (splitData[1].equals(PCK_OUTPUT_CONFIG)) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    try {

                        mBinding.outputLabelOsEDT.setText(splitData[5]);
                        if (outputSensorNo > 14) {
                            mBinding.functionModeOs.setEnabled(false);
                            //  if (splitData[4].equalsIgnoreCase("3")) {
                            mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(1).toString());
                            //   } else {
                            //        mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                            //   }
                        } else {
                            mBinding.functionModeOs.setEnabled(true);
                            mBinding.funtionModeOsATXT.setText(splitData[4].equalsIgnoreCase("4") ? mBinding.funtionModeOsATXT.getAdapter().getItem(Integer.parseInt(splitData[4]) - 1).toString() :
                                    mBinding.funtionModeOsATXT.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                            //mBinding.interLockChannelOsATXT.setText(mBinding.interLockChannelOsATXT.getAdapter().getItem(Integer.parseInt(splitData[6]) - 34).toString());
                            if (!splitData[4].equals("0")) {
                                int interlockChannelPos = Integer.parseInt(splitData[6]);
                                int setInterlockChannelPos = interlockChannelPos;
                                if(interlockChannelPos == 0) {
                                    mBinding.interLockChannelOsATXT.setText(mBinding.interLockChannelOsATXT.getAdapter().getItem(interlockChannelPos).toString());
                                }
                                else if (interlockChannelPos >= 34) {
                                    setInterlockChannelPos = interlockChannelPos - 33;
                                    mBinding.interLockChannelOsATXT.setText(mBinding.interLockChannelOsATXT.getAdapter().getItem(setInterlockChannelPos).toString());
                                } else {
                                    setInterlockChannelPos = interlockChannelPos + 16;
                                    mBinding.interLockChannelOsATXT.setText(mBinding.interLockChannelOsATXT.getAdapter().getItem(setInterlockChannelPos).toString());
                                }
                                int activiateChannelPos = Integer.parseInt(splitData[7]);
                                int setActiivateChannelPos = activiateChannelPos;
                                if(activiateChannelPos == 0) {
                                    mBinding.activateChannelOsATXT.setText(mBinding.activateChannelOsATXT.getAdapter().getItem(activiateChannelPos).toString());
                                } else if (activiateChannelPos >= 34) {
                                    setActiivateChannelPos = activiateChannelPos - 33;
                                    mBinding.activateChannelOsATXT.setText(mBinding.activateChannelOsATXT.getAdapter().getItem(setActiivateChannelPos).toString());
                                } else {
                                    setActiivateChannelPos = activiateChannelPos + 16;
                                    mBinding.activateChannelOsATXT.setText(mBinding.activateChannelOsATXT.getAdapter().getItem(setActiivateChannelPos).toString());
                                }
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
                                    if (Integer.parseInt(splitData[8]) < 26) {
                                        mBinding.sensorLinkInputSensorAtxtOsc.setText(mBinding.sensorLinkInputSensorAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[8]) - 1).toString());
                                    } else {
                                        mBinding.sensorLinkInputSensorAtxtOsc.setText(mBinding.sensorLinkInputSensorAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[8]) - 33).toString());
                                    }
                                    setMaxLength();
                                    if (inputType.equalsIgnoreCase("ORP") || inputType.equalsIgnoreCase("Temperature")) {
                                        mBinding.sensorSetpointvalueTBtn.setChecked((splitData[10].substring(0, 1)).equals("+"));
                                        mBinding.sensorSetPointEdtOsc.setText(splitData[10].substring(1, sensorLength + 1));
                                        mBinding.sensorSetPointDeciOsc.setText(splitData[10].substring(sensorLength + 2, sensorLength + 4));
                                        mBinding.sensorSafetyMinTBtn.setChecked((splitData[16].substring(0, 1)).equals("+"));
                                        mBinding.sensorSafetyMinEdtOsc.setText(splitData[16].substring(1, sensorLength + 1));
                                        mBinding.sensorSafetyMinDeciOsc.setText(splitData[16].substring(sensorLength + 2, sensorLength + 4));
                                        mBinding.sensorSafetyMaxTBtn.setChecked((splitData[15].substring(0, 1)).equals("+"));
                                        mBinding.sensorSafetyMaxEdtOsc.setText(splitData[15].substring(1, sensorLength + 1));
                                        mBinding.sensorSafetyMaxDeciOsc.setText(splitData[15].substring(sensorLength + 2, sensorLength + 4));

                                        mBinding.pidHysteresisTBtn.setChecked((splitData[12].substring(0, 1)).equals("+"));
                                        mBinding.pidHysteresisEdtOsc.setText(splitData[12].substring(1, sensorLength + 1));
                                        mBinding.pidHysteresisDeciOsc.setText(splitData[12].substring(sensorLength + 2, sensorLength + 4));
                                    } else {
                                        mBinding.sensorSetPointEdtOsc.setText(splitData[10].substring(0, sensorLength));
                                        mBinding.sensorSetPointDeciOsc.setText(splitData[10].substring(sensorLength + 1, sensorLength + 3));
                                        mBinding.sensorSafetyMinEdtOsc.setText(splitData[16].substring(0, sensorLength));
                                        mBinding.sensorSafetyMinDeciOsc.setText(splitData[16].substring(sensorLength + 1, sensorLength + 3));
                                        mBinding.sensorSafetyMaxEdtOsc.setText(splitData[15].substring(0, sensorLength));
                                        mBinding.sensorSafetyMaxDeciOsc.setText(splitData[15].substring(sensorLength + 1, sensorLength + 3));
                                        mBinding.pidHysteresisEdtOsc.setText(splitData[12].substring(0, sensorLength));
                                        mBinding.pidHysteresisDeciOsc.setText(splitData[12].substring(sensorLength + 1, sensorLength + 3));
                                    }
                                    mBinding.sensorDoseTypeAtxtOsc.setText(mBinding.sensorDoseTypeAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[11])).toString());

                                    mBinding.sensorDutyCycleEdtOsc.setText(splitData[13]);
                                    mBinding.sensorLockoutTimeDelayEdtOsc.setText(splitData[14]);
                                    mBinding.sensorDoseTypeAtxtOsc.setAdapter(getAdapter(doseTypeArr));
                                    mBinding.sensorLinkInputSensorAtxtOsc.setAdapter(getAdapter(sensorInputArr));
                                } else if (splitData[9].equals("1")) { // PID
                                    enablePID();
                                    if (Integer.parseInt(splitData[8]) < 26) {
                                        mBinding.pidLinkInputAtxtOsc.setText(mBinding.pidLinkInputAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[8]) - 1).toString());
                                    } else {
                                        mBinding.pidLinkInputAtxtOsc.setText(mBinding.pidLinkInputAtxtOsc.getAdapter().getItem(Integer.parseInt(splitData[8]) - 33).toString());
                                    }
                                    setMaxLengthPID();
                                    if (inputType.equalsIgnoreCase("ORP") || inputType.equalsIgnoreCase("Temperature")) {
                                        mBinding.pidSetpointvalueTBtn.setChecked((splitData[10].substring(0, 1)).equals("+"));
                                        mBinding.pidSetPointEdtOsc.setText(splitData[10].substring(1, sensorLength + 1));
                                        mBinding.pidSetPointDeciOsc.setText(splitData[10].substring(sensorLength + 2, sensorLength + 4));

                                        mBinding.pidInputMinvalueTBtn.setChecked((splitData[18].substring(0, 1)).equals("+"));
                                        mBinding.pidInputMinEdtOsc.setText(splitData[18].substring(1, sensorLength + 1));
                                        mBinding.pidInputMinDeciOsc.setText(splitData[18].substring(sensorLength + 2, sensorLength + 4));

                                        mBinding.pidInputMaxvalueTBtn.setChecked((splitData[19].substring(0, 1)).equals("+"));
                                        mBinding.pidInputMaxEdtOsc.setText(splitData[19].substring(1, sensorLength + 1));
                                        mBinding.pidInputMaxDeciOsc.setText(splitData[19].substring(sensorLength + 2, sensorLength + 4));

                                        mBinding.pidSafetyMaxvalueTBtn.setChecked((splitData[21].substring(0, 1)).equals("+"));
                                        mBinding.pidSafetyMaxEdtOsc.setText(splitData[21].substring(1, sensorLength + 1));
                                        mBinding.pidSafetyMaxDeciOsc.setText(splitData[21].substring(sensorLength + 2, sensorLength + 4));

                                        mBinding.pidSafetyMinvalueTBtn.setChecked((splitData[22].substring(0, 1)).equals("+"));
                                        mBinding.pidSafetyMinEdtOsc.setText(splitData[22].substring(1, sensorLength + 1));
                                        mBinding.pidSafetyMinDeciOsc.setText(splitData[22].substring(sensorLength + 2, sensorLength + 4));
                                    } else {
                                        mBinding.pidSetPointEdtOsc.setText(splitData[10].substring(0, sensorLength));
                                        mBinding.pidSetPointDeciOsc.setText(splitData[10].substring(sensorLength + 1, sensorLength + 3));

                                        mBinding.pidInputMinEdtOsc.setText(splitData[18].substring(0, sensorLength));
                                        mBinding.pidInputMinDeciOsc.setText(splitData[18].substring(sensorLength + 1, sensorLength + 3));

                                        mBinding.pidInputMaxEdtOsc.setText(splitData[19].substring(0, sensorLength));
                                        mBinding.pidInputMaxDeciOsc.setText(splitData[19].substring(sensorLength + 1, sensorLength + 3));

                                        mBinding.pidSafetyMaxEdtOsc.setText(splitData[21].substring(0, sensorLength));
                                        mBinding.pidSafetyMaxDeciOsc.setText(splitData[21].substring(sensorLength + 1, sensorLength + 3));

                                        mBinding.pidSafetyMinEdtOsc.setText(splitData[22].substring(0, sensorLength));
                                        mBinding.pidSafetyMinDeciOsc.setText(splitData[22].substring(sensorLength + 1, sensorLength + 3));
                                    }

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
                                    //mBinding.pidInputMinEdtOsc.setText(splitData[18]);
                                    //mBinding.pidInputMaxEdtOsc.setText(splitData[19]);
                                    mBinding.pidLockoutDelayEdtOsc.setText(splitData[20]);
                                    //mBinding.pidSafetyMaxEdtOsc.setText(splitData[21]);
                                    //mBinding.pidSafetyMinEdtOsc.setText(splitData[22]);

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
                                if (splitData[6].equals("0")) {
                                    enableAnalogDisable();
                                } else if (splitData[6].equals("2")) {
                                    enableAnalogTest();
                                /* if(Integer.parseInt(splitData[7]) < 18) {
                                    mBinding.testLinkInputRelayEdtOsc.setText(mBinding.testLinkInputRelayEdtOsc.getAdapter().getItem(Integer.parseInt(splitData[7]) - 1).toString());
                                }else{
                                    mBinding.testLinkInputRelayEdtOsc.setText(mBinding.testLinkInputRelayEdtOsc.getAdapter().getItem(Integer.parseInt(splitData[7]) - 33).toString());
                                } */
                                    mBinding.analogFixedValueEdtOsc.setText(splitData[7].substring(0, 2));
                                    mBinding.analogFixedValueDeciOsc.setText(splitData[7].substring(3, 5));
                                    // mBinding.testLinkInputRelayEdtOsc.setAdapter(getAdapter(sensorInputArr));
                                } else {
                                    enableAnalogMain();
                                    mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(Integer.parseInt(splitData[6])).toString());
                                    String gethardwareNo = splitData[7].substring(1, 3);
                                    if (splitData[7].startsWith("I")) {
                                        if (Integer.parseInt(gethardwareNo) < 18) {
                                            mBinding.analogLinkInputAtxtOsc.setText(mBinding.analogLinkInputAtxtOsc.getAdapter().getItem(Integer.parseInt(gethardwareNo) - 1).toString());
                                        } else {
                                            mBinding.analogLinkInputAtxtOsc.setText(mBinding.analogLinkInputAtxtOsc.getAdapter().getItem(Integer.parseInt(gethardwareNo) - 33).toString());
                                        }
                                    } else {
                                        mBinding.analogLinkInputAtxtOsc.setText(mBinding.analogLinkInputAtxtOsc.getAdapter().getItem(Integer.parseInt(gethardwareNo) + 24).toString());
                                    }
                                    mBinding.analogMinMaEdtOsc.setText(splitData[8].substring(0, 2));
                                    mBinding.analogMinMaDeciOsc.setText(splitData[8].substring(3, 5));

                                    mBinding.analogMaxMaEdtOsc.setText(splitData[9].substring(0, 2));
                                    mBinding.analogMaxMaDeciOsc.setText(splitData[9].substring(3, 5));
                                    setMaxLengthAnalog();
                                    if (inputType.equalsIgnoreCase("ORP") || inputType.equalsIgnoreCase("Temperature")) {
                                        mBinding.analogMinValueTBtn.setChecked((splitData[10].substring(0, 1)).equals("+"));
                                        mBinding.analogMinValueEdtOsc.setText(splitData[10].substring(1, sensorLength + 1));
                                        mBinding.analogMinValueDeciOsc.setText(splitData[10].substring(sensorLength + 2, sensorLength + 4));

                                        mBinding.analogMaxValueTBtn.setChecked((splitData[11].substring(0, 1)).equals("+"));
                                        mBinding.analogMaxValueEdtOsc.setText(splitData[11].substring(1, sensorLength + 1));
                                        mBinding.analogMaxValueDeciOsc.setText(splitData[11].substring(sensorLength + 2, sensorLength + 4));
                                    } else {
                                        mBinding.analogMinValueEdtOsc.setText(splitData[10].substring(0, sensorLength));
                                        mBinding.analogMinValueDeciOsc.setText(splitData[10].substring(sensorLength + 1, sensorLength + 3));

                                        mBinding.analogMaxValueEdtOsc.setText(splitData[11].substring(0, sensorLength));
                                        mBinding.analogMaxValueDeciOsc.setText(splitData[11].substring(sensorLength + 1, sensorLength + 3));
                                    }
                                    mBinding.analogLinkInputAtxtOsc.setAdapter(getAdapter(analogInputArr));
                                }
                                mBinding.modeOsATXT.setAdapter(getAdapter(modeAnalog));
                                break;
                            case "4"://Manual
                                enableManual();
                                break;
                        }
                        initAdapter();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    void setMaxLength() {
        String[] sensorLink = mBinding.sensorLinkInputSensorAtxtOsc.getText().toString().split("-");
        String[] inputhardwareNo = sensorLink[1].split("\\(");
        if (Integer.parseInt(inputhardwareNo[0].replaceAll("\\s", "")) < 26) {
            inputType = inputDAO.getInputType(Integer.parseInt(inputhardwareNo[0].replaceAll("\\s", "")));
        } else {
            inputType = virtualDAO.getInputType(Integer.parseInt(inputhardwareNo[0].replaceAll("\\s", "")));
        }
        sensorLayoutVisibility(false);
        mBinding.sensorSetPointEdtOsc.setText("");
        mBinding.sensorSafetyMinEdtOsc.setText("");
        mBinding.sensorSafetyMaxEdtOsc.setText("");
        mBinding.pidHysteresisEdtOsc.setText("");
        mBinding.sensorSetpointvalueTBtn.setChecked(true);
        mBinding.sensorSafetyMinTBtn.setChecked(true);
        mBinding.sensorSafetyMaxTBtn.setChecked(true);
        mBinding.pidHysteresisTBtn.setChecked(true);
        switch (inputType) {
            case "ORP":
                sensorLength = 4;
                sensorLayoutVisibility(true);
                break;
            case "Temperature":
                sensorLength = 3;
                sensorLayoutVisibility(true);
                break;
            case "Toroidal Conductivity":
                sensorLength = 7;
                break;
            case "Contacting Conductivity":
            case "Tank Level":
                sensorLength = 6;
                break;
            case "Modbus Sensor":
                sensorLength = 3;
                break;
            case "Flow/Water Meter":
                sensorLength = 10;
                break;
            default:
                sensorLength = 2;
                break;
        }
        mBinding.sensorSetPointEdtOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sensorLength)});
        mBinding.sensorSafetyMinEdtOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sensorLength)});
        mBinding.sensorSafetyMaxEdtOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sensorLength)});
        mBinding.pidHysteresisEdtOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sensorLength)});
    }

    void setMaxLengthPID() {
        String[] sensorLink = mBinding.pidLinkInputAtxtOsc.getText().toString().split("-");
        String[] inputhardwareNo = sensorLink[1].split("\\(");
        if (Integer.parseInt(inputhardwareNo[0].replaceAll("\\s", "")) < 18) {
            inputType = inputDAO.getInputType(Integer.parseInt(inputhardwareNo[0].replaceAll("\\s", "")));
        } else {
            inputType = virtualDAO.getInputType(Integer.parseInt(inputhardwareNo[0].replaceAll("\\s", "")));
        }
        sensorPidLayoutVisibility(false);
        mBinding.pidSetPointEdtOsc.setText("");
        mBinding.pidInputMinEdtOsc.setText("");
        mBinding.pidInputMaxEdtOsc.setText("");
        mBinding.pidSafetyMinEdtOsc.setText("");
        mBinding.pidSafetyMaxEdtOsc.setText("");
        mBinding.pidSetpointvalueTBtn.setChecked(true);
        mBinding.pidInputMaxvalueTBtn.setChecked(true);
        mBinding.pidInputMinvalueTBtn.setChecked(true);
        mBinding.pidSafetyMinvalueTBtn.setChecked(true);
        mBinding.pidSafetyMaxvalueTBtn.setChecked(true);
        switch (inputType) {
            case "ORP":
                sensorLength = 4;
                sensorPidLayoutVisibility(true);
                break;
            case "Temperature":
                sensorLength = 3;
                sensorPidLayoutVisibility(true);
                break;
            case "Toroidal Conductivity":
                sensorLength = 7;
                break;
            case "Contacting Conductivity":
            case "Tank Level":
                sensorLength = 6;
                break;
            case "Modbus Sensor":
                sensorLength = 3;
                break;
            case "Flow/Water Meter":
                sensorLength = 10;
                break;
            default:
                sensorLength = 2;
                break;
        }
        mBinding.pidSetPointEdtOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sensorLength)});
        mBinding.pidInputMinEdtOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sensorLength)});
        mBinding.pidInputMaxEdtOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sensorLength)});
        mBinding.pidSafetyMinEdtOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sensorLength)});
        mBinding.pidSafetyMaxEdtOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sensorLength)});
    }

    void setMaxLengthAnalog() {
        String[] sensorLink = mBinding.analogLinkInputAtxtOsc.getText().toString().split("-");
        if(!sensorLink[0].equals("Output")) {
            String[] inputhardwareNo = sensorLink[1].split("\\(");
            if (Integer.parseInt(inputhardwareNo[0].replaceAll("\\s", "")) < 18) {
                inputType = inputDAO.getInputType(Integer.parseInt(inputhardwareNo[0].replaceAll("\\s", "")));
            } else {
                inputType = virtualDAO.getInputType(Integer.parseInt(inputhardwareNo[0].replaceAll("\\s", "")));
            }
        } else {
            inputType = "";
        }
        sensorAnalogLayoutVisibility(false);
        mBinding.analogMinValueEdtOsc.setText("");
        mBinding.analogMaxValueEdtOsc.setText("");
        mBinding.analogMinValueDeciOsc.setText("");
        mBinding.analogMaxValueDeciOsc.setText("");
        mBinding.analogMinValueTBtn.setChecked(true);
        mBinding.analogMaxValueTBtn.setChecked(true);
        switch (inputType) {
            case "ORP":
                sensorLength = 4;
                sensorAnalogLayoutVisibility(true);
                break;
            case "Temperature":
                sensorLength = 3;
                sensorAnalogLayoutVisibility(true);
                break;
            case "Toroidal Conductivity":
                sensorLength = 7;
                break;
            case "Contacting Conductivity":
            case "Tank Level":
                sensorLength = 6;
                break;
            case "Modbus Sensor":
                sensorLength = 3;
                break;
            case "Flow/Water Meter":
                sensorLength = 10;
                break;
            default:
                sensorLength = 2;
                break;
        }
        mBinding.analogMinValueEdtOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sensorLength)});
        mBinding.analogMaxValueEdtOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sensorLength)});
        mBinding.analogMinValueDeciOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        mBinding.analogMaxValueDeciOsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
    }

    void sensorPidLayoutVisibility(boolean visibility) {
        if (visibility) {
            mBinding.pidSetpointvalueTBtn.setVisibility(View.VISIBLE);
            mBinding.pidInputMinvalueTBtn.setVisibility(View.VISIBLE);
            mBinding.pidInputMaxvalueTBtn.setVisibility(View.VISIBLE);
            mBinding.pidSafetyMinvalueTBtn.setVisibility(View.VISIBLE);
            mBinding.pidSafetyMaxvalueTBtn.setVisibility(View.VISIBLE);
        } else {
            mBinding.pidSetpointvalueTBtn.setVisibility(View.GONE);
            mBinding.pidInputMinvalueTBtn.setVisibility(View.GONE);
            mBinding.pidInputMaxvalueTBtn.setVisibility(View.GONE);
            mBinding.pidSafetyMinvalueTBtn.setVisibility(View.GONE);
            mBinding.pidSafetyMaxvalueTBtn.setVisibility(View.GONE);
        }
    }

    void sensorAnalogLayoutVisibility(boolean visibility) {
        if (visibility) {
            mBinding.analogMinValueTBtn.setVisibility(View.VISIBLE);
            mBinding.analogMaxValueTBtn.setVisibility(View.VISIBLE);
        } else {
            mBinding.analogMinValueTBtn.setVisibility(View.GONE);
            mBinding.analogMaxValueTBtn.setVisibility(View.GONE);
        }
    }

    void sensorLayoutVisibility(boolean visibility) {
        if (visibility) {
            mBinding.sensorSetpointvalueTBtn.setVisibility(View.VISIBLE);
            mBinding.sensorSafetyMinTBtn.setVisibility(View.VISIBLE);
            mBinding.sensorSafetyMaxTBtn.setVisibility(View.VISIBLE);
            mBinding.pidHysteresisTBtn.setVisibility(View.VISIBLE);
        } else {
            mBinding.sensorSetpointvalueTBtn.setVisibility(View.GONE);
            mBinding.sensorSafetyMinTBtn.setVisibility(View.GONE);
            mBinding.sensorSafetyMaxTBtn.setVisibility(View.GONE);
            mBinding.pidHysteresisTBtn.setVisibility(View.GONE);
        }
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
        } else if (Integer.parseInt(mBinding.contFlowRateEdtOsc.getText().toString()) < 1 ||
                Integer.parseInt(mBinding.contFlowRateEdtOsc.getText().toString()) > 100000000) {
            mAppClass.showSnackBar(getContext(), "Flow Rate values between 1 - 100000000");
            return false;
        } else if (isEmpty(mBinding.contDoseRateEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Dose Rate cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.contDoseRateEdtOsc.getText().toString()) < 1 ||
                Integer.parseInt(mBinding.contDoseRateEdtOsc.getText().toString()) > 100000000) {
            mAppClass.showSnackBar(getContext(), "Dose Rate values between 1 - 100000000");
            return false;
        } else if (isEmpty(mBinding.contDosePeriodEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Dose Periods cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.contDosePeriodEdtOsc.getText().toString()) < 1 ||
                Integer.parseInt(mBinding.contDosePeriodEdtOsc.getText().toString()) > 1440) {
            mAppClass.showSnackBar(getContext(), "Dose Periods values between  1 - 1440");
            return false;
        }
        if (Integer.parseInt(mBinding.contFlowRateEdtOsc.getText().toString()) == 100000000) {
            if (!isEmpty(mBinding.contFlowRateDeciOsc) && Integer.parseInt(mBinding.contFlowRateDeciOsc.getText().toString()) > 0) {
                mAppClass.showSnackBar(getContext(), "Flow Rate decimal values should be 0");
                return false;
            }
        }
        if (Integer.parseInt(mBinding.contDoseRateEdtOsc.getText().toString()) == 100000000) {
            if (!isEmpty(mBinding.contDoseRateDeciOsc) && Integer.parseInt(mBinding.contDoseRateDeciOsc.getText().toString()) > 0) {
                mAppClass.showSnackBar(getContext(), "Dose Rate decimal values should be 0");
                return false;
            }
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
        } else if (Integer.parseInt(mBinding.bleedBleedFlowRateEdtOsc.getText().toString()) > 100000) {
            mAppClass.showSnackBar(getContext(), "Bleed Flow Rate should be less than 100000");
            return false;
        } else if (isEmpty(mBinding.bleedTargetPPMEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Target PPM cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.bleedTargetPPMEdtOsc.getText().toString()) > 1000000) {
            mAppClass.showSnackBar(getContext(), "Target PPM should be less than 1000000");
            return false;
        } else if (isEmpty(mBinding.bleedConcentrationEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Concentration cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.bleedConcentrationEdtOsc.getText().toString()) > 100) {
            mAppClass.showSnackBar(getContext(), "Concentration should be less than 100");
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
        } else if (Integer.parseInt(mBinding.bleedPumpFlowRateEdtOsc.getText().toString()) < 1 ||
                Integer.parseInt(mBinding.bleedPumpFlowRateEdtOsc.getText().toString()) > 100000000) {
            mAppClass.showSnackBar(getContext(), "Pump Flow Rate values between 1 - 100000000");
            return false;
        }
        if (Integer.parseInt(mBinding.bleedBleedFlowRateEdtOsc.getText().toString()) == 0) {
            if (isEmpty(mBinding.bleedBleedFlowrateDeciOsc)) {
                mAppClass.showSnackBar(getContext(), "Bleed Flow Rate decimal values should be greater then 01");
                return false;
            } else if (Integer.parseInt(mBinding.bleedBleedFlowrateDeciOsc.getText().toString()) == 0) {
                mAppClass.showSnackBar(getContext(), "Bleed Flow Rate decimal values should be greater then 01");
                return false;
            }
        }
        if (Integer.parseInt(mBinding.bleedBleedFlowRateEdtOsc.getText().toString()) == 100000) {
            if (!isEmpty(mBinding.bleedBleedFlowrateDeciOsc) && Integer.parseInt(mBinding.bleedBleedFlowrateDeciOsc.getText().toString()) > 0) {
                mAppClass.showSnackBar(getContext(), "Bleed Flow Rate decimal values should be 0");
                return false;
            }
        }
        if (Integer.parseInt(mBinding.bleedTargetPPMEdtOsc.getText().toString()) == 1000000) {
            if (!isEmpty(mBinding.bleedTargetPPMDeciOsc) && Integer.parseInt(mBinding.bleedTargetPPMDeciOsc.getText().toString()) > 0) {
                mAppClass.showSnackBar(getContext(), "Target PPM decimal values should be 0");
                return false;
            }
        }
        if (Integer.parseInt(mBinding.bleedPumpFlowRateEdtOsc.getText().toString()) == 100000000) {
            if (!isEmpty(mBinding.bleedPumpFlowRateDeciOsc) && Integer.parseInt(mBinding.bleedPumpFlowRateDeciOsc.getText().toString()) > 0) {
                mAppClass.showSnackBar(getContext(), "Pump Flow Rate decimal values should be 0");
                return false;
            }
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
        } else if (isEmpty(mBinding.waterPumpFlowRateEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Pump Flow Rate cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.waterPumpFlowRateEdtOsc.getText().toString()) < 1 ||
                Integer.parseInt(mBinding.waterPumpFlowRateEdtOsc.getText().toString()) > 100000000) {
            mAppClass.showSnackBar(getContext(), "Pump Flow Rate values between 1 - 100000000");
            return false;
        } else if (isEmpty(mBinding.waterTargetPPMEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Target PPM cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.waterTargetPPMEdtOsc.getText().toString()) > 1000000) {
            mAppClass.showSnackBar(getContext(), "Target PPM should be less than 1000000");
            return false;
        } else if (isEmpty(mBinding.waterConcentrationEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Concentration cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.waterConcentrationEdtOsc.getText().toString()) > 100) {
            mAppClass.showSnackBar(getContext(), "Concentration should be less than 100");
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
        if (Integer.parseInt(mBinding.waterPumpFlowRateEdtOsc.getText().toString()) == 100000000) {
            if (!isEmpty(mBinding.waterPumpFlowRateDeciOsc) && Integer.parseInt(mBinding.waterPumpFlowRateDeciOsc.getText().toString()) > 0) {
                mAppClass.showSnackBar(getContext(), "Pump Flow Rate decimal values should be 0");
                return false;
            }
        }
        if (Integer.parseInt(mBinding.waterTargetPPMEdtOsc.getText().toString()) == 1000000) {
            if (!isEmpty(mBinding.waterTargetPPMDeciOsc) && Integer.parseInt(mBinding.waterTargetPPMDeciOsc.getText().toString()) > 0) {
                mAppClass.showSnackBar(getContext(), "Target PPM decimal values should be 0");
                return false;
            }
        }
        return true;
    }

    private boolean validation3() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output label cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorLinkInputSensorAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Link Input Sensor cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorSetPointEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Set Point cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorDoseTypeAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Dose Type cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.pidHysteresisEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Hysteresis cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorDutyCycleEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Duty Cycle cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.sensorDutyCycleEdtOsc.getText().toString()) > 100) {
            mAppClass.showSnackBar(getContext(), "Duty Cycle should be less than 100");
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
        } else if (Integer.parseInt(mBinding.sensorLockoutTimeDelayEdtOsc.getText().toString()) < 1 ||
                Integer.parseInt(mBinding.sensorLockoutTimeDelayEdtOsc.getText().toString()) > 86400) {
            mAppClass.showSnackBar(getContext(), "LockOut Time Delay values between 1 - 86400");
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
        } else if (Integer.parseInt(mBinding.pidGainEdtOsc.getText().toString()) > 1000) {
            mAppClass.showSnackBar(getContext(), "Gain should be less than 1000");
            return false;
        } else if (isEmpty(mBinding.pidIntegeralTimeEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Integral Time cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.pidIntegeralTimeEdtOsc.getText().toString()) > 1000) {
            mAppClass.showSnackBar(getContext(), "Integral Time should be less than 1000");
            return false;
        } else if (isEmpty(mBinding.pidDerivativeTimeEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Derivative Time cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.pidDerivativeTimeEdtOsc.getText().toString()) > 1000) {
            mAppClass.showSnackBar(getContext(), "Derivative Time should be less than 1000");
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
        } else if (Integer.parseInt(mBinding.pidMinOutputEdtOsc.getText().toString()) > 100) {
            mAppClass.showSnackBar(getContext(), "Output Min should be less than 100");
            return false;
        } else if (isEmpty(mBinding.pidMaxOutputEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Output Max cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.pidMaxOutputEdtOsc.getText().toString()) > 100) {
            mAppClass.showSnackBar(getContext(), "Output Max should be less than 100");
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
        } else if (Integer.parseInt(mBinding.pidLockoutDelayEdtOsc.getText().toString()) < 1 ||
                Integer.parseInt(mBinding.pidLockoutDelayEdtOsc.getText().toString()) > 86400) {
            mAppClass.showSnackBar(getContext(), "LockOut Time Delay values between 1 - 86400");
            return false;
        } else if (isEmpty(mBinding.pidDoseTypeAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Please select Dose Type");
            return false;
        } else if (isEmpty(mBinding.pidLinkInputAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Please select Link Input Sensor");
            return false;
        }


        if (Integer.parseInt(mBinding.pidGainEdtOsc.getText().toString()) == 0) {
            if (isEmpty(mBinding.pidGainDeciOsc)) {
                mAppClass.showSnackBar(getContext(), "Gain decimal values should be greater then 001");
                return false;
            } else if (Integer.parseInt(mBinding.pidGainDeciOsc.getText().toString()) == 0) {
                mAppClass.showSnackBar(getContext(), "Gain decimal values should be greater then 001");
                return false;
            }
        }
        if (Integer.parseInt(mBinding.pidGainEdtOsc.getText().toString()) == 1000) {
            if (!isEmpty(mBinding.pidGainDeciOsc) && Integer.parseInt(mBinding.pidGainDeciOsc.getText().toString()) > 0) {
                mAppClass.showSnackBar(getContext(), "Gain decimal values should be 0");
                return false;
            }
        }

        if (Integer.parseInt(mBinding.pidIntegeralTimeEdtOsc.getText().toString()) == 0) {
            if (isEmpty(mBinding.pidIntegeralTimeDeciOsc)) {
                mAppClass.showSnackBar(getContext(), "Integral Time decimal values should be greater then 001");
                return false;
            } else if (Integer.parseInt(mBinding.pidIntegeralTimeDeciOsc.getText().toString()) == 0) {
                mAppClass.showSnackBar(getContext(), "Integral Time decimal values should be greater then 001");
                return false;
            }
        }
        if (Integer.parseInt(mBinding.pidIntegeralTimeEdtOsc.getText().toString()) == 1000) {
            if (!isEmpty(mBinding.pidIntegeralTimeDeciOsc) && Integer.parseInt(mBinding.pidIntegeralTimeDeciOsc.getText().toString()) > 0) {
                mAppClass.showSnackBar(getContext(), "Integral Time decimal values should be 0");
                return false;
            }
        }
        if (Integer.parseInt(mBinding.pidDerivativeTimeEdtOsc.getText().toString()) == 0) {
            if (isEmpty(mBinding.pidDerivativeTimeDeciOsc)) {
                mAppClass.showSnackBar(getContext(), "Derivative Time decimal values should be greater then 001");
                return false;
            } else if (Integer.parseInt(mBinding.pidDerivativeTimeDeciOsc.getText().toString()) == 0) {
                mAppClass.showSnackBar(getContext(), "Derivative Time decimal values should be greater then 001");
                return false;
            }
        }
        if (Integer.parseInt(mBinding.pidDerivativeTimeEdtOsc.getText().toString()) == 1000) {
            if (!isEmpty(mBinding.pidDerivativeTimeDeciOsc) && Integer.parseInt(mBinding.pidDerivativeTimeDeciOsc.getText().toString()) > 0) {
                mAppClass.showSnackBar(getContext(), "Derivative Time decimal values should be 0");
                return false;
            }
        }
        return true;
    }

    private boolean validation5() {
        return true;
    }

    private boolean validation6() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output Label  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogLinkInputAtxtOsc)) {
            mAppClass.showSnackBar(getContext(), "Select the Link Input Relay");
            return false;
        } else if (isEmpty(mBinding.analogMinMaEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "min mA cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.analogMinMaEdtOsc.getText().toString()) < 4 ||
                Integer.parseInt(mBinding.analogMinMaEdtOsc.getText().toString()) > 20) {
            mAppClass.showSnackBar(getContext(), "min mA values between 4 - 20");
            return false;
        } else if (isEmpty(mBinding.analogMaxMaEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "max mA cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.analogMaxMaEdtOsc.getText().toString()) < 4 ||
                Integer.parseInt(mBinding.analogMaxMaEdtOsc.getText().toString()) > 20) {
            mAppClass.showSnackBar(getContext(), "max mA values between 4 - 20");
            return false;
        } else if (isEmpty(mBinding.analogMinValueEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Min Value cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogMaxValueEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Max Value  cannot be Empty");
            return false;
        }
        if (Integer.parseInt(mBinding.analogMinMaEdtOsc.getText().toString()) == 20) {
            if (!isEmpty(mBinding.analogMinMaDeciOsc) && Integer.parseInt(mBinding.analogMinMaDeciOsc.getText().toString()) > 0) {
                mAppClass.showSnackBar(getContext(), "min mA decimal values should be 0");
                return false;
            }
        }
        if (Integer.parseInt(mBinding.analogMaxMaEdtOsc.getText().toString()) == 20) {
            if (!isEmpty(mBinding.analogMaxMaDeciOsc) && Integer.parseInt(mBinding.analogMaxMaDeciOsc.getText().toString()) > 0) {
                mAppClass.showSnackBar(getContext(), "max mA decimal values should be 0");
                return false;
            }
        }
        return true;
    }

    private boolean validation7() {

        /*else if (isEmpty(mBinding.testLinkInputRelayEdtOsc)) {
            mAppClass.showSnackBar(getContext(), "Select the Link Input Relay");
            return false;
        }   24/11 */

        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output Label  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogFixedValueEdtOsc)) {
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
      /*  if (!(currentFunctionMode.equals("Disabled"))) {
            if (isEmpty(mBinding.interLockChannelOsATXT)) {
                mAppClass.showSnackBar(getContext(), "Please select the Interlock Channel");
                return false;
            } else if (isEmpty(mBinding.activateChannelOsATXT)) {
                mAppClass.showSnackBar(getContext(), "Please select the Activate Channel");
                return false;
            } else if (formDigits(2, "" + getInterlockChannaleHardwareNumber()).equalsIgnoreCase(formDigits(2, "" + getActiviateChannaleHardwareNumber()))) {
                mAppClass.showSnackBar(getContext(), "Please choose different Activate With Channel output");
                return false;
            }
        }*/
        if (!(currentFunctionMode.equals("Disabled") || currentFunctionMode.equalsIgnoreCase("Manual"))) {
            if (isEmpty(mBinding.modeOsATXT)) {
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
        String linkInputSensor = "", subValue1 = "";
        boolean tbtn = false;
        try {
            if (!(mBinding.funtionModeOsATXT.getText().toString().equals("Analog") &&
                    (mBinding.modeOsATXT.getText().toString().equalsIgnoreCase("Disable") ||
                            mBinding.modeOsATXT.getText().toString().equalsIgnoreCase("Test")))) {
                linkInputSensor = toString(mBinding.analogLinkInputAtxtOsc);
            }
            if (inputType.equalsIgnoreCase("ORP") || inputType.equalsIgnoreCase("Temperature")) {
                tbtn = true;
            }
            if (mBinding.funtionModeOsATXT.getText().toString().equalsIgnoreCase("Sensor")) {
                switch (getPosition(1, toString(mBinding.modeOsATXT), modeSensor)) {
                    case "0":
                        String setpoint = toString(mBinding.sensorSetPointEdtOsc) + "." + mBinding.sensorSetPointDeciOsc.getText().toString() + "$" +
                                formDigits(2, getLinkInputSensor(mBinding.sensorLinkInputSensorAtxtOsc));
                        subValue1 = tbtn ? ((mBinding.sensorSetpointvalueTBtn.isChecked() ? "+" : "-") + setpoint) :
                                setpoint;
                        break;
                    case "1":
                        String pidsetpoint = toString(mBinding.pidSetPointEdtOsc) + "." + mBinding.pidSetPointDeciOsc.getText().toString() + "$" +
                                formDigits(2, getLinkInputSensor(mBinding.pidLinkInputAtxtOsc));
                        subValue1 = tbtn ? ((mBinding.pidSetpointvalueTBtn.isChecked() ? "+" : "-") + pidsetpoint) :
                                pidsetpoint;
                        break;
                    default:
                        break;
                }
            } else if (mBinding.funtionModeOsATXT.getText().toString().equalsIgnoreCase("Inhibitor")) {
                switch (getPosition(1, toString(mBinding.modeOsATXT), modeInhibitor)) {
                    case "0":
                        subValue1 = toString(mBinding.contDosePeriodEdtOsc) + "$" +
                                toString(mBinding.contDoseRateEdtOsc) + "." + mBinding.contDoseRateDeciOsc.getText().toString();
                        break;
                    case "1":
                        subValue1 = toString(mBinding.bleedTargetPPMEdtOsc) + "." + mBinding.bleedTargetPPMDeciOsc.getText().toString();
                        break;
                    case "2":
                        subValue1 = toString(mBinding.waterTargetPPMEdtOsc) + "." + mBinding.waterTargetPPMDeciOsc.getText().toString();
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        OutputConfigurationEntity entityUpdate = new OutputConfigurationEntity
                (outputSensorNo, "Output- " + outputSensorNo + " (" + toString(mBinding.outputLabelOsEDT) + ")", toString(0, mBinding.outputLabelOsEDT),
                        (mBinding.funtionModeOsATXT.getText().toString().equals("Analog") ? mBinding.modeOsATXT.getText().toString() : subValue1),
                        (mBinding.funtionModeOsATXT.getText().toString().equals("Analog") ? linkInputSensor : (mBinding.modeOsATXT.getText().toString().equals("")
                                ? mBinding.funtionModeOsATXT.getText().toString() : mBinding.modeOsATXT.getText().toString())), STARTPACKET + writePacket + ENDPACKET);
        List<OutputConfigurationEntity> entryListUpdate = new ArrayList<>();
        entryListUpdate.add(entityUpdate);
        updateToDb(entryListUpdate);
        new EventLogDemo(String.valueOf(outputSensorNo), "output-" + outputSensorNo, "Output Setting Changed",
                SharedPref.read(pref_USERLOGINID, ""),getContext());
        ApiService.tempString = "0";
        ApiService.getInstance(getContext()).processApiData(READ_PACKET, "05", "Output Setting Changed - " +
                SharedPref.read(pref_USERLOGINID, ""));
    }

}