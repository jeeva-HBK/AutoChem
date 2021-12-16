package com.ionexchange.Fragments.Configuration.HomeScreen;

import static com.ionexchange.Others.ApplicationClass.macAddress;

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
import com.ionexchange.Database.Entity.DefaultLayoutConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSethomescreenBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentSetHomeScreen extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    FragmentSethomescreenBinding mBinding;
    ApplicationClass mAppClass;
    WaterTreatmentDb dB;
    DefaultLayoutConfigurationDao dao;
    int enableScreenNo = 0, screenNo = 0;

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
        mAppClass = (ApplicationClass) getActivity().getApplication();
        dao = dB.defaultLayoutConfigurationDao();
        screenNo = 1;
        setDefaultDb();
        enabledScreen();
        mBinding.layoutRadioGroup.setOnCheckedChangeListener(this);
        mBinding.setLayoutBtn.setOnClickListener(this);
        mBinding.setDefaultLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    switch (screenNo) {
                        case 1:
                            dao.update(1, 1);
                            dao.update(0, 2);
                            dao.update(0, 3);
                            dao.update(0, 4);
                            dao.update(0, 5);
                            break;
                        case 2:
                            dao.update(0, 1);
                            dao.update(1, 2);
                            dao.update(0, 3);
                            dao.update(0, 4);
                            dao.update(0, 5);
                            break;
                        case 3:
                            dao.update(0, 1);
                            dao.update(0, 2);
                            dao.update(1, 3);
                            dao.update(0, 4);
                            dao.update(0, 5);
                            break;
                        case 4:
                            dao.update(0, 1);
                            dao.update(0, 2);
                            dao.update(0, 3);
                            dao.update(1, 4);
                            dao.update(0, 5);
                            break;
                        case 5:
                            dao.update(0, 1);
                            dao.update(0, 2);
                            dao.update(0, 3);
                            dao.update(0, 4);
                            dao.update(1, 5);
                            break;
                    }
                }
            }
        });

    }

    public void insertToDb(List<DefaultLayoutConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        DefaultLayoutConfigurationDao dao = db.defaultLayoutConfigurationDao();
        dao.insert(entryList.toArray(new DefaultLayoutConfigurationEntity[0]));
    }


    void enabledScreen() {
        enableScreenNo = dao.getEnableDefaultLayout(1).get(0).getScreenNo();
        switch (enableScreenNo) {
            case 1:
                mBinding.layout1RB.setChecked(true);
                mBinding.setDefaultLayout.setChecked(true);
                screenNo = 1;
                break;
            case 2:
                mBinding.layout2RB.setChecked(true);
                mBinding.setDefaultLayout.setChecked(true);
                screenNo = 2;
                break;
            case 3:
                mBinding.layout3RB.setChecked(true);
                mBinding.setDefaultLayout.setChecked(true);
                screenNo = 3;
                break;
            case 4:
                mBinding.layout4RB.setChecked(true);
                mBinding.setDefaultLayout.setChecked(true);
                screenNo = 4;
                break;
            case 5:
                mBinding.layout5RB.setChecked(true);
                mBinding.setDefaultLayout.setChecked(true);
                screenNo = 5;
                break;

        }
    }

    void setDefaultDb() {
        if (dao.getDefaultLayoutConfigurationEntityList().isEmpty()) {
            for (int i = 1; i < 6; i++) {
                DefaultLayoutConfigurationEntity entityUpdate = new DefaultLayoutConfigurationEntity
                        (i, i, 0, macAddress, 1);
                List<DefaultLayoutConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                insertToDb(entryListUpdate);
            }
            mBinding.setLayoutBtn.setText("Set Layout 1");
            mBinding.layout1RB.setChecked(true);
            dao.update(1, 1);
            mBinding.setDefaultLayout.setChecked(true);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getCheckedRadioButtonId()) {
            case R.id.layout1RB:
                screenNo = 1;
                mBinding.setLayoutBtn.setText("Set Layout 1");
                mBinding.setDefaultLayout.setChecked(dao.enableScreen(1) == 1);
                break;
            case R.id.layout2RB:
                screenNo = 2;
                mBinding.setLayoutBtn.setText("Set Layout 2");
                mBinding.setDefaultLayout.setChecked(dao.enableScreen(2) == 1);
                break;

            case R.id.layout3RB:
                screenNo = 3;
                mBinding.setLayoutBtn.setText("Set Layout 3");
                mBinding.setDefaultLayout.setChecked(dao.enableScreen(3) == 1);
                break;

            case R.id.layout4RB:
                screenNo = 4;
                mBinding.setLayoutBtn.setText("Set Layout 4");
                mBinding.setDefaultLayout.setChecked(dao.enableScreen(4) == 1);
                break;

            case R.id.layout5RB:
                screenNo = 5;
                mBinding.setLayoutBtn.setText("Set Layout 5");
                mBinding.setDefaultLayout.setChecked(dao.enableScreen(5) == 1);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        mAppClass.navigateToBundle(getActivity(), R.id.action_homeScreen_to_setlayout, putBundle(screenNo));
    }

    Bundle putBundle(int screenNo) {
        Bundle bundle = new Bundle();
        bundle.putInt("screenNo", screenNo);
        Log.e("TAG", "putBundle: "+screenNo );
        return bundle;
    }
}
