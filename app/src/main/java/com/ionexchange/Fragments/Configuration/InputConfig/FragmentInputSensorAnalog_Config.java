package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Entity.EventLogEntity;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.Others.EventLogDemo;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsensorAnalogBinding;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.analogInputArr;
import static com.ionexchange.Others.ApplicationClass.analogSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.analogUnitArr;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.getDecimalValue;
import static com.ionexchange.Others.ApplicationClass.getPositionFromAtxt;
import static com.ionexchange.Others.ApplicationClass.getStringValue;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.isFieldEmpty;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

//created by Silambu
public class FragmentInputSensorAnalog_Config extends Fragment implements DataReceiveCallback {

    FragmentInputsensorAnalogBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    String inputNumber;
    String sensorName,sequenceNo = "1",analog_type="(4 - 20mA)";
    int sensorStatus, sequenceType = 0, analogType = 0,sensorLength=2;
    WaterTreatmentDb db;
    InputConfigurationDao dao;

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
        changeUi();

        inputNumber = getArguments().getString("inputNumber");
        sensorName = getArguments().getString("sensorName");
        sensorStatus = getArguments().getInt("sensorStatus");
        sequenceNo = getArguments().getString("sequenceNo");
        sequenceType = getArguments().getInt("sequenceType");
        analogType = getArguments().getInt("sequenceValueRead");

        mBinding.analogSaveFabIsc.setOnClickListener(this::save);
        mBinding.analogDeleteFabIsc.setOnClickListener(this::delete);

