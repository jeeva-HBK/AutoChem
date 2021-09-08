package com.ionexchange.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Database.Dao.DefaultLayoutConfigurationDao;
import com.ionexchange.Database.Dao.MainConfigurationDao;
import com.ionexchange.Database.Entity.DefaultLayoutConfigurationEntity;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DialogDismissListener;
import com.ionexchange.Others.DialogFrag;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSetlayoutBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FragmentSetLayout extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    FragmentSetlayoutBinding mBinding;
    int screenNo;
    WaterTreatmentDb dB;
    DefaultLayoutConfigurationDao dao;
    public FragmentSetLayout(int screenNo) {
        this.screenNo = screenNo;
    }


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setlayout, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dB = WaterTreatmentDb.getDatabase(getContext());
        dao = dB.defaultLayoutConfigurationDao();
        List<DefaultLayoutConfigurationEntity> layoutlist =  dao.getEnableDefaultLayout(screenNo);
        if(layoutlist.size() > 0){
            switch (layoutlist.get(0).getDefault_layout_no()){
                case 1:
                    mBinding.radioLyOne.setChecked(true);
                    mBinding.setSelected("layout1");
                    break;
                case 2:
                    mBinding.radioLyTwo.setChecked(true);
                    mBinding.setSelected("layout2");
                    break;
                case 3:
                    mBinding.radioLyThree.setChecked(true);
                    mBinding.setSelected("layout3");
                    break;
                case 4:
                    mBinding.radioLyFour.setChecked(true);
                    mBinding.setSelected("layout4");
                    break;
                case 5:
                    mBinding.radioLyFive.setChecked(true);
                    mBinding.setSelected("layout5");
                    break;
                case 6:
                    mBinding.radioLySix.setChecked(true);
                    mBinding.setSelected("layout6");
                    break;
            }
        } else {
            mBinding.radioLyOne.setChecked(true);
            mBinding.setSelected("layout1");
        }
        mBinding.radioLyOne.setOnCheckedChangeListener(this);
        mBinding.radioLyTwo.setOnCheckedChangeListener(this);
        mBinding.radioLyThree.setOnCheckedChangeListener(this);
        mBinding.radioLyFour.setOnCheckedChangeListener(this);
        mBinding.radioLyFive.setOnCheckedChangeListener(this);
        mBinding.radioLySix.setOnCheckedChangeListener(this);

        mBinding.layout1View1.setOnClickListener(this);
        mBinding.layout2View1.setOnClickListener(this);
        mBinding.layout2View2.setOnClickListener(this);

        mBinding.layout3View1.setOnClickListener(this);
        mBinding.layout3View2.setOnClickListener(this);
        mBinding.layout3View3.setOnClickListener(this);

        mBinding.layout4View1.setOnClickListener(this);
        mBinding.layout4View2.setOnClickListener(this);
        mBinding.layout4Layout3.setOnClickListener(this);

        mBinding.layout5View1.setOnClickListener(this);
        mBinding.layout5View2.setOnClickListener(this);
        mBinding.layout5View3.setOnClickListener(this);
        mBinding.layout5View4.setOnClickListener(this);

        mBinding.layout6View1.setOnClickListener(this);
        mBinding.layout6View2.setOnClickListener(this);
        mBinding.layout6View3.setOnClickListener(this);
        mBinding.layout6View4.setOnClickListener(this);
        mBinding.layout6View5.setOnClickListener(this);
        mBinding.layout6View6.setOnClickListener(this);

        mBinding.saveLayoutInputSettings.setOnClickListener(this::save);
        mBinding.saveFabInputSettings.setOnClickListener(this::save);
    }
    private void save(View view) {
       int updateDefaultLayout = mBinding.radioGroup.indexOfChild(getActivity().findViewById(mBinding.radioGroup.getCheckedRadioButtonId()));
        updateDefaultLayout = updateDefaultLayout + 1;
           if(updateDefaultLayout > 0){
              dao.updateLayout(updateDefaultLayout,screenNo);
           }
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            switch (compoundButton.getId()) {
                case R.id.radio_ly_one:
                    mBinding.setSelected("layout1");
                    break;
                case R.id.radio_ly_two:
                    mBinding.setSelected("layout2");
                    break;
                case R.id.radio_ly_three:
                    mBinding.setSelected("layout3");
                    break;
                case R.id.radio_ly_four:
                    mBinding.setSelected("layout4");
                    break;
                case R.id.radio_ly_five:
                    mBinding.setSelected("layout5");
                    break;
                case R.id.radio_ly_six:
                    mBinding.setSelected("layout6");
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        dB = WaterTreatmentDb.getDatabase(getContext());
        MainConfigurationDao dao = dB.mainConfigurationDao();

        DialogFrag fragment = new DialogFrag();
        int updateDefaultLayout = mBinding.radioGroup.indexOfChild(getActivity().findViewById(mBinding.radioGroup.getCheckedRadioButtonId()));
        updateDefaultLayout = updateDefaultLayout + 1;
        String windowNo = view.getTag().toString();
        windowNo = windowNo.substring(windowNo.length() - 1);
        Log.e("window", windowNo + " ="+ windowNo.substring(windowNo.length() - 1));
        List<MainConfigurationEntity> sensorlist = dao.getSensorList(screenNo,updateDefaultLayout,Integer.parseInt(windowNo));

       /* FragmentSelectSensors fragmentSetLayout = new FragmentSelectSensors(fragment,screenNo,updateDefaultLayout,Integer.parseInt(windowNo));
        fragment.init(fragmentSetLayout, "Layout "+updateDefaultLayout+" - Window "+ windowNo, new DialogDismissListener() {
            @Override
            public void OnDismiss() {

            }
        });
        fragment.show(getChildFragmentManager(), null);*/
    }
}
