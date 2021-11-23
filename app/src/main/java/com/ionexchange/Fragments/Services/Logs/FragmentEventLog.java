package com.ionexchange.Fragments.Services.Logs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ionexchange.Adapters.AlarmLogRvAdapter;
import com.ionexchange.Adapters.EventLogRvAdapter;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentEventLogBinding;

public class FragmentEventLog extends Fragment {

    FragmentEventLogBinding mBinding;
    EventLogRvAdapter eventLogRvAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_log, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventLogRvAdapter = new EventLogRvAdapter();
        mBinding.rvAlarmLog.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvAlarmLog.setAdapter(eventLogRvAdapter);
    }
}
