package com.ionexchange.Fragments.Configuration.VirtualConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.VirtualInputIndexRvAdapter;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentVirtualsensorBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentVirtualSensorList_Config extends Fragment implements RvOnClick {
    RvOnClick rvOnClick;
    FragmentVirtualsensorBinding mBinding;
    WaterTreatmentDb db;
    VirtualConfigurationDao dao;
    List<VirtualConfigurationEntity> virtualConfigurationEntityList;

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
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.virtualConfigurationDao();
        if (dao.getVirtualConfigurationEntityList().isEmpty()) {
            for (int i = 46; i < 54; i++) {
                VirtualConfigurationEntity entityUpdate = new VirtualConfigurationEntity
                        (i,  0, "N/A",
                                "N/A", "N/A");
                List<VirtualConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
            }
        }
        virtualConfigurationEntityList = dao.getVirtualConfigurationEntityList();
        mBinding.viRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mBinding.viRv.setAdapter(new VirtualInputIndexRvAdapter(this, virtualConfigurationEntityList));
    }

    public void updateToDb(List<VirtualConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        VirtualConfigurationDao dao = db.virtualConfigurationDao();
        dao.insert(entryList.toArray(new VirtualConfigurationEntity[0]));
    }

    @Override
    public void onClick(int sensorInputNo) {
        mBinding.viRv.setVisibility(View.GONE);
        mBinding.view8.setVisibility(View.GONE);
        getParentFragmentManager().beginTransaction().replace(mBinding.viHost.getId(), new FragmentVirtualSensor_config(sensorInputNo)).commit();
    }

    @Override
    public void onClick(String sensorInputNo) {

    }
}
