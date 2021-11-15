package com.ionexchange.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ionexchange.Database.Entity.DiagnosticDataEntity;
import com.ionexchange.Database.Entity.InputConfigurationEntity;

import java.util.List;

@Dao
public interface DiagnosticDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DiagnosticDataEntity... diagnosticDataEntities);

    @Query("SELECT sNo FROM DiagnosticDataEntity order by 1 desc limit 1")
    int getLastSno();

    @Query("select * FROM DiagnosticDataEntity order by sNo desc")
    List<DiagnosticDataEntity> getDiagnosticDataList();

    @Query("select * FROM DiagnosticDataEntity WHERE " +
            " hardwareNo BETWEEN :hardwareTo AND :hardwareNo" +
            " AND hardwareNo BETWEEN :hardwareTo AND :hardwareNo" +
            " AND hardwareNo BETWEEN :hardwareToSec AND :hardwareNoSec " +
            " AND hardwareNo BETWEEN :hardwareToThree AND :hardwareNoThree"+
            " AND hardwareNo BETWEEN :hardwareToFour AND :hardwareNoFour"+
            " AND hardwareNo BETWEEN :hardwareToFive AND :hardwareNoFive"+
            " AND hardwareNo BETWEEN :hardwareToSix AND :hardwareNoSix"+
            " AND hardwareNo BETWEEN :hardwareToSeven AND :hardwareNoSeven"
    )
    List<DiagnosticDataEntity> getInputHardWareNoDiagnosticDataEntity(
            int hardwareTo, int hardwareNo,
            int hardwareToSec,int hardwareNoSec,
            int hardwareToThree,int hardwareNoThree,
            int hardwareToFour,int hardwareNoFour,
            int hardwareToFive,int hardwareNoFive,
            int hardwareToSix,int hardwareNoSix,
            int hardwareToSeven,int hardwareNoSeven);
}
