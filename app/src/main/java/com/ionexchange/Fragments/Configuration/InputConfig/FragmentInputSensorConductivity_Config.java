package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsensorCondBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.tempLinkedArr;
import static com.ionexchange.Others.ApplicationClass.unitArr;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.INPUT_SENSOR_CONFIG;
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
        initAdapters();

        mBinding.saveFabCondIS.setOnClickListener(this::save);
        mBinding.saveLayoutCondIS.setOnClickListener(this::save);
        mBinding.DeleteFabCondIS.setOnClickListener(this::delete);
        mBinding.DeleteLayoutCondIS.setOnClickListener(this::delete);

        mBinding.backArrow.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
    }

    private void delete(View view) {
        sendData(2);
    }

    private void save(View view) {
        if (validation()) {
            sendData(sensorStatus);
        }

    }

    void sendData(int sensorStatus) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "04" + SPILT_CHAR +
                toString(2, mBinding.inputNumberCondISEDT) + SPILT_CHAR +
                getPosition(2, toString(mBinding.sensorActivationCondISATXT), sensorActivationArr) + SPILT_CHAR +
                toString(6, mBinding.inputLabelCondISEdt) + SPILT_CHAR +
                getPosition(1, toString(mBinding.tempLinkedCondISEdt), tempLinkedArr) + SPILT_CHAR +
                toString(2, mBinding.tempValueCondISEdt) + SPILT_CHAR +
                getPosition(1, toString(mBinding.unitOfMeasureCondISEdt), unitArr) + SPILT_CHAR +
                toString(4, mBinding.tempCompCondISEdt) + SPILT_CHAR +
                toString(4, mBinding.tempCompFacCondISEdt) + SPILT_CHAR +
                toString(3, mBinding.smoothingFactorCondISEdt) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.alarmLowCondISEdt) +
                SPILT_CHAR + toStringSplit(4, 2, mBinding.alarmHighCondISEdt) + SPILT_CHAR +
                toString(3, mBinding.calibRequiredAlarmCondISEdt) + SPILT_CHAR +
                getPosition(1, toString(mBinding.resetCalibCondISEdt), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus
        );
    }

    private String toStringSplit(int digits, int digitPoint, EditText editText) {
        if (editText.getText().toString().split("\\.").length == 1) {
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
    }

    boolean validation() {
        if (isEmpty(mBinding.inputLabelCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Input Label Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempValueCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Temperature Value Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.cellConstantCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Cell Constant Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempCompCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Temperature Compensation Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempCompFacCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Temperature Compensation Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.smoothingFactorCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.calibRequiredAlarmCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Calibration Required Alarm Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmLowCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm Low Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmHighCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm High Factor Cannot be Empty");
            return false;
        } else if (mBinding.alarmLowCondISEdt.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm low is decimal format");
            return false;
        } else if (mBinding.alarmHighCondISEdt.getText().toString().matches(".")) {
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

    private void initAdapters() {
        mBinding.sensorTypeCondISATXT.setAdapter(getAdapter(inputTypeArr));
        mBinding.sensorActivationCondISATXT.setAdapter(getAdapter(sensorActivationArr));
        mBinding.tempLinkedCondISEdt.setAdapter(getAdapter(tempLinkedArr));
        mBinding.unitOfMeasureCondISEdt.setAdapter(getAdapter(unitArr));
        mBinding.resetCalibCondISEdt.setAdapter(getAdapter(resetCalibrationArr));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "03");
        } else {
            mBinding.inputNumberCondISEDT.setText(inputNumber);
            mBinding.sensorTypeCondISATXT.setText(sensorName);
            mBinding.DeleteLayoutCondIS.setVisibility(View.INVISIBLE);
            mBinding.saveTxt.setText("ADD");
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
            handleResponse(data.split("\\*")[1].split("#"));
        }
    }

    private void handleResponse(String[] spiltData) {
        // read Res - {*1# 04# 0# | 03# 04# 0# CONCON# 1# 33# 1# 2345# 2371# 1500# 100# 120000# 220000# 300# 1*}
        // write Res -
        if (spiltData[1].equals(INPUT_SENSOR_CONFIG)) {

            if (spiltData[0].equals(READ_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mBinding.inputNumberCondISEDT.setText(spiltData[3]);
                    mBinding.sensorTypeCondISATXT.setText(mBinding.sensorTypeCondISATXT.getAdapter().getItem(Integer.parseInt(spiltData[4])).toString());
                    mBinding.sensorActivationCondISATXT.setText(mBinding.sensorActivationCondISATXT.getAdapter().getItem(Integer.parseInt(spiltData[5])).toString());
                    mBinding.inputLabelCondISEdt.setText(spiltData[6]);
                    mBinding.tempLinkedCondISEdt.setText(mBinding.tempLinkedCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[7])).toString());
                    mBinding.tempValueCondISEdt.setText(spiltData[8]);
                    mBinding.unitOfMeasureCondISEdt.setText(mBinding.unitOfMeasureCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[9])).toString());
                    mBinding.cellConstantCondISEdt.setText(spiltData[10]);
                    mBinding.tempCompCondISEdt.setText(spiltData[11]);
                    mBinding.tempCompFacCondISEdt.setText(spiltData[12]);
                    mBinding.smoothingFactorCondISEdt.setText(spiltData[13]);
                    mBinding.alarmLowCondISEdt.setText(spiltData[14].substring(0, 4) + "." + spiltData[14].substring(4, 6));
                    mBinding.alarmHighCondISEdt.setText(spiltData[15].substring(0, 4) + "." + spiltData[15].substring(4, 6));
                    mBinding.calibRequiredAlarmCondISEdt.setText(spiltData[16]);
                    mBinding.resetCalibCondISEdt.setText(mBinding.resetCalibCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[17])).toString());

                    initAdapters();

                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
            } else if (spiltData[0].equals(WRITE_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }

        } else {
            Log.e(TAG, "handleResponse: Wrong Packet");
        }

    }
}
