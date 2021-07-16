package com.ionexchange.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.R;
import com.ionexchange.databinding.FragmentConfigurationBinding;


public class FragmentConfiguration extends Fragment implements View.OnClickListener {
    FragmentConfigurationBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_configuration, container, false);
        return mBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.setItem1IsVisible(!mBinding.getItem1IsVisible());
        mBinding.setSelected("1-1");

        mBinding.headerText1.setOnClickListener(this::newOnClick);
        mBinding.headerText2.setOnClickListener(this::newOnClick);

        mBinding.h1Subtext1.setOnClickListener(this);
        mBinding.h1Subtext2.setOnClickListener(this);
        mBinding.h1Subtext3.setOnClickListener(this);

        mBinding.h2Subtext1.setOnClickListener(this);
        mBinding.h2Subtext2.setOnClickListener(this);
        mBinding.h2Subtext3.setOnClickListener(this);
        mBinding.h2Subtext4.setOnClickListener(this);
        mBinding.headerText3.setOnClickListener(this);


    }

    private void newOnClick(View view) {
        setNormalState();
        switch (view.getId()) {
            case R.id.headerText1:
                mBinding.setItem1IsVisible(!mBinding.getItem1IsVisible());
                break;

            case R.id.headerText2:
                mBinding.setItem2IsVisible(!mBinding.getItem2IsVisible());
                break;
        }
    }

    private void setNormalState() {
        mBinding.setItem1IsVisible(false);
        mBinding.setItem2IsVisible(false);
        mBinding.setSelected("0");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.h1_subtext1:
                mBinding.setSelected("1-1");
                break;
            case R.id.h1_subtext2:
                mBinding.setSelected("1-2");
                break;
            case R.id.h1_subtext3:
                mBinding.setSelected("1-3");
                break;

            case R.id.h2_subtext1:
                mBinding.setSelected("2-1");
                break;
            case R.id.h2_subtext2:
                mBinding.setSelected("2-2");
                break;
            case R.id.h2_subtext3:
                mBinding.setSelected("2-3");
                break;
            case R.id.h2_subtext4:
                mBinding.setSelected("2-4");
                break;

            case R.id.headerText3:
                setNormalState();
                mBinding.setSelected("3-1");
                break;

        }
    }
}
