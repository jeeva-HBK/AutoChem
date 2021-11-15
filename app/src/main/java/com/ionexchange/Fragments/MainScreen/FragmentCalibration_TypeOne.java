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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
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
import static com.ionexchange.Others.PacketControl.PCK_DIAGNOSTIC;
import static com.ionexchange.Others.PacketControl.PCK_SENSORCALIB;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentCalibration_TypeOne extends Fragment implements CompoundButton.OnCheckedChangeListener, DataReceiveCallback {
    SensorDetailsCalibrationBinding mBinding;
    AlertDialog alertReading, alertStabilization;
    ApplicationClass mAppClass;
    int stabilizationCount = 0;
    String lastStabValue = null, inputNumber, inputType, bufferType, tempValue;
    CountDownTimer stabilizationTimer;
    boolean calibCompleted = false;
    EditText leftEdt;

    EditText autoDectectedValue;
    private static final String TAG = "FragmentSensor";

    public FragmentCalibration_TypeOne(String inputNumber, String inputType, String bufferType) {
        this.inputNumber = inputNumber;
        this.inputType = getValueFromArr(inputType, inputTypeArr);
        this.bufferType = bufferType;
    }

    public FragmentCalibration_TypeOne() {
    }

    public FragmentCalibration_TypeOne(String inputNumber, String inputType) {
        this.inputType = inputType;
        this.inputNumber = inputNumber;
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

        mAppClass = (ApplicationClass) getActivity().getApplication();

        if (!inputType.equals("null")) {
            if ("pH".equals(inputType)) {
                if (!bufferType.equals("null")) {
                    mBinding.precise.performClick();
                    mBinding.calibrationTypeTxt.setText(getValueFromArr(bufferType, bufferArr));
                    mBinding.calibrationSensorName.setText(inputType);
                    mBinding.extPreciseValue.setText(getValueFromArr(bufferType, bufferArr));
                }
            } else if ("ORP".equals(inputType)) {
                mBinding.txtCalibrationValue.setVisibility(View.GONE);
                mBinding.txtGain.setVisibility(View.GONE);
                mBinding.txtGainValue.setVisibility(View.GONE);
                mBinding.txtOffset.setVisibility(View.GONE);
                mBinding.txtOffSetValue.setVisibility(View.GONE);
                mBinding.txtCurrentMode.setVisibility(View.GONE);
                mBinding.calibrationTypeTxt.setVisibility(View.GONE);
                mBinding.calibrationSensorName.setText("ORP Calibration");
                mBinding.precise.setVisibility(View.GONE);
                mBinding.quick.performClick();
            }
        } else {
            Toast.makeText(getContext(), "Sensor Type is Null !", Toast.LENGTH_SHORT).show();
        }

        mBinding.startCalibrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("pH".equals(inputType)) {
                    startPHcalibration();
                } else if ("ORP".equals(inputType)) {
                    startQuickCalibration();
                }
            }
        });
    }

    private void startPHcalibration() {
        if (getCalibrationType().equals("0")) { // Quick
            startQuickCalibration(); // pH - Quick -> step 1
        } else { // Precise
            if (bufferType.equals("0")) { // AUTO
                showPHAutoCalib("Rinse the sensor and insert it in 7.01 buffer solution");
            } else {
                showPHManualCalib("Enter First Buffer Value", "First Buffer Temperature", "First Buffer Value"); // Manual -> step 1
            }
        }
    }

    /* Quick Calibration */
    private void startQuickCalibration() {
        if (!mBinding.extPreciseValue.getText().toString().equals("")) {  // todo verify
            tempValue = mBinding.extPreciseValue.getText().toString();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                    PCK_SENSORCALIB + SPILT_CHAR + inputNumber + SPILT_CHAR + inputType + SPILT_CHAR + "1" +
                    SPILT_CHAR + "0" + SPILT_CHAR + "0" + SPILT_CHAR + mBinding.extPreciseValue.getText().toString());
        } else {
            mAppClass.showSnackBar(getContext(), "Calibration Value should not be empty !");
        }
        // next step - write response
    }

    private void readQuickStabilization() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_twoedt, null);
        dialogBuilder.setView(dialogView);
        alertReading = dialogBuilder.create();

        TextView leftText = dialogView.findViewById(R.id.twodt_mainTxt);
        TextView rightText = dialogView.findViewById(R.id.twodt_mainTxt2);
        TextView subText = dialogView.findViewById(R.id.twodt_subTxt);
        leftEdt = dialogView.findViewById(R.id.twodt_edt);
        EditText rightEdt = dialogView.findViewById(R.id.twodt_edt2);
        Button leftBtn = dialogView.findViewById(R.id.twodt_leftBtn);
        Button rightBtn = dialogView.findViewById(R.id.twodt_rightBtn);

        leftText.setText("Calibration Value");
        rightText.setText("Countdown");

        if (tempValue != null && !tempValue.equals("")) {
            leftEdt.setText(tempValue);
        }

        leftEdt.setEnabled(false);
        subText.setText("Reading, Please Wait...");
        leftBtn.setText("CANCEL");
        final boolean[] canSend = {true};
        leftBtn.setOnClickListener(View -> {
            canSend[0] = false;
            alertReading.dismiss();
        });
        rightBtn.setText("CONFIRM");
        rightBtn.setAlpha(0.5f);
        rightEdt.setEnabled(false);

        rightBtn.setOnClickListener(View -> {
            sendpHreadPck();
        });
        if (stabilizationTimer != null) {
            stabilizationTimer.cancel();
        }
        stabilizationCount = 0;
        stabilizationTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                rightEdt.setText("" + millisUntilFinished / 1000);
                checkStabilization(inputNumber);
            }

            public void onFinish() {
                rightEdt.setText("00");
                rightBtn.setEnabled(true);
                rightBtn.setAlpha(1f);
                alertReading.dismiss();
            }
        };
        stabilizationTimer.start();

        alertReading.show();
        // next step - read response
    }

    private void showPHManualCalib(String msg, String txt1, String txt2) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_twoedt, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        MaterialToolbar toolbar = dialogView.findViewById(R.id.materialToolbar);
        TextView leftText = dialogView.findViewById(R.id.twodt_mainTxt);
        TextView rightText = dialogView.findViewById(R.id.twodt_mainTxt2);
        TextView subText = dialogView.findViewById(R.id.twodt_subTxt);
        EditText leftEdt = dialogView.findViewById(R.id.twodt_edt);
        EditText rightEdt = dialogView.findViewById(R.id.twodt_edt2);
        Button leftBtn = dialogView.findViewById(R.id.twodt_leftBtn);
        Button rightBtn = dialogView.findViewById(R.id.twodt_rightBtn);

        toolbar.setTitle(msg);
        leftText.setText(txt1);
        rightText.setText(txt2);
        subText.setVisibility(View.GONE);
        rightBtn.setText("CONFIRM");
        leftBtn.setText("CANCEL");

        leftBtn.setOnClickListener(View -> {
            alertDialog.dismiss();
        });

        rightBtn.setOnClickListener(View -> {
            if (leftEdt.getText().toString().equals("") || leftEdt.getText().toString() == null) {
                mAppClass.showSnackBar(getContext(), leftText.getText().toString() + " should not be empty");
            } else if (rightEdt.getText().toString().equals("") || rightEdt.getText().toString() == null) {
                mAppClass.showSnackBar(getContext(), rightText.getText().toString() + " should not be empty");
            } else {
                alertDialog.dismiss();
                mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                        PCK_SENSORCALIB + SPILT_CHAR + inputNumber + SPILT_CHAR +
                        getPosition(2, inputType, inputTypeArr) + SPILT_CHAR + "1" + SPILT_CHAR + getCalibrationType() + SPILT_CHAR +
                        leftEdt.getText().toString() + SPILT_CHAR + rightEdt.getText().toString());
            }
        });
        alertDialog.show();
    }

    private void showPHAutoCalib(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        alertReading = dialogBuilder.create();

        TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
        Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        mainText.setText(msg);
        cancel.setText("CANCEL");
        confirm.setText("CONFIRM");
        subText.setVisibility(View.INVISIBLE);

        cancel.setOnClickListener(View -> {
            alertReading.dismiss();
            if (stabilizationTimer != null) {
                stabilizationTimer.cancel();
            }
        });

        confirm.setOnClickListener(View -> {
            alertReading.dismiss();
            mAppClass.sendPacket(this,
                    DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                            PCK_SENSORCALIB + SPILT_CHAR + inputNumber + SPILT_CHAR +
                            getPosition(2, inputType, inputTypeArr) + SPILT_CHAR +
                            "1" + SPILT_CHAR + "0" + SPILT_CHAR + getCalibrationType() + SPILT_CHAR + "1");
        });
        alertReading.show();
        // next step -  write response
    }

    private String getCalibrationType() {
        if (mBinding.relativeRadioGroup.getCheckedRadioButtonId() == mBinding.quick.getId()) {
            return "0";
        }
        return "1";
    }

    void startTimer(String inputNumber, String inpuType, String seqNumber) {
        stabilizationTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                sendStabilizationPacket(inputNumber, inpuType, seqNumber);
                start();
            }
        }.start();
    }

    private void sendStabilizationPacket(String inputNumber, String inpuType, String seqNumber) {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_SENSORCALIB + SPILT_CHAR + inputNumber + SPILT_CHAR +
                inpuType + SPILT_CHAR + seqNumber + SPILT_CHAR + "0");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.precise:
                if (isChecked) {
                    mBinding.txtChange.setText("CALIBRATION MODE");
                    mBinding.extPreciseValue.setEnabled(false);
                    mBinding.extPreciseValue.setClickable(false);

                    if (inputType.equals("pH")) {
                        mBinding.extPreciseValue.setText(getValueFromArr(bufferType, bufferArr));
                    }
                }
                break;
            case R.id.quick:
                if (isChecked) {
                    mBinding.txtChange.setText("Please Enter the Calibration Value");
                    mBinding.extPreciseValue.setText("");
                    mBinding.extPreciseValue.setHint("00");
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
                    if ("pH".equals(inputType)) {
                        if (getCalibrationType().equals("0")) {
                            readQuickStabilization(); // pH - Quick - step 2
                        } else {
                            if (bufferType.equals("0")) { // Auto
                                readQuickStabilization();
                            } else {
                                startpHManualStabilization();
                            }
                        }
                    } else if ("ORP".equals(inputType)) {
                        readQuickStabilization();
                    }
                } else {
                    mAppClass.showSnackBar(getContext(), "Write Failed !");
                }
            } else if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    if ("pH".equals(inputType)) {
                        if (getCalibrationType().equals("0")) {
                            showPhResult(splitData[4], splitData[5]);
                        } else {

                        }
                    } else if ("ORP".equals(inputType)) {
                        showOrpResult(splitData[4], splitData[5]);
                    }
                } else {
                    mAppClass.showSnackBar(getContext(), "Read Failed !");
                }
            }
        }
    }

    private void readAutoStabilization() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_twoedt, null);
        dialogBuilder.setView(dialogView);
        alertReading = dialogBuilder.create();

        TextView leftText = dialogView.findViewById(R.id.twodt_mainTxt);
        TextView rightText = dialogView.findViewById(R.id.twodt_mainTxt2);
        TextView subText = dialogView.findViewById(R.id.twodt_subTxt);
        EditText leftEdt = dialogView.findViewById(R.id.twodt_edt);
        EditText rightEdt = dialogView.findViewById(R.id.twodt_edt2);
        Button leftBtn = dialogView.findViewById(R.id.twodt_leftBtn);
        Button rightBtn = dialogView.findViewById(R.id.twodt_rightBtn);

        leftText.setText("Auto Detected Value");
        rightText.setText("Countdown");

        if (tempValue != null && !tempValue.equals("")) {
            leftEdt.setText(tempValue);
        }

        leftEdt.setEnabled(false);
        subText.setText("Reading, Please Wait...");
        leftBtn.setText("CANCEL");
        final boolean[] canSend = {true};
        leftBtn.setOnClickListener(View -> {
            canSend[0] = false;
            alertReading.dismiss();
        });
        rightBtn.setText("CONFIRM");
        rightBtn.setAlpha(0.5f);
        rightEdt.setEnabled(false);

        rightBtn.setOnClickListener(View -> {
            sendpHreadPck();
        });
        if (stabilizationTimer != null) {
            stabilizationTimer.cancel();
        }
        stabilizationCount = 0;
        stabilizationTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                rightEdt.setText("" + millisUntilFinished / 1000);
                checkStabilization(inputNumber);
            }

            public void onFinish() {
                rightEdt.setText("00");
                rightBtn.setEnabled(true);
                rightBtn.setAlpha(1f);
                alertReading.dismiss();
            }
        };
        stabilizationTimer.start();

        alertReading.show();
    }

    private void showOrpResult(String offset, String calibValue) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_twoedt, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertReading = dialogBuilder.create();

        ImageView iv = dialogView.findViewById(R.id.resultTwo_iv);
        TextView gainTv = dialogView.findViewById(R.id.resulTwo_leftValue);
        TextView offsetTv = dialogView.findViewById(R.id.resulTwo_rightValue);
        Button leftBtn = dialogView.findViewById(R.id.resultTwo_leftBtn);
        Button rightBtn = dialogView.findViewById(R.id.resultTwo_rightBtn);

        gainTv.setText("Calibrated Value");
        if (Double.parseDouble(offset) >= 0.5 && Double.parseDouble(offset) <= 1.5) {
            iv.setImageResource(R.drawable.ic_success);
            gainTv.setText(calibValue);
            leftBtn.setVisibility(View.GONE);
            offsetTv.setText(offset);
        } else {
            iv.setImageResource(R.drawable.ic_failed);
            gainTv.setText(calibValue);
            offsetTv.setText(offset);
        }
        leftBtn.setText("RETRY");
        rightBtn.setText("CONFIRM");
        leftBtn.setOnClickListener(View -> {
            sendpHreadPck();
        });
        rightBtn.setOnClickListener(View -> {
            alertReading.dismiss();
        });

        alertReading.show();
    }

    private void showPhResult(String value1, String value2) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_result_two, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertReading = dialogBuilder.create();

        ImageView iv = dialogView.findViewById(R.id.resultTwo_iv);
        TextView leftValue = dialogView.findViewById(R.id.resulTwo_leftValue);
        TextView rightValue = dialogView.findViewById(R.id.resulTwo_rightValue);
        Button leftBtn = dialogView.findViewById(R.id.resultTwo_leftBtn);
        Button rightBtn = dialogView.findViewById(R.id.resultTwo_rightBtn);
        TextView leftHeading = dialogView.findViewById(R.id.resultTwo_leftHeading);
        TextView rightHeading = dialogView.findViewById(R.id.resultTwo_rightHeading);

        leftHeading.setText("Calibration value");
        rightHeading.setText("Offset");

        if (Double.parseDouble(value1) >= -140 && Double.parseDouble(value1) <= 140) {
            iv.setImageResource(R.drawable.ic_success);
            leftValue.setText(value2.substring(0, 4));
            leftBtn.setVisibility(View.GONE);
            rightValue.setText(value1.substring(0, 4));
        } else {
            iv.setImageResource(R.drawable.ic_failed);
            leftValue.setText(value2.substring(0, 4));
            rightValue.setText(value1.substring(0, 4));
        }

        leftBtn.setText("RETRY");
        rightBtn.setText("CONFIRM");

        leftBtn.setOnClickListener(View -> {
            sendpHreadPck();
            alertReading.dismiss();
        });

        rightBtn.setOnClickListener(View -> {
            alertReading.dismiss();
        });

        alertReading.show();
    }

    private void sendpHreadPck() {
        mAppClass.sendPacket(this::OnDataReceive, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR +
                PCK_SENSORCALIB + SPILT_CHAR + inputNumber + SPILT_CHAR + getPosition(2, inputType, inputTypeArr) + SPILT_CHAR + "1" + SPILT_CHAR + "0");
    }

    private void checkStabilization(String value) {
        int inputNo = Integer.parseInt(value);
        String setID = "0";
        if (inputNo < 11) {
            setID = "0";
        } else if (inputNo < 21) {
            setID = "1";
        } else if (inputNo < 31) {
            setID = "2";
        } else if (inputNo < 41) {
            setID = "3";
        } else if (inputNo < 57) {
            setID = "4";
        }
        mAppClass.sendPacket(new DataReceiveCallback() {
            @Override
            public void OnDataReceive(String data) {
                String[] splitData = data.split("\\*")[1].split("\\$");
                if (isValidStabilizationPck(splitData)) {
                    if (getCalibrationType().equals("1")) {
                        leftEdt.setText(splitData[inputNo + 3].substring(2, splitData[inputNo + 3].length()));
                    }

                    if (splitData[inputNo + 3].substring(2, splitData[inputNo + 3].length()).equals(lastStabValue)) {
                        stabilizationCount++;
                        if (stabilizationCount > 5) {
                            stabilizationTimer.cancel();
                            alertReading.dismiss();
                            sendpHreadPck();
                        }
                    }
                    lastStabValue = splitData[inputNo + 3].substring(2, splitData[inputNo + 3].length());
                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET +
                SPILT_CHAR + PCK_DIAGNOSTIC + SPILT_CHAR + setID);
    }

    private boolean isValidStabilizationPck(String[] splitData) {
        if (splitData[0].equals(READ_PACKET)) {
            if (splitData[1].equals(PCK_DIAGNOSTIC)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void startpHManualStabilization() {
        stabilizationCount = 0;
        startTimer(inputNumber, inputType, "0");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertpHManual = dialogBuilder.create();

        TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
        Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        mainText.setText("Rinse the sensor and insert it in first buffer");
        cancel.setText("CANCEL");
        confirm.setText("CONFIRM");
        subText.setVisibility(View.INVISIBLE);
        confirm.setEnabled(false);
        confirm.setAlpha(0.5f);

        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                confirm.setEnabled(true);
                confirm.setAlpha(1f);
            }
        };


        cancel.setOnClickListener(View -> {
            alertpHManual.cancel();
        });

        confirm.setOnClickListener(View -> {
            alertpHManual.cancel();
        });
        alertpHManual.show();
    }

}