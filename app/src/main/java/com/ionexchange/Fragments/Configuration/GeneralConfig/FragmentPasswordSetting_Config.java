package com.ionexchange.Fragments.Configuration.GeneralConfig;

import static com.ionexchange.Activity.BaseActivity.dismissProgress;
import static com.ionexchange.Activity.BaseActivity.showProgress;
import static com.ionexchange.Others.PacketControl.ACK;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_FACTORYRESET;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINNAME;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINPASSWORDCHANED;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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
import com.ionexchange.Database.Dao.UserManagementDao;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.Singleton.SharedPref;
import com.ionexchange.databinding.FragmentPasswordsettingsBinding;

import org.jetbrains.annotations.NotNull;

public class FragmentPasswordSetting_Config extends Fragment implements View.OnClickListener {
    FragmentPasswordsettingsBinding mBinding;
    ApplicationClass mAppclass;
    BaseActivity mActivity;
    WaterTreatmentDb db;
    UserManagementDao userManagementDao;
    private static final String TAG = "FragmentUnitIpSettings";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_passwordsettings, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppclass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        userManagementDao = db.userManagementDao();
        mBinding.saveFab.setOnClickListener(this);
        mBinding.saveLayoutUnitIp.setOnClickListener(this);
        mBinding.logout.setOnClickListener(View -> {
            BaseActivity.logOut();
        });

        mBinding.reset.setOnClickListener(View -> {
            factoryResetConfirmation();
        });
        dismissProgress();
    }

    private void factoryResetConfirmation() {
        new MaterialAlertDialogBuilder(getContext()).setTitle("Confirmation")
                .setMessage("Are you sure, you want to reset the unit ?")
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showProgress();
                        sendResetPck(dialogInterface);
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }

    public static boolean isOdd(int num) {
        return num % 2 == 0;
    }

    private void sendResetPck(DialogInterface dialog) {
        final boolean[] dataReceived = {false};
        mAppclass.sendPacket(new DataReceiveCallback() { // {*0$0$17$0*}
            @Override
            public void OnDataReceive(String data) {
                if (mAppclass.isValidPck(WRITE_PACKET, data, getContext())) {
                    if (data.split("\\*")[1].split("\\$")[2].equals("0")) {
                        dataReceived[0] = true;
                        dismissProgress();
                        if (mAppclass.factoryRest()) {
                            mAppclass.showSnackBar(getContext(), "Factory Reset Success");
                        } else {
                            mAppclass.showSnackBar(getContext(), "Factory Reset Failed, try again later");
                        }
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        mAppclass.showSnackBar(getContext(), "Factory Reset Failed, try again later");
                    }
                } else {
                    dialog.dismiss();
                    mAppclass.showSnackBar(getContext(), "Factory Reset Failed, try again later");

                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_FACTORYRESET + SPILT_CHAR + ACK);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!dataReceived[0]){
                    dismissProgress();
                    mAppclass.showSnackBar(getContext(), "Factory Reset Failed, try again later");
                }
            }
        }, 10000);
    }

    private String toString(EditText editText) {
        return editText.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private Boolean isEmpty(TextInputEditText editText) {
        if (editText == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveFab:
            case R.id.saveLayout_unitIp:
                if (validateFields()) {
                    userManagementDao.updatePassword(mBinding.confirmChangePasswordEdt.getText().toString(), SharedPref.read(pref_USERLOGINNAME, ""));
                    SharedPref.write(pref_USERLOGINPASSWORDCHANED, "passwordChanged");
                    mAppclass.showSnackBar(getContext(), "Password changed");
                }
                break;
        }
    }

    private boolean validateFields() {
        if (isEmpty(mBinding.currentPasswordEdt)) {
            mBinding.changePasswordEdt.requestFocus();
            mAppclass.showSnackBar(getContext(), "Current Password Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.changePasswordEdt)) {
            mBinding.changePasswordEdt.requestFocus();
            mAppclass.showSnackBar(getContext(), "Change Password Cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.confirmChangePasswordEdt)) {
            mBinding.confirmChangePasswordEdt.requestFocus();
            mAppclass.showSnackBar(getContext(), "Confirm Change Password Cannot be Empty");
            return false;
        } else if (!mBinding.currentPasswordEdt.getText().toString().
                equals(userManagementDao.getPassword(SharedPref.read(pref_USERLOGINNAME, "")))) {
            mBinding.confirmChangePasswordEdt.requestFocus();
            mAppclass.showSnackBar(getContext(), "Current Password is wrong");
        }
        return true;
    }
}
