package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.AlarmLogEntity;

import java.util.List;

@Dao
public interface AlarmLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AlarmLogEntity... alarmLogEntities);

    @Query("select * FROM AlarmLogEntity")
    List<AlarmLogEntity> getAlarmLogList();

    @Query("SELECT sNo FROM AlarmLogEntity order by 1 desc limit 1")
    int getLastSno();

    @Query("select * FROM alarmlogentity WHERE  date BETWEEN :formDate AND :toDate")
    List<AlarmLogEntity> getDateWise(String formDate, String toDate);

    @Query("DELETE FROM alarmlogentity WHERE date = :Date")
    void deleteDateWise(String Date);

    @Query("select date FROM alarmlogentity WHERE date = :Date")
    List<String> getDeleteDate(String Date);
}