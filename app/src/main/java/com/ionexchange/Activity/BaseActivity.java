package com.ionexchange.Activity;

import static android.os.Build.VERSION.SDK_INT;
import static com.ionexchange.Fragments.Configuration.TimerConfig.FragmentTimer_Config.accessoryTimerDialog;
import static com.ionexchange.Fragments.Configuration.TimerConfig.FragmentTimer_Config.dayalertDialog;
import static com.ionexchange.Others.ApplicationClass.DB;
import static com.ionexchange.Others.ApplicationClass.bleConnected;
import static com.ionexchange.Others.ApplicationClass.defaultPassword;
import static com.ionexchange.Others.ApplicationClass.triggerWebService;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Singleton.SharedPref.pref_LOGGEDIN;
import static com.ionexchange.Singleton.SharedPref.pref_MACADDRESS;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINID;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINNAME;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINREQUIRED;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINROLE;
import static com.ionexchange.Singleton.SharedPref.pref_USERLOGINSTATUS;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.ionexchange.Adapters.ExpandableListAdapter;
import com.ionexchange.BLE.BluetoothConnectCallback;
import com.ionexchange.BLE.BluetoothHelper;
import com.ionexchange.BLE.BluetoothScannerCallback;
import com.ionexchange.Database.Dao.AlarmLogDao;
import com.ionexchange.Database.Dao.UserManagementDao;
import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Others.AdminReceiver;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.Others.EventLogDemo;
import com.ionexchange.R;
import com.ionexchange.Singleton.SharedPref;
import com.ionexchange.databinding.ActivityBaseBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Created By silambu
public class BaseActivity extends AppCompatActivity implements View.OnClickListener,
        ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupClickListener,
        ExpandableListView.OnChildClickListener {
    private static final int PERMISSION_REQUEST_CODE = 2296;
    private static final String TAG = "BaseActivity";
    private static final String DEBUG_TAG = "Gestures";
    public static boolean canGoBack;
    static CoordinatorLayout coordinatorLayout;
    static Context context;
    static ActivityBaseBinding msBinding;
    static Context msContext;
    static BaseActivity baseActivity;
    static ApplicationClass msAppClass;
    static android.app.AlertDialog reconnectDialog;
    static int retryCount = 0;
    static boolean tempBool = true;
    static TextView attemptTxt;
    ActivityBaseBinding mBinding;
    AppBarConfiguration mAppBarConfiguration;
    NavController mNavController;
    NavGraph navGraph;
    ApplicationClass mAppClass;
    HashMap<String, List<String>> childList;
    List<String> generaList, ioList, homescreenList, headerList;
    boolean permission = false;
    GestureDetector gestureDetector;
    Runnable userInactive;
    Handler handler;
    WaterTreatmentDb db;
    UserManagementDao userManagementDao;
    AlarmLogDao alarmLogDao;
    private int lastPosition = -1;

    public static void showSnack(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        TextView tv = (TextView) snackbar.getView().findViewById(R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showProgress() {
        msBinding.progressCircular.setVisibility(View.VISIBLE);
        msBinding.progressBar.setVisibility(View.VISIBLE);
        baseActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        canGoBack = false;
    }

    public static void dismissProgress() {
        Log.e(TAG, "Config-dismissProgress: ");
        msBinding.progressCircular.setVisibility(View.GONE);
        msBinding.progressBar.setVisibility(View.GONE);
        /* msBinding.progressBar.loop(false);
        msBinding.progressBar.pauseAnimation();
        msBinding.progressBar.clearAnimation();
        msBinding.progressBar.cancelAnimation();*/
        baseActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        canGoBack = true;
    }

    public static void logOut() {
        new MaterialAlertDialogBuilder(context)
                .setTitle("LOGOUT")
                .setMessage("Are you sure, you want to Logout ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPref.write(pref_LOGGEDIN, false);
                        SharedPref.write(pref_USERLOGINREQUIRED, true);
                        SharedPref.write(pref_USERLOGINSTATUS, 0);
                        /*PackageManager packageManager = context.getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
                        ComponentName componentName = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                        context.startActivity(mainIntent);
                        Runtime.getRuntime().exit(0);*/
                        msBinding.mainScreenBtn.performClick();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public static void disconnectBle() {
        new MaterialAlertDialogBuilder(context)
                .setTitle("LOGOUT")
                .setMessage("Are you sure, you want to Logout ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPref.write(pref_LOGGEDIN, false);
                        SharedPref.write(pref_USERLOGINREQUIRED, true);
                        SharedPref.write(pref_USERLOGINSTATUS, 0);
                        PackageManager packageManager = context.getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
                        ComponentName componentName = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                        context.startActivity(mainIntent);
                        Runtime.getRuntime().exit(0);
                        msBinding.mainScreenBtn.performClick();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public static void reconnect() {
        if (!SharedPref.read(pref_MACADDRESS, "").equals("")) {
            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    baseActivity.stopHandler();
                    android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
                    LayoutInflater inflater = baseActivity.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_reconnect, null);
                    attemptTxt = dialogView.findViewById(R.id.attempt);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setCancelable(false);
                    reconnectDialog = dialogBuilder.create();
                    tempBool = true;
                    startReconnect();
                    reconnectDialog.show();
                }
            });
        }
    }

    private static void startReconnect() {
        Log.e(TAG, "Reconnect: start" + "tempBool - " + tempBool + "retryCount -" + retryCount);
        if (attemptTxt != null) {
            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    attemptTxt.setText("attempt : " + retryCount + "/3");
                }
            });
        }
        if (retryCount < 3) {
            try {
                BluetoothHelper.getInstance().scanBLE(new BluetoothScannerCallback() {
                    @Override
                    public void OnScanCompleted(List<BluetoothDevice> devices) {
                        Log.e(TAG, "OnScanCompleted: " + "tempBool - " + tempBool + "retryCount -" + retryCount);
                        if (tempBool) {
                            retryCount++;
                            startReconnect();
                        }
                        for (int i = 0; i < devices.size(); i++) {
                            if (devices.get(i).getAddress().equals(SharedPref.read(pref_MACADDRESS, ""))) {
                                Log.e(TAG, "OnScanDevice found -> " + "tempBool - " + tempBool + "retryCount -" + retryCount);
                                tempBool = false;
                                connect(devices.get(i));
                                break;
                            }
                        }
                    }

                    @Override
                    public void SearchResult(BluetoothDevice device) {
                    }

                    @Override
                    public void OnDeviceFoundUpdate(List<BluetoothDevice> devices) {
                        Log.e(TAG, "OnDeviceFoundUpdate: " + "Devices size" + devices.size() + "tempBool - " + tempBool + "retryCount -" + retryCount);
                        for (BluetoothDevice d : devices) {
                            if (d.getAddress().equals(SharedPref.read(pref_MACADDRESS, ""))) {
                                Log.e(TAG, "onFounDevice found -> " + d + "tempBool - " + tempBool + "retryCount -" + retryCount);
                                tempBool = false;
                                connect(d);
                                break;
                            }
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "startReconnect: Catch" + "tempBool - " + tempBool + "retryCount -" + retryCount);
                retryCount++;
                startReconnect();
                e.printStackTrace();
            }
        } else {
            if (reconnectDialog != null && reconnectDialog.isShowing()) {
                reconnectDialog.dismiss();
            }
            showSnack("Failed to reconnect, try again later" + "tempBool - " + tempBool + "retryCount -" + retryCount);
            baseActivity.kickOut();
            baseActivity.startHandler();
            retryCount = 0;
        }
    }

    private static void connect(BluetoothDevice bluetoothDevice) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (retryCount < 3) {
                    try {
                        if (BluetoothHelper.getInstance() != null) {
                            BluetoothHelper.getInstance().disConnect();
                            BluetoothHelper.getInstance().connectBLE(context, bluetoothDevice, new BluetoothConnectCallback() {
                                @Override
                                public void OnConnectSuccess() {
                                    tempBool = false;
                                    reconnectDialog.dismiss();
                                    retryCount = 0;
                                    bleConnected.set(true);
                                    triggerWebService.set(true);
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                                        showSnack("Reconnected to " + bluetoothDevice.getName() + " Successfully");
                                        return;
                                    }
                                    baseActivity.startHandler();
                                }

                                @Override
                                public void OnConnectFailed(Exception e) {
                                    Log.e(TAG, "OnConnectFailed:");
                                    retryCount++;
                                    tempBool = true;
                                    startReconnect();
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "ConnectElse: " + "tempBool - " + tempBool + "retryCount -" + retryCount);
                        retryCount++;
                        startReconnect();
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "ConnectElse: " + "tempBool - " + tempBool + "retryCount -" + retryCount);
                }
            }
        }, 5000);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        mAppClass = (ApplicationClass) getApplication();
        msAppClass = (ApplicationClass) getApplication();
        msContext = getApplicationContext();
        context = BaseActivity.this;
        db = WaterTreatmentDb.getDatabase(getApplicationContext());
        userManagementDao = db.userManagementDao();
        alarmLogDao = db.alarmLogDao();

        setNavigation(R.navigation.navigation, R.id.Dashboard);
        expandedListView();
        baseActivity = this;
        msBinding = mBinding;
        SharedPref.write(pref_USERLOGINREQUIRED, true);
        SharedPref.write(pref_USERLOGINSTATUS, 0);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);
        ComponentName admin = new ComponentName(this, AdminReceiver.class);
        Intent intent = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
        this.startActivity(intent);
        coordinatorLayout = findViewById(R.id.cod);
        mBinding.mainScreenBtn.setOnClickListener(this);
        mBinding.trendScreenBtn.setOnClickListener(this);
        mBinding.eventLogsScreenBtn.setOnClickListener(this);
        mBinding.configScreenBtn.setOnClickListener(this);
        mBinding.expList.setOnGroupExpandListener(this);
        mBinding.expList.setOnGroupClickListener(this);
        mBinding.expList.setOnChildClickListener(this);

        inactiveHandler();
        mBinding.notficationTxt.setVisibility(View.INVISIBLE);
        mBinding.notficationView.setVisibility(View.INVISIBLE);

        alarmLogDao.getAlarmLiveList().observe(this, new Observer<List<AlarmLogEntity>>() {
            @Override
            public void onChanged(List<AlarmLogEntity> alarmLogEntities) {
                if (alarmLogEntities.size() == 0) {
                    mBinding.notficationTxt.setVisibility(View.INVISIBLE);
                    mBinding.notficationView.setVisibility(View.INVISIBLE);
                } else {
                    mBinding.notficationTxt.setVisibility(View.VISIBLE);
                    mBinding.notficationView.setVisibility(View.VISIBLE);
                    mBinding.notficationTxt.setText(String.valueOf(alarmLogEntities.size()));
                }
            }
        });
    }

    void inactiveHandler() {
        handler = new Handler();
        userInactive = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O_MR1)
            @Override
            public void run() {
                screenTimeout();
            }
        };
        startHandler();
    }

    private void lock() {
        SharedPref.write(pref_USERLOGINREQUIRED, true);
        SharedPref.write(pref_USERLOGINSTATUS, 0);
        mBinding.mainScreenBtn.performClick();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isScreenOn()) {
            DevicePolicyManager policy = (DevicePolicyManager)
                    getSystemService(Context.DEVICE_POLICY_SERVICE);
            try {
                policy.lockNow();
            } catch (SecurityException ex) {
                Toast.makeText(this,
                        "must enable device administrator",
                        Toast.LENGTH_LONG).show();
                ComponentName admin = new ComponentName(this, AdminReceiver.class);
                Intent intent = new Intent(
                        DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).putExtra(
                        DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
                this.startActivity(intent);
            }
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        stopHandler();//stop first and then start
        startHandler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopHandler();
    }

    public void stopHandler() {
        handler.removeCallbacks(userInactive);
    }

    public void startHandler() {
        handler.postDelayed(userInactive, 5 * 60 * 1000); //for 5 minutes
    }

    void expandedListView() {
        childList = new HashMap<>();

        generaList = new ArrayList<>();
        ioList = new ArrayList<>();
        homescreenList = new ArrayList<>();
        headerList = new ArrayList<>();

        generaList.add("- Site Settings");
        generaList.add("- DU Settings");
        // generaList.add("- Target IP Settings");

        ioList.add("- Input Settings");
        ioList.add("- Output Settings");
        ioList.add("- Timer Settings");
        ioList.add("- Virtual Settings");

        headerList.add("General Settings");
        headerList.add("I/O Settings");
        headerList.add("Home Settings");

        childList.put("General Settings", generaList);
        childList.put("I/O Settings", ioList);
        childList.put("Home Settings", homescreenList);

        mBinding.expList.setAdapter(new ExpandableListAdapter(this, headerList, childList, true));

        mBinding.expList.setChildDivider(getResources().getDrawable(R.color.primary));
        mBinding.expList.setDivider(getResources().getDrawable(R.color.primary));

        changeToolBarVisibility(View.GONE);

        mBinding.expList.expandGroup(0);
        onGroupClick(mBinding.expList, null, 0, 0);

    }

    public void setNavigation(int navigation, int des) {
        mNavController = Navigation.findNavController(this, R.id.nav_host_frag);
        navGraph = mNavController.getNavInflater().inflate(navigation);
        navGraph.setStartDestination(des);
        mNavController.setGraph(navGraph);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_frag);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setNormalState() {
        mBinding.homeMain.setVisibility(View.VISIBLE);
        mBinding.statisticsMain.setVisibility(View.VISIBLE);
        mBinding.supportMain.setVisibility(View.VISIBLE);
        mBinding.configMain.setVisibility(View.VISIBLE);

        mBinding.homeBigCircle.setVisibility(View.INVISIBLE);
        mBinding.homeSmallCircle.setVisibility(View.INVISIBLE);
        mBinding.homeSub.setVisibility(View.INVISIBLE);
        mBinding.homeText.setVisibility(View.INVISIBLE);

        mBinding.statisticsBigCircle.setVisibility(View.INVISIBLE);
        mBinding.statisticsSmallCircle.setVisibility(View.INVISIBLE);
        mBinding.statisticsSub.setVisibility(View.INVISIBLE);
        mBinding.statisticsText.setVisibility(View.INVISIBLE);

        mBinding.supportBigCircle.setVisibility(View.INVISIBLE);
        mBinding.supportSmallCircle.setVisibility(View.INVISIBLE);
        mBinding.supportSub.setVisibility(View.INVISIBLE);
        mBinding.supportText.setVisibility(View.INVISIBLE);
        mBinding.notficationView.setVisibility(View.INVISIBLE);
        mBinding.configBigCircle.setVisibility(View.INVISIBLE);
        mBinding.configSmallCircle.setVisibility(View.INVISIBLE);
        mBinding.configSub.setVisibility(View.INVISIBLE);
        mBinding.configText.setVisibility(View.INVISIBLE);
    }

    private void setNewState(View bigCircle, View main, View sub, View smallCircle, TextView txtView, NavGraph NavGraph,
                             int fragment, NavController navController) {
        setNormalState();
        bigCircle.setVisibility(View.VISIBLE);
        smallCircle.setVisibility(View.VISIBLE);
        sub.setVisibility(View.VISIBLE);
        txtView.setVisibility(View.VISIBLE);
        main.setVisibility(View.INVISIBLE);
        NavGraph.setStartDestination(fragment);
        navController.setGraph(navGraph);
    }

    void setNavGraph(NavGraph NavGraph, int fragment, NavController navController) {
        NavGraph.setStartDestination(fragment);
        navController.setGraph(navGraph);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_screen_btn:
                //showProgress();
                setNewState(mBinding.homeBigCircle, mBinding.homeMain, mBinding.homeSub, mBinding.homeSmallCircle,
                        mBinding.homeText, navGraph, R.id.Dashboard, mNavController);
                mBinding.view.setVisibility(View.GONE);
                break;

            case R.id.trend_screen_btn:
                showProgress();
                setNewState(mBinding.statisticsBigCircle, mBinding.statisticsMain, mBinding.statisticsSub, mBinding.statisticsSmallCircle,
                        mBinding.statisticsText, navGraph, R.id.trend, mNavController);
                mBinding.view.setVisibility(View.GONE);
                //mAppClass.showSnackBar(getApplicationContext(), "UNDER DEVELOPMENT !");
                break;

            case R.id.event_logs_screen_btn:
                showProgress();
                setNewState(mBinding.supportBigCircle, mBinding.supportMain, mBinding.supportSub, mBinding.supportSmallCircle, mBinding.supportText,
                        navGraph, R.id.event_log, mNavController);
                mBinding.view.setVisibility(View.GONE);
                break;

            case R.id.config_screen_btn:
                if (SharedPref.read(pref_USERLOGINREQUIRED, true)) {
                    checkUserLogin();
                } else {
                    moveToConfig();
                }
                break;
        }
    }

    private void screenTimeout() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BaseActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_time_out, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        TextView dialog_timeout_txt = dialogView.findViewById(R.id.dialog_timeout_txt);
        CountDownTimer CountDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.e(TAG, "onTick: "+ millisUntilFinished / 1000 + ""+ millisUntilFinished);
                dialog_timeout_txt.setText(millisUntilFinished / 1000 + "");
            }

            public void onFinish() {
                alertDialog.dismiss();
                if (dayalertDialog != null) {
                    if (dayalertDialog.isShowing()) {
                        dayalertDialog.dismiss();
                    }
                }
                if (accessoryTimerDialog != null) {
                    if (accessoryTimerDialog.isShowing()) {
                        accessoryTimerDialog.dismiss();
                    }
                }
                lock();

            }

        }.start();
        btnCancel.setOnClickListener(V -> {
            alertDialog.dismiss();
            CountDownTimer.cancel();
            onUserInteraction();
        });
    }

    private void checkUserLogin() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BaseActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.show();

        EditText userName = dialogView.findViewById(R.id.dialog_usernameEdt);
        userName.setText("SuperAdmin");
        EditText password = dialogView.findViewById(R.id.dialog_passwordEdt);
        password.setText("123456");
        Button loginBtn = dialogView.findViewById(R.id.dialog_login);
        TextView forgotPassword = dialogView.findViewById(R.id.forgot_password);
        DB = WaterTreatmentDb.getDatabase(getApplicationContext());
        userManagementDao = DB.userManagementDao();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName.getText().toString().equals("")) {
                    Snackbar.make(dialogView, "Username should not be empty", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (password.getText().toString().equals("")) {
                    Snackbar.make(dialogView, "Password should not be empty", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (userManagementDao.getPassword(userName.getText().toString()) == null) {
                    Snackbar.make(dialogView, "User not found", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (password.getText().toString().equals(userManagementDao.getPassword(userName.getText().toString()))) {
                    alertDialog.dismiss();
                    SharedPref.write(pref_USERLOGINNAME, userName.getText().toString());
                    SharedPref.write(pref_USERLOGINROLE, userManagementDao.getRole(userName.getText().toString()));
                    userType = SharedPref.read(pref_USERLOGINROLE, 0);
                    moveToConfig();
                } else {
                    Snackbar.make(dialogView, "Password is Incorrect", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                forgotPassword();
            }
        });
    }

    void forgotPassword() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BaseActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        EditText defPassword = dialogView.findViewById(R.id.dialog_default_pass_Edt);
        EditText password = dialogView.findViewById(R.id.dialog_current_pass_Edt);
        Button confirmBtn = dialogView.findViewById(R.id.cnf_btn);
        confirmBtn.setOnClickListener(v -> {
            if (defPassword.getText().toString().equals("")) {
                mAppClass.showSnackBar(BaseActivity.this, "Default Password should be empty");
                return;
            }
            if (password.getText().toString().equals("")) {
                mAppClass.showSnackBar(BaseActivity.this, "New Password should be empty");
                return;
            }
            if (!defPassword.getText().toString().equals(defaultPassword)) {
                mAppClass.showSnackBar(BaseActivity.this, "Default Password not Matched");
            } else {
                userManagementDao.updatePassword(password.getText().toString(), SharedPref.read(pref_USERLOGINNAME, ""));
                mAppClass.showSnackBar(getApplicationContext(), "Password changed");
                alertDialog.dismiss();
            }
        });
    }

    private void moveToConfig() {
        showProgress();
        SharedPref.write(pref_USERLOGINID, userManagementDao.getUserId(SharedPref.read(pref_USERLOGINNAME, "")));
        SharedPref.write(pref_USERLOGINSTATUS, 1);
        new EventLogDemo("0", "-", "Menu accessed by " + SharedPref.read(pref_USERLOGINNAME, ""),
                userManagementDao.getUserId(SharedPref.read(pref_USERLOGINNAME, "")), getApplicationContext());
        setNewState(mBinding.configBigCircle, mBinding.configMain, mBinding.configSub, mBinding.configSmallCircle, mBinding.configText,
                navGraph, R.id.siteSetting, mNavController);
        expandedListView();
        mBinding.view.setVisibility(View.VISIBLE);
        SharedPref.write(pref_USERLOGINREQUIRED, false);

    }

    public void changeActionBarText(String titile) {
        //  mBinding.toolbarText.setText(titile);
    }

    public void changeToolBarVisibility(int visibility) {
        // mBinding.toolBar.setVisibility(visibility);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onGroupExpand(int pos) {
        if (lastPosition != -1 && pos != lastPosition) {
            mBinding.expList.collapseGroup(lastPosition);
        }
        lastPosition = pos;
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View v, int groupPosition, long id) {
        expandableListView.setItemChecked(groupPosition, true);
        if (groupPosition == 0) {
            if (userType != 0) {
                setNavGraph(navGraph, R.id.siteSetting, mNavController);
            }
        } else if (groupPosition == 1) {
            setNavGraph(navGraph, R.id.inputSetting, mNavController);
        } else if (groupPosition == 2) {
            if (userType == 3) {
                setNavGraph(navGraph, R.id.homeScreen, mNavController);
            } else {
                mAppClass.showSnackBar(getApplicationContext(), "Access Denied");
            }
        }
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View v, int pos, int pos1, long id) {
        expandableListView.getChildAt((pos == 0) ? pos1 + 1 : pos1 + 2).setActivated(true);
        switch (pos) {
            case 0:
                showProgress();
                switch (pos1) {
                    case 0:
                        if (userType == 3) {
                            setNavGraph(navGraph, R.id.siteSetting, mNavController);
                        } else {
                            showSnack("Access Denied");
                        }
                        break;

                    case 1:
                        setNavGraph(navGraph, R.id.passwordSettings, mNavController);
                        //setNavGraph(navGraph, R.id.targetIpSetting, mNavController);
                        break;
                }
                break;
            case 1:
                switch (pos1) {
                    case 0:
                        setNavGraph(navGraph, R.id.inputSetting, mNavController);
                        break;
                    case 1:
                        setNavGraph(navGraph, R.id.outputSetting, mNavController);
                        break;
                    case 2:
                        setNavGraph(navGraph, R.id.TimerSetting, mNavController);
                        break;
                    case 3:
                        setNavGraph(navGraph, R.id.virtualSetting, mNavController);
                        break;
                }
                break;

        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        startHandler();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            requestPermission();
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 2296);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {

                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        // perform action when allow permission success
                    } else {
                        Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public void hideStatusNavigationBar() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void kickOut() {
        Intent intent = new Intent(this, ConnectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}

