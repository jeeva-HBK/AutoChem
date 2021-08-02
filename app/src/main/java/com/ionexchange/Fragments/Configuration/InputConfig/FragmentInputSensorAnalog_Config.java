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
import com.ionexchange.databinding.FragmentInputsensorAnalogBinding;

import static com.ionexchange.Others.ApplicationClass.analogTypeArr;
import static com.ionexchange.Others.ApplicationClass.analogUnitArr;
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

public class FragmentInputSensorAnalog_Config extends Fragment implements DataReceiveCallback {

    FragmentInputsensorAnalogBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_inputsensor_analog, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        initAdapter();
        mBinding.saveLayoutInputSettings.setOnClickListener(this::save);
        mBinding.saveFabInputSettings.setOnClickListener(this::save);

        mBinding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
            }
        });
    }

    private void save(View view) {
        if (validField()) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                    INPUT_SENSOR_CONFIG + SPILT_CHAR +
                    toString(2, mBinding.analogInputNumberTie) + SPILT_CHAR +
                    getPosition(2, toString(mBinding.analogSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.analogTypeTie), analogTypeArr) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.analogSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                    toString(0, mBinding.analogInputLabelTie) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.analogUnitMeasurementTie), analogUnitArr) + SPILT_CHAR +
                    toString(4, mBinding.analogMinValueTie) + SPILT_CHAR +
                    toString(4, mBinding.analogMaxValueTie) + SPILT_CHAR +
                    toString(3, mBinding.analogSmoothingFactorTie) + SPILT_CHAR +
                    toString(6, mBinding.analogAlarmLowTie) + SPILT_CHAR +
                    toString(6, mBinding.analogHighLowTie) + SPILT_CHAR +
                    toString(3, mBinding.analogCalibrationRequiredAlarmTie) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.analogResetCalibrationTie), resetCalibrationArr));
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
        mBinding.analogSensorTypeTie.setAdapter(getAdapter(inputTypeArr));
        mBinding.analogSensorActivationTie.setAdapter(getAdapter(sensorActivationArr));
        mBinding.analogTypeTie.setAdapter(getAdapter(analogTypeArr));
        mBinding.analogUnitMeasurementTie.setAdapter(getAdapter(analogUnitArr));
        mBinding.analogResetCalibrationTie.setAdapter(getAdapter(resetCalibrationArr));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "19");
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

                    mBinding.analogInputNumberTie.setText(data[3]);

                    mBinding.analogSensorTypeTie.setText(mBinding.analogSensorTypeTie.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    mBinding.analogTypeTie.setText(mBinding.analogTypeTie.getAdapter().getItem(Integer.parseInt(data[5])).toString());

                    mBinding.analogSensorActivationTie.setText(mBinding.analogSensorActivationTie.getAdapter().getItem(Integer.parseInt(data[6])).toString());

                    mBinding.analogInputLabelTie.setText(data[7]);
                    mBinding.analogUnitMeasurementTie.setText(mBinding.analogUnitMeasurementTie.getAdapter().getItem(Integer.parseInt(data[8])).toString());
                    mBinding.analogMinValueTie.setText(data[9]);
                    mBinding.analogMaxValueTie.setText(data[10]);
                    mBinding.analogSmoothingFactorTie.setText(data[11]);
                    mBinding.analogAlarmLowTie.setText(data[12]);
                    mBinding.analogHighLowTie.setText(data[13]);
                    mBinding.analogCalibrationRequiredAlarmTie.setText(data[14]);
                    mBinding.analogResetCalibrationTie.setText(mBinding.analogResetCalibrationTie.getAdapter().getItem(Integer.parseInt(data[15])).toString());
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


    private boolean validField() {
        if (isEmpty(mBinding.analogInputNumberTie)) {
            mAppClass.showSnackBar(getContext(), "InputNumber cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogInputLabelTie)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogMinValueTie)) {
            mAppClass.showSnackBar(getContext(), "Min Value cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogMaxValueTie)) {
            mAppClass.showSnackBar(getContext(), "Max Value cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogAlarmLowTie)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogHighLowTie)) {
            mAppClass.showSnackBar(getContext(), "Alarm High cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogSmoothingFactorTie)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogCalibrationRequiredAlarmTie)) {
            mAppClass.showSnackBar(getContext(), "Calibration cannot be Empty");
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
