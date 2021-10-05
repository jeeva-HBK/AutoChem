package com.ionexchange.Fragments.Dashboard;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Adapters.DashboardRvAdapter;
import com.ionexchange.Database.Dao.DefaultLayoutConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.MainConfigurationDao;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentDashboardBinding;

import java.util.List;

import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_DIAGNOSTIC;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;

public class FragmentDashboard extends Fragment implements View.OnClickListener, RvOnClick, DataReceiveCallback {
    FragmentDashboardBinding mBinding;
    List<MainConfigurationEntity> mainConfigurationEntityList;
    DefaultLayoutConfigurationDao defaultLayoutConfigurationDao;
    MainConfigurationDao mainConfigurationDao;
    KeepAliveCurrentValueDao keepAliveCurrentValueDao;
    WaterTreatmentDb db;
    BaseActivity baseActivity;
    ApplicationClass mAppClass;
    int girdCount, layout, screenNo, pageNo = 1;
    int maxPage;
    String mData = "{*1$11$0$012500$05212125$2401212$3540$07007$2729$2594$3425$2945$2345*}";
    CountDownTimer mTimer = new CountDownTimer(5000, 0) {
        @Override
        public void onTick(long l) {
            Log.e("mTimer", "onTick: " + l);
        }

        @Override
        public void onFinish() {
            mAppClass.sendPacket(FragmentDashboard.this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_DIAGNOSTIC + SPILT_CHAR + "0");
        }
    }.start();

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
    public void onClick(int sensorInputNo) {
    }

    @Override
    public void onClick(String sensorInputNo) {
    }

    @Override
    public void onClick(String sensorInputNo, String type, int position) {
        if (!sensorInputNo.equals("0")) {
            Bundle bundle = new Bundle();
            bundle.putString("inputNumber", sensorInputNo);
            bundle.putString("inpuType", type);
            mAppClass.navigateToBundle(getActivity(), R.id.action_Dashboard_to_sensorDetails1, bundle);
        } else {
            mAppClass.showSnackBar(getContext(), "Sensor Not Added");
        }

    }

    @Override
    public void OnDataReceive(String data) {
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.timeout));
        }
        if (data != null) {
            handleResponse(data.split("\\*")[1].split(RES_SPILT_CHAR));
        }
    }

    private void handleResponse(String[] splitData) {
        Log.e("TAG", "handleResponse: ");
        keepAliveCurrentValueDao = db.keepAliveCurrentValueDao();
        if (splitData[0].equals(READ_PACKET)) {
            if (splitData[1].equals(PCK_DIAGNOSTIC)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[3].substring(0, 2)), splitData[3].substring(2, splitData[3].length()));
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[4].substring(0, 2)), splitData[4].substring(2, splitData[4].length()));
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[5].substring(0, 2)), splitData[5].substring(2, splitData[5].length()));
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[6].substring(0, 2)), splitData[6].substring(2, splitData[6].length()));
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[7].substring(0, 2)), splitData[7].substring(2, splitData[7].length()));
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[8].substring(0, 2)), splitData[8].substring(2, splitData[8].length()));
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[9].substring(0, 2)), splitData[9].substring(2, splitData[9].length()));
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[10].substring(0, 2)), splitData[10].substring(2, splitData[10].length()));
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[11].substring(0, 2)), splitData[11].substring(2, splitData[11].length()));
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[12].substring(0, 2)), splitData[12].substring(2, splitData[12].length()));
                }
            }
        }
    }
}
