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
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.TimerConfigurationDao;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.TimerConfigurationEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Fragments.Configuration.FragmentRoot_Config;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentMainhostBinding;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.macAddress;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.ADMIN;
import static com.ionexchange.Others.PacketControl.APP_VERSION;
import static com.ionexchange.Others.PacketControl.CONNECT_COMMAND;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_connectPacket;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentHostDashboard extends Fragment implements View.OnClickListener, DataReceiveCallback {
    FragmentMainhostBinding mBinding;
    BaseActivity mActivity;
    ApplicationClass mAppClass;

    public static WaterTreatmentDb DB;
    public static InputConfigurationDao inputDAO;
    public static OutputConfigurationDao outputDAO;
    public static VirtualConfigurationDao virtualDAO;
    public static TimerConfigurationDao timerDAO;


    private static final String TAG = "FragmentMainHost";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mainhost, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (BaseActivity) getActivity();
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mBinding.mainScreenBtn.setOnClickListener(this);
        mBinding.trendScreenBtn.setOnClickListener(this);
        mBinding.eventLogsScreenBtn.setOnClickListener(this);
        mBinding.configScreenBtn.setOnClickListener(this);
        // Connect_Packet

        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_connectPacket + SPILT_CHAR + APP_VERSION + SPILT_CHAR + CONNECT_COMMAND + SPILT_CHAR + ADMIN);
        setNewState(mBinding.homeBigCircle, mBinding.homeMain, mBinding.homeSub, mBinding.homeSmallCircle, mBinding.homeText, new FragmentRoot_MainScreen(), "Dashboard");
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

    private void setNewState(View bigCircle, View main, View sub, View smallCircle, TextView txtView, Fragment fragment, String title) {
        setNormalState();
        bigCircle.setVisibility(View.VISIBLE);
        smallCircle.setVisibility(View.VISIBLE);
        sub.setVisibility(View.VISIBLE);
        txtView.setVisibility(View.VISIBLE);

        main.setVisibility(View.INVISIBLE);

        castFrag(fragment);
        mActivity.changeActionBarText(title);
    }

    @Override
    public void onClick(View v) {
        if (userType != 0) {
            switch (v.getId()) {
                case R.id.main_screen_btn:
                    setNewState(mBinding.homeBigCircle, mBinding.homeMain, mBinding.homeSub, mBinding.homeSmallCircle, mBinding.homeText, new FragmentRoot_MainScreen(), "Dashboard");
                    break;

                case R.id.trend_screen_btn:
                    setNewState(mBinding.statisticsBigCircle, mBinding.statisticsMain, mBinding.statisticsSub, mBinding.statisticsSmallCircle, mBinding.statisticsText, new FragmentRoot_Trend(), "Statistics");
                    break;

                case R.id.event_logs_screen_btn:
                    setNewState(mBinding.supportBigCircle, mBinding.supportMain, mBinding.supportSub, mBinding.supportSmallCircle, mBinding.supportText, new FragmentRoot_EventLogs(), "Events & Logs");
                    break;

                case R.id.config_screen_btn:
                    initDB();
                    setNewState(mBinding.configBigCircle, mBinding.configMain, mBinding.configSub, mBinding.configSmallCircle, mBinding.configText, new FragmentRoot_Config(), "Configuration");
                    break;
            }
        } else {
            mAppClass.showSnackBar(getContext(), "Access Denied !");
        }
    }

    private void initDB() {
        DB = WaterTreatmentDb.getDatabase(getContext());
        /*Input_DB*/
        inputDAO = DB.inputConfigurationDao();
        if (inputDAO.getInputConfigurationEntityList().isEmpty()) {
            for (int i = 1; i < 46; i++) {
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (i, "N/A", 0, "N/A",
                                "N/A", "N/A", 0);
                List<InputConfigurationEntity> inputentryList = new ArrayList<>();
                inputentryList.add(entityUpdate);
                updateInputDB(inputentryList);
            }
        }

        /*Output_DB*/
        outputDAO = DB.outputConfigurationDao();
        if (outputDAO.getOutputConfigurationEntityList().isEmpty()) {
            for (int i = 1; i < 23; i++) {
                OutputConfigurationEntity entityUpdate = new OutputConfigurationEntity
                        (i, "output-" + i, "N/A",
                                "N/A",
                                "N/A");
                List<OutputConfigurationEntity> outputEntryList = new ArrayList<>();
                outputEntryList.add(entityUpdate);
                updateOutPutDB(outputEntryList);
            }
        }

        /*Virtual_DB*/
        virtualDAO = DB.virtualConfigurationDao();
        if (virtualDAO.getVirtualConfigurationEntityList().isEmpty()) {
            for (int i = 46; i < 54; i++) {
                VirtualConfigurationEntity entityUpdate = new VirtualConfigurationEntity
                        (i, "virtual-" + (i - 45), 0, "N/A",
                                "N/A", "N/A");
                List<VirtualConfigurationEntity> virtualEntryList = new ArrayList<>();
                virtualEntryList.add(entityUpdate);
                updateVirtualDB(virtualEntryList);
            }
        }

        /*Timer_DB*/
        timerDAO = DB.timerConfigurationDao();
        if (timerDAO.geTimerConfigurationEntityList().isEmpty()) {
            for (int i = 1; i < 7; i++) {
                TimerConfigurationEntity entityUpdate = new TimerConfigurationEntity
                        (i, "N/A",
                                "N/A",
                                "N/A", 0, 0, "N/A");
                List<TimerConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateTimerDB(entryListUpdate);
            }
        }
    }

    private void updateTimerDB(List<TimerConfigurationEntity> entryList) {
        TimerConfigurationDao dao = DB.timerConfigurationDao();
        dao.insert(entryList.toArray(new TimerConfigurationEntity[0]));
    }

    private void updateVirtualDB(List<VirtualConfigurationEntity> entryList) {
        VirtualConfigurationDao dao = DB.virtualConfigurationDao();
        dao.insert(entryList.toArray(new VirtualConfigurationEntity[0]));
    }

    private void updateInputDB(List<InputConfigurationEntity> entryList) {
        InputConfigurationDao dao = DB.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void updateOutPutDB(List<OutputConfigurationEntity> entryList) {
        OutputConfigurationDao dao = DB.outputConfigurationDao();
        dao.insert(entryList.toArray(new OutputConfigurationEntity[0]));
    }


    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleResponse(data.split("\\*")[1].split("\\$"));
        }
    }

    private void handleResponse(String[] spiltData) {
        try {
            if (spiltData[1].equals(PCK_connectPacket)) {
                if (spiltData[0].equals(READ_PACKET)) {
                    if (spiltData[2].equals(RES_SUCCESS)) {
                        macAddress = spiltData[5];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
