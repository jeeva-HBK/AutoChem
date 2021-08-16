package com.ionexchange.Fragments.Configuration.HomeScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Interface.DialogDismissListener;
import com.ionexchange.Others.DialogFrag;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSetlayoutBinding;

import org.jetbrains.annotations.NotNull;

public class FragmentSetLayout extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    FragmentSetlayoutBinding mBinding;
    String sensorInput;

    public FragmentSetLayout(String sensorInputNo) {
        this.sensorInput = sensorInputNo;
    }

    public FragmentSetLayout() {
        this.sensorInput = null;
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

        mBinding.radioLyOne.setChecked(true);

        if (sensorInput != null) {
            mBinding.layout1View1Txt.setText(sensorInput);
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
        DialogFrag fragment = new DialogFrag();
        FragmentSelectSensors fragmentSetLayout = new FragmentSelectSensors(fragment);

        fragment.init(fragmentSetLayout, "Commissioning", new DialogDismissListener() {
            @Override
            public void OnDismiss() {

            }
        });
        fragment.show(getChildFragmentManager(), null);
    }
}
