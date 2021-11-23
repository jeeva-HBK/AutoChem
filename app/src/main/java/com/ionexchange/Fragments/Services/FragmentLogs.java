package com.ionexchange.Fragments.Services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Fragments.Services.Logs.FragmentAlarmLog;
import com.ionexchange.Fragments.Services.Logs.FragmentEventLog;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentLogsBinding;


import org.jetbrains.annotations.NotNull;

public class FragmentLogs extends Fragment implements RadioGroup.OnCheckedChangeListener {
    FragmentLogsBinding mBinding;
    ApplicationClass mAppClass;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_logs, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mBinding.serviceRadioGroup.setOnCheckedChangeListener(this);
        mBinding.serviceAlarm.performClick();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getCheckedRadioButtonId()) {
            case R.id.service_alarm:
                mAppClass.castFrag(getParentFragmentManager(), R.id.log_hostFrame, new FragmentAlarmLog());
                break;

            case R.id.service_event:
                mAppClass.castFrag(getParentFragmentManager(), R.id.log_hostFrame, new FragmentEventLog());
                break;
        }
    }
}
