package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.ionexchange.Database.Entity.ServicesNotificationEntity;

@Dao
public interface ServicesNotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ServicesNotificationEntity... servicesNotificationEntities);

    @Query("SELECT sNo FROM ServicesNotificationEntity order by 1 desc limit 1")
    int getLastSno();

    @Query("Delete FROM ServicesNotificationEntity WHERE sNo in (SELECT sNo FROM ServicesNotificationEntity limit 1)")
    void deleteRow();
}
