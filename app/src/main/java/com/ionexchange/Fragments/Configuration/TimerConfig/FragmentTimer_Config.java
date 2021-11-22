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
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.Entity.TimerConfigurationEntity;
import com.ionexchange.Fragments.FragmentHostDashboard;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentTimerconfigurationBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//created by Silambu
public class FragmentTimer_Config extends Fragment implements RvOnClick {
    FragmentTimerconfigurationBinding mBinding;
    TimerIndexRvAdapter mAdapter;
    ApplicationClass mAppClass;
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
        mAppClass = (ApplicationClass) getActivity().getApplication();
        timerConfigurationEntityList = new ArrayList<>();
        dao = ApplicationClass.timerDAO;
        timerConfigurationEntityList = dao.geTimerConfigurationEntityList();
        mAdapter = new TimerIndexRvAdapter(this, timerConfigurationEntityList);
        mBinding.timerRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mBinding.timerRv.setAdapter(mAdapter);

    }

    @Override
    public void onClick(int sensorInputNo) { }

    @Override
    public void onClick(String sensorInputNo) {
        switch (sensorInputNo) {
            case "1":
                mAppClass.navigateToBundle(getActivity(), R.id.action_TimerSetting_to_Timer, putBundle("00", "01", "02", "03", "0"));
                break;
            case "2":
                mAppClass.navigateToBundle(getActivity(), R.id.action_TimerSetting_to_Timer, putBundle("04", "05", "06", "07", "1"));
                break;
            case "3":
                mAppClass.navigateToBundle(getActivity(), R.id.action_TimerSetting_to_Timer, putBundle("08", "09", "10", "11", "2"));
                break;
            case "4":
                mAppClass.navigateToBundle(getActivity(), R.id.action_TimerSetting_to_Timer, putBundle("12", "13", "14", "15", "3"));
                break;
            case "5":
                mAppClass.navigateToBundle(getActivity(), R.id.action_TimerSetting_to_Timer, putBundle("16", "17", "18", "19", "4"));
                break;
            case "6":
                mAppClass.navigateToBundle(getActivity(), R.id.action_TimerSetting_to_Timer, putBundle("20", "21", "22", "23", "5"));
                break;
        }
    }


    Bundle putBundle(String week1, String week2, String week3, String week4, String timerNo) {
        Bundle bundle = new Bundle();
        bundle.putString("week1", week1);
        bundle.putString("week2", week2);
        bundle.putString("week3", week3);
        bundle.putString("week4", week4);
        bundle.putString("timerNo", timerNo);


        return bundle;
    }

    @Override
    public void onClick(MainConfigurationEntity mEntity) {
    }
}
