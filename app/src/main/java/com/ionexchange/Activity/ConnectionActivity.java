package com.ionexchange.Activity;

import static com.ionexchange.Others.ApplicationClass.editor;
import static com.ionexchange.Others.ApplicationClass.mIPAddress;
import static com.ionexchange.Others.ApplicationClass.mPortNumber;
import static com.ionexchange.Others.ApplicationClass.preferences;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.ActivityConnectionBinding;

public class ConnectionActivity extends AppCompatActivity {
    ActivityConnectionBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_connection);

        mAppClass = (ApplicationClass) getApplication();

        if (preferences.getBoolean("prefLoggedIn", false)) {
            proceedToBaseAct();
        }
        try {
            mBinding.ipAdressEdt.append(preferences.getString("prefIp", ""));
            mBinding.portEdt.append(preferences.getString("prefPort", ""));
        } catch (Exception e) {

        }

        mActivity = new BaseActivity();
        mBinding.button.setOnClickListener(View -> {
            if (validateField()) {
                editor.putBoolean("prefLoggedIn", true);
                editor.putString("prefIp", mBinding.ipAdressEdt.getText().toString());
                editor.putString("prefPort", mBinding.portEdt.getText().toString());
                editor.putString("prefPassword", mBinding.passwordEdt.getText().toString());
                editor.apply();
                mIPAddress = mBinding.ipAdressEdt.getText().toString();
                mPortNumber = Integer.parseInt(mBinding.portEdt.getText().toString());
                DEVICE_PASSWORD = mBinding.passwordEdt.getText().toString();
                proceedToBaseAct();
            }
        });
    }

    void proceedToBaseAct() {
        startActivity(new Intent(ConnectionActivity.this, BaseActivity.class));
    }

    private boolean validateField() {
        if (mBinding.ipAdressEdt.getText().toString().equals("")) {
            mBinding.ipAdressEdt.setError("field should not be empty !");
            return false;
        } else if (mBinding.portEdt.getText().toString().equals("")) {
            mBinding.portEdt.setError("field should not be empty !");
            return false;
        } else if (mBinding.passwordEdt.getText().toString().equals("")) {
            mBinding.passwordEdt.setError("field should not be empty !");
            return false;
        } /*else if (!ApplicationClass.isValidIp(mBinding.ipAdressEdt.getText().toString())) {
            mBinding.
        } else if (!ApplicationClass.isValidPort(mBinding.portEdt.getText().toString())) {

        }*/
        return true;
    }
}
