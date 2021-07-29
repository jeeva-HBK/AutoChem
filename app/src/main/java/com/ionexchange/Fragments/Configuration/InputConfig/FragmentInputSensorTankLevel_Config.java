package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
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
import com.ionexchange.databinding.FragmentInputSensorTankLevelBinding;

import static com.ionexchange.Others.ApplicationClass.digital_Arr;
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

public class FragmentInputSensorTankLevel_Config extends Fragment implements DataReceiveCallback {

    FragmentInputSensorTankLevelBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_input_sensor_tank_level, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        initAdapter();
        mBinding.saveLayoutInputSettings.setOnClickListener(this::save);
        mBinding.saveFabInputSettings.setOnClickListener(this::save);

    }

    private void save(View view) {
        if (validField()) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                    INPUT_SENSOR_CONFIG + SPILT_CHAR +
                    toString(2, mBinding.tankLevelInputNumberTie) + SPILT_CHAR +
                    getPosition(2, toString(mBinding.tankLevelInputSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.tankLevelInputSensorSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                    toString(0, mBinding.tankLevelInputSensorLabelTie) + SPILT_CHAR +
                    toString(3, mBinding.tankLevelInputSensorOpenMessageTie) + SPILT_CHAR +
                    toString(6, mBinding.tankLevelInputSensorCloseMessageTie) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.tankLevelInputSensorInnerLockAct), digital_Arr) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.tankLevelInputSensorAlarmAct), digital_Arr) + SPILT_CHAR +
                    toString(6, mBinding.tankLevelInputSensorTotalTimeTie) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.tankLevelInputSensorResetTimeAct), resetCalibrationArr));
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

    private void initAdapter() {
        mBinding.tankLevelInputSensorTypeTie.setAdapter(getAdapter(inputTypeArr));
        mBinding.tankLevelInputSensorSensorActivationTie.setAdapter(getAdapter(sensorActivationArr));
        mBinding.tankLevelInputSensorInnerLockAct.setAdapter(getAdapter(digital_Arr));
        mBinding.tankLevelInputSensorAlarmAct.setAdapter(getAdapter(digital_Arr));
        mBinding.tankLevelInputSensorResetTimeAct.setAdapter(getAdapter(resetCalibrationArr));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
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


    private void handleResponse(String[] data) {

        if (data[1].equals(INPUT_SENSOR_CONFIG)) {
            if (data[0].equals(READ_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {

                    mBinding.tankLevelInputNumberTie.setText(data[3]);

                    mBinding.tankLevelInputSensorTypeTie.setText(mBinding.tankLevelInputSensorTypeTie.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    mBinding.tankLevelInputSensorSensorActivationTie.setText(mBinding.tankLevelInputSensorSensorActivationTie.getAdapter().getItem(Integer.parseInt(data[5])).toString());

                    mBinding.tankLevelInputSensorLabelTie.setText(data[6]);
                    mBinding.tankLevelInputSensorOpenMessageTie.setText(data[7]);
                    mBinding.tankLevelInputSensorCloseMessageTie.setText(data[8]);
                    mBinding.tankLevelInputSensorInnerLockAct.setText(mBinding.tankLevelInputSensorInnerLockAct.getAdapter().getItem(Integer.parseInt(data[9])).toString());
                    mBinding.tankLevelInputSensorAlarmAct.setText(mBinding.tankLevelInputSensorAlarmAct.getAdapter().getItem(Integer.parseInt(data[10])).toString());
                    mBinding.tankLevelInputSensorTotalTimeTie.setText(data[11]);
                    mBinding.tankLevelInputSensorResetTimeAct.setText(mBinding.tankLevelInputSensorResetTimeAct.getAdapter().getItem(Integer.parseInt(data[12])).toString());
                    initAdapter();
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
            } else if (data[0].equals(WRITE_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), "Received Wrong Pack !");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "17");
    }


    private boolean validField() {
        if (isEmpty(mBinding.tankLevelInputNumberTie)) {
            mAppClass.showSnackBar(getContext(), "InputNumber cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tankLevelInputSensorLabelTie)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tankLevelInputSensorOpenMessageTie)) {
            mAppClass.showSnackBar(getContext(), "Open message cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tankLevelInputSensorCloseMessageTie)) {
            mAppClass.showSnackBar(getContext(), "Close message cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tankLevelInputSensorTotalTimeTie)) {
            mAppClass.showSnackBar(getContext(), "Total time cannot be Empty");
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
}
