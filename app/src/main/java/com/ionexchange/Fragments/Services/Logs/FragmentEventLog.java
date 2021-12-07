package com.ionexchange.Fragments.Services.Logs;

import static com.ionexchange.Others.ApplicationClass.alarmArr;
import static com.ionexchange.Others.ApplicationClass.eventLogArr;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.getPositionFromAtxt;
import static com.ionexchange.Others.ApplicationClass.getStringValue;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.ionexchange.Adapters.AlarmLogRvAdapter;
import com.ionexchange.Adapters.EventLogRvAdapter;
import com.ionexchange.Database.Dao.EventLogDao;
import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.Entity.EventLogEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentEventLogBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

public class FragmentEventLog extends Fragment {

    FragmentEventLogBinding mBinding;
    EventLogRvAdapter eventLogRvAdapter;
    List<EventLogEntity> eventLogEntityList;
    WaterTreatmentDb db;
    EventLogDao eventLogDao;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_log, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db =  WaterTreatmentDb.getDatabase(getContext());
        eventLogDao = db.eventLogDao();
        eventLogEntityList = eventLogDao.getEventLogList();
        setAdapter(eventLogEntityList);
        getDateFormDb();
        mBinding.roundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.edtFormDate.getText().toString().isEmpty() && !mBinding.edtToDate.getText().toString().isEmpty()){
                    if (!mBinding.alertsType.getText().toString().isEmpty()){
                       eventLogEntityList = eventLogDao.getDateWiseAndType(mBinding.edtFormDate.getText().toString(), mBinding.edtToDate.getText().toString(),
                               mBinding.alertsType.getText().toString());
                    }else {
                        eventLogEntityList = eventLogDao.getDateWise(mBinding.edtFormDate.getText().toString(), mBinding.edtToDate.getText().toString());
                    }
                    setAdapter(eventLogEntityList);
                }

            }
        });
        mBinding.edtToDate.setOnClickListener(View -> {
            mBinding.edtToDate.showDropDown();
        });
        mBinding.edtFormDate.setOnClickListener(View ->{
            mBinding.edtFormDate.showDropDown();
        });
        mBinding.alertsType.setOnClickListener(View ->{
            mBinding.alertsType.showDropDown();
        });


    }

    public void deleteTopRow() {
        if (eventLogEntityList.size() >= 1000) {
            eventLogDao.deleteFirstRow();
        }
    }

    void getDateFormDb() {
        List<String> date = new ArrayList<>();
        date = eventLogDao.getDateList();
        LinkedHashSet<String> dateset = new LinkedHashSet<>(date);
        date.clear();
        date.addAll(dateset);
        mBinding.edtFormDate.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, date));
        mBinding.edtToDate.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, date));
        mBinding.alertsType.setAdapter(getAdapter(eventLogArr,getContext()));
    }

    void setAdapter(List<EventLogEntity> eventLogEntityList){
        eventLogRvAdapter = new EventLogRvAdapter(eventLogEntityList);
        mBinding.rvAlarmLog.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvAlarmLog.setAdapter(eventLogRvAdapter);
    }
}
