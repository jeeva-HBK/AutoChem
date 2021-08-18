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
}
