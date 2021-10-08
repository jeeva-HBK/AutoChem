package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DiagnosticDataEntity {

    @PrimaryKey(autoGenerate = true)
    public int sNo;

    @ColumnInfo(name = "hardwareNo")
    public int hardWare;

    @ColumnInfo(name = "diagnosticData")
    public String diagnosticData;

    @ColumnInfo(name = "timeStamp")
    public String timeStamp;

    public DiagnosticDataEntity(int sNo, int hardWare, String diagnosticData, String timeStamp) {
        this.sNo = sNo;
        this.hardWare = hardWare;
        this.diagnosticData = diagnosticData;
        this.timeStamp = timeStamp;
    }

    public int getsNo() {
        return sNo;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }

    public int getHardWare() {
        return hardWare;
    }

    public void setHardWare(int hardWare) {
        this.hardWare = hardWare;
    }

    public String getDiagnosticData() {
        return diagnosticData;
    }

    public void setDiagnosticData(String diagnosticData) {
        this.diagnosticData = diagnosticData;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
