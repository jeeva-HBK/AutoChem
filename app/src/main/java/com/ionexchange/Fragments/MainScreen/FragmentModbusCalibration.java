package com.ionexchange.Fragments.MainScreen;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentCalibrationModbusBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.ApplicationClass.getPosition;
import static com.ionexchange.Others.ApplicationClass.modBusTypeArr;
import static com.ionexchange.Others.ApplicationClass.typeOfValueRead;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_SENSORCALIB;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentModbusCalibration extends Fragment implements DataReceiveCallback {
    FragmentCalibrationModbusBinding mBinding;
    ApplicationClass mAppClass;
    Bundle mBundle;
    String calibMode = "0";
    AlertDialog alertReading, alertSlopeReadingPleaseWait;

    public FragmentModbusCalibration(Bundle bundle) {
        this.mBundle = bundle;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_calibration_modbus, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();

        if (!mBundle.isEmpty()) {
            mBinding.modbusCalibType.setText(mBundle.getString("ModbusType") + " | " + mBundle.getString("TypeOfValue"));
        }

        switch (mBundle.getString("ModbusType")) {
            case "CR-300 CU":
            case "CR300 CS":
                mBinding.diagCheckRb.setVisibility(View.GONE);
                mBinding.zeroCalibRb.setVisibility(View.GONE);
                break;

            case "ST-588":
                mBinding.diagCheckRb.setVisibility(View.GONE);
                break;
        }

        mBinding.modbusCalibStartBtn.setOnClickListener(View -> {
            switch (mBinding.modbusCalibRg.getCheckedRadioButtonId()) {
                case -1: // No Mode selected
                    calibMode = "0";
                    mAppClass.showSnackBar(getContext(), "Please Select a Calibration Mode");
                    break;

                case R.id.zeroCalibRb:
                    calibMode = "1";
                    startZeroCalibration();
                    break;

                case R.id.slopeCalibRb:
                    calibMode = "2";
                    startSlopeCalibration();
                    break;

                case R.id.diagCheckRb:
                    calibMode = "3";
                    startDiagnosticsCheck();
                    break;
            }
        });
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
                    switch (calibMode) {
                        case "1":
                            readZeroCalibPacket();
                            break;

                        case "2":
                            insertStandardSolution();
                            break;

                        case "3":
                            readDiagnosticCheckPacket("1021");
                            break;
                    }
                } else {
                    mAppClass.showSnackBar(getContext(), "Write Failed !");
                }
            } else if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    switch (calibMode) {
                        case "1":
                            alertReading.dismiss();
                            showZeroCalibResult(splitData[4]);
                            break;
                        case "2":
                            alertReading.dismiss();
                            showSlopeCalibResult(splitData[4]);
                            break;
                        case "3":
                            alertReading.dismiss();
                            showDiagCheckResult(splitData[4]);
                            break;
                    }

                } else {
                    mAppClass.showSnackBar(getContext(), "Read Failed !");
                }
            }
        }
    }

    /* Calibration Procedure */
    /*Zero Calibration*/
    private void startZeroCalibration() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
        Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        mainText.setText("\"Insert The Sensor In Distilled Water\"");
        subText.setVisibility(View.INVISIBLE);
        cancel.setText("Cancel");
        confirm.setText("Confirm");

        cancel.setOnClickListener(View -> {
            alertDialog.dismiss();
        });

        confirm.setOnClickListener(View -> {
            alertDialog.dismiss();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR +
                    WRITE_PACKET + SPILT_CHAR + PCK_SENSORCALIB + SPILT_CHAR + mBundle.getString("InputNo") + SPILT_CHAR + "07" + SPILT_CHAR + "1" + SPILT_CHAR +
                    getPosition(0, mBundle.getString("ModbusType"), modBusTypeArr) + SPILT_CHAR +
                    getPosition(0, mBundle.getString("TypeOfValue"), typeOfValueRead) + SPILT_CHAR + "1");
        });
        alertDialog.show();
    }

    /*Slope Calibration*/
    private void startSlopeCalibration() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_with_onedt, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        EditText editText = dialogView.findViewById(R.id.onedt_edt);
        Button confirm = dialogView.findViewById(R.id.onedt_rightBtn);
        Button cancel = dialogView.findViewById(R.id.onedt_leftBtn);

        cancel.setOnClickListener(View -> {
            alertDialog.dismiss();
        });

        confirm.setOnClickListener(View -> {
            if (!editText.getText().toString().trim().equals("")) {
                mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR +
                        WRITE_PACKET + SPILT_CHAR + PCK_SENSORCALIB + SPILT_CHAR + mBundle.getString("InputNo") + SPILT_CHAR + "07" + SPILT_CHAR + "1" + SPILT_CHAR +
                        getPosition(0, mBundle.getString("ModbusType"), modBusTypeArr) + SPILT_CHAR +
                        getPosition(0, mBundle.getString("TypeOfValue"), typeOfValueRead) + SPILT_CHAR + "2" + SPILT_CHAR + editText.getText().toString().trim());
                alertDialog.dismiss();
            } else {
                mAppClass.showSnackBar(getContext(), "Field should not be empty !");
            }
        });
        alertDialog.show();
    }

    /*Diagnostic Check*/
    private void startDiagnosticsCheck() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
        Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        mainText.setText("\"Insert The Sensor In Distilled Water\"");
        subText.setText("");
        cancel.setText("Cancel");
        confirm.setText("Confirm");

        cancel.setOnClickListener(View -> {
            alertDialog.dismiss();
        });

        confirm.setOnClickListener(View -> {
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR +
                    WRITE_PACKET + SPILT_CHAR + PCK_SENSORCALIB + SPILT_CHAR + mBundle.getString("InputNo") + SPILT_CHAR + "07" + SPILT_CHAR + "1" + SPILT_CHAR +
                    getPosition(0, mBundle.getString("ModbusType"), modBusTypeArr) + SPILT_CHAR +
                    getPosition(0, mBundle.getString("TypeOfValue"), typeOfValueRead) + SPILT_CHAR + "3"
            );
        });

        alertDialog.show();
    }

    /* Reading Register Values */
    private void readZeroCalibPacket() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        alertReading = dialogBuilder.create();

        TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
        Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        mainText.setText("\"Reading Calibration Status (1031) Register From Modbus\"");
        subText.setText("");
        confirm.setVisibility(View.GONE);

        final Boolean[] canSend = {true};
        cancel.setOnClickListener(View -> {
            canSend[0] = false;
            alertReading.dismiss();
        });

        alertReading.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (canSend[0]) {
                    mAppClass.sendPacket(FragmentModbusCalibration.this,
                            DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_SENSORCALIB + SPILT_CHAR +
                                    mBundle.getString("InputNo") + SPILT_CHAR + "07" + SPILT_CHAR + "1" + SPILT_CHAR + getPosition(0, mBundle.getString("ModbusType"), modBusTypeArr) + SPILT_CHAR +
                                    getPosition(0, mBundle.getString("TypeOfValue"), typeOfValueRead) + SPILT_CHAR + calibMode);
                }
            }
        }, 5000);
    }

    private void insertStandardSolution() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
        Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        mainText.setText("\"Insert the Sensor in Standard Solution\"");
        subText.setVisibility(View.INVISIBLE);
        confirm.setText("Confirm");
        cancel.setText("Cancel");

        final Boolean[] canSend = {true};
        cancel.setOnClickListener(View -> {
            canSend[0] = false;
            alertDialog.dismiss();
        });
        confirm.setOnClickListener(View -> {
            sendSlopeCalibrationPacket();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void sendSlopeCalibrationPacket() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        alertSlopeReadingPleaseWait = dialogBuilder.create();

        TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
        Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        mainText.setText("\"Reading Calibration Status (1031) Register From Modbus\"");
        subText.setVisibility(View.INVISIBLE);
        confirm.setVisibility(View.GONE);
        cancel.setText("Cancel");

        final boolean[] canSend = {true};
        cancel.setOnClickListener(View -> {
            canSend[0] = false;
            alertSlopeReadingPleaseWait.dismiss();
        });

        alertSlopeReadingPleaseWait.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (canSend[0]) {
                    mAppClass.sendPacket(FragmentModbusCalibration.this,
                            DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_SENSORCALIB + SPILT_CHAR +
                                    mBundle.getString("InputNo") + SPILT_CHAR + "07" + SPILT_CHAR + "1" + SPILT_CHAR + getPosition(0, mBundle.getString("ModbusType"), modBusTypeArr) + SPILT_CHAR +
                                    getPosition(0, mBundle.getString("TypeOfValue"), typeOfValueRead) + SPILT_CHAR + calibMode);
                }
            }
        }, 5000);

    }

    private void readDiagnosticCheckPacket(String register) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        alertReading = dialogBuilder.create();

        TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
        Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        mainText.setText("\"Reading Calibration Status(" + register + ") Register From Modbus\"");
        subText.setText("");
        confirm.setVisibility(View.GONE);

        final Boolean[] canSend = {true};
        cancel.setOnClickListener(View -> {
            canSend[0] = false;
            alertReading.dismiss();
        });

        alertReading.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (canSend[0]) {
                    mAppClass.sendPacket(FragmentModbusCalibration.this,
                            DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_SENSORCALIB + SPILT_CHAR +
                                    mBundle.getString("InputNo") + SPILT_CHAR + "07" + SPILT_CHAR + "3" + SPILT_CHAR + getPosition(0, mBundle.getString("ModbusType"), modBusTypeArr) + SPILT_CHAR +
                                    getPosition(0, mBundle.getString("TypeOfValue"), typeOfValueRead) + SPILT_CHAR + calibMode);
                }
            }
        }, 5000);

    }

    /* Calibration Results */
    private void showZeroCalibResult(String result) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        ImageView iv = dialogView.findViewById(R.id.dialogType1CalibIv);
        TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
        Button cancelBtn = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button retryBtn = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        switch (result) {
            case "0":
                iv.setBackgroundResource(R.drawable.ic_success);
                mainText.setText("Status - 0");
                subText.setText("OK");
                cancelBtn.setText("CONFIRM");
                retryBtn.setVisibility(View.GONE);
                cancelBtn.setOnClickListener(View -> {
                    alertDialog.dismiss();
                });
                break;

            case "2048":
                iv.setBackgroundResource(R.drawable.ic_failed);
                mainText.setText("Status - 2048");
                subText.setText("Probe is fouled, cleaning required");
                cancelBtn.setText("CONFIRM");
                retryBtn.setVisibility(View.VISIBLE);

                cancelBtn.setOnClickListener(View -> {
                    alertDialog.dismiss();
                });

                retryBtn.setOnClickListener(View -> {
                    alertDialog.dismiss();
                    readZeroCalibPacket();
                });
                break;

            case "4096":
                iv.setBackgroundResource(R.drawable.ic_failed);
                mainText.setText("Status - 2096");
                subText.setText("Distilled Water has fluorescence");
                cancelBtn.setText("CONFIRM");
                retryBtn.setVisibility(View.VISIBLE);

                cancelBtn.setOnClickListener(View -> {
                    alertDialog.dismiss();
                });

                retryBtn.setOnClickListener(View -> {
                    alertDialog.dismiss();
                    readZeroCalibPacket();
                });
                break;
        }
    }

    private void showSlopeCalibResult(String result) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        ImageView iv = dialogView.findViewById(R.id.dialogType1CalibIv);
        TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
        Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        switch (result) {
            case "0":
                iv.setBackgroundResource(R.drawable.ic_success);
                mainText.setText("Status - 0");
                subText.setText("OK");
                cancel.setText("CONFIRM");
                confirm.setVisibility(View.GONE);
                cancel.setOnClickListener(View -> {
                    alertDialog.dismiss();
                });
                break;

            case "256":
                iv.setBackgroundResource(R.drawable.ic_failed);
                mainText.setText("Status - 256");
                subText.setText("Solution is too high");
                cancel.setText("CONFIRM");
                confirm.setVisibility(View.VISIBLE);

                cancel.setOnClickListener(View -> {
                    alertDialog.dismiss();
                });

                confirm.setOnClickListener(View -> {
                    alertDialog.dismiss();
                    readZeroCalibPacket();
                });
                break;

            case "1024":
                iv.setBackgroundResource(R.drawable.ic_failed);
                mainText.setText("Status - 1024");
                subText.setText("Solution is too low");
                cancel.setText("CONFIRM");
                confirm.setVisibility(View.VISIBLE);

                cancel.setOnClickListener(View -> {
                    alertDialog.dismiss();
                });

                confirm.setOnClickListener(View -> {
                    alertDialog.dismiss();
                    readZeroCalibPacket();
                });
                break;
        }
    }

    private void showDiagCheckResult(String spiltData) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        ImageView iv = dialogView.findViewById(R.id.dialogType1CalibIv);
        TextView mainText = dialogView.findViewById(R.id.dialogType1Calib_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogType1Calib_subText);
        Button cancel = dialogView.findViewById(R.id.dialogType1Calib_leftBtn);
        Button confirm = dialogView.findViewById(R.id.dialogType1Calib_rightBtn);

        if (Integer.parseInt(spiltData) < 600) {
            iv.setBackgroundResource(R.drawable.ic_verify);
            mainText.setText("\"Inset the sensor in cooling water\"");
            subText.setText("");
            confirm.setText("Retry");
            cancel.setText("CANCEL");
            cancel.setOnClickListener(View -> {
                alertDialog.dismiss();
            });
            confirm.setOnClickListener(View -> {
                alertDialog.dismiss();
                readDiagnosticCheckPacket("1024");
            });
            alertDialog.show();

        } else if (Integer.parseInt(spiltData) > 600) {
            iv.setBackgroundResource(R.drawable.ic_verify);
            mainText.setText("No Cleaning Required");
            subText.setText("");
            confirm.setText("CONFIRM");
            cancel.setVisibility(View.GONE);
            confirm.setOnClickListener(View -> {
                alertDialog.dismiss();
            });
            alertDialog.show();

        } else if (Integer.parseInt(spiltData) > 3000) {
            iv.setBackgroundResource(R.drawable.ic_verify);
            mainText.setText("Sensor Cleaning Required");
            subText.setText("");
            confirm.setText("CONFIRM");
            cancel.setVisibility(View.GONE);
            confirm.setOnClickListener(View -> {
                alertDialog.dismiss();
            });
            alertDialog.show();

        } else if (Integer.parseInt(spiltData) < 3000) {
            iv.setBackgroundResource(R.drawable.ic_verify);
            mainText.setText("No Cleaning Required");
            subText.setText("");
            confirm.setText("CONFIRM");
            cancel.setVisibility(View.GONE);
            confirm.setOnClickListener(View -> {
                alertDialog.dismiss();
            });
            alertDialog.show();
        }
    }
}
