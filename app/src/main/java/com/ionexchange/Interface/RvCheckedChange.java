package com.ionexchange.Interface;

import android.widget.CompoundButton;

import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;

public interface RvCheckedChange {

    void onCheckChanged(Object mObj, CompoundButton compoundButton,int mode);
}
