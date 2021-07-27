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
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentTimerconfigurationBinding;


import org.jetbrains.annotations.NotNull;

public class FragmentTimer_Config extends Fragment implements RvOnClick {
    FragmentTimerconfigurationBinding mBinding;
    TimerIndexRvAdapter mAdapter;

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
        mAdapter = new TimerIndexRvAdapter(this);
        mBinding.timerRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mBinding.timerRv.setAdapter(mAdapter);

    }

    @Override
    public void onClick(String sensorInputNo) {
        mBinding.frameTimer.setVisibility(View.VISIBLE);
        mBinding.timerRv.setVisibility(View.GONE);
        getParentFragmentManager().beginTransaction().replace(R.id.frame_timer, new FragmentTimerStatus_Config()).commit();
    }
}
