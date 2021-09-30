package com.ionexchange.Fragments.Configuration.HomeScreen;

import android.content.DialogInterface;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ionexchange.Adapters.SelectSensorListAdapter;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.MainConfigurationDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.RvCheckedChange;
import com.ionexchange.Others.DialogFrag;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSelectsensorBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentSelectSensors extends Fragment implements CompoundButton.OnCheckedChangeListener, RvCheckedChange {
    FragmentSelectsensorBinding mBinding;


    DialogFrag fragment;
    int screenNo, layoutNo, windowNo, pageNo;
    String sensorType;
    WaterTreatmentDb dB;
    InputConfigurationDao inputConfigurationDao;
    VirtualConfigurationDao virtualConfigurationDao;
    OutputConfigurationDao outputConfigurationDao;
    MainConfigurationDao mainConfigurationDao;

    List<VirtualConfigurationEntity> virtualConfigurationEntityList;
    List<OutputConfigurationDao> outputConfigurationDaoList;
    List<InputConfigurationEntity> inputConfigurationEntityList;

    public FragmentSelectSensors(DialogFrag fragment, int screenNo, int layoutNo, int windowNo, int pageNo) {
        this.fragment = fragment;
        this.screenNo = screenNo;
        this.layoutNo = layoutNo;
        this.windowNo = windowNo;
        this.pageNo = pageNo;
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

        inputConfigurationEntityList = new ArrayList<>();
        outputConfigurationDaoList = new ArrayList<>();
        virtualConfigurationEntityList = new ArrayList<>();

        dB = WaterTreatmentDb.getDatabase(getContext());
        inputConfigurationDao = dB.inputConfigurationDao();
        virtualConfigurationDao = dB.virtualConfigurationDao();
        outputConfigurationDao = dB.outputConfigurationDao();
        mainConfigurationDao = dB.mainConfigurationDao();

        inputConfigurationEntityList = inputConfigurationDao.
                getInputHardWareNoConfigurationEntityList(0, 14);

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

    private void setAdapter(int mode, boolean b, String type, List configurationList, int adapterType) {
        if (b) {
            sensorType = type;
            switch (mode) {
                case 0:
                    mBinding.selectSensorRv.setAdapter(new SelectSensorListAdapter(this, configurationList, adapterType, "type"));
                    break;
                case 1:
                    mBinding.selectSensorRv.setAdapter(new SelectSensorListAdapter(this, configurationList, adapterType));
                    break;
                case 2:
                    mBinding.selectSensorRv.setAdapter(new SelectSensorListAdapter(this, configurationList, adapterType, true));
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        inputConfigurationEntityList = new ArrayList<>();
        outputConfigurationDaoList = new ArrayList<>();
        virtualConfigurationEntityList = new ArrayList<>();
        switch (compoundButton.getId()) {
            case R.id.inputRb:
                setAdapter(0, b, "Sensor", inputConfigurationDao.getInputHardWareNoConfigurationEntityList(1, 13), 0);
                break;

            case R.id.analogRb:
                setAdapter(0, b, "Analog", inputConfigurationDao.getInputHardWareNoConfigurationEntityList(14, 21), 0);
                break;

            case R.id.flowMeterRb:
                setAdapter(0, b, "Flowmeter", inputConfigurationDao.getInputHardWareNoConfigurationEntityList(22, 29), 0);
                break;

            case R.id.digitalRb:
                setAdapter(0, b, "Digital", inputConfigurationDao.getInputHardWareNoConfigurationEntityList(30, 37), 0);
                break;

            case R.id.modbusRb:
                setAdapter(0, b, "Modbus", inputConfigurationDao.getInputHardWareNoConfigurationEntityList(3, 13), 0);
                break;

            case R.id.tankRb:
                setAdapter(0, b, "Tank", inputConfigurationDao.getInputHardWareNoConfigurationEntityList(38, 45), 0);
                break;

            case R.id.virtualSensorRb:
                setAdapter(1, b, "Virtual", virtualConfigurationDao.getVirtualHardWareNoConfigurationEntityList(46, 53), 1);
                break;

            case R.id.outputRb:
                setAdapter(2, b, "Relay Output", outputConfigurationDao.getOutputHardWareNoConfigurationEntityList(1, 14), 2);
                break;

            case R.id.generalRb:
                setAdapter(2, b, "Analog Output", outputConfigurationDao.getOutputHardWareNoConfigurationEntityList(15, 22), 2);
                break;

        }
    }


    @Override
    public void onCheckChanged(Object mObj, CompoundButton compoundButton, int mode) {
        if (compoundButton.isChecked()) {
            switch (mode) {
                case 0:
                    InputConfigurationEntity inputObj = (InputConfigurationEntity) mObj;
                    confirmDialog(compoundButton, inputObj.hardwareNo, inputObj.inputType, 0, sensorType, inputObj.subValueOne, inputObj.subValueTwo, inputObj.flagKey);
                    break;
                case 1:
                    VirtualConfigurationEntity virtualObj = (VirtualConfigurationEntity) mObj;
                    confirmDialog(compoundButton, virtualObj.hardwareNo, virtualObj.virtualType, 0, sensorType, virtualObj.subValueOne, virtualObj.subValueTwo, 1);
                    break;

                case 2:
                    OutputConfigurationEntity outputObj = (OutputConfigurationEntity) mObj;
                    confirmDialog(compoundButton, outputObj.outputHardwareNo, outputObj.outputType, 0, sensorType, outputObj.outputStatus, outputObj.outputMode, 1);
                    break;
            }

        }
    }

    void mainConfigurationEntity(int hardwareNo, String inputType, int SeqNo, String sensorName, String low, String high, int flag) {
        MainConfigurationEntity mainConfigurationEntity = new MainConfigurationEntity
                (mainConfigurationDao.getLastSno() + 1,
                        screenNo, layoutNo, windowNo, pageNo, hardwareNo, inputType, SeqNo, sensorName, flag);
        List<MainConfigurationEntity> entryListUpdate = new ArrayList<>();
        entryListUpdate.add(mainConfigurationEntity);
        updateToDb(entryListUpdate);
    }

    public void updateToDb(List<MainConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        MainConfigurationDao dao = db.mainConfigurationDao();
        dao.insert(entryList.toArray(new MainConfigurationEntity[0]));
    }


    void confirmDialog(CompoundButton compoundButton, int hardwareNo, String inputType, int SeqNo, String sensorName, String low, String high, int flag) {
        new MaterialAlertDialogBuilder(getContext()).setTitle("Confirmation").
                setMessage("Please confirm your selected sensor is - " + compoundButton.getText().toString())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mainConfigurationDao.getInputType(screenNo, layoutNo, windowNo, pageNo) != null
                                && mainConfigurationDao.getInputType(screenNo, layoutNo, windowNo, pageNo).equals("Sensor not Added")) {
                            mainConfigurationDao.delete(screenNo, layoutNo, windowNo, pageNo);
                        }
                        mainConfigurationEntity(hardwareNo, inputType, SeqNo, sensorName, low, high, flag);
                        dialog.dismiss();
                        fragment.dismiss();
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                compoundButton.setChecked(false);
            }
        }).show().setCancelable(false);
    }
}


