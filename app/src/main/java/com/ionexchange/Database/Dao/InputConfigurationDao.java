package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.ionexchange.Database.Entity.InputConfigurationEntity;

import java.util.List;

@Dao
public interface InputConfigurationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(InputConfigurationEntity... inputConfigurationEntities);


}
