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



    @Query("select * FROM alarmlogentity WHERE  alarmLog =:type AND date BETWEEN :formDate AND :toDate")
    List<AlarmLogEntity> getDateWiseAndType(String formDate, String toDate,String type);



    @Query("select date FROM alarmlogentity")
    List<String> getDateList();


    @Query("Delete FROM alarmlogentity WHERE sNo in (SELECT sNo FROM AlarmLogEntity limit 1)")
    void deleteFirstRow();

}
