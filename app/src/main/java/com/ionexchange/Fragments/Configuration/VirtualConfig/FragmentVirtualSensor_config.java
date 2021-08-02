package com.ionexchange.Fragments.Configuration.VirtualConfig;

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

import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentVirtualsensorConfigBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.ApplicationClass.calculationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.sensorsViArr;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.VIRTUAL_INPUT;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentVirtualSensor_config extends Fragment implements DataReceiveCallback {
    FragmentVirtualsensorConfigBinding mBinding;
    ApplicationClass mAppClass;
    String sensorInputNo;
    private static final String TAG = "FragmentVirtualSensor_c";

    public FragmentVirtualSensor_config(String sensorInputNo) {
        this.sensorInputNo = sensorInputNo;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_virtualsensor_config, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + VIRTUAL_INPUT + SPILT_CHAR + "01");
        initAdapters();
        mBinding.saveFabInputSettings.setOnClickListener(this::save);
        mBinding.saveLayoutInputSettings.setOnClickListener(this::save);
        mBinding.DeleteFabInputSettings.setOnClickListener(this::delete);
    }

    private void delete(View view) {

    }

    private void save(View view) {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + VIRTUAL_INPUT + SPILT_CHAR + "01" + SPILT_CHAR +
                getPosition(0, toString(mBinding.sensorActivationViEDT), sensorActivationArr) + SPILT_CHAR + toString(0, mBinding.labelViEDT) + SPILT_CHAR +
                getPosition(2, toString(mBinding.sensor1ViATXT), sensorsViArr) + SPILT_CHAR + toString(4, mBinding.sensor1ConstantViEDT) + SPILT_CHAR +
                getPosition(2, toString(mBinding.sensor2ViATXT), sensorsViArr) + SPILT_CHAR + toString(4, mBinding.sensor2ConstantViEDT) + SPILT_CHAR +
                toString(4, mBinding.lowRangeViEDT) + SPILT_CHAR + toString(4, mBinding.highRangeViEDT) + SPILT_CHAR +
                toString(3, mBinding.smoothingFactorViEDT) + SPILT_CHAR +
                toString(6, mBinding.lowAlarmViEDT) + SPILT_CHAR + toString(6, mBinding.highAlarmViEDT) + SPILT_CHAR +
                getPosition(0, toString(mBinding.calculationViEDT), calculationArr)
        );
    }

    private void initAdapters() {
        mBinding.sensorActivationViEDT.setAdapter(getAdapter(sensorActivationArr));
        mBinding.calculationViEDT.setAdapter(getAdapter(calculationArr));
        mBinding.sensor1ViATXT.setAdapter(getAdapter(sensorsViArr));
        mBinding.sensor2ViATXT.setAdapter(getAdapter(sensorsViArr));
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

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleResponse(data.split("\\*")[1].split("#"));
        }
    }

    private void handleResponse(String[] spiltData) {
        // READ Res - {*1# 05# 0# | 01# 0# VirtualInput3# 01# 2345# 02# 3214# 1100# 2200# 100# 120000# 240000# 1*}
        // WRITE Res -
        if (spiltData[1].equals(VIRTUAL_INPUT)) {
            if (spiltData[0].equals(READ_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mBinding.sensorActivationViEDT.setText(mBinding.sensorActivationViEDT.getAdapter().getItem(Integer.parseInt(spiltData[4])).toString());
                    mBinding.labelViEDT.setText(spiltData[5]);
                    mBinding.sensor1ViATXT.setText(mBinding.sensor1ViATXT.getAdapter().getItem(Integer.parseInt(spiltData[6])).toString());
                    mBinding.sensor1ConstantViEDT.setText(spiltData[7]);
                    mBinding.sensor2ViATXT.setText(mBinding.sensor2ViATXT.getAdapter().getItem(Integer.parseInt(spiltData[8])).toString());
                    mBinding.sensor2ConstantViEDT.setText(spiltData[9]);
                    mBinding.lowRangeViEDT.setText(spiltData[10]);
                    mBinding.highRangeViEDT.setText(spiltData[11]);
                    mBinding.smoothingFactorViEDT.setText(spiltData[12]);
                    mBinding.lowAlarmViEDT.setText(spiltData[13]);
                    mBinding.highAlarmViEDT.setText(spiltData[14]);
                    mBinding.calculationViEDT.setText(mBinding.calculationViEDT.getAdapter().getItem(Integer.parseInt(spiltData[15])).toString());

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
            Log.e(TAG, "handleResponse: Received Wrong Packet");
        }
    }
}
