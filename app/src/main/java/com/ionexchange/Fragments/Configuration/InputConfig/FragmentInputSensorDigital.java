
package com.ionexchange.Fragments.Configuration.InputConfig;

import static com.ionexchange.Activity.BaseActivity.dismissProgress;
import static com.ionexchange.Activity.BaseActivity.showProgress;
import static com.ionexchange.Others.ApplicationClass.digitalArr;
import static com.ionexchange.Others.ApplicationClass.digitalsensorSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.getPositionFromAtxt;
import static com.ionexchange.Others.ApplicationClass.getStringValue;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.isFieldEmpty;
import static com.ionexchange.Others.ApplicationClass.mainConfigurationDao;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.totalTimeArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.ENDPACKET;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.STARTPACKET;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

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
import com.ionexchange.Others.EventLogDemo;
import com.ionexchange.R;
import com.ionexchange.Singleton.ApiService;
import com.ionexchange.Singleton.SharedPref;
import com.ionexchange.databinding.FragmentInputsensorDigitalBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentInputSensorDigital extends Fragment implements DataReceiveCallback {

    FragmentInputsensorDigitalBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    String sensorSequence = "1";
    String  writePacket;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_inputsensor_digital, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
        inputNumber = getArguments().getString("inputNumber");
        sensorName = getArguments().getString("sensorName");
        sensorStatus = getArguments().getInt("sensorStatus");
        sensorSequence = getArguments().getString("sequenceNo");

        initAdapter();
        changeUi();

        mBinding.digitalLevelSaveFabIsc.setOnClickListener(this::save);
        mBinding.digitalLevelDeleteFabIsc.setOnClickListener(this::delete);

        mBinding.backArrowIsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppClass.popStackBack(getActivity());
            }
        });
    }

    private void changeUi() {
        switch (userType) {
            case 1:
                mBinding.digitalInputNumber.setEnabled(false);
                mBinding.digitalSensorLabel.setEnabled(false);
                mBinding.digitalSensorType.setEnabled(false);
                mBinding.digitalOpenMessage.setEnabled(false);
                mBinding.digitalCloseMessage.setEnabled(false);
                mBinding.digitalInnerLock.setEnabled(false);
                mBinding.digitalAlarm.setEnabled(false);
                mBinding.digitalTotalTime.setEnabled(false);
                mBinding.digitalResetTime.setEnabled(false);

                mBinding.digitalSensorActivation.setVisibility(View.GONE);
                mBinding.digitalLevelRow5Isc.setVisibility(View.GONE);
                break;

            case 2:
                mBinding.digitalInputNumber.setEnabled(false);
                mBinding.digitalSensorType.setEnabled(false);
                mBinding.digitalInnerLock.setEnabled(false);
                mBinding.digitalAlarm.setEnabled(false);
                mBinding.digitalTotalTime.setEnabled(false);
                mBinding.digitalInputSensorCloseMessageTie.setImeOptions(EditorInfo.IME_ACTION_DONE);
                mBinding.digitalSensorActivation.setVisibility(View.GONE);
                mBinding.digitalLevelDeleteLayoutIsc.setVisibility(View.GONE);
                break;
        }
    }

    private void delete(View view) {
        sendData(2);
    }

    private void save(View view) {
        if (validField()) {
            sendData(1);
        }
    }

    void sendData(int sensorStatus) {
        showProgress();
        writePacket = DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.digitalInputNumberTie) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.digitalInputSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.digitalInputSensorSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.digitalInputSensorLabelTie) + SPILT_CHAR +
                getStringValue(0, mBinding.digitalInputSensorOpenMessageTie) + SPILT_CHAR +
                getStringValue(0, mBinding.digitalInputSensorCloseMessageTie) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.digitalInputSensorInnerLockAct), digitalArr) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.digitalInputSensorActivateAct), digitalArr) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.digitalInputSensorAlarmAct), digitalArr) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.digitalInputSensorTotalTimeTie), totalTimeArr) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.digitalInputSensorResetTimeAct), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus + "$00";
        mAppClass.sendPacket(this, writePacket);
    }


    private void initAdapter() {
        mBinding.digitalInputSensorTypeTie.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.digitalInputSensorSensorActivationTie.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.digitalInputSensorInnerLockAct.setAdapter(getAdapter(digitalArr, getContext()));
        mBinding.digitalInputSensorAlarmAct.setAdapter(getAdapter(digitalArr, getContext()));
        mBinding.digitalInputSensorActivateAct.setAdapter(getAdapter(digitalArr, getContext()));
        mBinding.digitalInputSensorResetTimeAct.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.digitalLevelSequenceNumberTie.setAdapter(getAdapter(digitalsensorSequenceNumber, getContext()));
        mBinding.digitalInputSensorTotalTimeTie.setAdapter(getAdapter(totalTimeArr, getContext()));
    }

    @Override
    public void OnDataReceive(String data) {
        dismissProgress();
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(),  getString(R.string.connection_failed));
        } else if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(),  getString(R.string.connection_failed));
        } else if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(),  getString(R.string.connection_failed));
        } else if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), "TimeOut");
        } else if (data != null) {
            handleResponse(data.split("\\*")[1].split("\\$"));
        }
    }


    private void handleResponse(String[] data) {
        if (data[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (data[0].equals(READ_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {
                    try {
                        mBinding.digitalInputNumberTie.setText(data[3]);
                        mBinding.digitalInputSensorTypeTie.setText(mBinding.digitalInputSensorTypeTie.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                        mBinding.digitalLevelSequenceNumberTie.setText(mBinding.digitalLevelSequenceNumberTie.getAdapter().getItem(Integer.parseInt(data[5])).toString());
                        mBinding.digitalInputSensorSensorActivationTie.setText(mBinding.digitalInputSensorSensorActivationTie.getAdapter().getItem(Integer.parseInt(data[6])).toString());
                        sensorSequence = data[5];
                        mBinding.digitalInputSensorLabelTie.setText(data[7]);
                        mBinding.digitalInputSensorOpenMessageTie.setText(data[8]);
                        mBinding.digitalInputSensorCloseMessageTie.setText(data[9]);
                        mBinding.digitalInputSensorInnerLockAct.setText(mBinding.digitalInputSensorInnerLockAct.getAdapter().getItem(Integer.parseInt(data[10])).toString());
                        mBinding.digitalInputSensorActivateAct.setText(mBinding.digitalInputSensorActivateAct.getAdapter().getItem(Integer.parseInt(data[11])).toString());
                        mBinding.digitalInputSensorAlarmAct.setText(mBinding.digitalInputSensorAlarmAct.getAdapter().getItem(Integer.parseInt(data[12])).toString());

                        mBinding.digitalInputSensorTotalTimeTie.setText(mBinding.digitalInputSensorTotalTimeTie.getAdapter().getItem(Integer.parseInt(data[13])).toString());
                        mBinding.digitalInputSensorResetTimeAct.setText(mBinding.digitalInputSensorResetTimeAct.getAdapter().getItem(Integer.parseInt(data[14])).toString());

                        initAdapter();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            } else if (data[0].equals(WRITE_PACKET)) {
                if (data[3].equals(RES_SUCCESS)) {
                    analogEntity(Integer.parseInt(data[2]));
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                } else if (data[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), getString(R.string.wrongPack));
        }
    }


    private boolean validField() {
        if (isFieldEmpty(mBinding.digitalInputSensorLabelTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.input_name_validation));
            return false;
        } else if (isFieldEmpty(mBinding.digitalInputSensorSensorActivationTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.sensor_activation_validation));
            return false;
        } else if (isFieldEmpty(mBinding.digitalInputSensorOpenMessageTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.openmessage_validation));
            return false;
        } else if (isFieldEmpty(mBinding.digitalInputSensorCloseMessageTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.closemessage_validation));
            return false;
        } else if (isFieldEmpty(mBinding.digitalInputSensorResetTimeAct)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.reset_time_validation));
            return false;
        } else if (isFieldEmpty(mBinding.digitalInputSensorTotalTimeTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.total_time_validation));
            return false;
        } else if (isFieldEmpty(mBinding.digitalInputSensorAlarmAct)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_mode_validation));
            return false;
        } else if (isFieldEmpty(mBinding.digitalInputSensorInnerLockAct)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.interlock_validation));
            return false;
        }  else if (isFieldEmpty(mBinding.digitalInputSensorActivateAct)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.activatechannal_validation));
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR +
                    READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + inputNumber);
        } else {
            mBinding.digitalInputNumberTie.setText(inputNumber);
            mBinding.digitalInputSensorTypeTie.setText(sensorName);
            mBinding.digitalLevelDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.digitalLevelSequenceNumberTie.setText(mBinding.digitalLevelSequenceNumberTie.getAdapter().getItem(Integer.parseInt(sensorSequence)).toString());
            mBinding.digitalLevelSaveTxtIsc.setText("ADD");
        }
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
                        (Integer.parseInt(getStringValue(2, mBinding.digitalInputNumberTie)),
                                mBinding.digitalInputSensorTypeTie.getText().toString(), "DIGITAL",
                                1, mBinding.digitalLevelSequenceNumberTie.getText().toString(),
                                Integer.parseInt(sensorSequence), getStringValue(0, mBinding.digitalInputSensorLabelTie),
                                "N/A", "N/A", "N/A","N/A", 0,STARTPACKET + writePacket + ENDPACKET);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                new EventLogDemo(inputNumber,"Digital","Input Setting Deleted",
                        SharedPref.read(pref_USERLOGINID, ""),getContext());
                ApiService.tempString = "0";
                ApiService.getInstance(getContext()).processApiData(READ_PACKET, "04", "Input Setting Deleted - " +
                        SharedPref.read(pref_USERLOGINID, ""));
                mainConfigurationDao.updateAddSensorValue(0, Integer.parseInt(inputNumber));
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.digitalInputNumberTie)),
                                mBinding.digitalInputSensorTypeTie.getText().toString(), "DIGITAL", 1,
                                mBinding.digitalLevelSequenceNumberTie.getText().toString(),
                                Integer.parseInt(sensorSequence), getStringValue(0, mBinding.digitalInputSensorLabelTie),
                                mBinding.digitalInputSensorOpenMessageTie.getText().toString(),
                                mBinding.digitalInputSensorCloseMessageTie.getText().toString(), "N/A", "N/A", 1,STARTPACKET + writePacket + ENDPACKET);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                new EventLogDemo(inputNumber,"Digital","Input Setting Changed",
                        SharedPref.read(pref_USERLOGINID, ""),getContext());
                ApiService.tempString = "0";
                ApiService.getInstance(getContext()).processApiData(READ_PACKET, "04", "Input Setting Changed - " +
                        SharedPref.read(pref_USERLOGINID, ""));
                mainConfigurationDao.updateAddSensorValue(1, Integer.parseInt(inputNumber));
                break;
        }
        mBinding.backArrowIsc.performClick();
    }
}


