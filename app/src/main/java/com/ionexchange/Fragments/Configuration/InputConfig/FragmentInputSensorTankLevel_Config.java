package com.ionexchange.Fragments.Configuration.InputConfig;

import static com.ionexchange.Others.ApplicationClass.digitalArr;
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
import com.ionexchange.databinding.FragmentInputSensorTankLevelBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentInputSensorTankLevel_Config extends Fragment implements DataReceiveCallback {
    FragmentInputSensorTankLevelBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    String sensorSequence;
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
        sensorSequenceNumber();
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
                mBinding.tankSensorActivation.setVisibility(View.GONE);
                break;

            case 3:

                break;


        }

        mBinding.tankLevelSaveLayoutIsc.setOnClickListener(this::save);
        mBinding.tankLevelSaveFabIsc.setOnClickListener(this::save);
        mBinding.tankLevelDeleteLayoutIsc.setOnClickListener(this::delete);
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
        sensorSequenceNumber();
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + "0" + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.tankLevelInputNumberTie) + SPILT_CHAR +
                getPosition(2, toString(mBinding.tankLevelInputSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                sensorSequence + SPILT_CHAR +
                getPosition(1, toString(mBinding.tankLevelInputSensorSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.tankLevelInputSensorLabelTie) + SPILT_CHAR +
                toString(3, mBinding.tankLevelInputSensorOpenMessageTie) + SPILT_CHAR +
                toString(6, mBinding.tankLevelInputSensorCloseMessageTie) + SPILT_CHAR +
                getPosition(1, toString(mBinding.tankLevelInputSensorInnerLockAct), digitalArr) + SPILT_CHAR +
                getPosition(1, toString(mBinding.tankLevelInputSensorAlarmAct), digitalArr) + SPILT_CHAR +
                toString(6, mBinding.tankLevelInputSensorTotalTimeTie) + SPILT_CHAR +
                getPosition(1, toString(mBinding.tankLevelInputSensorResetTimeAct), resetCalibrationArr) + SPILT_CHAR +
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

    private String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    private void initAdapter() {
        mBinding.tankLevelInputSensorTypeTie.setAdapter(getAdapter(inputTypeArr));
        mBinding.tankLevelInputSensorSensorActivationTie.setAdapter(getAdapter(sensorActivationArr));
        mBinding.tankLevelInputSensorInnerLockAct.setAdapter(getAdapter(digitalArr));
        mBinding.tankLevelInputSensorAlarmAct.setAdapter(getAdapter(digitalArr));
        mBinding.tankLevelInputSensorResetTimeAct.setAdapter(getAdapter(resetCalibrationArr));
        mBinding.tankLevelSequenceNumberTie.setAdapter(getAdapter(sensorSequenceNumber));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
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
            mBinding.tankLevelDeleteLayoutIsc.setVisibility(View.INVISIBLE);
            mBinding.tankLevelSaveTxtIsc.setText("ADD");
        }
    }

    private boolean validField() {
        if (isEmpty(mBinding.tankLevelInputNumberTie)) {
            mAppClass.showSnackBar(getContext(), "InputNumber cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tankLevelInputSensorLabelTie)) {
            mAppClass.showSnackBar(getContext(), "InputLabel cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tankLevelInputSensorOpenMessageTie)) {
            mAppClass.showSnackBar(getContext(), "Open message cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tankLevelInputSensorCloseMessageTie)) {
            mAppClass.showSnackBar(getContext(), "Close message cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tankLevelInputSensorTotalTimeTie)) {
            mAppClass.showSnackBar(getContext(), "Total time cannot be Empty");
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

    public void tankLevelEntity(int flagValue) {
        switch (flagValue) {
            case 2:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.tankLevelInputNumberTie)), "0", 0, "0", "0", "0", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.tankLevelInputNumberTie)),
                                mBinding.tankLevelInputSensorTypeTie.getText().toString(),
                                0, toString(0, mBinding.tankLevelInputSensorLabelTie),
                                "N/A",
                                "N/A", 1);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }

    }

    void sensorSequenceNumber() {
        if (Integer.parseInt(inputNumber) > 13 && Integer.parseInt(inputNumber) < 21) {
            mBinding.tankLevelSequenceNumber.setVisibility(View.VISIBLE);
            if (!mBinding.tankLevelSequenceNumberTie.getText().toString().isEmpty()) {
                sensorSequence = getPosition(1, toString(mBinding.tankLevelSequenceNumberTie), sensorSequenceNumber);
            }
        } else {
            mBinding.tankLevelSequenceNumber.setVisibility(View.GONE);
            sensorSequence = "0";
        }
    }
}
