package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.InputConfigurationEntity;

import java.util.List;

@Dao
public interface InputConfigurationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(InputConfigurationEntity... inputConfigurationEntities);

    @Query("select * FROM inputConfigurationEntity")
    List<InputConfigurationEntity> getInputConfigurationEntityList();

    @Query("select * FROM inputConfigurationEntity WHERE flagKey = :flagKey LIMIT  :limit OFFSET :currentValue")
    List<InputConfigurationEntity> getInputConfigurationEntityFlagKeyList(int flagKey, int limit, int currentValue);

    @Query("select * FROM inputConfigurationEntity WHERE flagKey = :flagKey ")
    List<InputConfigurationEntity> getInputConfigurationEntityFlagKeyList(int flagKey);

    @Query("select * FROM inputConfigurationEntity WHERE  hardwareNo BETWEEN :hardwareTo AND :hardwareNo")
    List<InputConfigurationEntity> getInputHardWareNoConfigurationEntityList(int hardwareTo, int hardwareNo);

    @Query("select subValueOne  FROM inputConfigurationEntity WHERE  hardwareNo = :hardwareNo ")
    String getLowAlarm(int hardwareNo);

    @Query("select subValueTwo  FROM inputConfigurationEntity WHERE  hardwareNo = :hardwareNo ")
    String getHighAlarm(int hardwareNo);

}
