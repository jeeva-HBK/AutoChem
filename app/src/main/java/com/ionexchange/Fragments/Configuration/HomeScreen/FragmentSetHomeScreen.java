package com.ionexchange.Fragments.Configuration.HomeScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSethomescreenBinding;

import org.jetbrains.annotations.NotNull;

public class FragmentSetHomeScreen extends Fragment {
    FragmentSethomescreenBinding mBinding;

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

        mBinding.layoutRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.layout1RB:
                        mBinding.setLayoutBtn.setText("Set Layout 1");
                        break;

                    case R.id.layout2RB:
                        mBinding.setLayoutBtn.setText("Set Layout 2");
                        break;

                    case R.id.layout3RB:
                        mBinding.setLayoutBtn.setText("Set Layout 3");
                        break;

                    case R.id.layout4RB:
                        mBinding.setLayoutBtn.setText("Set Layout 4");
                        break;

                    case R.id.layout5RB:
                        mBinding.setLayoutBtn.setText("Set Layout 5");
                        break;
                }
            }
        });


        mBinding.setLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.setHomeScreenHost, new FragmentSetLayout()).commit();
            }
        });

    }
}
