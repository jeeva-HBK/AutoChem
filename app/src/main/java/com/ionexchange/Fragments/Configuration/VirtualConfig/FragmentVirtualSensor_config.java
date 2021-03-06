package com.ionexchange.Fragments.Configuration.VirtualConfig;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentVirtualsensorConfigBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.calculationArr;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getStringValue;
import static com.ionexchange.Others.ApplicationClass.inputSensors;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.sensorTypeArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.VIRTUAL_INPUT;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

//created by Silambu

public class FragmentVirtualSensor_config extends Fragment implements DataReceiveCallback {
    FragmentVirtualsensorConfigBinding mBinding;
    ApplicationClass mAppClass;
    int sensorInputNo,sensorLength=2;
    WaterTreatmentDb db;
    VirtualConfigurationDao dao;
    KeepAliveCurrentValueDao sensorvalueDao;
    InputConfigurationDao inputTypeDao;
    String[] inputNames;
    private static final String TAG = "FragmentVirtualSensor_c";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_virtualsensor_config, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        Bundle bundle = getArguments();
        sensorInputNo = bundle.getInt("virtualInputNo");
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.virtualConfigurationDao();
        sensorvalueDao = db.keepAliveCurrentValueDao();
        inputTypeDao = db.inputConfigurationDao();

        switch (userType) {
            case 1: // Basic
                mBinding.lowRangeVi.setEnabled(false);
                mBinding.lowRangeTBtn.setEnabled(false);
                mBinding.lowRangeViDec.setEnabled(false);
                mBinding.highRangeVi.setEnabled(false);
                mBinding.highRangeTBtn.setEnabled(false);
                mBinding.highRangeViDec.setEnabled(false);
                mBinding.lowAlarmVi.setEnabled(false);
                mBinding.lowAlarmTBtn.setEnabled(false);
                mBinding.lowAlarmViDec.setEnabled(false);
                mBinding.highAlarmVi.setEnabled(false);
                mBinding.highAlarmTBtn.setEnabled(false);
                mBinding.highAlarmDec.setEnabled(false);
                mBinding.sensorlabelVi.setEnabled(false);
                //mBinding.vsRow1Isc.setVisibility(View.GONE);
                mBinding.sensorActivationVi.setVisibility(View.GONE);
                mBinding.calculationVi.setVisibility(View.GONE);
                mBinding.smoothingFactorVi.setVisibility(View.GONE);
                mBinding.vsRow2Isc.setVisibility(View.GONE);
                mBinding.vsRow3aIsc.setVisibility(View.GONE);
                mBinding.vsRow4Isc.setVisibility(View.GONE);
                break;

            case 2: // Intermediate
                mBinding.lowRangeVi.setEnabled(false);
                mBinding.lowRangeTBtn.setEnabled(false);
                mBinding.lowRangeViDec.setEnabled(false);
                mBinding.highRangeVi.setEnabled(false);
                mBinding.highRangeTBtn.setEnabled(false);
                mBinding.highRangeViDec.setEnabled(false);
                mBinding.smoothingFactorVi.setEnabled(false);
                mBinding.calculationVi.setVisibility(View.GONE);
                mBinding.sensorActivationVi.setVisibility(View.GONE);
                mBinding.vsRow2Isc.setVisibility(View.GONE);
                mBinding.vsRow3aIsc.setVisibility(View.GONE);
                break;

            case 3:
                break;
        }

