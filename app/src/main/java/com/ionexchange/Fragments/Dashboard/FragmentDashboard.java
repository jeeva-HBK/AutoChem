package com.ionexchange.Fragments.Dashboard;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
    static ApplicationClass mAppClass;
    int girdCount, layout, screenNo, pageNo = 1;
    int maxPage;
    Handler handler;


    private static final String TAG = "FragmentDashboard";
    CountDownTimer timer = new CountDownTimer(10000, 500) {
        @Override
        public void onTick(long l) { }

        @Override
        public void onFinish() {
            sendKeepAlive("0");
            start();
        }
    };

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
                Log.e(TAG, "onChanged: VALUE CHANGED");
                mBinding.rvDashboard.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sendKeepAlive("0");
        timer.start();
        Log.e(TAG, "Timer Started !");
    }


    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        Log.e(TAG, "Timer Canceled !");
    }

    private void sendKeepAlive(String setID) {
        mAppClass.sendPacket(this,
                DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET +
                        SPILT_CHAR + PCK_DIAGNOSTIC + SPILT_CHAR + setID);
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

    @Override
    public void OnDataReceive(String data) {
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        } else if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        } else if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        } else if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.timeout));
        } else if (data != null) {
            //String mData = "{*1$11$0$0$0107.00$021900.00$03300.00$04200.00$050.000000$060.000000$070.000000$080.000000$090.000000$100.000000*}";
            handleResponse(data.split("\\*")[1].split(RES_SPILT_CHAR));
        }
    }

    private void handleResponse(String[] splitData) {
        // {*1$ 11$ 0$ | 0125.00$ 02$ 03$ 04200.00$ 05$ 060.000000$ 070.000000$ 08$ 090.000000$ 10*}
        if (splitData[0].equals(READ_PACKET)) {
            if (splitData[1].equals(PCK_DIAGNOSTIC)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    if (splitData[4].length() > 2) {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[4].substring(0, 2)), splitData[4].substring(2, splitData[4].length()));
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[4].substring(0, 2)), "N/A");
                    }
                    if (splitData[5].length() > 2) {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[5].substring(0, 2)), splitData[5].substring(2, splitData[5].length()));
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[5].substring(0, 2)), "N/A");
                    }
                    if (splitData[6].length() > 2) {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[6].substring(0, 2)), splitData[6].substring(2, splitData[6].length()));
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[6].substring(0, 2)), "N/A");
                    }
                    if (splitData[7].length() > 2) {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[7].substring(0, 2)), splitData[7].substring(2, splitData[7].length()));
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[7].substring(0, 2)), "N/A");
                    }
                    if (splitData[8].length() > 2) {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[8].substring(0, 2)), splitData[8].substring(2, splitData[8].length()));
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[8].substring(0, 2)), "N/A");
                    }
                    if (splitData[9].length() > 2) {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[9].substring(0, 2)), splitData[9].substring(2, splitData[9].length()));
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[9].substring(0, 2)), "N/A");
                    }
                    if (splitData[10].length() > 2) {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[10].substring(0, 2)), splitData[10].substring(2, splitData[10].length()));
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[10].substring(0, 2)), "N/A");
                    }
                    if (splitData[11].length() > 2) {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[11].substring(0, 2)), splitData[11].substring(2, splitData[11].length()));
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[11].substring(0, 2)), "N/A");
                    }
                    if (splitData[12].length() > 2) {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[12].substring(0, 2)), splitData[12].substring(2, splitData[12].length()));
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[12].substring(0, 2)), "N/A");
                    }
                    if (splitData[13].length() > 2) {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[13].substring(0, 2)), splitData[13].substring(2, splitData[13].length()));
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(splitData[13].substring(0, 2)), "N/A");
                    }

                    if (splitData[3].equals("0")) {
                        sendKeepAlive("1");
                    }
                }
            }
        }
    }


}
