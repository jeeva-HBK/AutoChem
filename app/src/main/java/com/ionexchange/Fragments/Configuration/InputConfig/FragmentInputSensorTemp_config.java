package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.util.Log;
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
import com.ionexchange.databinding.FragmentInputsensorTempBinding;

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

public class FragmentInputSensorTemp_config extends Fragment implements DataReceiveCallback {
    private static final String TAG = "FragmentInputSensorTemp";
    FragmentInputsensorTempBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    String sensorSequence;

    public FragmentInputSensorTemp_config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorTemp_config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_temp, container, false);
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
                mBinding.tempInputLabelTilIsc.setEnabled(false);
                mBinding.tempDefaultTemperatureValueTilIsc.setEnabled(false);
                mBinding.tempTempDeciIsc.setEnabled(false);
                mBinding.tempTempValueTBtn.setEnabled(false);

                mBinding.tempLowAlarmEdtIsc.setEnabled(false);
                mBinding.tempLowAlarmDeciIsc.setEnabled(false);
                mBinding.tempLowAlarmTBtn.setEnabled(false);

                mBinding.tempHighAlarmEdtIsc.setEnabled(false);
                mBinding.tempHighAlarmDeciIsc.setEnabled(false);
                mBinding.tempHighAlarmTBtn.setEnabled(false);

                mBinding.tempCalibRequiredAlarmEdtIsc.setEnabled(false);
                mBinding.tempResetCalibAtxtIsc.setEnabled(false);

                mBinding.tempSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.tempSmoothingFactorTilIsc.setVisibility(View.GONE);

                mBinding.tempRow5Isc.setVisibility(View.GONE);
                break;

            case 2:
                mBinding.tempDefaultTemperatureValueTilIsc.setEnabled(false);
                mBinding.tempTempDeciIsc.setEnabled(false);
                mBinding.tempTempValueTBtn.setEnabled(false);

                mBinding.tempSmoothingFactorTilIsc.setEnabled(false);

                mBinding.tempSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.tempDeleteLayoutIsc.setVisibility(View.GONE);
                break;
        }

        initAdapter();
        sensorSequenceNumber();
        mBinding.tempSaveFabIsc.setOnClickListener(this::save);
        mBinding.tempDeleteFabSic.setOnClickListener(this::delete);
        mBinding.tempBackArrowIsc.setOnClickListener(v -> {
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

    void sensorSequenceNumber() {
        if (Integer.parseInt(inputNumber) > 13 && Integer.parseInt(inputNumber) < 21) {
            mBinding.tempSeqNumberTilIsc.setVisibility(View.VISIBLE);
            if (!mBinding.temSeqNumberAtxtIsc.getText().toString().isEmpty()) {
                sensorSequence = getPosition(1, toString(mBinding.temSeqNumberAtxtIsc), sensorSequenceNumber);
            }
        } else {
            mBinding.tempSeqNumberTilIsc.setVisibility(View.GONE);
            sensorSequence = "0";

        }
    }

    void sendData(int sensorStatus) {
        sensorSequenceNumber();
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.tempInputNumberEdtIsc) + SPILT_CHAR +
                getPosition(2, toString(mBinding.temSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPosition(0, toString(mBinding.tempSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.tempInputLabelEdtIsc) + SPILT_CHAR +
                getPlusMinusValue(mBinding.tempTempValueTBtn, mBinding.tempTemperatureEdtIsc, 3, mBinding.tempTempDeciIsc, 2) + SPILT_CHAR +
                toString(3, mBinding.tempSmoothingFactorEdtIsc) + SPILT_CHAR +
                getPlusMinusValue(mBinding.tempLowAlarmTBtn, mBinding.tempLowAlarmEdtIsc, 3, mBinding.tempLowAlarmDeciIsc, 2) + SPILT_CHAR +
                getPlusMinusValue(mBinding.tempHighAlarmTBtn, mBinding.tempHighAlarmEdtIsc, 3, mBinding.tempHighAlarmDeciIsc, 2) + SPILT_CHAR +
                toString(3, mBinding.tempCalibRequiredAlarmEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.tempResetCalibAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus);
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

    private void initAdapter() {
        mBinding.temSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr));
        mBinding.tempSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr));
        mBinding.tempResetCalibAtxtIsc.setAdapter(getAdapter(resetCalibrationArr));
        mBinding.temSeqNumberAtxtIsc.setAdapter(getAdapter(sensorSequenceNumber));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
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

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, inputNumber));
        } else {
            mBinding.tempInputNumberEdtIsc.setText(inputNumber);
            mBinding.temSensorTypeAtxtIsc.setText(sensorName);
            mBinding.tempDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.tempSaveTxtIsc.setText("ADD");
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

    private void handleResponse(String[] splitData) {
        // Read RES - {1# 04# 0# |05# 02# 1# TEMPPH# 33# 100# 120000# 240000# 320# 1}
        // Write Res -
        if (splitData[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.tempInputNumberEdtIsc.setText(splitData[3]);
                    mBinding.temSensorTypeAtxtIsc.setText(mBinding.temSensorTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                    mBinding.temSeqNumberAtxtIsc.setText(mBinding.temSeqNumberAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[5])).toString());
                    mBinding.tempSensorActivationAtxtIsc.setText(mBinding.tempSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[6])).toString());
                    mBinding.tempInputLabelEdtIsc.setText(splitData[7]);
                    mBinding.tempTempValueTBtn.setChecked((splitData[8].substring(0, 1)).equals("+"));
                    mBinding.tempTemperatureEdtIsc.setText(splitData[8].substring(1, 4));
                    mBinding.tempTempDeciIsc.setText(splitData[8].substring(5, 6));

                    mBinding.tempSmoothingFactorEdtIsc.setText(splitData[9]);

                    mBinding.tempLowAlarmTBtn.setChecked(splitData[10].substring(0, 1).equals("+"));
                    mBinding.tempLowAlarmEdtIsc.setText(splitData[10].substring(1, 4));
                    mBinding.tempLowAlarmDeciIsc.setText(splitData[10].substring(5, 6));

                    mBinding.tempHighAlarmTBtn.setChecked(splitData[11].substring(0, 1).equals("+"));
                    mBinding.tempHighAlarmEdtIsc.setText(splitData[11].substring(1, 4));
                    mBinding.tempHighAlarmDeciIsc.setText(splitData[11].substring(5, 6));

                    mBinding.tempCalibRequiredAlarmEdtIsc.setText(splitData[12]);
                    mBinding.tempResetCalibAtxtIsc.setText(mBinding.tempResetCalibAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[13])).toString());

                    initAdapter();
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
            } else if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[3].equals(RES_SUCCESS)) {
                    temperatureEntity(Integer.parseInt(splitData[2]));
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }
        } else {
            Log.e(TAG, "handleResponse: WRONG_PACK");
        }
    }

    boolean validation() {
        if (isEmpty(mBinding.tempSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Input Label Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempCalibRequiredAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Calibration Alarm Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempTemperatureEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Temperature Value Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempLowAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm Low Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempHighAlarmEdtIsc)) {
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

    public void temperatureEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.tempInputNumberEdtIsc)), "0", 0, "0", "0", "0", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                break;
            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.tempInputNumberEdtIsc)),
                                mBinding.temSensorTypeAtxtIsc.getText().toString(),
                                0, toString(0, mBinding.tempInputLabelEdtIsc),
                                getPlusMinusValue(mBinding.tempLowAlarmTBtn, mBinding.tempLowAlarmEdtIsc, 3, mBinding.tempLowAlarmDeciIsc, 2),
                                getPlusMinusValue(mBinding.tempHighAlarmTBtn, mBinding.tempHighAlarmEdtIsc, 3, mBinding.tempHighAlarmDeciIsc, 2), 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }

    }
}
