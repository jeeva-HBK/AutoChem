package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.KeepAliveCurrentEntity;

import java.util.List;

@Dao
public interface KeepAliveCurrentValueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(KeepAliveCurrentEntity... keepAliveCurrentEntities);

    @Query("select * FROM KeepAliveCurrentEntity")
    List<KeepAliveCurrentEntity> getKeepAliveList();

    @Query("select currentValue  FROM KeepAliveCurrentEntity WHERE  hardwareNo = :hardwareNo ")
    String getCurrentValue(int hardwareNo);

    @Query("Update KeepAliveCurrentEntity SET currentValue = :cValue WHERE hardwareNo = :hardwareNo")
    void updateCurrentValue(int hardwareNo, String cValue);
}
