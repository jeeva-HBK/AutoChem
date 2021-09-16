package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.ionexchange.databinding.FragmentInputsensorModbusBinding;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.getDecimalValue;
import static com.ionexchange.Others.ApplicationClass.getPositionFromAtxt;
import static com.ionexchange.Others.ApplicationClass.getStringValue;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.isFieldEmpty;
import static com.ionexchange.Others.ApplicationClass.modBusTypeArr;
import static com.ionexchange.Others.ApplicationClass.modBusUnitArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.sensorSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.typeOfValueRead;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
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
    String sensorSequence; // Todo should verify
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
        initAdapter();

        mBinding.modBusDiagnosticSweepTie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mBinding.setModbusType("0");
                }
                if (position == 1) {
                    mBinding.setModbusType("1");
                }
            }
        });

        changeUi();

        mBinding.modbusSaveFabIsc.setOnClickListener(this::save);
        mBinding.modbusDeleteFabIsc.setOnClickListener(this::delete);

        mBinding.modBusTypeTie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), mBinding.modbusSequenceNumberTie.getAdapter().getItem(i).toString(), Toast.LENGTH_SHORT).show();
                mBinding.modbusSequenceNumberTie.setText(mBinding.modbusSequenceNumberTie.getAdapter().getItem(i).toString());
                mBinding.modbusSequenceNumberTie.setAdapter(getAdapter(sensorSequenceNumber, getContext()));
            }
        });

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
        sendData(sensorStatus);
    }

    void sendData(int sensorStatus) {
        if (validField()) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + "0" + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                    PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                    getStringValue(2, mBinding.modBusInputNumberTie) + SPILT_CHAR +
                    getPositionFromAtxt(2, getStringValue(mBinding.modBusSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.modBusTypeTie), modBusTypeArr) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.modBusTypeTie), modBusTypeArr) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.modBusTypeOfValueReadTie), typeOfValueRead) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.modBusSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                    getStringValue(0, mBinding.modBusInputLabelTie) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.modBusUnitMeasurementTie), modBusUnitArr) + SPILT_CHAR +
                    getStringValue(3, mBinding.modBusMinValueTie) + "." + getStringValue(2, mBinding.modbusMinDeciIsc) + SPILT_CHAR +
                    getStringValue(3, mBinding.modBusMaxValueTie) + "." + getStringValue(2, mBinding.modbusMaxDeciIsc) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.modBusDiagnosticSweepTie), sensorActivationArr) + getStringValue(6, mBinding.modBusTimeTie) + SPILT_CHAR +
                    getStringValue(3, mBinding.modBusSmoothingFactorTie) + SPILT_CHAR +
                    getStringValue(3, mBinding.modBusAlarmLowTie) + "." + getStringValue(2, mBinding.modbusAlarmLowIsc) + SPILT_CHAR +
                    getStringValue(3, mBinding.modBusAlarmHighTie) + "." + getStringValue(2, mBinding.modbusAlarmHighIsc) + SPILT_CHAR +
                    getStringValue(3, mBinding.modBusCalibrationRequiredAlarmTie) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.modBusResetCalibrationTie), resetCalibrationArr) + SPILT_CHAR + sensorStatus);
        }
    }


    private void initAdapter() {
        mBinding.modBusSensorTypeTie.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.modBusSensorActivationTie.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.modBusTypeTie.setAdapter(getAdapter(modBusTypeArr, getContext()));
        mBinding.modBusUnitMeasurementTie.setAdapter(getAdapter(modBusUnitArr, getContext()));
        mBinding.modBusDiagnosticSweepTie.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.modBusResetCalibrationTie.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.modBusTypeOfValueReadTie.setAdapter(getAdapter(typeOfValueRead, getContext()));
        mBinding.modbusSequenceNumberTie.setAdapter(getAdapter(sensorSequenceNumber, getContext()));
    }
    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, inputNumber));
        } else {
            mBinding.modBusInputNumberTie.setText(inputNumber);
            mBinding.modBusSensorTypeTie.setText(sensorName);
            mBinding.modbusDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.modbusSaveTxtIsc.setText("ADD");
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

                    mBinding.modBusInputNumberTie.setText(data[3]);
                    mBinding.modBusSensorTypeTie.setText(mBinding.modBusSensorTypeTie.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    mBinding.modbusSequenceNumberTie.setText(mBinding.modbusSequenceNumberTie.getAdapter().getItem(Integer.parseInt(data[5])).toString());
                    mBinding.modBusTypeTie.setText(mBinding.modBusTypeTie.getAdapter().getItem(Integer.parseInt(data[6])).toString());
                    mBinding.modBusTypeOfValueReadTie.setText(mBinding.modBusTypeOfValueReadTie.getAdapter().getItem(Integer.parseInt(data[7])).toString());
                    mBinding.modBusSensorActivationTie.setText(mBinding.modBusSensorActivationTie.getAdapter().getItem(Integer.parseInt(data[8])).toString());

                    mBinding.modBusInputLabelTie.setText(data[9]);
                    mBinding.modBusUnitMeasurementTie.setText(mBinding.modBusUnitMeasurementTie.getAdapter().getItem(Integer.parseInt(data[10])).toString());
                    mBinding.modBusMinValueTie.setText(data[11].substring(0, 3));
                    mBinding.modbusMinDeciIsc.setText(data[11].substring(4, 6));
                    mBinding.modBusMaxValueTie.setText(data[12].substring(0, 3));
                    mBinding.modbusMaxDeciIsc.setText(data[12].substring(4, 6));
                    mBinding.setModbusType(data[13]);
                    mBinding.modBusDiagnosticSweepTie.setText(mBinding.modBusDiagnosticSweepTie.getAdapter().getItem(Integer.parseInt(data[13].substring(0, 1))).toString());
                    mBinding.modBusTimeTie.setText(data[13].substring(1, 7));
                    mBinding.modBusSmoothingFactorTie.setText(data[14]);
                    mBinding.modBusAlarmLowTie.setText(data[15].substring(0, 3));
                    mBinding.modbusAlarmLowIsc.setText(data[15].substring(4, 6));
                    mBinding.modBusAlarmHighTie.setText(data[16].substring(0, 3));
                    mBinding.modbusAlarmHighIsc.setText(data[16].substring(4, 6));
                    mBinding.modBusCalibrationRequiredAlarmTie.setText(data[17]);
                    mBinding.modBusResetCalibrationTie.setText(mBinding.modBusResetCalibrationTie.getAdapter().getItem(Integer.parseInt(data[18])).toString());
                    initAdapter();
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
            } else if (data[0].equals(WRITE_PACKET)) {
                if (data[3].equals(RES_SUCCESS)) {
                    modBusEntity(Integer.parseInt(data[2]));
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
        if (isFieldEmpty(mBinding.modBusInputLabelTie)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.modBusTypeTie)) {
            mAppClass.showSnackBar(getContext(), "Modbus Type cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.modBusTypeOfValueReadTie)) {
            mAppClass.showSnackBar(getContext(), "Type value of read cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.modBusMinValueTie)) {
            mAppClass.showSnackBar(getContext(), "Min Value cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.modBusMaxValueTie)) {
            mAppClass.showSnackBar(getContext(), "Max Value cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.modBusDiagnosticSweepTie)) {
            mAppClass.showSnackBar(getContext(), "Diagnostic sweep cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.modBusAlarmLowTie)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.modBusAlarmHighTie)) {
            mAppClass.showSnackBar(getContext(), "Alarm High cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.modBusCalibrationRequiredAlarmTie)) {
            mAppClass.showSnackBar(getContext(), "Calibration Required cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.modBusResetCalibrationTie)) {
            mAppClass.showSnackBar(getContext(), "Reset Calibration cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.modBusSmoothingFactorTie)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.modBusUnitMeasurementTie)) {
            mAppClass.showSnackBar(getContext(), "Measurement Unit cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.modBusSensorActivationTie)) {
            mAppClass.showSnackBar(getContext(), "Sensor Activation cannot be Empty");
            return false;
        } else if (mBinding.modBusTimeTie.getText().toString().length() > 6) {
            mAppClass.showSnackBar(getContext(), "Invalid Time ");
            return false;
        }
        return true;
    }

    void changeUi(){
        switch (userType) {
            case 1:
                mBinding.modbusInputNumber.setEnabled(false);
                mBinding.modbusInputLabel.setEnabled(false);
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
                mBinding.modbusRow6Isc.setVisibility(View.GONE);
                break;

            case 2:
                mBinding.modbusInputNumber.setEnabled(false);

                mBinding.modbusTypeOfValueRead.setEnabled(false);
                mBinding.modbusUnit.setEnabled(false);
                mBinding.modbusDiagnosticSweep.setEnabled(false);
                mBinding.modbusDiagnosticTime.setEnabled(false);
                mBinding.modbusSmoothingFactor.setEnabled(false);

                mBinding.modbusSensorActivation.setVisibility(View.GONE);
                mBinding.modbusDeleteLayoutIsc.setVisibility(View.GONE);
                break;
        }
    }

    public void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void modBusEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.modBusInputNumberTie)),
                                "0", 0, "0", "0",
                                "0", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                mBinding.backArrowIsc.performClick();
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.modBusInputNumberTie)),
                                mBinding.modBusSensorTypeTie.getText().toString(),
                                0, getStringValue(0, mBinding.modBusInputLabelTie),
                                getDecimalValue(mBinding.modBusAlarmLowTie, 3, mBinding.modbusAlarmLowIsc, 2),
                                getDecimalValue(mBinding.modBusAlarmHighTie, 3, mBinding.modbusAlarmHighIsc, 2), 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }
    }
}