        initAdapters();
        mBinding.saveFabInputSettings.setOnClickListener(this::save);
        mBinding.sensor1ViATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    mBinding.sensor1ConstantViEDT.setEnabled(true);
                    mBinding.sensor1ConstantDec.setEnabled(true);
                    mBinding.sensor1TypeVi.setEnabled(true);
                    mBinding.pidConstant1valueTBtn.setEnabled(true);
                    mBinding.sensor1TypeATXT.setAdapter(getAdapter(inputTypeArr));
                    refreshData();
                    setMaxLength(mBinding.sensor1TypeATXT.getText().toString(),mBinding.sensor1ConstantViEDT,0);
                } else {
                    if(!getStringValue(mBinding.sensor1ViATXT).isEmpty()){
                        String[] gethardwareNo = getStringValue(mBinding.sensor1ViATXT).split("-");
                        String hardwareNo = gethardwareNo[0];
                        String sensorValue = sensorvalueDao.getCurrentValue(Integer.parseInt(hardwareNo));
                        String inputType = inputTypeDao.getInputType(Integer.parseInt(hardwareNo));
                        if(sensorValue.equalsIgnoreCase("N/A"))
                            sensorValue = "0";
                        if(sensorValue.contains("-")){
                            mBinding.pidConstant1valueTBtn.setChecked(false);
                        } else {
                            mBinding.pidConstant1valueTBtn.setChecked(true);
                        }
                        if(sensorValue.contains("-") || sensorValue.contains("+")){
                            mBinding.sensor1ConstantViEDT.setText(sensorValue.substring(1));
                            splitDecimal(sensorValue.substring(1),mBinding.sensor1ConstantViEDT,mBinding.sensor1ConstantDec);
                        }else{
                            mBinding.sensor1ConstantViEDT.setText(sensorValue);
                            splitDecimal(sensorValue,mBinding.sensor1ConstantViEDT,mBinding.sensor1ConstantDec);
                        }
                        mBinding.sensor1TypeATXT.setText(inputType);
                        if(!mBinding.sensor2TypeATXT.getText().toString().isEmpty()) {
                            if (!mBinding.sensor2TypeATXT.getText().toString().equalsIgnoreCase("Analog Input")) {
                                if (!mBinding.sensor2TypeATXT.getText().toString().equalsIgnoreCase(mBinding.sensor1TypeATXT.getText().toString())) {
                                    mAppClass.showSnackBar(getContext(), "Sensor 1 Input Type is not matching with Sensor 2 Input Type");
                                }
                            }
                        }
                       /* if(!mBinding.sensor2TypeATXT.getText().toString().isEmpty() &&
                                (!mBinding.sensor2TypeATXT.getText().toString().equalsIgnoreCase(mBinding.sensor1TypeATXT.getText().toString() ))){
                            mAppClass.showSnackBar(getContext(), "Sensor 1 Input Type is not matching with Sensor 2 Input Type");
                        }*/
                    }
                    mBinding.sensor1ConstantViEDT.setEnabled(false);
                    mBinding.sensor1ConstantDec.setEnabled(false);
                    mBinding.sensor1TypeVi.setEnabled(false);
                    mBinding.pidConstant1valueTBtn.setEnabled(false);
                    refreshData();
                    setMaxLength(mBinding.sensor1TypeATXT.getText().toString(),mBinding.sensor1ConstantViEDT,1);
                }
            }
        });
        mBinding.sensor2ViATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 0) {
                    mBinding.sensor2ConstantViEDT.setEnabled(true);
                    mBinding.sensor2ConstantDec.setEnabled(true);
                    mBinding.sensor2TypeVi.setEnabled(true);
                    mBinding.pidConstant2valueTBtn.setEnabled(true);
                    mBinding.sensor2TypeATXT.setAdapter(getAdapter(inputTypeArr));
                        refreshData();
                    setMaxLength(mBinding.sensor2TypeATXT.getText().toString(),mBinding.sensor2ConstantViEDT,0);
                    } else {
                        if(!getStringValue(mBinding.sensor2ViATXT).isEmpty()){
                            String[] gethardwareNo = getStringValue(mBinding.sensor2ViATXT).split("-");
                            String hardwareNo = gethardwareNo[0];
                            String sensorValue = sensorvalueDao.getCurrentValue(Integer.parseInt(hardwareNo));
                            String inputType = inputTypeDao.getInputType(Integer.parseInt(hardwareNo));
                            if(sensorValue.equalsIgnoreCase("N/A"))
                                sensorValue = "0";
                            if(sensorValue.contains("-")){
                                mBinding.pidConstant2valueTBtn.setChecked(false);
                            } else {
                                mBinding.pidConstant2valueTBtn.setChecked(true);
                            }
                            if(sensorValue.contains("-") || sensorValue.contains("+")){
                                mBinding.sensor2ConstantViEDT.setText(sensorValue.substring(1));
                                splitDecimal(sensorValue.substring(1),mBinding.sensor2ConstantViEDT,mBinding.sensor2ConstantDec);
                            } else {
                                mBinding.sensor2ConstantViEDT.setText(sensorValue);
                                splitDecimal(sensorValue,mBinding.sensor2ConstantViEDT,mBinding.sensor2ConstantDec);
                            }
                            mBinding.sensor2TypeATXT.setText(inputType);
                            if(!mBinding.sensor1TypeATXT.getText().toString().isEmpty()) {
                                if (!mBinding.sensor1TypeATXT.getText().toString().equalsIgnoreCase("Analog Input")) {
                                    if (!mBinding.sensor2TypeATXT.getText().toString().equalsIgnoreCase(mBinding.sensor1TypeATXT.getText().toString())) {
                                        mAppClass.showSnackBar(getContext(), "Sensor 1 Input Type is not matching with Sensor 2 Input Type");
                                    }
                                }
                            }
                    }
                    mBinding.sensor2ConstantViEDT.setEnabled(false);
                    mBinding.sensor2ConstantDec.setEnabled(false);
                    mBinding.sensor2TypeVi.setEnabled(false);
                    mBinding.pidConstant2valueTBtn.setEnabled(false);
                    refreshData();
                    setMaxLength(mBinding.sensor2TypeATXT.getText().toString(),mBinding.sensor2ConstantViEDT,1);
                    }
            }
        });
        mBinding.sensor1TypeATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(!mBinding.sensor2TypeATXT.getText().toString().isEmpty() &&
                            !mBinding.sensor2TypeATXT.getText().toString().equalsIgnoreCase(mBinding.sensor1TypeATXT.getText().toString())){
                        mAppClass.showSnackBar(getContext(), "Sensor 1 Input Type is not matching with Sensor 2 Input Type");
                    }
                    refreshData();
                    if(!mBinding.sensor1ViATXT.getText().toString().isEmpty() && mBinding.sensor1ViATXT.getText().toString().equalsIgnoreCase("Constant")) {
                        setMaxLength(mBinding.sensor1TypeATXT.getText().toString(), mBinding.sensor1ConstantViEDT, 0);
                        mBinding.sensor1ConstantViEDT.setText("");
                    }else{
                        setMaxLength(mBinding.sensor1TypeATXT.getText().toString(), mBinding.sensor1ConstantViEDT, 1);
                    }
            }
        });

        mBinding.sensor2TypeATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(!mBinding.sensor1TypeATXT.getText().toString().isEmpty() &&
                            !mBinding.sensor1TypeATXT.getText().toString().equalsIgnoreCase(mBinding.sensor2TypeATXT.getText().toString())){
                        mAppClass.showSnackBar(getContext(), "Sensor 2 Input Type is not matching with Sensor 1 Input Type");
                    }
                refreshData();
                if(!mBinding.sensor2ViATXT.getText().toString().isEmpty() && mBinding.sensor2ViATXT.getText().toString().equalsIgnoreCase("Constant")) {
                    setMaxLength(mBinding.sensor2TypeATXT.getText().toString(), mBinding.sensor2ConstantViEDT, 0);
                    mBinding.sensor2ConstantViEDT.setText("");
                }else{
                    setMaxLength(mBinding.sensor2TypeATXT.getText().toString(), mBinding.sensor2ConstantViEDT, 1);
                }
            }
        });
        mBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppClass.popStackBack(getActivity());
            }
        });

    }
    private void TBtnVisiblity(String alarmValue,ToggleButton TBtn,TextInputEditText textValue,EditText deciValue){
        if(alarmValue.contains("-") || alarmValue.contains("+")){
            if(alarmValue.substring(0,1).equalsIgnoreCase("+")){
                TBtn.setChecked(true);
            } else{
                TBtn.setChecked(false);
            }
            splitDecimal(alarmValue.substring(1),textValue,deciValue);
        } else {
            splitDecimal(alarmValue,textValue,deciValue);
        }
    }

    private void splitDecimal(String sensorValue,TextInputEditText sensorConstant,EditText sensorDecimal){
        try {
            if (sensorValue.contains(".")) {
                String[] sensorDec = sensorValue.split("\\.");
                sensorConstant.setText(sensorDec[0]);
                sensorDecimal.setText(sensorDec[1]);
                if (sensorDec[1].length() > 2) {
                    sensorDecimal.setText(sensorDec[1].substring(0,2));
                }
            } else {
                sensorConstant.setText(sensorValue);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private String getTBtnValue(AutoCompleteTextView sensorType, ToggleButton tBtn){
        String TbtnValue = "";
        if(toString(sensorType).equalsIgnoreCase("ORP") ||
                toString(sensorType).equalsIgnoreCase("Temperature"))
        {
            TbtnValue = tBtn.isChecked() ? "+" : "-";
        }
        return  TbtnValue;
    }
    private void delete(View view) { }

    private void save(View view) {
        if (validField()) {
            String u_sensor1Type,u_sensor2Type,u_sensor1No,u_sensor2No,u_sensor1Constant,
                    u_sensor2Constant,u_sensor1type="",u_sensor2type="";
            if(toString(mBinding.sensor1ViATXT).isEmpty() || toString(mBinding.sensor1ViATXT).equalsIgnoreCase("Constant")){
                u_sensor1Constant =  getTBtnValue(mBinding.sensor1TypeATXT,mBinding.pidConstant1valueTBtn)+toString(sensorLength, mBinding.sensor1ConstantViEDT) + "." + toString(2, mBinding.sensor1ConstantDec);
                u_sensor1Type = "1";
            }else{
                u_sensor1Type = "0";
                String[] inputNo = toString(mBinding.sensor1ViATXT).split("-");
                u_sensor1Constant = inputNo[0];
                u_sensor1Constant = formDigits(2, u_sensor1Constant);
                //u_sensor1Constant = mBinding.sensor1ConstantViEDT.getText().toString();
            }
            if(toString(mBinding.sensor2ViATXT).isEmpty() || toString(mBinding.sensor2ViATXT).equalsIgnoreCase("Constant")){
                u_sensor2Constant =   getTBtnValue(mBinding.sensor2TypeATXT,mBinding.pidConstant2valueTBtn)+toString(sensorLength, mBinding.sensor2ConstantViEDT) + "." + toString(2, mBinding.sensor2ConstantDec);
                u_sensor2Type = "1";
            }else{
                u_sensor2Type = "0";
                String[] inputNo = toString(mBinding.sensor2ViATXT).split("-");
                u_sensor2Constant = inputNo[0];
                u_sensor2Constant = formDigits(2, u_sensor2Constant);
                //u_sensor2Constant = mBinding.sensor1ConstantViEDT.getText().toString();
            }
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET +
                    SPILT_CHAR + VIRTUAL_INPUT + SPILT_CHAR +
                    sensorInputNo + SPILT_CHAR +
                    getPosition(0, toString(mBinding.sensorActivationViEDT), sensorActivationArr) + SPILT_CHAR +
                    toString(0, mBinding.labelViEDT) + SPILT_CHAR +
                    u_sensor1Type + SPILT_CHAR +
                    u_sensor1Constant + SPILT_CHAR +
                    formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.sensor1TypeATXT), inputTypeArr))) + "") + SPILT_CHAR +
                    u_sensor2Type + SPILT_CHAR +
                    u_sensor2Constant + SPILT_CHAR +
                    formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.sensor2TypeATXT), inputTypeArr))) + "") + SPILT_CHAR +
                    getTBtnValue(mBinding.sensor1TypeATXT,mBinding.lowRangeTBtn)+toString(sensorLength, mBinding.lowRangeViEDT) +  "." + toString(2, mBinding.lowRangeViDec) + SPILT_CHAR +
                    getTBtnValue(mBinding.sensor1TypeATXT,mBinding.highRangeTBtn)+toString(sensorLength, mBinding.highRangeViEDT) +  "." + toString(2, mBinding.highRangeViDec) + SPILT_CHAR +
                    toString(3, mBinding.smoothingFactorViEDT) + SPILT_CHAR +
                    getTBtnValue(mBinding.sensor1TypeATXT,mBinding.lowAlarmTBtn)+toString(sensorLength, mBinding.lowAlarmViEDT) + "." + toString(2, mBinding.lowAlarmViDec) + SPILT_CHAR +
                    getTBtnValue(mBinding.sensor1TypeATXT,mBinding.highAlarmTBtn)+toString(sensorLength, mBinding.highAlarmViEDT) + "." + toString(2, mBinding.highAlarmDec) + SPILT_CHAR +
                    getPosition(0, toString(mBinding.calculationViEDT), calculationArr) + SPILT_CHAR + "1"
            );


        }
    }

    private void initAdapters() {
        mBinding.sensorActivationViEDT.setAdapter(getAdapter(sensorActivationArr));
        mBinding.calculationViEDT.setAdapter(getAdapter(calculationArr));
        getSensorInputArray();
        mBinding.sensor1ViATXT.setAdapter(getAdapter(inputNames));
        mBinding.sensor2ViATXT.setAdapter(getAdapter(inputNames));
        mBinding.sensor1TypeATXT.setAdapter(getAdapter(inputTypeArr));
        mBinding.sensor2TypeATXT.setAdapter(getAdapter(inputTypeArr));
    }
    private void getSensorInputArray() {
        WaterTreatmentDb DB = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao DAO = DB.inputConfigurationDao();
        List<InputConfigurationEntity> inputNameList = DAO.getAnalogInputHardWareNoConfigurationEntityList(0);
        inputNames = new String[inputNameList.size()+1];
        inputNames[0] = "Constant";
        if (!inputNameList.isEmpty()) {
            for (int i = 0; i < inputNameList.size(); i++) {
                inputNames[i+1] = inputNameList.get(i).getHardwareNo() + "- " + inputNameList.get(i).getInputLabel();
            }
        }
    }

    void refreshData(){
        mBinding.lowRangeViEDT.setText("");
        mBinding.highRangeViEDT.setText("");
        mBinding.lowAlarmViEDT.setText("");
        mBinding.highAlarmViEDT.setText("");
        mBinding.lowRangeViDec.setText("");
        mBinding.highRangeViDec.setText("");
        mBinding.lowAlarmViDec.setText("");
        mBinding.highAlarmDec.setText("");
    }
    void setMaxLength(String inputType, TextInputEditText constantValue,int pos){

        sensorLayoutVisibility(false);
        switch (inputType){
            case "ORP":
                sensorLength = 4;
                sensorLayoutVisibility(true);
                break;
            case "Temperature":
                sensorLength = 3;
                sensorLayoutVisibility(true);
                break;
            case "Modbus Sensor":
                sensorLength = 3;
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
        mBinding.lowRangeViEDT.setFilters(new InputFilter[] { new InputFilter.LengthFilter(sensorLength) });
        mBinding.highRangeViEDT.setFilters(new InputFilter[] { new InputFilter.LengthFilter(sensorLength) });
        mBinding.lowAlarmViEDT.setFilters(new InputFilter[] { new InputFilter.LengthFilter(sensorLength) });
        mBinding.highAlarmViEDT.setFilters(new InputFilter[] { new InputFilter.LengthFilter(sensorLength) });
        if(pos == 0) {
            constantValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sensorLength)});
        }
    }
    void sensorLayoutVisibility(boolean visibility){
        if(visibility){
            mBinding.pidConstant1valueTBtn.setVisibility(View.VISIBLE);
            mBinding.pidConstant2valueTBtn.setVisibility(View.VISIBLE);
            mBinding.lowRangeTBtn.setVisibility(View.VISIBLE);
            mBinding.highRangeTBtn.setVisibility(View.VISIBLE);
            mBinding.lowAlarmTBtn.setVisibility(View.VISIBLE);
            mBinding.highAlarmTBtn.setVisibility(View.VISIBLE);
        } else {
            mBinding.pidConstant1valueTBtn.setVisibility(View.GONE);
            mBinding.pidConstant2valueTBtn.setVisibility(View.GONE);
            mBinding.lowRangeTBtn.setVisibility(View.GONE);
            mBinding.highRangeTBtn.setVisibility(View.GONE);
            mBinding.lowAlarmTBtn.setVisibility(View.GONE);
            mBinding.highAlarmTBtn.setVisibility(View.GONE);
        }
    }
    private Boolean isEmpty(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        return false;
    }

    private boolean validField() {
        if (isEmpty(mBinding.labelViEDT)) {
            mAppClass.showSnackBar(getContext(), "Sensor Label cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorActivationViEDT)) {
            mAppClass.showSnackBar(getContext(), "Please select Sensor Activation mode");
            return false;
        } else if (isEmpty(mBinding.calculationViEDT)) {
            mAppClass.showSnackBar(getContext(), "Please select Calculation mode");
            return false;
        } else if (isEmpty(mBinding.smoothingFactorViEDT)) {
            mAppClass.showSnackBar(getContext(), "Smoothing factor cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.smoothingFactorViEDT.getText().toString()) > 100) {
            mAppClass.showSnackBar(getContext(), "Smoothing factor value less than 101");
            return false;
        } else if (isEmpty(mBinding.sensor1ViATXT)) {
            mAppClass.showSnackBar(getContext(), "Please select Sensor 1 Input Number");
            return false;
        } else if (isEmpty(mBinding.sensor1TypeATXT)) {
            mAppClass.showSnackBar(getContext(), "Please select Sensor 1 Input Type");
            return false;
        } else if (isEmpty(mBinding.sensor1ConstantViEDT)) {
            mAppClass.showSnackBar(getContext(), "Sensor Constant 1 cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensor2ViATXT)) {
            mAppClass.showSnackBar(getContext(), "Please select Sensor 2 Input Number");
            return false;
        } else if (isEmpty(mBinding.sensor2TypeATXT)) {
            mAppClass.showSnackBar(getContext(), "Please select Sensor 2 Input Type");
            return false;
        } else if (isEmpty(mBinding.sensor2ConstantViEDT)) {
            mAppClass.showSnackBar(getContext(), "Sensor Constant 2 cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.lowRangeViEDT)) {
            mAppClass.showSnackBar(getContext(), "Low Range cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.highAlarmViEDT)) {
            mAppClass.showSnackBar(getContext(), "High Range cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.lowAlarmViEDT)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.highAlarmViEDT)) {
            mAppClass.showSnackBar(getContext(), "Alarm high cannot be Empty");
            return false;
        } else if(!mBinding.sensor1TypeATXT.getText().toString().equalsIgnoreCase("Analog Input") ||
                !mBinding.sensor2TypeATXT.getText().toString().equalsIgnoreCase("Analog Input")){
                if(!mBinding.sensor2TypeATXT.getText().toString().equalsIgnoreCase(mBinding.sensor1TypeATXT.getText().toString()))
            {
                mAppClass.showSnackBar(getContext(), "Sensor 1 Input Type is not matching with Sensor 2 Input Type");
                return false;
            }
        }
        return true;
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

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + VIRTUAL_INPUT + SPILT_CHAR + sensorInputNo);
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleResponse(data.split("\\*")[1].split(RES_SPILT_CHAR));
        }
    }

    private void handleResponse(String[] spiltData) {
        if (spiltData[1].equals(VIRTUAL_INPUT)) {
            if (spiltData[0].equals(READ_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mBinding.sensorActivationViEDT.setText(mBinding.sensorActivationViEDT.getAdapter().getItem(Integer.parseInt(spiltData[4])).toString());
                    mBinding.labelViEDT.setText(spiltData[5]);
                    mBinding.sensor1TypeATXT.setText(mBinding.sensor1TypeATXT.getAdapter().getItem(Integer.parseInt(spiltData[8])).toString());
                    //mBinding.sensor1TypeATXT.setText("Contacting Conductivity");
                   // mBinding.sensor2TypeATXT.setText("Contacting Conductivity");
                    if(spiltData[6].equalsIgnoreCase("1")){
                        mBinding.sensor1ViATXT.setText(mBinding.sensor1ViATXT.getAdapter().getItem(0).toString());
                        setMaxLength(mBinding.sensor1TypeATXT.getText().toString(),mBinding.sensor1ConstantViEDT,0);
                        TBtnVisiblity(spiltData[7],mBinding.pidConstant1valueTBtn,mBinding.sensor1ConstantViEDT,mBinding.sensor1ConstantDec);
                        mBinding.sensor1TypeVi.setEnabled(true);
                        mBinding.sensor1ConstantViEDT.setEnabled(true);
                        mBinding.sensor1ConstantDec.setEnabled(true);
                        mBinding.pidConstant1valueTBtn.setEnabled(true);
                    } else {
                        mBinding.sensor1ViATXT.setText(mBinding.sensor1ViATXT.getAdapter().getItem(Integer.parseInt(spiltData[7])).toString());
                        String sensorValue = sensorvalueDao.getCurrentValue(Integer.parseInt(spiltData[7]));
                        if(sensorValue.equalsIgnoreCase("N/A"))
                            sensorValue = "0";
                        if(sensorValue.contains("-")){
                            mBinding.pidConstant1valueTBtn.setChecked(false);
                        } else {
                            mBinding.pidConstant1valueTBtn.setChecked(true);
                        }
                        if(sensorValue.contains("-") || sensorValue.contains("+")){
                            mBinding.sensor1ConstantViEDT.setText(sensorValue.substring(1));
                            splitDecimal(sensorValue.substring(1),mBinding.sensor1ConstantViEDT,mBinding.sensor1ConstantDec);
                        }else{
                            mBinding.sensor1ConstantViEDT.setText(sensorValue);
                            splitDecimal(sensorValue,mBinding.sensor1ConstantViEDT,mBinding.sensor1ConstantDec);
                        }
                        mBinding.sensor1ConstantViEDT.setEnabled(false);
                        mBinding.sensor1ConstantDec.setEnabled(false);
                        mBinding.pidConstant1valueTBtn.setEnabled(false);
                        mBinding.sensor1TypeVi.setEnabled(false);
                        setMaxLength(mBinding.sensor1TypeATXT.getText().toString(),mBinding.sensor1ConstantViEDT,1);
                    }
                   // mBinding.sensor1ConstantViEDT.setText(spiltData[7].substring(0, 6));
                    //mBinding.sensor1ConstantDec.setText(spiltData[7].substring(7, 9));
                    mBinding.sensor2TypeATXT.setText(mBinding.sensor2TypeATXT.getAdapter().getItem(Integer.parseInt(spiltData[11])).toString());
                    if(spiltData[9].equalsIgnoreCase("1")){
                        mBinding.sensor2ViATXT.setText(mBinding.sensor2ViATXT.getAdapter().getItem(0).toString());
                        setMaxLength(mBinding.sensor2TypeATXT.getText().toString(),mBinding.sensor2ConstantViEDT,0);
                        TBtnVisiblity(spiltData[10],mBinding.pidConstant2valueTBtn,mBinding.sensor2ConstantViEDT,mBinding.sensor2ConstantDec);
                        mBinding.sensor2TypeVi.setEnabled(true);
                        mBinding.sensor2ConstantViEDT.setEnabled(true);
                        mBinding.sensor2ConstantDec.setEnabled(true);
                        mBinding.pidConstant2valueTBtn.setEnabled(true);
                    }else{
                        mBinding.sensor2ViATXT.setText(mBinding.sensor2ViATXT.getAdapter().getItem(Integer.parseInt(spiltData[10])).toString());
                        String sensorValue = sensorvalueDao.getCurrentValue(Integer.parseInt(spiltData[10]));
                        if(sensorValue.equalsIgnoreCase("N/A"))
                            sensorValue = "0";
                        if(sensorValue.contains("-")){
                            mBinding.pidConstant2valueTBtn.setChecked(false);
                        } else {
                            mBinding.pidConstant2valueTBtn.setChecked(true);
                        }
                        if(sensorValue.contains("-") || sensorValue.contains("+")){
                            mBinding.sensor2ConstantViEDT.setText(sensorValue.substring(1));
                            splitDecimal(sensorValue.substring(1),mBinding.sensor2ConstantViEDT,mBinding.sensor2ConstantDec);
                        }else{
                            mBinding.sensor2ConstantViEDT.setText(sensorValue);
                            splitDecimal(sensorValue,mBinding.sensor2ConstantViEDT,mBinding.sensor2ConstantDec);
                        }
                        mBinding.sensor2ConstantViEDT.setEnabled(false);
                        mBinding.sensor2ConstantDec.setEnabled(false);
                        mBinding.pidConstant2valueTBtn.setEnabled(false);
                        mBinding.sensor2TypeVi.setEnabled(false);
                        setMaxLength(mBinding.sensor2TypeATXT.getText().toString(),mBinding.sensor2ConstantViEDT,1);
                    }
                    //mBinding.sensor2ConstantViEDT.setText(spiltData[9].substring(0, 6));
                    //mBinding.sensor2ConstantDec.setText(spiltData[9].substring(7, 9));
                    //mBinding.lowRangeViEDT.setText(spiltData[10]);
                    //mBinding.highRangeViEDT.setText(spiltData[11]);
                    mBinding.lowRangeViEDT.setText(spiltData[12].substring(0, sensorLength));
                    mBinding.lowRangeViDec.setText(spiltData[12].substring(sensorLength+1, sensorLength+3));
                    TBtnVisiblity(spiltData[12],mBinding.lowRangeTBtn,mBinding.lowRangeViEDT,mBinding.lowRangeViDec);

                    mBinding.highRangeViEDT.setText(spiltData[13].substring(0, sensorLength));
                    mBinding.highRangeViDec.setText(spiltData[13].substring(sensorLength+1, sensorLength+3));
                    TBtnVisiblity(spiltData[13],mBinding.highRangeTBtn,mBinding.highRangeViEDT,mBinding.highRangeViDec);

                    mBinding.smoothingFactorViEDT.setText(spiltData[14]);

                    mBinding.lowAlarmViEDT.setText(spiltData[15].substring(0, sensorLength));
                    mBinding.lowAlarmViDec.setText(spiltData[15].substring(sensorLength+1, sensorLength+3));
                    TBtnVisiblity(spiltData[15],mBinding.lowAlarmTBtn,mBinding.lowAlarmViEDT,mBinding.lowAlarmViDec);

                    mBinding.highAlarmViEDT.setText(spiltData[16].substring(0, sensorLength));
                    mBinding.highAlarmDec.setText(spiltData[16].substring(sensorLength+1, sensorLength+3));
                    TBtnVisiblity(spiltData[16],mBinding.highAlarmTBtn,mBinding.highAlarmViEDT,mBinding.highAlarmDec);

                    mBinding.calculationViEDT.setText(mBinding.calculationViEDT.getAdapter().getItem(Integer.parseInt(spiltData[17])).toString());
                    initAdapters();
                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.readFailed));
                }
            } else if (spiltData[0].equals(WRITE_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                    virtualEntity();
                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            }
        } else {
            Log.e(TAG, "handleResponse: Received Wrong Packet");
        }
    }

    void virtualEntity() {
        VirtualConfigurationEntity virtualConfigurationEntity = new VirtualConfigurationEntity(
                sensorInputNo, "Virtual", 0, toString(0, mBinding.labelViEDT),toString(0, mBinding.sensor1TypeATXT),
                toString(6, mBinding.lowAlarmViEDT) + "." + toString(2, mBinding.lowAlarmViDec),
                toString(6, mBinding.highAlarmViEDT) + "." + toString(2, mBinding.highAlarmDec));
        List<VirtualConfigurationEntity> entryListUpdate = new ArrayList<>();
        entryListUpdate.add(virtualConfigurationEntity);
        updateToDb(entryListUpdate);
    }

    public void updateToDb(List<VirtualConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        VirtualConfigurationDao dao = db.virtualConfigurationDao();
        dao.insert(entryList.toArray(new VirtualConfigurationEntity[0]));
    }
}
