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

import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsensorOrpBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

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

public class FragmentInputSensorORP_Config extends Fragment implements DataReceiveCallback {
    FragmentInputsensorOrpBinding mBinding;
    ApplicationClass mAppClass;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_orp, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        initAdapter();

        mBinding.orpsaveLayoutInputSettings.setOnClickListener(this::save);
        mBinding.orpsaveFabInputSettings.setOnClickListener(this::save);

        mBinding.orpDeleteFabInputSettings.setOnClickListener(this::delete);
        mBinding.orpDeleteFabInputSettings.setOnClickListener(this::delete);
    }

    private void delete(View view) {

    }

    private void save(View view) {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "02" + SPILT_CHAR + "ORP" +
                getPosition(toString(mBinding.orpSensorActISEDT), sensorActivationArr) + SPILT_CHAR + toString(mBinding.orpInputLabelISEDT) + SPILT_CHAR + toString(mBinding.orpSmoothingFactorISEDT) + SPILT_CHAR +
                toString(mBinding.orpalarmLowISEDT) + SPILT_CHAR + toString(mBinding.orpalarmHighISEDT) + SPILT_CHAR + toString(mBinding.orpCalibrationAlarmRequiredISEDT) + SPILT_CHAR +
                getPosition(toString(mBinding.orpResetCalibrationISEDT), resetCalibrationArr)
        );
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

    private void initAdapter() {
        mBinding.orpSensorActISEDT.setAdapter(getAdapter(sensorActivationArr));
        mBinding.orpSensorTypeEDT.setAdapter(getAdapter(inputTypeArr));
        mBinding.orpResetCalibrationISEDT.setAdapter(getAdapter(resetCalibrationArr));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "02");
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleResponse(data.split("\\*")[1].split("#"));
        }
    }

    private void handleResponse(String[] data) {
        // Read - Res - 1# 04# 0# | 02# 1# 1# ORP# 10# 500# 1000# 20# 0
        // Write - Res -
        if (data[1].equals(INPUT_SENSOR_CONFIG)) {
            if (data[0].equals(READ_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {

                    mBinding.orpInputNumberInputSettingsEDT.setText(data[3]);

                    mBinding.orpSensorTypeEDT.setText(mBinding.orpSensorTypeEDT.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    mBinding.orpSensorActISEDT.setText(mBinding.orpSensorActISEDT.getAdapter().getItem(Integer.parseInt(data[5])).toString());

                    mBinding.orpInputLabelISEDT.setText(data[6]);
                    mBinding.orpSmoothingFactorISEDT.setText(data[7]);
                    mBinding.orpalarmLowISEDT.setText(data[8]);
                    mBinding.orpalarmHighISEDT.setText(data[9]);
                    mBinding.orpCalibrationAlarmRequiredISEDT.setText(data[10]);

                    mBinding.orpResetCalibrationISEDT.setText(mBinding.orpResetCalibrationISEDT.getAdapter().getItem(Integer.parseInt(data[11])).toString());

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
}