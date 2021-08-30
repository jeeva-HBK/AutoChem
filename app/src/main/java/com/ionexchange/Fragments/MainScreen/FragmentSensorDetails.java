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
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.SensorDetailsRvAdapter;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSensorDetailsBinding;


public class FragmentSensorDetails extends Fragment {

    FragmentSensorDetailsBinding mBinding;
    SensorDetailsRvAdapter sensorDetailsRvAdapter;
    int type = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_sensor_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sensorDetailsRvAdapter = new SensorDetailsRvAdapter();
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mBinding.recyclerView.setAdapter(sensorDetailsRvAdapter);
        mBinding.btnTrendCalibartion.setChecked(true);
        getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), new FragmentSensorCalibration()).commit();
        mBinding.btnTrendCalibartion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), new FragmentSensorCalibration()).commit();
                    mBinding.txtTrendCalibration.setText("TREND");
                    mBinding.viewTrendCalibration.setBackground(getContext().getDrawable(R.drawable.graph));
                } else {
                    getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), new FragmentSensorStatistics()).commit();
                    mBinding.txtTrendCalibration.setText("CALIBRATION");
                    mBinding.viewTrendCalibration.setBackground(getContext().getDrawable(R.drawable.flask));
                }
            }
        });


    }
}
