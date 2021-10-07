package com.ionexchange.Fragments.MainScreen;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.R;
import com.ionexchange.databinding.SensorDetailsCalibrationBinding;

import static com.ionexchange.Others.ApplicationClass.bufferArr;
import static com.ionexchange.Others.ApplicationClass.getValueFromArr;

public class FragmentSensorCalibration extends Fragment implements CompoundButton.OnCheckedChangeListener {
    SensorDetailsCalibrationBinding mBinding;
    String sensorType;
    Bundle bundle;

    public FragmentSensorCalibration(Bundle b) {
        this.bundle = b;
        this.sensorType = b.getString("sensorType");
    }

    public FragmentSensorCalibration() { }

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
        mBinding.precise.performClick();
        if (!sensorType.equals("null")) {
            switch (sensorType) {
                case "pH":
                    String bufferType = bundle.getString("bufferType");
                    if (!bufferType.equals("null")) {
                        mBinding.calibrationTypeTxt.setText(getValueFromArr(bufferType, bufferArr));
                        mBinding.calibrationSensorName.setText(sensorType);
                    }
                    break;
            }
        } else {
            Toast.makeText(getContext(), "Sensor Type is Null !", Toast.LENGTH_SHORT).show();
        }

        mBinding.startCalibrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCalibration(sensorType);
            }
        });
    }

    private void startCalibration(String sensorType) {
        switch (sensorType) {
            case "pH":
                startPHcalibration();
                break;

            case "ORP":
                // todo
                break;
        }
    }

    private void startPHcalibration() {
        if (bundle.getString("bufferType").equals("0")) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_calib_firstbuffervalue, null);
            dialogBuilder.setView(dialogView);
            AlertDialog alertDialog = dialogBuilder.create();
            EditText editText = dialogView.findViewById(R.id.ext_value);
            Button cancel = dialogView.findViewById(R.id.cancel_btn);
            Button confirm = dialogView.findViewById(R.id.confirm_btn);

            cancel.setOnClickListener(view -> {
                alertDialog.dismiss();
            });
            confirm.setOnClickListener(view -> {
                editText.getText().toString(); // todo Stopped Here
            });

            alertDialog.show();
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.50);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.55);
            alertDialog.getWindow().setLayout(width, height);
        } else {
            Toast.makeText(getContext(), "Manual", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.precise:
                if (isChecked) {
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
