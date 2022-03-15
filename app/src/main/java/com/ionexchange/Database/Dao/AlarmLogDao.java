package com.ionexchange.Database.Dao;

import androidx.lifecycle.LiveData;
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

    @Query("select * FROM AlarmLogEntity order by sNo desc")
    List<AlarmLogEntity> getAlarmLogList();

    @Query("SELECT sNo FROM AlarmLogEntity order by 1 desc limit 1")
    int getLastSno();

    @Query("select * FROM alarmlogentity WHERE  date BETWEEN :formDate AND :toDate order by sNo desc")
    List<AlarmLogEntity> getDateWise(String formDate, String toDate);

    @Query("Update alarmlogentity SET lockOutAlarm = :cValue WHERE hardwareNo = :hardWareNo AND sensorType = :sensorType")
    void updateLockAlarm(String hardWareNo, String cValue, String sensorType);

    @Query("select * FROM alarmlogentity WHERE  alarmLog =:type AND date BETWEEN :formDate AND :toDate order by sNo desc")
    List<AlarmLogEntity> getDateWiseAndType(String formDate, String toDate,String type);

    @Query("select * FROM alarmlogentity WHERE  alarmLog =:type order by sNo desc")
    List<AlarmLogEntity> getTypeWise(String type);

    @Query("select * FROM alarmlogentity WHERE  alarmLog =:alarmLog AND  lockOutAlarm =:lockOutAlarm")
    List<AlarmLogEntity> getLockAlarmSize(String alarmLog, String lockOutAlarm);

    @Query("select date FROM alarmlogentity order by sNo desc")
    List<String> getDateList();

    @Query("Delete FROM alarmlogentity WHERE sNo in (SELECT sNo FROM AlarmLogEntity limit 1)")
    void deleteFirstRow();

    @Query("select * FROM AlarmLogEntity WHERE lockOutAlarm = 1")
    LiveData<List<AlarmLogEntity>> getAlarmLiveList();

    @Query("select * FROM AlarmLogEntity order by sNo desc")
    LiveData<List<AlarmLogEntity>> getAlarmList();

}
