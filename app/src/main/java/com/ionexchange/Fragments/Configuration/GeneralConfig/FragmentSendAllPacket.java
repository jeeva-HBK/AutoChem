package com.ionexchange.Fragments.Configuration.GeneralConfig;

import static com.ionexchange.Activity.BaseActivity.dismissProgress;
import static com.ionexchange.Activity.BaseActivity.showProgress;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.OUTPUT_CONTROL_CONFIG;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.PCK_TIMER_CONFIG;
import static com.ionexchange.Others.PacketControl.PCK_WEEKLY_CONFIG;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.VIRTUAL_INPUT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ionexchange.Adapters.GetAllPacketAdapter;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.TimerConfigurationDao;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.GetAllPacketModel;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSendAllBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentSendAllPacket extends Fragment implements DataReceiveCallback {


    ApplicationClass mAppClass;
    WaterTreatmentDb db;
    InputConfigurationDao inputConfigurationDao;
    VirtualConfigurationDao virtualConfigurationDao;
    OutputConfigurationDao outputConfigurationDao;
    TimerConfigurationDao timerConfigurationDao;
    int hardwareNo;
    int week;
    List<GetAllPacketModel> getAllPacketModelList;
    GetAllPacketAdapter getAllPacketAdapter;
    FragmentSendAllBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_all, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        db = WaterTreatmentDb.getDatabase(getContext());
        inputConfigurationDao = db.inputConfigurationDao();
        virtualConfigurationDao = db.virtualConfigurationDao();
        outputConfigurationDao = db.outputConfigurationDao();
        timerConfigurationDao = db.timerConfigurationDao();
    }


    void sendData(String packet) {
        showProgress();
        mAppClass.sendPacket(this, packet);
    }

    void setAdapter() {
        getAllPacketAdapter = new GetAllPacketAdapter(getAllPacketModelList);
        mBinding.rvGet.setAdapter(getAllPacketAdapter);
        mBinding.rvGet.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    void sendAllPacket(String start) {
        sendData(DEVICE_PASSWORD + SPILT_CHAR + "18" + SPILT_CHAR + "0" + SPILT_CHAR + start);
    }

    @Override
    public void onResume() {
        getAllPacketModelList = new ArrayList<>();
        setAdapter();
        sendAllPacket("1");
        super.onResume();
    }

    @Override
    public void OnDataReceive(String data) {
        switch (data) {
            case "FailedToConnect":
            case "pckError":
            case "sendCatch":
                mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
                break;
            case "Timeout":
                dismissProgress();
                mAppClass.showSnackBar(getContext(), "TimeOut");
                break;
            default:
                handleResponse(data.split("\\*")[1].split("\\$"), data);
                break;
        }
    }

    private void handleResponse(String[] split, String data) {
        if (split[0].equals("18") && split[2].equals("0")) {
            sendData(sendInputWritePacket(1));
        } else {
            if (split[0].equals("18") && split[2].equals("1")) {
                dismissProgress();
                mAppClass.showSnackBar(getContext(), "Update Failed");
            } else {
                if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 1) {

                    sendData(sendInputWritePacket(2));
                    getAllPacketModelList.add(new GetAllPacketModel("01", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 1) {

                    getAllPacketModelList.add(new GetAllPacketModel("01", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(2));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 2) {

                    sendData(sendInputWritePacket(3));
                    getAllPacketModelList.add(new GetAllPacketModel("02", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 2) {

                    getAllPacketModelList.add(new GetAllPacketModel("02", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(3));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 3) {

                    sendData(sendInputWritePacket(4));
                    getAllPacketModelList.add(new GetAllPacketModel("03", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 3) {

                    getAllPacketModelList.add(new GetAllPacketModel("03", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(4));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 4) {

                    sendData(sendInputWritePacket(5));
                    getAllPacketModelList.add(new GetAllPacketModel("04", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 4) {

                    getAllPacketModelList.add(new GetAllPacketModel("04", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(5));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 5) {

                    sendData(sendInputWritePacket(6));
                    getAllPacketModelList.add(new GetAllPacketModel("05", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 5) {

                    getAllPacketModelList.add(new GetAllPacketModel("05", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(6));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 6) {

                    sendData(sendInputWritePacket(7));
                    getAllPacketModelList.add(new GetAllPacketModel("06", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 6) {

                    getAllPacketModelList.add(new GetAllPacketModel("06", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(7));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 7) {

                    sendData(sendInputWritePacket(8));
                    getAllPacketModelList.add(new GetAllPacketModel("07", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 7) {

                    getAllPacketModelList.add(new GetAllPacketModel("07", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(8));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 8) {

                    sendData(sendInputWritePacket(9));
                    getAllPacketModelList.add(new GetAllPacketModel("08", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 8) {

                    getAllPacketModelList.add(new GetAllPacketModel("08", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(9));


                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 9) {

                    sendData(sendInputWritePacket(10));
                    getAllPacketModelList.add(new GetAllPacketModel("09", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 9) {

                    getAllPacketModelList.add(new GetAllPacketModel("09", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(10));
                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 10) {

                    sendData(sendInputWritePacket(11));
                    getAllPacketModelList.add(new GetAllPacketModel("10", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 10) {

                    getAllPacketModelList.add(new GetAllPacketModel("10", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(11));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 11) {

                    sendData(sendInputWritePacket(12));
                    getAllPacketModelList.add(new GetAllPacketModel("11", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 11) {

                    getAllPacketModelList.add(new GetAllPacketModel("11", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(12));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 12) {

                    sendData(sendInputWritePacket(13));
                    getAllPacketModelList.add(new GetAllPacketModel("12", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 12) {

                    getAllPacketModelList.add(new GetAllPacketModel("12", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(13));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 13) {

                    sendData(sendInputWritePacket(14));
                    getAllPacketModelList.add(new GetAllPacketModel("13", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 13) {

                    getAllPacketModelList.add(new GetAllPacketModel("13", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(14));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 14) {

                    sendData(sendInputWritePacket(15));
                    getAllPacketModelList.add(new GetAllPacketModel("14", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 14) {

                    getAllPacketModelList.add(new GetAllPacketModel("14", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(15));
                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 15) {

                    sendData(sendInputWritePacket(16));
                    getAllPacketModelList.add(new GetAllPacketModel("15", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 15) {

                    getAllPacketModelList.add(new GetAllPacketModel("15", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(16));


                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 16) {

                    sendData(sendInputWritePacket(17));
                    getAllPacketModelList.add(new GetAllPacketModel("16", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 16) {

                    getAllPacketModelList.add(new GetAllPacketModel("16", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(17));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 17) {

                    sendData(sendInputWritePacket(18));
                    getAllPacketModelList.add(new GetAllPacketModel("17", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 17) {

                    getAllPacketModelList.add(new GetAllPacketModel("17", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(18));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 18) {

                    sendData(sendInputWritePacket(19));
                    getAllPacketModelList.add(new GetAllPacketModel("18", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 18) {

                    getAllPacketModelList.add(new GetAllPacketModel("18", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(19));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 19) {

                    sendData(sendInputWritePacket(20));
                    getAllPacketModelList.add(new GetAllPacketModel("19", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 19) {

                    getAllPacketModelList.add(new GetAllPacketModel("19", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(20));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 20) {

                    sendData(sendInputWritePacket(21));
                    getAllPacketModelList.add(new GetAllPacketModel("20", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 20) {

                    getAllPacketModelList.add(new GetAllPacketModel("20", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(21));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 21) {

                    sendData(sendInputWritePacket(22));
                    getAllPacketModelList.add(new GetAllPacketModel("21", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 21) {

                    getAllPacketModelList.add(new GetAllPacketModel("21", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(22));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 22) {

                    sendData(sendInputWritePacket(23));
                    getAllPacketModelList.add(new GetAllPacketModel("22", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 22) {

                    getAllPacketModelList.add(new GetAllPacketModel("22", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(23));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 23) {

                    sendData(sendInputWritePacket(24));
                    getAllPacketModelList.add(new GetAllPacketModel("23", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 23) {

                    getAllPacketModelList.add(new GetAllPacketModel("23", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(24));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 24) {

                    sendData(sendInputWritePacket(25));
                    getAllPacketModelList.add(new GetAllPacketModel("24", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 24) {

                    getAllPacketModelList.add(new GetAllPacketModel("24", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(25));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 25) {

                    sendData(sendInputWritePacket(26));
                    getAllPacketModelList.add(new GetAllPacketModel("25", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 25) {

                    getAllPacketModelList.add(new GetAllPacketModel("25", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(26));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 26) {

                    sendData(sendInputWritePacket(27));
                    getAllPacketModelList.add(new GetAllPacketModel("26", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 26) {

                    getAllPacketModelList.add(new GetAllPacketModel("26", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(27));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 27) {

                    sendData(sendInputWritePacket(28));
                    getAllPacketModelList.add(new GetAllPacketModel("27", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 27) {

                    getAllPacketModelList.add(new GetAllPacketModel("27", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(28));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 28) {

                    sendData(sendInputWritePacket(29));
                    getAllPacketModelList.add(new GetAllPacketModel("28", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 28) {

                    getAllPacketModelList.add(new GetAllPacketModel("28", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(29));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 29) {

                    sendData(sendInputWritePacket(30));
                    getAllPacketModelList.add(new GetAllPacketModel("29", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 29) {

                    getAllPacketModelList.add(new GetAllPacketModel("29", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(30));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 30) {

                    sendData(sendInputWritePacket(31));
                    getAllPacketModelList.add(new GetAllPacketModel("30", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 30) {

                    getAllPacketModelList.add(new GetAllPacketModel("30", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(31));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 31) {

                    sendData(sendInputWritePacket(32));
                    getAllPacketModelList.add(new GetAllPacketModel("31", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 31) {

                    getAllPacketModelList.add(new GetAllPacketModel("31", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(32));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 32) {

                    sendData(sendInputWritePacket(33));
                    getAllPacketModelList.add(new GetAllPacketModel("32", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 32) {

                    getAllPacketModelList.add(new GetAllPacketModel("32", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(33));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 33) {

                    sendData(sendInputWritePacket(34));
                    getAllPacketModelList.add(new GetAllPacketModel("33", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 33) {

                    getAllPacketModelList.add(new GetAllPacketModel("33", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(34));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 34) {

                    sendData(sendInputWritePacket(35));
                    getAllPacketModelList.add(new GetAllPacketModel("34", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 34) {

                    getAllPacketModelList.add(new GetAllPacketModel("34", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(34));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 35) {

                    sendData(sendInputWritePacket(36));
                    getAllPacketModelList.add(new GetAllPacketModel("35", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 35) {

                    getAllPacketModelList.add(new GetAllPacketModel("35", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(36));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 36) {

                    sendData(sendInputWritePacket(37));
                    getAllPacketModelList.add(new GetAllPacketModel("36", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 36) {

                    getAllPacketModelList.add(new GetAllPacketModel("36", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(37));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 37) {

                    sendData(sendInputWritePacket(38));
                    getAllPacketModelList.add(new GetAllPacketModel("37", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 37) {

                    getAllPacketModelList.add(new GetAllPacketModel("37", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(38));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 38) {

                    sendData(sendInputWritePacket(39));
                    getAllPacketModelList.add(new GetAllPacketModel("38", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 38) {

                    getAllPacketModelList.add(new GetAllPacketModel("38", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(39));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 39) {

                    sendData(sendInputWritePacket(40));
                    getAllPacketModelList.add(new GetAllPacketModel("39", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 39) {

                    getAllPacketModelList.add(new GetAllPacketModel("39", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(40));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 40) {

                    sendData(sendInputWritePacket(41));
                    getAllPacketModelList.add(new GetAllPacketModel("40", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 40) {

                    getAllPacketModelList.add(new GetAllPacketModel("40", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(41));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 41) {

                    sendData(sendInputWritePacket(42));
                    getAllPacketModelList.add(new GetAllPacketModel("41", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 41) {

                    getAllPacketModelList.add(new GetAllPacketModel("41", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(42));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 42) {

                    sendData(sendInputWritePacket(43));
                    getAllPacketModelList.add(new GetAllPacketModel("42", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 42) {

                    getAllPacketModelList.add(new GetAllPacketModel("42", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(43));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 43) {

                    sendData(sendInputWritePacket(44));
                    getAllPacketModelList.add(new GetAllPacketModel("43", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 43) {

                    getAllPacketModelList.add(new GetAllPacketModel("43", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(44));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 44) {

                    sendData(sendInputWritePacket(45));
                    getAllPacketModelList.add(new GetAllPacketModel("44", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 44) {

                    getAllPacketModelList.add(new GetAllPacketModel("44", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(45));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 45) {

                    sendData(sendInputWritePacket(46));
                    getAllPacketModelList.add(new GetAllPacketModel("45", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 45) {

                    getAllPacketModelList.add(new GetAllPacketModel("45", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(46));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 46) {

                    sendData(sendInputWritePacket(47));
                    getAllPacketModelList.add(new GetAllPacketModel("46", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 46) {

                    getAllPacketModelList.add(new GetAllPacketModel("46", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(47));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 47) {

                    sendData(sendInputWritePacket(48));
                    getAllPacketModelList.add(new GetAllPacketModel("47", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 47) {

                    getAllPacketModelList.add(new GetAllPacketModel("47", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(48));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 48) {

                    sendData(sendInputWritePacket(49));
                    getAllPacketModelList.add(new GetAllPacketModel("48", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 48) {

                    getAllPacketModelList.add(new GetAllPacketModel("48", "INPUT", "FAILED"));
                    sendData(sendInputWritePacket(49));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_SUCCESS) && hardwareNo == 49) {

                    sendData(sendVirtualWritePacket(50));
                    getAllPacketModelList.add(new GetAllPacketModel("49", "INPUT", "SUCCESS"));

                } else if (split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 49) {

                    getAllPacketModelList.add(new GetAllPacketModel("49", "INPUT", "FAILED"));
                    sendData(sendVirtualWritePacket(50));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && hardwareNo == 50) {

                    sendData(sendVirtualWritePacket(51));
                    getAllPacketModelList.add(new GetAllPacketModel("50", "VIRTUAL", "SUCCESS"));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && hardwareNo == 50) {

                    getAllPacketModelList.add(new GetAllPacketModel("50", "VIRTUAL", "FAILED"));
                    sendData(sendVirtualWritePacket(51));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && hardwareNo == 51) {

                    sendData(sendVirtualWritePacket(52));
                    getAllPacketModelList.add(new GetAllPacketModel("51", "VIRTUAL", "SUCCESS"));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && hardwareNo == 51) {

                    getAllPacketModelList.add(new GetAllPacketModel("51", "VIRTUAL", "FAILED"));
                    sendData(sendVirtualWritePacket(52));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && hardwareNo == 52) {

                    sendData(sendVirtualWritePacket(53));
                    getAllPacketModelList.add(new GetAllPacketModel("52", "VIRTUAL", "SUCCESS"));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && hardwareNo == 52) {

                    getAllPacketModelList.add(new GetAllPacketModel("52", "VIRTUAL", "FAILED"));
                    sendData(sendVirtualWritePacket(53));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && hardwareNo == 53) {

                    sendData(sendVirtualWritePacket(54));
                    getAllPacketModelList.add(new GetAllPacketModel("53", "VIRTUAL", "SUCCESS"));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && hardwareNo == 53) {

                    getAllPacketModelList.add(new GetAllPacketModel("53", "VIRTUAL", "FAILED"));
                    sendData(sendVirtualWritePacket(54));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && hardwareNo == 54) {

                    sendData(sendVirtualWritePacket(55));
                    getAllPacketModelList.add(new GetAllPacketModel("54", "VIRTUAL", "SUCCESS"));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && hardwareNo == 54) {

                    getAllPacketModelList.add(new GetAllPacketModel("54", "VIRTUAL", "FAILED"));
                    sendData(sendVirtualWritePacket(55));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && hardwareNo == 55) {

                    sendData(sendVirtualWritePacket(56));
                    getAllPacketModelList.add(new GetAllPacketModel("55", "VIRTUAL", "SUCCESS"));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && hardwareNo == 55) {

                    getAllPacketModelList.add(new GetAllPacketModel("55", "VIRTUAL", "FAILED"));
                    sendData(sendVirtualWritePacket(56));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && hardwareNo == 56) {

                    sendData(sendVirtualWritePacket(57));
                    getAllPacketModelList.add(new GetAllPacketModel("56", "VIRTUAL", "SUCCESS"));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && hardwareNo == 56) {

                    getAllPacketModelList.add(new GetAllPacketModel("56", "VIRTUAL", "FAILED"));
                    sendData(sendVirtualWritePacket(57));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && hardwareNo == 57) {

                    sendData(sendOutputWritePacket(1));
                    getAllPacketModelList.add(new GetAllPacketModel("57", "VIRTUAL", "SUCCESS"));

                } else if (split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && hardwareNo == 57) {

                    getAllPacketModelList.add(new GetAllPacketModel("57", "VIRTUAL", "FAILED"));
                    sendData(sendOutputWritePacket(1));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 1) {

                    sendData(sendOutputWritePacket(2));
                    getAllPacketModelList.add(new GetAllPacketModel("01", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 1) {

                    getAllPacketModelList.add(new GetAllPacketModel("01", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(2));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 2) {

                    sendData(sendOutputWritePacket(3));
                    getAllPacketModelList.add(new GetAllPacketModel("02", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 2) {

                    getAllPacketModelList.add(new GetAllPacketModel("02", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(3));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 3) {

                    sendData(sendOutputWritePacket(4));
                    getAllPacketModelList.add(new GetAllPacketModel("03", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 3) {

                    getAllPacketModelList.add(new GetAllPacketModel("03", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(4));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 4) {

                    sendData(sendOutputWritePacket(5));
                    getAllPacketModelList.add(new GetAllPacketModel("04", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 4) {

                    getAllPacketModelList.add(new GetAllPacketModel("04", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(5));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 5) {

                    sendData(sendOutputWritePacket(6));
                    getAllPacketModelList.add(new GetAllPacketModel("05", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 5) {

                    getAllPacketModelList.add(new GetAllPacketModel("05", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(6));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 6) {

                    sendData(sendOutputWritePacket(7));
                    getAllPacketModelList.add(new GetAllPacketModel("06", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 6) {

                    getAllPacketModelList.add(new GetAllPacketModel("06", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(7));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 7) {

                    sendData(sendOutputWritePacket(8));
                    getAllPacketModelList.add(new GetAllPacketModel("07", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 7) {

                    getAllPacketModelList.add(new GetAllPacketModel("07", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(8));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 8) {

                    sendData(sendOutputWritePacket(9));
                    getAllPacketModelList.add(new GetAllPacketModel("08", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 8) {

                    getAllPacketModelList.add(new GetAllPacketModel("08", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(9));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 9) {

                    sendData(sendOutputWritePacket(10));
                    getAllPacketModelList.add(new GetAllPacketModel("09", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 9) {

                    getAllPacketModelList.add(new GetAllPacketModel("09", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(10));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 10) {

                    sendData(sendOutputWritePacket(11));
                    getAllPacketModelList.add(new GetAllPacketModel("10", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 10) {

                    getAllPacketModelList.add(new GetAllPacketModel("10", "OUTPUT", "FAILED"));
                    sendData(sendVirtualWritePacket(11));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 11) {

                    sendData(sendOutputWritePacket(12));
                    getAllPacketModelList.add(new GetAllPacketModel("11", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 11) {

                    getAllPacketModelList.add(new GetAllPacketModel("11", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(12));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 12) {

                    sendData(sendOutputWritePacket(13));
                    getAllPacketModelList.add(new GetAllPacketModel("12", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 12) {

                    getAllPacketModelList.add(new GetAllPacketModel("12", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(13));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 13) {

                    sendData(sendOutputWritePacket(14));
                    getAllPacketModelList.add(new GetAllPacketModel("13", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 13) {

                    getAllPacketModelList.add(new GetAllPacketModel("13", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(14));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 14) {

                    sendData(sendOutputWritePacket(15));
                    getAllPacketModelList.add(new GetAllPacketModel("14", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 14) {

                    getAllPacketModelList.add(new GetAllPacketModel("14", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(15));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 15) {

                    sendData(sendOutputWritePacket(16));
                    getAllPacketModelList.add(new GetAllPacketModel("15", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 15) {

                    getAllPacketModelList.add(new GetAllPacketModel("15", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(16));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 16) {

                    sendData(sendOutputWritePacket(17));
                    getAllPacketModelList.add(new GetAllPacketModel("16", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[3].equals(RES_FAILED) && hardwareNo == 16) {

                    getAllPacketModelList.add(new GetAllPacketModel("16", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(17));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 17) {

                    sendData(sendOutputWritePacket(18));
                    getAllPacketModelList.add(new GetAllPacketModel("17", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 17) {

                    getAllPacketModelList.add(new GetAllPacketModel("17", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(18));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 18) {

                    sendData(sendOutputWritePacket(19));
                    getAllPacketModelList.add(new GetAllPacketModel("18", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 18) {

                    getAllPacketModelList.add(new GetAllPacketModel("18", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(19));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 19) {

                    sendData(sendOutputWritePacket(20));
                    getAllPacketModelList.add(new GetAllPacketModel("19", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 19) {

                    getAllPacketModelList.add(new GetAllPacketModel("19", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(20));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 20) {

                    sendData(sendOutputWritePacket(21));
                    getAllPacketModelList.add(new GetAllPacketModel("20", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 20) {

                    getAllPacketModelList.add(new GetAllPacketModel("20", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(21));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 21) {

                    sendData(sendOutputWritePacket(22));
                    getAllPacketModelList.add(new GetAllPacketModel("21", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 21) {

                    getAllPacketModelList.add(new GetAllPacketModel("21", "OUTPUT", "FAILED"));
                    sendData(sendOutputWritePacket(22));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 22) {

                    sendData(sendAccessoryPacket(0));
                    getAllPacketModelList.add(new GetAllPacketModel("22", "OUTPUT", "SUCCESS"));

                } else if (split[1].equals(OUTPUT_CONTROL_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 22) {

                    sendData(sendAccessoryPacket(0));
                    getAllPacketModelList.add(new GetAllPacketModel("22", "OUTPUT", "FAILED"));

                } else if (split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 0) {

                    sendData(sendWeekOnePacket(0, 0));

                } else if (split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 0) {

                    sendData(sendWeekOnePacket(0, 0));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 0) {

                    sendData(sendWeekTwoPacket(0, 1));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 0) {

                    sendData(sendWeekTwoPacket(0, 1));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 1) {

                    sendData(sendWeekThreePacket(0, 2));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 1) {

                    sendData(sendWeekThreePacket(0, 2));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 2) {

                    sendData(sendWeekFourPacket(0, 3));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 2) {

                    sendData(sendWeekFourPacket(0, 3));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 3) {

                    sendData(sendAccessoryPacket(1));
                    getAllPacketModelList.add(new GetAllPacketModel("00", "TIMER", "UPDATED"));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 3) {

                    sendData(sendAccessoryPacket(1));
                    getAllPacketModelList.add(new GetAllPacketModel("00", "TIMER", "FAILED"));

                } else if (split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 1) {

                    sendData(sendWeekOnePacket(1, 4));

                } else if (split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 1) {

                    sendData(sendWeekOnePacket(1, 4));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 4) {

                    sendData(sendWeekTwoPacket(1, 5));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 4) {

                    sendData(sendWeekTwoPacket(1, 5));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 5) {

                    sendData(sendWeekThreePacket(1, 6));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 5) {

                    sendData(sendWeekThreePacket(1, 6));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 6) {

                    sendData(sendWeekFourPacket(1, 7));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 6) {

                    sendData(sendWeekFourPacket(1, 7));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 7) {

                    sendData(sendAccessoryPacket(2));
                    getAllPacketModelList.add(new GetAllPacketModel("01", "TIMER", "UPDATED"));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 7) {

                    sendData(sendAccessoryPacket(2));
                    getAllPacketModelList.add(new GetAllPacketModel("01", "TIMER", "FAILED"));

                } else if (split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 2) {

                    sendData(sendWeekOnePacket(2, 8));

                } else if (split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 2) {

                    sendData(sendWeekOnePacket(2, 8));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 8) {

                    sendData(sendWeekTwoPacket(2, 9));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 8) {

                    sendData(sendWeekTwoPacket(2, 9));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 9) {

                    sendData(sendWeekThreePacket(2, 10));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 9) {

                    sendData(sendWeekThreePacket(2, 10));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 10) {

                    sendData(sendWeekFourPacket(2, 11));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 10) {

                    sendData(sendWeekFourPacket(2, 11));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 11) {

                    sendData(sendAccessoryPacket(3));
                    getAllPacketModelList.add(new GetAllPacketModel("02", "TIMER", "UPDATED"));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 11) {

                    sendData(sendAccessoryPacket(3));
                    getAllPacketModelList.add(new GetAllPacketModel("02", "TIMER", "FAILED"));

                } else if (split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 3) {

                    sendData(sendWeekOnePacket(3, 12));

                } else if (split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 3) {

                    sendData(sendWeekOnePacket(3, 12));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 12) {

                    sendData(sendWeekTwoPacket(3, 13));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 12) {

                    sendData(sendWeekTwoPacket(3, 13));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 13) {

                    sendData(sendWeekThreePacket(3, 14));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 13) {

                    sendData(sendWeekThreePacket(3, 14));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 14) {

                    sendData(sendWeekFourPacket(3, 15));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 14) {

                    sendData(sendWeekFourPacket(3, 15));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 15) {

                    sendData(sendAccessoryPacket(4));
                    getAllPacketModelList.add(new GetAllPacketModel("03", "TIMER", "UPDATED"));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 15) {

                    sendData(sendAccessoryPacket(4));
                    getAllPacketModelList.add(new GetAllPacketModel("03", "TIMER", "FAILED"));

                } else if (split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 4) {

                    sendData(sendWeekOnePacket(4, 16));

                } else if (split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 4) {

                    sendData(sendWeekOnePacket(4, 16));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 16) {

                    sendData(sendWeekTwoPacket(4, 17));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 16) {

                    sendData(sendWeekTwoPacket(4, 17));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 17) {

                    sendData(sendWeekThreePacket(4, 18));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 17) {

                    sendData(sendWeekThreePacket(4, 18));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 18) {

                    sendData(sendWeekFourPacket(4, 19));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 18) {

                    sendData(sendWeekFourPacket(4, 19));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 19) {

                    sendData(sendAccessoryPacket(5));
                    getAllPacketModelList.add(new GetAllPacketModel("04", "TIMER", "UPDATED"));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 19) {

                    sendData(sendAccessoryPacket(5));
                    getAllPacketModelList.add(new GetAllPacketModel("04", "TIMER", "FAILED"));

                } else if (split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_SUCCESS) && hardwareNo == 5) {

                    sendData(sendWeekOnePacket(5, 20));

                } else if (split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_FAILED) && hardwareNo == 5) {

                    sendData(sendWeekOnePacket(5, 20));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 20) {

                    sendData(sendWeekTwoPacket(5, 21));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 20) {

                    sendData(sendWeekTwoPacket(5, 21));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 21) {

                    sendData(sendWeekThreePacket(5, 22));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 21) {

                    sendData(sendWeekThreePacket(5, 22));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 22) {

                    sendData(sendWeekFourPacket(5, 23));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 22) {

                    sendData(sendWeekFourPacket(5, 23));

                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && week == 23) {

                    sendAllPacket("0");
                    getAllPacketModelList.add(new GetAllPacketModel("05", "TIMER", "UPDATED"));
                } else if (split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && week == 23) {

                    sendAllPacket("0");
                    getAllPacketModelList.add(new GetAllPacketModel("05", "TIMER", "FAILED"));

                } else if (split[0].equals("18") && split[2].equals("0")) {
                    dismissProgress();
                    mAppClass.showSnackBar(getContext(), "Update SuccessFully");
                } else if (split[0].equals("18") && split[2].equals("1")) {
                    dismissProgress();
                    mAppClass.showSnackBar(getContext(), "Update Failed");
                }
            }
        }
    }

    String sendInputWritePacket(int hardWareNo) {
        hardwareNo = hardWareNo;
        return inputConfigurationDao.getWritePacket(hardWareNo).
                substring(2, inputConfigurationDao.getWritePacket(hardWareNo).length() - 2);
    }

    String sendVirtualWritePacket(int hardWareNo) {
        hardwareNo = hardWareNo;
        return virtualConfigurationDao.getWritePacket(hardWareNo).
                substring(2, virtualConfigurationDao.getWritePacket(hardWareNo).length() - 2);
    }

    String sendOutputWritePacket(int hardWareNo) {
        hardwareNo = hardWareNo;
        return outputConfigurationDao.getWritePacket(hardWareNo).
                substring(2, outputConfigurationDao.getWritePacket(hardWareNo).length() - 2);
    }

    String sendAccessoryPacket(int timerNo) {
        hardwareNo = timerNo;
        return timerConfigurationDao.getAccessoryPacket(timerNo).
                substring(2, timerConfigurationDao.getAccessoryPacket(timerNo).length() - 2);
    }

    String sendWeekOnePacket(int timerNo, int weekly) {
        hardwareNo = timerNo;
        week = weekly;
        return timerConfigurationDao.getWeekOnePacket(timerNo).
                substring(2, timerConfigurationDao.getWeekOnePacket(timerNo).length() - 2);
    }

    String sendWeekTwoPacket(int timerNo, int weekly) {
        hardwareNo = timerNo;
        week = weekly;
        return timerConfigurationDao.getWeekTwoPacket(timerNo).
                substring(2, timerConfigurationDao.getWeekTwoPacket(timerNo).length() - 2);
    }

    String sendWeekThreePacket(int timerNo, int weekly) {
        hardwareNo = timerNo;
        week = weekly;
        return timerConfigurationDao.getWeekThreePacket(timerNo).
                substring(2, timerConfigurationDao.getWeekThreePacket(timerNo).length() - 2);
    }

    String sendWeekFourPacket(int timerNo, int weekly) {
        hardwareNo = timerNo;
        week = weekly;
        return timerConfigurationDao.getWeekFourPacket(timerNo).
                substring(2, timerConfigurationDao.getWeekFourPacket(timerNo).length() - 2);
    }
}
