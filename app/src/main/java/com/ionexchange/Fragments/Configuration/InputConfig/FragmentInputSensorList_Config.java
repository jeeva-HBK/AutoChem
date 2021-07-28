package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.InputsIndexRvAdapter;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsettingsBinding;


import org.jetbrains.annotations.NotNull;

public class FragmentInputSensorList_Config extends Fragment implements RvOnClick {
    FragmentInputsettingsBinding mBinding;
    RvOnClick rvOnClick;
    private static final String TAG = "FragmentInputSettings";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsettings, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.inputsRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mBinding.inputsRv.setAdapter(new InputsIndexRvAdapter(rvOnClick = this));
    }

    @Override
    public void onClick(String inputNumber) {
        mBinding.inputsRv.setVisibility(View.GONE);
        switch (inputNumber) {

            case "0":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorPh_Config(inputNumber)).commit();
                break;
            case "1":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorORP_Config()).commit();
                break;
            case "2":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorConductivity_Config()).commit();
                break;
            case "3":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorToroidalConductivity_config()).commit();
                break;
            case "4":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorTemp_config()).commit();
                break;
            case "5":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorFlow_config()).commit();
                break;

        }
    }
}
