package com.ionexchange.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ionexchange.Fragments.FragmentMainHost;
import com.ionexchange.R;
import com.ionexchange.databinding.ActivityBaseBinding;
import com.ionexchange.databinding.ToolbarBinding;

public class BaseActivity extends AppCompatActivity {
    ActivityBaseBinding mBinding;
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        getSupportFragmentManager().beginTransaction().replace(mBinding.baseFrameLayout.getId(), new FragmentMainHost()).commit();
    }

    public void changeActionBarText(String titile) {
        mBinding.toolbarText.setText(titile);
    }

    public void changeToolBarVisib(int visibility) {
        mBinding.toolBar.setVisibility(visibility);
    }

}