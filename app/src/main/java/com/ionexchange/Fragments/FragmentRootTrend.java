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
import com.ionexchange.databinding.FragmentTrendBinding;

public class FragmentRootTrend extends Fragment implements DataReceiveCallback {
    FragmentTrendBinding mBinding;
    BaseActivity mActivity;
    ApplicationClass mAppclass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trend, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (BaseActivity) getActivity();
        mAppclass = (ApplicationClass) getActivity().getApplication();
        mActivity.changeToolBarVisibility(View.VISIBLE);
        mAppclass.sendPacket(this, "1234#0#01#192.168.1.100#255.255.255.0#192.168.1.1#8.8.8.8#4.4.4.4&05000");
    }

    @Override
    public void OnDataReceive(String data) {
        Log.e("TAG", "OnDataReceive: " + data);
    }
}
