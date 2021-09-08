package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.DefaultLayoutConfigurationEntity;
import com.ionexchange.Database.Entity.MainConfigurationEntity;

import java.util.List;
@Dao
public interface MainConfigurationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MainConfigurationEntity... mainConfigurationEntities);

    @Query("select * FROM MainConfigurationEntity")
    List<MainConfigurationEntity> getMainConfigurationEntityList();

    @Query("select * FROM MainConfigurationEntity WHERE screen_no = :screen_no and layout_no = :layout_no " +
            "and window_no = :window_no and inputType = :type")
    List<MainConfigurationEntity> getExitingSensorList(int screen_no,int layout_no,int window_no,String type);

    @Query("select * FROM MainConfigurationEntity WHERE screen_no = :screen_no and layout_no = :layout_no and window_no = :window_no")
    List<MainConfigurationEntity> getSensorList(int screen_no,int layout_no,int window_no);
}
