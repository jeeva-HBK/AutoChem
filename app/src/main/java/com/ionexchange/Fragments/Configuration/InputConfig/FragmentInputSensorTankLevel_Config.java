package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.ionexchange.databinding.FragmentInputSensorTankLevelBinding;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.digitalArr;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.getPositionFromAtxt;
import static com.ionexchange.Others.ApplicationClass.getStringValue;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.isFieldEmpty;
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

public class FragmentInputSensorTankLevel_Config extends Fragment implements DataReceiveCallback {
    FragmentInputSensorTankLevelBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    String sensorSequence = "1"; // todo sequenceNumber
    WaterTreatmentDb db;
    InputConfigurationDao dao;

    public FragmentInputSensorTankLevel_Config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorTankLevel_Config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_input_sensor_tank_level, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
        initAdapter();
        changeUi();
        mBinding.tankLevelSaveFabIsc.setOnClickListener(this::save);
        mBinding.tankLevelDeleteFabIsc.setOnClickListener(this::delete);
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
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + "0" + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.tankLevelInputNumberTie) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.tankLevelInputSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.tankLevelInputSensorSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.tankLevelInputSensorLabelTie) + SPILT_CHAR +
                getStringValue(3, mBinding.tankLevelInputSensorOpenMessageTie) + SPILT_CHAR +
                getStringValue(6, mBinding.tankLevelInputSensorCloseMessageTie) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.tankLevelInputSensorInnerLockAct), digitalArr) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.tankLevelInputSensorAlarmAct), digitalArr) + SPILT_CHAR +
                getStringValue(6, mBinding.tankLevelInputSensorTotalTimeTie) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.tankLevelInputSensorResetTimeAct), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus);
    }


    private void initAdapter() {
        mBinding.tankLevelInputSensorTypeTie.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.tankLevelInputSensorSensorActivationTie.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.tankLevelInputSensorInnerLockAct.setAdapter(getAdapter(digitalArr, getContext()));
        mBinding.tankLevelInputSensorAlarmAct.setAdapter(getAdapter(digitalArr, getContext()));
        mBinding.tankLevelInputSensorResetTimeAct.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.tankLevelSequenceNumberTie.setAdapter(getAdapter(sensorSequenceNumber, getContext()));
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
                    mBinding.tankLevelInputNumberTie.setText(data[3]);

                    mBinding.tankLevelInputSensorTypeTie.setText(mBinding.tankLevelInputSensorTypeTie.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    mBinding.tankLevelSequenceNumberTie.setText(mBinding.tankLevelSequenceNumberTie.getAdapter().getItem(Integer.parseInt(data[5])).toString());
                    mBinding.tankLevelInputSensorSensorActivationTie.setText(mBinding.tankLevelInputSensorSensorActivationTie.getAdapter().getItem(Integer.parseInt(data[6])).toString());

                    mBinding.tankLevelInputSensorLabelTie.setText(data[7]);
                    mBinding.tankLevelInputSensorOpenMessageTie.setText(data[8]);
                    mBinding.tankLevelInputSensorCloseMessageTie.setText(data[9]);
                    mBinding.tankLevelInputSensorInnerLockAct.setText(mBinding.tankLevelInputSensorInnerLockAct.getAdapter().getItem(Integer.parseInt(data[10])).toString());
                    mBinding.tankLevelInputSensorAlarmAct.setText(mBinding.tankLevelInputSensorAlarmAct.getAdapter().getItem(Integer.parseInt(data[11])).toString());
                    mBinding.tankLevelInputSensorTotalTimeTie.setText(data[12]);
                    mBinding.tankLevelInputSensorResetTimeAct.setText(mBinding.tankLevelInputSensorResetTimeAct.getAdapter().getItem(Integer.parseInt(data[13])).toString());
                    initAdapter();
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
            } else if (data[0].equals(WRITE_PACKET)) {
                if (data[3].equals(RES_SUCCESS)) {
                    tankLevelEntity(Integer.parseInt(data[2]));
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (data[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), "Received Wrong Pack !");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +"0"+SPILT_CHAR+ READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +inputNumber);
        } else {
            mBinding.tankLevelInputNumberTie.setText(inputNumber);
            mBinding.tankLevelInputSensorTypeTie.setText(sensorName);
            mBinding.tankLevelDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.tankLevelSaveTxtIsc.setText("ADD");
        }
    }

    private boolean validField() {
        if (isFieldEmpty(mBinding.tankLevelInputSensorLabelTie)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorTypeTie)) {
            mAppClass.showSnackBar(getContext(), "Sensor Type cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorInnerLockAct)) {
            mAppClass.showSnackBar(getContext(), "Interlock cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorOpenMessageTie)) {
            mAppClass.showSnackBar(getContext(), "Open message cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorCloseMessageTie)) {
            mAppClass.showSnackBar(getContext(), "Close message cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorResetTimeAct)) {
            mAppClass.showSnackBar(getContext(), "Reset Time cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorTotalTimeTie)) {
            mAppClass.showSnackBar(getContext(), "Total time cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorAlarmAct)) {
            mAppClass.showSnackBar(getContext(), "Alarm cannot be Empty");
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorSensorActivationTie)) {
            mAppClass.showSnackBar(getContext(), "Sensor Activation cannot be Empty");
            return false;
        }
        return true;
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
                        (Integer.parseInt(getStringValue(2, mBinding.tankLevelInputNumberTie)), "0", 0, "0", "0", "0", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                mBinding.backArrowIsc.performClick();
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.tankLevelInputNumberTie)),
                                mBinding.tankLevelInputSensorTypeTie.getText().toString(),
                                0, getStringValue(0, mBinding.tankLevelInputSensorLabelTie),
                                "N/A",
                                "N/A", 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }

    }
    void changeUi(){
        switch (userType) {
            case 1:
                mBinding.tankInputNumber.setEnabled(false);
                mBinding.tankSensorLabel.setEnabled(false);
                mBinding.tankSensorType.setEnabled(false);
                mBinding.tankOpenMessage.setEnabled(false);
                mBinding.tankCloseMessage.setEnabled(false);
                mBinding.tankInnerLock.setEnabled(false);
                mBinding.tankAlarm.setEnabled(false);
                mBinding.tankTotalTime.setEnabled(false);
                mBinding.tankReseTime.setEnabled(false);
                mBinding.tankSensorActivation.setVisibility(View.GONE);
                mBinding.tankLevelRow6Isc.setVisibility(View.GONE);
                break;

            case 2:
                mBinding.tankInputNumber.setEnabled(false);
                mBinding.tankSensorType.setEnabled(false);
                mBinding.tankTotalTime.setEnabled(false);
                mBinding.tankSensorActivation.setVisibility(View.GONE);
                mBinding.tankLevelDeleteLayoutIsc.setVisibility(View.GONE);
                break;

        }
    }

}
