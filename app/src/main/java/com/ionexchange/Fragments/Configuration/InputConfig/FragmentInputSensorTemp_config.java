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
import com.ionexchange.databinding.FragmentInputsensorTempBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorTemp_config extends Fragment implements DataReceiveCallback {
    private static final String TAG = "FragmentInputSensorTemp";
    FragmentInputsensorTempBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_temp, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity)getActivity();
        initAdapter();
        mBinding.saveFabCondIS.setOnClickListener(this::save);
        mBinding.saveLayoutTempIS.setOnClickListener(this::save);
        // FIXME: 30-07-2021 virtual Input Configuration
        mBinding.DeleteFabCondIS.setOnClickListener(this::delete);
        mBinding.DeleteLayoutTempIS.setOnClickListener(this::delete);
        mBinding.backArrow.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
    }

    private void delete(View view) {

    }

    // getPosition(toString(mBinding.sensorTypeTempISATXT), inputTypeArr)
    private void save(View view) {
        if (validation()) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + toString(2, mBinding.inputNumberTempISEDT) + SPILT_CHAR +
                    getPosition(2, toString(mBinding.sensorTypeTempISATXT), inputTypeArr) + SPILT_CHAR + getPosition(1, toString(mBinding.sensorActivationTempISATXT), sensorActivationArr) + SPILT_CHAR +
                    toString(0, mBinding.inputLabelTempISEdt) + SPILT_CHAR + toString(2, mBinding.tempValueTempISEdt) + SPILT_CHAR + toString(3, mBinding.smoothingFactorTempISEdt) + SPILT_CHAR + toStringSplit(4,2, mBinding.alarmLowTempISEdt) + SPILT_CHAR +
                    toStringSplit(4,2, mBinding.alarmHighTempISEdt) + SPILT_CHAR + toString(3, mBinding.calibRequiredAlarmTempISEdt) + SPILT_CHAR + getPosition(1, toString(mBinding.resetCalibTempISEdt), resetCalibrationArr));
        }
    }

    private String toStringSplit(int digits, int digitPoint, EditText editText) {
        if(editText.getText().toString().split("\\.").length==1){
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
    }


    private void initAdapter() {
        mBinding.sensorTypeTempISATXT.setAdapter(getAdapter(inputTypeArr));
        mBinding.sensorActivationTempISATXT.setAdapter(getAdapter(sensorActivationArr));
        mBinding.resetCalibTempISEdt.setAdapter(getAdapter(resetCalibrationArr));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
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

    @Override
    public void onResume() {
        super.onResume();
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "05");
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

    private void handleResponse(String[] splitData) {
        // Read RES - {*1# 04# 0# |05# 02# 1# TEMPPH# 33# 100# 120000# 240000# 320# 1*}
        // Write Res -
        if (splitData[1].equals(INPUT_SENSOR_CONFIG)) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.inputNumberTempISEDT.setText(splitData[3]);
                    mBinding.sensorTypeTempISATXT.setText(mBinding.sensorTypeTempISATXT.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                    mBinding.sensorActivationTempISATXT.setText(mBinding.sensorActivationTempISATXT.getAdapter().getItem(Integer.parseInt(splitData[5])).toString());
                    mBinding.inputLabelTempISEdt.setText(splitData[6]);
                    mBinding.tempValueTempISEdt.setText(splitData[7]);
                    mBinding.smoothingFactorTempISEdt.setText(splitData[8]);
                    mBinding.alarmLowTempISEdt.setText(splitData[9].substring(0,4)+"."+splitData[9].substring(4,6));
                    mBinding.alarmHighTempISEdt.setText(splitData[10].substring(0,4)+"."+splitData[10].substring(4,6));
                    mBinding.calibRequiredAlarmTempISEdt.setText(splitData[11]);
                    mBinding.resetCalibTempISEdt.setText(mBinding.resetCalibTempISEdt.getAdapter().getItem(Integer.parseInt(splitData[12])).toString());
                    initAdapter();
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), " READ FAILED");
                }
            } else if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), " WRITE SUCESS");
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), " WRITE FAILED");
                }
            }
        } else {
            Log.e(TAG, "handleResponse: WRONG_PACK");
        }
    }


    boolean validation() {
        if (isEmpty(mBinding.smoothingFactorTempISEdt)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.inputLabelTempISEdt)) {
            mAppClass.showSnackBar(getContext(), "Input Label Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.calibRequiredAlarmTempISEdt)) {
            mAppClass.showSnackBar(getContext(), "Calibration Alarm Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempValueTempISEdt)) {
            mAppClass.showSnackBar(getContext(), "Temperature Value Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmLowTempISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm Low Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmHighTempISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm High Factor Cannot be Empty");
            return false;
        }else if (mBinding.alarmLowTempISEdt.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm low is decimal format");
            return false;
        } else if (mBinding.alarmHighTempISEdt.getText().toString().matches(".")) {
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
}
