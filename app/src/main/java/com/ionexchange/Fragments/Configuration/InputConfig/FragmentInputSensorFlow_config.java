package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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
import static com.ionexchange.Others.ApplicationClass.scheduleReset;
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
        initAdapter();
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mBinding.flowMeterTypeFlowISATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBinding.setFlowMeterType(String.valueOf(i));
            }
        });
    }

    private void initAdapter() {
        mBinding.flowMeterTypeFlowISATXT.setAdapter(getAdapter(flowMeterTypeArr));
        mBinding.sensorTypeFlowISATXT.setAdapter(getAdapter(inputTypeArr));
        mBinding.sensorActivationFlowISATXT.setAdapter(getAdapter(sensorActivationArr));
        mBinding.flowUnitFlowISATXT.setAdapter(getAdapter(flowUnitArr));
        mBinding.scheduleResetFlowISEdt.setAdapter(getAdapter(scheduleReset));
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
        // {*1# 04# 0# | 07# 03# 0# 0# AnalogInput1# 1# 1000# 1500# 3000# 100# 2000# 4000# 1# 1200# 120000# 240000# 333# 1*}
        if (splitData[1].equals(INPUT_SENSOR_CONFIG)) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {


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
