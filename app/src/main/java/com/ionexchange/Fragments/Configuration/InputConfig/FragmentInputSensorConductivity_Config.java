package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsensorCondBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.TemperatureCompensationType;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.tempLinkedArr;
import static com.ionexchange.Others.ApplicationClass.unitArr;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorConductivity_Config extends Fragment implements DataReceiveCallback {
    private static final String TAG = "FragmentInputSensorCond";
    FragmentInputsensorCondBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    int sensorStatus;
    String inputNumber;
    String sensorName;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    int userManagement;


    public FragmentInputSensorConductivity_Config(String inputNumber, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorStatus = sensorStatus;
    }

    public FragmentInputSensorConductivity_Config(String inputNumber, String sensorName, int sensorStatus) {
        this.inputNumber = inputNumber;
        this.sensorName = sensorName;
        this.sensorStatus = sensorStatus;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_cond, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
        userManagement = 3;
        userManagement();
        initAdapters();
        mBinding.conSaveFabIsc.setOnClickListener(this::save);
        mBinding.conDeleteFabIsc.setOnClickListener(this::delete);

        mBinding.conBackArrowIsc.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
    }

    private void delete(View view) {
        if (validation()) {
            if (getPosition(1, toString(mBinding.conCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
                sendDataLinearTemperature(2);
            } else {
                sendStandardNaClTemperature(2);
            }
        }
    }

    private void save(View view) {
        if (validation()) {
            if (getPosition(1, toString(mBinding.conCompensationAtxtIsc), TemperatureCompensationType).equals("0")) {
                sendDataLinearTemperature(sensorStatus);
            } else {
                sendStandardNaClTemperature(sensorStatus);
            }
        }
    }

    void sendDataLinearTemperature(int sensorStatus) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.conInputNumberEdtIsc) + SPILT_CHAR +
                getPosition(2, toString(mBinding.conSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                getPosition(2, toString(mBinding.conSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.conInputLabelEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.conTempLinkedAtxtIsc), tempLinkedArr) + SPILT_CHAR +
                getPlusMinusValue(mBinding.conDefaultTempValueTBtn, mBinding.conDefaultTemperatureEdtIsc, 3, mBinding.conDefaultTempDeciIsc, 2) + SPILT_CHAR +
                getPosition(1, toString(mBinding.conUnitOfMeasureAxtIsc), unitArr) + SPILT_CHAR +
                toString(4, mBinding.conCellConstantEdtIsc) + SPILT_CHAR +
                getPosition(0, toString(mBinding.conCompensationAtxtIsc), TemperatureCompensationType) + SPILT_CHAR +
                toString(4, mBinding.conTempCompFacEdtIsc) + SPILT_CHAR +
                toString(3, mBinding.conSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.conAlarmLowEdtIsc, 6, mBinding.conAlarmLowDeciIsc, 2) +
                getDecimalValue(mBinding.conAlarmhighEdtIsc, 6, mBinding.conHighAlarmDeciIsc, 2) + SPILT_CHAR +
                toString(3, mBinding.conCalibRequiredAlarmEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.conResetCalibAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus
        );
    }

    String getDecimalValue(TextInputEditText prefixEdittext, int prefixDigit, EditText suffixEdittext, int suffixDigit) {
        return toString(prefixDigit, prefixEdittext) + "." + toString(suffixDigit, suffixEdittext);
    }

    String getPlusMinusValue(ToggleButton toggleButton, TextInputEditText prefixEdt, int prefixDigit, EditText suffixEdt, int suffixDigit) {
        return (toggleButton.isChecked() ? "+" : "-") + toString(prefixDigit, prefixEdt) + "." + toString(suffixDigit, suffixEdt);
    }

    void sendStandardNaClTemperature(int sensorStatus) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                toString(2, mBinding.conInputNumberEdtIsc) + SPILT_CHAR +
                getPosition(2, toString(mBinding.conSensorTypeAtxtIsc), inputTypeArr) + SPILT_CHAR +
                getPosition(2, toString(mBinding.conSensorActivationAtxtIsc), sensorActivationArr) + SPILT_CHAR +
                toString(0, mBinding.conInputLabelEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.conTempLinkedAtxtIsc), tempLinkedArr) + SPILT_CHAR +
                getPlusMinusValue(mBinding.conDefaultTempValueTBtn, mBinding.conDefaultTemperatureEdtIsc, 3, mBinding.conDefaultTempDeciIsc, 2) + SPILT_CHAR +
                getPosition(1, toString(mBinding.conUnitOfMeasureAxtIsc), unitArr) + SPILT_CHAR +
                toString(4, mBinding.conCellConstantEdtIsc) + SPILT_CHAR +
                getPosition(0, toString(mBinding.conCompensationAtxtIsc), TemperatureCompensationType) + SPILT_CHAR +
                toString(3, mBinding.conSmoothingFactorEdtIsc) + SPILT_CHAR +
                getDecimalValue(mBinding.conAlarmLowEdtIsc, 6, mBinding.conAlarmLowDeciIsc, 2) + SPILT_CHAR +
                getDecimalValue(mBinding.conAlarmhighEdtIsc,6, mBinding.conHighAlarmDeciIsc, 2) + SPILT_CHAR +
                toString(3, mBinding.conCalibRequiredAlarmEdtIsc) + SPILT_CHAR +
                getPosition(1, toString(mBinding.conResetCalibAtxtIsc), resetCalibrationArr) + SPILT_CHAR +
                sensorStatus
        );
    }

    private String toStringSplit(int digits, int digitPoint, EditText editText) {
        if (editText.getText().toString().split("\\.").length == 1) {
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
    }

    boolean validation() {
       /* if (isEmpty(mBinding.inputLabelCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Input Label Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempValueCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Temperature Value Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.cellConstantCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Cell Constant Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tempCompFacCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Temperature Compensation Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.smoothingFactorCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Smoothing Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.calibRequiredAlarmCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Calibration Required Alarm Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmLowCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm Low Factor Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.alarmHighCondISEdt)) {
            mAppClass.showSnackBar(getContext(), "Alarm High Factor Cannot be Empty");
            return false;
        } else if (mBinding.alarmLowCondISEdt.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm low is decimal format");
            return false;
        } else if (mBinding.alarmHighCondISEdt.getText().toString().matches(".")) {
            mAppClass.showSnackBar(getContext(), "Alarm High is decimal format");
            return false;
        }*/
        return true;
    }

    private Boolean isEmpty(EditText editText) {
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

    private void initAdapters() {
        mBinding.conSensorTypeAtxtIsc.setAdapter(getAdapter(inputTypeArr));
        mBinding.conSensorActivationAtxtIsc.setAdapter(getAdapter(sensorActivationArr));
        mBinding.conTempLinkedAtxtIsc.setAdapter(getAdapter(tempLinkedArr));
        mBinding.conUnitOfMeasureAxtIsc.setAdapter(getAdapter(unitArr));
        mBinding.conResetCalibAtxtIsc.setAdapter(getAdapter(resetCalibrationArr));
        mBinding.conCompensationAtxtIsc.setAdapter(getAdapter(TemperatureCompensationType));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + "03");
        } else {
            mBinding.conInputNumberEdtIsc.setText(inputNumber);
            mBinding.conSensorTypeAtxtIsc.setText(sensorName);
            mBinding.conDeleteLayoutIsc.setVisibility(View.INVISIBLE);
            mBinding.conSaveTxtIsc.setText("ADD");
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

    private void handleResponse(String[] spiltData) {
      /*  // read Res - {*1# 04# 0# | 03# 04# 0# CONCON# 1# 33# 1# 2345# 2371# 1500# 100# 120000# 220000# 300# 1*}
        // write Res -
        if (spiltData[1].equals(PCK_INPUT_SENSOR_CONFIG)) {

            if (spiltData[0].equals(READ_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mBinding.inputNumberCondISEDT.setText(spiltData[3]);
                    mBinding.sensorTypeCondISATXT.setText(mBinding.sensorTypeCondISATXT.getAdapter().getItem(Integer.parseInt(spiltData[4])).toString());
                    mBinding.sensorActivationCondISATXT.setText(mBinding.sensorActivationCondISATXT.getAdapter().getItem(Integer.parseInt(spiltData[5])).toString());
                    mBinding.inputLabelCondISEdt.setText(spiltData[6]);
                    mBinding.tempLinkedCondISEdt.setText(mBinding.tempLinkedCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[7])).toString());
                    mBinding.tempValueCondISEdt.setText(spiltData[8]);
                    mBinding.unitOfMeasureCondISEdt.setText(mBinding.unitOfMeasureCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[9])).toString());
                    mBinding.cellConstantCondISEdt.setText(spiltData[10]);
                    mBinding.tempCompensationAct.setText(mBinding.tempCompensationAct.getAdapter().getItem(Integer.parseInt(spiltData[11])).toString());
                    if (spiltData[11].equals("0")) {
                        mBinding.tempCompFacCondISEdt.setText(spiltData[12]);
                        mBinding.smoothingFactorCondISEdt.setText(spiltData[13]);
                        mBinding.alarmLowCondISEdt.setText(spiltData[14].substring(0, 4) + "." + spiltData[14].substring(4, 6));
                        mBinding.alarmHighCondISEdt.setText(spiltData[15].substring(0, 4) + "." + spiltData[15].substring(4, 6));
                        mBinding.calibRequiredAlarmCondISEdt.setText(spiltData[16]);
                        mBinding.resetCalibCondISEdt.setText(mBinding.resetCalibCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[17])).toString());
                    } else {
                        mBinding.smoothingFactorCondISEdt.setText(spiltData[12]);
                        mBinding.alarmLowCondISEdt.setText(spiltData[13].substring(0, 4) + "." + spiltData[13].substring(4, 6));
                        mBinding.alarmHighCondISEdt.setText(spiltData[14].substring(0, 4) + "." + spiltData[14].substring(4, 6));
                        mBinding.calibRequiredAlarmCondISEdt.setText(spiltData[15]);
                        mBinding.resetCalibCondISEdt.setText(mBinding.resetCalibCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[16])).toString());
                    }
                    initAdapters();
                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
            } else if (spiltData[0].equals(WRITE_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    conductivityEntity(1);
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }

        } else {
            Log.e(TAG, "handleResponse: Wrong Packet");
        }
*/
    }


    public void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void conductivityEntity(int flagValue) {
      /*  switch (flagValue) {
            case 0:
                InputConfigurationEntity entityDelete = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.inputNumberCondISEDT)), "0", 0, "0", "0", "0", flagValue);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                break;

            case 1:
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (Integer.parseInt(toString(2, mBinding.inputNumberCondISEDT)),
                                mBinding.sensorTypeCondISATXT.getText().toString(),
                                0, toString(0, mBinding.inputLabelCondISEdt),
                                toStringSplit(4, 2, mBinding.alarmLowCondISEdt),
                                toStringSplit(4, 2, mBinding.alarmHighCondISEdt), flagValue);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }*/

    }

    void userManagement() {
      /*  switch (userManagement) {
            case 1:
                mBinding.inputLabelInputSettings.setEnabled(false);
                mBinding.tempLinkedInputSettings.setEnabled(false);
                mBinding.tempValueInputSettings.setEnabled(false);
                mBinding.unitOfMeasureInputSettings.setEnabled(false);
                mBinding.cellConstantInputSettings.setEnabled(false);
                mBinding.alarmLowInputSettings.setEnabled(false);
                mBinding.alarmHighInputSettings.setEnabled(false);
                mBinding.calibRequiredAlarmInputSettings.setEnabled(false);
                mBinding.resetCalibInputSettings.setEnabled(false);
                mBinding.sensorActivationInputSettings.setVisibility(View.GONE);
                mBinding.condRow5.setVisibility(View.GONE);
                mBinding.conRow7.setVisibility(View.GONE);
                break;

            case 2:

                //View
                mBinding.cellConstantInputSettings.setEnabled(false);
                mBinding.tempCompensationTil.setEnabled(false);
                mBinding.tempCompFacInputSettings.setEnabled(false);
                mBinding.smoothingFactorInputSettings.setEnabled(false);
                mBinding.sensorActivationInputSettings.setVisibility(View.GONE);
                mBinding.DeleteLayoutInputSettings.setVisibility(View.GONE);

                break;

            case 3:

                break;
        }*/
    }
}
