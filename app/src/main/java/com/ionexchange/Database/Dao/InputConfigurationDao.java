package com.ionexchange.Database.Dao;

import androidx.lifecycle.LiveData;
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

    @Query("select * FROM InputConfigurationEntity")
    LiveData<List<InputConfigurationEntity>> getInputLiveList();

    @Query("select * FROM inputConfigurationEntity")
    List<InputConfigurationEntity> getInputConfigurationEntityList();

    @Query("select * FROM inputConfigurationEntity WHERE flagKey = :flagKey LIMIT  :limit OFFSET :currentValue")
    List<InputConfigurationEntity> getInputConfigurationEntityFlagKeyList(int flagKey, int limit, int currentValue);

    @Query("select * FROM inputConfigurationEntity WHERE flagKey = :flagKey ")
    List<InputConfigurationEntity> getInputConfigurationEntityFlagKeyList(int flagKey);

    @Query("select * FROM inputConfigurationEntity WHERE  hardwareNo BETWEEN :hardwareTo AND :hardwareNo")
    List<InputConfigurationEntity> getInputHardWareNoConfigurationEntityList(int hardwareTo, int hardwareNo);

    @Query("select * FROM inputConfigurationEntity WHERE  sensorType = :sensorType")
    List<InputConfigurationEntity> getSensorTypeConfigurationEntityList(String sensorType);

    @Query("select * FROM inputConfigurationEntity WHERE  signalType = :signalType")
    List<InputConfigurationEntity> getAnalogInputHardWareNoConfigurationEntityList(int signalType);

    @Query("select subValueOne FROM inputConfigurationEntity WHERE hardwareNo = :hardwareNo ")
    String getLowAlarm(int hardwareNo);

    @Query("select subValueTwo FROM inputConfigurationEntity WHERE hardwareNo = :hardwareNo ")
    String getHighAlarm(int hardwareNo);

    @Query("select inputLabel FROM inputConfigurationEntity WHERE hardwareNo = :hardwareNo ")
    String getInputLabel(int hardwareNo);

    @Query("select inputType  FROM inputConfigurationEntity WHERE  hardwareNo = :hardwareNo ")
    String getInputType(int hardwareNo);

    @Query("select type  FROM inputConfigurationEntity WHERE  hardwareNo = :hardwareNo ")
    String getType(int hardwareNo);

    @Query("select unit  FROM inputConfigurationEntity WHERE  hardwareNo = :hardwareNo ")
    String getUnit(int hardwareNo);

    @Query("select sensorType  FROM inputConfigurationEntity WHERE  hardwareNo = :hardwareNo ")
    String getSensorType(int hardwareNo);

    @Query("select inputSequenceNumber from inputconfigurationentity WHERE hardwareNo = :hardwareNo")
    int getSeqNumber(int hardwareNo);

    @Query("select writePacket from inputconfigurationentity WHERE hardwareNo = :hardwareNo ")
    String getWritePacket(int hardwareNo);

    @Query("select * from inputconfigurationentity WHERE flagKey = 1")
    List<InputConfigurationEntity> getConfigSensor();

    @Query("select hardwareNo || ' - ' || inputType FROM inputConfigurationEntity where flagKey =1 and hardwareNo < 34 " +
            "UNION  ALL select hardwareNo || ' - ' || virtualType from VirtualConfigurationEntity  ")
    String[] getEnabledSensor();

    @Query("Delete from inputconfigurationentity")
    void deleteInputDao();

    @Query("select flagKey from inputconfigurationentity where hardwareNo = :hNo")
    int getFlag(String hNo);

}
