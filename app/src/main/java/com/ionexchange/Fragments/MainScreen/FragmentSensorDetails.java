package com.ionexchange.Fragments.MainScreen;

import static com.ionexchange.Others.ApplicationClass.analogInputArr;
import static com.ionexchange.Others.ApplicationClass.bleedRelay;
import static com.ionexchange.Others.ApplicationClass.bufferArr;
import static com.ionexchange.Others.ApplicationClass.calculationArr;
import static com.ionexchange.Others.ApplicationClass.doseTypeArr;
import static com.ionexchange.Others.ApplicationClass.fMode;
import static com.ionexchange.Others.ApplicationClass.flowAlarmMode;
import static com.ionexchange.Others.ApplicationClass.flowMeterTypeArr;
import static com.ionexchange.Others.ApplicationClass.flowUnitArr;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getPosition;
import static com.ionexchange.Others.ApplicationClass.getValueFromArr;
import static com.ionexchange.Others.ApplicationClass.inputAnalogSensors;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.interlockChannel;
import static com.ionexchange.Others.ApplicationClass.modBusTypeArr;
import static com.ionexchange.Others.ApplicationClass.modBusUnitArr;
import static com.ionexchange.Others.ApplicationClass.modeAnalog;
import static com.ionexchange.Others.ApplicationClass.modeInhibitor;
import static com.ionexchange.Others.ApplicationClass.modeSensor;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.scheduleResetArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.tempLinkedArr;
import static com.ionexchange.Others.ApplicationClass.typeOfValueRead;
import static com.ionexchange.Others.ApplicationClass.unitArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.VIRTUAL_INPUT;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.card.MaterialCardView;
import com.ionexchange.Adapters.SensorDetailsParamsRvAdapter;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Fragments.MainScreen.Calibration.FragmentModbusCalibration;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSensorDetailsBinding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FragmentSensorDetails extends Fragment {
    FragmentSensorDetailsBinding mBinding;
    Context mContext;
    ApplicationClass mAppClass;
    List<List<String[]>> finalSensorParamList;
    int currentPage = 0, pageMax = 8;
    FragmentSensorCalibration sensorCalibration;
    static MaterialCardView mainScreenBtn;
    FragmentModbusCalibration modbusCalibration;
    String inputNumber, inputType, spareKey = "null";
    String inNumber = "Input Number", inpuType = "Input Type", seqNumber = "Sequence Number", sensorActivation = "Sensor Activation",
            inputLabel = "Input Label", bufferType = "Buffer Type", tempSensorLinked = "Temperature Sensor Linked", defTempValue = "Default Temperature Value",
            smoothingFactor = "Smoothing Factor", alarmLow = "Alarm Low", alarmHigh = "Alarm High", calibrationRequiredAlarm = "Calibration Required Alarm",
            resetCalibration = "Reset Calibration", sensorStatus = "SensorStatus", unitOfMeasurement = "Unit Of Measurement", cellConstant = "Cell Constant",
            compType = "Compensation Type", compFactor = "Compensation Factor", analogType = "Analog Type", assigned = "Assigned To Sensor", minValue = "Min Value",
            maxValue = "Max Value", openMsg = "Open Message", closeMsg = "Close Message", interLock = "Interlock", alarm = "Alarm", totalTime = "Total Time",
            resetTotalTime = "Reset Total Time";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_sensor_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity().getApplicationContext();
        mAppClass = (ApplicationClass) getActivity().getApplication();
        inputNumber = getArguments().getString("hardwareNumber");
        inputType = getArguments().getString("hardwareType");
        mainScreenBtn = view.findViewById(mBinding.cardViewMultiMainScreen.getId());
        getTCPData(inputNumber, inputType);
        mBinding.btnTrendCalibartion.setChecked(true);

        if (inputType.contains("Digital Input") || inputType.contains("Tank") || inputType.contains("Output")) {
            mBinding.cardViewMultiSelection.setVisibility(View.GONE);
            getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), new FragmentNoCalibration()).commit();
            /*mBinding.detailFrame.setVisibility(View.GONE);
            mBinding.btnNext.setVisibility(View.GONE);
            mBinding.btnPerv.setVisibility(View.GONE);
            mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6)); // should change
            ConstraintLayout constraintLayout = view.findViewById(R.id.detailRootLayout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.recyclerView, ConstraintSet.START, R.id.cardView_multi_mainScreen, ConstraintSet.RIGHT, 0);
            constraintSet.applyTo(constraintLayout);*/
        }

        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        mBinding.viewTrendMainScreen.setOnClickListener(View -> {
            mAppClass.popStackBack(getActivity());
        });

        mBinding.cardViewMultiMainScreen.setOnClickListener(View -> {
            mAppClass.popStackBack(getActivity());
        });

        mBinding.btnTrendCalibartion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBinding.txtTrendCalibration.setText("TREND");

                    mBinding.viewTrendCalibration.setBackground(getContext().getDrawable(R.drawable.graph));
                    if (inputType.equals("Modbus Sensor")) {
                        if (modbusCalibration != null) {
                            getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), modbusCalibration).commit();
                        }
                    } else {
                        switch (inputType) {
                            case "pH":
                                sensorCalibration = new FragmentSensorCalibration(inputNumber, getPosition(2, inputType, inputTypeArr), spareKey);
                                getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), sensorCalibration).commit();
                                break;
                            case "ORP":
                            case "Contacting Conductivity":
                            case "Temperature":
                               /* sensorCalibration = new FragmentSensorCalibration(inputNumber, getPosition(2, inputType, inputTypeArr));
                                getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), sensorCalibration).commit();
                                break;*/
                            case "Analog Input":
                                sensorCalibration = new FragmentSensorCalibration(inputNumber, getPosition(2, inputType, inputTypeArr));
                                getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), sensorCalibration).commit();
                                break;
                        }
                    }
                } else {
                    mBinding.txtTrendCalibration.setText("CALIBRATION");
                    mBinding.viewTrendCalibration.setBackground(getContext().getDrawable(R.drawable.calib_flask));
                    getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), new FragmentSensorStatistics(inputNumber, inputType)).commit();
                }
            }
        });

        mBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage < finalSensorParamList.size() - 1) {
                    currentPage = ++currentPage;
                } else {
                    mAppClass.showSnackBar(getContext(), "End of Page");
                }
                if (currentPage < finalSensorParamList.size()) {
                    if (finalSensorParamList.get(currentPage) != null && !finalSensorParamList.get(currentPage).isEmpty()) {
                        setAdapter(finalSensorParamList.get(currentPage));
                    }
                }
            }
        });

        mBinding.btnPerv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage > 0) {
                    currentPage = --currentPage;
                    if (finalSensorParamList.get(currentPage) != null && !finalSensorParamList.get(currentPage).isEmpty()) {
                        setAdapter(finalSensorParamList.get(currentPage));
                    }
                } else {
                    mAppClass.showSnackBar(getContext(), "End of Page");
                }
            }
        });
    }

    public static void clickMainScreenBtn(){
        mainScreenBtn.performClick();
    }

    private void getTCPData(String inputNumber, String inpuType) {
        mAppClass.sendPacket(new DataReceiveCallback() {
            @Override
            public void OnDataReceive(String data) {
                if (data.equals("FailedToConnect")) {
                    mAppClass.showSnackBar(getContext(), "Failed to connect");
                } else if (data.equals("pckError")) {
                    mAppClass.showSnackBar(getContext(), "Failed to connect");
                } else if (data.equals("sendCatch")) {
                    mAppClass.showSnackBar(getContext(), "Failed to connect");
                } else if (data.equals("Timeout")) {
                    mAppClass.showSnackBar(getContext(), "TimeOut");
                } else if (data != null && !data.equals("")) {
                    String[] splitData = data.split("\\*")[1].split("\\$");
                    if (splitData[1].equals("04")) {
                        if (splitData[0].equals(READ_PACKET)) {
                            if (splitData[2].equals(RES_SUCCESS)) {
                                Bundle mBundle = new Bundle();
                                mBundle.putString("sensorType", getValueFromArr(splitData[4], inputTypeArr));
                                switch (getValueFromArr(splitData[4], inputTypeArr)) {
                                    case "pH":
                                        mBundle.putString("bufferType", splitData[8]);
                                        mBundle.putString("inputNumber", splitData[3]);
                                        spareKey = splitData[8];
                                        sensorCalibration = new FragmentSensorCalibration(splitData[3], splitData[4], splitData[8]);
                                        getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), sensorCalibration).commit();
                                        setAdapter(formpHMap(data.split("\\*")[1].split("\\$")));
                                        break;

                                    case "ORP":
                                        sensorCalibration = new FragmentSensorCalibration(splitData[3], splitData[4]);
                                        getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), sensorCalibration).commit();
                                        setAdapter(formORPMap(data.split("\\*")[1].split("\\$")));
                                        break;

                                    case "Temperature":
                                        sensorCalibration = new FragmentSensorCalibration(splitData[3], splitData[4]);
                                        getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), sensorCalibration).commit();
                                        setAdapter(formTempMap(data.split("\\*")[1].split("\\$")));
                                        break;

                                    case "Flow/Water Meter":
                                        setAdapter(formFlowMap(data.split("\\*")[1].split("\\$")));
                                        break;

                                    case "Contacting Conductivity":
                                        sensorCalibration = new FragmentSensorCalibration(splitData[3], splitData[4]);
                                        getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), sensorCalibration).commit();
                                        setAdapter(formContConMap(data.split("\\*")[1].split("\\$")));
                                        break;

                                    case "Toroidal Conductivity":
                                        sensorCalibration = new FragmentSensorCalibration(splitData[3], splitData[4]);
                                        getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), sensorCalibration).commit();
                                        setAdapter(formTorConMap(data.split("\\*")[1].split("\\$")));
                                        break;

                                    case "Analog Input":
                                        sensorCalibration = new FragmentSensorCalibration(splitData[3], splitData[4]);
                                        getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), sensorCalibration).commit();
                                        setAdapter(formAnalogMap(data.split("\\*")[1].split("\\$")));
                                        break;

                                    case "Tank Level":
                                        setAdapter(formTankMap(data.split("\\*")[1].split("\\$")));
                                        break;

                                    case "Digital Input":
                                        setAdapter(formDigitalMap(data.split("\\*")[1].split("\\$")));
                                        break;

                                    case "Modbus Sensor":
                                        mBundle.putString("InputNo", splitData[3]);
                                        mBundle.putString("ModbusType", getValueFromArr(splitData[6], modBusTypeArr));
                                        mBundle.putString("TypeOfValue", getValueFromArr(splitData[7], typeOfValueRead));
                                        modbusCalibration = new FragmentModbusCalibration(mBundle);
                                        getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), modbusCalibration).commit();
                                        setAdapter(formModbusMap(data.split("\\*")[1].split("\\$")));
                                        break;
                                }
                            } else if (splitData[2].equals(RES_FAILED)) {
                                mAppClass.showSnackBar(getContext(), getString(R.string.readFailed));
                            }
                        }
                    } else if (splitData[1].equals("06")) {
                        if (splitData[0].equals(READ_PACKET)) {
                            if (splitData[2].equals(RES_SUCCESS)) {
                                setAdapter(formOutputMap(data.split("\\*")[1].split("\\$")));
                            }
                        }
                    } else if (splitData[1].equals("05")) { // virtual
                        if (splitData[0].equals(READ_PACKET)) {
                            if (splitData[2].equals(RES_SUCCESS)) {
                                mBinding.cardViewMultiSelection.setVisibility(View.INVISIBLE);
                                getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), new FragmentSensorStatistics(inputNumber, inputType)).commit();
                                setAdapter(formVirtualMap(data.split("\\*")[1].split("\\$")));
                            }
                        }
                    } else {
                        mAppClass.showSnackBar(getContext(), getString(R.string.wrongPack));
                    }
                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + getPckId(inpuType) + SPILT_CHAR + formDigits(2, inputNumber))
        ;
    }

    private List<String[]> formVirtualMap(String[] splitData) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        if (splitData[1].equals(VIRTUAL_INPUT)) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    tempMap.put("Input Number", splitData[3]);
                    tempMap.put("Sensor Activation", getValueFromArr(splitData[4], sensorActivationArr));
                    tempMap.put("Sensor Label", splitData[5]);
                    tempMap.put("Input 1 Type", splitData[6].equals("0") ? "Physical" : "Constant");

                    if (splitData[6].equals("0")) {
                        tempMap.put("Input 1 Number", getInputName(splitData[7]));
                        tempMap.put("Input 1 Type", getValueFromArr(splitData[8], inputTypeArr));
                    } else if (splitData[6].equals("1")) {
                        tempMap.put("Sensor 1 Constant Value", splitData[7]);
                        tempMap.put("Input 1 Type", getValueFromArr(splitData[8], inputTypeArr));
                    }

                    tempMap.put("Input 2 Type", splitData[9].equals("0") ? "Physical" : "Constant");
                    if (splitData[9].equals("0")) {
                        tempMap.put("Input 2 Number", getInputName(splitData[10]));
                        tempMap.put("Input 2 Type", getValueFromArr(splitData[11], inputTypeArr));
                    } else if (splitData[9].equals("1")) {
                        tempMap.put("Sensor 2 Constant Value", splitData[10]);
                        tempMap.put("Input 2 Type", getValueFromArr(splitData[11], inputTypeArr));
                    }
                    tempMap.put("Low Range", splitData[12]);
                    tempMap.put("High Range", splitData[13]);
                    tempMap.put("Smoothing Factor", splitData[14]);
                    tempMap.put("Alarm Low", splitData[15]);
                    tempMap.put("Alarm High", splitData[16]);
                    tempMap.put("Calculation", getValueFromArr(splitData[17], calculationArr));
                    //  tempMap.put("Unit", splitData[19]); todo unComment this
                   /* tempMap.put("Sensor 1 Constant", splitData[7]);
                    tempMap.put("Sensor 2 Number", getValueFromArr(splitData[8], getSensorInputArray()));
                    tempMap.put("Sensor 2 Constant", splitData[9]);
                    tempMap.put("Low Range", splitData[10]);
                    tempMap.put("High Range", splitData[11]);
                    tempMap.put("Smoothing Factor", splitData[12]);
                    tempMap.put("Alarm Low", splitData[13]);
                    tempMap.put("Alarm High", splitData[14]);
                    tempMap.put("Calculation", getValueFromArr(splitData[15], calculationArr));*/
                    switch (userType) {
                        case 1:
                            tempMap.remove("Sensor Activation");
                            tempMap.remove("Input 1 Number");
                            tempMap.remove("Sensor 1 Constant Value");
                            tempMap.remove("Input 2 Number");
                            tempMap.remove("Sensor 2 Constant Value");
                            tempMap.remove("Smoothing Factor");
                            tempMap.remove("Calculation");
                            break;
                        case 2:
                            tempMap.remove("Sensor Activation");
                            tempMap.remove("Input 1 Number");
                            tempMap.remove("Sensor 1 Constant Value");
                            tempMap.remove("Input 2 Number");
                            tempMap.remove("Sensor 2 Constant Value");
                            tempMap.remove("Calculation");
                            break;
                    }

                } else {
                    mAppClass.showSnackBar(getContext(), getString(R.string.readFailed));
                }
            }
        }
        finalSensorParamList = splitIntoParts(convertMap(tempMap), pageMax);
        return finalSensorParamList.get(0);
    }

    private String getInputName(String splitData) {
        return ApplicationClass.DB.inputConfigurationDao().getSensorType(Integer.parseInt(splitData));
    }

    private List<String[]> formOutputMap(String[] splitData) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        tempMap.put("OutPut Number", splitData[3]);
        tempMap.put("Function Mode", getValueFromArr(splitData[4], fMode));
        if (splitData[4].equals("0")) {
            // Disable
        } else if (splitData[4].equals("1")) { // inhibitor
            tempMap.put("Output Mode", getValueFromArr(splitData[8], modeInhibitor));
            tempMap.put("Output Label", splitData[5]);
            tempMap.put("Interlock channel", getValueFromArr((Integer.parseInt(splitData[6]) - 30) + "", interlockChannel));
            tempMap.put("Activate channel", getValueFromArr((Integer.parseInt(splitData[7]) - 30) + "", interlockChannel));
            if (userType == 3) {
                if (splitData[8].equals("0")) { // Continuous
                    tempMap.put("Pump Flow Rate", splitData[9]);
                    tempMap.put("Dose Rate", splitData[10]);
                    tempMap.put("Dose Period", splitData[11]);
                } else if (splitData[8].equals("1")) { // BleedDown
                    tempMap.put("Link Bleed Relay", getValueFromArr(splitData[9], getBleedArray()));
                    tempMap.put("Bleed Flow Rate", splitData[10]);
                    tempMap.put("Pump Flow Rate", splitData[11]);
                    tempMap.put("Target PPM", splitData[12]);
                    tempMap.put("Concentration", splitData[13]);
                    tempMap.put("Specific Gravity", splitData[14]);
                } else if (splitData[8].equals("2")) { // WaterMeter/Biocide
                    tempMap.put("FlowMeter Type", getValueFromArr(splitData[9], flowMeterTypeArr));
                    tempMap.put("FlowMeter Input Sequence Number", splitData[10]);
                    tempMap.put("Link Bleed Relay", getValueFromArr(splitData[11], getBleedArray()));
                    tempMap.put("Pump Flow Rate", splitData[12]);
                    tempMap.put("Target PPM", splitData[13]);
                    tempMap.put("Concentration", splitData[14]);
                    tempMap.put("Specific Gravity", splitData[15]);
                }
            }
        } else if (splitData[4].equals("2")) { // Sensor
            tempMap.put("Sensor Mode", getValueFromArr(splitData[9], modeSensor));
            tempMap.put("Output Label", splitData[5]);
            tempMap.put("Interlock channel", getValueFromArr((Integer.parseInt(splitData[6]) - 30) + "", interlockChannel));
            tempMap.put("Activate channel", getValueFromArr((Integer.parseInt(splitData[7]) - 30) + "", interlockChannel));
            if (userType == 3) {
                if (splitData[9].equals("0")) { // On/Off
                    tempMap.put("Set Point", splitData[10]);
                    tempMap.put("Dose Type", getValueFromArr(splitData[11], doseTypeArr));
                    tempMap.put("Link Input Sensor", getValueFromArr(splitData[8], getSensorInputArray()));
                    tempMap.put("Hysteresis", splitData[12]);
                    tempMap.put("Duty Cycle", splitData[13]);
                    tempMap.put("Lock Out Delay Time", splitData[14]);
                    tempMap.put("Safety Max", splitData[15]);
                    tempMap.put("Safety Min", splitData[16]);
                } else if (splitData[9].equals("1")) { // PID
                    tempMap.put("set Point", splitData[10]);
                    tempMap.put("Gain", splitData[11]);
                    tempMap.put("Integral Time", splitData[12]);
                    tempMap.put("Derivative Time", splitData[13]);
                    tempMap.put("Reset PID Integral", splitData[14]);
                    tempMap.put("Min Output", splitData[15]);
                    tempMap.put("Max Output", splitData[16]);
                    tempMap.put("Dose Type", (splitData[17].equals("0") ? "Above" : "Below"));
                    tempMap.put("Min Input", splitData[18]);
                    tempMap.put("Max Input", splitData[19]);
                    tempMap.put("Lockout Delay Time", splitData[20]);
                    tempMap.put("Safety Max", splitData[21]);
                    tempMap.put("Safety Min", splitData[22]);
                }
            } else if (splitData[9].equals("2")) { // Fuzzy
            }
        } else if (splitData[4].equals("3")) { // Analog
            tempMap.put("Sensor Mode", getValueFromArr(splitData[5], modeAnalog));
            tempMap.put("Link Input Relay", getValueFromArr(splitData[6], getSensorInputArray()));
            if (splitData[5].equals("0")) { // Disable

            } else if (splitData[5].equals("1") || splitData[5].equals("3") || splitData[5].equals("4")) { // other modes
                tempMap.put("Min mA", splitData[6]);
                tempMap.put("Max mA", splitData[7]);
                tempMap.put("Min Value", splitData[8]);
                tempMap.put("Max Value", splitData[9]);
            } else if (splitData[5].equals("2")) {
                tempMap.put("Fixed Value", splitData[6]);
            }
        }

        finalSensorParamList = splitIntoParts(convertMap(tempMap), pageMax);
        return finalSensorParamList.get(0);
    }

    private String[] getSensorInputArray() {
        WaterTreatmentDb DB = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao DAO = DB.inputConfigurationDao();
        List<InputConfigurationEntity> inputNameList = DAO.getInputHardWareNoConfigurationEntityList(1, 13);
        String[] inputNames = new String[13];
        if (!inputNameList.isEmpty()) {
            for (int i = 0; i < inputNameList.size(); i++) {
                inputNames[i] = "Input- " + inputNameList.get(i).getHardwareNo() + " (" + inputNameList.get(i).getInputLabel() + ")";
            }
        }
        if (inputNames.length == 0) {
            inputNames = inputAnalogSensors;
        }
        return inputNames;
    }

    String[] getBleedArray() {
        WaterTreatmentDb DB = WaterTreatmentDb.getDatabase(getContext());
        OutputConfigurationDao DAO = DB.outputConfigurationDao();
        List<OutputConfigurationEntity> outputNameList = DAO.getOutputHardWareNoConfigurationEntityList(1, 14);
        String[] outputNames = new String[14];
        if (!outputNameList.isEmpty()) {
            for (int i = 0; i < outputNameList.size(); i++) {
                outputNames[i] = "Output- " + outputNameList.get(i).getOutputHardwareNo() + " (" + outputNameList.get(i).getOutputLabel() + ")";
            }
        }
        if (outputNames.length == 0) {
            outputNames = bleedRelay;
        }
        return outputNames;
    }

    private String getPckId(String inpuType) {
        if (inpuType.contains("Output")) {
            return "06";
        } else if (inpuType.contains("Virtual")) {
            return "05";
        }
        return "04";
    }

    private void setAdapter(List<String[]> hashMap) {
        mBinding.recyclerView.setAdapter(new SensorDetailsParamsRvAdapter(hashMap));
    }

    private List<String[]> formpHMap(String[] splitData) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        tempMap.put(inNumber, splitData[3]);
        tempMap.put(inpuType, getValueFromArr(splitData[4], inputTypeArr));
        tempMap.put(seqNumber, splitData[5]);
        tempMap.put(sensorActivation, getValueFromArr(splitData[6], sensorActivationArr));
        tempMap.put(inputLabel, splitData[7]);
        tempMap.put(bufferType, getValueFromArr(splitData[8], bufferArr));
        tempMap.put(tempSensorLinked, getValueFromArr(splitData[9], tempLinkedArr));
        tempMap.put(defTempValue, splitData[10]);
        tempMap.put(smoothingFactor, splitData[11]);
        tempMap.put(alarmLow, splitData[12]);
        tempMap.put(alarmHigh, splitData[13]);
        tempMap.put(calibrationRequiredAlarm, splitData[14]);
        tempMap.put(resetCalibration, getValueFromArr(splitData[15], resetCalibrationArr));

        switch (userType) {
            case 1:
                tempMap.remove(seqNumber);
                tempMap.remove(sensorActivation);
                tempMap.remove(smoothingFactor);
                tempMap.remove(tempSensorLinked);
                break;

            case 2:
                tempMap.remove(seqNumber);
                tempMap.remove(sensorActivation);
                break;
        }
        finalSensorParamList = splitIntoParts(convertMap(tempMap), pageMax);
        return finalSensorParamList.get(0);
    }

    private List<String[]> formORPMap(String[] splitData) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        tempMap.put(inNumber, splitData[3]);
        tempMap.put(inpuType, getValueFromArr(splitData[4], inputTypeArr));
        tempMap.put(seqNumber, splitData[5]);
        tempMap.put(sensorActivation, getValueFromArr(splitData[6], sensorActivationArr));
        tempMap.put(inputLabel, splitData[7]);
        tempMap.put(smoothingFactor, splitData[8]);
        tempMap.put(alarmLow, splitData[9]);
        tempMap.put(alarmHigh, splitData[10]);
        tempMap.put(calibrationRequiredAlarm, splitData[11]);
        tempMap.put(resetCalibration, getValueFromArr(splitData[12], resetCalibrationArr));

        switch (userType) {
            case 1:
                tempMap.remove(sensorActivation);
                tempMap.remove(smoothingFactor);
                tempMap.remove(seqNumber);
                break;

            case 2:
                tempMap.remove(seqNumber);
                tempMap.remove(sensorActivation);
                break;
        }
        finalSensorParamList = splitIntoParts(convertMap(tempMap), pageMax);
        return finalSensorParamList.get(0);
    }

    private List<String[]> formTempMap(String[] splitData) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        tempMap.put(inNumber, splitData[3]);
        tempMap.put(inpuType, getValueFromArr(splitData[4], inputTypeArr));
        tempMap.put(seqNumber, splitData[5]);
        tempMap.put(sensorActivation, getValueFromArr(splitData[6], sensorActivationArr));
        tempMap.put(inputLabel, splitData[7]);
        tempMap.put(defTempValue, splitData[8]);
        tempMap.put(smoothingFactor, splitData[9]);
        tempMap.put(alarmLow, splitData[10]);
        tempMap.put(alarmHigh, splitData[11]);
        tempMap.put(calibrationRequiredAlarm, splitData[12]);
        tempMap.put(resetCalibration, getValueFromArr(splitData[13], resetCalibrationArr));

        switch (userType) {
            case 1:
                tempMap.remove(sensorActivation);
                tempMap.remove(smoothingFactor);
                break;

            case 2:

                break;
        }
        finalSensorParamList = splitIntoParts(convertMap(tempMap), pageMax);
        return finalSensorParamList.get(0);
    }

    private List<String[]> formContConMap(String[] splitData) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        tempMap.put(inNumber, splitData[3]);
        tempMap.put(inpuType, getValueFromArr(splitData[4], inputTypeArr));
        tempMap.put(seqNumber, splitData[5]);
        tempMap.put(sensorActivation, getValueFromArr(splitData[6], sensorActivationArr));
        tempMap.put(inputLabel, splitData[7]);
        tempMap.put(tempSensorLinked, getValueFromArr(splitData[8], tempLinkedArr));
        tempMap.put(defTempValue, splitData[9]);
        tempMap.put(unitOfMeasurement, getValueFromArr(splitData[10], unitArr));
        tempMap.put(cellConstant, splitData[11]);
        if (splitData[12].equals("0")) {
            tempMap.put(compType, "Linear Compensation");
            tempMap.put(compFactor, splitData[13]);
            tempMap.put(smoothingFactor, splitData[14]);
            tempMap.put(alarmLow, splitData[15]);
            tempMap.put(alarmHigh, splitData[16]);
            tempMap.put(calibrationRequiredAlarm, splitData[17]);
            tempMap.put(resetCalibration, getValueFromArr(splitData[18], resetCalibrationArr));
        } else {
            tempMap.put(compType, "Standard NaCl");
            tempMap.put(smoothingFactor, splitData[13]);
            tempMap.put(alarmLow, splitData[14]);
            tempMap.put(alarmHigh, splitData[15]);
            tempMap.put(calibrationRequiredAlarm, splitData[16]);
            tempMap.put(resetCalibration, getValueFromArr(splitData[17], resetCalibrationArr));
        }
        switch (userType) {
            case 1:
                tempMap.remove(compType);
                tempMap.put(compFactor, "0");
                tempMap.remove(compFactor);
                tempMap.remove(smoothingFactor);
                tempMap.remove(seqNumber);
                tempMap.remove(sensorActivation);
                break;

            case 2:
                tempMap.remove(seqNumber);
                tempMap.remove(sensorActivation);
                break;
        }
        finalSensorParamList = splitIntoParts(convertMap(tempMap), pageMax);
        return finalSensorParamList.get(0);
    }

    private List<String[]> formTorConMap(String[] splitData) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        tempMap.put(inNumber, splitData[3]);
        tempMap.put(inpuType, getValueFromArr(splitData[4], inputTypeArr));
        tempMap.put(seqNumber, splitData[5]);
        tempMap.put(sensorActivation, getValueFromArr(splitData[6], sensorActivationArr));
        tempMap.put(inputLabel, splitData[7]);
        tempMap.put(tempSensorLinked, getValueFromArr(splitData[8], tempLinkedArr));
        tempMap.put(defTempValue, splitData[9]);
        tempMap.put(unitOfMeasurement, getValueFromArr(splitData[10], unitArr));
        if (splitData[11].equals("0")) {
            tempMap.put(compType, "Linear Compensation");
            tempMap.put(compFactor, splitData[12]);
            tempMap.put(smoothingFactor, splitData[13]);
            tempMap.put(alarmLow, splitData[14]);
            tempMap.put(alarmHigh, splitData[15]);
            tempMap.put(calibrationRequiredAlarm, splitData[16]);
            tempMap.put(resetCalibration, getValueFromArr(splitData[17], resetCalibrationArr));
        } else {
            tempMap.put(compType, "Standard Nacl");
            tempMap.put(compFactor, "0");
            tempMap.put(smoothingFactor, splitData[12]);
            tempMap.put(alarmLow, splitData[13]);
            tempMap.put(alarmHigh, splitData[14]);
            tempMap.put(calibrationRequiredAlarm, splitData[15]);
            tempMap.put(resetCalibration, getValueFromArr(splitData[16], resetCalibrationArr));
        }

        switch (userType) {
            case 1:
                tempMap.remove(compType);
                tempMap.remove(seqNumber);
                tempMap.remove(compFactor);
                tempMap.remove(smoothingFactor);
                tempMap.remove(sensorActivation);
                break;

            case 2:
                tempMap.remove(seqNumber);
                tempMap.remove(sensorActivation);
                break;
        }
        finalSensorParamList = splitIntoParts(convertMap(tempMap), pageMax);
        return finalSensorParamList.get(0);
    }

    private List<String[]> formAnalogMap(String[] splitData) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        tempMap.put(inNumber, splitData[3]);
        tempMap.put(inpuType, getValueFromArr(splitData[4], inputTypeArr));
        tempMap.put(assigned, getValueFromArr(splitData[5], analogInputArr));
        tempMap.put(seqNumber, splitData[6]);
        tempMap.put(analogType, (splitData[7].equals("0") ? "4-20mA" : "0-10V"));
        tempMap.put(sensorActivation, splitData[8]);
        tempMap.put(inputLabel, splitData[9]);
        tempMap.put(unitOfMeasurement, (splitData[10].equals("0") ? "mA" : "V"));
        tempMap.put(minValue, splitData[11]);
        tempMap.put(maxValue, splitData[12]);
        tempMap.put(smoothingFactor, splitData[13]);
        tempMap.put(alarmLow, splitData[14]);
        tempMap.put(alarmHigh, splitData[15]);
        tempMap.put(calibrationRequiredAlarm, splitData[16]);
        tempMap.put(resetCalibration, getValueFromArr(splitData[17], resetCalibrationArr));

        switch (userType) {
            case 1:
                tempMap.remove(smoothingFactor);
                tempMap.remove(sensorActivation);
                break;

            case 2:
                tempMap.remove(sensorActivation);
                tempMap.remove(seqNumber);
                break;
        }
        finalSensorParamList = splitIntoParts(convertMap(tempMap), pageMax);
        return finalSensorParamList.get(0);
    }

    private List<String[]> formTankMap(String[] splitData) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        tempMap.put(inNumber, splitData[3]);
        tempMap.put(inpuType, getValueFromArr(splitData[4], inputTypeArr));
        tempMap.put(seqNumber, splitData[5]);
        tempMap.put(sensorActivation, splitData[6]);
        tempMap.put(inputLabel, splitData[7]);
        tempMap.put(openMsg, splitData[8]);
        tempMap.put(closeMsg, splitData[9]);
        tempMap.put(interLock, (splitData[10].equals("0") ? "NC" : "NO"));
        tempMap.put(alarm, (splitData[11].equals("0") ? "NC" : "NO"));
        tempMap.put(totalTime, (splitData[12].equals("0") ? "NC" : "NO"));
        tempMap.put(resetTotalTime, splitData[13].equals("0") ? "No Time" : "Reset Time");
        tempMap.put("Total Time Amount", splitData[16]);

        switch (userType) {
            case 1:
                tempMap.remove(sensorActivation);
                break;
            case 2:
                break;
        }
        finalSensorParamList = splitIntoParts(convertMap(tempMap), pageMax);
        return finalSensorParamList.get(0);
    }

    private List<String[]> formDigitalMap(String[] splitData) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        tempMap.put(inNumber, splitData[3]);
        tempMap.put(inpuType, getValueFromArr(splitData[4], inputTypeArr));
        tempMap.put(seqNumber, splitData[5]);
        tempMap.put(sensorActivation, splitData[6]);
        tempMap.put(inputLabel, splitData[7]);
        tempMap.put(openMsg, splitData[8]);
        tempMap.put(closeMsg, splitData[9]);
        tempMap.put(interLock, (splitData[10].equals("0") ? "NC" : "NO"));
        tempMap.put(alarm, (splitData[11].equals("0") ? "NC" : "NO"));
        tempMap.put(totalTime, (splitData[12].equals("0") ? "NC" : "NO"));
        tempMap.put(resetTotalTime, splitData[13].equals("0") ? "No Time" : "Reset Time");
        tempMap.put("Total Time Amount", splitData[16]);

        switch (userType) {
            case 1:
                tempMap.remove(sensorActivation);
                break;
            case 2:
                break;
        }
        finalSensorParamList = splitIntoParts(convertMap(tempMap), pageMax);
        return finalSensorParamList.get(0);
    }

    private List<String[]> formModbusMap(String[] splitData) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        tempMap.put(inNumber, splitData[3]);
        tempMap.put(inpuType, getValueFromArr(splitData[4], inputTypeArr));
        tempMap.put(seqNumber, splitData[5]);
        tempMap.put("modbusType", getValueFromArr(splitData[6], modBusTypeArr));
        tempMap.put("typeOfValueRead", getValueFromArr(splitData[7], typeOfValueRead));
        tempMap.put(sensorActivation, splitData[8]);
        tempMap.put(inputLabel, splitData[9]);
        tempMap.put(unitOfMeasurement, getValueFromArr(splitData[10], modBusUnitArr));
        tempMap.put(minValue, splitData[11]);
        tempMap.put(maxValue, splitData[12]);
        tempMap.put("Diagnostic Sweep",splitData[13]); // todo need 2 change
        tempMap.put(smoothingFactor,splitData[14]);
        tempMap.put(alarmLow, splitData[15]);
        tempMap.put(alarmHigh, splitData[16]);
        tempMap.put(calibrationRequiredAlarm, splitData[17]);
        tempMap.put(resetCalibration, splitData[18]);

        switch (userType) {
            case 1:
                tempMap.remove(smoothingFactor);
                tempMap.remove(sensorActivation);
                tempMap.remove(seqNumber);
                break;
            case 2:
                tempMap.remove(sensorActivation);
                tempMap.remove(seqNumber);
                break;
        }
        finalSensorParamList = splitIntoParts(convertMap(tempMap), pageMax);
        return finalSensorParamList.get(0);
    }

    private List<String[]> formFlowMap(String[] splitData) {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
        tempMap.put(inNumber, splitData[3]);
        tempMap.put(inpuType, getValueFromArr(splitData[4], inputTypeArr));
        tempMap.put("Flow Meter Type", getValueFromArr(splitData[5], flowMeterTypeArr));
        tempMap.put(seqNumber, splitData[6]);
        tempMap.put(sensorActivation, getValueFromArr(splitData[7], sensorActivationArr));
        tempMap.put(inputLabel, splitData[8]);
        tempMap.put("Flow Unit", getValueFromArr(splitData[9], flowUnitArr));

        if (splitData[5].equals("0")) {
            tempMap.put("Rate Unit", splitData[10]);
            tempMap.put("Flow Meter Max", splitData[11]);
            tempMap.put("Flow Meter Min", splitData[12]);
            tempMap.put(smoothingFactor, splitData[13]);
            tempMap.put("Totalizer Alarm", splitData[14]);
            tempMap.put("Reset Flow Total", splitData[15].equals("0") ? "No Reset" : "Reset");
            tempMap.put("Schedule Reset", getValueFromArr(splitData[16], scheduleResetArr));
            tempMap.put("Set Flow Total", splitData[17]);
            tempMap.put(alarmLow, splitData[18]);
            tempMap.put(alarmHigh, splitData[19]);
            tempMap.put(calibrationRequiredAlarm, splitData[20]);
            tempMap.put(resetCalibration, getValueFromArr(splitData[21], resetCalibrationArr));

        } else if (splitData[5].equals("1")) {
            tempMap.put("Volume/Contactor", splitData[10]);
            tempMap.put("Totalizer Alarm", splitData[11]);
            tempMap.put("Reset Flow Total", splitData[12].equals("0") ? "No Reset" : "Reset");
            tempMap.put("Set Flow Total", splitData[13]);
            tempMap.put("Schedule Reset", getValueFromArr(splitData[14], scheduleResetArr));
            tempMap.put(alarmLow, splitData[15]);
            tempMap.put(alarmHigh, splitData[16]);

        } else if (splitData[5].equals("2")) {
            tempMap.put("Rate Unit", splitData[10]);
            tempMap.put("K-Factor", splitData[11]);
            tempMap.put("Totalizer Alarm", splitData[12]);
            tempMap.put("Reset Flow Total", splitData[13].equals("0") ? "No Reset" : "Reset");
            tempMap.put("Set Flow Total", splitData[14]);
            tempMap.put("Schedule Reset", getValueFromArr(splitData[15], scheduleResetArr));
            tempMap.put(alarmLow, splitData[16]);
            tempMap.put(alarmHigh, splitData[17]);

        } else if (splitData[5].equals("3")) {
            tempMap.put("Volume/Contactor", splitData[10]);
            tempMap.put("Rate Unit", splitData[11]);
            tempMap.put("Total Alarm Mode", splitData[12].equals("0") ? "Interlock" : "Maintain");
            tempMap.put("Flow Alarm Mode", getValueFromArr(splitData[13], flowAlarmMode));
            tempMap.put("Flow Alarm Delay", splitData[14]);
            tempMap.put("Flow Alarm Clear", splitData[15]);
            // OutPutRelayLink todo Hold
            tempMap.put("Totalizer Alarm", splitData[17]);
            tempMap.put("Reset Flow Total", splitData[18].equals("0") ? "No Reset" : "Reset");
            tempMap.put("Set Flow Total", splitData[19]);
            tempMap.put("Schedule Reset", getValueFromArr(splitData[20], scheduleResetArr));
            tempMap.put(alarmLow, splitData[21]);
            tempMap.put(alarmHigh, splitData[22]);
        }

        switch (userType) {
            case 1:
                tempMap.remove(smoothingFactor);
                tempMap.remove(sensorActivation);
                tempMap.remove(seqNumber);
                break;
            case 2:
                tempMap.remove(seqNumber);
                tempMap.remove(sensorActivation);
                break;
        }
        finalSensorParamList = splitIntoParts(convertMap(tempMap), pageMax);
        return finalSensorParamList.get(0);
    }

    private List<String[]> convertMap(LinkedHashMap<String, String> mMap) {
        List<String[]> mList = new ArrayList<>();
        for (Map.Entry<String, String> entry : mMap.entrySet()) {
            mList.add(new String[]{entry.getKey(), entry.getValue()});
        }
        return mList;
    }

    public List<List<String[]>> splitIntoParts(List<String[]> mList, int itemsPerList) {
        List<List<String[]>> splittedList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i += itemsPerList) {
            splittedList.add(mList.subList(i, Math.min(mList.size(), i + itemsPerList)));
        }
        return splittedList;
    }
}
