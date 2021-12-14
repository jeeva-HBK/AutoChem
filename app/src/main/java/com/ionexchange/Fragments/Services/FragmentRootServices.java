package com.ionexchange.Fragments.Services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentRootServicesBinding;

public class FragmentRootServices extends Fragment implements RadioGroup.OnCheckedChangeListener {
    FragmentRootServicesBinding mBinding;
    ApplicationClass mAppClass;
    private static final String TAG = "FragmentRoot_EventLogs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_root_services, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();

        mBinding.serviceRadioGroup.setOnCheckedChangeListener(this);

        mBinding.serviceLogsRb.performClick();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.service_diagnosticsRb:
                mAppClass.castFrag(getParentFragmentManager(), R.id.service_hostFrame, new FragmentDiagnosticsData());
                break;

            case R.id.service_logsRb:
                mAppClass.castFrag(getParentFragmentManager(), R.id.service_hostFrame, new FragmentLogs());
                break;

            case R.id.service_outputControlRb:
                mAppClass.castFrag(getParentFragmentManager(), R.id.service_hostFrame, new FragmentOutputControl());
                break;
        }
    }
}
