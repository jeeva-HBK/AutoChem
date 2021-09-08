package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.ionexchange.databinding.FragmentInputsensorCondBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.TemperatureCompensationType;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.sensorSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.tempLinkedArr;
import static com.ionexchange.Others.ApplicationClass.unitArr;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorConductivity_Config extends Fragment implements DataReceiveCallback {
    private static final String TAG = "FragmentInputSensorCond";
    FragmentInputsensorCondBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    int sensorStatus;
    String inputNumber;
    String sensorName;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    int userManagement;
    String sensorSequence;

    public FragmentInputSensorConductivity_Config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorConductivity_Config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_cond, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
        userManagement = 3;
        userManagement();
        initAdapters();
        sensorSequenceNumber();
        mBinding.conSaveFabIsc.setOnClickListener(this::save);
        mBinding.conDeleteFabIsc.setOnClickListener(this::delete);

        mBinding.setCompType("0");
        mBinding.conCompensationAtxtIsc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                mBinding.setCompType(String.valueOf(pos));
            }
        });

        mBinding.conBackArrowIsc.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
    }

    private void sensorSequenceNumber() {
        if (Integer.parseInt(inputNumber) > 13 && Integer.parseInt(inputNumber) < 21) {
            mBinding.conSeqNumberTilIsc.setVisibility(View.VISIBLE);
            if (!mBinding.conSeqNumberAtxtIsc.getText().toString().isEmpty()) {
                sensorSequence = getPosition(1, toString(mBinding.conSeqNumberAtxtIsc), sensorSequenceNumber);
            }
        } else {
            mBinding.conSeqNumberTilIsc.setVisibility(View.GONE);
            sensorSequence = "0";
        }
    }

    private void delete(View view) {
        if (validation()) {
            if (getPosition(1, toString(mBinding.conCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
                sendDataLinearTemperature(2);
            } else {
                sendStandardNaClTemperature(2);
            }
        }
    }

    private void save(View view) {
        if (validation()) {
            if (getPosition(1, toString(mBinding.conCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
                sendDataLinearTemperature(sensorStatus);
            } else {
                sendStandardNaClTemperature(sensorStatus);
            }
        }
    }

    void sendDataLinearTemperature(int sensorStatus) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.conInputNumberEdtIsc) + SPILT_CHAR +
                getPosition(2, toString(mBinding.conSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPosition(0, toString(mBinding.conSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.conInputLabelEdtIsc) + SPILT_CHAR +
                getPosition(0, toString(mBinding.conTempLinkedAtxtIsc), tempLinkedArr) + SPILT_CHAR +
                getPlusMinusValue(mBinding.conDefaultTempValueTBtn, mBinding.conDefaultTemperatureEdtIsc, 3, mBinding.conDefaultTempDeciIsc, 2) + SPILT_CHAR +
                getPosition(0, toString(mBinding.conUnitOfMeasureAxtIsc), unitArr) + SPILT_CHAR +
                getDecimalValue(mBinding.conCellConstantEdtIsc, 2, mBinding.conCellConstantDeciIsc, 2) + SPILT_CHAR +
                getPosition(0, toString(mBinding.conCompensationAtxtIsc), TemperatureCompensationType) + SPILT_CHAR +
                getDecimalValue(mBinding.conCompFactorEdtIsc, 2, mBinding.conCompFactorDeciIsc, 2) + SPILT_CHAR +
                toString(3, mBinding.conSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.conAlarmLowEdtIsc, 6, mBinding.conAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.conAlarmhighEdtIsc, 6, mBinding.conHighAlarmDeciIsc, 2) + SPILT_CHAR +
                toString(3, mBinding.conCalibRequiredAlarmEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.conResetCalibAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus
        );
    }

    String getDecimalValue(TextInputEditText prefixEdittext, int prefixDigit, EditText suffixEdittext, int suffixDigit) {
        return toString(prefixDigit, prefixEdittext) + "." + toString(suffixDigit, suffixEdittext);
    }

    String getPlusMinusValue(ToggleButton toggleButton, TextInputEditText prefixEdt, int prefixDigit, EditText suffixEdt, int suffixDigit) {
        return (toggleButton.isChecked() ? "+" : "-") + toString(prefixDigit, prefixEdt) + "." + toString(suffixDigit, suffixEdt);
    }

    void sendStandardNaClTemperature(int sensorStatus) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.conInputNumberEdtIsc) + SPILT_CHAR +
                getPosition(2, toString(mBinding.conSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPosition(2, toString(mBinding.conSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.conInputLabelEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.conTempLinkedAtxtIsc), tempLinkedArr) + SPILT_CHAR +
                getPlusMinusValue(mBinding.conDefaultTempValueTBtn, mBinding.conDefaultTemperatureEdtIsc, 3, mBinding.conDefaultTempDeciIsc, 2) + SPILT_CHAR +
                getPosition(0, toString(mBinding.conUnitOfMeasureAxtIsc), unitArr) + SPILT_CHAR +
                getDecimalValue(mBinding.conCellConstantEdtIsc, 2, mBinding.conCellConstantDeciIsc, 2) + SPILT_CHAR +
                getPosition(0, toString(mBinding.conCompensationAtxtIsc), TemperatureCompensationType) + SPILT_CHAR +
                toString(3, mBinding.conSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.conAlarmLowEdtIsc, 6, mBinding.conAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.conAlarmhighEdtIsc, 6, mBinding.conHighAlarmDeciIsc, 2) + SPILT_CHAR +
                toString(3, mBinding.conCalibRequiredAlarmEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.conResetCalibAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus
        );
    }

    private String toStringSplit(int digits, int digitPoint, EditText editText) {
        if (editText.getText().toString().split("\\.").length == 1) {
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
    }

    boolean validation() {
        if (isEmpty(mBinding.conInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Input Label Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.conDefaultTemperatureEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Temperature Value Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.conCellConstantEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Cell Constant Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.conCompFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Temperature Compensation Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.conSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.conCalibRequiredAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Calibration Required Alarm Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.conAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm Low Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.conAlarmhighEdtIsc)) {
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

    private void initAdapters() {
        mBinding.conSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr));
        mBinding.conSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr));
        mBinding.conTempLinkedAtxtIsc.setAdapter(getAdapter(tempLinkedArr));
        mBinding.conUnitOfMeasureAxtIsc.setAdapter(getAdapter(unitArr));
        mBinding.conResetCalibAtxtIsc.setAdapter(getAdapter(resetCalibrationArr));
        mBinding.conCompensationAtxtIsc.setAdapter(getAdapter(TemperatureCompensationType));
        mBinding.conSeqNumberAtxtIsc.setAdapter(getAdapter(sensorSequenceNumber));
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
            mBinding.conInputNumberEdtIsc.setText(inputNumber);
            mBinding.conSensorTypeAtxtIsc.setText(sensorName);
            mBinding.conDeleteLayoutIsc.setVisibility(View.INVISIBLE);
            mBinding.conSaveTxtIsc.setText("ADD");
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

    private void handleResponse(String[] spiltData) {
        if (spiltData[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (spiltData[0].equals(READ_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mBinding.conInputNumberEdtIsc.setText(spiltData[3]);
                    mBinding.conSensorTypeAtxtIsc.setText(mBinding.conSensorTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[4])).toString());
                    mBinding.conSeqNumberAtxtIsc.setText(mBinding.conSeqNumberAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[5])).toString());
                    mBinding.conSensorActivationAtxtIsc.setText(mBinding.conSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[6])).toString());
                    mBinding.conInputLabelEdtIsc.setText(spiltData[7]);
                    mBinding.conTempLinkedAtxtIsc.setText(mBinding.conTempLinkedAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[8])).toString());

                    mBinding.conDefaultTempValueTBtn.setChecked((spiltData[9].substring(0, 1).equals("+")));
                    mBinding.conDefaultTemperatureEdtIsc.setText(spiltData[9].substring(1, 4));
                    mBinding.conDefaultTempDeciIsc.setText(spiltData[9].substring(5, 7));

                    mBinding.conUnitOfMeasureAxtIsc.setText(mBinding.conUnitOfMeasureAxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[10])).toString());
                    mBinding.conCellConstantEdtIsc.setText(spiltData[11].substring(0, 2));
                    mBinding.conCellConstantDeciIsc.setText(spiltData[11].substring(3, 5));
                    mBinding.conCompensationAtxtIsc.setText(mBinding.conCompensationAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[12])).toString());
                    mBinding.conCompFactorEdtIsc.setEnabled(spiltData[12].equals("0"));
                    mBinding.conCompFactorDeciIsc.setEnabled(spiltData[12].equals("0"));
                    if (spiltData[12].equals("0")) {
                        mBinding.conCompFactorEdtIsc.setText(spiltData[13].substring(0, 2));
                        mBinding.conCompFactorDeciIsc.setText(spiltData[13].substring(3, 5));
                        mBinding.conSmoothingFactorEdtIsc.setText(spiltData[14]);

                        mBinding.conAlarmLowEdtIsc.setText(spiltData[15].substring(0, 6));
                        mBinding.conAlarmLowDeciIsc.setText(spiltData[15].substring(7, 9));

                        mBinding.conAlarmhighEdtIsc.setText(spiltData[16].subSequence(0, 6));
                        mBinding.conHighAlarmDeciIsc.setText(spiltData[16].subSequence(7, 9));

                        mBinding.conCalibRequiredAlarmEdtIsc.setText(spiltData[17]);
                        mBinding.conResetCalibAtxtIsc.setText(mBinding.conResetCalibAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[18])).toString());
                    } else {
                        mBinding.conSmoothingFactorEdtIsc.setText(spiltData[13]);

                        mBinding.conAlarmLowEdtIsc.setText(spiltData[14].substring(0, 6));
                        mBinding.conAlarmLowDeciIsc.setText(spiltData[14].substring(7, 9));

                        mBinding.conAlarmhighEdtIsc.setText(spiltData[15].subSequence(0, 6));
                        mBinding.conHighAlarmDeciIsc.setText(spiltData[15].subSequence(7, 9));

                        mBinding.conCalibRequiredAlarmEdtIsc.setText(spiltData[16]);
                        mBinding.conResetCalibAtxtIsc.setText(mBinding.conResetCalibAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[17])).toString());
                    }

                    initAdapters();
                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
                //   {*0$ 04$ 1$ 0*}
            } else if (spiltData[0].equals(WRITE_PACKET)) {
                if (spiltData[3].equals(RES_SUCCESS)) {
                    conductivityEntity(Integer.parseInt(spiltData[2]));
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (spiltData[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }
        } else {
            Log.e(TAG, "handleResponse: Wrong Packet");
        }
    }


    public void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void conductivityEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.conInputNumberEdtIsc)), "0", 0, "0", "0", "0", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.conInputNumberEdtIsc)),
                                mBinding.conSensorTypeAtxtIsc.getText().toString(),
                                0, toString(0, mBinding.conInputLabelEdtIsc),
                                getDecimalValue(mBinding.conAlarmLowEdtIsc, 6, mBinding.conAlarmLowDeciIsc, 2),
                                getDecimalValue(mBinding.conAlarmhighEdtIsc, 6, mBinding.conHighAlarmDeciIsc, 2), 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }
    }

    void userManagement() {
        switch (userManagement) {
            case 1:
                mBinding.conInputLabelTilSic.setEnabled(false);
                mBinding.conTempLinkedTilIsc.setEnabled(false);
                mBinding.conDefaultTemperatureValueTilIsc.setEnabled(false);
                mBinding.conUnitOfMeasureTilIsc.setEnabled(false);
                mBinding.conCellConstantTilIsc.setEnabled(false);
                mBinding.conLowAlarmRootIsc.setEnabled(false);
                mBinding.conHighAlarmTilIsc.setEnabled(false);
                mBinding.conCalibRequiredAlarmTilIsc.setEnabled(false);
                mBinding.conResetCalibTilIsc.setEnabled(false);
                mBinding.conSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.conRow5Isc.setVisibility(View.GONE);
                mBinding.conRow6Isc.setVisibility(View.GONE);
                break;

            case 2:
                //View
                mBinding.conCellConstantEdtIsc.setEnabled(false);
                mBinding.conCompFactorRootIsc.setEnabled(false);
                mBinding.conCompFactorRootIsc.setEnabled(false);
                mBinding.conSmoothingFactorTilIsc.setEnabled(false);
                mBinding.conSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.conDeleteLayoutIsc.setVisibility(View.GONE);

                break;
        }
    }
}
