package com.ionexchange.Fragments.Configuration.GeneralConfig;

import static com.ionexchange.Others.ApplicationClass.mPortNumber;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_target_ip;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentTargetipsettingsBinding;

import org.jetbrains.annotations.NotNull;

public class FragmentTargetIpSettings_Config extends Fragment implements DataReceiveCallback {
    FragmentTargetipsettingsBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    private static final String TAG = "FragmentTargetIpSetting";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_targetipsettings, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        mBinding.saveFab.setOnClickListener(this::writeData);
        mBinding.saveLayoutUnitIp.setOnClickListener(this::writeData);
    }

    private void writeData(View view) {
        if (validateFields()) {
            mAppClass.sendPacket(this, formData());
        }
    }

    private boolean validateFields() {
        if (isEmpty(mBinding.server1ipTargetipEDT)) {
            mAppClass.showSnackBar(getContext(), "Server 1 Ip Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.server1portTargetipEDT)) {
            mAppClass.showSnackBar(getContext(), "Server 1 Port Ip Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tabipTargetipEDT)) {
            mAppClass.showSnackBar(getContext(), "Tablet Ip Port Ip Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.tabportTargetipEDT)) {
            mAppClass.showSnackBar(getContext(), "Tablet Port Port Ip Cannot be Empty");
            return false;
        }
        return true;
    }

    private boolean isEmpty(EditText editText) {
        if (editText == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        return false;
    }

    private String formData() {
        mActivity.showProgress();
        return DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_target_ip + SPILT_CHAR +
                toString(mBinding.server1ipTargetipEDT) + SPILT_CHAR +
                toString(mBinding.server1portTargetipEDT) + SPILT_CHAR +
                toString(mBinding.tabipTargetipEDT) + SPILT_CHAR +
                toString(mBinding.tabportTargetipEDT);
    }

    public String toString(EditText edt) {
        return edt.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        readData();
    }

    private void readData() {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_target_ip);
    }

    @Override
    public void OnDataReceive(String data) {
        mActivity.dismissProgress();
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), "TimeOut");
        }
        if (data != null) {
            handleData(data.split("\\*")[1].split("\\$"));
        }
    }

    private void handleData(String[] splitData) {
        // Read -> Response   --
        // Write -> Response  --
        if (splitData[1].equals("02")) {
            if (splitData[0].equals("1")) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.server1ipTargetipEDT.setText(splitData[3]);
                    mBinding.server1portTargetipEDT.setText(splitData[4]);
                    mBinding.tabipTargetipEDT.setText(splitData[5]);
                    mBinding.tabportTargetipEDT.setText(splitData[6]);
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Read Failed");
                }
            } else if (splitData[0].equals("0")) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), "Write Success");
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Write Failed");
                }
            }
        } else {
            Log.e(TAG, "handleData: Received Wrong Packet !");
        }
        mBinding.tabipTargetipEDT.setText(mAppClass.getTabletIp() + "");
        mBinding.tabportTargetipEDT.setText(mPortNumber + "");
    }
}