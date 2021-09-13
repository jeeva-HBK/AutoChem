package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsensorPhBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.bufferArr;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.sensorSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.tempLinkedArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorPh_Config extends Fragment implements DataReceiveCallback {
    FragmentInputsensorPhBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    boolean primary;
    private static final String TAG = "FragmentInputSensor";
    String sensorSequence;

    String inputNumber, sensorName;
    int sensorStatus;

    public FragmentInputSensorPh_Config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
        primary = true;
    }

    public FragmentInputSensorPh_Config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_ph, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (BaseActivity) getActivity();
        mAppClass = (ApplicationClass) getActivity().getApplication();

        init();
    }

    void init() {
        // initializing DB
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();

        // initializing ATXT adapters
        mBinding.phSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr));
        mBinding.phSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr));
        mBinding.phBufferTypeAtxtIsc.setAdapter(getAdapter(bufferArr));
        mBinding.phTempLinkedAtxtIsc.setAdapter(getAdapter(tempLinkedArr));
        mBinding.phResetCalibrationAtxtIsc.setAdapter(getAdapter(resetCalibrationArr));
        mBinding.phSeqNumberAtxtIsc.setAdapter(getAdapter(sensorSequenceNumber));
        sensorSequenceNumber();

        // change UI for userRole
        changeUI(userType);

        // initializing clickable's listeners
        mBinding.phSaveFabIsc.setOnClickListener(this::save);
        mBinding.phDeleteFabIsc.setOnClickListener(this::delete);

        // Back btn
        mBinding.phBackArrowIsc.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
    }

    void changeUI(int userRole) {
        switch (userRole) {
            case 1: // Basic
                mBinding.phTemperatureSensorLinkedTilIsc.setVisibility(View.GONE);
                mBinding.phRow5Isc.setVisibility(View.GONE);
                mBinding.phRow6Isc.setVisibility(View.GONE);
                mBinding.phInputNumberTilIsc.setEnabled(false);
                mBinding.phInputLabelTilIsc.setEnabled(false);
                mBinding.phSensorTypeTilIsc.setEnabled(false);
                mBinding.phBufferTypeTilIsc.setEnabled(false);
                mBinding.phCalibrationRequiredAlarmTilIsc.setEnabled(false);

                mBinding.phLowAlarmTilIsc.setEnabled(false);
                mBinding.phAlarmLowDeciIsc.setEnabled(false);
                mBinding.phHighAlarmTilIsc.setEnabled(false);
                mBinding.phHighAlarmDeciIsc.setEnabled(false);

                mBinding.phDefaultTemperatureValueTilIsc.setEnabled(false);
                mBinding.phResetCalibrationTilIsc.setEnabled(false);
                break;

            case 2: // Intermediate
                mBinding.phInputNumberTilIsc.setEnabled(false);
                mBinding.phSensorTypeTilIsc.setEnabled(false);
                mBinding.phTemperatureSensorLinkedTilIsc.setEnabled(false);
                mBinding.phSmoothingFactorTilIsc.setEnabled(false);
                mBinding.phSensorActivationTilIsc.setVisibility(View.GONE);
                mBinding.phDeleteLayoutIsc.setVisibility(View.GONE);
                break;
        }
    }

    void delete(View view) {
        sendData(2);
    }

    void save(View view) {
        Toast.makeText(mAppClass, getDefaultTempValue(), Toast.LENGTH_SHORT).show();
        if (validField()) {
            sendData(sensorStatus);
        }
    }

    void sendData(int sensorStatus) {
        sensorSequenceNumber();
        mActivity.showProgress();
        // Write -> {* 1234$ 0$ 0$ 04$ 01$ 00$ 1$ 0$ pHSensor$ 0$ 1$ +220.00$ 090$ 07.25$ 12.50$ 300$ 1$ 1 *}
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.phInputNumberEdtIsc) + SPILT_CHAR +
                getPosition(2, toString(mBinding.phSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPosition(1, toString(mBinding.phSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.phInputLabelEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.phBufferTypeAtxtIsc), bufferArr) + SPILT_CHAR +
                getPosition(1, toString(mBinding.phTempLinkedAtxtIsc), tempLinkedArr) + SPILT_CHAR +
                getDefaultTempValue() + SPILT_CHAR +
                toString(3, mBinding.phSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimal(toString(2, mBinding.phAlarmLowEdtIsc), toString(2, mBinding.phAlarmLowDeciIsc)) + SPILT_CHAR +
                getDecimal(toString(2, mBinding.phAlarmhighEdtIsc), toString(2, mBinding.phHighAlarmDeciIsc)) + SPILT_CHAR +
                toString(3, mBinding.phCalibrationRequiredEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.phResetCalibrationAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus
        );
    }

    void sensorSequenceNumber() {
        if (Integer.parseInt(inputNumber) > 13 && Integer.parseInt(inputNumber) < 21) {
            mBinding.phSeqNumberTilIsc.setVisibility(View.VISIBLE);
            if (!mBinding.phSeqNumberAtxtIsc.getText().toString().isEmpty()) {
                sensorSequence = getPosition(1, toString(mBinding.phSeqNumberAtxtIsc), sensorSequenceNumber);
            }
        } else {
            mBinding.phSeqNumberTilIsc.setVisibility(View.INVISIBLE);
            sensorSequence = "0";

        }
    }

    private String getDefaultTempValue() {
        return (mBinding.phTempValueTBtn.isChecked() ? "+" : "-") + toString(3, mBinding.phTemperatureEdtIsc) + "." + toString(2, mBinding.phTempDeciIsc);
    }

    private String getDecimal(String wholeValue, String decimalValue) {
        String wholeResult = wholeValue, decimalResult = decimalValue;
        if (wholeValue.isEmpty() || wholeValue.equals("") || wholeValue == null) {
            wholeResult = "00";
        }
        if (decimalValue.isEmpty() || decimalValue.equals("") || decimalValue == null) {
            decimalResult = "00";
        }
        return wholeResult + "." + decimalResult;
    }

    boolean validField() {
        if (isEmpty(mBinding.phCalibrationRequiredEdtIsc)) {
            mBinding.phCalibrationRequiredEdtIsc.setText("0");
        }
        if (isEmpty(mBinding.phInputNumberEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Input Number cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.phSensorTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Sensor Type cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.phInputLabelEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Input Label cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.phBufferTypeAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Choose any mode of Buffer Type");
            return false;
        } else if (Integer.parseInt(mBinding.phCalibrationRequiredEdtIsc.getText().toString()) > 366) {
            mAppClass.showSnackBar(getContext(), "Calibration Required Alarm should be less than 366");
            return false;
        } else if (isEmpty(mBinding.phAlarmLowEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.phAlarmhighEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Alarm high cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.phTempLinkedAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Select Temperature Sensor Linked value");
            return false;
        } else if (isEmpty(mBinding.phTemperatureEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Temperature value cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.phResetCalibrationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Select Reset Calibration value");
            return false;
        } else if (isEmpty(mBinding.phSmoothingFactorEdtIsc)) {
            mAppClass.showSnackBar(getContext(), "Smoothing factor cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.phSmoothingFactorEdtIsc.getText().toString()) > 101) {
            mAppClass.showSnackBar(getContext(), "Smoothing factor should be less than 101");
            return false;
        } else if (isEmpty(mBinding.phSensorActivationAtxtIsc)) {
            mAppClass.showSnackBar(getContext(), "Select Sensor Activation value");
            return false;
        }
        return true;
    }

    String toStringSplit(int digits, int digitPoint, EditText editText) {
        if (editText.getText().toString().split("\\.").length == 1) {
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + "." + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + "." + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
    }

    Boolean isEmpty(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        editText.setError(null);
        return false;
    }

    Boolean isEmpty(AutoCompleteTextView editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        editText.setError(null);
        return false;
    }

    String getPosition(int digit, String string, String[] strArr) {
        String j = null;
        for (int i = 0; i < strArr.length; i++) {
            if (string.equals(strArr[i])) {
                j = String.valueOf(i);
            }
        }
        return mAppClass.formDigits(digit, j);
    }

    String toString(int digits, EditText editText) {
        return mAppClass.formDigits(digits, editText.getText().toString());
    }

    String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
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
            handleResponce(data.split("\\*")[1].split("\\$"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, inputNumber));
        } else {
            mBinding.phInputNumberEdtIsc.setText(inputNumber);
            mBinding.phSensorTypeAtxtIsc.setText(sensorName);
            mBinding.phDeleteLayoutIsc.setVisibility(View.INVISIBLE);
            mBinding.phSaveTxtIsc.setText("ADD");
        }
    }

    void handleResponce(String[] splitData) {
        mActivity.dismissProgress();
        // {*0$ 04$ 1$ 0*}
        // READ_RES - {* 1$ 04$ 0$ 01$ 00$ | 1$ 0$ pHSensor$ 0$ 1$ +220.00$ 090 $ 07.25$ 12.50$ 300$ 1$ 1 *}
        if (splitData[1].equals("04")) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.phInputNumberEdtIsc.setText(splitData[3]);

                    mBinding.phSensorTypeAtxtIsc.setText(mBinding.phSensorTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                    mBinding.phSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr));

                    // splitData[5] - sequenceNumber

                    mBinding.phSensorActivationAtxtIsc.setText(mBinding.phSensorActivationAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[6])).toString());
                    mBinding.phSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr));

                    mBinding.phInputLabelEdtIsc.setText(splitData[7]);

                    mBinding.phBufferTypeAtxtIsc.setText(mBinding.phBufferTypeAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[8])).toString());
                    mBinding.phBufferTypeAtxtIsc.setAdapter(getAdapter(bufferArr));

                    mBinding.phTempLinkedAtxtIsc.setText(mBinding.phTempLinkedAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[9])).toString());
                    mBinding.phTempLinkedAtxtIsc.setAdapter(getAdapter(tempLinkedArr));

                    mBinding.phTempValueTBtn.setChecked((splitData[10].substring(0, 1)).equals("+"));

                    mBinding.phTemperatureEdtIsc.setText(splitData[10].substring(1, 4));
                    mBinding.phTempDeciIsc.setText(splitData[10].substring(5, 7));

                    mBinding.phSmoothingFactorEdtIsc.setText(splitData[11]);

                    mBinding.phAlarmLowEdtIsc.setText(splitData[12].substring(0, 2));
                    mBinding.phAlarmLowDeciIsc.setText(splitData[12].substring(3, 5));

                    mBinding.phAlarmhighEdtIsc.setText(splitData[13].substring(0, 2));
                    mBinding.phHighAlarmDeciIsc.setText(splitData[13].substring(3, 5));

                    mBinding.phCalibrationRequiredEdtIsc.setText(splitData[14]);

                    mBinding.phResetCalibrationAtxtIsc.setText(mBinding.phResetCalibrationAtxtIsc.getAdapter().getItem(Integer.parseInt(splitData[15])).toString());
                    mBinding.phResetCalibrationAtxtIsc.setAdapter(getAdapter(resetCalibrationArr));

                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.readFailed));
                }

            } else if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[3].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                    pHEntity(Integer.parseInt(splitData[2]));

                } else if (splitData[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), getString(R.string.wrongPack));
        }
    }

    void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    void pHEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.phInputNumberEdtIsc)),
                                "N/A", Integer.parseInt(sensorSequence), "N/A", "N/A", "N/A", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                mBinding.phBackArrowIsc.performClick();
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.phInputNumberEdtIsc)),
                                mBinding.phSensorTypeAtxtIsc.getText().toString(),
                                Integer.parseInt(sensorSequence), toString(0, mBinding.phInputLabelEdtIsc),
                                toString(2, mBinding.phAlarmLowEdtIsc) + "." + toString(2, mBinding.phAlarmLowDeciIsc),
                                toString(2, mBinding.phAlarmhighEdtIsc) + "." + toString(2, mBinding.phHighAlarmDeciIsc), 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }

    }
}