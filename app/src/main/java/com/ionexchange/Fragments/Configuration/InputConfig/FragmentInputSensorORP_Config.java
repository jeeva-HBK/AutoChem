package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsensorOrpBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.sensorSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorORP_Config extends Fragment implements DataReceiveCallback {
    FragmentInputsensorOrpBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    String sensorSequence;

    public FragmentInputSensorORP_Config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorORP_Config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_orp, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
        switch (userType) {

            case 1:
                mBinding.orpRow4Isc.setVisibility(View.GONE);
                mBinding.orpRow5Isc.setVisibility(View.GONE);

                // Only View Access for
                mBinding.orpInputNumberTilIsc.setEnabled(false);
                mBinding.orpInputLabelTilIsc.setEnabled(false);
                mBinding.orpSensorTypeTilIsc.setEnabled(false);
                mBinding.orpAlarmLowTilIsc.setEnabled(false);
                mBinding.orpAlarmHighTilIsc.setEnabled(false);
                mBinding.orpCalibrationAlarmRequiredTilIsc.setEnabled(false);
                mBinding.orpResetCalibrationTilIsc.setEnabled(false);
                break;

            case 2:
                mBinding.orpSmoothingFactorTilIsc.setEnabled(false);

                mBinding.orpSensorActTilIsc.setVisibility(View.GONE);
                mBinding.orpDeleteLayoutIsc.setVisibility(View.GONE);
                break;

            case 3:


                break;
        }

        initAdapter();
        sensorSequenceNumber();
        mBinding.orpSaveFabIsc.setOnClickListener(this::save);

        mBinding.orpDeleteFabIsc.setOnClickListener(this::delete);
        mBinding.orpBackArrowIsc.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
    }

    private void delete(View view) {
        sendData(2);
    }

    private void save(View view) {
        if (validation()) {
            sendData(sensorStatus);
        }
    }

    void sendData(int sensorStatus) {
        sensorSequenceNumber();
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.orpInputNumberEdtIsc) + SPILT_CHAR +
                getPosition(2, toString(mBinding.orpSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPosition(1, toString(mBinding.orpSensorActAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.orpInputLabelEdtIsc) + SPILT_CHAR +
                toString(3, mBinding.orpSmoothingFactorEdtIsc) + SPILT_CHAR +
                getPlusMinusValue(mBinding.orpAlarmLowTBtn, mBinding.orpAlarmLowEdtIsc, 4, mBinding.orpAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getPlusMinusValue(mBinding.orpAlarmHighTBtn, mBinding.orpAlarmHighEdtIsc, 4, mBinding.orpAlarmHighDeciIsc, 2) + SPILT_CHAR +
                toString(3, mBinding.orpCalibrationAlarmRequiredEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.orpResetCalibrationAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus);
    }

    void sensorSequenceNumber() {
        if (Integer.parseInt(inputNumber) > 13 && Integer.parseInt(inputNumber) < 21) {
            mBinding.orpSeqNumberTilIsc.setVisibility(View.VISIBLE);
            if (!mBinding.orpSeqNumberAtxtIsc.getText().toString().isEmpty()) {
                sensorSequence = getPosition(1, toString(mBinding.orpSeqNumberAtxtIsc), sensorSequenceNumber);
            }
        } else {
            mBinding.orpSeqNumberTilIsc.setVisibility(View.GONE);
            sensorSequence = "0";

        }
    }

    String getDecimalValue(TextInputEditText prefixEdittext, int prefixDigit, EditText suffixEdittext, int suffixDigit) {
        return toString(prefixDigit, prefixEdittext) + "." + toString(suffixDigit, suffixEdittext);
    }

    String getPlusMinusValue(ToggleButton toggleButton, TextInputEditText prefixEdt, int prefixDigit, EditText suffixEdt, int suffixDigit) {
        return (toggleButton.isChecked() ? "+" : "-") + toString(prefixDigit, prefixEdt) + "." + toString(suffixDigit, suffixEdt);
    }

    private String toStringSplit(int digits, int digitPoint, EditText editText) {
        if (editText.getText().toString().split("\\.").length == 1) {
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
    }

    private String getPosition(int digit, String string, String[] strArr) {
        String j = null;
        for (int i = 0; i < strArr.length; i++) {
            if (string.equals(strArr[i])) {
                j = String.valueOf(i);
            }
        }
        return mAppClass.formDigits(digit, j);
    }

    private String toString(int digits, EditText editText) {
        return mAppClass.formDigits(digits, editText.getText().toString());
    }

    private String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    private void initAdapter() {
        mBinding.orpSensorActAtxtIsc.setAdapter(getAdapter(sensorActivationArr));
        mBinding.orpSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr));
        mBinding.orpResetCalibrationAtxtIsc.setAdapter(getAdapter(resetCalibrationArr));
        mBinding.orpSeqNumberAtxtIsc.setAdapter(getAdapter(sensorSequenceNumber));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, inputNumber));
        } else {
            mBinding.orpInputNumberEdtIsc.setText(inputNumber);
            mBinding.orpSensorTypeAtxtIsc.setText(sensorName);
            mBinding.orpDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.orpSavTxtIsc.setText("ADD");
        }
    }

    @Override
    public void OnDataReceive(String data) {
        mActivity.dismissProgress();
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), "TimeOut");
        }
        if (data != null) {
            handleResponse(data.split("\\*")[1].split("\\$"));
        }
    }

    private void handleResponse(String[] data) {
        // Read - Res - {*1$ 04$ 0$ 02$ 01$ 1$ 0$ ORPSensor$ 010$ -1800.00$ +1900.25$ 300$ 1$ 1*}
        // Write - Res -
        if (data[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (data[0].equals(READ_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {

                    mBinding.orpInputNumberEdtIsc.setText(data[3]);
                    mBinding.orpSensorTypeAtxtIsc.setText(mBinding.orpSensorTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    // sequenceNumber 5
                    mBinding.orpSensorActAtxtIsc.setText(mBinding.orpSensorActAtxtIsc.getAdapter().getItem(Integer.parseInt(data[6])).toString());
                    mBinding.orpInputLabelEdtIsc.setText(data[7]);
                    mBinding.orpSmoothingFactorEdtIsc.setText(data[8]);

                    mBinding.orpAlarmLowTBtn.setChecked((data[9].substring(0, 1)).equals("+"));
                    mBinding.orpAlarmLowEdtIsc.setText(data[9].substring(1, 4));
                    mBinding.orpAlarmLowDeciIsc.setText(data[9].substring(6, 7));

                    mBinding.orpAlarmHighTBtn.setChecked(data[10].substring(0, 1).equals("+"));
                    mBinding.orpAlarmHighEdtIsc.setText(data[10].substring(1, 4));
                    mBinding.orpAlarmHighDeciIsc.setText(data[10].substring(6, 7));

                    mBinding.orpCalibrationAlarmRequiredEdtIsc.setText(data[11]);
                    mBinding.orpResetCalibrationAtxtIsc.setText(mBinding.orpResetCalibrationAtxtIsc.getAdapter().getItem(Integer.parseInt(data[12])).toString());
                    initAdapter();
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
                // *0$ 04$ 0$ *0}
            } else if (data[0].equals(WRITE_PACKET)) {
                if (data[3].equals(RES_SUCCESS)) {
                    orpEntity(Integer.parseInt(data[2]));
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (data[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), "Received Wrong Pack !");
        }

    }

    boolean validation() {
        if (isEmpty(mBinding.orpSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.orpInputNumberEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Input Label Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.orpCalibrationAlarmRequiredEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Calibration Alarm Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.orpAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm Low Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.orpAlarmHighEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm High Factor Cannot be Empty");
            return false;
        }
        return true;
    }

    private Boolean isEmpty(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        return false;
    }

    public void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void orpEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.orpInputNumberEdtIsc)), "0", 0, "0", "0", "0", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.orpInputNumberEdtIsc)),
                                mBinding.orpSensorTypeAtxtIsc.getText().toString(),
                                0, toString(0, mBinding.orpInputLabelEdtIsc),
                                getPlusMinusValue(mBinding.orpAlarmLowTBtn, mBinding.orpAlarmLowEdtIsc, 4, mBinding.orpAlarmLowDeciIsc, 2),
                                getPlusMinusValue(mBinding.orpAlarmHighTBtn, mBinding.orpAlarmHighEdtIsc, 4, mBinding.orpAlarmHighDeciIsc, 2), 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }

    }
}