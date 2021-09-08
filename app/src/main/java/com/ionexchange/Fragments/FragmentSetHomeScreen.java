package com.ionexchange.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Database.Dao.DefaultLayoutConfigurationDao;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Entity.DefaultLayoutConfigurationEntity;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSethomescreenBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.macAddress;

public class FragmentSetHomeScreen extends Fragment{
    FragmentSethomescreenBinding mBinding;
    WaterTreatmentDb dB;
    DefaultLayoutConfigurationDao dao;
    List<DefaultLayoutConfigurationEntity> getEnabledLayout;
    int enableScreenNo = 0,updateDefaultScreen = 0,findLayoutClick = 0;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sethomescreen, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dB = WaterTreatmentDb.getDatabase(getContext());
        dao = dB.defaultLayoutConfigurationDao();
        if (dao.getDefaultLayoutConfigurationEntityList().isEmpty()) {
            for (int i = 1; i < 6; i++) {
                DefaultLayoutConfigurationEntity entityUpdate = new DefaultLayoutConfigurationEntity
                        (i, i, 0, macAddress,1);
                List<DefaultLayoutConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                insertToDb(entryListUpdate);
            }
                mBinding.setLayoutBtn.setText("Set Layout 1");
                mBinding.layout1RB.setChecked(true);
                dao.update(1, 1);
                mBinding.setDefaultLayout.setChecked(true);
        }else{
            getEnabledLayout = dao.getEnableDefaultScreen(1);
            if(getEnabledLayout.size() == 0) {
                mBinding.setLayoutBtn.setText("Set Layout 1");
                mBinding.layout1RB.setChecked(true);
                dao.update(1, 1);
                mBinding.setDefaultLayout.setChecked(true);
            }else {
                updateLayout();
            }
        }
        mBinding.setDefaultLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateDefaultScreen = mBinding.layoutRadioGroup.indexOfChild(getActivity().findViewById(mBinding.layoutRadioGroup.getCheckedRadioButtonId()));
                    updateDefaultScreen = updateDefaultScreen + 1;
                    Log.e("default", updateDefaultScreen + "");
                    if(updateDefaultScreen > 0){
                        if(findLayoutClick == 0) {
                            WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
                            DefaultLayoutConfigurationDao dao = db.defaultLayoutConfigurationDao();
                            getEnabledLayout = dao.getEnableDefaultScreen(1);
                            if(getEnabledLayout.size() > 0){
                                dao.update(0,getEnabledLayout.get(0).screenNo);
                            }
                            dao.update( isChecked ? 1 : 0,updateDefaultScreen);
                            updateLayout();
                        }else{
                            findLayoutClick = 0;
                        }
                    }
           }
        });
        mBinding.layoutRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                updateEnableBox();
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.layout1RB:
                        mBinding.setLayoutBtn.setText("Set Layout 1");
                        break;
                    case R.id.layout2RB:
                        mBinding.setLayoutBtn.setText("Set Layout 2");
                        //updateDefaultScreen = 2;
                        break;

                    case R.id.layout3RB:
                        mBinding.setLayoutBtn.setText("Set Layout 3");
                        //updateDefaultScreen = 3;
                        break;

                    case R.id.layout4RB:
                        mBinding.setLayoutBtn.setText("Set Layout 4");
                        //updateDefaultScreen = 4;
                        break;

                    case R.id.layout5RB:
                        mBinding.setLayoutBtn.setText("Set Layout 5");
                        //updateDefaultScreen = 5;
                        break;
                }
            }
        });


        mBinding.setLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.setHomeScreenHost, new FragmentSetLayout(updateDefaultScreen)).commit();
            }
        });

    }
    public void insertToDb(List<DefaultLayoutConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        DefaultLayoutConfigurationDao dao = db.defaultLayoutConfigurationDao();
        dao.insert(entryList.toArray(new DefaultLayoutConfigurationEntity[0]));
    }

    private void updateEnableBox(){
        getEnabledLayout = dao.getEnableDefaultScreen(1);
        if(getEnabledLayout.size() != 0) {
            enableScreenNo = getEnabledLayout.get(0).screenNo;
            updateDefaultScreen = mBinding.layoutRadioGroup.indexOfChild(getActivity().findViewById(mBinding.layoutRadioGroup.getCheckedRadioButtonId()));
            updateDefaultScreen = updateDefaultScreen + 1;
            findLayoutClick = 1;
            mBinding.setDefaultLayout.setChecked(enableScreenNo == updateDefaultScreen);
        }
    }
    private void updateLayout(){
        getEnabledLayout = dao.getEnableDefaultScreen(1);
        if(getEnabledLayout.size() != 0){
            enableScreenNo =  getEnabledLayout.get(0).screenNo;
            updateDefaultScreen = enableScreenNo;
            mBinding.setDefaultLayout.setChecked(true);
            switch (enableScreenNo) {
                case 1:
                    mBinding.layout1RB.setChecked(true);
                    break;

                case 2:
                    mBinding.layout2RB.setChecked(true);
                    break;

                case 3:
                    mBinding.layout3RB.setChecked(true);
                    break;

                case 4:
                    mBinding.layout4RB.setChecked(true);
                    break;

                case 5:
                    mBinding.layout5RB.setChecked(true);
                    break;
            }
        }
    }
}
