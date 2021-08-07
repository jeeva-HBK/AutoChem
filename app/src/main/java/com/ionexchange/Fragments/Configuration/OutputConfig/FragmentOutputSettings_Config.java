package com.ionexchange.Fragments.Configuration.OutputConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.OutputIndexRvAdapter;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentOutputsettingsBinding;

import org.jetbrains.annotations.NotNull;

public class FragmentOutputSettings_Config extends Fragment implements RvOnClick {

    FragmentOutputsettingsBinding mBinding;
    RvOnClick rvOnClick;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_outputsettings, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.outputRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mBinding.outputRv.setAdapter(new OutputIndexRvAdapter(rvOnClick = this));
    }

    @Override
    public void onClick(String sensorInputNo) {
        mBinding.outputRv.setVisibility(View.GONE);
        mBinding.outputHost.setVisibility(View.VISIBLE);
        mBinding.view9.setVisibility(View.GONE);
        getParentFragmentManager().beginTransaction().replace(mBinding.outputHost.getId(), new FragmentOutput_Config()).commit();
    }
}
