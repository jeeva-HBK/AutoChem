package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.VirtualConfigurationEntity;

import java.util.List;

@Dao
public interface VirtualConfigurationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VirtualConfigurationEntity... virtualConfigurationEntities);

    @Query("select * FROM virtualconfigurationentity")
    List<VirtualConfigurationEntity> getVirtualConfigurationEntityList();

    @Query("select * FROM virtualconfigurationentity WHERE  hardwareNo BETWEEN :hardwareTo AND :hardwareNo")
    List<VirtualConfigurationEntity> getVirtualHardWareNoConfigurationEntityList(int hardwareTo, int hardwareNo);

    @Query("select inputType  FROM virtualconfigurationentity WHERE  hardwareNo = :hardwareNo ")
    String getInputType(int hardwareNo);

    @Query("select inputLabel FROM virtualconfigurationentity WHERE hardwareNo = :hardwareNo ")
    String getVirtualLabel(int hardwareNo);

    @Query("select subValueOne FROM virtualconfigurationentity WHERE hardwareNo = :hardwareNo ")
    String getVirtualLowAlarm(int hardwareNo);

    @Query("select subValueTwo FROM virtualconfigurationentity WHERE hardwareNo = :hardwareNo ")
    String getVirtualHighAlarm(int hardwareNo);

    @Query("select unit  FROM virtualconfigurationentity WHERE  hardwareNo = :hardwareNo ")
    String getUnit(int hardwareNo);

    @Query("select subValueOne  FROM virtualconfigurationentity WHERE  hardwareNo = :hardwareNo ")
    String getLowAlarm(int hardwareNo);

    @Query("select subValueTwo  FROM virtualconfigurationentity WHERE  hardwareNo = :hardwareNo ")
    String getHighAlarm(int hardwareNo);

    @Query("select writePacket from virtualconfigurationentity WHERE hardwareNo = :hardwareNo ")
    String getWritePacket(int hardwareNo);

    @Query("Delete from virtualconfigurationentity")
    void deleteVirtualDao();
}
