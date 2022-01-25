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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import com.ionexchange.databinding.FragmentDusettingsBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FragmentDuSetting_Config extends Fragment implements View.OnClickListener {
    FragmentDusettingsBinding mBinding;
    ApplicationClass mAppclass;
    BaseActivity mActivity;
    WaterTreatmentDb db;
    UserManagementDao userManagementDao;
    private static final String TAG = "FragmentUnitIpSettings";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dusettings, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppclass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        db = WaterTreatmentDb.getDatabase(getContext());
        userManagementDao = db.userManagementDao();

        dismissProgress();
        mBinding.passwordSetting.setOnClickListener(this);
        mBinding.factorySetting.setOnClickListener(this);
        mBinding.getAllConfig.setOnClickListener(this);
        mBinding.sendAllConfig.setOnClickListener(this);
        mBinding.logOut.setOnClickListener(this);
        mBinding.pendrive.setOnClickListener(this);
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
            case R.id.password_setting:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_password_settings, null);
                dialogBuilder.setView(dialogView);
                AlertDialog alertDialog = dialogBuilder.create();

                TextInputEditText currentPassword = dialogView.findViewById(R.id.current_password_edt);
                TextInputEditText newPassword = dialogView.findViewById(R.id.newPassword_edt);
                TextInputEditText confirmPasword = dialogView.findViewById(R.id.confirm_new_password_edt);
                LinearLayout save = dialogView.findViewById(R.id.saveLayout_unitIp);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (validateFields(currentPassword, newPassword, confirmPasword)) {
                            userManagementDao.updatePassword(confirmPasword.getText().toString(), SharedPref.read(pref_USERLOGINNAME, ""));
                            SharedPref.write(pref_USERLOGINPASSWORDCHANED, "passwordChanged");
                            mAppclass.showSnackBar(getContext(), "Password changed");
                        }
                    }
                });
                alertDialog.show();
                break;


            case R.id.factory_setting:
                factoryResetConfirmation();
                break;

            case R.id.logOut:
                BaseActivity.logOut();
                break;


            case R.id.pendrive:
                String srcDir = Environment.getExternalStorageDirectory().toString()+"/MyFolder";
                String dst = Environment.getExternalStorageDirectory().getPath() + "/Pictures";
                copyFileOrDirectory(srcDir, dst);
                // mAppclass.exportDB();
                break;


            case R.id.send_all_config:
                mAppclass.navigateTo(getActivity(), R.id.action_passwordSettings_to_fragmentSendAllPacket);
                break;

            case R.id.get_all_config:
                mAppclass.navigateTo(getActivity(), R.id.action_passwordSettings_to_fragmentGetAllPacket);
                break;
        }
    }

    private void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);
                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private boolean validateFields(TextInputEditText currentPassword, TextInputEditText newPassword, TextInputEditText confirmPasword) {
        if (isEmpty(currentPassword)) {
            currentPassword.requestFocus();
            mAppclass.showSnackBar(getContext(), "Current Password Cannot be Empty");
            return false;
        } else if (isEmpty(newPassword)) {
            newPassword.requestFocus();
            mAppclass.showSnackBar(getContext(), "Change Password Cannot be Empty");
            return false;
        } else if (isEmpty(confirmPasword)) {
            confirmPasword.requestFocus();
            mAppclass.showSnackBar(getContext(), "Confirm Change Password Cannot be Empty");
            return false;
        } else if (!currentPassword.
                equals(userManagementDao.getPassword(SharedPref.read(pref_USERLOGINNAME, "")))) {
            currentPassword.requestFocus();
            mAppclass.showSnackBar(getContext(), "Current Password is wrong");
        }
        return true;
    }
}
