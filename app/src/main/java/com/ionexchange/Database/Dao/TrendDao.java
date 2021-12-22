package com.ionexchange.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.Entity.TrendEntity;

import java.util.List;

@Dao
public interface TrendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TrendEntity... trendEntities);

    @Query("select * FROM TrendEntity where hardwareNo = :hNo")
    List<TrendEntity> getTrendList(String hNo);

    @Query("select * FROM TrendEntity")
    LiveData<List<TrendEntity>> getTrendLiveList();

    @Query("SELECT sNo FROM TrendEntity order by 1 desc limit 1")
    int getLastSno();

    @Query("select * FROM TrendEntity WHERE date BETWEEN :formDate AND :toDate")
    List<TrendEntity> getLessThenOneWeek(String formDate, String toDate);

    @Query("select * FROM TrendEntity WHERE date('now','1 hours')")
    List<TrendEntity> getList();
}
