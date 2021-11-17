package com.ionexchange.Fragments.MainScreen;

import static com.ionexchange.Others.ApplicationClass.bufferArr;
import static com.ionexchange.Others.ApplicationClass.getPosition;
import static com.ionexchange.Others.ApplicationClass.getValueFromArr;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_SENSORCALIB;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

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

import com.ionexchange.Database.Dao.CalibrationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Entity.CalibrationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.SensorDetailsCalibrationBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentCalibration_TypeOne extends Fragment implements CompoundButton.OnCheckedChangeListener {
    SensorDetailsCalibrationBinding mBinding;
    ApplicationClass mAppClass;
    WaterTreatmentDb db;
    CalibrationDao calibrationDao;
    KeepAliveCurrentValueDao keepAliveDao;
    String inputNumber, inputType, bufferType, tempValue;
    private static final String TAG = "FragmentSensorCalib";

    public FragmentCalibration_TypeOne(String inputNumber, String inputType, String bufferType) {
        this.inputNumber = inputNumber;
        this.inputType = getValueFromArr(inputType, inputTypeArr);
        this.bufferType = bufferType;
    }

    public FragmentCalibration_TypeOne() {
    }

    public FragmentCalibration_TypeOne(String inputNumber, String inputType) {
        this.inputType = getValueFromArr(inputType, inputTypeArr);
        ;
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
        db = WaterTreatmentDb.getDatabase(getContext());
        keepAliveDao = db.keepAliveCurrentValueDao();

        mAppClass = (ApplicationClass) getActivity().getApplication();

        // last Calibration value
        db = WaterTreatmentDb.getDatabase(getContext());
        calibrationDao = db.calibrationDao();

        CalibrationEntity lastCalibrationData = calibrationDao.getLastCalibrationData(Integer.parseInt(inputNumber));
        if (lastCalibrationData != null) {
            mBinding.txtCalibrationDate.setText(lastCalibrationData.getDate());
            String[] calibData = lastCalibrationData.getCalibrationValue().split("\\$");
            mBinding.txt1.setText(calibData[1]);
            mBinding.txt2.setText(calibData[2]);
            mBinding.txtGreen.setText(calibData[0].equals("0") ? "Offset" : "Gain");
        }

        mBinding.calibrationSensorName.setText(inputType);
        if (!inputType.equals("null")) {
            if ("pH".equals(inputType)) {
                if (!bufferType.equals("null")) {
                    mBinding.precise.performClick();
                    mBinding.currentModeValue.setText(getValueFromArr(bufferType, bufferArr));
                    mBinding.extValue.setText(getValueFromArr(bufferType, bufferArr));
                }
            } else if ("ORP".equals(inputType)) {
                mBinding.precise.setVisibility(View.GONE);
                mBinding.quick.performClick();
                mBinding.currentModeValue.setVisibility(View.GONE);
                mBinding.txtCurrentMode.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getContext(), "Sensor Type is Null !", Toast.LENGTH_SHORT).show();
        }

        mBinding.startCalibrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("pH".equals(getValueFromArr(inputType, inputTypeArr))) {
                    startPHcalibration();
                } else if ("ORP".equals(getValueFromArr(inputType, inputTypeArr))) {
                    startQuickCalibration();
                }
            }
        });
    }

    /* Quick Calibration */
    private void startQuickCalibration() {
        if (!mBinding.extValue.getText().toString().equals("")) {
            tempValue = mBinding.extValue.getText().toString();
            sendQuickCalibWritePacket();
        } else {
            mAppClass.showSnackBar(getContext(), "Calibration Value should not be empty !");
        }
    }

    private void sendQuickCalibWritePacket() { // common for all sensor
        mAppClass.sendPacket(new DataReceiveCallback() {
            @Override
            public void OnDataReceive(String data) {
                if (isValidPck(WRITE_PACKET, data)) {
                    checkStabilization("0");
                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_SENSORCALIB + SPILT_CHAR + inputNumber + SPILT_CHAR + inputType + SPILT_CHAR + "1" +
                SPILT_CHAR + "0" + SPILT_CHAR + "0" + SPILT_CHAR + mBinding.extValue.getText().toString());
    }


    private void startPHcalibration() {
        if (getCalibrationType().equals("0")) { // Quick
            startQuickCalibration(); // pH - Quick -> step 1
        } else { // Precise
            startPreceiseCalibration("1");
        }
    }

    private void startPreceiseCalibration(String firstORsecond) {
        if (inputType.equals("pH")) {
            if (bufferType.equals("0")) { // AUTO
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_calib_reading, null);
                dialogBuilder.setView(dialogView);
                AlertDialog alertDialog = dialogBuilder.create();

                TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
                TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
                Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
                Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

                mainText.setText(firstORsecond.equals("1") ? "\"Rinse the sensor and insert it in 7.01 buffer solution\""
                        : "\"Rinse the sensor and insert it in 4.01 or 10.00 buffer solution\"");
                cancel.setText("CANCEL");
                confirm.setText("CONFIRM");
                cancel.setOnClickListener(View -> {
                    alertDialog.dismiss();
                });
                confirm.setOnClickListener(View -> {
                    alertDialog.dismiss();
                    sendPreceiseCalibWritePacket(firstORsecond);
                });
                alertDialog.show();
            } else { // Manual
                getPhManualValues("First");
            }
        }
    }

    private void getPhManualValues(String type) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_calib_twoedt, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        TextView leftText = dialogView.findViewById(R.id.twodt_mainTxt);
        TextView rightText = dialogView.findViewById(R.id.twodt_mainTxt2);
        TextView subText = dialogView.findViewById(R.id.twodt_subTxt);
        EditText leftEdt = dialogView.findViewById(R.id.twodt_edt);
        EditText rightEdt = dialogView.findViewById(R.id.twodt_edt2);
        Button leftBtn = dialogView.findViewById(R.id.twodt_leftBtn);
        Button rightBtn = dialogView.findViewById(R.id.twodt_rightBtn);

        leftText.setText(type + " Temperature Value");
        rightText.setText(type + " Buffer Value");
        leftBtn.setText("CANCEL");
        rightBtn.setText("CONFIRM");

        leftBtn.setOnClickListener(View -> {
            alertDialog.dismiss();
        });

        rightBtn.setOnClickListener(View -> {
            if (leftEdt.getText().toString().equals("")) {
                mAppClass.showSnackBar(getContext(), "Temperature Value should not be empty");
                return;
            } else if (rightEdt.getText().toString().equals("")) {
                mAppClass.showSnackBar(getContext(), "Buffer Value should not be empty");
            } else {
                sendPreceiseCalibWritePacket(type.equals("First") ?
                        "1" + SPILT_CHAR + leftEdt.getText().toString() + SPILT_CHAR + rightEdt.getText().toString() :
                        "2" + SPILT_CHAR + leftEdt.getText().toString() + SPILT_CHAR + rightEdt.getText().toString());
            }
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void sendPreceiseCalibWritePacket(String firstORsecond) {
        mAppClass.sendPacket(new DataReceiveCallback() {
            @Override
            public void OnDataReceive(String data) {
                if (isValidPck(WRITE_PACKET, data)) {
                    if (bufferType.equals("0")) {
                        tempValue = firstORsecond.equals("1") ? "7.01" : "10.00";
                    } else {
                        tempValue = firstORsecond.split("\\$")[2];
                    }
                    //  data = firstORsecond.equals("1") ? "{*0$10$0$1*}" : "{*0$10$0$2*}"; // todo temp Manual Pck
                    String[] spiltData = spiltPacket(data);
                    if (spiltData[3].equals("1")) { // step 1
                        checkStabilization("1");
                    } else if (spiltData[3].equals("2")) { // step 2
                        checkStabilization("2");
                        // sendCalibReadPacket("2");
                    }
                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_SENSORCALIB + SPILT_CHAR + inputNumber + SPILT_CHAR +
                getPosition(2, inputType, inputTypeArr) + SPILT_CHAR + "1" + SPILT_CHAR +
                "0" + SPILT_CHAR + getCalibrationType() + SPILT_CHAR + firstORsecond);
    }

    private String[] spiltPacket(String data) {
        return data.split("\\*")[1].split("\\$");
    }

    private void checkStabilization(String type) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_calib_result_two, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        ImageView iv = dialogView.findViewById(R.id.resultTwo_iv);
        TextView leftValue = dialogView.findViewById(R.id.resulTwo_leftValue);
        TextView rightValue = dialogView.findViewById(R.id.resulTwo_rightValue);
        Button leftBtn = dialogView.findViewById(R.id.resultTwo_leftBtn);
        Button rightBtn = dialogView.findViewById(R.id.resultTwo_rightBtn);
        TextView leftHeading = dialogView.findViewById(R.id.resultTwo_leftHeading);
        TextView rightHeading = dialogView.findViewById(R.id.resultTwo_rightHeading);

        iv.setImageResource(R.drawable.calib_flask);
        leftHeading.setText("Detected Value");
        rightHeading.setText("CountDown");
        // leftValue.setText(mBinding.extValue.getText().toString());
        leftBtn.setText("CANCEL");
        rightBtn.setText("PROCEED");
        rightBtn.setText("PROCEED");

        rightBtn.setClickable(false);
        rightBtn.setAlpha(0.5f);
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        alertDialog.show();

        leftBtn.setOnClickListener(View -> {
            alertDialog.dismiss();
        });

        rightBtn.setOnClickListener(View -> {
            alertDialog.dismiss();
            if (getCalibrationType().equals("0")) {
                sendCalibReadPacket("0");
            } else {
                if (inputType.equals("pH")) {
                    if (bufferType.equals("0")) {
                        if (type.equals("1") || type.equals("0")) {
                            startPreceiseCalibration("2");
                        } else if (type.equals("2")) {
                            sendCalibReadPacket("2");
                        }
                    } else {
                        if (type.equals("1") || type.equals("0")) {
                            getPhManualValues("Second");
                        } else if (type.equals("2")) {
                            sendCalibReadPacket("2");
                        }
                    }
                } else {
                    // anyOtherSensor
                }
            }
        });

        final int[] stabilizationCount = {0};
        final long[] tempInt = {1};
        CountDownTimer stabilizationTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                rightValue.setText("" + seconds);
                leftValue.setText(keepAliveDao.getCurrentValue(Integer.parseInt(inputNumber)));
                if (tempInt[0] == 30 - seconds) {
                    if (!keepAliveDao.getCurrentValue(Integer.parseInt(inputNumber)).equals("N/A")) {
                        if ((int) Double.parseDouble(tempValue) == ((int) Double.parseDouble(keepAliveDao.getCurrentValue(Integer.parseInt(inputNumber))))) {
                            stabilizationCount[0]++;
                        }
                        tempInt[0] = tempInt[0] + 5;
                    }
                }

                if (stabilizationCount[0] > 3) {
                    cancel();
                    if (getCalibrationType().equals("0")) {
                        sendCalibReadPacket("0");
                    } else {
                        if (inputType.equals("pH")) {
                            if (bufferType.equals("0")) {
                                if (type.equals("1")) {
                                    startPreceiseCalibration("2");
                                } else if (type.equals("2")) {
                                    sendCalibReadPacket("2");
                                }
                            } else {
                                if (type.equals("1")) {
                                    getPhManualValues("Second");
                                } else if (type.equals("2")) {
                                    sendCalibReadPacket("2");
                                }
                            }
                        } else {
                            // anyOtherSensor
                        }
                    }
                    Toast.makeText(getContext(), "Sensor Stabilized", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }

            public void onFinish() {
                rightBtn.setAlpha(1f);
                rightBtn.setClickable(true);
            }
        };
        stabilizationTimer.start();
    }

    private void sendCalibReadPacket(String firstORsecond) {
        mAppClass.sendPacket(new DataReceiveCallback() {
            @Override
            public void OnDataReceive(String data) {
                if (isValidPck(READ_PACKET, data)) {
                    showCalibResult(spiltPacket(data));
                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR +
                PCK_SENSORCALIB + SPILT_CHAR + inputNumber + SPILT_CHAR +
                inputType + SPILT_CHAR + "1" + SPILT_CHAR + "0" +
                (firstORsecond.equals("0") ? "" : SPILT_CHAR + firstORsecond));
    }

    private void showCalibResult(String[] splitData) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_result_two, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertResult = dialogBuilder.create();

        ImageView iv = dialogView.findViewById(R.id.resultTwo_iv);
        TextView leftValue = dialogView.findViewById(R.id.resulTwo_leftValue);
        TextView rightValue = dialogView.findViewById(R.id.resulTwo_rightValue);
        Button leftBtn = dialogView.findViewById(R.id.resultTwo_leftBtn);
        Button rightBtn = dialogView.findViewById(R.id.resultTwo_rightBtn);
        TextView leftHeading = dialogView.findViewById(R.id.resultTwo_leftHeading);
        TextView rightHeading = dialogView.findViewById(R.id.resultTwo_rightHeading);

        leftBtn.setText("RETRY");
        rightBtn.setText("SAVE");

        leftBtn.setOnClickListener(View -> {
            alertResult.dismiss();
            mBinding.startCalibrationBtn.performClick();
        });

        rightBtn.setOnClickListener(View -> {
            alertResult.dismiss();
            saveCalibrationValue(splitData);
        });


        switch (getValueFromArr(inputType, inputTypeArr)) {
            case "pH":
                if (getCalibrationType().equals("0")) {
                    showQuickCalibResult(splitData, iv, leftValue, rightValue, leftHeading, rightHeading, leftBtn, rightBtn);
                } else {
                    showPreceiseCalibResult(splitData, iv, leftValue, rightValue, leftHeading, rightHeading, leftBtn, rightBtn);
                }
                break;


            case "ORP":
                showQuickCalibResult(splitData, iv, leftValue, rightValue, leftHeading, rightHeading, leftBtn, rightBtn);
                break;
        }
        alertResult.show();
    }


    private void showPreceiseCalibResult(String[] splitData, ImageView iv, TextView leftValue, TextView rightValue,
                                         TextView leftHeading, TextView rightHeading, Button leftBtn, Button rightBtn) {
        leftHeading.setText("Calibration Value");
        rightHeading.setText("Gain");
        String gain = splitData[4], calibrationValue = splitData[5];

        if (Double.parseDouble(gain) > 0.2 && Double.parseDouble(gain) < 1.2) { // calibrationSuccess
            calibrationValue = splitData[5].length() > 5 ? splitData[5].substring(0, 5) : splitData[5];

            gain = splitData[4].length() > 5 ? splitData[4].substring(0, 5) : splitData[4];
            iv.setImageResource(R.drawable.ic_success);
            leftValue.setText(calibrationValue);
            leftBtn.setVisibility(View.GONE);
            rightValue.setText(gain);
        } else {
            calibrationValue = splitData[5].length() > 5 ? splitData[5].substring(0, 5) : splitData[5];

            gain = splitData[4].length() > 5 ? splitData[4].substring(0, 5) : splitData[4];
            iv.setImageResource(R.drawable.ic_failed);
            leftValue.setText(calibrationValue);
            rightValue.setText(gain);
        }
    }

    private void showQuickCalibResult(String[] splitData, ImageView iv, TextView leftValue, TextView rightValue,
                                      TextView leftHeading, TextView rightHeading, Button leftBtn, Button rightBtn) {

        String offSet = splitData[4], calibratedValue = splitData[5];
        double minRange, maxRange;
        switch (inputType) {
            case "00":
                minRange = -140;
                maxRange = 140;
                break;

            case "01":
                minRange = -300;
                maxRange = 300;
                break;
            default:
                minRange = 0;
                maxRange = 0;
                break;
        }

        leftHeading.setText("Calibration value");
        rightHeading.setText("Offset");
        if (Double.parseDouble(offSet) >= minRange && Double.parseDouble(offSet) <= maxRange) {
            offSet = splitData[4].length() > 5 ? splitData[4].substring(0, 5) : splitData[4];
            calibratedValue = splitData[5].length() > 5 ? splitData[5].substring(0, 5) : splitData[5];

            iv.setImageResource(R.drawable.ic_success);
            leftValue.setText(calibratedValue);
            leftBtn.setVisibility(View.GONE);
            rightValue.setText(offSet);
        } else {
            offSet = splitData[4].length() > 5 ? splitData[4].substring(0, 5) : splitData[4];
            calibratedValue = splitData[5].length() > 5 ? splitData[5].substring(0, 5) : splitData[5];

            iv.setImageResource(R.drawable.ic_failed);
            leftValue.setText(calibratedValue);
            rightValue.setText(offSet);
        }
    }


    private String getCalibrationType() {
        if (mBinding.relativeRadioGroup.getCheckedRadioButtonId() == mBinding.quick.getId()) {
            return "0";
        }
        return "1";
    }

    private boolean isValidPck(String pckType, String data) {
        try {
            String[] splitData = data.split("\\*")[1].split("\\$");
            if (splitData[0].equals(pckType)) {
                if (splitData[1].equals(PCK_SENSORCALIB)) {
                    if (splitData[2].equals(RES_SUCCESS)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.precise:
                if (isChecked) {
                    mBinding.txtChange.setText("CALIBRATION MODE");
                    mBinding.extValue.setEnabled(false);
                    mBinding.extValue.setClickable(false);

                    if (inputType.equals("pH")) {
                        mBinding.extValue.setText(getValueFromArr(bufferType, bufferArr));
                    }
                }
                break;
            case R.id.quick:
                if (isChecked) {
                    mBinding.txtChange.setText("Please Enter the Calibration Value");
                    mBinding.extValue.setText("");
                    mBinding.extValue.setHint("00");
                    mBinding.extValue.setEnabled(true);
                    mBinding.extValue.setClickable(true);
                }
                break;
        }
    }

    private void saveCalibrationValue(String[] splitData) {
        CalibrationEntity entityUpdate = new CalibrationEntity(
                Integer.parseInt(splitData[3]), inputType,
                new SimpleDateFormat("yyyy.MM.dd | HH.mm.ss", Locale.getDefault()).format(new Date()),
                getCalibrationType().equals("0") ? getCalibrationType() + SPILT_CHAR + splitData[4] + SPILT_CHAR + splitData[5] :
                        getCalibrationType() + SPILT_CHAR + splitData[4] + SPILT_CHAR + splitData[5] // todo required confirmation
        );
        List<CalibrationEntity> entryListUpdate = new ArrayList<>();
        entryListUpdate.add(entityUpdate);
        updateToDb(entryListUpdate);
    }

    public void updateToDb(List<CalibrationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        CalibrationDao dao = db.calibrationDao();
        dao.insert(entryList.toArray(new CalibrationEntity[0]));
    }
}