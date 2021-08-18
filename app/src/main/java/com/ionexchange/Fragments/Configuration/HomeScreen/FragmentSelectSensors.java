package com.ionexchange.Fragments.Configuration.HomeScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.SelectSensorListAdapter;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.Others.DialogFrag;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSelectsensorBinding;

import org.jetbrains.annotations.NotNull;

public class FragmentSelectSensors extends Fragment implements CompoundButton.OnCheckedChangeListener, RvOnClick {
    FragmentSelectsensorBinding mBinding;

    RvOnClick rvOnClick;
    DialogFrag fragment;

    public FragmentSelectSensors(DialogFrag fragment) {
        this.fragment = fragment;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_selectsensor, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.inputRb.setOnCheckedChangeListener(this);
        mBinding.analogRb.setOnCheckedChangeListener(this);
        mBinding.digitalRb.setOnCheckedChangeListener(this);
        mBinding.flowMeterRb.setOnCheckedChangeListener(this);
        mBinding.modbusRb.setOnCheckedChangeListener(this);
        mBinding.generalRb.setOnCheckedChangeListener(this);
        mBinding.tankRb.setOnCheckedChangeListener(this);
        mBinding.virtualSensorRb.setOnCheckedChangeListener(this);
        mBinding.outputRb.setOnCheckedChangeListener(this);

        mBinding.selectSensorRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mBinding.inputRb.performClick();
    }

    private void setAdapter(boolean b, String[] mList) {
        if (b) {
            mBinding.selectSensorRv.setAdapter(new SelectSensorListAdapter(mList, rvOnClick = this));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.inputRb:
                setAdapter(b, new String[]{"pH", "ORP", "Conductivity 1", "Conductivity 2", "Temp 1", "TEMP 2", "Temp 3"});
                break;

            case R.id.analogRb:
                setAdapter(b, new String[]{"4-20 AI 1", "4-20 AI 2", "4-20 AI 3", "4-20 AI 4", "4-20 AI 5", "4-20 AI 6", "0 10v AI 7", "0 10v AI 8"});
                break;

            case R.id.digitalRb:
                setAdapter(b, new String[]{"DI 1", "DI 2", "DI 3", "DI 4", "DI 5", "DI 6", "DI 7", "DI 8"});
                break;

            case R.id.flowMeterRb:
                setAdapter(b, new String[]{"Flow meter 1", "Flow meter 2", "Flow meter 3", "Flow meter 4", "Flow meter 5", "Flow meter 6", "Flow meter 7", "Flow meter 8"});
                break;

            case R.id.modbusRb:
                setAdapter(b, new String[]{"ST-500", "CR300 CS", "ST-590", "ST-588", "ST-500 RO", "CR300 CU"});
                break;

            case R.id.generalRb:
                setAdapter(b, new String[]{"General 1", "General 2", "General 3", "General 4", "General 5", "General 6", "General 7"});
                break;

            case R.id.tankRb:
                setAdapter(b, new String[]{"DI 1", "DI 2", "DI 3", "DI 4", "DI 5", "DI 6", "DI 7", "DI 8"});
                break;

            case R.id.virtualSensorRb:
                setAdapter(b, new String[]{"DI 1", "DI 2", "DI 3", "DI 4", "DI 5", "DI 6", "DI 7", "DI 8"});
                break;

            case R.id.outputRb:
                setAdapter(b, new String[]{"Output 1", "Output 2", "Output 3", "Output 4", "Output 5", "Output 6"});
                break;
        }
    }

    @Override
    public void onClick(int sensorInputNo) {

    }

    @Override
    public void onClick(String sensorInputNo) {
        fragment.dismiss();
    }
}
