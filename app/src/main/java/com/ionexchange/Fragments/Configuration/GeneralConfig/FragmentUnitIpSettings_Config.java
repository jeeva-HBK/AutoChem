package com.ionexchange.Fragments.Configuration.GeneralConfig;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentUnitipsettingsBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.ApplicationClass.editor;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
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

        mBinding.logout.setOnClickListener(View -> {
            logOut();
        });
    }

    private void logOut() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("LOGOUT")
                .setMessage("Are you sure, you want to Logout ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editor.putBoolean("prefLoggedIn", false);
                        editor.putBoolean("requiredUserLogin", true);
                        editor.commit();
                        editor.apply();
                        PackageManager packageManager = getContext().getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(getContext().getPackageName());
                        ComponentName componentName = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                        getContext().startActivity(mainIntent);
                        Runtime.getRuntime().exit(0);
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
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
        mActivity.showProgress();
        mAppclass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_panelIpConfig);
    }


    private boolean validateFields() {
        if (isEmpty(mBinding.ipUnitipEDT)) {
            mBinding.ipUnitipEDT.requestFocus();
            mAppclass.showSnackBar(getContext(), "Ip Address Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.subNetUnitipEDT)) {
            mBinding.subNetUnitipEDT.requestFocus();
            mAppclass.showSnackBar(getContext(), "Subnet Ip Address Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.gatewayUnitipEDT)) {
            mBinding.gatewayUnitipEDT.requestFocus();
            mAppclass.showSnackBar(getContext(), "Gateway Ip Address Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.portUnitipEDT)) {
            mBinding.portUnitipEDT.requestFocus();
            mAppclass.showSnackBar(getContext(), "Port Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.DNS1UnitipEDT)) {
            mBinding.DNS1UnitipEDT.requestFocus();
            mAppclass.showSnackBar(getContext(), "DNS 1 Ip Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.DNS2UnitipEDT)) {
            mBinding.DNS2UnitipEDT.requestFocus();
            mAppclass.showSnackBar(getContext(), "DNS 2 Ip Cannot be Empty");
            return false;
        }
        return true;
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
        mActivity.showProgress();
        return DEVICE_PASSWORD + SPILT_CHAR +
                CONN_TYPE + SPILT_CHAR +
                WRITE_PACKET + SPILT_CHAR +
                PCK_panelIpConfig + SPILT_CHAR +
                toString(mBinding.ipUnitipEDT) + SPILT_CHAR +
                toString(mBinding.subNetUnitipEDT) + SPILT_CHAR +
                toString(mBinding.gatewayUnitipEDT) + SPILT_CHAR +
                toString(mBinding.DNS1UnitipEDT) + SPILT_CHAR +
                toString(mBinding.DNS2UnitipEDT) + SPILT_CHAR +
                toString(mBinding.portUnitipEDT);
    }

    @Override
    public void OnDataReceive(String data) {
        mActivity.dismissProgress();
        if (data.equals("FailedToConnect")) {
            mAppclass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("pckError")) {
            mAppclass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("sendCatch")) {
            mAppclass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("Timeout")) {
            mAppclass.showSnackBar(getContext(), "TimeOut");
        }
        if (data != null) {
            handleData(data.split("\\*")[1].split("\\$"));
        }

    }

    private void handleData(String[] splitData) {
        // Read -> Response   --
        // Write -> Response  --

        /* Read Res */
        if (splitData[1].equals("01")) {
            if (splitData[0].equals(READ_PACKET)) {
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
