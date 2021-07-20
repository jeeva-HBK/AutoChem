package com.ionexchange.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        mBinding.saveLayoutUnitIp.setOnClickListener(view1 -> {
            mAppclass.sendPacket(this, formData());
        });
    }

    private String formData() {
        /* String s = */
        return "";
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleData(data);
        }
    }

    private void handleData(String data) {

    }

    @Override
    public void onResume() {
        super.onResume();
        readData();
        // DEVICE_PASSWORD + SpiltChar + READ_PACKET + SpiltChar + PCK_panelIpConfig
    }

    private void readData() {
        mAppclass.sendPacket(this, "1234#0#01#192.168.1.100#255.255.255.0#192.168.1.1#8.8.8.8#4.4.4.4&05000");
    }
}
