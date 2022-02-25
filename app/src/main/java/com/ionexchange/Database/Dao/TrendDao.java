package com.ionexchange.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.ionexchange.Database.Entity.TrendEntity;

import java.util.List;

@Dao
public interface TrendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TrendEntity... trendEntities);

    @Query("select * FROM TrendEntity where hardwareNo = :hNo")
    List<TrendEntity> getTrendList(String hNo);

    @Query("select * FROM TrendEntity where date BETWEEN :formDate AND :toDate AND hardwareNo = :hNo")
    LiveData<List<TrendEntity>> getTrendLiveList(String hNo, String formDate, String toDate);

    @Query("SELECT sNo FROM TrendEntity order by 1 desc limit 1")
    int getLastSno();

    @Query("select * FROM TrendEntity WHERE date BETWEEN :formDate AND :toDate AND hardwareNo =:hardwareNo")
    List<TrendEntity> getLessThenOneWeek(String formDate, String toDate, String hardwareNo);

    @Query("select * FROM TrendEntity WHERE date BETWEEN :formDate AND :toDate AND hardwareNo =:hardwareNo AND rowNumber % 10 = 0")
    List<TrendEntity> getLessThenTwoWeek(String formDate, String toDate, String hardwareNo);

    @Query("select * FROM TrendEntity Where hardwareNo =:hardwareNo AND rowNumber % 60 = 0")
    List<TrendEntity> getMoreThanTwoWeek(String hardwareNo);

    @Query("SELECT max(rowNumber) FROM TrendEntity")
    Integer lastRowNumber();

    @Query("Delete from TrendEntity")
    void deleteTrendDao();
}
