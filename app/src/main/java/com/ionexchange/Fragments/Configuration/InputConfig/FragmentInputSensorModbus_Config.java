package com.ionexchange.Fragments.Configuration.InputConfig;

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
import com.ionexchange.databinding.FragmentInputsensorModbusBinding;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.modBusTypeArr;
import static com.ionexchange.Others.ApplicationClass.modBusUnitArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.typeOfValueRead;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorModbus_Config extends Fragment implements DataReceiveCallback {

    FragmentInputsensorModbusBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    WaterTreatmentDb db;
    InputConfigurationDao dao;

    public FragmentInputSensorModbus_Config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorModbus_Config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_inputsensor_modbus, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();

        switch (userType) {

            case 1:
                mBinding.modbusInputLabel.setEnabled(false);
                mBinding.modbusModbusType.setEnabled(false);
                mBinding.modbusTypeOfValueRead.setEnabled(false);
                mBinding.modbusUnit.setEnabled(false);
                mBinding.modbusMinValue.setEnabled(false);
                mBinding.modbusMaxValue.setEnabled(false);
                mBinding.modbusDiagnosticSweep.setEnabled(false);
                mBinding.modbusDiagnosticTime.setEnabled(false);
                mBinding.modbusAlarmLow.setEnabled(false);
                mBinding.modbusAlarmHigh.setEnabled(false);
                mBinding.modbusCalibrationRequiredAlarm.setEnabled(false);
                mBinding.modbusResetCalibration.setEnabled(false);

                mBinding.modbusSmoothingFactor.setVisibility(View.GONE);
                mBinding.modbusSensorActivation.setVisibility(View.GONE);

                mBinding.modbusRow6.setVisibility(View.GONE);
                break;

            case 2:
                mBinding.modbusModbusType.setEnabled(false);
                mBinding.modbusTypeOfValueRead.setEnabled(false);
                mBinding.modbusUnit.setEnabled(false);
                mBinding.modbusDiagnosticSweep.setEnabled(false);
                mBinding.modbusDiagnosticTime.setEnabled(false);
                mBinding.modbusSmoothingFactor.setEnabled(false);

                mBinding.modbusSensorActivation.setVisibility(View.GONE);
                mBinding.modbusDeleteLayout.setVisibility(View.GONE);
                break;

            case 3:

                break;

        }

        initAdapter();
        mBinding.modbusSaveFab.setOnClickListener(this::save);
        mBinding.modbusSaveLayout.setOnClickListener(this::save);

        mBinding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
            }
        });
    }

    private void save(View view) {
        if (validField()) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                    PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                    toString(2, mBinding.modBusInputNumberTie) + SPILT_CHAR +
                    getPosition(2, toString(mBinding.modBusSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.modBusTypeTie), modBusTypeArr) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.modBusTypeOfValueReadTie), typeOfValueRead) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.modBusSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                    toString(0, mBinding.modBusInputLabelTie) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.modBusUnitMeasurementTie), modBusUnitArr) + SPILT_CHAR +
                    toString(4, mBinding.modBusMinValueTie) + SPILT_CHAR +
                    toString(4, mBinding.modBusMaxValueTie) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.modBusDiagnosticSweepTie), sensorActivationArr) + toString(6, mBinding.modBusTimeTie) + SPILT_CHAR +
                    toString(3, mBinding.modBusSmoothingFactorTie) + SPILT_CHAR +
                    toStringSplit(4, 2, mBinding.modBusAlarmLowTie) + SPILT_CHAR +
                    toStringSplit(4, 2, mBinding.modBusAlarmHighTie) + SPILT_CHAR +
                    toString(3, mBinding.modBusCalibrationRequiredAlarmTie) + SPILT_CHAR +
                    getPosition(1, toString(mBinding.modBusResetCalibrationTie), resetCalibrationArr) + SPILT_CHAR + sensorStatus);

        }
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
        mBinding.modBusSensorTypeTie.setAdapter(getAdapter(inputTypeArr));
        mBinding.modBusSensorActivationTie.setAdapter(getAdapter(sensorActivationArr));
        mBinding.modBusTypeTie.setAdapter(getAdapter(modBusTypeArr));
        mBinding.modBusUnitMeasurementTie.setAdapter(getAdapter(modBusUnitArr));
        mBinding.modBusDiagnosticSweepTie.setAdapter(getAdapter(sensorActivationArr));
        mBinding.modBusResetCalibrationTie.setAdapter(getAdapter(resetCalibrationArr));
        mBinding.modBusTypeOfValueReadTie.setAdapter(getAdapter(typeOfValueRead));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + inputNumber);
        } else {
            mBinding.modBusInputNumberTie.setText(inputNumber);
            mBinding.modBusSensorTypeTie.setText(sensorName);
            mBinding.modbusDeleteLayout.setVisibility(View.INVISIBLE);
            mBinding.saveTxt.setText("ADD");
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
            handleResponse(data.split("\\*")[1].split("#"));
        }
    }

    private void handleResponse(String[] data) {

        if (data[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (data[0].equals(READ_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {

                    mBinding.modBusInputNumberTie.setText(data[3]);

                    mBinding.modBusSensorTypeTie.setText(mBinding.modBusSensorTypeTie.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    mBinding.modBusTypeTie.setText(mBinding.modBusTypeTie.getAdapter().getItem(Integer.parseInt(data[5])).toString());

                    mBinding.modBusSensorActivationTie.setText(mBinding.modBusSensorActivationTie.getAdapter().getItem(Integer.parseInt(data[5])).toString());

                    mBinding.modBusInputLabelTie.setText(data[7]);
                    mBinding.modBusUnitMeasurementTie.setText(mBinding.modBusUnitMeasurementTie.getAdapter().getItem(Integer.parseInt(data[8])).toString());
                    mBinding.modBusMinValueTie.setText(data[9]);
                    mBinding.modBusMaxValueTie.setText(data[10]);
                    mBinding.modBusDiagnosticSweepTie.setText(mBinding.modBusDiagnosticSweepTie.getAdapter().getItem(Integer.parseInt(data[11].substring(0, 1))).toString());
                    mBinding.modBusTimeTie.setText(data[11].substring(1, 7));
                    mBinding.modBusSmoothingFactorTie.setText(data[12]);
                    mBinding.modBusAlarmLowTie.setText(data[13]);
                    mBinding.modBusAlarmHighTie.setText(data[14]);
                    mBinding.modBusCalibrationRequiredAlarmTie.setText(data[15]);
                    mBinding.modBusResetCalibrationTie.setText(mBinding.modBusResetCalibrationTie.getAdapter().getItem(Integer.parseInt(data[16])).toString());
                    initAdapter();
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
            } else if (data[0].equals(WRITE_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {
                    modBusEntity(1);
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), "Received Wrong Pack !");
        }
    }

    private boolean validField() {
        if (isEmpty(mBinding.modBusInputNumberTie)) {
            mAppClass.showSnackBar(getContext(), "InputNumber cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.modBusInputLabelTie)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.modBusMinValueTie)) {
            mAppClass.showSnackBar(getContext(), "Min Value cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.modBusMaxValueTie)) {
            mAppClass.showSnackBar(getContext(), "Max Value cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.modBusAlarmLowTie)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.modBusAlarmHighTie)) {
            mAppClass.showSnackBar(getContext(), "Alarm High cannot be Empty");
            return false;
        } else if (mBinding.modBusAlarmLowTie.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm low is decimal format");
            return false;
        } else if (mBinding.modBusMaxValueTie.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm High is decimal format");
            return false;
        } else if (isEmpty(mBinding.modBusTimeTie)) {
            mAppClass.showSnackBar(getContext(), "Time cannot be Empty");
            return false;
        } else if (mBinding.modBusTimeTie.getText().toString().length() > 6) {
            mAppClass.showSnackBar(getContext(), "Invalid Time ");
            return false;
        } else if (isEmpty(mBinding.modBusSmoothingFactorTie)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.modBusCalibrationRequiredAlarmTie)) {
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

    public void modBusEntity(int flagValue) {
        switch (flagValue) {
            case 0:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.modBusInputNumberTie)),
                                "0", 0, "0", "0",
                                "0", flagValue);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                break;

            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.modBusInputNumberTie)),
                                mBinding.modBusSensorTypeTie.getText().toString(),
                                0, toString(0, mBinding.modBusInputLabelTie),
                                toStringSplit(4, 2, mBinding.modBusAlarmLowTie),
                                toStringSplit(4, 2, mBinding.modBusAlarmHighTie), flagValue);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }

    }
}
