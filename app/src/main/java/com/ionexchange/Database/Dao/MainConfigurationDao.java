package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.MainConfigurationEntity;

import java.util.List;

//created by silambu
@Dao
public interface MainConfigurationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MainConfigurationEntity... mainConfigurationEntities);

    @Query("select * FROM MainConfigurationEntity")
    List<MainConfigurationEntity> getMainConfigurationEntityList();

    @Query("select * FROM MainConfigurationEntity WHERE screen_no = :screen_no and layout_no = :layout_no " +
            " and page_No =:page_no")
    List<MainConfigurationEntity> getPageWiseSensor(int screen_no, int layout_no, int page_no);


    @Query("select inputType FROM MainConfigurationEntity WHERE screen_no = :screen_no and layout_no = :layout_no " +
            "and window_no = :window_no and page_No =:page_no")
    String getSensorName(int screen_no, int layout_no, int window_no, int page_no);

    @Query("DELETE FROM MainConfigurationEntity WHERE sNo = :sNo")
    void deleteBySnoId(int sNo);

    @Query("SELECT sNo FROM MainConfigurationEntity order by 1 desc limit 1")
    int getLastSno();

    @Query("select sNo  FROM MainConfigurationEntity WHERE  screen_no = :screen_no and layout_no = :layout_no " +
            " and window_no = :window_no   and page_No =:page_no")
    int getSno(int screen_no, int layout_no, int window_no, int page_no);


    @Query("select sum(window_no) FROM  MainConfigurationEntity WHERE screen_no = :screen_no and layout_no = :layout_no " +
            "and page_No =:page_no")
    Integer sumWindowNo(int screen_no, int layout_no, int page_no);

    @Query("select max(page_No) FROM  MainConfigurationEntity WHERE screen_no = :screen_no and layout_no = :layout_no ")
    int maxPageNo(int screen_no, int layout_no);

    @Query("select inputType  FROM MainConfigurationEntity WHERE  screen_no = :screen_no and layout_no = :layout_no " +
            " and window_no = :window_no   and page_No =:page_no")
    String getInputType(int screen_no, int layout_no, int window_no, int page_no);


    @Query("DELETE FROM MainConfigurationEntity WHERE screen_no = :screen_no and layout_no = :layout_no " +
            " and window_no = :window_no   and page_No =:page_no")
    void delete(int screen_no, int layout_no, int window_no, int page_no);


}
