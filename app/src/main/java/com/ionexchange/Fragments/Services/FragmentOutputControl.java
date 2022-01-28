package com.ionexchange.Fragments.Services;

import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getPosition;
import static com.ionexchange.Others.ApplicationClass.outputControl;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.OUTPUT_CONTROL_CONFIG;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Adapters.OutputControlRvAdapter;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputKeepAliveEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Interface.RvOutputControl;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentOutputcontrolBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentOutputControl extends Fragment implements RvOutputControl, DataReceiveCallback {
    FragmentOutputcontrolBinding mBinding;
    OutputControlRvAdapter outputControlRvAdapter;
    OutputConfigurationDao dao;
    OutputKeepAliveDao outputKeepAliveDao;
    WaterTreatmentDb db;
    ApplicationClass mAppClass;
    List<OutputConfigurationEntity> outputConfigurationEntityList;
    List<OutputKeepAliveEntity> outputKeepAliveEntityList;
    String Hours;
    BaseActivity mActivity;
    public static boolean canReceive = true;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_outputcontrol, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();

        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.outputConfigurationDao();
        outputKeepAliveDao = db.outputKeepAliveDao();
        outputConfigurationEntityList = new ArrayList<>();
        outputConfigurationEntityList = dao.getOutputConfigurationEntityList();
        outputKeepAliveEntityList = outputKeepAliveDao.getOutputList();
        outputControlRvAdapter = new OutputControlRvAdapter(this, outputConfigurationEntityList,outputKeepAliveEntityList);
        mBinding.outputControlRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.outputControlRv.setAdapter(outputControlRvAdapter);
        outputKeepAliveDao.getOutputLiveList().observe(getViewLifecycleOwner(), new Observer<List<OutputKeepAliveEntity>>() {
            @Override
            public void onChanged(List<OutputKeepAliveEntity> outputKeepAliveEntities) {
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {*/
                if (canReceive) {
                    outputControlRvAdapter.updateData(dao.getOutputConfigurationEntityList(), outputKeepAliveDao.getOutputList());
                }
                   /* }
                },500);*/

            }
        });

    }


    void TimerPicker(View view, int outputNumber, AutoCompleteTextView outputControl) {
        int clockFormat = TimeFormat.CLOCK_24H;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("HH:MM")
                .build();
        materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                view.setVisibility(View.VISIBLE);
                Hours = formDigits(2, String.valueOf(materialTimePicker.getHour())) +
                        formDigits(2, String.valueOf(materialTimePicker.getMinute())) + "00";
                sendData(outputNumber, outputControl);
                canReceive = true;
            }
        });
        materialTimePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialTimePicker.dismiss();
                canReceive = true;
            }
        });
        materialTimePicker.show(getParentFragmentManager(), "fragment_tag");
    }

    @Override
    public void OnDataReceive(String data) {
        if (data.equals("FailedToConnect")) {
            //mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("pckError")) {
           // mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("sendCatch")) {
           // mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getActivity(), getString(R.string.timeout));
        }
        if (data != null) {
            handleResponse(data.split("\\*")[1].split(RES_SPILT_CHAR));
        }
    }

    private void handleResponse(String[] splitData) {
        FragmentOutputControl.canReceive = true;
    }

    @Override
    public void click(TextView outputName, TextView outputType, AutoCompleteTextView outputControl, View view, int outputNumber, int pos) {
        if (outputControl.getText().toString().equals("Manual ON for")) {
            TimerPicker(view, outputNumber, outputControl);
        } else {
            view.setVisibility(View.GONE);
        }
        switch (pos) {
            case 0:
            case 1:
            case 2:
            case 3:
               sendData(outputNumber, outputControl);
                break;
            case 4:
                break;
        }


    }

    void sendData(int outputNumber, AutoCompleteTextView autoCompleteTextView) {

        String outputType = getPosition(0, autoCompleteTextView.getText().toString(), outputControl);
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + CONN_TYPE + SPILT_CHAR +
                OUTPUT_CONTROL_CONFIG + SPILT_CHAR + formDigits(2, String.valueOf(outputNumber)) + SPILT_CHAR +
                (outputType.equals("4") ? getPosition(1, autoCompleteTextView.getText().toString(), outputControl) + Hours:
                        getPosition(0, autoCompleteTextView.getText().toString(), outputControl)));
    }
}
