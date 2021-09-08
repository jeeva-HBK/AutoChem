package com.ionexchange.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.SelectSensorListAdapter;
import com.ionexchange.Database.Dao.MainConfigurationDao;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.Others.DialogFrag;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSelectsensorBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.macAddress;

public class FragmentSelectSensors extends Fragment implements CompoundButton.OnCheckedChangeListener, RvOnClick {
    FragmentSelectsensorBinding mBinding;

    RvOnClick rvOnClick;
    DialogFrag fragment;
    int screenNo, layoutNo, windowNo;
    WaterTreatmentDb dB;
    MainConfigurationDao dao;

    public FragmentSelectSensors(DialogFrag fragment, int screenNo, int layoutNo, int windowNo) {
        this.fragment = fragment;
        this.screenNo = screenNo;
        this.layoutNo = layoutNo;
        this.windowNo = windowNo;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_selectsensor, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dB = WaterTreatmentDb.getDatabase(getContext());
        dao = dB.mainConfigurationDao();

        mBinding.inputRb.setOnCheckedChangeListener(this);
        mBinding.analogRb.setOnCheckedChangeListener(this);
        mBinding.digitalRb.setOnCheckedChangeListener(this);
        mBinding.flowMeterRb.setOnCheckedChangeListener(this);
        mBinding.modbusRb.setOnCheckedChangeListener(this);
        mBinding.generalRb.setOnCheckedChangeListener(this);
        mBinding.tankRb.setOnCheckedChangeListener(this);
        mBinding.virtualSensorRb.setOnCheckedChangeListener(this);
        mBinding.outputRb.setOnCheckedChangeListener(this);

        mBinding.selectSensorRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mBinding.inputRb.performClick();
    }

    private void setAdapter(boolean b, String type, String[] mList) {
        if (b) {
            mBinding.selectSensorRv.setAdapter(new SelectSensorListAdapter(mList, type, rvOnClick = this, screenNo, layoutNo, windowNo));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.inputRb:
                setAdapter(b, "Sensor", new String[]{"PH", "ORP", "Contacting Conductivity", "Toradial Conductivity", "Temperature 1", "Temperature 2", "Temperature 3"});
                break;

            case R.id.analogRb:
                setAdapter(b, "Analog", new String[]{"4-20 Analog 1", "4-20 Analog 2", "4-20 Analog 3", "4-20 Analog 4",
                        "4-20 Analog 5", "4-20 Analog 6", "0 10v Analog 7", "0 10v Analog 8"});
                break;

            case R.id.digitalRb:
                setAdapter(b, "Digital", new String[]{"Digital Sensor 1", "Digital Sensor 2", "Digital Sensor 3", "Digital Sensor 4",
                        "Digital Sensor 5", "Digital Sensor 6", "Digital Sensor 7", "Digital Sensor 8"});
                break;

            case R.id.flowMeterRb:
                setAdapter(b, "Flowmeter", new String[]{"Flow meter 1", "Flow meter 2", "Flow meter 3", "Flow meter 4", "Flow meter 5", "Flow meter 6", "Flow meter 7", "Flow meter 8"});
                break;

            case R.id.modbusRb:
                setAdapter(b, "Modbus", new String[]{"ST-500", "CR300 CS", "CR300 CU", "ST-590", "ST-588", "ST-500 RO"});
                break;


            case R.id.tankRb:
                setAdapter(b, "Tank", new String[]{"Level Sensor 1", "Level Sensor 2", "Level Sensor 3", "Level Sensor 4",
                        "Level Sensor 5", "Level Sensor 6", "Level Sensor 7", "Level Sensor 8"});
                break;

            case R.id.virtualSensorRb:
                setAdapter(b, "Virtual", new String[]{"Virtual Sensor 1", "Virtual Sensor 2", "Virtual Sensor 3", "Virtual Sensor 4",
                        "Virtual Sensor 5", "Virtual Sensor 6", "Virtual Sensor 7", "Virtual Sensor 8"});
                break;

            case R.id.outputRb:
                setAdapter(b, "Relay Output", new String[]{"Output 1", "Output 2", "Output 3", "Output 4", "Output 5",
                        "Output 6", "Output 7", "Output 8", "Output 9", "Output 10",
                        "Output 11", "Output 12", "Output 13", "Output 14"});
                break;

            case R.id.generalRb:
                setAdapter(b, "Analog Output", new String[]{"Output 15", "Output 16", "Output 17", "Output 18", "Output 19",
                        "Output 20", "Output 21", "Output 22"});
                break;

        }
    }

    @Override
    public void onClick(int sensorInputNo) {

    }

    @Override
    public void onClick(String sensorInputNo) {

    }

    @Override
    public void onClick(String sensorInputNo, String type, int position) {
        int sensor_sequence_number = 0;
        String input_type = "Sensor";
        /*if(type.equalsIgnoreCase("Sensor")){
            switch (position){
                case 0:
                case 1:
                case 2:
                case 3:
                    input_type = sensorInputNo;
                    sensor_sequence_number = 1;
                    break;
                case 4:
                    input_type = "Temperature";
                    sensor_sequence_number = 1;
                    break;
                case 5:
                    input_type = "Temperature";
                    sensor_sequence_number = 2;
                    break;
                case 6:
                    input_type = "Temperature";
                    sensor_sequence_number = 3;
                    break;
            }
        } else {*/
        input_type = type;
        sensor_sequence_number = position + 1;
        //}
        Log.e("final_valueto_insert", screenNo + " " + layoutNo + " " + type + " " + sensor_sequence_number + " ");
        Log.e("sensor", sensorInputNo + "type " + type + "sequence_number " + sensor_sequence_number);
        MainConfigurationEntity entityUpdate = new MainConfigurationEntity(screenNo, layoutNo, windowNo, type, sensor_sequence_number, sensorInputNo, macAddress);
        List<MainConfigurationEntity> entryListUpdate = new ArrayList<>();
        entryListUpdate.add(entityUpdate);
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        MainConfigurationDao dao = db.mainConfigurationDao();
        dao.insert(entryListUpdate.toArray(new MainConfigurationEntity[0]));
        fragment.dismiss();
    }
}
