package com.ionexchange.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentMainhostBinding;

public class FragmentMainHost extends Fragment implements View.OnClickListener {
    FragmentMainhostBinding mBinding;
    BaseActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mainhost, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.mainScreenBtn.setOnClickListener(this);
        mBinding.trendScreenBtn.setOnClickListener(this);
        mBinding.eventLogsScreenBtn.setOnClickListener(this);
        mBinding.configScreenBtn.setOnClickListener(this);

        mActivity = (BaseActivity) getActivity();
        setNewState(mBinding.homeBigCircle, mBinding.homeMain, mBinding.homeSub, mBinding.homeSmallCircle, mBinding.homeText, new FragmentMainscreen(), "Dashboard");
    }

    private void castFrag(Fragment fragment) {
        getParentFragmentManager().beginTransaction().replace(mBinding.dashboardHost.getId(), fragment).commit();
    }

    private void setNormalState() {
        mBinding.homeMain.setVisibility(View.VISIBLE);
        mBinding.statisticsMain.setVisibility(View.VISIBLE);
        mBinding.supportMain.setVisibility(View.VISIBLE);
        mBinding.configMain.setVisibility(View.VISIBLE);

        mBinding.homeBigCircle.setVisibility(View.INVISIBLE);
        mBinding.homeSmallCircle.setVisibility(View.INVISIBLE);
        mBinding.homeSub.setVisibility(View.INVISIBLE);
        mBinding.homeText.setVisibility(View.INVISIBLE);

        mBinding.statisticsBigCircle.setVisibility(View.INVISIBLE);
        mBinding.statisticsSmallCircle.setVisibility(View.INVISIBLE);
        mBinding.statisticsSub.setVisibility(View.INVISIBLE);
        mBinding.statisticsText.setVisibility(View.INVISIBLE);

        mBinding.supportBigCircle.setVisibility(View.INVISIBLE);
        mBinding.supportSmallCircle.setVisibility(View.INVISIBLE);
        mBinding.supportSub.setVisibility(View.INVISIBLE);
        mBinding.supportText.setVisibility(View.INVISIBLE);

        mBinding.configBigCircle.setVisibility(View.INVISIBLE);
        mBinding.configSmallCircle.setVisibility(View.INVISIBLE);
        mBinding.configSub.setVisibility(View.INVISIBLE);
        mBinding.configText.setVisibility(View.INVISIBLE);
    }

    private void setNewState(View bigCircle, View main, View sub, View smallCircle, TextView txtView, Fragment fragment, String titile) {
        setNormalState();
        bigCircle.setVisibility(View.VISIBLE);
        smallCircle.setVisibility(View.VISIBLE);
        sub.setVisibility(View.VISIBLE);
        txtView.setVisibility(View.VISIBLE);

        main.setVisibility(View.INVISIBLE);

        castFrag(fragment);
        mActivity.changeActionBarText(titile);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_screen_btn:
                setNewState(mBinding.homeBigCircle, mBinding.homeMain, mBinding.homeSub, mBinding.homeSmallCircle, mBinding.homeText, new FragmentMainscreen(), "Dashboard");
                break;

            case R.id.trend_screen_btn:
                setNewState(mBinding.statisticsBigCircle, mBinding.statisticsMain, mBinding.statisticsSub, mBinding.statisticsSmallCircle, mBinding.statisticsText, new FragmentTrend(), "Statistics");
                break;

            case R.id.event_logs_screen_btn:
                setNewState(mBinding.supportBigCircle, mBinding.supportMain, mBinding.supportSub, mBinding.supportSmallCircle, mBinding.supportText, new FragmentLogs(), "Events & Logs");
                break;

            case R.id.config_screen_btn:
                setNewState(mBinding.configBigCircle, mBinding.configMain, mBinding.configSub, mBinding.configSmallCircle, mBinding.configText, new FragmentConfiguration(), "Configuration");
                break;
        }
    }

}
