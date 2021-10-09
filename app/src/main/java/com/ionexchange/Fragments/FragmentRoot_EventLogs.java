package com.ionexchange.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ionexchange.Adapters.DiagnosticDataAdapter;
import com.ionexchange.Database.Dao.DiagnosticDataDao;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Entity.DiagnosticDataEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentLogsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;


public class FragmentRoot_EventLogs extends Fragment implements DataReceiveCallback {
    FragmentLogsBinding mBinding;
    ApplicationClass mAppClass;
    private static final String TAG = "FragmentRoot_EventLogs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_logs, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mBinding.readNowBtn.setOnClickListener(View -> {
            sendPacket("0");
        });
        mBinding.readNowBtn.performClick();
        setAdapter();
    }

    private void sendPacket(String setID) {
        mAppClass.sendPacket(this, "1234$0$1$12$" + setID);
    }

    private void setDiagnosticsDb(String hardwareNo, String diagData, String timeStamp) {
        DiagnosticDataDao dao;
        WaterTreatmentDb dB;
        dB = WaterTreatmentDb.getDatabase(getContext());
        dao = dB.diagnosticDataDao();
        if (dao.getDiagnosticDataList() != null) {
            DiagnosticDataEntity diagnosticDataEntity =
                    new DiagnosticDataEntity(dao.getLastSno() + 1, Integer.parseInt(hardwareNo), diagData, timeStamp);
            List<DiagnosticDataEntity> entryListUpdate = new ArrayList<>();
            entryListUpdate.add(diagnosticDataEntity);
            insertDiagnosticDataDb(entryListUpdate);
        }
    }

    private void insertDiagnosticDataDb(List<DiagnosticDataEntity> entryListUpdate) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        DiagnosticDataDao dao = db.diagnosticDataDao();
        dao.insert(entryListUpdate.toArray(new DiagnosticDataEntity[0]));
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
            handleResponse(data.split("\\*")[1].split(RES_SPILT_CHAR));
        }
    }

    void setAdapter() {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        DiagnosticDataDao dao = db.diagnosticDataDao();
        InputConfigurationDao inputDao = db.inputConfigurationDao();
        mBinding.diagnosticRv.scrollToPosition(dao.getDiagnosticDataList().size() - 1);
        mBinding.diagnosticRv.setLayoutManager(new LinearLayoutManager(getContext()));
        if (dao.getDiagnosticDataList() != null) {
            mBinding.diagnosticRv.setAdapter(new DiagnosticDataAdapter(dao.getDiagnosticDataList(), inputDao));
        }
    }

    private void handleResponse(String[] splitData) {
        if (splitData[1].equals("12")) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    int i = 0;
                    String currentTime = new SimpleDateFormat("yyyy.MM.dd | HH.mm.ss", Locale.getDefault()).format(new Date());
                    while (i < 9) {
                        if (splitData[i + 4].length() > 2) {
                            setDiagnosticsDb(splitData[i + 4].substring(0, 2),
                                    splitData[i + 4].substring(2, splitData[i + 4].length()), currentTime);
                        } else {
                            setDiagnosticsDb(splitData[i + 4], "No Data Received", currentTime);
                        }
                        i++;
                    }
                    if (splitData[3].equals("0")) {
                        sendPacket("1");
                    }
                    setAdapter();
                }
            }
        } else {
            Log.e(TAG, "handleResponse: Received Wrong Pck");
        }
    }
}
