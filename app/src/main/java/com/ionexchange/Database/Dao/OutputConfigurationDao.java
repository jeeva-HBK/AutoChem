package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.OutputConfigurationEntity;

import java.util.List;

@Dao
public interface OutputConfigurationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OutputConfigurationEntity... outputConfigurationEntities);

    @Query("select * FROM outputConfigurationEntity")
    List<OutputConfigurationEntity> getOutputConfigurationEntityList();

    @Query("select * FROM outputConfigurationEntity LIMIT  :limit OFFSET :offset")
    List<OutputConfigurationEntity> getOutputConfigurationEntityList(int limit, int offset);

    @Query("select  outputLabel FROM outputConfigurationEntity  WHERE outputNumber = :hardwareNo ")
    String getOutputLabel(int hardwareNo);

    @Query("select * FROM outputConfigurationEntity  WHERE outputNumber BETWEEN :hardwareTo AND :hardwareNo")
    List<OutputConfigurationEntity> getOutputHardWareNoConfigurationEntityList(int hardwareTo, int hardwareNo);

    @Query("select outputMode FROM outputConfigurationEntity  WHERE outputNumber = :hardwareNo ")
    String getOutputMode(int hardwareNo);

    @Query("select outputStatus FROM outputConfigurationEntity  WHERE outputNumber = :hardwareNo ")
    String getOutputStatus(int hardwareNo);

    @Query("select outputType FROM outputConfigurationEntity  WHERE outputNumber = :hardwareNo ")
    String getOutputName(int hardwareNo);

    @Query("select writePacket from outputConfigurationEntity WHERE outputNumber = :hardwareNo ")
    String getWritePacket(int hardwareNo);

    @Query("select * FROM outputConfigurationEntity  WHERE outputNumber BETWEEN :fromHardwareNo AND :toHardwareNo LIMIT  :limit OFFSET :offset")
    List<OutputConfigurationEntity> getOutputHardWareNoConfigurationEntityList(int fromHardwareNo, int toHardwareNo, int limit, int offset);

    @Query("Delete from outputconfigurationentity")
    void deleteOutputDao();
}
