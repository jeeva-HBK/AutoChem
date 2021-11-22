package com.ionexchange.Fragments.Dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Adapters.DashboardRvAdapter;
import com.ionexchange.Database.Dao.DefaultLayoutConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.MainConfigurationDao;
import com.ionexchange.Database.Entity.KeepAliveCurrentEntity;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentDashboardBinding;

import java.util.List;

//created by Silambu
public class FragmentDashboard extends Fragment implements View.OnClickListener, RvOnClick {
    FragmentDashboardBinding mBinding;
    List<MainConfigurationEntity> mainConfigurationEntityList;
    DefaultLayoutConfigurationDao defaultLayoutConfigurationDao;
    MainConfigurationDao mainConfigurationDao;
    KeepAliveCurrentValueDao keepAliveCurrentValueDao;
    WaterTreatmentDb db;
    BaseActivity baseActivity;
    static ApplicationClass mAppClass;
    int girdCount, layout, screenNo, pageNo = 1;
    int maxPage;

    private static final String TAG = "FragmentDashboard";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_dashboard, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        baseActivity = (BaseActivity) getActivity();
        mAppClass = (ApplicationClass) getActivity().getApplicationContext();
        db = WaterTreatmentDb.getDatabase(getContext());
        defaultLayoutConfigurationDao = db.defaultLayoutConfigurationDao();
        mainConfigurationDao = db.mainConfigurationDao();

        setGridCount(pageNo);
        setDefaultPage();
        mBinding.rightArrowIsBtn.setOnClickListener(this);
        mBinding.leftArrowIsBtn.setOnClickListener(this);
        mBinding.rvDashboard.setNestedScrollingEnabled(false);

        // LiveData
        keepAliveCurrentValueDao = db.keepAliveCurrentValueDao();
        keepAliveCurrentValueDao.getLiveList().observe(getViewLifecycleOwner(), new Observer<List<KeepAliveCurrentEntity>>() {
            @Override
            public void onChanged(List<KeepAliveCurrentEntity> keepAliveCurrentEntities) {
                mBinding.rvDashboard.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setDefaultPage() {
        mBinding.pageNo.setText("PageNo - " + pageNo);
        maxPage = mainConfigurationDao.maxPageNo(screenNo, layout);
        if (maxPage > 1) {
            mBinding.rightArrowIsBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setGridCount(int pageNo) {
        if (defaultLayoutConfigurationDao.enabled(1) != null) {
            screenNo = defaultLayoutConfigurationDao.enabled(1);
            layout = defaultLayoutConfigurationDao.enableLayout(screenNo);
            switch (layout) {
                case 1:
                    girdCount = 1;
                    layout = 1;
                    break;
                case 2:
                    girdCount = 1;
                    layout = 2;
                    break;
                case 3:
                    layout = 3;
                    break;
                case 4:
                    layout = 4;
                    break;
                case 5:
                    girdCount = 2;
                    layout = 5;
                    break;
                case 6:
                    girdCount = 3;
                    layout = 6;
                    break;
            }
            if (mainConfigurationDao.getPageWiseSensor(defaultLayoutConfigurationDao.enabled(1), layout, pageNo) != null) {
                mainConfigurationEntityList = mainConfigurationDao.getPageWiseSensor(defaultLayoutConfigurationDao.enabled(1), layout, pageNo);
            }
            if (layout == 3 || layout == 4) {
                mBinding.rvDashboard.setLayoutManager(new LinearLayoutManager(getContext()));
            } else {
                mBinding.rvDashboard.setLayoutManager(new GridLayoutManager(getContext(), girdCount));
            }

            mBinding.rvDashboard.setAdapter(new DashboardRvAdapter(layout, mainConfigurationEntityList, this));
        } else {
            mBinding.rvDashboard.setLayoutManager(new GridLayoutManager(getContext(), girdCount));
            mBinding.rvDashboard.setAdapter(new DashboardRvAdapter(layout, mainConfigurationEntityList, this));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rightArrow_is_btn:
                pageNo++;
                if (maxPage >= pageNo) {
                    if (maxPage == pageNo) {
                        mBinding.rightArrowIsBtn.setVisibility(View.GONE);
                    }
                    setGridCount(pageNo);
                    mBinding.pageNo.setText("PageNo - " + pageNo);
                    mBinding.leftArrowIsBtn.setVisibility(View.VISIBLE);
                } else {
                    mBinding.rightArrowIsBtn.setVisibility(View.GONE);
                }
                break;

            case R.id.leftArrow_is_btn:
                pageNo--;
                setGridCount(pageNo);
                mBinding.pageNo.setText("PageNo - " + pageNo);
                mBinding.rightArrowIsBtn.setVisibility(View.VISIBLE);
                if (pageNo == 1) {
                    mBinding.leftArrowIsBtn.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onClick(int sensorInputNo) {}

    @Override
    public void onClick(String sensorInputNo) {}

    @Override
    public void onClick(MainConfigurationEntity mEntity) {
        Log.e(TAG, "onClick: ");
        if (mEntity.hardware_no != 0) {
            Bundle bundle = new Bundle();
            bundle.putString("inputNumber", String.valueOf(mEntity.hardware_no));
            bundle.putString("inpuType", mEntity.inputType);
            mAppClass.navigateToBundle(getActivity(), R.id.action_Dashboard_to_sensorDetails1, bundle);
        } else {
            mAppClass.showSnackBar(getContext(), "Sensor Not Added");
        }
    }

}
