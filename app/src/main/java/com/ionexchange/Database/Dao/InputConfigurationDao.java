package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.ionexchange.Database.Converters;
import com.ionexchange.Database.Entity.InputConfigurationEntity;

import java.util.List;

@Dao
public interface InputConfigurationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(InputConfigurationEntity... inputConfigurationEntities);

    @Query("select * FROM inputConfigurationEntity")
    List<InputConfigurationEntity> getInputConfigurationEntityList();

    @Query("select * FROM inputConfigurationEntity WHERE flagKey = :flagKey")
    List<InputConfigurationEntity> getInputConfigurationEntityFlagKeyList(int flagKey);


   /* @Query("SELECT hardwareNo FROM inputConfigurationEntity WHERE hardwareNo = :hardwareNo")
    String[] getAllHardwareNo(int hardwareNo);*/
}
