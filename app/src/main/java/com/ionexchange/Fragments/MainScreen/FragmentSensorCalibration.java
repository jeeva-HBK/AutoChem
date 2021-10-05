package com.ionexchange.Fragments.MainScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.R;
import com.ionexchange.databinding.SensorDetailsCalibrationBinding;

public class FragmentSensorCalibration extends Fragment implements CompoundButton.OnCheckedChangeListener {

    SensorDetailsCalibrationBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.sensor_details_calibration, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.precise.setOnCheckedChangeListener(this);
        mBinding.quick.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.precise:
                if (isChecked){
                    mBinding.txtChange.setText("CALIBRATION MODE");
                    mBinding.extPreciseValue.setText("AUTO");
                    mBinding.extPreciseValue.setEnabled(false);
                    mBinding.extPreciseValue.setClickable(false);
                }
                break;
            case R.id.quick:
                if (isChecked){
                    mBinding.txtChange.setText("Please Enter the Calibration Value");
                    mBinding.extPreciseValue.setText("4.5");
                    mBinding.extPreciseValue.setEnabled(true);
                    mBinding.extPreciseValue.setClickable(true);
                }
                break;

        }
    }
}
