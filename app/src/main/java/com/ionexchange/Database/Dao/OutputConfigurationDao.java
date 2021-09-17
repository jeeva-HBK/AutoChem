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
}
