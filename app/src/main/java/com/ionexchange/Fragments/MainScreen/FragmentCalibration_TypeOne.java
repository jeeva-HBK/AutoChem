package com.ionexchange.Fragments.MainScreen;

import static com.ionexchange.Fragments.MainScreen.FragmentSensorDetails.clickMainScreenBtn;
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
import android.app.WallpaperManager;
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
import com.ionexchange.Database.Dao.InputConfigurationDao;
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
    AlertDialog alertDialog;
    SensorDetailsCalibrationBinding mBinding;
    ApplicationClass mAppClass;
    WaterTreatmentDb db;
    CalibrationDao calibrationDao;
    KeepAliveCurrentValueDao keepAliveDao;
    String inputNumber, inputType, bufferType = "", tempValue;
    boolean tempBool = false; // onDataReceive will trigger twice, use boolean this for preventing method calling twice.
    CountDownTimer stabilizationTimer;
    private static final String TAG = "FragmentSensorCalib";

    ImageView iv;
    TextView leftValue;
    TextView rightValue;
    Button leftBtn;
    Button rightBtn;
    TextView leftHeading;
    TextView rightHeading;

    public FragmentCalibration_TypeOne(String inputNumber, String inputType, String bufferType) {
        this.inputNumber = inputNumber;
        this.inputType = getValueFromArr(inputType, inputTypeArr);
        this.bufferType = bufferType;
    }

    public FragmentCalibration_TypeOne() {
    }

    public FragmentCalibration_TypeOne(String inputNumber, String inputType) {
        this.inputType = getValueFromArr(inputType, inputTypeArr);
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
            } else if ("Contacting Conductivity".equals(inputType) || "Toroidal Conductivity".equals(inputType)) {
                mBinding.precise.performClick();
                mBinding.extValue.setText("");
                mBinding.extValue.setEnabled(false);
                mBinding.txtCurrentMode.setVisibility(View.GONE);
                mBinding.currentModeValue.setVisibility(View.GONE);
            } else if ("Temperature".equals(inputType)) {
                mBinding.precise.setVisibility(View.GONE);
                mBinding.quick.performClick();
                mBinding.currentModeValue.setVisibility(View.GONE);
                mBinding.txtCurrentMode.setVisibility(View.GONE);
            } else if ("Analog Input".equals(inputType)) {
                mBinding.precise.performClick();
                mBinding.extValue.setText("");
                mBinding.extValue.setEnabled(false);
                mBinding.txtCurrentMode.setVisibility(View.GONE);
                mBinding.currentModeValue.setVisibility(View.GONE);
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
                } else if ("Contacting Conductivity".equals(inputType) || "Toroidal Conductivity".equals(inputType)) {
                    startConductivityCalibration();
                } else if ("Temperature".equals(inputType)) {
                    startQuickCalibration();
                } else if ("Analog Input".equals(inputType)) {
                    startAnalogCalibration();
                }
            }
        });
    }

    private void startAnalogCalibration() {
        if (getCalibrationType().equals("0")) {
            startQuickCalibration();
        } else {
            startPreceiseCalibration("1" + SPILT_CHAR + "0");
        }
    }

    private void startConductivityCalibration() {
        if (getCalibrationType().equals("0")) {
            startQuickCalibration();
        } else {
            startPreceiseCalibration("1" + SPILT_CHAR + "0");
        }
    }

    /* Quick Calibration */
    private void startQuickCalibration() {
        if (!mBinding.extValue.getText().toString().equals("")) {
            tempValue = mBinding.extValue.getText().toString();
            tempBool = false;
            sendQuickCalibWritePacket();
        } else {
            mAppClass.showSnackBar(getContext(), "Calibration Value should not be empty !");
        }
    }

    private void sendQuickCalibWritePacket() { // common for all sensor
        mAppClass.sendPacket(new DataReceiveCallback() {
            @Override
            public void OnDataReceive(String data) {
                if (!tempBool) {
                    tempBool = true;
                    if (isValidPck(WRITE_PACKET, data)) {
                        checkStabilization("0");
                    }
                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_SENSORCALIB + SPILT_CHAR + inputNumber + SPILT_CHAR + getPosition(2, inputType, inputTypeArr) + SPILT_CHAR + getSeqNo() +
                SPILT_CHAR + "0" + SPILT_CHAR + "0" + SPILT_CHAR + mBinding.extValue.getText().toString());
    }

    private String getSeqNo() {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        if (inputType.equals("Temperature")) {
            return dao.getSeqNumber(Integer.parseInt(inputNumber)) + "";
        }
        return "1";
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
            if (bufferType.equals("0")) { // pH AUTO
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_calib_reading, null);
                dialogBuilder.setView(dialogView);
                alertDialog = dialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);

                TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
                TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
                Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
                Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

                mainText.setText(firstORsecond.equals("1") ? "\"Rinse the sensor and insert it in 7.01 buffer solution\""
                        : "\"Rinse the sensor and insert it in 4.01 or 10.00 buffer solution\"");
                cancel.setText("CANCEL");
                confirm.setText("CONFIRM");
                cancel.setOnClickListener(View -> {
                    tempBool = false;
                    alertDialog.dismiss();
                });
                confirm.setOnClickListener(View -> {
                    alertDialog.dismiss();
                    sendPreceiseCalibWritePacket(firstORsecond);
                });
                alertDialog.show();
            } else { // pH Manual
                getPhManualValues("First");
            }
        } else if (inputType.contains("Conductivity")) { // Conductivity
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_calib_reading, null);
            dialogBuilder.setView(dialogView);
            alertDialog = dialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);

            TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
            TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
            Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
            Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

            mainText.setText("\"Rinse the sensor and hold it in Air\"");
            cancel.setText("CANCEL");
            confirm.setText("CONFIRM");
            cancel.setOnClickListener(View -> {
                tempBool = false;
                alertDialog.dismiss();
            });
            confirm.setOnClickListener(View -> {
                alertDialog.dismiss();
                sendPreceiseCalibWritePacket(firstORsecond);
            });
            alertDialog.show();
        } else if (inputType.equals("Analog Input")) {
            showAnalogCalib(1);
        }
    }

    private void showAnalogCalib(int type) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_onedt, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        TextView title = dialogView.findViewById(R.id.onedt_mainTxt);
        EditText edt = dialogView.findViewById(R.id.onedt_edt);
        Button leftBtn = dialogView.findViewById(R.id.onedt_leftBtn);
        Button rightBtn = dialogView.findViewById(R.id.onedt_rightBtn);

        leftBtn.setText("CANCEL");
        rightBtn.setText("PROCEED");

        if (type == 1) {
            title.setText("\"Enter the value transmitter/sensor will be sending\"");
        } else if (type == 2) {
            title.setText("\"Enter second value the transmitter/sensor will be sending\"");
        }

        leftBtn.setOnClickListener(View -> {
            tempBool = false;
            alertDialog.dismiss();
        });

        rightBtn.setOnClickListener(View -> {
            if (!edt.getText().toString().equals("")) {
                alertDialog.dismiss();
                sendPreceiseCalibWritePacket(type + SPILT_CHAR + edt.getText().toString());
            } else {
                mAppClass.showSnackBar(getContext(), "Field should not be empty !");
            }
        });

        alertDialog.show();
    }

    private void getPhManualValues(String type) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_calib_twoedt, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);

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
            tempBool = false;
            alertDialog.dismiss();
        });

        rightBtn.setOnClickListener(View -> {
            if (leftEdt.getText().toString().equals("")) {
                mAppClass.showSnackBar(getContext(), "Temperature Value should not be empty");
                return;
            } else if (rightEdt.getText().toString().equals("")) {
                mAppClass.showSnackBar(getContext(), "Buffer Value should not be empty");
            } else {
                alertDialog.dismiss();
                showRinceDialog(type, leftEdt.getText().toString(), rightEdt.getText().toString());
            }

        });
        alertDialog.show();
    }

    private void showRinceDialog(String type, String leftEdt, String rightEdt) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        TextView title = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        Button leftBtn = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button rightBtn = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        leftBtn.setText("CANCEL");
        leftBtn.setOnClickListener(View -> {
            tempBool = false;
            alertDialog.dismiss();
        });

        title.setText("\"Rinse the sensor and insert it in " + type + " buffer\"");

        rightBtn.setText("CONFIRM");
        rightBtn.setOnClickListener(View -> {
            alertDialog.dismiss();
            sendPreceiseCalibWritePacket(type.equals("First") ?
                    "1" + SPILT_CHAR + leftEdt + SPILT_CHAR + rightEdt :
                    "2" + SPILT_CHAR + leftEdt + SPILT_CHAR + rightEdt);
        });
        alertDialog.show();
    }

    private void sendPreceiseCalibWritePacket(String firstORsecond) {
        tempBool = false;
        mAppClass.sendPacket(new DataReceiveCallback() {
            @Override
            public void OnDataReceive(String data) {
                if (isValidPck(WRITE_PACKET, data)) {
                    if (!tempBool) {
                        if (inputType.equals("pH")) {
                            if (bufferType.equals("0")) {
                                tempValue = firstORsecond.equals("1") ? "7.01" : "10.00";
                            } else {
                                tempValue = firstORsecond.split("\\$")[2];
                            }
                        } else if (inputType.equals("Contacting Conductivity")) {
                            tempValue = firstORsecond.equals("1") ? "0.00" : firstORsecond.split("\\$")[1];
                        } else if (inputType.equals("Analog Input")) {
                            tempValue = firstORsecond.split(SPILT_CHAR)[0];
                        }
                        /*String[] spiltData = spiltPacket(data);
                    if (spiltData[3].equals("1")) { // step 1
                        checkStabilization("1");
                    } else if (spiltData[3].equals("2")) { // step 2
                        checkStabilization("2");
                    }*/
                        checkStabilization(spiltPacket(data)[3]);
                        tempBool = true;
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
        AlertDialog stabDialog = dialogBuilder.create();
        stabDialog.setCanceledOnTouchOutside(false);

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
        rightBtn.setEnabled(false);

        rightBtn.setAlpha(0.5f);

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempBool = false;
                stabDialog.dismiss();
                stabilizationTimer.cancel();
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stabDialog.dismiss();
                proceedToNextStep(type);
            }
        });

        stabDialog.show();

        final int[] stabilizationCount = {0};
        final long[] tempInt = {1};
        stabilizationTimer = new CountDownTimer(30000, 1000) {
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
                    stabDialog.dismiss();
                    proceedToNextStep(type);
                }
            }

            public void onFinish() {
                rightBtn.setAlpha(1f);
                rightBtn.setClickable(true);
                rightBtn.setEnabled(true);
            }
        };
        stabilizationTimer.start();
    }

    private void proceedToNextStep(String type) {
        if (getCalibrationType().equals("0")) { // Quick Calibration procedure
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
                    tempBool = false;
                    if (type.equals("1") || type.equals("0")) {
                        getPhManualValues("Second");
                    } else if (type.equals("2")) {
                        sendCalibReadPacket("2");
                    }
                }
            } else if (inputType.equals("Contacting Conductivity") || inputType.equals("Toroidal Conductivity")) { // anyOtherSensor
                tempBool = false;
                if (type.equals("1")) {
                    showOneEdtDialog();
                } else {
                    sendCalibReadPacket("2");
                }
            } else if (inputType.equals("Analog Input")) {
                if (type.equals("1")) {
                    showAnalogCalib(2);
                } else {
                    sendCalibReadPacket("2");
                }
            }
        }
    }

    private void showOneEdtDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_onedt, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        TextView title = dialogView.findViewById(R.id.onedt_mainTxt);
        EditText edt = dialogView.findViewById(R.id.onedt_edt);
        Button leftBtn = dialogView.findViewById(R.id.onedt_leftBtn);
        Button rightBtn = dialogView.findViewById(R.id.onedt_rightBtn);

        title.setText("\"Enter the Standard conductivity buffer value\"");
        leftBtn.setText("CANCEL");
        rightBtn.setText("PROCEED");

        leftBtn.setOnClickListener(View -> {
            tempBool = false;
            alertDialog.dismiss();
        });

        rightBtn.setOnClickListener(View -> {
            if (!edt.getText().toString().equals("")) {
                alertDialog.dismiss();
                showMsgDialog(edt.getText().toString());
            } else {
                mAppClass.showSnackBar(getContext(), "Field should not be empty !");
            }
        });

        alertDialog.show();
    }

    private void showMsgDialog(String text) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        TextView title = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        Button leftBtn = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button rightBtn = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        leftBtn.setText("CANCEL");
        rightBtn.setText("PROCEED");

        title.setText("Insert the sensor in STANDARD buffer");
        leftBtn.setOnClickListener(View -> {
            tempBool = false;
            alertDialog.dismiss();
        });

        rightBtn.setOnClickListener(View -> {
            alertDialog.dismiss();
            sendPreceiseCalibWritePacket("2" + SPILT_CHAR + text);
        });

        alertDialog.show();
    }

    private void sendCalibReadPacket(String firstORsecond) {
        tempBool = false;
        mAppClass.sendPacket(new DataReceiveCallback() {
            @Override
            public void OnDataReceive(String data) {
                if (isValidPck(READ_PACKET, data)) {
                    if (!tempBool) {
                        tempBool = true;
                        showCalibResult(spiltPacket(data));
                    }
                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR +
                PCK_SENSORCALIB + SPILT_CHAR + inputNumber + SPILT_CHAR +
                getPosition(2, inputType, inputTypeArr) + SPILT_CHAR + getSeqNo() + SPILT_CHAR + "0" +
                (firstORsecond.equals("0") ? "" : SPILT_CHAR + firstORsecond));
    }

    private void showCalibResult(String[] splitData) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_result_two, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertResult = dialogBuilder.create();
        alertResult.setCanceledOnTouchOutside(false);

        iv = dialogView.findViewById(R.id.resultTwo_iv);
        leftValue = dialogView.findViewById(R.id.resulTwo_leftValue);
        rightValue = dialogView.findViewById(R.id.resulTwo_rightValue);
        leftBtn = dialogView.findViewById(R.id.resultTwo_leftBtn);
        rightBtn = dialogView.findViewById(R.id.resultTwo_rightBtn);
        leftHeading = dialogView.findViewById(R.id.resultTwo_leftHeading);
        rightHeading = dialogView.findViewById(R.id.resultTwo_rightHeading);

        leftBtn.setText("RETRY");
        rightBtn.setText("SAVE");

        leftBtn.setOnClickListener(View -> {
            tempBool = false;
            alertResult.dismiss();
            mBinding.startCalibrationBtn.performClick();
        });

        rightBtn.setOnClickListener(View -> {
            tempBool = false;
            alertResult.dismiss();
            saveCalibrationValue(splitData);
        });

        switch (inputType) {
            case "pH":
                if (getCalibrationType().equals("0")) {
                    showQuickCalibResult(splitData);
                } else {
                    showPreceiseCalibResult(splitData, iv, leftValue, rightValue, leftHeading, rightHeading, leftBtn, rightBtn);
                }
                break;

            case "ORP":
            case "Contacting Conductivity":
            case "Toroidal Conductivity":
            case "Temperature":
            case "Analog Input":
                showQuickCalibResult(splitData);
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

    private void showQuickCalibResult(String[] splitData) {
        String offSet = splitData[4], calibratedValue = splitData[5];
        double minRange = 0, maxRange = 0;
        switch (inputType) {
            case "pH":
                minRange = -140;
                maxRange = 140;
                break;

            case "ORP":
                minRange = -300;
                maxRange = 300;
                break;

            case "Contacting Conductivity":
                minRange = 0.5; // -100  toroidal = -10,000
                maxRange = 2.0; // 100   toroidal =  10,000
                break;

            case "Toroidal Conductivity":
                minRange = 0.5; // -100  toroidal = -10,000
                maxRange = 10; // 100   toroidal =  10,000
                break;

            case "Temperature":
                minRange = -10.0; // -2
                maxRange = 10.0; // 2
                break;

            case "Analog Input":
                minRange = -2;
                maxRange = 2;
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
        clickMainScreenBtn();
        mAppClass.showSnackBar(getContext(), "Calibration Completed");
    }

    public void updateToDb(List<CalibrationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        CalibrationDao dao = db.calibrationDao();
        dao.insert(entryList.toArray(new CalibrationEntity[0]));
    }
}