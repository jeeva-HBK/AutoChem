package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.CalibrationEntity;

@Dao
public interface CalibrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CalibrationEntity... calibrationEntities);

    @Query("select * from CalibrationEntity where inputNo = :inputNo")
    CalibrationEntity getLastCalibrationData(int inputNo);
}
