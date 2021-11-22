package com.ionexchange.Fragments.Configuration.OutputConfig;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.OutputIndexRvAdapter;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.Entity.OutputKeepAliveEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Enum.AppEnum;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentOutputsettingsBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FragmentOutputSettings_Config extends Fragment implements RvOnClick {

    static FragmentOutputsettingsBinding mBinding;
    ApplicationClass mAppClass;
    OutputConfigurationDao dao;
    int pageOffset = 0, currentPage = 0;
    WaterTreatmentDb waterTreatmentDb;
    OutputKeepAliveDao outputKeepAliveDao;
    AppEnum outpuType = AppEnum.RELAY_OUTPUT;

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

        mBinding.outputRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAppClass = (ApplicationClass) getActivity().getApplication();
        dao = ApplicationClass.outputDAO;
        waterTreatmentDb = WaterTreatmentDb.getDatabase(getContext());
        outputKeepAliveDao = waterTreatmentDb.outputKeepAliveDao();
        setOutputRvData(AppEnum.RELAY_OUTPUT);

        mBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                pageOffset = 0;
                if (radioGroup.getCheckedRadioButtonId() == mBinding.analogRb.getId()) {
                    outpuType = AppEnum.ANALOG_OUTPUT;
                    setOutputRvData(AppEnum.ANALOG_OUTPUT);
                    mBinding.leftArrowOsBtn.setVisibility(View.GONE);
                    mBinding.rightArrowOsBtn.setVisibility(View.GONE);
                } else {
                    outpuType = AppEnum.RELAY_OUTPUT;
                    setOutputRvData(AppEnum.RELAY_OUTPUT);
                    currentPage = 0;
                    mBinding.leftArrowOsBtn.setVisibility(View.GONE);
                    mBinding.rightArrowOsBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        mBinding.leftArrowOsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage--;
                //mBinding.outputRv.setAdapter(new OutputIndexRvAdapter(FragmentOutputSettings_Config.this, dao.getOutputConfigurationEntityList(9, pageOffset = pageOffset - 9)));

                mBinding.outputRv.setAdapter(new OutputIndexRvAdapter(FragmentOutputSettings_Config.this,
                        dao.getOutputHardWareNoConfigurationEntityList((outpuType == AppEnum.RELAY_OUTPUT ? 1 : 15), (outpuType == AppEnum.RELAY_OUTPUT ? 14 : 22), 9, pageOffset = pageOffset - 9),outputKeepAliveDao));

                mBinding.leftArrowOsBtn.setVisibility(View.GONE);
                mBinding.rightArrowOsBtn.setVisibility(View.VISIBLE);
            }
        });

        outputKeepAliveDao.getOutputLiveList().observe(getViewLifecycleOwner(), new Observer<List<OutputKeepAliveEntity>>() {
            @Override
            public void onChanged(List<OutputKeepAliveEntity> outputKeepAliveEntities) {
                mBinding.outputRv.getAdapter().notifyDataSetChanged();
            }
        });

        mBinding.rightArrowOsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage++;
                mBinding.leftArrowOsBtn.setVisibility(View.VISIBLE);
                // mBinding.outputRv.setAdapter(new OutputIndexRvAdapter(FragmentOutputSettings_Config.this, dao.getOutputConfigurationEntityList(9, pageOffset = pageOffset + 9)));

                mBinding.outputRv.setAdapter(new OutputIndexRvAdapter(FragmentOutputSettings_Config.this,
                        dao.getOutputHardWareNoConfigurationEntityList((outpuType == AppEnum.RELAY_OUTPUT ? 1 : 15), (outpuType == AppEnum.RELAY_OUTPUT ? 14 : 22), 9, pageOffset = pageOffset + 9), outputKeepAliveDao));

                mBinding.leftArrowOsBtn.setVisibility(View.VISIBLE);
                mBinding.rightArrowOsBtn.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("print",""+currentPage);
        if(mBinding.radioGroup.getCheckedRadioButtonId() == R.id.relayRb) {
            if (currentPage == 1) {
                mBinding.leftArrowOsBtn.setVisibility(View.VISIBLE);
                mBinding.rightArrowOsBtn.setVisibility(View.GONE);
            } else {
                mBinding.leftArrowOsBtn.setVisibility(View.GONE);
                mBinding.rightArrowOsBtn.setVisibility(View.VISIBLE);
            }
        }else{
            mBinding.leftArrowOsBtn.setVisibility(View.GONE);
            mBinding.rightArrowOsBtn.setVisibility(View.GONE);
        }
    }

    private void setOutputRvData(AppEnum outpuType) {
        mBinding.outputRv.setAdapter(new OutputIndexRvAdapter(this,
                dao.getOutputHardWareNoConfigurationEntityList((outpuType == AppEnum.RELAY_OUTPUT ? 1 : 15), (outpuType == AppEnum.RELAY_OUTPUT ? 14 : 22), 9, pageOffset), outputKeepAliveDao));
    }

    public static void hideToolbar() {
        mBinding.view8.setVisibility(View.GONE);
        mBinding.radioGroup.setVisibility(View.GONE);
    }

    @Override
    public void onClick(int sensorInputNo) {
        Bundle bundle = new Bundle();
        bundle.putInt("sensorInputNo", sensorInputNo);
        mAppClass.navigateToBundle(getActivity(), R.id.action_outputSetting_to_output, bundle);
    }

    @Override
    public void onClick(String sensorInputNo) {
    }

    @Override
    public void onClick(MainConfigurationEntity mainConfigurationEntity) {

    }

}
