package com.ionexchange.Fragments;

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

import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsensorchildBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.bufferArr;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.tempLinkedArr;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorChild extends Fragment implements DataReceiveCallback {
    FragmentInputsensorchildBinding mBinding;
    ApplicationClass mAppClass;

    private static final String TAG = "FragmentInputSensor";

    String inputNumber;

    public FragmentInputSensorChild(String inputNumber) {
        this.inputNumber = inputNumber;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensorchild, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAppClass = (ApplicationClass) getActivity().getApplication();
        initSensor(inputNumber);
        mBinding.saveLayoutInputSettings.setOnClickListener(this::save);
        mBinding.saveFabInputSettings.setOnClickListener(this::save);

        mBinding.DeleteLayoutInputSettings.setOnClickListener(this::delete);
        mBinding.DeleteFabInputSettings.setOnClickListener(this::delete);
    }

    private void delete(View view) {

    }

    private void save(View view) {
        if (validField()) {
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + toString(mBinding.inputNumberInputSettingsEDT) + SPILT_CHAR +
                    getPosition(toString(mBinding.sensorInputSettingsATXT), inputTypeArr) + SPILT_CHAR + getPosition(toString(mBinding.sensorActivationInputSettingsATXT), sensorActivationArr) + SPILT_CHAR +
                    toString(mBinding.inputLabelInputSettingsEdt) + SPILT_CHAR + getPosition(toString(mBinding.bufferTypeInputSettingATXT), bufferArr) + SPILT_CHAR +
                    getPosition(toString(mBinding.tempLinkedInputSettingATXT), tempLinkedArr) + SPILT_CHAR + toString(mBinding.temperatureInputSettingEDT) + SPILT_CHAR +
                    toString(mBinding.smoothingFactorInputSettingEDT) + SPILT_CHAR + toString(mBinding.alarmLowInputSettingEDT) + SPILT_CHAR +
                    toString(mBinding.alarmhighInputSettingEDT) + SPILT_CHAR + toString(mBinding.calibrationRequiredInputSettingATXT) + SPILT_CHAR +
                    getPosition(toString(mBinding.resetCalibrationInputSettingEDT), resetCalibrationArr));
        }
    }

    private boolean validField() {
        if (isEmpty(mBinding.inputNumberInputSettingsEDT)) {
            return false;
        } else if (isEmpty(mBinding.sensorInputSettingsATXT)) {
            return false;
        } else if (isEmpty(mBinding.sensorActivationInputSettingsATXT)) {
            return false;
        } else if (isEmpty(mBinding.tempLinkedInputSettingATXT)) {
            return false;
        } else if (isEmpty(mBinding.temperatureInputSettingEDT)) {
            return false;
        } else if (isEmpty(mBinding.smoothingFactorInputSettingEDT)) {
            return false;
        } else if (isEmpty(mBinding.alarmLowInputSettingEDT)) {
            return false;
        } else if (isEmpty(mBinding.alarmhighInputSettingEDT)) {
            return false;
        } else if (isEmpty(mBinding.calibrationRequiredInputSettingATXT)) {
            return false;
        } else return !isEmpty(mBinding.resetCalibrationInputSettingEDT);
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

    private int getPosition(String string, String[] strArr) {
        List list = Arrays.asList(strArr);
        return list.indexOf(string);
    }

    private String toString(EditText editText) {
        return editText.getText().toString();
    }

    private String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    private void initSensor(String inputNo) {
                mBinding.sensorActivationInputSettingsATXT.setAdapter(getAdapter(sensorActivationArr));
                mBinding.sensorInputSettingsATXT.setAdapter(getAdapter(inputTypeArr));
                mBinding.bufferTypeInputSettingATXT.setAdapter(getAdapter(bufferArr));
                mBinding.tempLinkedInputSettingATXT.setAdapter(getAdapter(tempLinkedArr));
                mBinding.resetCalibrationInputSettingEDT.setAdapter(getAdapter(resetCalibrationArr));
                mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "01");
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleResponce(data.split("\\*")[1].split("#"));
        }
    }

    private void handleResponce(String[] splitData) {
        // READ_RES - {* 1# 04# 0# 01# 0# 0# PHSensor# 0# 1# 33# 10# 400# 1300# 10# 0 *}

        if (splitData[1].equals("04")) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.inputNumberInputSettingsEDT.setText(splitData[3]);
                    // FIXME: 23-07-2021 AutoCompleteTextViewAdapter
                    mBinding.sensorInputSettingsATXT.setText(mBinding.sensorInputSettingsATXT.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                    mBinding.sensorInputSettingsATXT.setAdapter(getAdapter(inputTypeArr));

                    mBinding.sensorActivationInputSettingsATXT.setText(mBinding.sensorActivationInputSettingsATXT.getAdapter().getItem(Integer.parseInt(splitData[5])).toString());
                    mBinding.sensorActivationInputSettingsATXT.setAdapter(getAdapter(sensorActivationArr));

                    mBinding.inputLabelInputSettingsEdt.setText(splitData[6]);

                    mBinding.bufferTypeInputSettingATXT.setText(mBinding.bufferTypeInputSettingATXT.getAdapter().getItem(Integer.parseInt(splitData[7])).toString());
                    mBinding.bufferTypeInputSettingATXT.setAdapter(getAdapter(bufferArr));

                    mBinding.tempLinkedInputSettingATXT.setText(mBinding.tempLinkedInputSettingATXT.getAdapter().getItem(Integer.parseInt(splitData[8])).toString());
                    mBinding.tempLinkedInputSettingATXT.setAdapter(getAdapter(tempLinkedArr));

                    mBinding.temperatureInputSettingEDT.setText(splitData[9]);
                    mBinding.smoothingFactorInputSettingEDT.setText(splitData[10]);
                    mBinding.alarmLowInputSettingEDT.setText(splitData[11]);
                    mBinding.alarmhighInputSettingEDT.setText(splitData[12]);

                    mBinding.calibrationRequiredInputSettingATXT.setText(splitData[13]);
                    /*  if (splitData[13].equals("0")) {
                        mBinding.calibrationRequiredInputSettingATXT.setText(mBinding.calibrationRequiredInputSettingATXT.getAdapter().getItem(Integer.parseInt(splitData[13])).toString());
                    }
                    mBinding.calibrationRequiredInputSettingATXT.setAdapter(getAdapter(calibrationArr));*/

                    mBinding.resetCalibrationInputSettingEDT.setText(mBinding.resetCalibrationInputSettingEDT.getAdapter().getItem(Integer.parseInt(splitData[14])).toString());
                    mBinding.resetCalibrationInputSettingEDT.setAdapter(getAdapter(resetCalibrationArr));
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Read Failed !");
                }

            } else if (splitData[0].equals(WRITE_PACKET)) {

                if (splitData[2].equals(RES_SUCCESS)) {

                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Write Failed !");
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), "Received Wrong Packet");
        }
    }
}
