package com.ionexchange.Fragments.Configuration.OutputConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.OutputIndexRvAdapter;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentOutputsettingsBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentOutputSettings_Config extends Fragment implements RvOnClick {

    FragmentOutputsettingsBinding mBinding;
    WaterTreatmentDb dB;
    OutputConfigurationDao dao;
    int pageOffset = 0, currentPage = 0;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_outputsettings, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dB = WaterTreatmentDb.getDatabase(getContext());
        dao = dB.outputConfigurationDao();
        if (dao.getOutputConfigurationEntityList().isEmpty()) {
            for (int i = 1; i < 23; i++) {
                OutputConfigurationEntity entityUpdate = new OutputConfigurationEntity
                        (i, "output-" + i, "N/A",
                                "N/A",
                                "N/A");
                List<OutputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
            }
        }
        mBinding.rightArrowOsBtn.setVisibility(View.VISIBLE);

        mBinding.outputRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mBinding.outputRv.setAdapter(new OutputIndexRvAdapter(this, dao.getOutputConfigurationEntityList(9, pageOffset)));

        mBinding.leftArrowOsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage--;
                mBinding.outputRv.setAdapter(new OutputIndexRvAdapter(FragmentOutputSettings_Config.this, dao.getOutputConfigurationEntityList(9, pageOffset = pageOffset - 9)));
                mBinding.leftArrowOsBtn.setVisibility(currentPage <= 0 ? View.GONE : View.VISIBLE);
                mBinding.rightArrowOsBtn.setVisibility(View.VISIBLE);
            }
        });

        mBinding.rightArrowOsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage++;
                mBinding.leftArrowOsBtn.setVisibility(View.VISIBLE);
                mBinding.outputRv.setAdapter(new OutputIndexRvAdapter(FragmentOutputSettings_Config.this, dao.getOutputConfigurationEntityList(9, pageOffset = pageOffset + 9)));
                mBinding.rightArrowOsBtn.setVisibility(dao.getOutputConfigurationEntityList(9, pageOffset + 9).isEmpty() ? View.GONE : View.VISIBLE);
            }
        });

    }

    @Override
    public void onClick(int sensorInputNo) {
        mBinding.outputRv.setVisibility(View.GONE);
        mBinding.outputHost.setVisibility(View.VISIBLE);
        getParentFragmentManager().beginTransaction().replace(mBinding.outputHost.getId(), new FragmentOutput_Config(sensorInputNo)).commit();
    }

    @Override
    public void onClick(String sensorInputNo) {

    }

    @Override
    public void onClick(String sensorInputNo, String type, int position) {

    }

    public void updateToDb(List<OutputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        OutputConfigurationDao dao = db.outputConfigurationDao();
        dao.insert(entryList.toArray(new OutputConfigurationEntity[0]));
    }

}
