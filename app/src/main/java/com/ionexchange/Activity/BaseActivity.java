package com.ionexchange.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ionexchange.Adapters.ExpandableListAdapter;
import com.ionexchange.R;
import com.ionexchange.databinding.ActivityBaseBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.userType;

///Created By silambu
public class BaseActivity extends AppCompatActivity implements View.OnClickListener, ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {
    ActivityBaseBinding mBinding;
    private static final String TAG = "BaseActivity";
    boolean canGoBack;
    AppBarConfiguration mAppBarConfiguration;
    NavController mNavController;
    NavGraph navGraph;
    private int lastPosition = -1;
    HashMap<String, List<String>> childList;
    List<String> generaList, ioList, homescreenList, headerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        setNavigation();
        expandedListView();
        mBinding.mainScreenBtn.setOnClickListener(this);
        mBinding.trendScreenBtn.setOnClickListener(this);
        mBinding.eventLogsScreenBtn.setOnClickListener(this);
        mBinding.configScreenBtn.setOnClickListener(this);
        mBinding.expList.setOnGroupExpandListener(this);
        mBinding.expList.setOnGroupClickListener(this);
        mBinding.expList.setOnChildClickListener(this);
    }

    void expandedListView() {
        childList = new HashMap<>();

        generaList = new ArrayList<>();
        ioList = new ArrayList<>();
        homescreenList = new ArrayList<>();
        headerList = new ArrayList<>();

        generaList.add("- Unit IP Settings");
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
        //      this.onChildClick(mBinding.expList, mBinding.expList.getChildAt(0).getRootView(), 0, 0, 0);

    }

    void setNavigation() {
        mNavController = Navigation.findNavController(this, R.id.nav_host_frag);
        navGraph = mNavController.getNavInflater().inflate(R.navigation.navigation);
        navGraph.setStartDestination(R.id.Dashboard);
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
                    setNewState(mBinding.configBigCircle, mBinding.configMain, mBinding.configSub, mBinding.configSmallCircle, mBinding.configText,
                            navGraph, R.id.configuration, mNavController);
                    expandedListView();
                    mBinding.view.setVisibility(View.VISIBLE);
                    break;

        }
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
    public void onBackPressed() { }

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
}