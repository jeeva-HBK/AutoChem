package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.DefaultLayoutConfigurationEntity;

import java.util.List;
@Dao
public interface DefaultLayoutConfigurationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DefaultLayoutConfigurationEntity... DefaultLayoutConfigurationEntities);

    @Query("select * FROM DefaultLayoutConfigurationEntity")
    List<DefaultLayoutConfigurationEntity> getDefaultLayoutConfigurationEntityList();

    @Query("select * FROM DefaultLayoutConfigurationEntity WHERE default_screen_enable = :default_screen_enable")
    List<DefaultLayoutConfigurationEntity> getEnableDefaultScreen(int default_screen_enable);

    @Query("UPDATE DefaultLayoutConfigurationEntity SET default_screen_enable=:default_screen_enable WHERE s_no = :s_no")
    void update(int default_screen_enable,int s_no);

    @Query("UPDATE DefaultLayoutConfigurationEntity SET default_layout_no=:default_layout_no WHERE s_no = :s_no")
    void updateLayout(int default_layout_no,int s_no);

    @Query("select * FROM DefaultLayoutConfigurationEntity WHERE s_no = :s_no")
    List<DefaultLayoutConfigurationEntity> getEnableDefaultLayout(int s_no);
}
