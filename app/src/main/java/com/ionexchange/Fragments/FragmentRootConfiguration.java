package com.ionexchange.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentConfigurationBinding;


public class FragmentRootConfiguration extends Fragment implements View.OnClickListener {
    FragmentConfigurationBinding mBinding;
    ApplicationClass mAppClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_configuration, container, false);
        return mBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();

        mBinding.headerText1.setOnClickListener(this::newOnClick);
        mBinding.headerText2.setOnClickListener(this::newOnClick);
        mBinding.headerText3.setOnClickListener(this::newOnClick);
        mBinding.h1Subtext1.setOnClickListener(this);
        mBinding.h1Subtext2.setOnClickListener(this);
        mBinding.h1Subtext3.setOnClickListener(this);

        mBinding.h2Subtext1.setOnClickListener(this);
        mBinding.h2Subtext2.setOnClickListener(this);
        mBinding.h2Subtext3.setOnClickListener(this);
        mBinding.h2Subtext4.setOnClickListener(this);
        initState();
    }

    void initState() {
        mBinding.setItem1IsVisible(!mBinding.getItem1IsVisible());
        mBinding.setSelected("1-1");
        mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentUnitIpSettings());
    }

    private void newOnClick(View view) {
        setNormalState();
        switch (view.getId()) {
            case R.id.headerText1:
                mBinding.setItem1IsVisible(!mBinding.getItem1IsVisible());
                mBinding.setSelected("1-1");
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentUnitIpSettings());
                break;

            case R.id.headerText2:
                mBinding.setItem2IsVisible(!mBinding.getItem2IsVisible());
                mBinding.setSelected("2-1");
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSettings());
                break;

            case R.id.headerText3:
                mBinding.setItem3IsVisible(!mBinding.getItem3IsVisible());
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentHomeScreenConfiguration());
                break;
        }
    }

    private void setNormalState() {
        mBinding.setItem1IsVisible(false);
        mBinding.setItem2IsVisible(false);
        mBinding.setItem3IsVisible(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Header 1
            case R.id.h1_subtext1:
                mBinding.setSelected("1-1");
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentUnitIpSettings());
                break;
            case R.id.h1_subtext2:
                mBinding.setSelected("1-2");
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentTargetIpSettings());
                break;
            case R.id.h1_subtext3:
                mBinding.setSelected("1-3");
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentCommonSettings());
                break;

            // Header 2
            case R.id.h2_subtext1:
                mBinding.setSelected("2-1");
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSettings());
                break;
            case R.id.h2_subtext2:
                mBinding.setSelected("2-2");
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentOutputSettings());
                break;
            case R.id.h2_subtext3:
                mBinding.setSelected("2-3");
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentTimmerConfiguration());
                break;
            case R.id.h2_subtext4:
                mBinding.setSelected("2-4");
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentVirtualSensorConfiguration());
                break;
        }
    }
}
