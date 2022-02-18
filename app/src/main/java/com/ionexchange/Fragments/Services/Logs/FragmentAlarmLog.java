package com.ionexchange.Fragments.Services.Logs;

import static com.ionexchange.Others.ApplicationClass.alarmArr;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.PacketControl.ACK;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_LOCKOUT;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINID;

import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.ionexchange.Adapters.AlarmLogRvAdapter;
import com.ionexchange.Database.Dao.AlarmLogDao;
import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.BtnOnClick;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.Others.EventLogDemo;
import com.ionexchange.R;
import com.ionexchange.Singleton.ApiService;
import com.ionexchange.Singleton.SharedPref;
import com.ionexchange.databinding.FragmentAlarmLogBinding;

import java.util.Date;
import java.util.List;


public class FragmentAlarmLog extends Fragment implements BtnOnClick, DataReceiveCallback {
    FragmentAlarmLogBinding mBinding;
    AlarmLogRvAdapter alarmLogRvAdapter;
    WaterTreatmentDb waterTreatmentDb;
    AlarmLogDao alarmLogDao;
    ApplicationClass mAppClass;
    List<AlarmLogEntity> alarmLogEntityList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm_log, container, false);
        return mBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = ApplicationClass.getInstance();
        waterTreatmentDb = WaterTreatmentDb.getDatabase(getContext());
        alarmLogDao = waterTreatmentDb.alarmLogDao();
        alarmLogEntityList = alarmLogDao.getAlarmLogList();

        /*LiveData*/
        alarmLogDao.getAlarmList().observe(getViewLifecycleOwner(), new Observer<List<AlarmLogEntity>>() {
            @Override
            public void onChanged(List<AlarmLogEntity> alarmLogEntities) {
                setAdapter(alarmLogEntities);
            }
        });

        mBinding.alertsType.setAdapter(getAdapter(alarmArr, getContext()));
        mBinding.alertsType.setOnClickListener(v -> {
            mBinding.alertsType.showDropDown();
        });

        mBinding.roundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.edtFormDate.getText().toString().isEmpty() && !mBinding.edtToDate.getText().toString().isEmpty()) {
                    if (!mBinding.alertsType.getText().toString().isEmpty()) {
                        alarmLogEntityList = alarmLogDao.getDateWiseAndType(mBinding.edtFormDate.getText().toString(), mBinding.edtToDate.getText().toString(), mBinding.alertsType.getText().toString());
                    } else {
                        alarmLogEntityList = alarmLogDao.getDateWise(mBinding.edtFormDate.getText().toString(), mBinding.edtToDate.getText().toString());
                    }
                    setAdapter(alarmLogEntityList);
                } else if (!mBinding.alertsType.getText().toString().isEmpty()){
                    alarmLogEntityList = alarmLogDao.getTypeWise(mBinding.alertsType.getText().toString());
                    setAdapter(alarmLogEntityList);
                }

            }
        });

        mBinding.edtFormDate.setOnClickListener(View -> {
            MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
            materialDateBuilder.setTitleText("SELECT A DATE");
            final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
            materialDatePicker.show(getChildFragmentManager(), "MATERIAL_DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object selection) {
                    String startDate = DateFormat.format("dd/MM/yyyy", new Date(materialDatePicker.getHeaderText())).toString();
                    mBinding.edtFormDate.setText(startDate);
                }
            });
        });
        mBinding.edtToDate.setOnClickListener(View -> {
            MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
            materialDateBuilder.setTitleText("SELECT A DATE");
            final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
            materialDatePicker.show(getChildFragmentManager(), "MATERIAL_DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object selection) {
                    String endDate = DateFormat.format("dd/MM/yyyy", new Date(materialDatePicker.getHeaderText())).toString();
                    mBinding.edtToDate.setText(endDate);
                    if (!mBinding.edtFormDate.getText().toString().isEmpty()) {
                        String[] splitStartDate = mBinding.edtFormDate.getText().toString().split("/");
                        String[] splitEndDate = mBinding.edtToDate.getText().toString().split("/");
                        String startDate = splitStartDate[0] + splitStartDate[1] + splitStartDate[2];
                        String FinalDate = splitEndDate[0] + splitEndDate[1] + splitEndDate[2];
                        if (Integer.parseInt(FinalDate) < Integer.parseInt(startDate)) {
                            Toast.makeText(getContext(), "Invalid Date", Toast.LENGTH_SHORT).show();
                            mBinding.edtToDate.setText("");
                        }
                    }
                }
            });
        });
    }

    public void updateToDb(List<AlarmLogEntity> entryList) {
        alarmLogDao.insert(entryList.toArray(new AlarmLogEntity[0]));
    }

    void setAdapter(List<AlarmLogEntity> alarmLogEntityList) {
        if (alarmLogEntityList.size()==0){
            mBinding.txt.setVisibility(View.VISIBLE);
        }else {
            mBinding.txt.setVisibility(View.GONE);
        }
        alarmLogRvAdapter = new AlarmLogRvAdapter(alarmLogEntityList, this);
        mBinding.rvAlarmLog.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvAlarmLog.setAdapter(alarmLogRvAdapter);
    }


    @Override
    public void OnDataReceive(String data) { }

    @Override
    public void OnItemClick(int sNo, int hardwareNo, Button button, String sensorType) {
        if (sensorType.equals("IN") && hardwareNo >= 5 && hardwareNo <= 14) {
            alarmLogDao.updateLockAlarm(formDigits(2, String.valueOf(hardwareNo)), "0", sensorType);
            button.setVisibility(View.INVISIBLE);
            new EventLogDemo(String.valueOf(hardwareNo), "IN", "Diagnostic Sweep Acknowledged by #", SharedPref.read(pref_USERLOGINID, ""), getContext());
            ApiService.getInstance(getContext()).processApiData("1", "00", ("Diagnostic Sweep Acknowledged by #" + SharedPref.read(pref_USERLOGINID, "")));
        } else if (sensorType.equals("OP")) {
            mAppClass.sendPacket(new DataReceiveCallback() {
                @Override
                public void OnDataReceive(String data) {
                    String[] splitData = data.split("\\*")[1].split("\\$");
                    if (splitData[0].equals(WRITE_PACKET)) {
                        if (splitData[1].equals(PCK_LOCKOUT)) {
                            if (splitData[2].equals(RES_SUCCESS)) {
                                alarmLogDao.updateLockAlarm(formDigits(2, String.valueOf(hardwareNo)), "0", sensorType);
                                // button.setVisibility(View.INVISIBLE);
                                new EventLogDemo(String.valueOf(hardwareNo), "OP", "LockOut Alarm Acknowledged by #", SharedPref.read(pref_USERLOGINID, ""), getContext());
                                ApiService.getInstance(getContext()).processApiData("1", "00", ("LockOut Alarm Acknowledged by #" + SharedPref.read(pref_USERLOGINID, "")));
                            } else {
                                mAppClass.showSnackBar(getContext(), "Acknowledgement Failed");
                            }
                        } else {
                            mAppClass.showSnackBar(getContext(), "Acknowledgement Failed");
                        }
                    } else {
                        mAppClass.showSnackBar(getContext(), "Acknowledgement Failed");
                    }
                }
            }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR +
                    WRITE_PACKET + SPILT_CHAR + PCK_LOCKOUT + SPILT_CHAR + formDigits(2, String.valueOf(hardwareNo)) + SPILT_CHAR + ACK);
        }
    }
}
