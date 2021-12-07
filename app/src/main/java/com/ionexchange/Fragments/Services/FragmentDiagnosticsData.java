package com.ionexchange.Fragments.Services;

import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_DIAGNOSTIC;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Adapters.DiagnosticDataAdapter;
import com.ionexchange.Database.Dao.DiagnosticDataDao;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Entity.DiagnosticDataEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentDiagnosticsBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentDiagnosticsData extends Fragment implements DataReceiveCallback, CompoundButton.OnCheckedChangeListener {
    FragmentDiagnosticsBinding mBinding;
    ApplicationClass mAppClass;
    private static final String TAG = "FragmentDiagnosticsData";
    DiagnosticDataDao dao;
    WaterTreatmentDb dB;
    InputConfigurationDao inputDao;
    List<DiagnosticDataEntity> diagnosticDataEntityList;
    BaseActivity baseActivity;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_diagnostics, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        baseActivity = (BaseActivity) getActivity();
        mAppClass = (ApplicationClass) getActivity().getApplication();
        diagnosticDataEntityList = new ArrayList<>();
        dB = WaterTreatmentDb.getDatabase(getContext());
        dao = dB.diagnosticDataDao();
        inputDao = dB.inputConfigurationDao();
        diagnosticDataEntityList = dao.getDiagnosticDataList();
        mBinding.readNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagnosticDataEntityList = dao.getDiagnosticDataList();
                setAdapter(diagnosticDataEntityList);
                filterDialog();
            }
        });
        setAdapter(diagnosticDataEntityList);
        mBinding.refresh.setOnClickListener(View -> {
            sendPacket("0");

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sendPacket("0");
    }

    private void sendPacket(String setID) {
        baseActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR +
                READ_PACKET + SPILT_CHAR + PCK_DIAGNOSTIC + SPILT_CHAR + setID);
    }

    private void setDiagnosticsDb(String hardwareNo, String diagData, String timeStamp) {


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
        baseActivity.dismissProgress();
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

    void setAdapter(List<DiagnosticDataEntity> diagnosticDataEntityList) {
        mBinding.diagnosticRv.scrollToPosition(dao.getDiagnosticDataList().size() - 1);
        mBinding.diagnosticRv.setLayoutManager(new LinearLayoutManager(getContext()));
        if (dao.getDiagnosticDataList() != null) {
            mBinding.diagnosticRv.setAdapter(new DiagnosticDataAdapter(diagnosticDataEntityList, inputDao));
        }
    }

    private void handleResponse(String[] splitData) {
        if (splitData[1].equals("11")) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    int i = 0;
                    int j = 9;
                    String currentTime = new SimpleDateFormat("yyyy.MM.dd | HH.mm.ss", Locale.getDefault()).format(new Date());

                    while (i < j) {
                        if (splitData[i + 4].length() > 2) {
                            setDiagnosticsDb(splitData[i + 4].substring(0, 2),
                                    splitData[i + 4].substring(2, splitData[i + 4].length()), currentTime);
                        } else {
                            setDiagnosticsDb(splitData[i + 4], "No Data Received", currentTime);
                        }
                        i++;
                        if (i == 4) {
                            j = 7;
                        }
                    }


                    switch (splitData[3]) {
                        case "0":
                            sendPacket("1");
                            break;
                        case "1":
                            sendPacket("2");
                            break;
                        case "2":
                            sendPacket("3");
                            break;
                        case "3":
                            sendPacket("4");
                            break;
                        case "4":
                            sendPacket("5");
                            break;
                    }
                    setAdapter(diagnosticDataEntityList);
                }
            }
        } else {
            Log.e(TAG, "handleResponse: Received Wrong Pck");
        }
    }

    void filterDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_screen, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        ChipGroup group = dialogView.findViewById(R.id.chipGroup);


        for (int i = 1; i < 58; i++) {
            Chip chip = new Chip(dialogView.getContext());
            chip.setText(i + "");
            chip.setCheckable(true);
            chip.setId(100 + i);
            group.addView(chip);
        }


        group.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = dialogView.findViewById(group.getCheckedChipId());
                chip.setChipBackgroundColorResource(R.color.colorSecondary);
                diagnosticDataEntityList = dao.getDiagnosticData(chip.getText().toString());
                setAdapter(diagnosticDataEntityList);
                alertDialog.dismiss();
            }

        });


        alertDialog.show();

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


    }
}
