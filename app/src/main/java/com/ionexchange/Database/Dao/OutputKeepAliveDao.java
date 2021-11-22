package com.ionexchange.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.ionexchange.Database.Entity.KeepAliveCurrentEntity;
import com.ionexchange.Database.Entity.OutputKeepAliveEntity;

import java.util.List;

@Dao
public interface OutputKeepAliveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OutputKeepAliveEntity... outputKeepAliveEntities);

    @Query("select * FROM OutputKeepAliveEntity")
    LiveData<List<OutputKeepAliveEntity>> getOutputLiveList();

    @Query("Update OutputKeepAliveEntity SET outputStatus = :cValue WHERE hardwareNo = :hardwareNo")
    void updateOutputStatus(int hardwareNo, String cValue);

    @Query("Update OutputKeepAliveEntity SET outputStatusType = :cValue WHERE hardwareNo = :hardwareNo")
    void updateOutputRelayStatus(int hardwareNo, String cValue);

    @Query("select * FROM OutputKeepAliveEntity")
    List<OutputKeepAliveEntity> getOutputList();

    @Query("select outputStatus  FROM OutputKeepAliveEntity WHERE  hardwareNo = :hardwareNo ")
    String getOutputStatus(int hardwareNo);

    @Query("select outputStatusType  FROM OutputKeepAliveEntity WHERE  hardwareNo = :hardwareNo ")
    String getOutputRelayStatus(int hardwareNo);

}
