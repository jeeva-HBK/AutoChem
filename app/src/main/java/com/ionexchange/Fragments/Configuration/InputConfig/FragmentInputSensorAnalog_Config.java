package com.ionexchange.Fragments.Configuration.InputConfig;

import static com.ionexchange.Others.ApplicationClass.analogTypeArr;
import static com.ionexchange.Others.ApplicationClass.analogUnitArr;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.sensorSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

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
import com.ionexchange.databinding.FragmentInputsensorAnalogBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentInputSensorAnalog_Config extends Fragment implements DataReceiveCallback {

    FragmentInputsensorAnalogBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    Integer LowAlarm;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    String sensorSequence;

    public FragmentInputSensorAnalog_Config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorAnalog_Config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_inputsensor_analog, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
        initAdapter();
        sensorSequenceNumber();
        switch (userType) {
            case 1:
                mBinding.analogInputNumber.setEnabled(false);
                mBinding.analogInputLabel.setEnabled(false);
                mBinding.analogSensorType.setEnabled(false);
                mBinding.analogAnalogType.setEnabled(false);
                mBinding.analogUnit.setEnabled(false);
                mBinding.analogMinValue.setEnabled(false);
                mBinding.analogMaxValue.setEnabled(false);
                mBinding.analogSmoothingFactor.setVisibility(View.GONE);
                mBinding.analogAlarmLow.setEnabled(false);
                mBinding.analogAlarmHigh.setEnabled(false);
                mBinding.analogCalibAlarmRequired.setEnabled(false);
                mBinding.analogResetCalibration.setEnabled(false);
                mBinding.analogSensorActivation.setVisibility(View.GONE);
                mBinding.analogRow6Isc.setVisibility(View.GONE);
                break;

            case 2:
                mBinding.analogInputNumber.setEnabled(false);
                mBinding.analogSensorType.setEnabled(false);
                mBinding.analogAnalogType.setEnabled(false);
                mBinding.analogUnit.setEnabled(false);
                mBinding.analogSmoothingFactor.setEnabled(false);
                mBinding.analogSensorActivation.setVisibility(View.GONE);
                mBinding.analogDeleteLayoutIsc.setVisibility(View.GONE);
                break;
        }

        mBinding.analogSaveLayoutIsc.setOnClickListener(this::save);
        mBinding.analogSaveFabIsc.setOnClickListener(this::save);
        mBinding.analogDeleteFabIsc.setOnClickListener(this::delete);
        mBinding.analogDeleteLayoutIsc.setOnClickListener(this::delete);

        mBinding.backArrowIsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
            }
        });
    }


    private void delete(View view) {
        sendData(2);
    }


    private void save(View view) {
        if (validField()) {
            sendData(sensorStatus);
        }
    }

    void sendData(int sensorStatus) {
        sensorSequenceNumber();
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + "0" + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.analogInputNumberTie) + SPILT_CHAR +
                getPosition(2, toString(mBinding.analogSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPosition(1, toString(mBinding.analogTypeTie), analogTypeArr) + SPILT_CHAR +
                getPosition(1, toString(mBinding.analogSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.analogInputLabelTie) + SPILT_CHAR +
                getPosition(1, toString(mBinding.analogUnitMeasurementTie), analogUnitArr) + SPILT_CHAR +
                toString(2, mBinding.analogMinValueTie) + "." + toString(2, mBinding.analogMinValueIsc) + SPILT_CHAR +
                toString(2, mBinding.analogMaxValueTie) + "." + toString(2, mBinding.analogMaxValueIsc) + SPILT_CHAR +
                toString(3, mBinding.analogSmoothingFactorTie) + SPILT_CHAR +
                toString(2, mBinding.analogAlarmLowTie) + "." + toString(2, mBinding.lowAlarmMinValueIsc) + SPILT_CHAR +
                toString(2, mBinding.analogHighLowTie) + "." + toString(2, mBinding.highAlarmMinValueIsc) + SPILT_CHAR +
                toString(3, mBinding.analogCalibrationRequiredAlarmTie) + SPILT_CHAR +
                getPosition(1, toString(mBinding.analogResetCalibrationTie), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus);
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

    private String toStringSplit(int digits, int digitPoint, EditText editText) {
        if (editText.getText().toString().split("\\.").length == 1) {
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
    }

    private String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    private void initAdapter() {
        mBinding.analogSensorTypeTie.setAdapter(getAdapter(inputTypeArr));
        mBinding.analogSensorActivationTie.setAdapter(getAdapter(sensorActivationArr));
        mBinding.analogTypeTie.setAdapter(getAdapter(analogTypeArr));
        mBinding.analogUnitMeasurementTie.setAdapter(getAdapter(analogUnitArr));
        mBinding.analogResetCalibrationTie.setAdapter(getAdapter(resetCalibrationArr));
        mBinding.analogSequenceNumberTie.setAdapter(getAdapter(sensorSequenceNumber));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + "0" + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + inputNumber);
        } else {
            mBinding.analogInputNumberTie.setText(inputNumber);
            mBinding.analogSensorTypeTie.setText(sensorName);
            mBinding.analogDeleteLayoutIsc.setVisibility(View.INVISIBLE);
            mBinding.analogSaveTxtIsc.setText("ADD");
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
        if (data[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (data[0].equals(READ_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {
                    mBinding.analogInputNumberTie.setText(data[3]);
                    mBinding.analogSensorTypeTie.setText(mBinding.analogSensorTypeTie.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    mBinding.analogSequenceNumberTie.setText(mBinding.analogSequenceNumberTie.getAdapter().getItem(Integer.parseInt(data[5])).toString());
                    mBinding.analogTypeTie.setText(mBinding.analogTypeTie.getAdapter().getItem(Integer.parseInt(data[6])).toString());
                    mBinding.analogSensorActivationTie.setText(mBinding.analogSensorActivationTie.getAdapter().getItem(Integer.parseInt(data[7])).toString());
                    mBinding.analogInputLabelTie.setText(data[8]);
                    mBinding.analogUnitMeasurementTie.setText(mBinding.analogUnitMeasurementTie.getAdapter().getItem(Integer.parseInt(data[9])).toString());
                    mBinding.analogMinValueTie.setText(data[10].substring(0, 2));
                    mBinding.analogMinValueIsc.setText(data[10].substring(3, 5));
                    mBinding.analogMaxValueTie.setText(data[11].substring(0, 2));
                    mBinding.analogMaxValueIsc.setText(data[11].substring(3, 5));
                    mBinding.analogSmoothingFactorTie.setText(data[12]);
                    mBinding.analogAlarmLowTie.setText(data[13].substring(0, 2));
                    mBinding.lowAlarmMinValueIsc.setText(data[13].substring(3, 5));
                    mBinding.analogHighLowTie.setText(data[14].substring(0, 2));
                    mBinding.highAlarmMinValueIsc.setText(data[14].substring(3, 5));
                    mBinding.analogCalibrationRequiredAlarmTie.setText(data[15]);
                    mBinding.analogResetCalibrationTie.setText(mBinding.analogResetCalibrationTie.getAdapter().getItem(Integer.parseInt(data[16])).toString());
                    initAdapter();
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
            } else if (data[0].equals(WRITE_PACKET)) {
                if (data[3].equals(RES_SUCCESS)) {
                    analogEntity(Integer.parseInt(data[2]));
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (data[3].equals(RES_FAILED)) {

                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), "Received Wrong Pack !");
        }
    }


    private boolean validField() {
        if (isEmpty(mBinding.analogInputNumberTie)) {
            mAppClass.showSnackBar(getContext(), "InputNumber cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogInputLabelTie)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogMinValueTie)) {
            mAppClass.showSnackBar(getContext(), "Min Value cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogMaxValueTie)) {
            mAppClass.showSnackBar(getContext(), "Max Value cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogAlarmLowTie)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (mBinding.analogAlarmLowTie.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm low is decimal format");
            return false;
        } else if (mBinding.analogHighLowTie.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm High is decimal format");
            return false;
        } else if (isEmpty(mBinding.analogHighLowTie)) {
            mAppClass.showSnackBar(getContext(), "Alarm High cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogSmoothingFactorTie)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.analogCalibrationRequiredAlarmTie)) {
            mAppClass.showSnackBar(getContext(), "Calibration cannot be Empty");
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

    private Boolean isEmpty(AutoCompleteTextView editText) {
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

    public void analogEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.analogInputNumberTie)), "0",
                                0, "0", "0", "0", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.analogInputNumberTie)),
                                mBinding.analogSensorTypeTie.getText().toString(),
                                0, toString(0, mBinding.analogInputLabelTie),
                                toStringSplit(4, 2, mBinding.analogAlarmLowTie),
                                toStringSplit(4, 2, mBinding.analogHighLowTie), 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }
    }


    class pHm {

        String inputNumber = "InputNumber,01";

        public String getInputNumber() {
            return inputNumber;
        }

        public void setInputNumber(String inputNumber) {
            this.inputNumber = inputNumber;
        }
    }


    void sensorSequenceNumber() {
        if (Integer.parseInt(inputNumber) > 13 && Integer.parseInt(inputNumber) < 21) {
            mBinding.analogSequenceNumber.setVisibility(View.VISIBLE);
            if (!mBinding.analogSequenceNumberTie.getText().toString().isEmpty()) {
                sensorSequence = getPosition(1, toString(mBinding.analogSequenceNumberTie), sensorSequenceNumber);
            }
        } else {
            mBinding.analogSequenceNumber.setVisibility(View.GONE);
            sensorSequence = "0";

        }
    }
}
