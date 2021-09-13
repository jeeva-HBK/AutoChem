package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.TimerConfigurationEntity;

import java.util.List;

@Dao
public interface TimerConfigurationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TimerConfigurationEntity... timerConfigurationEntities);

    @Query("select * FROM timerconfigurationentity")
    List<TimerConfigurationEntity> geTimerConfigurationEntityList();

    @Query("UPDATE TimerConfigurationEntity SET timerName =:timerName , outputLinked =:outputLinked " +
            ", mode=:mode WHERE timerNo = :timerNo")
    void updateTimer(String timerName,String outputLinked,String mode,int timerNo);

}
