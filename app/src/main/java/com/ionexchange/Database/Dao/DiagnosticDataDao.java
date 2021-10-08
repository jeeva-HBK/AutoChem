package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.DiagnosticDataEntity;

import java.util.List;

@Dao
public interface DiagnosticDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DiagnosticDataEntity... diagnosticDataEntities);

    @Query("SELECT sNo FROM DiagnosticDataEntity order by 1 desc limit 1")
    int getLastSno();

    @Query("select * FROM DiagnosticDataEntity order by sNo desc")
    List<DiagnosticDataEntity> getDiagnosticDataList();

}
