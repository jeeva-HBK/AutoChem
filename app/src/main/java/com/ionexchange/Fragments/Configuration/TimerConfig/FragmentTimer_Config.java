package com.ionexchange.Fragments.Configuration.TimerConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.TimerIndexRvAdapter;
import com.ionexchange.Database.Dao.TimerConfigurationDao;
import com.ionexchange.Database.Entity.TimerConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentTimerconfigurationBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentTimer_Config extends Fragment implements RvOnClick {
    FragmentTimerconfigurationBinding mBinding;
    TimerIndexRvAdapter mAdapter;
    WaterTreatmentDb dB;
    TimerConfigurationDao dao;
    List<TimerConfigurationEntity> timerConfigurationEntityList;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timerconfiguration, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timerConfigurationEntityList = new ArrayList<>();
        dB = WaterTreatmentDb.getDatabase(getContext());
        dao = dB.timerConfigurationDao();
        if (dao.geTimerConfigurationEntityList().isEmpty()) {
            for (int i = 1; i < 7; i++) {
                TimerConfigurationEntity entityUpdate = new TimerConfigurationEntity
                        (i, "N/A",
                                "N/A",
                                "N/A", 0, 0, "N/A");
                List<TimerConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
            }
        }
        timerConfigurationEntityList = dao.geTimerConfigurationEntityList();
        mAdapter = new TimerIndexRvAdapter(this, timerConfigurationEntityList);
        mBinding.timerRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mBinding.timerRv.setAdapter(mAdapter);

    }

    @Override
    public void onClick(int sensorInputNo) {


    }

    @Override
    public void onClick(String sensorInputNo) {
        mBinding.frameTimer.setVisibility(View.VISIBLE);
        mBinding.timerRv.setVisibility(View.GONE);
        switch (sensorInputNo){
            case "1":
                getParentFragmentManager().beginTransaction().replace(R.id.frame_timer, new FragmentTimerStatus_Config("00", "01", "02", "03", "0")).commit();
                break;
            case "2":
                getParentFragmentManager().beginTransaction().replace(R.id.frame_timer, new FragmentTimerStatus_Config("04", "05", "06", "07", "1")).commit();
                break;
            case "3":
                getParentFragmentManager().beginTransaction().replace(R.id.frame_timer, new FragmentTimerStatus_Config("08", "09", "10", "11", "2")).commit();
                break;
            case "4":
                getParentFragmentManager().beginTransaction().replace(R.id.frame_timer, new FragmentTimerStatus_Config("12", "13", "14", "15", "3")).commit();
                break;
            case "5":
                getParentFragmentManager().beginTransaction().replace(R.id.frame_timer, new FragmentTimerStatus_Config("16", "17", "18", "19", "4")).commit();
                break;
            case "6":
                getParentFragmentManager().beginTransaction().replace(R.id.frame_timer, new FragmentTimerStatus_Config("20", "21", "22", "23", "5")).commit();
                break;
        }
    }

    @Override
    public void onClick(String sensorInputNo, String type, int position) {

    }

    public void updateToDb(List<TimerConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        TimerConfigurationDao dao = db.timerConfigurationDao();
        dao.insert(entryList.toArray(new TimerConfigurationEntity[0]));
    }
}
