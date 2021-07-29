
package com.ionexchange.Fragments.Configuration.InputConfig;

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
import com.ionexchange.databinding.FragmentInputsensorFlowBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.ApplicationClass.flowMeterTypeArr;
import static com.ionexchange.Others.ApplicationClass.flowUnitArr;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.scheduleResetArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorFlow_config extends Fragment implements DataReceiveCallback {
    FragmentInputsensorFlowBinding mBinding;
    ApplicationClass mAppClass;
    private static final String TAG = "FragmentInputSensorFlow";

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
        initAdapter();
        mBinding.flowMeterTypeFlowISATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBinding.setFlowMeterType(String.valueOf(i));
            }
        });
        mBinding.saveFabFlowIS.setOnClickListener(this::save);
        mBinding.saveLayoutFlowIS.setOnClickListener(this::save);
        mBinding.DeleteFabFlowIS.setOnClickListener(this::delete);
        mBinding.DeleteLayoutFlowIS.setOnClickListener(this::delete);
    }

    private void delete(View view) {

    }

    private void save(View view) {

        switch (getPosition(toString(mBinding.flowMeterTypeFlowISATXT), flowMeterTypeArr)) {
            case 0:
                sendAnalogPacket();
                break;
            case 1:
                sendContactorPacket();
                break;

            case 2:
                sendPaddleWheelPacket();
                break;

            case 3:
                sendFeedMonitorPacket();
                break;
        }
    }

    private void sendFeedMonitorPacket() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(mBinding.inputNumberFlowISEDT) + SPILT_CHAR +
                "03" + SPILT_CHAR +
                getPosition(toString(mBinding.flowMeterTypeFlowISATXT), flowMeterTypeArr) + SPILT_CHAR +
                getPosition(toString(mBinding.sensorActivationFlowISATXT), sensorActivationArr) + SPILT_CHAR +
                toString(mBinding.inputLabelFlowISEdt) + SPILT_CHAR +
                getPosition(toString(mBinding.flowUnitFlowISATXT), flowUnitArr) + SPILT_CHAR +
                toString(mBinding.rateUnitPaddleFlowISEdt) + SPILT_CHAR +
                toString(mBinding.totalAlarmModeFeedFlowISEdt) + SPILT_CHAR +
                toString(mBinding.flowAlarmModeFeedFlowISEdt) + SPILT_CHAR +
                toString(mBinding.flowAlarmDelayFeedISEdt) + SPILT_CHAR +
                toString(mBinding.flowAlarmClearFeedFlowISEdt) + SPILT_CHAR +
                toString(mBinding.outputRelayLinkFeedFlowISEdt) + SPILT_CHAR +
                toString(mBinding.totalizerAlarmFlowISEdt) + SPILT_CHAR +
                toString(mBinding.resetFlowTotalFlowISEdt) + SPILT_CHAR +
                toString(mBinding.setFlowTotalFeedIsEDT) + SPILT_CHAR +
                getPosition(toString(mBinding.scheduleResetFlowISEdt), scheduleResetArr) + SPILT_CHAR +
                toString(mBinding.alarmLowFeedFlowISEdt) + SPILT_CHAR +
                toString(mBinding.alarmHighFeedFlowISEdt));
    }

    private void sendPaddleWheelPacket() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(mBinding.inputNumberFlowISEDT) + SPILT_CHAR +
                "03" + SPILT_CHAR +
                getPosition(toString(mBinding.flowMeterTypeFlowISATXT), flowMeterTypeArr) + SPILT_CHAR +
                getPosition(toString(mBinding.sensorActivationFlowISATXT), sensorActivationArr) + SPILT_CHAR +
                toString(mBinding.inputLabelFlowISEdt) + SPILT_CHAR +
                getPosition(toString(mBinding.flowUnitFlowISATXT), flowUnitArr) + SPILT_CHAR +
                toString(mBinding.rateUnitPaddleFlowISEdt) + SPILT_CHAR +
                toString(mBinding.kFactorFlowISEdt) + SPILT_CHAR +
                toString(mBinding.totalizerAlarmFlowISEdt) + SPILT_CHAR +
                toString(mBinding.resetFlowTotalFlowISEdt) + SPILT_CHAR +
                toString(mBinding.setFlowTotalPaddleFlowISEdt) + SPILT_CHAR +
                getPosition(toString(mBinding.scheduleResetFlowISEdt), scheduleResetArr) + SPILT_CHAR +
                toString(mBinding.alarmLowPaddleFlowISEdt) + SPILT_CHAR +
                toString(mBinding.alarmHighPaddleFlowISEdt));
    }

    private void sendContactorPacket() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(mBinding.inputNumberFlowISEDT) + SPILT_CHAR +
                "03" + SPILT_CHAR +
                getPosition(toString(mBinding.flowMeterTypeFlowISATXT), flowMeterTypeArr) + SPILT_CHAR +
                getPosition(toString(mBinding.sensorActivationFlowISATXT), sensorActivationArr) + SPILT_CHAR +
                toString(mBinding.inputLabelFlowISEdt) + SPILT_CHAR +
                getPosition(toString(mBinding.flowUnitFlowISATXT), flowUnitArr) + SPILT_CHAR +
                toString(mBinding.volumeFlowISEdt) + SPILT_CHAR +
                toString(mBinding.totalizerAlarmFlowISEdt) + SPILT_CHAR +
                toString(mBinding.resetFlowTotalFlowISEdt) + SPILT_CHAR +
                toString(mBinding.setFlowTotalContactorFlowISEdt) + SPILT_CHAR +
                getPosition(toString(mBinding.scheduleResetFlowISEdt), scheduleResetArr) + SPILT_CHAR +
                toString(mBinding.alarmLowContactorFlowISEdt) + SPILT_CHAR +
                toString(mBinding.alarmHighContactorFlowISEdt));
    }

    private void sendAnalogPacket() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(mBinding.inputNumberFlowISEDT) + SPILT_CHAR +
                "03" + SPILT_CHAR +
                getPosition(toString(mBinding.flowMeterTypeFlowISATXT), flowMeterTypeArr) + SPILT_CHAR +
                getPosition(toString(mBinding.sensorActivationFlowISATXT), sensorActivationArr) + SPILT_CHAR +
                toString(mBinding.inputLabelFlowISEdt) + SPILT_CHAR +
                getPosition(toString(mBinding.flowUnitFlowISATXT), flowUnitArr) + SPILT_CHAR +
                toString(mBinding.rateUnitAnalogFlowISEdt) + SPILT_CHAR +
                toString(mBinding.flowMaxAnalogFlowISEdt) + SPILT_CHAR +
                toString(mBinding.flowMinAnalogFlowISEdt) + SPILT_CHAR +
                toString(mBinding.smoothingFactorAnalogFlowISEdt) + SPILT_CHAR +
                toString(mBinding.totalizerAlarmFlowISEdt) + SPILT_CHAR +
                toString(mBinding.resetFlowTotalFlowISEdt) + SPILT_CHAR +
                getPosition(toString(mBinding.scheduleResetFlowISEdt), scheduleResetArr) + SPILT_CHAR +
                toString(mBinding.setFlowTotalAnalogFlowISEdt) + SPILT_CHAR +
                toString(mBinding.alarmLowAnalogFlowISEdt) + SPILT_CHAR +
                toString(mBinding.alarmHighAnalogFlowISEdt) + SPILT_CHAR +
                toString(mBinding.calibrationRequiredFlowISEdt) + SPILT_CHAR +
                getPosition(toString(mBinding.resetCalibrationAnalogFlowISEdt), resetCalibrationArr));
    }

    private String toString(EditText editText) {
        return editText.getText().toString();
    }

    private String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    private int getPosition(String string, String[] strArr) {
        int i;
        for (i = 0; i < strArr.length; i++) {
            if (string.equals(strArr[i])) {
                return i;
            }
        }
        return i;
    }

    private void initAdapter() {
        mBinding.flowMeterTypeFlowISATXT.setAdapter(getAdapter(flowMeterTypeArr));
        mBinding.sensorTypeFlowISATXT.setAdapter(getAdapter(inputTypeArr));
        mBinding.sensorActivationFlowISATXT.setAdapter(getAdapter(sensorActivationArr));
        mBinding.flowUnitFlowISATXT.setAdapter(getAdapter(flowUnitArr));
        mBinding.scheduleResetFlowISEdt.setAdapter(getAdapter(scheduleResetArr));
        mBinding.resetCalibrationAnalogFlowISEdt.setAdapter(getAdapter(resetCalibrationArr));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "07");
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleResponse(data.split("\\*")[1].split("#"));
        }
    }

    private void handleResponse(String[] splitData) {
        /* READ_RES */
        //  - Analog->      {*1# 04# 0# | 07# 03# 0# 0# AnalogInput1# 1# | 1000# 1500# 3000# 100# 2000# 4000# 1# 1200# 120000# 240000# 333# 1*}
        //  - Contactor->   {*1# 04# 0# | 07# 03# 1# 0# DigitalInput2# 1#| 100000# 2000# 4000# 4000# 1# 120000# 240000*}
        //  - paddle wheel-> {*1# 04# 0# | 07# 03# 2# 0# DigitalInput2# 1#| 1000# 80# 2000# 4000# 4000# 1# 120000# 240000*}
        //  - FeedMonitor ->{*1# 04# 0# | 07# 03# 3# 0# DigitalInput2# 1#| 1000# 01# 01# 2230# 240000# 00# 2000# 4000# 4000# 1# 120000# 240000*}
        if (splitData[1].equals(INPUT_SENSOR_CONFIG)) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.setFlowMeterType(splitData[5]);
                    // Alarm Low/Alarm High
                    mBinding.inputNumberFlowISEDT.setText(splitData[3]);
                    mBinding.sensorTypeFlowISATXT.setText(mBinding.sensorTypeFlowISATXT.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                    mBinding.flowMeterTypeFlowISATXT.setText(mBinding.flowMeterTypeFlowISATXT.getAdapter().getItem(Integer.parseInt(splitData[5])).toString());
                    mBinding.sensorActivationFlowISATXT.setText(mBinding.sensorActivationFlowISATXT.getAdapter().getItem(Integer.parseInt(splitData[6])).toString());
                    mBinding.inputLabelFlowISEdt.setText(splitData[7]);
                    mBinding.flowUnitFlowISATXT.setText(mBinding.flowUnitFlowISATXT.getAdapter().getItem(Integer.parseInt(splitData[8])).toString());
                    // Analog Flow Meter
                    if (splitData[5].equals("0")) {
                        mBinding.totalizerAlarmFlowISEdt.setText(splitData[13]);
                        mBinding.resetFlowTotalFlowISEdt.setText(splitData[14]);
                        mBinding.scheduleResetFlowISEdt.setText(mBinding.scheduleResetFlowISEdt.getAdapter().getItem(Integer.parseInt(splitData[15])).toString());
                        mBinding.alarmLowAnalogFlowISEdt.setText(splitData[17]);
                        mBinding.alarmHighAnalogFlowISEdt.setText(splitData[18]);


                        mBinding.rateUnitAnalogFlowISEdt.setText(splitData[9]);
                        mBinding.flowMaxAnalogFlowISEdt.setText(splitData[10]);
                        mBinding.flowMinAnalogFlowISEdt.setText(splitData[11]);
                        mBinding.smoothingFactorAnalogFlowISEdt.setText(splitData[12]);
                        mBinding.setFlowTotalAnalogFlowISEdt.setText(splitData[16]);
                        mBinding.calibrationRequiredFlowISEdt.setText(splitData[19]);
                        mBinding.resetCalibrationAnalogFlowISEdt.setText(mBinding.resetCalibrationAnalogFlowISEdt.getAdapter().getItem(Integer.parseInt(splitData[20])).toString());
                        // Flow meter Contactor type
                    } else if (splitData[5].equals("1")) {
                        mBinding.volumeFlowISEdt.setText(splitData[9]);
                        mBinding.totalizerAlarmFlowISEdt.setText(splitData[10]);
                        mBinding.resetFlowTotalFlowISEdt.setText(splitData[11]);
                        mBinding.setFlowTotalContactorFlowISEdt.setText(splitData[12]);
                        mBinding.scheduleResetFlowISEdt.setText(mBinding.scheduleResetFlowISEdt.getAdapter().getItem(Integer.parseInt(splitData[13])).toString());
                        mBinding.alarmLowContactorFlowISEdt.setText(splitData[14]);
                        mBinding.alarmHighContactorFlowISEdt.setText(splitData[15]);
                        // Paddle wheel flow meter type
                    } else if (splitData[5].equals("2")) {
                        mBinding.rateUnitPaddleFlowISEdt.setText(splitData[9]);
                        mBinding.kFactorFlowISEdt.setText(splitData[10]);
                        mBinding.totalizerAlarmFlowISEdt.setText(splitData[11]);
                        mBinding.resetFlowTotalFlowISEdt.setText(splitData[12]);
                        mBinding.setFlowTotalPaddleFlowISEdt.setText(splitData[13]);
                        mBinding.scheduleResetFlowISEdt.setText(mBinding.scheduleResetFlowISEdt.getAdapter().getItem(Integer.parseInt(splitData[14])).toString());
                        mBinding.alarmLowPaddleFlowISEdt.setText(splitData[15]);
                        mBinding.alarmHighPaddleFlowISEdt.setText(splitData[16]);
                        // Feed Monitor type
                    } else if (splitData[5].equals("3")) {
                        mBinding.rateUnitFeedInputSettingsEdt.setText(splitData[9]);
                        mBinding.totalAlarmModeFeedFlowISEdt.setText(splitData[10]);
                        mBinding.flowAlarmModeFeedFlowISEdt.setText(splitData[11]);
                        mBinding.flowAlarmDelayFeedISEdt.setText(splitData[12]);
                        mBinding.flowAlarmClearFeedFlowISEdt.setText(splitData[13]);
                        mBinding.outputRelayLinkFeedFlowISEdt.setText(splitData[14]);
                        mBinding.totalizerAlarmFlowISEdt.setText(splitData[15]);
                        mBinding.resetFlowTotalFlowISEdt.setText(splitData[16]);
                        mBinding.setFlowTotalFeedIsEDT.setText(splitData[17]);
                        mBinding.scheduleResetFlowISEdt.setText(mBinding.scheduleResetFlowISEdt.getAdapter().getItem(Integer.parseInt(splitData[18])).toString());
                        mBinding.alarmLowFeedFlowISEdt.setText(splitData[19]);
                        mBinding.alarmHighFeedFlowISEdt.setText(splitData[20]);
                    }
                    initAdapter();
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Response Failed");
                }
            } else if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), "Write Success");
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Write Failed");
                }
            }
        } else {
            Log.e(TAG, "handleResponse: Received Wrong Packet");
        }
    }
}