package com.ionexchange.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentCommonsettingsBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_GENERAL;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentCommonSettings extends Fragment implements DataReceiveCallback {
    FragmentCommonsettingsBinding mBinding;
    ApplicationClass mAppClass;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_commonsettings, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();

        mBinding.saveLayoutCommonSettings.setOnClickListener(this::onCLick);
        mBinding.saveFabCommonSettings.setOnClickListener(this::onCLick);

    }

    private void onCLick(View view) {
        if (validateFields()) {
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_GENERAL + SPILT_CHAR + toString(mBinding.siteIdCommonSettingsEDT) + SPILT_CHAR +
                    toString(mBinding.siteNameCommonSettingsEDT) + SPILT_CHAR + toString(mBinding.sitePasswordCommonSettingsEDT) + SPILT_CHAR + 0 + SPILT_CHAR + toString(mBinding.siteLocationCommonSettingsEDT) + SPILT_CHAR +
                    toString(mBinding.alarmDelayCommonSettingsEDT) + SPILT_CHAR + 0 + SPILT_CHAR + toString(mBinding.Hours) + toString(mBinding.MM) + toString(mBinding.SS) + toString(mBinding.NN) + toString(mBinding.DD) +
                    toString(mBinding.month) + toString(mBinding.YYYY));
        }
    }

    public String toString(EditText editText) {
        return editText.getText().toString();
    }

    private boolean validateFields() {
        if (isEmpty(mBinding.siteIdCommonSettingsEDT)) {
            return false;
        } else if (isEmpty(mBinding.siteNameCommonSettingsEDT)) {
            return false;
        } else if (isEmpty(mBinding.siteLocationCommonSettingsEDT)) {
            return false;
        } else if (isEmpty(mBinding.sitePasswordCommonSettingsEDT)) {
            return false;
        } else if (isEmpty(mBinding.alarmDelayCommonSettingsEDT)) {
            return false;
        } else if (isEmpty(mBinding.Hours)) {
            return false;
        } else if (isEmpty(mBinding.MM)) {
            return false;
        } else if (isEmpty(mBinding.SS)) {
            return false;
        } else if (isEmpty(mBinding.NN)) {
            return false;
        } else if (isEmpty(mBinding.DD)) {
            return false;
        } else if (isEmpty(mBinding.month)) {
            return false;
        } else return !isEmpty(mBinding.YYYY);
    }

    private Boolean isEmpty(EditText editText) {
        if (editText == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        readData();
    }

    private void readData() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_GENERAL);
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleResponce(data.split("\\*")[1].split("#"));
        }
    }

    private void handleResponce(String[] splitData) {
        // Read - Res -
        // Write - Res -
        if (splitData[1].equals(PCK_GENERAL)) {
            // READ_Response
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {


                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), String.valueOf(R.string.readFailed));
                }
            } else if (splitData[0].equals(WRITE_PACKET)) {

            }
        } else {
            mAppClass.showSnackBar(getContext(), String.valueOf(R.string.wrongPack));
        }
    }
}
