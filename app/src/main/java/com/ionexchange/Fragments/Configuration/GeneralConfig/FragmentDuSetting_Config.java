package com.ionexchange.Fragments.Configuration.GeneralConfig;

import static com.ionexchange.Activity.BaseActivity.dismissProgress;
import static com.ionexchange.Activity.BaseActivity.showProgress;
import static com.ionexchange.Activity.BaseActivity.showSnack;
import static com.ionexchange.Database.WaterTreatmentDb.DB_NAME;
import static com.ionexchange.Others.PacketControl.ACK;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_FACTORYRESET;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;
import static com.ionexchange.Singleton.SharedPref.pref_MACADDRESS;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINID;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINNAME;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINPASSWORDCHANED;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.ionexchange.Others.EventLogDemo;
import com.ionexchange.R;
import com.ionexchange.Singleton.ApiService;
import com.ionexchange.Singleton.SharedPref;
import com.ionexchange.databinding.FragmentDusettingsBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import me.jahnen.libaums.core.UsbMassStorageDevice;
import me.jahnen.libaums.core.fs.FileSystem;
import me.jahnen.libaums.core.fs.UsbFile;
import me.jahnen.libaums.core.fs.UsbFileOutputStream;

public class FragmentDuSetting_Config extends Fragment implements View.OnClickListener {
    FragmentDusettingsBinding mBinding;
    ApplicationClass mAppclass;
    BaseActivity mActivity;
    WaterTreatmentDb db;
    UserManagementDao userManagementDao;
    private static final String TAG = "FragmentUnitIpSettings";
    boolean otgDetected = false;
    private static final String ACTION_USB_PERMISSION = "com.ionexchange.USB_PERMISSION";

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            otgDetected = true;
                            UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getContext());
                            if (devices.length > 0) {
                                UsbMassStorageDevice mSelectedDevice = devices[0];
                                try {
                                    mSelectedDevice.init();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    dismissProgress();
                                    showSnack("Backup Failed, Try again later");
                                    getActivity().unregisterReceiver(mUsbReceiver);
                                }
                                FileSystem fs = mSelectedDevice.getPartitions().get(0).getFileSystem();
                                UsbFile root = fs.getRootDirectory();
                                Log.e(TAG, "onReceive: ");
                                try {
                                    String currentDBPath = getActivity().getDatabasePath(DB_NAME).getAbsolutePath();

                                    String backupDBPath = SharedPref.read(pref_MACADDRESS, "").substring(SharedPref.read(pref_MACADDRESS, "").length() - 5).replace(":", "") +"-"+
                                            new SimpleDateFormat("dd-mm-yy HH-mm-ss").format(new Date()) +"-" + "WT-IOT-DB";

                                    File currentDB = new File(currentDBPath);
                                    int len;
                                    InputStream in = new FileInputStream(currentDB);
                                    ByteBuffer buffer = ByteBuffer.allocate(4096);
                                    UsbFile file =  root.createDirectory(backupDBPath).createFile("AutoChemDataBase.db");
                                    UsbFileOutputStream mOutPut = new UsbFileOutputStream(file);

                                    while ((len = in.read(buffer.array())) > 0) {
                                        mOutPut.write(buffer.array());
                                    }

                                    in.close();
                                    file.close();
                                    mOutPut.close();
                                    dismissProgress();
                                    showSnack("Backup Complete | Dir :" + device.getManufacturerName() + "/" + backupDBPath + "AutoChem.db");
                                    new EventLogDemo("", "", "Data backuped with OTG", SharedPref.read(pref_USERLOGINID, ""), getContext());
                                    ApiService.getInstance(getContext()).processApiData("1", "00", ("Data Backuped with OTG by #" + SharedPref.read(pref_USERLOGINID, "")));
                                    getActivity().unregisterReceiver(mUsbReceiver);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    dismissProgress();
                                    showSnack("Backup Failed, Try again later");
                                    getActivity().unregisterReceiver(mUsbReceiver);
                                }
                            }
                        }
                    }
                    Log.e(TAG, "onReceive: ");
                }
            }
        }
    };


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

        mBinding.passwordSetting.setOnClickListener(this);
        mBinding.factorySetting.setOnClickListener(this);
        mBinding.getAllConfig.setOnClickListener(this);
        mBinding.sendAllConfig.setOnClickListener(this);
        mBinding.logOut.setOnClickListener(this);
        mBinding.pendrive.setOnClickListener(this);
        mBinding.disconnectDuConfig.setOnClickListener(this);
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
        mAppclass.sendPacket(new DataReceiveCallback() { // {*0$17$0*}
            @Override
            public void OnDataReceive(String data) {
                if (data != null) {
                    if (data.split("\\*")[1].split("\\$")[2].equals("0")) {
                        dataReceived[0] = true;
                        dismissProgress();
                        if (mAppclass.factoryRest()) {
                            mAppclass.showSnackBar(getContext(), "Factory Reset Success");
                            new EventLogDemo("", "", "Factory Reset by #", SharedPref.read(pref_USERLOGINID, ""), getContext());
                            ApiService.getInstance(getContext()).processApiData("1", "00", ("Factory Reset by #" + SharedPref.read(pref_USERLOGINID, "")));
                        } else {
                            mAppclass.showSnackBar(getContext(), "Factory Reset Failed, try again later");
                        }
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        mAppclass.showSnackBar(getContext(), "Factory Reset Failed");
                    }
                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_FACTORYRESET + SPILT_CHAR + ACK);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!dataReceived[0]) {
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
                otgDetected = false;
                new MaterialAlertDialogBuilder(getContext()).setTitle("Backup")
                        .setMessage("Please insert your OTG-USB and press continue")
                        .setPositiveButton("continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showProgress();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        UsbDevice device = null;
                                        UsbManager mUsbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
                                        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
                                        for (UsbDevice usbDevice : deviceList.values()) {
                                            device = usbDevice;
                                        }
                                        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                                        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                                        getActivity().registerReceiver(mUsbReceiver, filter);
                                        if (device != null) {
                                            mUsbManager.requestPermission(device, mPermissionIntent);
                                        }
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!otgDetected) {
                                                    dismissProgress();
                                                    getActivity().unregisterReceiver(mUsbReceiver);
                                                    showSnack("Unable to Detect OTG-USB, Please try again later !");
                                                }
                                            }
                                        }, 10000);
                                    }
                                }, 5000);
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().unregisterReceiver(mUsbReceiver);
                        dialogInterface.dismiss();
                    }
                }).show();
                break;


            case R.id.send_all_config:
                mAppclass.navigateTo(getActivity(), R.id.action_passwordSettings_to_fragmentSendAllPacket);
                break;

            case R.id.get_all_config:
                mAppclass.navigateTo(getActivity(), R.id.action_passwordSettings_to_fragmentGetAllPacket);
                break;

            case R.id.disconnect_du_config:
                BaseActivity.disconnectBle();
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
