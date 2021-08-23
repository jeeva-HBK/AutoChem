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
import com.ionexchange.databinding.FragmentInputsensorPhBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.bufferArr;
import static com.ionexchange.Others.ApplicationClass.findDecimal;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.tempLinkedArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.INPUT_SENSOR_CONFIG;
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
    private static final String TAG = "FragmentInputSensor";

    String inputNumber;
    String sensorName;
    int sensorStatus;

    public FragmentInputSensorPh_Config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
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
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
        switch (userType) {
            case 1:
                mBinding.pHTemperatureSensorLinked.setVisibility(View.GONE);
                mBinding.phRow5Isc.setVisibility(View.GONE);
                mBinding.phRow6Isc.setVisibility(View.GONE);

                // View Access only For
                mBinding.pHInputNumber.setEnabled(false);
                mBinding.pHInputNumber.setFocusableInTouchMode(false);

                mBinding.pHInputLabel.setEnabled(false);
                mBinding.pHInputLabel.setFocusableInTouchMode(false);

                mBinding.pHSensorType.setEnabled(false);
                mBinding.pHSensorType.setFocusableInTouchMode(false);

                mBinding.pHBufferType.setEnabled(false);
                mBinding.pHBufferType.setFocusableInTouchMode(false);

                mBinding.pHCalibrationRequiredAlarm.setEnabled(false);
                mBinding.pHCalibrationRequiredAlarm.setFocusableInTouchMode(false);

                mBinding.pHLowAlarm.setEnabled(false);
                mBinding.pHLowAlarm.setFocusableInTouchMode(false);

                mBinding.pHHighAlarm.setEnabled(false);
                mBinding.pHHighAlarm.setFocusableInTouchMode(false);

                mBinding.pHDefaultTemperatureValue.setEnabled(false);
                mBinding.pHDefaultTemperatureValue.setFocusableInTouchMode(false);

                mBinding.pHResetCalibration.setEnabled(false);
                mBinding.pHResetCalibration.setFocusableInTouchMode(false);

                break;

            case 2:
                mBinding.pHInputNumber.setEnabled(false);
                mBinding.pHInputNumber.setFocusableInTouchMode(false);

                mBinding.pHSensorType.setEnabled(false);
                mBinding.pHSensorType.setFocusableInTouchMode(false);

                mBinding.pHTemperatureSensorLinked.setEnabled(false);
                mBinding.pHTemperatureSensorLinked.setFocusableInTouchMode(false);

                mBinding.pHSmoothingFactor.setEnabled(false);
                mBinding.pHSmoothingFactor.setFocusableInTouchMode(false);

                mBinding.pHSensorActivation.setVisibility(View.GONE);
                mBinding.DeleteLayoutInputSettings.setVisibility(View.GONE);
                break;

            case 3:

                break;
        }

        initSensor(inputNumber);

        mBinding.saveLayoutInputSettings.setOnClickListener(this::save);
        mBinding.saveFabInputSettings.setOnClickListener(this::save);

        mBinding.DeleteLayoutInputSettings.setOnClickListener(this::delete);
        mBinding.DeleteFabInputSettings.setOnClickListener(this::delete);

        mBinding.backArrow.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
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

        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.inputNumberInputSettingsEDT) + SPILT_CHAR +
                getPosition(2, toString(mBinding.sensorInputSettingsATXT), inputTypeArr) + SPILT_CHAR +
                getPosition(1, toString(mBinding.sensorActivationInputSettingsATXT), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.inputLabelInputSettingsEdt) + SPILT_CHAR +
                getPosition(1, toString(mBinding.bufferTypeInputSettingATXT), bufferArr) + SPILT_CHAR +
                getPosition(1, toString(mBinding.tempLinkedInputSettingATXT), tempLinkedArr) + SPILT_CHAR +
                toString(2, mBinding.temperatureInputSettingEDT) + SPILT_CHAR +
                toString(3, mBinding.smoothingFactorInputSettingEDT) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.alarmLowInputSettingEDT) + SPILT_CHAR +
                toStringSplit(4, 2, mBinding.alarmhighInputSettingEDT) + SPILT_CHAR +
                toString(3, mBinding.calibrationRequiredInputSettingATXT) + SPILT_CHAR +
                getPosition(1, toString(mBinding.resetCalibrationInputSettingEDT), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus);
    }

    private boolean validField() {
        if (mBinding.calibrationRequiredInputSettingATXT.getText().toString().isEmpty()) {
            mBinding.calibrationRequiredInputSettingATXT.setText("0");
        }
        if (isEmpty(mBinding.inputNumberInputSettingsEDT)) {
            mAppClass.showSnackBar(getContext(), "Input Number cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorInputSettingsATXT)) {
            mAppClass.showSnackBar(getContext(), "Sensor Type cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.inputLabelInputSettingsEdt)) {
            mAppClass.showSnackBar(getContext(), "Input Label cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.bufferTypeInputSettingATXT)) {
            mAppClass.showSnackBar(getContext(), "Choose any mode of Buffer Type");
            return false;
        } else if (Integer.parseInt(mBinding.calibrationRequiredInputSettingATXT.getText().toString()) > 366) {
            mAppClass.showSnackBar(getContext(), "Calibration Required Alarm should be less than 366");
            return false;
        } else if (isEmpty(mBinding.alarmLowInputSettingEDT)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmhighInputSettingEDT)) {
            mAppClass.showSnackBar(getContext(), "Alarm high cannot be Empty");
            return false;
        } else if ((!mBinding.alarmLowInputSettingEDT.getText().toString().contains(".") && mBinding.alarmLowInputSettingEDT.getText().toString().length() > 4)
                || (mBinding.alarmLowInputSettingEDT.getText().toString().contains(".") && findDecimal(mBinding.alarmLowInputSettingEDT) == 1)) {
            mAppClass.showSnackBar(getContext(), "Alarm low decimal format like XXXX.XX");
            return false;
        } else if ((!mBinding.alarmhighInputSettingEDT.getText().toString().contains(".") && mBinding.alarmhighInputSettingEDT.getText().toString().length() > 4)
                || (mBinding.alarmhighInputSettingEDT.getText().toString().contains(".") && findDecimal(mBinding.alarmhighInputSettingEDT) == 1)) {
            mAppClass.showSnackBar(getContext(), "Alarm high decimal format like XXXX.XX");
            return false;
        } else if (isEmpty(mBinding.tempLinkedInputSettingATXT)) {
            mAppClass.showSnackBar(getContext(), "Select Temperature Sensor Linked value");
            return false;
        } else if (isEmpty(mBinding.temperatureInputSettingEDT)) {
            mAppClass.showSnackBar(getContext(), "Temperature value cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.resetCalibrationInputSettingEDT)) {
            mAppClass.showSnackBar(getContext(), "Select Reset Calibration value");
            return false;
        } else if (isEmpty(mBinding.smoothingFactorInputSettingEDT)) {
            mAppClass.showSnackBar(getContext(), "Smoothing factor cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.smoothingFactorInputSettingEDT.getText().toString()) > 101) {
            mAppClass.showSnackBar(getContext(), "Smoothing factor should be less than 101");
            return false;
        } else if (isEmpty(mBinding.sensorActivationInputSettingsATXT)) {
            mAppClass.showSnackBar(getContext(), "Select Sensor Activation value");
            return false;
        }
        return true;
    }

    private String toStringSplit(int digits, int digitPoint, EditText editText) {
        if (editText.getText().toString().split("\\.").length == 1) {
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
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

    private void initSensor(String inputNo) {
        mBinding.sensorActivationInputSettingsATXT.setAdapter(getAdapter(sensorActivationArr));
        mBinding.sensorInputSettingsATXT.setAdapter(getAdapter(inputTypeArr));
        mBinding.bufferTypeInputSettingATXT.setAdapter(getAdapter(bufferArr));
        mBinding.tempLinkedInputSettingATXT.setAdapter(getAdapter(tempLinkedArr));
        mBinding.resetCalibrationInputSettingEDT.setAdapter(getAdapter(resetCalibrationArr));
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
            handleResponce(data.split("\\*")[1].split("#"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "01");
        } else {
            mBinding.inputNumberInputSettingsEDT.setText(inputNumber);
            mBinding.sensorInputSettingsATXT.setText(sensorName);
            mBinding.DeleteLayoutInputSettings.setVisibility(View.INVISIBLE);
            mBinding.saveTxt.setText("ADD");
        }

    }

    private void handleResponce(String[] splitData) {
        mActivity.dismissProgress();
        // READ_RES - {* 1# 04# 0# 01# 0# 0# PHSensor# 0# 1# 33# 10# 400# 1300# 10# 0 *}
        if (splitData[1].equals("04")) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.inputNumberInputSettingsEDT.setText(splitData[3]);
                    // FIXME: 23-07-2021 AutoCompleteTextViewAdapter
                    mBinding.sensorInputSettingsATXT.setText(mBinding.sensorInputSettingsATXT.getAdapter().getItem(Integer.parseInt(splitData[4])).toString());
                    mBinding.sensorInputSettingsATXT.setAdapter(getAdapter(inputTypeArr));

                    mBinding.sensorActivationInputSettingsATXT.setText(mBinding.sensorActivationInputSettingsATXT.getAdapter().getItem(Integer.parseInt(splitData[5])).toString());
                    mBinding.sensorActivationInputSettingsATXT.setAdapter(getAdapter(sensorActivationArr));

                    mBinding.inputLabelInputSettingsEdt.setText(splitData[6]);

                    mBinding.bufferTypeInputSettingATXT.setText(mBinding.bufferTypeInputSettingATXT.getAdapter().getItem(Integer.parseInt(splitData[7])).toString());
                    mBinding.bufferTypeInputSettingATXT.setAdapter(getAdapter(bufferArr));

                    mBinding.tempLinkedInputSettingATXT.setText(mBinding.tempLinkedInputSettingATXT.getAdapter().getItem(Integer.parseInt(splitData[8])).toString());
                    mBinding.tempLinkedInputSettingATXT.setAdapter(getAdapter(tempLinkedArr));

                    mBinding.temperatureInputSettingEDT.setText(splitData[9]);
                    mBinding.smoothingFactorInputSettingEDT.setText(splitData[10]);
                    mBinding.alarmLowInputSettingEDT.setText(splitData[11].substring(0, 4) + "." + splitData[11].substring(4, 6));
                    mBinding.alarmhighInputSettingEDT.setText(splitData[12].substring(0, 4) + "." + splitData[12].substring(4, 6));

                    mBinding.calibrationRequiredInputSettingATXT.setText(splitData[13]);

                    mBinding.resetCalibrationInputSettingEDT.setText(mBinding.resetCalibrationInputSettingEDT.getAdapter().getItem(Integer.parseInt(splitData[14])).toString());
                    mBinding.resetCalibrationInputSettingEDT.setAdapter(getAdapter(resetCalibrationArr));
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.readFailed));
                }

            } else if (splitData[0].equals(WRITE_PACKET)) {

                if (splitData[2].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                    pHEntity(1);
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), getString(R.string.wrongPack));
        }


    }

    public void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void pHEntity(int flagValue) {
        switch (flagValue) {
            case 0:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.inputNumberInputSettingsEDT)),
                                "0", 0, "0", "0", "0", flagValue);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                break;

            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.inputNumberInputSettingsEDT)),
                                mBinding.sensorInputSettingsATXT.getText().toString(),
                                0, toString(0, mBinding.inputLabelInputSettingsEdt),
                                toStringSplit(4, 2, mBinding.alarmLowInputSettingEDT),
                                toStringSplit(4, 2, mBinding.alarmhighInputSettingEDT), flagValue);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }

    }
}
