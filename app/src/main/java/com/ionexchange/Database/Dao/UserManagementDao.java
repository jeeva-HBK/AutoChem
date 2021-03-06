package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.UsermanagementEntity;

import java.util.List;

@Dao
public interface UserManagementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UsermanagementEntity... usermanagementEntities);

    @Query("select * FROM usermanagemententity")
    List<UsermanagementEntity> getUsermanagementEntity();

    @Query("select user_password FROM usermanagemententity where user_name == :userName")
    String getPassword(String userName);
}
