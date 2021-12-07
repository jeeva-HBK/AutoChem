package com.ionexchange.Fragments.Services.Logs;

import static com.ionexchange.Others.ApplicationClass.alarmArr;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.getPositionFromAtxt;
import static com.ionexchange.Others.ApplicationClass.getStringValue;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ionexchange.Adapters.AlarmLogRvAdapter;
import com.ionexchange.Database.Dao.AlarmLogDao;
import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentAlarmLogBinding;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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
        getDateFormDb();
        mBinding.roundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.edtFormDate.getText().toString().isEmpty() && !mBinding.edtToDate.getText().toString().isEmpty()){
                    if (!mBinding.alertsType.getText().toString().isEmpty()){
                        alarmLogEntityList = alarmLogDao.getDateWiseAndType(mBinding.edtFormDate.getText().toString(), mBinding.edtToDate.getText().toString(),  mBinding.alertsType.getText().toString());
                    }else {
                        alarmLogEntityList = alarmLogDao.getDateWise(mBinding.edtFormDate.getText().toString(), mBinding.edtToDate.getText().toString());
                    }
                    setAdapter(alarmLogEntityList);
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

     /*   LocalDate currentDate = LocalDate.now();
        LocalDate currentDateMinus6Months = currentDate.minusMonths(3);
        String dt = currentDateMinus6Months.toString();
        String[] split = dt.split("-");
        String ad = split[2]+"/"+split[1]+"/"+split[0];

            if (alarmLogDao.getDeleteDate(ad).contains(ad)) {
                alarmLogDao.deleteDateWise(ad);
            }*/
    }

    void getDateFormDb() {
        List<String> date = new ArrayList<>();
        date = alarmLogDao.getDateList();
        LinkedHashSet<String> dateset = new LinkedHashSet<>(date);
        date.clear();
        date.addAll(dateset);
        mBinding.edtFormDate.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, date));
        mBinding.edtToDate.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, date));
        mBinding.alertsType.setAdapter(getAdapter(alarmArr,getContext()));
    }



    public void updateToDb(List<AlarmLogEntity> entryList) {
        alarmLogDao.insert(entryList.toArray(new AlarmLogEntity[0]));
    }

    void setAdapter(List<AlarmLogEntity> alarmLogEntityList) {
        alarmLogRvAdapter = new AlarmLogRvAdapter(alarmLogEntityList);
        mBinding.rvAlarmLog.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvAlarmLog.setAdapter(alarmLogRvAdapter);
    }
}
