package com.ionexchange.Fragments.Services.Logs;

import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.ionexchange.Adapters.AlarmLogRvAdapter;
import com.ionexchange.Database.Dao.AlarmLogDao;
import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentAlarmLogBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FragmentAlarmLog extends Fragment {
    FragmentAlarmLogBinding mBinding;
    AlarmLogRvAdapter alarmLogRvAdapter;
    WaterTreatmentDb waterTreatmentDb;
    AlarmLogDao alarmLogDao;
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

        waterTreatmentDb = WaterTreatmentDb.getDatabase(getContext());
        alarmLogDao = waterTreatmentDb.alarmLogDao();
        alarmLogEntityList = alarmLogDao.getAlarmLogList();
        setAdapter(alarmLogEntityList);
        for (int i = 1; i < 30; i++) {
            int date = 1 + i;
            AlarmLogEntity alarmLogEntity = new AlarmLogEntity(alarmLogDao.getLastSno() + 1,
                    "01", "pH", "Input Setting Changed", "", String.valueOf(date) + "/12/2021");
            List<AlarmLogEntity> outputEntryList = new ArrayList<>();
            outputEntryList.add(alarmLogEntity);
            updateToDb(outputEntryList);
        }

        mBinding.edtFormDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

            }
        });
        mBinding.edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            String startDate = splitStartDate[0] + splitEndDate[1] + splitStartDate[2];
                            String FinalDate = splitEndDate[0] + splitEndDate[1] + splitEndDate[2];
                            if (Integer.parseInt(FinalDate) < Integer.parseInt(startDate)) {
                                Toast.makeText(getContext(), "Invalid Date", Toast.LENGTH_SHORT).show();
                                mBinding.edtToDate.setText("");
                            }
                        }
                    }
                });

            }
        });
        mBinding.roundView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            alarmLogEntityList = alarmLogDao.getDateWise(mBinding.edtFormDate.getText().toString(),mBinding.edtToDate.getText().toString());


            }
        });

        LocalDate currentDate = LocalDate.now();
        LocalDate currentDateMinus6Months = currentDate.minusMonths(3);
        String dt = currentDateMinus6Months.toString();
     String[] split = dt.split("-");
     String ad = split[2]+"/"+split[1]+"/"+split[0];

            if (alarmLogDao.getDeleteDate(ad).contains(ad)) {
                alarmLogDao.deleteDateWise(ad);
            }
    }

    public void updateToDb(List<AlarmLogEntity> entryList) {
        alarmLogDao.insert(entryList.toArray(new AlarmLogEntity[0]));
    }

    void setAdapter(List<AlarmLogEntity> alarmLogEntityList){
        alarmLogRvAdapter = new AlarmLogRvAdapter(alarmLogEntityList);
        mBinding.rvAlarmLog.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvAlarmLog.setAdapter(alarmLogRvAdapter);
    }
}
