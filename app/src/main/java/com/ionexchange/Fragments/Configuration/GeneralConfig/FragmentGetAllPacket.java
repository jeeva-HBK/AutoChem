package com.ionexchange.Fragments.Configuration.GeneralConfig;

import static com.ionexchange.Activity.BaseActivity.dismissProgress;
import static com.ionexchange.Activity.BaseActivity.showProgress;
import static com.ionexchange.Others.ApplicationClass.analogSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.analogUnitArr;
import static com.ionexchange.Others.ApplicationClass.digitalsensorSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.flowmeterSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.levelsensorSequenceNumber;
import static com.ionexchange.Others.ApplicationClass.modBusTypeArr;
import static com.ionexchange.Others.ApplicationClass.modBusUnitArr;
import static com.ionexchange.Others.ApplicationClass.modeAnalog;
import static com.ionexchange.Others.ApplicationClass.modeSensor;
import static com.ionexchange.Others.ApplicationClass.timerOutputMode;
import static com.ionexchange.Others.ApplicationClass.unitArr;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.ENDPACKET;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.PCK_OUTPUT_CONFIG;
import static com.ionexchange.Others.PacketControl.PCK_TIMER_CONFIG;
import static com.ionexchange.Others.PacketControl.PCK_WEEKLY_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.STARTPACKET;
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
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.TimerConfigurationEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.Others.GetAllPacketModel;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentGetPacketBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentGetAllPacket extends Fragment implements DataReceiveCallback {

    ApplicationClass mAppClass;
    WaterTreatmentDb db;
    InputConfigurationDao dao;
    FragmentGetPacketBinding mBinding;
    String timerAccessor;
    String weekOne;
    String weekTwo;
    String weekThree;
    String weekFour;
    String timerName;
    String outputLink;
    String mode;
    List<GetAllPacketModel> getAllPacketModelList;
    GetAllPacketAdapter getAllPacketAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_get_packet, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.inputConfigurationDao();
    }

    @Override
    public void onResume() {
        getAllPacketModelList = new ArrayList<>();
        setAdapter();
        getAllPacket("1");
        super.onResume();
    }

    void setAdapter() {
        getAllPacketAdapter = new GetAllPacketAdapter(getAllPacketModelList);
        mBinding.rvGet.setAdapter(getAllPacketAdapter);
        mBinding.rvGet.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    void sendData(String packet) {
        showProgress();
        mAppClass.sendPacket(this, packet);
    }

    void getAllPacket(String start) {
        sendData(DEVICE_PASSWORD + SPILT_CHAR + "18" + SPILT_CHAR + "1" + SPILT_CHAR + start);
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
            sendData(DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG
                    + SPILT_CHAR + formDigits(2, "1"));
        } else {
            if (split[0].equals("18") && split[2].equals("1")) {
                dismissProgress();
                mAppClass.showSnackBar(getContext(), "Update Failed");
            } else {
                mBinding.rvGet.notify();
                String packet = "{*1234$0$0$" + data.substring(4, 6) + data.substring(8);
                if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("01")) {

                    spiltPhData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("02");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("01")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("02");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("02")) {

                    spiltORPData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("03");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("02")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("03");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("03")) {

                    spiltContactingConductivityData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("04");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("03")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("04");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("04")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    spiltToroidalConductivityData(split, packet);
                    inputSendData("05");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("04")) {
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("05");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("05")) {

                    spiltModBusData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("06");


                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("05")) {
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("06");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("06")) {
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    spiltModBusData(split, packet);
                    inputSendData("07");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("06")) {
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("07");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("07")) {

                    spiltModBusData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("08");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("07")) {
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("08");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("08")) {

                    spiltModBusData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("09");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("08")) {
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("09");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("09")) {

                    spiltModBusData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("10");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("09")) {
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("10");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("10")) {

                    spiltModBusData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("11");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("10")) {
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("11");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("11")) {

                    spiltModBusData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("12");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("11")) {
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("12");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("12")) {

                    spiltModBusData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("13");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("12")) {
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("13");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("13")) {

                    spiltModBusData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("14");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("13")) {
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("14");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("14")) {

                    spiltModBusData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("15");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("14")) {
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("15");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("15")) {

                    spiltTemperatureData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("16");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("15")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("16");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("16")) {

                    spiltTemperatureData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("17");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("16")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("17");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("17")) {

                    spiltTemperatureData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("18");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("17")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("18");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("18")) {

                    spiltAnalogInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("19");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("18")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("19");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("19")) {

                    spiltAnalogInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("20");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("19")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("20");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("20")) {

                    spiltAnalogInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("21");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("20")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("21");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("21")) {

                    spiltAnalogInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("22");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("21")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("22");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("22")) {

                    spiltAnalogInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("23");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("22")) {

                    inputSendData("23");
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("23")) {

                    spiltAnalogInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("24");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("23")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("24");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("24")) {

                    spiltAnalogInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("25");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("24")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("25");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("25")) {

                    spiltAnalogInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("26");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("25")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("26");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("26")) {

                    spiltFlowWaterInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("27");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("26")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("27");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("27")) {

                    spiltFlowWaterInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("28");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("27")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("28");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("28")) {

                    spiltFlowWaterInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("29");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("28")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("29");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("29")) {

                    spiltFlowWaterInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("30");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("29")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("30");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("30")) {

                    spiltFlowWaterInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("31");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("30")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("31");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("31")) {

                    spiltFlowWaterInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("32");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("31")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("32");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("32")) {

                    spiltFlowWaterInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("33");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("32")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("33");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("33")) {

                    spiltFlowWaterInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("34");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("33")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("34");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("34")) {

                    spiltDigitalInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("35");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("34")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("35");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("35")) {

                    spiltDigitalInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("35");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("35")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("35");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("36")) {

                    spiltDigitalInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("36");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("36")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("36");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("37")) {

                    spiltDigitalInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("37");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("37")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("37");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("38")) {

                    spiltDigitalInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("39");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("38")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("39");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("39")) {

                    spiltDigitalInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("40");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("39")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("40");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("40")) {

                    spiltDigitalInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("41");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("40")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("41");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("41")) {

                    spiltDigitalInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("42");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("41")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("42");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("42")) {

                    spiltTankInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("43");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("42")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("43");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("43")) {

                    spiltTankInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("44");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("43")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("44");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("44")) {

                    spiltTankInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("45");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("44")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("45");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("45")) {

                    spiltTankInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("46");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("45")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("46");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("46")) {

                    spiltTankInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("47");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("46")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("47");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("47")) {

                    spiltTankInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("48");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("47")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("48");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("48")) {

                    spiltTankInputData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "UPDATED"));
                    inputSendData("49");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("48")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "INPUT", "FAILED"));
                    inputSendData("49");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("49")) {

                    spiltVirtualData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "UPDATED"));
                    virtualSendData("50");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_INPUT_SENSOR_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("49")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "FAILED"));
                    virtualSendData("50");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && split[3].equals("50")) {

                    spiltVirtualData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "UPDATED"));
                    virtualSendData("51");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && split[3].equals("50")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "FAILED"));
                    virtualSendData("51");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && split[3].equals("51")) {

                    spiltVirtualData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "UPDATED"));
                    virtualSendData("52");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && split[3].equals("51")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "FAILED"));
                    virtualSendData("52");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && split[3].equals("52")) {

                    spiltVirtualData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "UPDATED"));
                    virtualSendData("53");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && split[3].equals("52")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "FAILED"));
                    virtualSendData("53");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && split[3].equals("53")) {

                    spiltVirtualData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "UPDATED"));
                    virtualSendData("54");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && split[3].equals("53")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "FAILED"));
                    virtualSendData("54");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && split[3].equals("54")) {

                    spiltVirtualData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "UPDATED"));
                    virtualSendData("55");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && split[3].equals("54")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "FAILED"));
                    virtualSendData("55");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && split[3].equals("55")) {

                    spiltVirtualData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "UPDATED"));
                    virtualSendData("56");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && split[3].equals("55")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "FAILED"));
                    virtualSendData("56");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && split[3].equals("56")) {

                    spiltVirtualData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "UPDATED"));
                    virtualSendData("57");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && split[3].equals("56")) {

                    virtualSendData("57");
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "FAILED"));

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_SUCCESS) && split[3].equals("57")) {

                    spiltVirtualData(split, packet);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "UPDATED"));
                    outputSendData("1");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(VIRTUAL_INPUT) && split[2].equals(RES_FAILED) && split[3].equals("57")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "VIRTUAL", "FAILED"));
                    outputSendData("1");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("01")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("2");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("01")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("2");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("02")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("3");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("02")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("3");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("03")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("4");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("03")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("4");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("04")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("5");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("04")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("5");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("05")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("6");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("05")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("6");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("06")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("7");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("06")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("7");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("07")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("8");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("07")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("8");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("08")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("9");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("08")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("9");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("09")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("10");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("09")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("10");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("10")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("11");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("10")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("11");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("11")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("12");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("11")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("12");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("12")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("13");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("12")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("13");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("13")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("14");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("13")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("14");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("14")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("15");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("14")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("15");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("15")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("16");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("15")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("16");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("16")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("17");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("16")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("17");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("17")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("18");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("17")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("18");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("18")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("19");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("18")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("19");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("19")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("20");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("19")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("20");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("20")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("21");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("20")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("21");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("21")) {

                    spiltOutputData(split, data);
                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "UPDATED"));
                    outputSendData("22");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("21")) {

                    getAllPacketModelList.add(new GetAllPacketModel(split[3], "OUTPUT", "FAILED"));
                    outputSendData("22");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("23")) {

                    timerAccessorySendData("0");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_OUTPUT_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("23")) {

                    timerAccessorySendData("0");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("0")) {

                    timerAccessor = packet;
                    timerName = split[4];
                    outputLink = split[5];
                    mode = timerOutputMode[Integer.parseInt(split[6])];
                    timerWeekSendData("0", "0");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("0")) {

                    timerWeekSendData("0", "0");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("0") && split[4].equals("0")) {

                    weekOne = packet;
                    timerWeekSendData("0", "1");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("0") && split[4].equals("0")) {

                    timerWeekSendData("0", "1");
                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("0") && split[4].equals("1")) {

                    weekTwo = packet;
                    timerWeekSendData("0", "2");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("0") && split[4].equals("1")) {

                    timerWeekSendData("0", "2");
                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("0") && split[4].equals("2")) {

                    weekThree = packet;
                    timerWeekSendData("0", "3");
                    getAllPacketModelList.add(new GetAllPacketModel("0", "TIMER", "UPDATED"));

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("0") && split[4].equals("2")) {

                    timerWeekSendData("0", "3");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("0") && split[4].equals("3")) {

                    weekFour = packet;
                    timerEntity(0, timerName, outputLink, mode, timerAccessor, weekOne, weekTwo, weekThree, weekFour);
                    timerAccessorySendData("1");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("0") && split[4].equals("3")) {
                    timerAccessorySendData("1");
                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("1")) {

                    timerAccessor = packet;
                    timerName = split[4];
                    outputLink = split[5];
                    mode = timerOutputMode[Integer.parseInt(split[6])];
                    timerWeekSendData("1", "4");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("1")) {

                    timerWeekSendData("1", "4");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("1") && split[4].equals("4")) {

                    weekOne = packet;
                    timerWeekSendData("1", "5");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("1") && split[4].equals("4")) {

                    timerWeekSendData("1", "5");
                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("1") && split[4].equals("5")) {

                    weekTwo = packet;
                    timerWeekSendData("1", "6");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("1") && split[4].equals("5")) {

                    timerWeekSendData("1", "6");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("1") && split[4].equals("6")) {


                    weekThree = packet;
                    timerWeekSendData("1", "7");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("1") && split[4].equals("6")) {


                    timerWeekSendData("1", "7");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("1") && split[4].equals("7")) {

                    weekFour = packet;
                    timerEntity(1, timerName, outputLink, mode, timerAccessor, weekOne, weekTwo, weekThree, weekFour);
                    getAllPacketModelList.add(new GetAllPacketModel("1", "TIMER", "UPDATED"));
                    timerAccessorySendData("2");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("1") && split[4].equals("7")) {

                    timerAccessorySendData("2");
                    getAllPacketModelList.add(new GetAllPacketModel("1", "TIMER", "FAILED"));

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("2")) {

                    timerAccessor = packet;
                    timerName = split[4];
                    outputLink = split[5];
                    mode = timerOutputMode[Integer.parseInt(split[6])];
                    timerWeekSendData("2", "8");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("2")) {

                    timerWeekSendData("2", "8");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("2") && split[4].equals("8")) {

                    weekOne = packet;
                    timerWeekSendData("2", "9");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("2") && split[4].equals("8")) {

                    timerWeekSendData("2", "9");
                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("2") && split[4].equals("9")) {

                    weekTwo = packet;
                    timerWeekSendData("2", "10");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("2") && split[4].equals("9")) {

                    timerWeekSendData("2", "10");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("2") && split[4].equals("10")) {

                    weekThree = packet;
                    timerWeekSendData("2", "11");


                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("2") && split[4].equals("10")) {

                    timerWeekSendData("2", "11");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("2") && split[4].equals("11")) {

                    weekFour = packet;
                    timerEntity(2, timerName, outputLink, mode, timerAccessor, weekOne, weekTwo, weekThree, weekFour);
                    getAllPacketModelList.add(new GetAllPacketModel("2", "TIMER", "UPDATED"));
                    timerAccessorySendData("3");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("2") && split[4].equals("11")) {
                    getAllPacketModelList.add(new GetAllPacketModel("2", "TIMER", "FAILED"));
                    timerAccessorySendData("3");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("3")) {

                    timerAccessor = packet;
                    timerName = split[4];
                    outputLink = split[5];
                    mode = timerOutputMode[Integer.parseInt(split[6])];
                    timerWeekSendData("3", "12");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("3")) {

                    timerWeekSendData("3", "12");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("3") && split[4].equals("12")) {

                    weekOne = packet;
                    timerWeekSendData("3", "13");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("3") && split[4].equals("12")) {

                    timerWeekSendData("3", "13");
                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("3") && split[4].equals("13")) {

                    weekTwo = packet;
                    timerWeekSendData("3", "14");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("3") && split[4].equals("13")) {

                    timerWeekSendData("3", "14");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("3") && split[4].equals("14")) {

                    weekThree = packet;

                    timerWeekSendData("3", "15");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("3") && split[4].equals("14")) {

                    timerWeekSendData("3", "15");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("3") && split[4].equals("15")) {

                    weekFour = packet;
                    timerEntity(2, timerName, outputLink, mode, timerAccessor, weekOne, weekTwo, weekThree, weekFour);
                    getAllPacketModelList.add(new GetAllPacketModel("3", "TIMER", "UPDATED"));
                    timerAccessorySendData("4");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("3") && split[4].equals("15")) {

                    timerAccessorySendData("4");
                    getAllPacketModelList.add(new GetAllPacketModel("3", "TIMER", "FAILED"));

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("4")) {

                    timerAccessor = packet;
                    timerName = split[4];
                    outputLink = split[5];
                    mode = timerOutputMode[Integer.parseInt(split[6])];
                    timerWeekSendData("4", "16");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("4")) {

                    timerWeekSendData("4", "16");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("4") && split[4].equals("16")) {

                    weekOne = packet;
                    timerWeekSendData("4", "17");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("4") && split[4].equals("16")) {

                    timerWeekSendData("4", "17");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("4") && split[4].equals("17")) {

                    weekTwo = packet;
                    timerWeekSendData("4", "18");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("3") && split[4].equals("17")) {

                    timerWeekSendData("4", "18");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("4") && split[4].equals("18")) {

                    weekThree = packet;
                    timerWeekSendData("4", "19");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("4") && split[4].equals("18")) {

                    timerWeekSendData("4", "19");
                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("4") && split[4].equals("19")) {

                    weekFour = packet;
                    timerEntity(2, timerName, outputLink, mode, timerAccessor, weekOne, weekTwo, weekThree, weekFour);
                    getAllPacketModelList.add(new GetAllPacketModel("4", "TIMER", "UPDATED"));
                    timerAccessorySendData("5");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("4") && split[4].equals("19")) {

                    timerAccessorySendData("5");
                    getAllPacket("0");
                    getAllPacketModelList.add(new GetAllPacketModel("4", "TIMER", "FAILED"));

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("5")) {

                    timerAccessor = packet;
                    timerName = split[4];
                    outputLink = split[5];
                    mode = timerOutputMode[Integer.parseInt(split[6])];
                    timerWeekSendData("5", "20");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_TIMER_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("5")) {

                    timerWeekSendData("5", "20");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("5") && split[4].equals("20")) {

                    weekOne = packet;
                    timerWeekSendData("5", "21");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("5") && split[4].equals("20")) {

                    timerWeekSendData("5", "21");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("5") && split[4].equals("21")) {

                    weekTwo = packet;
                    timerWeekSendData("5", "22");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("5") && split[4].equals("21")) {

                    timerWeekSendData("5", "22");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("5") && split[4].equals("22")) {

                    weekThree = packet;
                    timerWeekSendData("5", "23");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("5") && split[4].equals("22")) {

                    timerWeekSendData("5", "23");
                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_SUCCESS) && split[3].equals("5") && split[4].equals("23")) {

                    weekFour = packet;
                    timerEntity(2, timerName, outputLink, mode, timerAccessor, weekOne, weekTwo, weekThree, weekFour);
                    getAllPacketModelList.add(new GetAllPacketModel("5", "TIMER", "UPDATED"));
                    getAllPacket("0");

                } else if (split[0].equals(READ_PACKET) && split[1].equals(PCK_WEEKLY_CONFIG) && split[2].equals(RES_FAILED) && split[3].equals("5") && split[4].equals("23")) {

                    getAllPacket("0");
                    getAllPacketModelList.add(new GetAllPacketModel("5", "TIMER", "FAILED"));

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


    void spiltPhData(String[] split, String packet) {
        inputEntityUpdate(Integer.parseInt(split[3]), inputTypeArr[Integer.parseInt(split[4])],
                "SENSOR", 0, inputTypeArr[Integer.parseInt(split[4])], 1, split[7], split[12], split[13], "N/A",
                "N/A", Integer.parseInt(split[split.length]) == 0 ? 0 : 1, packet);


    }

    void spiltORPData(String[] split, String packet) {
        inputEntityUpdate(Integer.parseInt(split[3]), inputTypeArr[Integer.parseInt(split[4])],
                "SENSOR", 0, inputTypeArr[Integer.parseInt(split[4])], 1, split[7], split[9], split[10], "mV",
                "N/A", Integer.parseInt(split[split.length]) == 0 ? 0 : 1, packet);
    }


    void spiltContactingConductivityData(String[] split, String packet) {
        inputEntityUpdate(Integer.parseInt(split[3]), inputTypeArr[Integer.parseInt(split[4])],
                "SENSOR", 0, inputTypeArr[Integer.parseInt(split[4])], 1, split[7], split[12].equals("0") ? split[15] : split[14],
                split[12].equals("0") ? split[16] : split[15], unitArr[Integer.parseInt(split[10])], "N/A", Integer.parseInt(split[split.length]) == 0 ? 0 : 1, packet);

    }

    void spiltToroidalConductivityData(String[] split, String packet) {
        inputEntityUpdate(Integer.parseInt(split[3]), inputTypeArr[Integer.parseInt(split[4])],
                "SENSOR", 0, inputTypeArr[Integer.parseInt(split[4])], 1, split[7], split[11].equals("0") ? split[14] : split[13],
                split[11].equals("0") ? split[15] : split[14], unitArr[Integer.parseInt(split[10])], "N/A", Integer.parseInt(split[split.length]) == 0 ? 0 : 1, packet);

    }

    void spiltModBusData(String[] split, String packet) {
        String[] typeOfValueArr = new String[]{"Fluorescence", "Turbidity"};
        int typeOfValueRead = 0;
        switch (Integer.parseInt(split[6])) {
            case 0:
                typeOfValueArr = new String[]{"Fluorescence", "Turbidity"};
                typeOfValueRead = Integer.parseInt(split[7]) - 1;
                break;
            case 1:
            case 2:
                typeOfValueArr = new String[]{"Corrosion rate", "Pitting rate"};
                typeOfValueRead = Integer.parseInt(split[7]) - 3;
                break;
            case 3:
                typeOfValueArr = new String[]{"Tagged Polymer"};
                typeOfValueRead = 0;
                break;
            case 4:
                typeOfValueArr = new String[]{"Fluorescence", "Tagged Polymer"};
                typeOfValueRead = Integer.parseInt(split[7]) - 5;
                break;
            case 5:
                typeOfValueArr = new String[]{"Fluorescence"};
                typeOfValueRead = 0;
                break;
        }
        inputEntityUpdate(Integer.parseInt(split[3]), inputTypeArr[Integer.parseInt(split[4])],
                "MODBUS ", 0, modBusTypeArr[Integer.parseInt(split[6])] + " - " + typeOfValueArr[typeOfValueRead],
                1, split[9], split[15], split[16], modBusUnitArr[Integer.parseInt(split[10])], typeOfValueArr[typeOfValueRead],
                Integer.parseInt(split[split.length]) == 0 ? 0 : 1, packet);
    }


    void spiltTemperatureData(String[] split, String packet) {
        inputEntityUpdate(Integer.parseInt(split[3]), inputTypeArr[Integer.parseInt(split[4])],
                "SENSOR", 0, inputTypeArr[Integer.parseInt(split[5])],
                Integer.parseInt(split[5]), split[7], split[10],
                split[11], "°C", "N/A", Integer.parseInt(split[split.length]) == 0 ? 0 : 1, packet);

    }

    void spiltAnalogInputData(String[] split, String packet) {
        inputEntityUpdate(Integer.parseInt(split[3]), inputTypeArr[Integer.parseInt(split[4])],
                "Analog", 0, analogSequenceNumber[Integer.parseInt(split[6])],
                Integer.parseInt(split[6]), split[7], split[14],
                split[15], analogUnitArr[Integer.parseInt(split[10])], "N/A", Integer.parseInt(split[split.length]) == 0 ? 0 : 1, packet);

    }

    void spiltFlowWaterInputData(String[] split, String packet) {
        String lowAlarm = "";
        String highAlarm = "";
        switch (split[5]) {
            case "0":
                lowAlarm = split[19];
                highAlarm = split[20];
                break;
            case "1":
                lowAlarm = split[15];
                highAlarm = split[16];
                break;
            case "2":
                lowAlarm = split[16];
                highAlarm = split[17];
                break;
            case "3":
                lowAlarm = split[21];
                highAlarm = split[22];
                break;
        }
        inputEntityUpdate(Integer.parseInt(split[3]), inputTypeArr[Integer.parseInt(split[4])],
                "FLOWMETER", 0, flowmeterSequenceNumber[Integer.parseInt(split[6])],
                Integer.parseInt(split[6]), split[7], lowAlarm,
                highAlarm, "N/A", "N/A", Integer.parseInt(split[split.length]) == 0 ? 0 : 1, packet);

    }

    void spiltDigitalInputData(String[] split, String packet) {
        inputEntityUpdate(Integer.parseInt(split[3]), inputTypeArr[Integer.parseInt(split[4])],
                "DIGITAL", 1, digitalsensorSequenceNumber[Integer.parseInt(split[5])],
                Integer.parseInt(split[5]), split[7], split[8],
                split[9], "N/A", "N/A", Integer.parseInt(split[split.length - 1]) == 0 ? 0 : 1, packet);

    }

    void spiltTankInputData(String[] split, String packet) {
        inputEntityUpdate(Integer.parseInt(split[3]), inputTypeArr[Integer.parseInt(split[4])],
                "TANK", 1, levelsensorSequenceNumber[Integer.parseInt(split[5])],
                Integer.parseInt(split[5]), split[7], split[8],
                split[9], "N/A", "N/A", Integer.parseInt(split[split.length - 1]) == 0 ? 0 : 1, packet);

    }

    void spiltOutputData(String[] split, String packet) {

        String[] functionArr = {"Disable", "Inhibitor", "sensor", "Analog Output", "Manual "};
        String functionMode = split[4];
        String subValueLeft = "", subValueRight = "";

        switch (functionMode) {
            case "1":
                switch (split[8]) {
                    case "0":
                        subValueLeft = split[11] + "$" + split[10];
                        subValueRight = "Continuous";
                        break;
                    case "1":
                        subValueLeft = split[12];
                        subValueRight = "Bleed/Blow Down";
                        break;
                    case "2":
                        subValueLeft = split[13];
                        subValueRight = "Water Meter/Biocide";
                        break;
                }
                break;

            case "2":
                subValueLeft = split[10] + "$" + split[8];
                subValueRight = modeSensor[Integer.parseInt(split[9])];
                break;
            case "3":

                subValueLeft = modeAnalog[Integer.parseInt(split[8])];
                subValueRight = split[8].equals("0") || split[8].equals("2") ? "" : split[9];
                break;

            default:
                subValueLeft = "";
                subValueRight = functionArr[Integer.parseInt(split[4])];
                break;
        }


        outputEntityUpdate(Integer.parseInt(split[3]), "Output-" + Integer.parseInt(split[3]) + " (" + split[5] + " )",
                split[5], subValueLeft, subValueRight, packet);

    }

    void spiltVirtualData(String[] split, String packet) {
        String lowAlarm = "",
                highAlarm = "";

        if (split[6].equals("0")) { //sensor1 - physical input
            if (split[8].equals("03")) { //sensor1 - flow meter
                if (split[10].equals("0")) { // sensor 2 - physical input
                    lowAlarm = split[17];
                    highAlarm = split[18];
                } else {                       //sensor 2 constant
                    lowAlarm = split[16];
                    highAlarm = split[17];
                }
            } else { //sensor 1 - physical & sensor 2 constant without flowmeter
                lowAlarm = split[15];
                highAlarm = split[16];
            }
        }
        if (split[6].equals("1")) { // sensor1 - constant
            if (split[9].equals("0")) { // sensor 2 - physical input
                if (split[11].equals("03")) { // sensor 1 - constant - flow meter
                    lowAlarm = split[16];
                    highAlarm = split[17];
                } else {                   // sensor 1 - constant - without flow meter
                    lowAlarm = split[15];
                    highAlarm = split[16];
                }
            } else {                       //  sensor 2 - constant
                lowAlarm = split[15];
                highAlarm = split[16];
            }
        }
        virtualEntity(Integer.parseInt(split[3]), "Virtual", 0, split[5],
                inputTypeArr[Integer.parseInt(split[8])], lowAlarm, highAlarm, "N/A", packet);
        virtualEntity(Integer.parseInt(split[3]), "Virtual", 0, split[5], inputTypeArr[Integer.parseInt(split[8])], "", "", "N/A", packet);
    }


    void inputSendData(String hardWareNo) {
        sendData(DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR +
                READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG
                + SPILT_CHAR + formDigits(2, hardWareNo));
    }

    void virtualSendData(String hardWareNo) {
        sendData(DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR +
                READ_PACKET + SPILT_CHAR + VIRTUAL_INPUT
                + SPILT_CHAR + formDigits(2, hardWareNo));
    }

    void outputSendData(String hardWareNo) {
        sendData(DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR +
                READ_PACKET + SPILT_CHAR + PCK_OUTPUT_CONFIG + SPILT_CHAR +
                formDigits(2, hardWareNo));
    }

    void timerAccessorySendData(String timerNo) {
        sendData(DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE +
                SPILT_CHAR + READ_PACKET + SPILT_CHAR +
                PCK_TIMER_CONFIG + SPILT_CHAR + timerNo);
    }

    void timerWeekSendData(String timerNo, String week) {
        sendData(DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE +
                SPILT_CHAR + READ_PACKET + SPILT_CHAR +
                PCK_WEEKLY_CONFIG + SPILT_CHAR + timerNo + SPILT_CHAR + week);
    }

    void updateInputToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    public void updateOutputDb(List<OutputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        OutputConfigurationDao dao = db.outputConfigurationDao();
        dao.insert(entryList.toArray(new OutputConfigurationEntity[0]));
    }

    public void updateTimerDb(List<TimerConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        TimerConfigurationDao dao = db.timerConfigurationDao();
        dao.insert(entryList.toArray(new TimerConfigurationEntity[0]));
    }

    public void updateVirtualDb(List<VirtualConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        VirtualConfigurationDao dao = db.virtualConfigurationDao();
        dao.insert(entryList.toArray(new VirtualConfigurationEntity[0]));
    }

    void inputEntityUpdate(int hardWareNo, String inputType, String sensorType, int signalType,
                           String inputSeqName, int inputSeqNo, String label, String lowValue,
                           String highValue, String unit, String type, int flagValue, String packet) {
        InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                (hardWareNo, inputType, sensorType, signalType, inputSeqName,
                        inputSeqNo, label, lowValue, highValue, unit, type, flagValue, STARTPACKET + packet + ENDPACKET);
        List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
        entryListUpdate.add(entityUpdate);
        updateInputToDb(entryListUpdate);
    }

    void outputEntityUpdate(int hardWareNo, String outputType, String outPutLabel, String outputMode, String outPutStatus, String packet) {
        OutputConfigurationEntity entityUpdate = new OutputConfigurationEntity(hardWareNo, outputType, outPutLabel, outputMode, outPutStatus,
                STARTPACKET + packet + ENDPACKET);
        List<OutputConfigurationEntity> entryListUpdate = new ArrayList<>();
        entryListUpdate.add(entityUpdate);
        updateOutputDb(entryListUpdate);
    }

    void virtualEntity(int hardWareNo, String virtualTye, int seq, String virtualLabel, String inputType, String low, String high, String unit, String packet) {
        VirtualConfigurationEntity virtualConfigurationEntity = new VirtualConfigurationEntity(hardWareNo, virtualTye, seq, virtualLabel, inputType, low, high, unit, packet);
        List<VirtualConfigurationEntity> entryListUpdate = new ArrayList<>();
        entryListUpdate.add(virtualConfigurationEntity);
        updateVirtualDb(entryListUpdate);


    }

    public void timerEntity(int timerNo, String timerName, String outputLink, String mode, String mainTimer, String weekOne,
                            String weekTwo, String weekThree, String weekFour) {
        TimerConfigurationEntity entity = new TimerConfigurationEntity(timerNo, timerName, outputLink, mode,
                mainTimer, weekOne, weekTwo, weekThree, weekFour);
        List<TimerConfigurationEntity> entryListDelete = new ArrayList<>();
        entryListDelete.add(entity);
        updateTimerDb(entryListDelete);


    }
}
