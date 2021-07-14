package com.ionexchange.Activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ionexchange.Fragments.FragmentMainHost;
import com.ionexchange.R;
import com.ionexchange.databinding.ActivityBaseBinding;

public class BaseActivity extends AppCompatActivity implements Application.ActivityLifecycleCallbacks {
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

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated: ");
    }
    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.e(TAG, "onActivityStarted: ");
    }
    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.e(TAG, "onActivityResumed: ");
    }
    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.e(TAG, "onActivityPaused: ");
    }
    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.e(TAG, "onActivityStopped: ");
    }
    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Log.e(TAG, "onActivitySaveInstanceState: ");
    }
    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.e(TAG, "onActivityDestroyed: ");
    }

}