        mBinding.backArrowIsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppClass.popStackBack(getActivity());
            }
        });

        mBinding.analogTypeTie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBinding.analogMinValueTie.setText("");
                mBinding.analogMaxValueTie.setText("");
                mBinding.analogAlarmLowTie.setText("");
                mBinding.analogHighLowTie.setText("");
                sensorLayoutVisibility(false);
                setMaxLength();
            }
        });
    }

    void setMaxLength(){
        switch (mBinding.analogTypeTie.getText().toString()){
            case "ORP":
                sensorLength = 4;
                sensorLayoutVisibility(true);
                break;
            case "Temperature":
                sensorLength = 3;
                sensorLayoutVisibility(true);
                break;
            case "Flow/Water Meter":
            case "Toroidal Conductivity":
                sensorLength = 7;
                break;
            case "Contacting Conductivity":
            case "Tank Level":
                sensorLength = 6;
                break;
            default:
                sensorLength = 2;
                break;
        }
        mBinding.analogMinValueTie.setFilters(new InputFilter[] { new InputFilter.LengthFilter(sensorLength) });
        mBinding.analogMaxValueTie.setFilters(new InputFilter[] { new InputFilter.LengthFilter(sensorLength) });
        mBinding.analogAlarmLowTie.setFilters(new InputFilter[] { new InputFilter.LengthFilter(sensorLength) });
        mBinding.analogHighLowTie.setFilters(new InputFilter[] { new InputFilter.LengthFilter(sensorLength) });
    }
    void sensorLayoutVisibility(boolean visibility){
        if(visibility){
            mBinding.analogLowAlarmTBtn.setVisibility(View.VISIBLE);
            mBinding.analogHighAlarmTBtn.setVisibility(View.VISIBLE);
            mBinding.analogminvalueTBtn.setVisibility(View.VISIBLE);
            mBinding.analogmaxvalueTBtn.setVisibility(View.VISIBLE);
        }else {
            mBinding.analogLowAlarmTBtn.setVisibility(View.GONE);
            mBinding.analogHighAlarmTBtn.setVisibility(View.GONE);
            mBinding.analogminvalueTBtn.setVisibility(View.GONE);
            mBinding.analogmaxvalueTBtn.setVisibility(View.GONE);
        }
    }
    void changeUi() {
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
                mBinding.analogLowAlarmTBtn.setEnabled(false);
                mBinding.analogHighAlarmTBtn.setEnabled(false);
                mBinding.analogminvalueTBtn.setEnabled(false);
                mBinding.analogmaxvalueTBtn.setEnabled(false);
                mBinding.lowAlarmMinValueIsc.setEnabled(false);
                mBinding.highAlarmMinValueIsc.setEnabled(false);
                mBinding.analogMinValueIsc.setEnabled(false);
                mBinding.analogMaxValueIsc.setEnabled(false);
                break;

            case 2:
                mBinding.analogInputNumber.setEnabled(false);
                mBinding.analogSensorType.setEnabled(false);
                mBinding.analogAnalogType.setEnabled(false);
                mBinding.analogUnit.setEnabled(false);
                mBinding.analogSmoothingFactor.setEnabled(false);
                mBinding.analogSensorActivation.setVisibility(View.GONE);
                mBinding.analogDeleteLayoutIsc.setVisibility(View.GONE);
                mBinding.analogCalibrationRequiredAlarmTie.setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;
        }
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
        if((getPositionFromAtxt(1, getStringValue(mBinding.analogTypeTie), analogInputArr).equals("2")) ||
                (getPositionFromAtxt(1, getStringValue(mBinding.analogTypeTie), analogInputArr).equals("3"))){
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                    PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                    getStringValue(2, mBinding.analogInputNumberTie) + SPILT_CHAR +
                    getPositionFromAtxt(2, getStringValue(mBinding.analogSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.analogTypeTie), analogInputArr) + SPILT_CHAR +
                    sequenceNo + SPILT_CHAR + analogType + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.analogSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                    getStringValue(0, mBinding.analogInputLabelTie) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.analogUnitMeasurementTie), analogUnitArr) + SPILT_CHAR +
                    getDecimalValue(mBinding.analogminvalueTBtn, mBinding.analogMinValueTie,sensorLength, mBinding.analogMinValueIsc,2) + SPILT_CHAR +
                    getDecimalValue(mBinding.analogmaxvalueTBtn,  mBinding.analogMaxValueTie,sensorLength, mBinding.analogMaxValueIsc,2) + SPILT_CHAR +
                    getStringValue(3, mBinding.analogSmoothingFactorTie) + SPILT_CHAR +
                    getDecimalValue(mBinding.analogLowAlarmTBtn, mBinding.analogAlarmLowTie,sensorLength, mBinding.lowAlarmMinValueIsc,2) + SPILT_CHAR +
                    getDecimalValue(mBinding.analogHighAlarmTBtn, mBinding.analogHighLowTie,sensorLength, mBinding.highAlarmMinValueIsc,2) + SPILT_CHAR +
                    getStringValue(3, mBinding.analogCalibrationRequiredAlarmTie) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.analogResetCalibrationTie), resetCalibrationArr) + SPILT_CHAR +
                    sensorStatus);
        } else {
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                    PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR +
                    getStringValue(2, mBinding.analogInputNumberTie) + SPILT_CHAR +
                    getPositionFromAtxt(2, getStringValue(mBinding.analogSensorTypeTie), inputTypeArr) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.analogTypeTie), analogInputArr) + SPILT_CHAR +
                    sequenceNo + SPILT_CHAR + analogType + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.analogSensorActivationTie), sensorActivationArr) + SPILT_CHAR +
                    getStringValue(0, mBinding.analogInputLabelTie) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.analogUnitMeasurementTie), analogUnitArr) + SPILT_CHAR +
                    getStringValue(sensorLength, mBinding.analogMinValueTie) + "." + getStringValue(2, mBinding.analogMinValueIsc) + SPILT_CHAR +
                    getStringValue(sensorLength, mBinding.analogMaxValueTie) + "." + getStringValue(2, mBinding.analogMaxValueIsc) + SPILT_CHAR +
                    getStringValue(3, mBinding.analogSmoothingFactorTie) + SPILT_CHAR +
                    getStringValue(sensorLength, mBinding.analogAlarmLowTie) + "." + getStringValue(2, mBinding.lowAlarmMinValueIsc) + SPILT_CHAR +
                    getStringValue(sensorLength, mBinding.analogHighLowTie) + "." + getStringValue(2, mBinding.highAlarmMinValueIsc) + SPILT_CHAR +
                    getStringValue(3, mBinding.analogCalibrationRequiredAlarmTie) + SPILT_CHAR +
                    getPositionFromAtxt(1, getStringValue(mBinding.analogResetCalibrationTie), resetCalibrationArr) + SPILT_CHAR +
                    sensorStatus);
        }
    }

    private void initAdapter() {
        mBinding.analogSensorTypeTie.setAdapter(getAdapter(inputTypeArr, getContext()));
        mBinding.analogSensorActivationTie.setAdapter(getAdapter(sensorActivationArr, getContext()));
        mBinding.analogTypeTie.setAdapter(getAdapter(analogInputArr, getContext()));
        mBinding.analogUnitMeasurementTie.setAdapter(getAdapter(analogUnitArr, getContext()));
        mBinding.analogResetCalibrationTie.setAdapter(getAdapter(resetCalibrationArr, getContext()));
        mBinding.analogSequenceNumberTie.setAdapter(getAdapter(analogSequenceNumber, getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorName == null) {
            mActivity.showProgress();
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE +
                    SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + inputNumber);
        } else {
            mBinding.analogInputNumberTie.setText(inputNumber);
            mBinding.analogSensorTypeTie.setText(sensorName);
            if(analogType == 1){
                analog_type = "(0 - 10V)";
            }
            mBinding.analogSequenceNumberTie.setText(mBinding.analogSequenceNumberTie.getAdapter().
                    getItem(Integer.parseInt(sequenceNo)).toString() + " "+analog_type);
            mBinding.analogDeleteLayoutIsc.setVisibility(View.GONE);
            mBinding.analogSaveTxtIsc.setText("ADD");
        }
    }

    @Override
    public void OnDataReceive(String data) {
        mActivity.dismissProgress();
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.timeout));
        }
        if (data != null) {
            handleResponse(data.split("\\*")[1].split(RES_SPILT_CHAR));
        }
    }

    private void handleResponse(String[] data) {
        if (data[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
            if (data[0].equals(READ_PACKET)) {
                if (data[2].equals(RES_SUCCESS)) {
                    mBinding.analogInputNumberTie.setText(data[3]);
                    mBinding.analogSensorTypeTie.setText(mBinding.analogSensorTypeTie.getAdapter().getItem(Integer.parseInt(data[4])).toString());
                    if(data[7].equals("1")){
                        analog_type = "(0 - 10V)";
                    }else {
                        analog_type = "(4 - 20mA)";
                    }
                    sequenceNo = data[6];
                    mBinding.analogSequenceNumberTie.setText(mBinding.analogSequenceNumberTie.getAdapter().getItem(Integer.parseInt(data[6])).toString()+ " "+analog_type);
                    mBinding.analogTypeTie.setText(mBinding.analogTypeTie.getAdapter().getItem(Integer.parseInt(data[5])).toString());
                    mBinding.analogSensorActivationTie.setText(mBinding.analogSensorActivationTie.getAdapter().getItem(Integer.parseInt(data[8])).toString());
                    mBinding.analogInputLabelTie.setText(data[9]);
                    mBinding.analogUnitMeasurementTie.setText(mBinding.analogUnitMeasurementTie.getAdapter().getItem(Integer.parseInt(data[10])).toString());
                    setMaxLength();
                    if(data[5].equals("2") || data[5].equals("3")){
                        mBinding.analogminvalueTBtn.setChecked((data[11].substring(0, 1)).equals("+"));
                        mBinding.analogMinValueTie.setText(data[11].substring(1, sensorLength+1));
                        mBinding.analogMinValueIsc.setText(data[11].substring(sensorLength+2, sensorLength+4));
                        mBinding.analogmaxvalueTBtn.setChecked((data[12].substring(0, 1)).equals("+"));
                        mBinding.analogMaxValueTie.setText(data[12].substring(1, sensorLength+1));
                        mBinding.analogMaxValueIsc.setText(data[12].substring(sensorLength+2, sensorLength+4));
                        mBinding.analogLowAlarmTBtn.setChecked((data[14].substring(0, 1)).equals("+"));
                        mBinding.analogAlarmLowTie.setText(data[14].substring(1, sensorLength+1));
                        mBinding.lowAlarmMinValueIsc.setText(data[14].substring(sensorLength+2, sensorLength+4));
                        mBinding.analogHighAlarmTBtn.setChecked((data[15].substring(0, 1)).equals("+"));
                        mBinding.analogHighLowTie.setText(data[15].substring(1, sensorLength+1));
                        mBinding.highAlarmMinValueIsc.setText(data[15].substring(sensorLength+2, sensorLength+4));
                    }else{
                        mBinding.analogMinValueTie.setText(data[11].substring(0, sensorLength));
                        mBinding.analogMinValueIsc.setText(data[11].substring(sensorLength+1, sensorLength+3));
                        mBinding.analogMaxValueTie.setText(data[12].substring(0, sensorLength));
                        mBinding.analogMaxValueIsc.setText(data[12].substring(sensorLength+1, sensorLength+3));
                        mBinding.analogAlarmLowTie.setText(data[14].substring(0, sensorLength));
                        mBinding.lowAlarmMinValueIsc.setText(data[14].substring(sensorLength+1, sensorLength+3));
                        mBinding.analogHighLowTie.setText(data[15].substring(0, sensorLength));
                        mBinding.highAlarmMinValueIsc.setText(data[15].substring(sensorLength+1,sensorLength+3));
                    }

                    mBinding.analogSmoothingFactorTie.setText(data[13]);

                    mBinding.analogCalibrationRequiredAlarmTie.setText(data[16]);
                    mBinding.analogResetCalibrationTie.setText(mBinding.analogResetCalibrationTie.getAdapter().getItem(Integer.parseInt(data[17])).toString());
                    initAdapter();
                } else if (data[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.readFailed));
                }
            } else if (data[0].equals(WRITE_PACKET)) {
                if (data[3].equals(RES_SUCCESS)) {
                    analogEntity(Integer.parseInt(data[2]));
                    new EventLogDemo(inputNumber,"Analog","Input Setting Changed",getContext());
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
        if (isFieldEmpty(mBinding.analogInputLabelTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.input_name_validation));
            return false;
        } else if (isFieldEmpty(mBinding.analogTypeTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.analogtype_validation));
            return false;
        } else if (isFieldEmpty(mBinding.analogUnitMeasurementTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.unit_validation));
            return false;
        } else if (isFieldEmpty(mBinding.analogMinValueTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.min_validation));
            return false;
        } else if (isFieldEmpty(mBinding.analogMaxValueTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.max_validation));
            return false;
        } else if (isFieldEmpty(mBinding.analogAlarmLowTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_low_validation));
            return false;
        } else if (isFieldEmpty(mBinding.analogHighLowTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_high_validation));
            return false;
        } else if (isFieldEmpty(mBinding.analogCalibrationRequiredAlarmTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.calibration_alarm_vali));
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.analogCalibrationRequiredAlarmTie)) > 365) {
            mBinding.analogCalibrationRequiredAlarmTie.setError(getString(R.string.calibration_alarm_validation));
            return false;
        } else if (isFieldEmpty(mBinding.analogSmoothingFactorTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.smoothing_factor_validation));
            return false;
        } else if (Integer.parseInt(getStringValue(3, mBinding.analogSmoothingFactorTie)) > 90) {
            mBinding.analogSmoothingFactorTie.setError(getString(R.string.smoothing_factor_vali));
            return false;
        } else if (isFieldEmpty(mBinding.analogResetCalibrationTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.reset_calibration_validation));
            return false;
        } else if (isFieldEmpty(mBinding.analogSensorActivationTie)) {
            mAppClass.showSnackBar(getContext(), getString(R.string.sensor_activation_validation));
            return false;
        }
        /*else if (Float.parseFloat(getDecimalValue(mBinding.analogAlarmLowTie, 2, mBinding.lowAlarmMinValueIsc, 2)) >=
                Float.parseFloat(getDecimalValue(mBinding.analogHighLowTie, 2, mBinding.highAlarmMinValueIsc, 2))) {
            mAppClass.showSnackBar(getContext(), getString(R.string.alarm_limit_validation));
            return false;
        } else if (Float.parseFloat(getDecimalValue(mBinding.analogMinValueTie, 2, mBinding.analogMinValueIsc, 2)) >=
                Float.parseFloat(getDecimalValue(mBinding.analogMaxValueTie, 2, mBinding.analogMaxValueIsc, 2))) {
            mAppClass.showSnackBar(getContext(), getString(R.string.maxmin_validation));
            return false;
        }*/
        return true;
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
                        (Integer.parseInt(getStringValue(2, mBinding.analogInputNumberTie)), "N/A",
                                "Analog", 0, "N/A",
                                1, "N/A", "N/A", "N/A", "N/A", "N/A", 0);
                List<InputConfigurationEntity> entryListDelete = new ArrayList<>();
                entryListDelete.add(entityDelete);
                updateToDb(entryListDelete);
                mBinding.backArrowIsc.performClick();
                break;

            case 0:
            case 1:
                InputConfigurationEntity entityUpdate;
                if((getPositionFromAtxt(1, getStringValue(mBinding.analogTypeTie), analogInputArr).equals("2")) ||
                        (getPositionFromAtxt(1, getStringValue(mBinding.analogTypeTie), analogInputArr).equals("3"))){
                    entityUpdate = new InputConfigurationEntity
                            (Integer.parseInt(getStringValue(2, mBinding.analogInputNumberTie)),
                                    mBinding.analogSensorTypeTie.getText().toString(), "Analog", 0,
                                    mBinding.analogSequenceNumberTie.getText().toString(),
                                    Integer.parseInt(sequenceNo), getStringValue(0, mBinding.analogInputLabelTie),
                                    (mBinding.analogLowAlarmTBtn.isChecked() ? "+" : "-") + mBinding.analogAlarmLowTie.getText().toString() + "." + mBinding.lowAlarmMinValueIsc.getText().toString(),
                                    (mBinding.analogHighAlarmTBtn.isChecked() ? "+" : "-") + mBinding.analogHighLowTie.getText().toString() + "." + mBinding.highAlarmMinValueIsc.getText().toString(),
                                    getStringValue(mBinding.analogUnitMeasurementTie), "N/A", 1);
                    // todo
                } else {
                    entityUpdate = new InputConfigurationEntity
                            (Integer.parseInt(getStringValue(2, mBinding.analogInputNumberTie)),
                                    mBinding.analogSensorTypeTie.getText().toString(), "Analog", 0,
                                    mBinding.analogSequenceNumberTie.getText().toString(),
                                    Integer.parseInt(sequenceNo), getStringValue(0, mBinding.analogInputLabelTie),
                                    mBinding.analogAlarmLowTie.getText().toString() + "." + mBinding.lowAlarmMinValueIsc.getText().toString(),
                                    mBinding.analogHighLowTie.getText().toString() + "." + mBinding.highAlarmMinValueIsc.getText().toString(),
                                    getStringValue(mBinding.analogUnitMeasurementTie), "N/A", 1);
                }
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
                break;
        }
    }
}
