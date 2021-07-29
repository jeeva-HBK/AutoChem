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
import com.ionexchange.databinding.FragmentInputsensorToraidalconductivityBinding;

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

public class FragmentInputSensorToroidalConductivity_config extends Fragment implements DataReceiveCallback {
    private static final String TAG = "FragmentInputSensorCond";
    FragmentInputsensorToraidalconductivityBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_toraidalconductivity, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        initAdapters();
        mBinding.saveFabCondIS.setOnClickListener(this::save);
        mBinding.saveLayoutTorCondIS.setOnClickListener(this::save);
        mBinding.DeleteFabCondIS.setOnClickListener(this::delete);
        mBinding.DeleteLayoutTorCondIS.setOnClickListener(this::delete);
        mBinding.backArrow.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
    }

    private void delete(View view) {

    }

    private void save(View view) {
        if (validation()) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                    INPUT_SENSOR_CONFIG + SPILT_CHAR +
                    toString(2, mBinding.inputNumberTorCondISEDT) + SPILT_CHAR +
                    getPosition(2, toString(mBinding.sensorTypeTorCondISATXT), inputTypeArr) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.sensorActivationTorCondISATXT), sensorActivationArr) + SPILT_CHAR +
                    toString(0, mBinding.inputLabelTorCondISEdt) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.tempLinkedTorCondISEdt), tempLinkedArr) + SPILT_CHAR +
                    toString(2, mBinding.tempValueTorCondISEdt) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.unitOfMeasureTorCondISEdt), unitArr) + SPILT_CHAR +
                    toString(4, mBinding.tempCompTorCondISEdt) + SPILT_CHAR +
                    toString(4, mBinding.tempCompFacTorCondISEdt) + SPILT_CHAR +
                    toString(3, mBinding.smoothingFactorTorConEDT) + SPILT_CHAR +
                    toString(6, mBinding.alarmLowTorCondISEdt) + SPILT_CHAR +
                    toString(6, mBinding.alarmHighTorCondISEdt) + SPILT_CHAR +
                    toString(3, mBinding.calibRequiredAlarmTorCondISEdt) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.resetCalibTorCondISEdt), resetCalibrationArr)
            );
        }
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
        mBinding.sensorTypeTorCondISATXT.setAdapter(getAdapter(inputTypeArr));
        mBinding.sensorActivationTorCondISATXT.setAdapter(getAdapter(sensorActivationArr));
        mBinding.tempLinkedTorCondISEdt.setAdapter(getAdapter(tempLinkedArr));
        mBinding.unitOfMeasureTorCondISEdt.setAdapter(getAdapter(unitArr));
        mBinding.resetCalibTorCondISEdt.setAdapter(getAdapter(resetCalibrationArr));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "04");
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
        mActivity.dismissProgress();
        // read Res - {*1# 04# 0# | 03# 04# 0# CONCON# 1# 33# 1# 2345# 2371# 1500# 100# 120000# 220000# 300# 1*}
        // write Res -
        if (spiltData[1].equals(INPUT_SENSOR_CONFIG)) {
            if (spiltData[0].equals(READ_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mBinding.inputNumberTorCondISEDT.setText(spiltData[3]);
                    mBinding.sensorTypeTorCondISATXT.setText(mBinding.sensorTypeTorCondISATXT.getAdapter().getItem(Integer.parseInt(spiltData[4])).toString());
                    mBinding.sensorActivationTorCondISATXT.setText(mBinding.sensorActivationTorCondISATXT.getAdapter().getItem(Integer.parseInt(spiltData[5])).toString());
                    mBinding.inputLabelTorCondISEdt.setText(spiltData[6]);
                    mBinding.tempLinkedTorCondISEdt.setText(mBinding.tempLinkedTorCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[7])).toString());
                    mBinding.tempValueTorCondISEdt.setText(spiltData[8]);
                    mBinding.unitOfMeasureTorCondISEdt.setText(mBinding.unitOfMeasureTorCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[9])).toString());
                    mBinding.tempCompTorCondISEdt.setText(spiltData[10]);
                    mBinding.tempCompFacTorCondISEdt.setText(spiltData[11]);
                    mBinding.smoothingFactorTorConEDT.setText(spiltData[12]);
                    mBinding.alarmLowTorCondISEdt.setText(spiltData[13]);
                    mBinding.alarmHighTorCondISEdt.setText(spiltData[14]);
                    mBinding.calibRequiredAlarmTorCondISEdt.setText(spiltData[15]);
                    mBinding.resetCalibTorCondISEdt.setText(mBinding.resetCalibTorCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[16])).toString());

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

    boolean validation() {
        if (isEmpty(mBinding.smoothingFactorTorConEDT)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.inputLabelTorCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Input Label Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.calibRequiredAlarmTorCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Calibration Alarm Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempCompFacTorCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Temperature Compensation Value Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempLinkedTorCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Temperature Linked  Value Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmLowTorCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm Low Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmHighTorCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm High Factor Cannot be Empty");
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
