package com.ionexchange.Fragments.Configuration.InputConfig;

import static com.ionexchange.Activity.BaseActivity.dismissProgress;
import static com.ionexchange.Activity.BaseActivity.showProgress;
import static com.ionexchange.Others.ApplicationClass.digitalArr;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.getPositionFromAtxt;
import static com.ionexchange.Others.ApplicationClass.getStringValue;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.isFieldEmpty;
import static com.ionexchange.Others.ApplicationClass.levelsensorSequenceNumber;
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
import com.ionexchange.databinding.FragmentInputSensorTankLevelBinding;

import java.util.ArrayList;
import java.util.List;

//created by Silambu
public class FragmentInputSensorTankLevel extends Fragment implements DataReceiveCallback {
    FragmentInputSensorTankLevelBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    String sensorSequence = "1";
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    String writePacket;

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
        inputNumber = getArguments().getString("inputNumber");
        sensorName = getArguments().getString("sensorName");
        sensorStatus = getArguments().getInt("sensorStatus");
        sensorSequence = getArguments().getString("sequenceNo");
        initAdapter();
        changeUi();
        mBinding.tankLevelSaveFabIsc.setOnClickListener(this::save);
        mBinding.tankLevelDeleteFabIsc.setOnClickListener(this::delete);
        mBinding.backArrowIsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppClass.popStackBack(getActivity());
            }
        });
    }

    private void delete(View view) {
        if (validField()){
            sendData(2);
        }
    }

    private void save(View view) {
        if (validField()) {
            sendData(1);
        }
    }

    void sendData(int sensorStatus) {
        showProgress();
        writePacket = DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                getStringValue(2, mBinding.tankLevelInputNumberTie) + SPILT_CHAR +
                getPositionFromAtxt(2, getStringValue(mBinding.tankLevelInputSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.tankLevelInputSensorSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                getStringValue(0, mBinding.tankLevelInputSensorLabelTie) + SPILT_CHAR +
                getStringValue(0, mBinding.tankLevelInputSensorOpenMessageTie) + SPILT_CHAR +
                getStringValue(0, mBinding.tankLevelInputSensorCloseMessageTie) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.tankLevelInputSensorInnerLockAct), digitalArr) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.tankLevelInputSensorActivateAct), digitalArr) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.tankLevelInputSensorAlarmAct), digitalArr) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.tankLevelInputSensorTotalTimeTie), totalTimeArr) + SPILT_CHAR +
                getPositionFromAtxt(1, getStringValue(mBinding.tankLevelInputSensorResetTimeAct), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus + "$00";
        mAppClass.sendPacket(this,writePacket );
    }

    private void initAdapter() {
        mBinding.tankLevelInputSensorTypeTie.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.tankLevelInputSensorSensorActivationTie.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.tankLevelInputSensorInnerLockAct.setAdapter(getAdapter(digitalArr, getContext()));
        mBinding.tankLevelInputSensorAlarmAct.setAdapter(getAdapter(digitalArr, getContext()));
        mBinding.tankLevelInputSensorActivateAct.setAdapter(getAdapter(digitalArr, getContext()));
        mBinding.tankLevelInputSensorResetTimeAct.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.tankLevelSequenceNumberTie.setAdapter(getAdapter(levelsensorSequenceNumber, getContext()));
        mBinding.tankLevelInputSensorTotalTimeTie.setAdapter(getAdapter(totalTimeArr, getContext()));
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
                    mBinding.tankLevelInputNumberTie.setText(data[3]);

                    mBinding.tankLevelInputSensorTypeTie.setText(mBinding.tankLevelInputSensorTypeTie.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    mBinding.tankLevelSequenceNumberTie.setText(mBinding.tankLevelSequenceNumberTie.getAdapter().getItem(Integer.parseInt(data[5])).toString());
                    mBinding.tankLevelInputSensorSensorActivationTie.setText(mBinding.tankLevelInputSensorSensorActivationTie.getAdapter().getItem(Integer.parseInt(data[6])).toString());
                    sensorSequence = data[5];
                    mBinding.tankLevelInputSensorLabelTie.setText(data[7]);
                    mBinding.tankLevelInputSensorOpenMessageTie.setText(data[8]);
                    mBinding.tankLevelInputSensorCloseMessageTie.setText(data[9]);
                    mBinding.tankLevelInputSensorInnerLockAct.setText(mBinding.tankLevelInputSensorInnerLockAct.getAdapter().getItem(Integer.parseInt(data[10])).toString());
                    mBinding.tankLevelInputSensorActivateAct.setText(mBinding.tankLevelInputSensorActivateAct.getAdapter().getItem(Integer.parseInt(data[11])).toString());
                    mBinding.tankLevelInputSensorAlarmAct.setText(mBinding.tankLevelInputSensorAlarmAct.getAdapter().getItem(Integer.parseInt(data[12])).toString());
                    mBinding.tankLevelInputSensorTotalTimeTie.setText(mBinding.tankLevelInputSensorTotalTimeTie.getAdapter().getItem(Integer.parseInt(data[13])).toString());
                    mBinding.tankLevelInputSensorResetTimeAct.setText(mBinding.tankLevelInputSensorResetTimeAct.getAdapter().getItem(Integer.parseInt(data[14])).toString());
                    initAdapter();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            } else if (data[0].equals(WRITE_PACKET)) {
                if (data[3].equals(RES_SUCCESS)) {
                    tankLevelEntity(Integer.parseInt(data[2]));
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                } else if (data[3].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), getString(R.string.wrongPack));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {

            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR+ READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +inputNumber);
        } else {
            mBinding.tankLevelInputNumberTie.setText(inputNumber);
            mBinding.tankLevelInputSensorTypeTie.setText(sensorName);
            mBinding.tankLevelDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.tankLevelSequenceNumberTie.setText(mBinding.tankLevelSequenceNumberTie.getAdapter().getItem(Integer.parseInt(sensorSequence)).toString());
            mBinding.tankLevelSaveTxtIsc.setText("ADD");
        }
    }

    private boolean validField() {
        if (isFieldEmpty(mBinding.tankLevelInputSensorLabelTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.input_name_validation));
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorSensorActivationTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.sensor_activation_validation));
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorOpenMessageTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.openmessage_validation));
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorCloseMessageTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.closemessage_validation));
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorResetTimeAct)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.reset_time_validation));
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorTotalTimeTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.total_time_validation));
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorAlarmAct)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_mode_validation));
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorInnerLockAct)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.interlock_validation));
            return false;
        } else if (isFieldEmpty(mBinding.tankLevelInputSensorActivateAct)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.activatechannal_validation));
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
                        (Integer.parseInt(getStringValue(2, mBinding.tankLevelInputNumberTie)), "N/A",
                                "TANK", 1, "N/A",
                                1, "N/A", "N/A",
                                "N/A", "N/A","N/A", 0,STARTPACKET + writePacket + ENDPACKET);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                new EventLogDemo(inputNumber,"Tank","Input Setting Deleted", SharedPref.read(pref_USERLOGINID, ""),getContext());
                ApiService.getInstance(getContext()).processApiData(READ_PACKET, "04", "Input Setting Deleted - " +
                        SharedPref.read(pref_USERLOGINID, ""));
                mainConfigurationDao.updateAddSensorValue(0, Integer.parseInt(inputNumber));
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(getStringValue(2, mBinding.tankLevelInputNumberTie)),
                                mBinding.tankLevelInputSensorTypeTie.getText().toString(),"TANK", 1 ,
                                mBinding.tankLevelSequenceNumberTie.getText().toString(),
                                Integer.parseInt(sensorSequence), getStringValue(0, mBinding.tankLevelInputSensorLabelTie),
                                "N/A",
                                "N/A", "N/A","N/A", 1,
                                STARTPACKET + writePacket + ENDPACKET);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                mainConfigurationDao.updateAddSensorValue(1, Integer.parseInt(inputNumber));
                new EventLogDemo(inputNumber,"Tank","Input Setting Changed", SharedPref.read(pref_USERLOGINID, ""),getContext());
                ApiService.getInstance(getContext()).processApiData(READ_PACKET, "04", "Input Setting Changed - " +
                        SharedPref.read(pref_USERLOGINID, ""));
                break;
        }
        mBinding.backArrowIsc.performClick();
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
                mBinding.tankLevelInputSensorCloseMessageTie.setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;

        }
    }

}
