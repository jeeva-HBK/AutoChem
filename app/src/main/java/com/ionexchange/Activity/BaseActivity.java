package com.ionexchange.Activity;

import static android.os.Build.VERSION.SDK_INT;
import static com.ionexchange.Others.ApplicationClass.DB;
import static com.ionexchange.Others.ApplicationClass.editor;
import static com.ionexchange.Others.ApplicationClass.preferences;
import static com.ionexchange.Others.ApplicationClass.userManagementDao;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ionexchange.Adapters.ExpandableListAdapter;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Others.AdminReceiver;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.Others.TcpServer;
import com.ionexchange.R;
import com.ionexchange.databinding.ActivityBaseBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

///Created By silambu
public class BaseActivity extends AppCompatActivity implements View.OnClickListener,
        ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupClickListener,
        ExpandableListView.OnChildClickListener {
    ActivityBaseBinding mBinding;
    private static final int PERMISSION_REQUEST_CODE = 2296;
    private static final String TAG = "BaseActivity";
    boolean canGoBack;
    AppBarConfiguration mAppBarConfiguration;
    NavController mNavController;
    NavGraph navGraph;
    ApplicationClass mAppClass;
    private int lastPosition = -1;
    HashMap<String, List<String>> childList;
    List<String> generaList, ioList, homescreenList, headerList;
    boolean permission = false;
    private static final String DEBUG_TAG = "Gestures";
    GestureDetector gestureDetector;
    Runnable userInactive;
    Handler handler;



    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        mAppClass = (ApplicationClass) getApplication();

        startService(new Intent(this, TcpServer.class).setAction(TcpServer.START_SERVER));
        expandedListView();
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);
        ComponentName admin = new ComponentName(this, AdminReceiver.class);
        Intent intent = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
        this.startActivity(intent);
        mBinding.mainScreenBtn.setOnClickListener(this);
        mBinding.trendScreenBtn.setOnClickListener(this);
        mBinding.eventLogsScreenBtn.setOnClickListener(this);
        mBinding.configScreenBtn.setOnClickListener(this);
        mBinding.expList.setOnGroupExpandListener(this);
        mBinding.expList.setOnGroupClickListener(this);
        mBinding.expList.setOnChildClickListener(this);
        setNavigation(R.navigation.navigation, R.id.Dashboard);

        inactiveHandler();


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
        editor.putBoolean("requiredUserLogin", true).commit();
        editor.apply();
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

        generaList.add("- Unit Settings");
        generaList.add("- Target IP Settings");
        generaList.add("- Site Settings");

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

        mBinding.expList.setAdapter(new ExpandableListAdapter(this, headerList, childList));

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
                setNewState(mBinding.homeBigCircle, mBinding.homeMain, mBinding.homeSub, mBinding.homeSmallCircle,
                        mBinding.homeText, navGraph, R.id.Dashboard, mNavController);
                mBinding.view.setVisibility(View.GONE);
                break;

            case R.id.trend_screen_btn:
                setNewState(mBinding.statisticsBigCircle, mBinding.statisticsMain, mBinding.statisticsSub, mBinding.statisticsSmallCircle,
                        mBinding.statisticsText, navGraph, R.id.trend, mNavController);
                mBinding.view.setVisibility(View.GONE);

                break;

            case R.id.event_logs_screen_btn:
                setNewState(mBinding.supportBigCircle, mBinding.supportMain, mBinding.supportSub, mBinding.supportSmallCircle, mBinding.supportText,
                        navGraph, R.id.event_log, mNavController);
                mBinding.view.setVisibility(View.GONE);
                break;

            case R.id.config_screen_btn:
                if (preferences.getBoolean("requiredUserLogin", true)) {
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
        CountDownTimer CountDownTimer=  new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                dialog_timeout_txt.setText(millisUntilFinished / 1000+"");
            }

            public void onFinish() {
                alertDialog.dismiss();
                lock();

            }

        }.start();
        btnCancel.setOnClickListener(V -> {
            alertDialog.dismiss();
            CountDownTimer.cancel();
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
        EditText password = dialogView.findViewById(R.id.dialog_passwordEdt);
        Button loginBtn = dialogView.findViewById(R.id.dialog_login);
        DB = WaterTreatmentDb.getDatabase(getApplicationContext());
        userManagementDao = DB.userManagementDao();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName.getText().toString().equals("")) {
                    mAppClass.showSnackBar(BaseActivity.this, "Username should be empty");
                    return;
                }
                if (userName.getText().toString().equals("")) {
                    mAppClass.showSnackBar(BaseActivity.this, "Password should be empty");
                    return;
                }
                if (userManagementDao.getPassword(userName.getText().toString()) == null) {
                    mAppClass.showSnackBar(BaseActivity.this, "user not found");
                    return;
                }

                if (password.getText().toString().equals(userManagementDao.getPassword(userName.getText().toString()))) {
                    alertDialog.dismiss();
                    moveToConfig();
                } else {
                    mAppClass.showSnackBar(BaseActivity.this, "password is Incorrect");
                }
            }
        });
    }

    private void moveToConfig() {
        setNewState(mBinding.configBigCircle, mBinding.configMain, mBinding.configSub, mBinding.configSmallCircle, mBinding.configText,
                navGraph, R.id.configuration, mNavController);
        expandedListView();
        mBinding.view.setVisibility(View.VISIBLE);
        editor.putBoolean("requiredUserLogin", false).commit();
        editor.apply();
    }


    public void changeActionBarText(String titile) {
        //  mBinding.toolbarText.setText(titile);
    }

    public void changeToolBarVisibility(int visibility) {
        // mBinding.toolBar.setVisibility(visibility);
    }

    public void changeProgress(int visibility) {
        mBinding.progressCircular.setVisibility(visibility);
    }

    public void showProgress() {
        mBinding.progressCircular.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        canGoBack = false;
    }

    public void dismissProgress() {
        mBinding.progressCircular.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        canGoBack = true;
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
        if (groupPosition == 2) {
            setNavGraph(navGraph, R.id.homeScreen, mNavController);
        }
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View v, int pos, int pos1, long id) {
        expandableListView.getChildAt((pos == 0) ? pos1 + 1 : pos1 + 2).setActivated(true);

        switch (pos) {
            case 0:
                switch (pos1) {
                    case 0:
                        setNavGraph(navGraph, R.id.configuration, mNavController);
                        break;
                    case 1:
                        setNavGraph(navGraph, R.id.targetIpSetting, mNavController);
                        break;
                    case 2:
                        setNavGraph(navGraph, R.id.siteSetting, mNavController);
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
    protected void onDestroy() {
        super.onDestroy();
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
}



