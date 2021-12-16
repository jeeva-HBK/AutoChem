package com.ionexchange.Fragments.Configuration.VirtualConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.VirtualSensorIndexRvAdapter;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.KeepAliveCurrentEntity;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Fragments.FragmentHostDashboard;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentVirtualsensorBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

//created by Silambu

public class FragmentVirtualSensorList_Config extends Fragment implements RvOnClick {
    RvOnClick rvOnClick;
    ApplicationClass mAppClass;
    FragmentVirtualsensorBinding mBinding;
    VirtualConfigurationDao dao;
    List<VirtualConfigurationEntity> virtualConfigurationEntityList;
    WaterTreatmentDb waterTreatmentDb;
    KeepAliveCurrentValueDao keepAliveCurrentValueDao;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_virtualsensor, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        dao = ApplicationClass.virtualDAO;
        waterTreatmentDb = WaterTreatmentDb.getDatabase(getContext());
        keepAliveCurrentValueDao = waterTreatmentDb.keepAliveCurrentValueDao();
        virtualConfigurationEntityList = dao.getVirtualConfigurationEntityList();

        mBinding.viRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mBinding.viRv.setAdapter(new VirtualSensorIndexRvAdapter(this, virtualConfigurationEntityList,keepAliveCurrentValueDao));
        keepAliveCurrentValueDao.getLiveList().observe(getViewLifecycleOwner(), new Observer<List<KeepAliveCurrentEntity>>() {
            @Override
            public void onChanged(List<KeepAliveCurrentEntity> keepAliveCurrentEntities) {
                mBinding.viRv.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(int sensorInputNo) {
        Bundle bundle = new Bundle();
        bundle.putInt("virtualInputNo", sensorInputNo);
        mAppClass.navigateToBundle(getActivity(), R.id.action_virtualSetting_to_virtual, bundle);
    }


    @Override
    public void onClick(String sensorInputNo) {

    }

    @Override
    public void onClick(MainConfigurationEntity mEntity) {

    }
}
