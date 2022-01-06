package com.ionexchange.Fragments.Configuration;

import static com.ionexchange.Activity.BaseActivity.showProgress;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Adapters.ExpandableListAdapter;
import com.ionexchange.Fragments.Configuration.GeneralConfig.FragmentSiteSettings_Config;
import com.ionexchange.Fragments.Configuration.GeneralConfig.FragmentPasswordSetting_Config;
import com.ionexchange.Fragments.Configuration.GeneralConfig.FragmentTargetIpSettings_Config;
import com.ionexchange.Fragments.Configuration.HomeScreen.FragmentHomeScreen_Config;
import com.ionexchange.Fragments.Configuration.InputConfig.FragmentInputSensorList_Config;
import com.ionexchange.Fragments.Configuration.OutputConfig.FragmentOutputSettings_Config;
import com.ionexchange.Fragments.Configuration.TimerConfig.FragmentTimer_Config;
import com.ionexchange.Fragments.Configuration.VirtualConfig.FragmentVirtualSensorList_Config;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentConfigurationBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FragmentRoot_Config extends Fragment implements ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {
    FragmentConfigurationBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    private int lastPosition = -1;
    HashMap<String, List<String>> childList;
    List<String> generaList, ioList, homescreenList, headerList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_configuration, container, false);
        return mBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        init();
    }

    void init() {
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();

        childList = new HashMap<>();

        generaList = new ArrayList<>();
        ioList = new ArrayList<>();
        homescreenList = new ArrayList<>();
        headerList = new ArrayList<>();

        generaList.add("- Unit Settings");
        // generaList.add("- Target IP Settings");
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

        mBinding.expList.setAdapter(new ExpandableListAdapter(getContext(), headerList, childList));
        mBinding.expList.setOnGroupExpandListener(this);
        mBinding.expList.setOnGroupClickListener(this);
        mBinding.expList.setOnChildClickListener(this);
        mBinding.expList.setChildDivider(getResources().getDrawable(R.color.primary));
        mBinding.expList.setDivider(getResources().getDrawable(R.color.primary));

        mActivity.changeToolBarVisibility(View.GONE);
        mBinding.expList.setGroupIndicator(null);
        mBinding.expList.expandGroup(0);
        onGroupClick(mBinding.expList, null, 0, 0);

        //  mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentUnitIpSettings_Config());
        //  mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentCommonSettings_Config());
    }

    void cast(Fragment fragment) {
        mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, fragment);
    }

    @Override
    public void onGroupExpand(int pos) {
        if (lastPosition != -1 && pos != lastPosition) {
            mBinding.expList.collapseGroup(lastPosition);
        }
        lastPosition = pos;
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int pos, long l) {
        expandableListView.setItemChecked(pos, true);
        if (pos == 2) {
            cast(new FragmentHomeScreen_Config());
        }
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int pos, int pos1, long pos2) {
        Log.e("TAG", "onChildClick: " + expandableListView + "|" + view + "|" + pos + "|" + pos1 + "|" + pos2);
        expandableListView.getChildAt((pos == 0) ? pos1 + 1 : pos1 + 2).setActivated(true);
        showProgress();
        switch (pos) {

            case 0:
                switch (pos1) {
                    case 0:
                        cast(new FragmentPasswordSetting_Config());
                        break;
                    case 1:
                        cast(new FragmentTargetIpSettings_Config());
                        break;
                    case 2:
                        cast(new FragmentSiteSettings_Config());
                        break;
                }
                break;

            case 1:
                switch (pos1) {
                    case 0:
                        cast(new FragmentInputSensorList_Config());
                        break;
                    case 1:
                        cast(new FragmentOutputSettings_Config());
                        break;
                    case 2:
                        cast(new FragmentTimer_Config());
                        break;

                    case 3:
                        cast(new FragmentVirtualSensorList_Config());
                        break;
                }
                break;
        }
        return false;
    }
}
