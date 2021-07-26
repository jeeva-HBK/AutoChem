package com.ionexchange.Fragments.Configuration;

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

import com.google.android.material.textfield.TextInputEditText;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentUnitipsettingsBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_panelIpConfig;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentUnitIpSettings_Config extends Fragment implements DataReceiveCallback {
    FragmentUnitipsettingsBinding mBinding;
    ApplicationClass mAppclass;
    BaseActivity mActivity;
    private static final String TAG = "FragmentUnitIpSettings";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_unitipsettings, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppclass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();

        mBinding.saveFab.setOnClickListener(this::writeData);
        mBinding.saveLayoutUnitIp.setOnClickListener(this::writeData);
    }

    private String toString(EditText editText) {
        return editText.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        readData();
    }

    private void readData() {
        mAppclass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_panelIpConfig);
    }


    private boolean validateFields() {
        if (isEmpty(mBinding.ipUnitipEDT)) {
            return false;
        } else if (isEmpty(mBinding.subNetUnitipEDT)) {
            return false;
        } else if (isEmpty(mBinding.gatewayUnitipEDT)) {
            return false;
        } else if (isEmpty(mBinding.portUnitipEDT)) {
            return false;
        } else if (isEmpty(mBinding.DNS1UnitipEDT)) {
            return false;
        } else return !isEmpty(mBinding.DNS2UnitipEDT);
    }

    private Boolean isEmpty(TextInputEditText editText) {
        if (editText == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        return false;
    }

    private void writeData(View view) {
        if (validateFields()) {
            mAppclass.sendPacket(this, formData());
        }
    }

    private String formData() {
        return DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_panelIpConfig + SPILT_CHAR
                + toString(mBinding.ipUnitipEDT) + SPILT_CHAR + toString(mBinding.subNetUnitipEDT) + SPILT_CHAR + toString(mBinding.gatewayUnitipEDT) + SPILT_CHAR
                + toString(mBinding.DNS1UnitipEDT) + SPILT_CHAR + toString(mBinding.DNS2UnitipEDT) + SPILT_CHAR + toString(mBinding.portUnitipEDT);
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleData(data.split("\\*")[1].split("#"));
        }
    }

    private void handleData(String[] splitData) {
        // Read -> Response   --
        // Write -> Response  --

        /* Read Res */
        if (splitData[1].equals("01")) {
            if (splitData[0].equals("1")) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.ipUnitipEDT.setText(splitData[3]);
                    mBinding.subNetUnitipEDT.setText(splitData[4]);
                    mBinding.gatewayUnitipEDT.setText(splitData[5]);
                    mBinding.DNS1UnitipEDT.setText(splitData[6]);
                    mBinding.DNS2UnitipEDT.setText(splitData[7]);
                    mBinding.portUnitipEDT.setText(splitData[8].replace("&", ""));
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppclass.showSnackBar(getContext(), "Read Failed");
                }
                /* Write Res */
            } else if (splitData[0].equals("0")) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mAppclass.showSnackBar(getContext(), "Write Success");
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppclass.showSnackBar(getContext(), "Write Failed");
                }
            }
        } else {
            mAppclass.showSnackBar(getContext(), "Received Wrong Packet !");
            Log.e(TAG, "handleData: Received Wrong Packet !");
        }
        mActivity.changeProgress(View.GONE);
    }
}
