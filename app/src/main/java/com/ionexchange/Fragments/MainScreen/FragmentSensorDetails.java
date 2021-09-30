package com.ionexchange.Fragments.MainScreen;

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

import com.ionexchange.Adapters.SensorDetailsParamsRvAdapter;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSensorDetailsBinding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.ionexchange.Others.ApplicationClass.bufferArr;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getValueFromArr;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.tempLinkedArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;


public class FragmentSensorDetails extends Fragment {
    FragmentSensorDetailsBinding mBinding;
    Context mContext;
    ApplicationClass mAppClass;
    List<List<String[]>> printablePages;
    int currentPage = 0;
    // String inputNumber;
    String inNumber = "Input Number", inpuType = "Input Type", seqNumber = "Sequence Number", sensorActivation = "Sensor Activation",
            inputLabel = "Input Label", bufferType = "Buffer Type", tempSensorLinked = "Temperature Sensor Linked", defTempValue = "Default Temperature Value",
            smoothingFactor = "Smoothing Factor", alarmLow = "Alarm Low", alarmHigh = "Alarm High", calibrationRequiredAlarm = "Calibration Required Alarm",
            resetCalibration = "Reset Calibration", sensorStatus = "SensorStatus";

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
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        getTCPData("01");
        mBinding.btnTrendCalibartion.setChecked(true);
        getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), new FragmentSensorCalibration()).commit();
        mBinding.btnTrendCalibartion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), new FragmentSensorCalibration()).commit();
                    mBinding.txtTrendCalibration.setText("TREND");
                    mBinding.viewTrendCalibration.setBackground(getContext().getDrawable(R.drawable.graph));
                } else {
                    getParentFragmentManager().beginTransaction().replace(mBinding.sensorDetailsFrame.getId(), new FragmentSensorStatistics()).commit();
                    mBinding.txtTrendCalibration.setText("CALIBRATION");
                    mBinding.viewTrendCalibration.setBackground(getContext().getDrawable(R.drawable.flask));
                }
            }
        });
        mBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage < printablePages.size() - 1) {
                    currentPage = ++currentPage;
                } else {
                    mAppClass.showSnackBar(getContext(), "End of Page");
                }
                if (currentPage < printablePages.size()) {
                    if (printablePages.get(currentPage) != null && !printablePages.get(currentPage).isEmpty()) {
                        setAdapter(printablePages.get(currentPage));
                    }
                }
            }

        });

        mBinding.btnPerv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage > 0) {
                    currentPage = --currentPage;
                    if (printablePages.get(currentPage) != null && !printablePages.get(currentPage).isEmpty()) {
                        setAdapter(printablePages.get(currentPage));
                    }
                } else {
                    mAppClass.showSnackBar(getContext(), "End of Page");
                }
            }
        });
    }

    private void getTCPData(String inputNumber) {
        mAppClass.sendPacket(new DataReceiveCallback() {
            @Override
            public void OnDataReceive(String data) {
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
                if (data != null && !data.equals("")) {
                    String[] splitData = data.split("\\*")[1].split("\\$");
                    if (splitData[1].equals("04")) {
                        if (splitData[0].equals(READ_PACKET)) {
                            if (splitData[2].equals(RES_SUCCESS)) {
                                switch (getValueFromArr(splitData[4], inputTypeArr)) {
                                    case "pH":
                                        setAdapter(formpHMap(data));
                                        break;
                                }
                            } else if (splitData[2].equals(RES_FAILED)) {
                                mAppClass.showSnackBar(getContext(), getString(R.string.readFailed));
                            }
                        }
                    } else {
                        mAppClass.showSnackBar(getContext(), getString(R.string.wrongPack));
                    }
                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, inputNumber));
    }

    private void setAdapter(List<String[]> hashMap) {
        mBinding.recyclerView.setAdapter(new SensorDetailsParamsRvAdapter(hashMap));
    }

    private List<String[]> formpHMap(String data) {
        String[] splitData = data.split("\\*")[1].split("\\$");
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
        }
        List<String[]> mList = new ArrayList<>();
        for (Map.Entry<String, String> entry : tempMap.entrySet()) {
            mList.add(new String[]{entry.getKey(), entry.getValue()});
        }

        printablePages = splitIntoParts(mList, 8);
        return printablePages.get(0);
    }

    public List<List<String[]>> splitIntoParts(List<String[]> passEntities, int itemsPerList) {
        List<List<String[]>> splittedList = new ArrayList<>();
        for (int i = 0; i < passEntities.size(); i += itemsPerList) {
            splittedList.add(passEntities.subList(i, Math.min(passEntities.size(), i + itemsPerList)));
        }
        return splittedList;
    }


}
