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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentOutputConfigBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.ApplicationClass.bleedRelay;
import static com.ionexchange.Others.ApplicationClass.doseTypeArr;
import static com.ionexchange.Others.ApplicationClass.flowMeters;
import static com.ionexchange.Others.ApplicationClass.functionMode;
import static com.ionexchange.Others.ApplicationClass.inputSensors;
import static com.ionexchange.Others.ApplicationClass.interlockChannel;
import static com.ionexchange.Others.ApplicationClass.linkBleedRelay;
import static com.ionexchange.Others.ApplicationClass.modeAnalog;
import static com.ionexchange.Others.ApplicationClass.modeInhibitor;
import static com.ionexchange.Others.ApplicationClass.modeSensor;
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
    String lInhibitorContinuous = "layoutInhibitorContinuous", lInhibitorBleed = "layoutInhibitorBleedDown", lInhibitorWaterFlow = "layoutInhibitorWaterFlow",
            lSensorOnOFF = "layoutSensorOnOff", lSensorPid = "layoutSensorPID", lAnalogMain = "layoutAnalogMain", lAnalogTest = "layoutAnalogTest", lAnalogDisable = "layoutAnalogDisable",
            currentFunctionMode = "";
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
        initAdapter();
        enableInhibitorLayout();
        mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(1).toString());
        initAdapter();
        mBinding.funtionModeOsATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                switch (pos) {
                    case 0:
                        mAppClass.showSnackBar(getContext(), "DISABLED");
                        break;
                    case 1:
                        enableInhibitorLayout();
                        break;
                    case 2:
                        enableSensorLayout();
                        break;
                    case 3:
                        enableAnalogLayout();
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

        mBinding.saveFabCommonSettings.setOnClickListener(this::save);
        mBinding.saveLayoutCommonSettings.setOnClickListener(this::save);
    }

    private void enableAnalogMain() {
        mBinding.setFunctionMode(lAnalogMain);
        mBinding.linkOutAnalogOsATXT.setAdapter(getAdapter(inputSensors));
    }

    private void enableAnalogTest() {
        mBinding.setFunctionMode(lAnalogTest);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(2).toString());
        mBinding.linkOutAnalogTestTie.setAdapter(getAdapter(inputSensors));
    }

    private void enableAnalogDisable() {
        mBinding.setFunctionMode(lAnalogDisable);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.linkOutAnalogDisabledTie.setAdapter(getAdapter(inputSensors));
    }

    private void save(View view) {
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


    private void sendAnalogTest() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_OUTPUT_CONFIG + SPILT_CHAR + "01" + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeAnalog) + SPILT_CHAR +
                getPosition(2, toString(mBinding.linkOutAnalogTestTie), inputSensors) + SPILT_CHAR +
                toString(4, mBinding.fixedValueTie));

    }

    private void sendAnalogDisable() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_OUTPUT_CONFIG + SPILT_CHAR + "01" + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeAnalog) + SPILT_CHAR +
                getPosition(2, toString(mBinding.linkOutAnalogDisabledTie), inputSensors));
    }

    private void sendAnalogValue() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_OUTPUT_CONFIG + SPILT_CHAR + "01" + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeAnalog) + SPILT_CHAR +
                getPosition(2, toString(mBinding.linkOutAnalogOsATXT), inputSensors) + SPILT_CHAR +
                toString(6, mBinding.minmAAnalogOsATXT) + SPILT_CHAR +
                toString(6, mBinding.maxmAAnalogOsATXT) + SPILT_CHAR +
                toString(6, mBinding.minValueAnalogOsATXT) + SPILT_CHAR +
                toString(6, mBinding.maxValueAnalogOsATXT));
    }

    private void sendFuzzy() {
        /*Still in Development*/
    }

    private void sendPID() {
        // Sensor - PID
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_OUTPUT_CONFIG + SPILT_CHAR + "01" + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                getPosition(2, toString(mBinding.linkInputPidOsATXT),inputSensors) + SPILT_CHAR+

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

    private void sendOnOFf() {
        // Sensor - On/Off
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_OUTPUT_CONFIG + SPILT_CHAR + "01" + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                getPosition(2, toString(mBinding.linkInputOnOffOsATXT), inputSensors) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeSensor) + SPILT_CHAR +
                toString(4, mBinding.targetPPMOnOFfOsATXT) + SPILT_CHAR +
                getPosition(0, toString(mBinding.doseTypeSensorOsATXT), doseTypeArr) + SPILT_CHAR +
                toString(5, mBinding.hysteresisSensorOsATXT) + SPILT_CHAR +
                toString(3, mBinding.dutyCycleSensorOsATXT) + SPILT_CHAR +
                toString(7, mBinding.lockOutTimeDelaySensorOsATXT) + SPILT_CHAR +
                toString(6, mBinding.safetyMaxSensorOsATXT) + SPILT_CHAR +
                toString(6, mBinding.safetyMinSensorOsATXT));
    }

    private void sendWaterMeter() {
        // Water Meter / BioCide
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_OUTPUT_CONFIG + SPILT_CHAR + "01" + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeInhibitor) + SPILT_CHAR +
                getPosition(2, toString(mBinding.flowMeterInputWaterOsATXT), flowMeters) + SPILT_CHAR +
                getPosition(2, toString(mBinding.bleedRelayTie), bleedRelay) + SPILT_CHAR +
                toString(4, mBinding.flowRateWaterOsATXT) + SPILT_CHAR +
                toString(3, mBinding.targetPPMWaterOsATXT) + SPILT_CHAR +
                toString(4, mBinding.concentrationWaterOsATXT) + SPILT_CHAR +
                toString(3, mBinding.GravityWaterOsATXT));


    }

    /* Bleed Blow */
    private void sendBleedBlow() {
        // Bleed/Blown -

        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_OUTPUT_CONFIG + SPILT_CHAR + "01" + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeInhibitor) + SPILT_CHAR +
                getPosition(2, toString(mBinding.LinkbleedBleedOsATXT), linkBleedRelay) + SPILT_CHAR +
                toString(3, mBinding.bleedFlowBleedOsATXT) + SPILT_CHAR +
                toString(4, mBinding.flowRateBleedOsATXT) + SPILT_CHAR +
                toString(4, mBinding.targetppmBleedOsATXT) + SPILT_CHAR +
                toString(3, mBinding.concentrationBleedOsATXT) + SPILT_CHAR +
                toString(3, mBinding.specificBleedOsATXT));
    }

    /* WriteData */
    private void sendContinuous() {
        // Con - {*1234# 0# 06# 01# Output1# 30# 30# 1# 0# 125# 322# 212*}

        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_OUTPUT_CONFIG + SPILT_CHAR + "01" + SPILT_CHAR +
                toString(0, mBinding.outputLabelOsEDT) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.interLockChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                (Integer.parseInt(getPosition(2, toString(mBinding.activateChannelOsATXT), interlockChannel)) + 30) + SPILT_CHAR +
                getPosition(0, toString(mBinding.funtionModeOsATXT), functionMode) + SPILT_CHAR +
                getPosition(0, toString(mBinding.modeOsATXT), modeInhibitor) + SPILT_CHAR +
                toString(4, mBinding.flowRateContOsATXT) + SPILT_CHAR +
                toString(3, mBinding.doseRateContOsATXT) + SPILT_CHAR +
                toString(3, mBinding.dosePeriodOsATXT));
    }

    private void enableOnOff() {
        mBinding.setFunctionMode(lSensorOnOFF);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeSensor));
        mBinding.linkInputOnOffOsATXT.setAdapter(getAdapter(inputSensors));
        mBinding.doseTypeSensorOsATXT.setAdapter(getAdapter(doseTypeArr));
        mBinding.linkInputPidOsATXT.setAdapter(getAdapter(inputSensors));
        mBinding.doseTypePidOsATXT.setAdapter(getAdapter(doseTypeArr));

    }

    private void enablePID() {
        mBinding.setFunctionMode(lSensorPid);
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(1).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeSensor));
        mBinding.doseTypePidOsATXT.setAdapter(getAdapter(doseTypeArr));
        mBinding.linkInputPidOsATXT.setAdapter(getAdapter(inputSensors));
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
        mBinding.LinkbleedBleedOsATXT.setAdapter(getAdapter(linkBleedRelay));
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
        mBinding.linkInputOnOffOsATXT.setAdapter(getAdapter(inputSensors));
        mBinding.doseTypeSensorOsATXT.setAdapter(getAdapter(doseTypeArr));
        mBinding.linkInputPidOsATXT.setAdapter(getAdapter(inputSensors));
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
        mBinding.linkOutAnalogDisabledTie.setAdapter(getAdapter(inputSensors));
        mBinding.modeOsATXT.setAdapter(getAdapter(modeAnalog));
    }

    private String getPosition(int digit, String string, String[] strArr) {
        String j = null;
        for (int i = 0; i < strArr.length; i++) {
            if (string.equals(strArr[i])) {
                j = String.valueOf(i);
            }
        }
        return mAppClass.formDigits(digit, j);
    }

    private String toString(int digits, EditText editText) {
        return mAppClass.formDigits(digits, editText.getText().toString());
    }

    private String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    private void initAdapter() {
        mBinding.interLockChannelOsATXT.setAdapter(getAdapter(interlockChannel));
        mBinding.activateChannelOsATXT.setAdapter(getAdapter(interlockChannel));
        mBinding.funtionModeOsATXT.setAdapter(getAdapter(functionMode));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_OUTPUT_CONFIG + SPILT_CHAR + "01");
    }

    @Override
    public void OnDataReceive(String data) {

        if (data != null) {
            handleResponse(data.split("\\*")[1].split("#"));
        }

    }

    private void handleResponse(String[] splitData) {
        // Read - Inhibitor -  {*1# 06# 0# 01# Output1# 30# 30# 1# 0# 125# 322# 212*}
        // Read - BleedBlow -  {*1# 06# 0# 03# Output3# 30# 30# 1# 1# 01# 01# 23# 52# 232# 10# 52*}
        // Read - Water/Flow -
        if (splitData[1].equals(PCK_OUTPUT_CONFIG)) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.outputLabelOsEDT.setText(splitData[4]);
                    mBinding.interLockChannelOsATXT.setText(mBinding.interLockChannelOsATXT.getAdapter().getItem(Integer.parseInt(splitData[5]) - 30).toString());
                    mBinding.activateChannelOsATXT.setText(mBinding.interLockChannelOsATXT.getAdapter().getItem(Integer.parseInt(splitData[6]) - 30).toString());
                    mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(Integer.parseInt(splitData[7])).toString());
                    switch (splitData[7]) {
                        case "0": // Disable
                            // FIXME: 05-08-2021 TODO
                            break;
                        case "1": // Inhibitor
                            enableInhibitorLayout();
                            if (splitData[8].equals("0")) {// Continious
                                enableContinuous();
                                mBinding.flowRateContOsATXT.setText(splitData[9]);
                                mBinding.doseRateContOsATXT.setText(splitData[10]);
                                mBinding.dosePeriodOsATXT.setText(splitData[11]);
                            } else if (splitData[8].equals("1")) { // Bleed/Blow
                                enableBleed();
                                mBinding.LinkbleedBleedOsATXT.setText(mBinding.LinkbleedBleedOsATXT.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());
                                mBinding.bleedFlowBleedOsATXT.setText(splitData[10]);
                                mBinding.flowRateBleedOsATXT.setText(splitData[11]);
                                mBinding.targetppmBleedOsATXT.setText(splitData[12]);
                                mBinding.concentrationBleedOsATXT.setText(splitData[13]);
                                mBinding.specificBleedOsATXT.setText(splitData[14]);
                                mBinding.LinkbleedBleedOsATXT.setAdapter(getAdapter(linkBleedRelay));
                            } else if (splitData[8].equals("2")) { // Water/Meter
                                enableWater();
                                mBinding.flowMeterInputWaterOsATXT.setText(mBinding.flowMeterInputWaterOsATXT.getAdapter().getItem(Integer.parseInt(splitData[9]) - 1).toString());
                                mBinding.bleedRelayTie.setText(mBinding.bleedRelayTie.getAdapter().getItem(Integer.parseInt(splitData[10])).toString());
                                mBinding.flowRateWaterOsATXT.setText(splitData[11]);
                                mBinding.targetPPMWaterOsATXT.setText(splitData[12]);
                                mBinding.concentrationWaterOsATXT.setText(splitData[13]);
                                mBinding.GravityWaterOsATXT.setText(splitData[14]);
                                mBinding.flowMeterInputWaterOsATXT.setAdapter(getAdapter(flowMeters));
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
                                mBinding.safetyMaxSensorOsATXT.setText(splitData[15]);
                                mBinding.safetyMinSensorOsATXT.setText(splitData[16]);

                                mBinding.doseTypeSensorOsATXT.setAdapter(getAdapter(doseTypeArr));
                                mBinding.linkInputOnOffOsATXT.setAdapter(getAdapter(inputSensors));
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
                            } else if (splitData[9].equals("2")) {
                                enableFuzzy();
                                // FIXME: 05-08-2021 Still Development
                            }

                            break;
                        case "3": // Analog
                            enableAnalogLayout();
                            if (splitData[8].equals("0")) {
                                enableAnalogDisable();
                                mBinding.linkOutAnalogDisabledTie.setText(mBinding.linkOutAnalogDisabledTie.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());
                            } else if (splitData[8].equals("2")) {
                                enableAnalogTest();
                                mBinding.linkOutAnalogTestTie.setText(mBinding.linkOutAnalogTestTie.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());
                                mBinding.fixedValueTie.setText(splitData[10]);
                                mBinding.linkOutAnalogTestTie.setAdapter(getAdapter(inputSensors));
                            } else {
                                enableAnalogMain();
                                mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(Integer.parseInt(splitData[8])).toString());
                                mBinding.linkOutAnalogOsATXT.setText(mBinding.linkOutAnalogOsATXT.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());
                                mBinding.minmAAnalogOsATXT.setText(splitData[10]);
                                mBinding.maxmAAnalogOsATXT.setText(splitData[11]);
                                mBinding.minValueAnalogOsATXT.setText(splitData[12]);
                                mBinding.maxValueAnalogOsATXT.setText(splitData[13]);
                                mBinding.linkOutAnalogOsATXT.setAdapter(getAdapter(inputSensors));
                            }
                            mBinding.modeOsATXT.setAdapter(getAdapter(modeAnalog));
                            break;
                    }
                    initAdapter();
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Read Failed !");
                }

            } else if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), "Read Success !");

                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Write Failed !");
                }
            }
        } else {
            Log.e(TAG, "handleResponse: ");
        }

    }

    private Boolean isEmpty(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        return false;
    }

    boolean validation() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output Label  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.flowRateContOsATXT)) {
            mAppClass.showSnackBar(getContext(), "FlowRate cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.doseRateContOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Dose Rate cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.dosePeriodOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Dose Periods cannot be Empty");
            return false;
        }
        return true;
    }

    private boolean validation1() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output Label  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.bleedFlowBleedOsATXT)) {
            mAppClass.showSnackBar(getContext(), "bleed Flow cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.flowRateBleedOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Flow Rate cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.targetppmBleedOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Target ppm cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.concentrationBleedOsATXT)) {
            mAppClass.showSnackBar(getContext(), "concentration ppm cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.specificBleedOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Specific Bleed  cannot be Empty");
            return false;
        }
        return true;
    }

    private boolean validation2() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output Label  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.flowMeterInputWaterOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Flow Meter cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.targetPPMWaterOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Target ppm cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.concentrationWaterOsATXT)) {
            mAppClass.showSnackBar(getContext(), "concentration ppm cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.GravityWaterOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Specific Gravity  cannot be Empty");
            return false;
        }
        return true;
    }

    private boolean validation3() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output Label  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.targetPPMOnOFfOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Set Point cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.hysteresisSensorOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Hysteresis cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.dutyCycleSensorOsATXT)) {
            mAppClass.showSnackBar(getContext(), "duty Cycle cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.lockOutTimeDelaySensorOsATXT)) {
            mAppClass.showSnackBar(getContext(), "LockOut Time  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.safetyMaxSensorOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Safety max  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.safetyMinSensorOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Safety Min  cannot be Empty");
            return false;
        }
        return true;
    }

    private boolean validation4() {
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output Label  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.setPointPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Set Point cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.gainPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Gain Pid cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.integeralPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Integeral Pid cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.resetPidPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "reset Pid  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.outputMinPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Output Min  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.outputMaxPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Output Max cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.inputMinPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Input min  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.inputMaxPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Input Max  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.lockOutDelayPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Lock out Delay  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.safetyMinPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "safety Min  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.safetyMaxPidOsATXT)) {
            mAppClass.showSnackBar(getContext(), "safety Max  cannot be Empty");
            return false;
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
        } else if (isEmpty(mBinding.minmAAnalogOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Min analog cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.maxmAAnalogOsATXT)) {
            mAppClass.showSnackBar(getContext(), "Max analog cannot be Empty");
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
        if (isEmpty(mBinding.outputLabelOsEDT)) {
            mAppClass.showSnackBar(getContext(), "Output Label  cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.fixedValueTie)) {
            mAppClass.showSnackBar(getContext(), "Fixed value cannot be Empty");
            return false;
        }

        return true;
    }
}