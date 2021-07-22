package com.ionexchange.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.InputsRvAdapter;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsettingsBinding;

import org.jetbrains.annotations.NotNull;

public class FragmentInputSettings extends Fragment implements RvOnClick {
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
        mBinding.inputsRv.setAdapter(new InputsRvAdapter(rvOnClick = this));
    }

    @Override
    public void onClick(String pos) {
        switch (pos) {
            case "pH":
                mBinding.inputsRv.setVisibility(View.GONE);
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorChild(pos)).commit();
                Log.e(TAG, "onClick: ");
                break;
        }
    }
}
