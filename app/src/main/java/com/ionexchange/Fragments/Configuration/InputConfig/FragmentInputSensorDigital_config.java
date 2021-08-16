
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
import com.ionexchange.databinding.FragmentInputsensorDigitalBinding;

import static com.ionexchange.Others.ApplicationClass.digitalArr;
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

public class FragmentInputSensorDigital_config extends Fragment implements DataReceiveCallback {

    FragmentInputsensorDigitalBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;

    public FragmentInputSensorDigital_config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;

    }

    public FragmentInputSensorDigital_config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_inputsensor_digital, container, false);
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
        mBinding.DeleteLayoutInputSettings.setOnClickListener(this::delete);
        mBinding.DeleteFabInputSettings.setOnClickListener(this::delete);

        mBinding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
            }
        });
    }

    private void delete(View view) {
        sendData(2);
    }

    private void save(View view) {
        if (validField()) {
            sendData(sensorStatus);
        }
    }

    void sendData(int sensorStatus) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.digitalInputNumberTie) + SPILT_CHAR +
                getPosition(2, toString(mBinding.digitalInputSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                getPosition(1, toString(mBinding.digitalInputSensorSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.digitalInputSensorLabelTie) + SPILT_CHAR +
                toString(3, mBinding.digitalInputSensorOpenMessageTie) + SPILT_CHAR +
                toString(6, mBinding.digitalInputSensorCloseMessageTie) + SPILT_CHAR +
                getPosition(1, toString(mBinding.digitalInputSensorInnerLockAct), digitalArr) + SPILT_CHAR +
                getPosition(1, toString(mBinding.digitalInputSensorAlarmAct), digitalArr) + SPILT_CHAR +
                toString(6, mBinding.digitalInputSensorTotalTimeTie) + SPILT_CHAR +
                getPosition(1, toString(mBinding.digitalInputSensorResetTimeAct), resetCalibrationArr) + SPILT_CHAR +
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

    private String toString(int digits, EditText editText) {
        return mAppClass.formDigits(digits, editText.getText().toString());
    }

    private String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    private void initAdapter() {
        mBinding.digitalInputSensorTypeTie.setAdapter(getAdapter(inputTypeArr));
        mBinding.digitalInputSensorSensorActivationTie.setAdapter(getAdapter(sensorActivationArr));
        mBinding.digitalInputSensorInnerLockAct.setAdapter(getAdapter(digitalArr));
        mBinding.digitalInputSensorAlarmAct.setAdapter(getAdapter(digitalArr));
        mBinding.digitalInputSensorResetTimeAct.setAdapter(getAdapter(resetCalibrationArr));
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

                    mBinding.digitalInputNumberTie.setText(data[3]);

                    mBinding.digitalInputSensorTypeTie.setText(mBinding.digitalInputSensorTypeTie.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    mBinding.digitalInputSensorSensorActivationTie.setText(mBinding.digitalInputSensorSensorActivationTie.getAdapter().getItem(Integer.parseInt(data[5])).toString());

                    mBinding.digitalInputSensorLabelTie.setText(data[6]);
                    mBinding.digitalInputSensorOpenMessageTie.setText(data[7]);
                    mBinding.digitalInputSensorCloseMessageTie.setText(data[8]);
                    mBinding.digitalInputSensorInnerLockAct.setText(mBinding.digitalInputSensorInnerLockAct.getAdapter().getItem(Integer.parseInt(data[9])).toString());
                    mBinding.digitalInputSensorAlarmAct.setText(mBinding.digitalInputSensorAlarmAct.getAdapter().getItem(Integer.parseInt(data[10])).toString());

                    mBinding.digitalInputSensorTotalTimeTie.setText(data[11]);
                    mBinding.digitalInputSensorResetTimeAct.setText(mBinding.digitalInputSensorResetTimeAct.getAdapter().getItem(Integer.parseInt(data[12])).toString());

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
        if (isEmpty(mBinding.digitalInputNumberTie)) {
            mAppClass.showSnackBar(getContext(), "InputNumber cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.digitalInputSensorLabelTie)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.digitalInputSensorOpenMessageTie)) {
            mAppClass.showSnackBar(getContext(), "Open message cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.digitalInputSensorCloseMessageTie)) {
            mAppClass.showSnackBar(getContext(), "Close message cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.digitalInputSensorTotalTimeTie)) {
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

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "16");
        } else {
            mBinding.digitalInputNumberTie.setText(inputNumber);
            mBinding.digitalInputSensorTypeTie.setText(sensorName);
            mBinding.DeleteLayoutInputSettings.setVisibility(View.INVISIBLE);
            mBinding.saveTxt.setText("ADD");
        }


    }
}


