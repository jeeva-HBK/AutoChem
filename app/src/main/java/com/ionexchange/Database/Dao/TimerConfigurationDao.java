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
    void updateTimer(String timerName, String outputLinked, String mode, int timerNo);

    @Query("Delete from timerconfigurationentity")
    void deleteTimerDao();

    @Query("select timerName from timerconfigurationentity WHERE timerNo = :timerNo ")
    String getTimerName(int timerNo);

    @Query("select mainTimerPacket from timerconfigurationentity WHERE timerNo = :timerNo ")
    String getAccessoryPacket(int timerNo);

    @Query("select weekOnePacket from timerconfigurationentity WHERE timerNo = :timerNo ")
    String getWeekOnePacket(int timerNo);

    @Query("select weekTwoPacket from timerconfigurationentity WHERE timerNo = :timerNo ")
    String getWeekTwoPacket(int timerNo);

    @Query("select weekThreePacket from timerconfigurationentity WHERE timerNo = :timerNo ")
    String getWeekThreePacket(int timerNo);

    @Query("select weekFourPacket from timerconfigurationentity WHERE timerNo = :timerNo ")
    String getWeekFourPacket(int timerNo);
}
