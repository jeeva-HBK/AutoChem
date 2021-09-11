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
import com.ionexchange.databinding.FragmentInputsensorToraidalconductivityBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.TemperatureCompensationType;
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

public class FragmentInputSensorToroidalConductivity_config extends Fragment implements DataReceiveCallback {
    private static final String TAG = "FragmentInputSensorCond";
    FragmentInputsensorToraidalconductivityBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    int sensorStatus;
    String inputNumber;
    String sensorName;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    int userManagement;
    String sensorSequence;

    public FragmentInputSensorToroidalConductivity_config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorToroidalConductivity_config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_toraidalconductivity, container, false);
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
        mBinding.candSaveFabIsc.setOnClickListener(this::save);
        mBinding.candDeleteFabIsc.setOnClickListener(this::delete);
        mBinding.candBackArrowIsc.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
        mBinding.candCompensationAtxtIsc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0) {
                    mBinding.candCompFactorEdtIsc.setEnabled(true);
                    mBinding.candHighAlarmDeciIsc.setEnabled(true);
                } else {
                    mBinding.candCompFactorEdtIsc.setEnabled(false);
                    mBinding.candHighAlarmDeciIsc.setEnabled(false);
                }
            }
        });
    }

    private void sensorSequenceNumber() {
        if (Integer.parseInt(inputNumber) > 13 && Integer.parseInt(inputNumber) < 21) {
            mBinding.candSeqNumberTilIsc.setVisibility(View.VISIBLE);
            if (!mBinding.candSeqNumberAtxtIsc.getText().toString().isEmpty()) {
                sensorSequence = getPosition(1, toString(mBinding.candSeqNumberAtxtIsc), sensorSequenceNumber);
            }
        } else {
            mBinding.candSeqNumberTilIsc.setVisibility(View.GONE);
            sensorSequence = "0";
        }
    }

    private void delete(View view) {
        if (validation()) {
            if (getPosition(1, toString(mBinding.candCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
                sendDataLinearTemperature(2);
            } else {
                sendStandardNaClTemperature(2);
            }

        }
    }

    private void save(View view) {
        if (validation()) {
            if (getPosition(1, toString(mBinding.candCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
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
                toString(2, mBinding.candInputNumberEdtIsc) + SPILT_CHAR +
                getPosition(2, toString(mBinding.candSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPosition(0, toString(mBinding.candSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.candInputLabelEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.candTempLinkedAtxtIsc), tempLinkedArr) + SPILT_CHAR +
                getPlusMinusValue(mBinding.candTempValueTBtn, mBinding.candTemperatureEdtIsc, 3, mBinding.candTempDeciIsc, 2) + SPILT_CHAR +
                getPosition(1, toString(mBinding.candUnitOfMeasureAtxtIsc), unitArr) + SPILT_CHAR +
                getPosition(0, toString(mBinding.candCompensationAtxtIsc), TemperatureCompensationType) + SPILT_CHAR +
                getDecimalValue(mBinding.candCompFactorEdtIsc, 2, mBinding.candCompFactorDeciIsc, 2) + SPILT_CHAR +
                toString(3, mBinding.candSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.candLowAlarmEdtIsc, 6, mBinding.candAlarmlowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.candHighAlarmEdtIsc, 6, mBinding.candHighAlarmDeciIsc, 2) + SPILT_CHAR +
                toString(3, mBinding.candCalibRequiredAlarmEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.candResetCalibAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus);
    }

    void sendStandardNaClTemperature(int sensorStatus) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.candInputNumberEdtIsc) + SPILT_CHAR +
                getPosition(2, toString(mBinding.candSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPosition(1, toString(mBinding.candSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.candInputLabelEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.candTempLinkedAtxtIsc), tempLinkedArr) + SPILT_CHAR +
                getPlusMinusValue(mBinding.candTempValueTBtn, mBinding.candTemperatureEdtIsc, 3, mBinding.candTempDeciIsc, 2) + SPILT_CHAR +
                getPosition(1, toString(mBinding.candUnitOfMeasureAtxtIsc), unitArr) + SPILT_CHAR +
                getPosition(0, toString(mBinding.candCompensationAtxtIsc), TemperatureCompensationType) + SPILT_CHAR +
                toString(3, mBinding.candSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.candLowAlarmEdtIsc, 6, mBinding.candAlarmlowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.candHighAlarmEdtIsc, 6, mBinding.candHighAlarmDeciIsc, 2) + SPILT_CHAR +
                toString(3, mBinding.candCalibRequiredAlarmEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.candResetCalibAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus
        );
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
        mBinding.candSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr));
        mBinding.candSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr));
        mBinding.candTempLinkedAtxtIsc.setAdapter(getAdapter(tempLinkedArr));
        mBinding.candUnitOfMeasureAtxtIsc.setAdapter(getAdapter(unitArr));
        mBinding.candResetCalibAtxtIsc.setAdapter(getAdapter(resetCalibrationArr));
        mBinding.candCompensationAtxtIsc.setAdapter(getAdapter(TemperatureCompensationType));
        mBinding.candSeqNumberAtxtIsc.setAdapter(getAdapter(sensorSequenceNumber));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    private String toStringSplit(int digits, int digitPoint, EditText editText) {
        if (editText.getText().toString().split("\\.").length == 1) {
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + "04");
        } else {
            mBinding.candInputNumberEdtIsc.setText(inputNumber);
            mBinding.candSensorTypeAtxtIsc.setText(sensorName);
            mBinding.candDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.candSaveIsc.setText("ADD");
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
        mActivity.dismissProgress();
        // read Res - 1$ 04$ 0$ 04$ 05$ 1$ 0$ TCOCOO$ 1$ 33$ 1$ 1$ 200$ 120000$ 220000$ 300$ 1$ 0
        // write Res -
        if (spiltData[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (spiltData[0].equals(READ_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mBinding.candInputNumberEdtIsc.setText(spiltData[3]);
                    mBinding.candSensorTypeAtxtIsc.setText(mBinding.candSensorTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[4])).toString());
                    mBinding.candSeqNumberAtxtIsc.setText(mBinding.candSeqNumberAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[5])).toString());
                    mBinding.candSensorActivationAtxtIsc.setText(mBinding.candSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[6])).toString());
                    mBinding.candInputLabelEdtIsc.setText(spiltData[7]);
                    mBinding.candTempLinkedAtxtIsc.setText(mBinding.candTempLinkedAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[8])).toString());

                    mBinding.candTempValueTBtn.setChecked(spiltData[9].substring(0, 1).equals("+"));
                    mBinding.candTemperatureEdtIsc.setText(spiltData[9].substring(1, 4));
                    mBinding.candTempDeciIsc.setText(spiltData[9].substring(5, 7));

                    mBinding.candUnitOfMeasureAtxtIsc.setText(mBinding.candUnitOfMeasureAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[10])).toString());
                    mBinding.candCompensationAtxtIsc.setText(mBinding.candCompensationAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[11])).toString());
                    if (spiltData[11].equals("0")) {
                        mBinding.candCompFactorEdtIsc.setText(spiltData[12].substring(0,2));
                        mBinding.candCompFactorDeciIsc.setText(spiltData[12].substring(3,5));

                        mBinding.candSmoothingFactorEdtIsc.setText(spiltData[13]);

                        mBinding.candLowAlarmEdtIsc.setText(spiltData[14].substring(0,6));
                        mBinding.candAlarmlowDeciIsc.setText(spiltData[14].substring(7,9));

                        mBinding.candHighAlarmEdtIsc.setText(spiltData[15].substring(0,6));
                        mBinding.candHighAlarmDeciIsc.setText(spiltData[15].substring(7,9));

                        mBinding.candCalibRequiredAlarmEdtIsc.setText(spiltData[16]);
                        mBinding.candResetCalibAtxtIsc.setText(mBinding.candResetCalibAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[17])).toString());
                    } else {
                        mBinding.candSmoothingFactorEdtIsc.setText(spiltData[12]);

                        mBinding.candLowAlarmEdtIsc.setText(spiltData[13].substring(0,6));
                        mBinding.candAlarmlowDeciIsc.setText(spiltData[13].substring(7,9));

                        mBinding.candHighAlarmEdtIsc.setText(spiltData[14].substring(0,6));
                        mBinding.candHighAlarmDeciIsc.setText(spiltData[14].substring(7,9));

                        mBinding.candCalibRequiredAlarmEdtIsc.setText(spiltData[15]);
                        mBinding.candResetCalibAtxtIsc.setText(mBinding.candResetCalibAtxtIsc.getAdapter().getItem(Integer.parseInt(spiltData[16])).toString());
                    }
                    initAdapters();
                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }

            } else if (spiltData[0].equals(WRITE_PACKET)) {
                if (spiltData[3].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                    tankLevelEntity(Integer.valueOf(spiltData[2]));
                } else if (spiltData[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }

        } else {
            Log.e(TAG, "handleResponse: Wrong Packet");
        }
    }

    String getDecimalValue(TextInputEditText prefixEdittext, int prefixDigit, EditText suffixEdittext, int suffixDigit) {
        return toString(prefixDigit, prefixEdittext) + "." + toString(suffixDigit, suffixEdittext);
    }

    String getPlusMinusValue(ToggleButton toggleButton, TextInputEditText prefixEdt, int prefixDigit, EditText suffixEdt, int suffixDigit) {
        return (toggleButton.isChecked() ? "+" : "-") + toString(prefixDigit, prefixEdt) + "." + toString(suffixDigit, suffixEdt);
    }

    boolean validation() {
        if (isEmpty(mBinding.candSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.candInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Input Label Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.candSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Calibration Alarm Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.candCompensationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Temperature Compensation Value Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.candTempLinkedAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Temperature Linked  Value Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.candLowAlarmEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm Low Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.candHighAlarmEdtIsc)) {
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

    public void tankLevelEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.candInputNumberEdtIsc)), "0", 0, "0", "0", "0", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                break;
            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.candInputNumberEdtIsc)),
                                mBinding.candSensorTypeAtxtIsc.getText().toString(),
                                0, toString(0, mBinding.candInputLabelEdtIsc),
                                getDecimalValue(mBinding.candLowAlarmEdtIsc, 7, mBinding.candAlarmlowDeciIsc, 2),
                                getDecimalValue(mBinding.candHighAlarmEdtIsc, 7, mBinding.candHighAlarmDeciIsc, 2), 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }

    }

    void userManagement() {
        switch (userManagement) {
            case 1:
                mBinding.candInputLabelEdtIsc.setEnabled(false);
                mBinding.candTempLinkedAtxtIsc.setEnabled(false);
                mBinding.candDefaultTemperatureValueTilIsc.setEnabled(false);
                mBinding.candTempDeciIsc.setEnabled(false);
                mBinding.candUnitOfMeasureAtxtIsc.setEnabled(false);
                mBinding.candLowAlarmEdtIsc.setEnabled(false);
                mBinding.candTempDeciIsc.setEnabled(false);
                mBinding.candHighAlarmEdtIsc.setEnabled(false);
                mBinding.candHighAlarmDeciIsc.setEnabled(false);
                mBinding.candCalibRequiredAlarmEdtIsc.setEnabled(false);
                mBinding.candResetCalibAtxtIsc.setEnabled(false);
                mBinding.candSensorActivationAtxtIsc.setVisibility(View.GONE);
                mBinding.candRow5Isc.setVisibility(View.GONE);
                mBinding.conRow7.setVisibility(View.GONE);
                break;

            case 2:
                mBinding.candCompensationAtxtIsc.setEnabled(false);
                // Role Management
                break;

            case 3:

                break;
        }
    }
}
