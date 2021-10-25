package com.ionexchange.Interface;

import com.ionexchange.Database.Entity.MainConfigurationEntity;

public interface RvOnClick {
    void onClick(int sensorInputNo);
    void onClick(String sensorInputNo);

    // For Calibration
    // void onClick(String sensorInputNo,String type,int position);
    void onClick(MainConfigurationEntity mainConfigurationEntity);
}

