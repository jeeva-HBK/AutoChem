package com.ionexchange.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentUnitipsettingsBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_panelIpConfig;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentUnitIpSettings extends Fragment implements DataReceiveCallback {
    FragmentUnitipsettingsBinding mBinding;
    ApplicationClass mAppclass;
    BaseActivity mActivity;
    private static final String TAG = "FragmentUnitIpSettings";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_unitipsettings, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppclass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();

        mBinding.saveFab.setOnClickListener(this::writeData);
        mBinding.saveLayoutUnitIp.setOnClickListener(this::writeData);
    }

    private String formData() {
        return DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_panelIpConfig + SPILT_CHAR
                + toString(mBinding.ipUnitipEDT) + SPILT_CHAR + toString(mBinding.subNetUnitipEDT) + SPILT_CHAR + toString(mBinding.gatewayUnitipEDT) + SPILT_CHAR
                + toString(mBinding.DNS1UnitipEDT) + SPILT_CHAR + toString(mBinding.DNS2UnitipEDT) + SPILT_CHAR + toString(mBinding.portUnitipEDT);
    }

    private String toString(EditText editText) {
        return editText.getText().toString();
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleData(data.split("\\*")[1].split("#"));
        }
    }

    private void handleData(String[] splitData) {
        // Read -> Response   --  {*01#0#192.168.1.100#255.255.255.0#192.168.1.1#8.8.8.8#4.4.4.4#05000*}
        // Write -> Response  --  {*01#1*}
        if (splitData[0].equals("01")) {
            if (splitData[1].equals("0")) {
                mBinding.ipUnitipEDT.setText(splitData[2]);
                mBinding.subNetUnitipEDT.setText(splitData[3]);
                mBinding.gatewayUnitipEDT.setText(splitData[4]);
                mBinding.DNS1UnitipEDT.setText(splitData[5]);
                mBinding.DNS2UnitipEDT.setText(splitData[6]);
                mBinding.portUnitipEDT.setText(splitData[7]);
            } else {
                mAppclass.showSnackBar(getContext(), "Write Success");
            }
        } else {
            Log.e(TAG, "handleData: Received Wrong Packet !");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        readData();
    }

    private void readData() {
        mAppclass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_panelIpConfig);
    }

    private void writeData(View view) {
        mAppclass.sendPacket(this, formData());
    }
}
