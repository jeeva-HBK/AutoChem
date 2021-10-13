package com.ionexchange.Fragments.MainScreen;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentCalibrationModbusBinding;

import org.jetbrains.annotations.NotNull;

public class FragmentModbusCalibration extends Fragment {
    FragmentCalibrationModbusBinding mBinding;
    ApplicationClass mAppClass;
    Bundle mBundle;
    public FragmentModbusCalibration(Bundle bundle) {
    this.mBundle = bundle;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_calibration_modbus, container,false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();

        if (!mBundle.isEmpty()){
            mBinding.modbusCalibType.setText(mBundle.getString("ModbusType") + " | "+ mBundle.getString("TypeOfValue"));
        }

        mBinding.modbusCalibStartBtn.setOnClickListener(View ->{
            switch (mBinding.modbusCalibRg.getCheckedRadioButtonId()){
                case -1:
                    mAppClass.showSnackBar(getContext(), "Please Select a Calibration Mode");
                    break;

                case R.id.zeroCalibRb:
                    startZeroCalibration();
                    break;

                case R.id.slopeCalibRb:

                    break;

                case R.id.diagnosticRv:

                    break;
            }
        });
    }

    private void startZeroCalibration() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_zerocalib1, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        Button cancel = alertDialog.findViewById(R.id.cancelZeroCalib1);
        Button confirm = alertDialog.findViewById(R.id.confirmZeroCalib1);

        cancel.setOnClickListener(View ->{
            alertDialog.dismiss();
        });

        confirm.setOnClickListener(View ->{
          // todo StoppedHere - DayEnd
        });
        alertDialog.show();
        /*   int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.50);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
        alertDialog.getWindow().setLayout(width, height);*/
    }
}
