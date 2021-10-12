package com.ionexchange.Fragments.Configuration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentConnectionBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.ApplicationClass.editor;

public class FragmentConnection extends Fragment {

    FragmentConnectionBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_connection, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();

        mBinding.button.setOnClickListener(View -> {
        /*  if (!mBinding.ipAdressEdt.getText().toString().trim().equals("") && !mBinding.portEdt.getText().toString().equals("")) {
                if (ApplicationClass.isValidIp(mBinding.ipAdressEdt.getText().toString()) && ApplicationClass.isValidPort(mBinding.portEdt.getText().toString())) {
                    ApplicationClass.mIPAddress = mBinding.ipAdressEdt.getText().toString();
                    ApplicationClass.mPortNumber = Integer.parseInt(mBinding.portEdt.getText().toString());
                } else {

                }
            } else {

            }*/
            if (validateField()) {
                editor.putBoolean("prefLoggedIn", true);
                editor.putString("prefIp", mBinding.ipAdressEdt.getText().toString());
                editor.putString("prefPort", mBinding.portEdt.getText().toString());
                editor.apply();
                mActivity.setNavigation(R.navigation.navigation, R.id.Dashboard);
            }
        });
    }

    private boolean validateField() {
        if (mBinding.ipAdressEdt.getText().toString().equals("")) {
            mBinding.ipAdressEdt.setError("field should not be empty !");
            return false;
        } else if (mBinding.portEdt.getText().toString().equals("")) {
            mBinding.portEdt.setError("field should not be empty !");
            return false;
        } /*else if (!ApplicationClass.isValidIp(mBinding.ipAdressEdt.getText().toString())) {
            mBinding.
        } else if (!ApplicationClass.isValidPort(mBinding.portEdt.getText().toString())) {

        }*/
        return true;
    }

}
