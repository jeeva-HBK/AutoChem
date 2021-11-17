package com.ionexchange.Fragments.Services;

import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.ionexchange.Adapters.DiagnosticDataAdapter;
import com.ionexchange.Database.Dao.DiagnosticDataDao;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Entity.DiagnosticDataEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentDiagnosticsBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentDiagnosticsData extends Fragment implements DataReceiveCallback, CompoundButton.OnCheckedChangeListener {
    FragmentDiagnosticsBinding mBinding;
    ApplicationClass mAppClass;
    private static final String TAG = "FragmentDiagnosticsData";
    DiagnosticDataDao dao;
    WaterTreatmentDb dB;
    InputConfigurationDao inputDao;
    List<DiagnosticDataEntity> diagnosticDataEntityList;
    int minSensor = 0,
            maxSensor = 0,
            minModbusSensor = 0,
            maxModbusSensor = 0, minFlowSensor = 0,
            maxFlowSensor = 0, minTemp, maxTemp = 0,
            minAnalog = 0, maxAnalog = 0, minTank = 0, maxTank = 0,
            minDigital = 0, maxDigital = 0;
    boolean sensorBool = false, modBusBool = false, flowMeterBool = false, tempBool = false, analogBool = false, tankBool = false, digitalBool = false, empty = false;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_diagnostics, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        diagnosticDataEntityList = new ArrayList<>();
        dB = WaterTreatmentDb.getDatabase(getContext());
        dao = dB.diagnosticDataDao();
        inputDao = dB.inputConfigurationDao();
        diagnosticDataEntityList = dao.getDiagnosticDataList();
        mBinding.readNowBtn.setOnClickListener(View -> {
            sendPacket("0");
        });
        mBinding.readNowBtn.performClick();
        setAdapter(diagnosticDataEntityList);
        mBinding.readNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });
    }

    private void sendPacket(String setID) {
        // todo need to change
        mAppClass.sendPacket(this, "1234$0$1$12$" + setID);
    }

    private void setDiagnosticsDb(String hardwareNo, String diagData, String timeStamp) {


        if (dao.getDiagnosticDataList() != null) {
            DiagnosticDataEntity diagnosticDataEntity =
                    new DiagnosticDataEntity(dao.getLastSno() + 1, Integer.parseInt(hardwareNo), diagData, timeStamp);
            List<DiagnosticDataEntity> entryListUpdate = new ArrayList<>();
            entryListUpdate.add(diagnosticDataEntity);
            insertDiagnosticDataDb(entryListUpdate);
        }
    }

    private void insertDiagnosticDataDb(List<DiagnosticDataEntity> entryListUpdate) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        DiagnosticDataDao dao = db.diagnosticDataDao();
        dao.insert(entryListUpdate.toArray(new DiagnosticDataEntity[0]));
    }

    @Override
    public void OnDataReceive(String data) {
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        } else if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        } else if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        } else if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.timeout));
        } else if (data != null) {
            handleResponse(data.split("\\*")[1].split(RES_SPILT_CHAR));
        }
    }

    void setAdapter(List<DiagnosticDataEntity> diagnosticDataEntityList) {
        mBinding.diagnosticRv.scrollToPosition(dao.getDiagnosticDataList().size() - 1);
        mBinding.diagnosticRv.setLayoutManager(new LinearLayoutManager(getContext()));
        if (dao.getDiagnosticDataList() != null) {
            mBinding.diagnosticRv.setAdapter(new DiagnosticDataAdapter(diagnosticDataEntityList, inputDao));
        }
    }

    private void handleResponse(String[] splitData) {
        if (splitData[1].equals("12")) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    int i = 0;
                    int j = 9;
                    String currentTime = new SimpleDateFormat("yyyy.MM.dd | HH.mm.ss", Locale.getDefault()).format(new Date());

                    while (i < j) {
                        if (splitData[i + 4].length() > 2) {
                            setDiagnosticsDb(splitData[i + 4].substring(0, 2),
                                    splitData[i + 4].substring(2, splitData[i + 4].length()), currentTime);
                        } else {
                            setDiagnosticsDb(splitData[i + 4], "No Data Received", currentTime);
                        }
                        i++;
                        if (i == 4) {
                            j = 7;
                        }
                    }


                    if (splitData[3].equals("0")) {
                        sendPacket("1");
                    } else if (splitData[3].equals("1")) {
                        sendPacket("2");
                    } else if (splitData[3].equals("2")) {
                        sendPacket("3");
                    } else if (splitData[3].equals("3")) {
                        sendPacket("4");
                    } else if (splitData[3].equals("4")) {
                        sendPacket("5");
                    }
                    setAdapter(diagnosticDataEntityList);
                }
            }
        } else {
            Log.e(TAG, "handleResponse: Received Wrong Pck");
        }
    }

    void filterDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_screen, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        Chip sensor = dialogView.findViewById(R.id.Sensor);
        Chip modbus = dialogView.findViewById(R.id.modbusSensor);
        Chip flowSensor = dialogView.findViewById(R.id.flowSensor);
        Chip temp = dialogView.findViewById(R.id.tempSensor);
        Chip analog = dialogView.findViewById(R.id.analogSensor);
        Chip tank = dialogView.findViewById(R.id.tankSensor);

        Chip digital = dialogView.findViewById(R.id.digitalSensor);
        ChipGroup group = dialogView.findViewById(R.id.radio_group);
        Button ok = dialogView.findViewById(R.id.ok);
        Button cancel = dialogView.findViewById(R.id.cancel);

        sensor.setOnCheckedChangeListener(this);
        modbus.setOnCheckedChangeListener(this);
        flowSensor.setOnCheckedChangeListener(this);
        temp.setOnCheckedChangeListener(this);
        analog.setOnCheckedChangeListener(this);
        tank.setOnCheckedChangeListener(this);
        digital.setOnCheckedChangeListener(this);

        if (sensorBool) {
            sensor.setChecked(true);
        }
        if (modBusBool) {
            modbus.setChecked(true);
        }
        if (analogBool) {
            analog.setChecked(true);
        }
        if (digitalBool) {
            digital.setChecked(true);
        }
        if (flowMeterBool) {
            flowSensor.setChecked(true);
        }
        if (tankBool) {
            tank.setChecked(true);
        }
        if (tempBool) {
            temp.setChecked(true);
        }


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (empty){
                    diagnosticDataEntityList = dao.getDiagnosticDataList();
                }else {
                    diagnosticDataEntityList = dao.getInputHardWareNoDiagnosticDataEntity(
                            minSensor, maxSensor, minModbusSensor, maxModbusSensor, minTemp, maxTemp,
                            minAnalog, maxAnalog, minFlowSensor, maxFlowSensor, minDigital, maxDigital,
                            minTank, maxTank);
                }
                setAdapter(diagnosticDataEntityList);
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.Sensor:
                    if (isChecked) {
                        minSensor = 1;
                        maxSensor = 4;
                        sensorBool = true;
                    } else {
                        minSensor = 0;
                        maxSensor = 0;
                        sensorBool = false;
                    }
                    break;
                case R.id.modbusSensor:
                    if (isChecked) {
                        minModbusSensor = 5;
                        maxModbusSensor = 14;
                        modBusBool = true;
                    } else {
                        minModbusSensor = 0;
                        maxModbusSensor = 0;
                        modBusBool = false;
                    }
                    break;
                case R.id.flowSensor:
                    if (isChecked) {
                        minFlowSensor = 26;
                        maxFlowSensor = 33;
                        flowMeterBool = true;
                    } else {
                        minFlowSensor = 0;
                        maxFlowSensor = 0;
                        flowMeterBool = false;
                    }
                    break;
                case R.id.tempSensor:
                    if (isChecked) {
                        minTemp = 15;
                        maxTemp = 17;
                        tempBool = true;
                    } else {
                        minTemp = 0;
                        maxTemp = 0;
                        tempBool = false;
                    }

                    break;
                case R.id.analogSensor:
                    if (isChecked) {
                        minAnalog = 18;
                        maxAnalog = 25;
                        analogBool = true;
                    } else {
                        minAnalog = 0;
                        maxAnalog = 0;
                        analogBool = false;
                    }
                    break;
                case R.id.tankSensor:
                    if (isChecked) {
                        minTank = 42;
                        maxTank = 49;
                        tankBool = true;
                    } else {
                        minTank = 0;
                        maxTank = 0;
                        tankBool = false;
                    }

                    break;

                case R.id.digitalSensor:
                    if (isChecked) {
                        minDigital = 34;
                        maxDigital = 41;
                        digitalBool = true;
                    } else {
                        minDigital = 0;
                        maxDigital = 0;
                        digitalBool = false;
                    }
                    break;
            }
        }else {
            empty = true;
            sensorBool = false;
            digitalBool = false;
            tankBool = false;
            analogBool = false;
            tempBool = false;
            flowMeterBool = false;
            modBusBool = false;
        }

    }
}
