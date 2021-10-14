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
    AlertDialog alertZeroReadingPleaseWait, alertSlopeReadingPleaseWait;

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

                case R.id.diagnosticRv:
                    calibMode = "3";

                    break;
            }
        });
    }

    private void startSlopeCalibration() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_getvalue, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        EditText editText = dialogView.findViewById(R.id.dialogCalib3_edt);
        Button confirm = dialogView.findViewById(R.id.dialogCalib3_confirmBtn);
        Button cancel = dialogView.findViewById(R.id.dialogCalib3_cancelBtn);

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
                editText.setError("Field should not be empty !");
            }
        });

        alertDialog.show();
    }

    private void startZeroCalibration() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_zerocalib1, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        Button cancel = dialogView.findViewById(R.id.cancelZeroCalib1);
        Button confirm = dialogView.findViewById(R.id.confirmZeroCalib1);

        cancel.setOnClickListener(View -> {
            alertDialog.dismiss();
        });

        confirm.setOnClickListener(View -> {
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR +
                    WRITE_PACKET + SPILT_CHAR + PCK_SENSORCALIB + SPILT_CHAR + mBundle.getString("InputNo") + SPILT_CHAR + "07" + SPILT_CHAR + "1" + SPILT_CHAR +
                    getPosition(0, mBundle.getString("ModbusType"), modBusTypeArr) + SPILT_CHAR +
                    getPosition(0, mBundle.getString("TypeOfValue"), typeOfValueRead) + SPILT_CHAR + "1");
            alertDialog.dismiss();
        });
        alertDialog.show();
        /*   int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.50);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
        alertDialog.getWindow().setLayout(width, height);*/
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
                            sendZeroCalibPacket();
                            break;

                        case "2":
                            insertStandardSolution();
                            break;

                        case "3":

                            break;
                    }
                } else {
                    mAppClass.showSnackBar(getContext(), "Write Failed !");
                }
            } else if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    switch (calibMode) {
                        case "1":
                            alertZeroReadingPleaseWait.dismiss();
                            showZeroCalibResult(splitData[4]);
                            break;
                        case "2":
                            alertZeroReadingPleaseWait.dismiss();
                            showSlopeCalibResult(splitData[4]);
                            break;
                        case "3":

                            break;
                    }

                } else {
                    mAppClass.showSnackBar(getContext(), "Read Failed !");
                }
            }
        }
    }

    private void showSlopeCalibResult(String result) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        ImageView iv = dialogView.findViewById(R.id.dialog2CalibCenterImage);
        TextView mainText = dialogView.findViewById(R.id.dialogCalib2_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogCalib2_subText);
        Button cancelBtn = dialogView.findViewById(R.id.dialogCalib2_Cancelbtn);
        Button retryBtn = dialogView.findViewById(R.id.dialogCalib2_retryBtn);

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

            case "256":
                iv.setBackgroundResource(R.drawable.ic_failed);
                mainText.setText("Status - 256");
                subText.setText("Solution is too high");
                cancelBtn.setText("CONFIRM");
                retryBtn.setVisibility(View.VISIBLE);

                cancelBtn.setOnClickListener(View -> {
                    alertDialog.dismiss();
                });

                retryBtn.setOnClickListener(View -> {
                    sendZeroCalibPacket();
                });
                break;

            case "1024":
                iv.setBackgroundResource(R.drawable.ic_failed);
                mainText.setText("Status - 1024");
                subText.setText("Solution is too low");
                cancelBtn.setText("CONFIRM");
                retryBtn.setVisibility(View.VISIBLE);

                cancelBtn.setOnClickListener(View -> {
                    alertDialog.dismiss();
                });

                retryBtn.setOnClickListener(View -> {
                    sendZeroCalibPacket();
                });
                break;
        }
    }

    private void showZeroCalibResult(String result) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        ImageView iv = dialogView.findViewById(R.id.dialog2CalibCenterImage);
        TextView mainText = dialogView.findViewById(R.id.dialogCalib2_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogCalib2_subText);
        Button cancelBtn = dialogView.findViewById(R.id.dialogCalib2_Cancelbtn);
        Button retryBtn = dialogView.findViewById(R.id.dialogCalib2_retryBtn);

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
                    sendZeroCalibPacket();
                });
                break;

            case "4096":
                iv.setBackgroundResource(R.drawable.ic_failed);
                mainText.setText("Status - 2096");
                subText.setText("Distilled Water has flurorescene");
                cancelBtn.setText("CONFIRM");
                retryBtn.setVisibility(View.VISIBLE);

                cancelBtn.setOnClickListener(View -> {
                    alertDialog.dismiss();
                });

                retryBtn.setOnClickListener(View -> {
                    sendZeroCalibPacket();
                });
                break;
        }
    }

    private void sendZeroCalibPacket() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calib_reading, null);
        dialogBuilder.setView(dialogView);
        alertZeroReadingPleaseWait = dialogBuilder.create();

        Button cancel = dialogView.findViewById(R.id.dialogCalib2_Cancelbtn);
        final Boolean[] canSend = {true};
        cancel.setOnClickListener(View -> {
            canSend[0] = false;
            alertZeroReadingPleaseWait.dismiss();
        });

        alertZeroReadingPleaseWait.show();
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
        TextView textView = dialogView.findViewById(R.id.dialogCalib2_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogCalib2_subText);
        Button cancel = dialogView.findViewById(R.id.dialogCalib2_Cancelbtn);
        Button confirm = dialogView.findViewById(R.id.dialogCalib2_retryBtn);

        textView.setText("Insert the Sensor in Standard Solution");
        subText.setVisibility(View.INVISIBLE);

        confirm.setText("Confirm");
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
        TextView textView = dialogView.findViewById(R.id.dialogCalib2_mainText);
        TextView subText = dialogView.findViewById(R.id.dialogCalib2_subText);
        Button cancel = dialogView.findViewById(R.id.dialogCalib2_Cancelbtn);
        Button confirm = dialogView.findViewById(R.id.dialogCalib2_retryBtn);

        textView.setText("Insert the sensor in standard solution");
        subText.setVisibility(View.GONE);
        confirm.setVisibility(View.GONE);

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


}
