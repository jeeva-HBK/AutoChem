package com.ionexchange.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.Entity.EventLogEntity;

import java.util.List;

@Dao
public interface EventLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EventLogEntity... eventLogEntities);

    @Query("select * FROM EventLogEntity")
    List<EventLogEntity> getEventLogList();

    @Query("SELECT sNo FROM EventLogEntity order by 1 desc limit 1")
    int getLastSno();

    @Query("select * FROM EventLogEntity")
    LiveData<List<EventLogEntity>> getEventLiveList();

    @Query("select * FROM EventLogEntity WHERE  date BETWEEN :formDate AND :toDate")
    List<EventLogEntity> getDateWise(String formDate, String toDate);

    @Query("select * FROM EventLogEntity WHERE  EventLog =:type AND date BETWEEN :formDate AND :toDate ")
    List<EventLogEntity> getDateWiseAndType(String formDate, String toDate,String type);


    @Query("select date FROM EventLogEntity")
    List<String> getDateList();

    @Query("Delete FROM EventLogEntity WHERE sNo in (SELECT sNo FROM EventLogEntity limit 1)")
    void deleteFirstRow();
}
