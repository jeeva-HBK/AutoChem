package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.ionexchange.Database.Entity.OutputConfigurationEntity;

@Dao
public interface OutputConfigurationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OutputConfigurationEntity... outputConfigurationEntities);
}
