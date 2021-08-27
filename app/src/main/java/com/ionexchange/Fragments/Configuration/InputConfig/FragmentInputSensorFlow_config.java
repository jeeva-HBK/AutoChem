
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

import static com.ionexchange.Others.ApplicationClass.flowMeterTypeArr;
import static com.ionexchange.Others.ApplicationClass.flowUnitArr;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.scheduleResetArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.userType;
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
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    int packetId;
    WaterTreatmentDb db;
    InputConfigurationDao dao;

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
        checkUser();

        initAdapter();
        mActivity = (BaseActivity) getActivity();
        mBinding.flowMeterTypeFlowISATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBinding.setFlowMeterType(String.valueOf(i));
            }
        });
        mBinding.flowSaveFab.setOnClickListener(this::save);
        mBinding.flowSaveLayout.setOnClickListener(this::save);
        mBinding.flowDeleteFab.setOnClickListener(this::delete);
        mBinding.flowDeleteLayout.setOnClickListener(this::delete);
        handleResponse(new String[]{});
    }

    private void checkUser() {
        switch (userType) {
            case 1: // Basic
                // root
                mBinding.flowInputNoRoot.setEnabled(false);
                mBinding.flowInputLabelRoot.setEnabled(false);
                mBinding.flowSensorTypeRoot.setEnabled(false);
                mBinding.flowFlowUnitRoot.setEnabled(false);
                mBinding.flowTotalizerAlarmRoot.setEnabled(false);
                mBinding.flowResetFlowTotalRoot.setEnabled(false);
                mBinding.flowScheduleResetRoot.setEnabled(false);
                mBinding.flowFlowMeterTypeRoot.setEnabled(false);

                /* flowMeter type */
                // analog
                mBinding.flowRateUnitAnalog.setEnabled(false);
                mBinding.flowFlowMeterMinAnalog.setEnabled(false);
                mBinding.flowFlowMeterMaxAnalog.setEnabled(false);
                mBinding.flowCalibrationRequiredAlarmAnalog.setEnabled(false);
                mBinding.flowAlarmLowAnalog.setEnabled(false);
                mBinding.flowAlarmHighAnalog.setEnabled(false);
                mBinding.flowSetFlowTotalAnalog.setEnabled(false);
                mBinding.flowResetCalibrationAnalog.setEnabled(false);
                mBinding.flowResetCalibrationAnalog.setEnabled(false);
                mBinding.flowSmoothingFactorAnalog.setVisibility(View.GONE);
                mBinding.flowSensorActivationRoot.setVisibility(View.GONE);

                // contactor
                mBinding.flowVolumeContactor.setEnabled(false);
                mBinding.flowSetFlowTotalContactor.setEnabled(false);
                mBinding.flowAlarmHighContactor.setEnabled(false);
                mBinding.flowAlarmLowContactor.setEnabled(false);

                // paddle wheel
                mBinding.flowRateUnitPaddle.setEnabled(false);
                mBinding.flowKFactorPaddle.setEnabled(false);
                mBinding.flowKFactorPaddle.setEnabled(false);
                mBinding.flowSetFlowTotalPaddle.setEnabled(false);
                mBinding.flowALarmLowPaddle.setEnabled(false);
                mBinding.flowAlarmHighPaddle.setEnabled(false);
                mBinding.flowKFactorPaddle.setEnabled(false);

                // feed monitor
                mBinding.flowRateunitFeedmonitor.setEnabled(false);
                mBinding.flowTotalAlarmModeFeedmonitor.setEnabled(false);
                mBinding.flowOutPutRealyFeedmonitor.setEnabled(false);
                mBinding.flowSetflowtotalFeedmonitor.setEnabled(false);
                mBinding.flowAlarmLowFeedmonitor.setEnabled(false);
                mBinding.flowAlarmhighFeedmonitor.setEnabled(false);
                mBinding.flowFlowAlarmModeFeedmonitor.setEnabled(false);
                mBinding.flowAlarmClearFeedmoniter.setEnabled(false);
                mBinding.flowAlarmDelayFeedmoniter.setEnabled(false);

                mBinding.flowRow8Isc.setVisibility(View.GONE);

                break;
            case 2: // Intermediate
                // root
                mBinding.flowInputNoRoot.setEnabled(false);
                mBinding.flowSensorTypeRoot.setEnabled(false);
                mBinding.flowSensorTypeRoot.setEnabled(false);
                mBinding.flowFlowMeterTypeRoot.setEnabled(false);
                mBinding.flowFlowUnitRoot.setEnabled(false);
                mBinding.flowResetFlowTotalRoot.setEnabled(false);
                mBinding.flowTotalizerAlarmRoot.setEnabled(false);
                mBinding.flowScheduleResetRoot.setEnabled(false);

                /* flowMeter type */
                // analog
                mBinding.flowRateUnitAnalog.setEnabled(false);
                mBinding.flowFlowMeterMinAnalog.setEnabled(false);
                mBinding.flowFlowMeterMaxAnalog.setEnabled(false);
                mBinding.flowSmoothingFactorAnalog.setEnabled(false);

                // contactor
                mBinding.flowVolumeContactor.setEnabled(false);

                //paddle wheel//
                mBinding.flowRateUnitPaddle.setEnabled(false);
                mBinding.flowKFactorPaddle.setEnabled(false);

                //feed monitor
                mBinding.flowFlowAlarmModeFeedmonitor.setEnabled(false);
                mBinding.flowTotalAlarmModeFeedmonitor.setEnabled(false);
                mBinding.flowAlarmDelayFeedmoniter.setEnabled(false);
                mBinding.flowAlarmClearFeedmoniter.setEnabled(false);

                mBinding.flowDeleteLayout.setVisibility(View.GONE);
                break;
        }

    }

    private void delete(View view) {
        switch (getPosition(toString(mBinding.flowMeterTypeFlowISATXT), flowMeterTypeArr)) {
            case 0:
                if (validation()) {
                    sendAnalogPacket(2);
                    packetId = 0;
                }
                break;
            case 1:
                if (validation1()) {
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
        switch (getPosition(toString(mBinding.flowMeterTypeFlowISATXT), flowMeterTypeArr)) {
            case 0:
                if (validation()) {
                    sendAnalogPacket(sensorStatus);
                    packetId = 0;
                }
                break;
            case 1:
                if (validation1()) {
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
        if (isEmpty(mBinding.inputNumberFlowISEDT)) {
            mAppClass.showSnackBar(getContext(), "InputNumber cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.inputLabelFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.rateUnitPaddleFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Rate Unit be Empty");
            return false;
        } else if (isEmpty(mBinding.totalAlarmModeFeedFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Total Alarm cannot be Empty");
            return false;
        } else if (mBinding.flowAlarmDelayFeedISEdt.getText().toString().length() > 6) {
            mAppClass.showSnackBar(getContext(), "Alarm Delay cannot be Empty ");
            return false;
        } else if (isEmpty(mBinding.flowAlarmClearFeedFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm Clear cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.flowAlarmModeFeedFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm Feed cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.outputRelayLinkFeedFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "OutputRelay cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.resetFlowTotalFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "ResetFlow cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.setFlowTotalFeedIsEDT)) {
            mAppClass.showSnackBar(getContext(), "SetFlow cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmLowFeedFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm high cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmHighFeedFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Calibartion Required cannot be Empty");
            return false;
        } else if (mBinding.alarmLowFeedFlowISEdt.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm low is decimal format");
            return false;
        } else if (mBinding.alarmHighFeedFlowISEdt.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm High is decimal format");
            return false;
        }
        return true;
    }

    private boolean validation2() {
        if (isEmpty(mBinding.inputNumberFlowISEDT)) {
            mAppClass.showSnackBar(getContext(), "InputNumber cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.inputLabelFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.rateUnitPaddleFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Rate Unit be Empty");
            return false;
        } else if (isEmpty(mBinding.kFactorFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "K Factor cannot be Empty");
            return false;
        } else if (mBinding.totalizerAlarmFlowISEdt.getText().toString().length() > 6) {
            mAppClass.showSnackBar(getContext(), "TotalizerAlarm cannot be Empty ");
            return false;
        } else if (isEmpty(mBinding.alarmLowPaddleFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmHighContactorFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm high cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmHighPaddleFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Calibartion Required cannot be Empty");
            return false;
        } else if (mBinding.alarmHighContactorFlowISEdt.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm low is decimal format");
            return false;
        } else if (mBinding.alarmHighPaddleFlowISEdt.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm High is decimal format");
            return false;
        }
        return true;
    }

    private boolean validation1() {
        if (isEmpty(mBinding.inputNumberFlowISEDT)) {
            mAppClass.showSnackBar(getContext(), "InputNumber cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.inputLabelFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.volumeFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Volume cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.totalizerAlarmFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Totalizer Alarm cannot be Empty");
            return false;
        } else if (mBinding.resetFlowTotalFlowISEdt.getText().toString().length() > 6) {
            mAppClass.showSnackBar(getContext(), "ResetFlowTotal cannot be Empty ");
            return false;
        } else if (isEmpty(mBinding.alarmLowContactorFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmHighContactorFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm high cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.calibrationRequiredFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Calibartion Required cannot be Empty");
            return false;
        } else if (mBinding.alarmLowContactorFlowISEdt.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm low is decimal format");
            return false;
        } else if (mBinding.alarmHighContactorFlowISEdt.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm High is decimal format");
            return false;
        }
        return true;

    }

    private boolean validation() {
        if (isEmpty(mBinding.inputNumberFlowISEDT)) {
            mAppClass.showSnackBar(getContext(), "InputNumber cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.inputLabelFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.rateUnitAnalogFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Rate unit cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.flowMaxAnalogFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Flow max cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.flowMinAnalogFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "flow min cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.smoothingFactorAnalogFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "smoothing Factor cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.totalizerAlarmFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Totalizer Alarm cannot be Empty");
            return false;
        } else if (mBinding.setFlowTotalAnalogFlowISEdt.getText().toString().length() > 6) {
            mAppClass.showSnackBar(getContext(), "SetFlowTotal cannot be Empty ");
            return false;
        } else if (isEmpty(mBinding.alarmLowAnalogFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmHighAnalogFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm high cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.calibrationRequiredFlowISEdt)) {
            mAppClass.showSnackBar(getContext(), "Calibartion Required cannot be Empty");
            return false;
        } else if (mBinding.alarmLowAnalogFlowISEdt.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm low is decimal format");
            return false;
        } else if (mBinding.alarmHighAnalogFlowISEdt.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm High is decimal format");
            return false;
        }
        return true;
    }

    private Boolean isEmpty(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        return false;
    }

    private Boolean isEmpty(AutoCompleteTextView editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        return false;
    }

    private void sendFeedMonitorPacket(int sensorStatus) {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.inputNumberFlowISEDT) + SPILT_CHAR +
                "03" + SPILT_CHAR +
                getPosition(toString(mBinding.flowMeterTypeFlowISATXT), flowMeterTypeArr) + SPILT_CHAR +
                getPosition(toString(mBinding.sensorActivationFlowISATXT), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.inputLabelFlowISEdt) + SPILT_CHAR +
                getPosition(1, toString(mBinding.flowUnitFlowISATXT), flowUnitArr) + SPILT_CHAR +
                toString(4, mBinding.rateUnitPaddleFlowISEdt) + SPILT_CHAR +
                toString(2, mBinding.totalAlarmModeFeedFlowISEdt) + SPILT_CHAR +
                toString(2, mBinding.flowAlarmModeFeedFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.flowAlarmDelayFeedISEdt) + SPILT_CHAR +
                toString(6, mBinding.flowAlarmClearFeedFlowISEdt) + SPILT_CHAR +
                toString(2, mBinding.outputRelayLinkFeedFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.totalizerAlarmFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.resetFlowTotalFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.setFlowTotalFeedIsEDT) + SPILT_CHAR +
                getPosition(toString(mBinding.scheduleResetFlowISEdt), scheduleResetArr) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.alarmLowFeedFlowISEdt) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.alarmHighFeedFlowISEdt) + SPILT_CHAR +
                sensorStatus);
    }

    private void sendPaddleWheelPacket(int sensorStatus) {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.inputNumberFlowISEDT) + SPILT_CHAR +
                "03" + SPILT_CHAR +
                getPosition(2, toString(mBinding.flowMeterTypeFlowISATXT), flowMeterTypeArr) + SPILT_CHAR +
                getPosition(1, toString(mBinding.sensorActivationFlowISATXT), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.inputLabelFlowISEdt) + SPILT_CHAR +
                getPosition(1, toString(mBinding.flowUnitFlowISATXT), flowUnitArr) + SPILT_CHAR +
                toString(4, mBinding.rateUnitPaddleFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.kFactorFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.totalizerAlarmFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.resetFlowTotalFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.setFlowTotalPaddleFlowISEdt) + SPILT_CHAR +
                getPosition(1, toString(mBinding.scheduleResetFlowISEdt), scheduleResetArr) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.alarmLowPaddleFlowISEdt) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.alarmHighPaddleFlowISEdt) + SPILT_CHAR +
                sensorStatus);
    }

    private void sendContactorPacket(int sensorStatus) {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.inputNumberFlowISEDT) + SPILT_CHAR +
                "03" + SPILT_CHAR +
                getPosition(2, toString(mBinding.flowMeterTypeFlowISATXT), flowMeterTypeArr) + SPILT_CHAR +
                getPosition(1, toString(mBinding.sensorActivationFlowISATXT), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.inputLabelFlowISEdt) + SPILT_CHAR +
                getPosition(1, toString(mBinding.flowUnitFlowISATXT), flowUnitArr) + SPILT_CHAR +
                toString(6, mBinding.volumeFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.totalizerAlarmFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.resetFlowTotalFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.setFlowTotalContactorFlowISEdt) + SPILT_CHAR +
                getPosition(1, toString(mBinding.scheduleResetFlowISEdt), scheduleResetArr) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.alarmLowContactorFlowISEdt) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.alarmHighContactorFlowISEdt) + SPILT_CHAR +
                sensorStatus);
    }

    private void sendAnalogPacket(int sensorStatus) {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.inputNumberFlowISEDT) + SPILT_CHAR +
                "03" + SPILT_CHAR +
                getPosition(2, toString(mBinding.flowMeterTypeFlowISATXT), flowMeterTypeArr) + SPILT_CHAR +
                getPosition(1, toString(mBinding.sensorActivationFlowISATXT), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.inputLabelFlowISEdt) + SPILT_CHAR +
                getPosition(1, toString(mBinding.flowUnitFlowISATXT), flowUnitArr) + SPILT_CHAR +
                toString(1, mBinding.rateUnitAnalogFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.flowMaxAnalogFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.flowMinAnalogFlowISEdt) + SPILT_CHAR +
                toString(3, mBinding.smoothingFactorAnalogFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.totalizerAlarmFlowISEdt) + SPILT_CHAR +
                toString(4, mBinding.resetFlowTotalFlowISEdt) + SPILT_CHAR +
                getPosition(1, toString(mBinding.scheduleResetFlowISEdt), scheduleResetArr) + SPILT_CHAR +
                toString(4, mBinding.setFlowTotalAnalogFlowISEdt) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.alarmLowAnalogFlowISEdt) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.alarmHighAnalogFlowISEdt) + SPILT_CHAR +
                toString(3, mBinding.calibrationRequiredFlowISEdt) + SPILT_CHAR +
                getPosition(1, toString(mBinding.resetCalibrationAnalogFlowISEdt), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus);
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

    private int getPosition(String string, String[] strArr) {
        int j = 0;
        for (int i = 0; i < strArr.length; i++) {
            if (string.equals(strArr[i])) {
                j = (i);
            }
        }
        return j;
    }

    private String toString(int digits, EditText editText) {
        return mAppClass.formDigits(digits, editText.getText().toString());
    }

    private String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    private String toStringSplit(int digits, int digitPoint, EditText editText) {
        if (editText.getText().toString().split("\\.").length == 1) {
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
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
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "07");
        } else {
            mBinding.inputNumberFlowISEDT.setText(inputNumber);
            mBinding.sensorTypeFlowISATXT.setText(sensorName);
            mBinding.flowDeleteLayout.setVisibility(View.INVISIBLE);
            mBinding.saveTxt.setText("ADD");
        }


    }

    @Override
    public void OnDataReceive(String data) {
        mActivity.dismissProgress();
        if (data != null) {
            handleResponse(data.split("\\*")[1].split("#"));
        }
    }

    private void handleResponse(String[] splitData) {
        /* READ_RES */
        //  - Analog->      {*1# 04# 0# | 07# 03# 0# 0# AnalogInput1# 1# | 1000# 1500# 3000# 100# 2000# 4000# 1# 1200# 120000# 240000# 333# 1*}
        //  - Contactor->   {*1# 04# 0# | 07# 03# 1# 0# DigitalInput2# 1#| 100000# 2000# 4000# 4000# 1# 120000# 240000*}
        //  - paddle wheel->{*1# 04# 0# | 07# 03# 2# 0# DigitalInput2# 1#| 1000# 80# 2000# 4000# 4000# 1# 120000# 240000*}
        //  - FeedMonitor ->{*1# 04# 0# | 07# 03# 3# 0# DigitalInput2# 1#| 1000# 01# 01# 2230# 240000# 00# 2000# 4000# 4000# 1# 120000# 240000*}

        splitData = "{*1#04#0#07#03#0#0#AnalogInput1#1#1000#1500#3000#100#2000#4000#1#1200#120000#240000#333#1*}".split("\\*")[1].split("#");
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
                        mBinding.alarmLowAnalogFlowISEdt.setText(splitData[17].substring(0, 4) + "." + splitData[17].substring(4, 6));
                        mBinding.alarmHighAnalogFlowISEdt.setText(splitData[18].substring(0, 4) + "." + splitData[18].substring(4, 6));

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
                        mBinding.alarmLowContactorFlowISEdt.setText(splitData[14].substring(0, 4) + "." + splitData[14].substring(4, 6));
                        mBinding.alarmHighContactorFlowISEdt.setText(splitData[15].substring(0, 4) + "." + splitData[15].substring(4, 6));

                        // Paddle wheel flow meter type
                    } else if (splitData[5].equals("2")) {
                        mBinding.rateUnitPaddleFlowISEdt.setText(splitData[9]);
                        mBinding.kFactorFlowISEdt.setText(splitData[10]);
                        mBinding.totalizerAlarmFlowISEdt.setText(splitData[11]);
                        mBinding.resetFlowTotalFlowISEdt.setText(splitData[12]);
                        mBinding.setFlowTotalPaddleFlowISEdt.setText(splitData[13]);
                        mBinding.scheduleResetFlowISEdt.setText(mBinding.scheduleResetFlowISEdt.getAdapter().getItem(Integer.parseInt(splitData[14])).toString());
                        mBinding.alarmLowPaddleFlowISEdt.setText(splitData[15].substring(0, 4) + "." + splitData[15].substring(4, 6));
                        mBinding.alarmHighPaddleFlowISEdt.setText(splitData[16].substring(0, 4) + "." + splitData[16].substring(4, 6));

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
                        mBinding.alarmLowFeedFlowISEdt.setText(splitData[19].substring(0, 4) + "." + splitData[19].substring(4, 6));
                        mBinding.alarmHighFeedFlowISEdt.setText(splitData[20].substring(0, 4) + "." + splitData[20].substring(4, 6));
                    }
                    initAdapter();
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Response Failed");
                }
            } else if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    flowMeterEntity(1, packetId);
                    mAppClass.showSnackBar(getContext(), "Write Success");
                } else if (splitData[2].equals(RES_FAILED)) {
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

    public void flowMeterEntity(int flagValue, int packetId) {
        switch (flagValue) {
            case 0:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.inputNumberFlowISEDT)),
                                "0", 0, "0", "0",
                                "0", flagValue);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                break;

            case 1:
                switch (packetId) {
                    case 0:
                        InputConfigurationEntity entityAnalogUpdate = new InputConfigurationEntity
                                (Integer.parseInt(toString(2, mBinding.inputNumberFlowISEDT)),
                                        mBinding.sensorTypeFlowISATXT.getText().toString(),
                                        0, toString(0, mBinding.inputLabelFlowISEdt),
                                        toStringSplit(4, 2, mBinding.alarmLowAnalogFlowISEdt),
                                        toStringSplit(4, 2, mBinding.alarmHighAnalogFlowISEdt),
                                        flagValue);
                        List<InputConfigurationEntity> entryListAnalogUpdate = new ArrayList<>();
                        entryListAnalogUpdate.add(entityAnalogUpdate);
                        updateToDb(entryListAnalogUpdate);
                        break;
                    case 1:
                        InputConfigurationEntity entityContactorUpdate = new InputConfigurationEntity
                                (Integer.parseInt(toString(2, mBinding.inputNumberFlowISEDT)),
                                        mBinding.sensorTypeFlowISATXT.getText().toString(),
                                        0, toString(0, mBinding.inputLabelFlowISEdt),
                                        toStringSplit(4, 2, mBinding.alarmLowContactorFlowISEdt),
                                        toStringSplit(4, 2, mBinding.alarmHighContactorFlowISEdt),
                                        flagValue);
                        List<InputConfigurationEntity> entryListContactorUpdate = new ArrayList<>();
                        entryListContactorUpdate.add(entityContactorUpdate);
                        updateToDb(entryListContactorUpdate);
                        break;
                    case 2:
                        InputConfigurationEntity entityPaddleWheelUpdate = new InputConfigurationEntity
                                (Integer.parseInt(toString(2, mBinding.inputNumberFlowISEDT)),
                                        mBinding.sensorTypeFlowISATXT.getText().toString(),
                                        0, toString(0, mBinding.inputLabelFlowISEdt),
                                        toStringSplit(4, 2, mBinding.alarmLowPaddleFlowISEdt),
                                        toStringSplit(4, 2, mBinding.alarmHighAnalogFlowISEdt),
                                        flagValue);
                        List<InputConfigurationEntity> entryPaddleWheelUpdate = new ArrayList<>();
                        entryPaddleWheelUpdate.add(entityPaddleWheelUpdate);
                        updateToDb(entryPaddleWheelUpdate);
                        break;
                    case 3:
                        InputConfigurationEntity entityFeedMonitorUpdate = new InputConfigurationEntity
                                (Integer.parseInt(toString(2, mBinding.inputNumberFlowISEDT)),
                                        mBinding.sensorTypeFlowISATXT.getText().toString(),
                                        0, toString(0, mBinding.inputLabelFlowISEdt),
                                        toStringSplit(4, 2, mBinding.alarmLowFeedFlowISEdt),
                                        toStringSplit(4, 2, mBinding.alarmHighFeedFlowISEdt),
                                        flagValue);
                        List<InputConfigurationEntity> entryFeedMonitorUpdate = new ArrayList<>();
                        entryFeedMonitorUpdate.add(entityFeedMonitorUpdate);
                        updateToDb(entryFeedMonitorUpdate);
                        break;
                }
                break;
        }

    }
}
