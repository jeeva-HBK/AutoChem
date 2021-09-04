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
import com.ionexchange.databinding.FragmentInputsensorOrpBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorORP_Config extends Fragment implements DataReceiveCallback {
    FragmentInputsensorOrpBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    String inputNumber;
    String sensorName;
    int sensorStatus;
    WaterTreatmentDb db;
    InputConfigurationDao dao;

    public FragmentInputSensorORP_Config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorORP_Config(String inputNumber, String sensorName, int sensorStatus) {
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
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();

        switch (userType) {

            case 1:
                mBinding.orpRow4.setVisibility(View.GONE);
                mBinding.orpRow5.setVisibility(View.GONE);

                // Only View Access for
                mBinding.orpInputNumber.setEnabled(false);
                mBinding.orpInputLabel.setEnabled(false);
                mBinding.orpSensorType.setEnabled(false);
                mBinding.orpAlarmLow.setEnabled(false);
                mBinding.orpAlarmHigh.setEnabled(false);
                mBinding.orpCalibrationAlarmRequired.setEnabled(false);
                mBinding.orpResetCalibration.setEnabled(false);
                break;

            case 2:
                mBinding.orpSmoothingFactor.setEnabled(false);

                mBinding.orpSensorAct.setVisibility(View.GONE);
                mBinding.orpDeleteLayoutInputSettings.setVisibility(View.GONE);
                break;

            case 3:


                break;
        }

        initAdapter();


        mBinding.orpsaveLayoutInputSettings.setOnClickListener(this::save);
        mBinding.orpsaveFabInputSettings.setOnClickListener(this::save);

        mBinding.orpDeleteFabInputSettings.setOnClickListener(this::delete);
        mBinding.orpDeleteFabInputSettings.setOnClickListener(this::delete);
        mBinding.backArrow.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
    }

    private void delete(View view) {
        sendData(2);
    }

    private void save(View view) {
        if (validation()) {
            sendData(sensorStatus);
        }
    }

    void sendData(int sensorStatus) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.orpInputNumberInputSettingsEDT) + SPILT_CHAR +
                getPosition(2, toString(mBinding.orpSensorTypeEDT), inputTypeArr) + SPILT_CHAR +
                getPosition(1, toString(mBinding.orpSensorActISEDT), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.orpInputLabelISEDT) + SPILT_CHAR +
                toString(3, mBinding.orpSmoothingFactorISEDT) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.orpalarmLowISEDT) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.orpalarmHighISEDT) + SPILT_CHAR +
                toString(3, mBinding.orpCalibrationAlarmRequiredISEDT) + SPILT_CHAR +
                getPosition(1, toString(mBinding.orpResetCalibrationISEDT), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus
        );
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
        mBinding.orpSensorActISEDT.setAdapter(getAdapter(sensorActivationArr));
        mBinding.orpSensorTypeEDT.setAdapter(getAdapter(inputTypeArr));
        mBinding.orpResetCalibrationISEDT.setAdapter(getAdapter(resetCalibrationArr));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + "02");
        } else {
            mBinding.orpInputNumberInputSettingsEDT.setText(inputNumber);
            mBinding.orpSensorTypeEDT.setText(sensorName);
            mBinding.orpDeleteLayoutInputSettings.setVisibility(View.INVISIBLE);
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
        // Read - Res - 1# 04# 0# | 02# 1# 1# ORP# 10# 500# 1000# 20# 0
        // Write - Res -
        if (data[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (data[0].equals(READ_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {

                    mBinding.orpInputNumberInputSettingsEDT.setText(data[3]);

                    mBinding.orpSensorTypeEDT.setText(mBinding.orpSensorTypeEDT.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    mBinding.orpSensorActISEDT.setText(mBinding.orpSensorActISEDT.getAdapter().getItem(Integer.parseInt(data[5])).toString());

                    mBinding.orpInputLabelISEDT.setText(data[6]);
                    mBinding.orpSmoothingFactorISEDT.setText(data[7]);
                    mBinding.orpalarmLowISEDT.setText(data[8].substring(0, 4) + "." + data[8].substring(4, 6));
                    mBinding.orpalarmHighISEDT.setText(data[9].substring(0, 4) + "." + data[9].substring(4, 6));
                    mBinding.orpCalibrationAlarmRequiredISEDT.setText(data[10]);

                    mBinding.orpResetCalibrationISEDT.setText(mBinding.orpResetCalibrationISEDT.getAdapter().getItem(Integer.parseInt(data[11])).toString());

                    initAdapter();
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
            } else if (data[0].equals(WRITE_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {
                    orpEntity(1);
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), "Received Wrong Pack !");
        }

    }

    boolean validation() {
        if (isEmpty(mBinding.orpSmoothingFactorISEDT)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.orpInputNumberInputSettingsEDT)) {
            mAppClass.showSnackBar(getContext(), "Input Label Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.orpCalibrationAlarmRequiredISEDT)) {
            mAppClass.showSnackBar(getContext(), "Calibration Alarm Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.orpalarmLowISEDT)) {
            mAppClass.showSnackBar(getContext(), "Alarm Low Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.orpalarmHighISEDT)) {
            mAppClass.showSnackBar(getContext(), "Alarm High Factor Cannot be Empty");
            return false;
        } else if (mBinding.orpalarmLowISEDT.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm low is decimal format");
            return false;
        } else if (mBinding.orpalarmHighISEDT.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm High is decimal format");
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

    public void orpEntity(int flagValue) {
        switch (flagValue) {
            case 0:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.orpInputNumberInputSettingsEDT)), "0", 0, "0", "0", "0", flagValue);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                break;

            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.orpInputNumberInputSettingsEDT)),
                                mBinding.orpSensorTypeEDT.getText().toString(),
                                0, toString(0, mBinding.orpInputLabelISEDT),
                                toStringSplit(4, 2, mBinding.orpalarmLowISEDT),
                                toStringSplit(4, 2, mBinding.orpalarmHighISEDT), flagValue);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }

    }
}