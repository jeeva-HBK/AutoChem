package com.ionexchange.Fragments.MainScreen;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.SensorDetailsCalibrationBinding;

import static com.ionexchange.Others.ApplicationClass.bufferArr;
import static com.ionexchange.Others.ApplicationClass.getPosition;
import static com.ionexchange.Others.ApplicationClass.getValueFromArr;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_SENSORCALIB;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentSensorCalibration extends Fragment implements CompoundButton.OnCheckedChangeListener, DataReceiveCallback {
    SensorDetailsCalibrationBinding mBinding;
    String sensorType;
    Bundle bundle;
    AlertDialog alertReading;
    ApplicationClass mAppClass;
    int stabilizationCount = 0;
    String lastStabValue = null;
    CountDownTimer mTimer;

    public FragmentSensorCalibration(Bundle b) {
        this.bundle = b;
        this.sensorType = b.getString("sensorType");
    }

    public FragmentSensorCalibration() {

    }

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

        mAppClass = (ApplicationClass) getActivity().getApplication();

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
                switch (sensorType) {
                    case "pH":
                        startPHcalibration();
                        break;

                    case "ORP":
                        // todo
                        break;
                }
            }
        });
    }

    private void startPHcalibration() {
        if (bundle.getString("bufferType").equals("0")) { // AUTO
            /* AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
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
                editText.getText().toString();
            });

            alertDialog.show();
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.70);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.70);
            alertDialog.getWindow().setLayout(width, height);*/
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
            dialogBuilder.setView(dialogView);
            alertReading = dialogBuilder.create();

            TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
            TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
            Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
            Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

            mainText.setText("Rinse the sensor and insert it in 7.01 buffer solution");
            cancel.setText("CANCEL");
            confirm.setText("CONFIRM");
            subText.setVisibility(View.INVISIBLE);

            cancel.setOnClickListener(View -> {
                alertReading.dismiss();
            });

            confirm.setOnClickListener(View -> {
                mAppClass.sendPacket(this,
                        DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                                PCK_SENSORCALIB + SPILT_CHAR + bundle.getString("inputNumber") + SPILT_CHAR + getPosition(2, sensorType, inputTypeArr) + SPILT_CHAR + "1" + getCalibrationType());
            });

            alertReading.show();
        } else {
            Toast.makeText(getContext(), "Manual", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCalibrationType() {
        if (mBinding.relativeRadioGroup.getCheckedRadioButtonId() == mBinding.quick.getId()) {
            return "0";
        }
        return "1";
    }

    void startTimer(String inputNumber, String inpuType, String seqNumber) {
        mTimer = new CountDownTimer(5000, 0) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                sendStabilizationPacket(inputNumber, inpuType, seqNumber);
            }
        };
        mTimer.start();
    }

    private void startpHStabilization() {
        startTimer(bundle.getString("inputNumber"), getPosition(2, sensorType, inputTypeArr), "1");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_twoedt, null);
        dialogBuilder.setView(dialogView);
        alertReading = dialogBuilder.create();

        TextView mainText = dialogView.findViewById(R.id.twodt_mainTxt);
        TextView subText = dialogView.findViewById(R.id.twodt_subTxt);
        EditText edtOne = dialogView.findViewById(R.id.twodt_edt);
        EditText edtTwo = dialogView.findViewById(R.id.twodt_edt2);
        Button cancel = dialogView.findViewById(R.id.twodt_leftBtn);
        Button confirm = dialogView.findViewById(R.id.twodt_rightBtn);

        /* new CountDownTimer(1000, 0) {
            public void onTick(long millisUntilFinished) {
                edtTwo.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                edtTwo.setText("done!");
                start();
            }
        }.start();*/

        cancel.setOnClickListener(View -> {
            alertReading.dismiss();
        });

        confirm.setOnClickListener(View -> {

        });
        alertReading.show();
    }

    private void sendStabilizationPacket(String inputNumber, String inpuType, String seqNumber) {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + READ_PACKET + SPILT_CHAR + PCK_SENSORCALIB + SPILT_CHAR + inputNumber + SPILT_CHAR +
                inpuType + SPILT_CHAR + seqNumber);
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
                if (isChecked) {
                    mBinding.txtChange.setText("Please Enter the Calibration Value");
                    mBinding.extPreciseValue.setText("4.5");
                    mBinding.extPreciseValue.setEnabled(true);
                    mBinding.extPreciseValue.setClickable(true);
                }
                break;
        }
    }

    @Override
    public void OnDataReceive(String data) {
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        } else if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        } else if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        } else if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.timeout));
        } else if (data != null) {
            handleResponse(data.split("\\*")[1].split(RES_SPILT_CHAR));
        }
    }

    private void handleResponse(String[] splitData) {
        if (splitData[1].equals("10")) {
            if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    switch (sensorType) {
                        case "pH":
                            alertReading.dismiss();
                            startpHStabilization();
                            break;
                    }
                } else {
                    mAppClass.showSnackBar(getContext(), "Write Failed !");
                }
            } else if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    if (splitData[3].equals(bundle.getString("inputNumber"))) {
                        Toast.makeText(mAppClass, "Calib-Value =" + splitData[4], Toast.LENGTH_SHORT).show();
                        if (splitData[4].equals(lastStabValue)) {
                            stabilizationCount++;
                        }
                        if (stabilizationCount >= 5) {
                            mTimer.cancel();
                            Toast.makeText(mAppClass, "Sensor Calib Stabilized !", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    mAppClass.showSnackBar(getContext(), "Read Failed !");
                }
            }
        }
    }
}
