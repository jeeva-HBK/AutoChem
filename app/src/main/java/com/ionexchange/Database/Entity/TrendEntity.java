package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"sNo"})
public class TrendEntity {

    @ColumnInfo(name = "sNo")
    public int sNo;

    @ColumnInfo(name = "hardwareNo")
    public String hardwareNo;

    @ColumnInfo(name = "keepValue")
    public String keepValue;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "rowNumber")
    public int rowNumber;

    public TrendEntity(int sNo, String hardwareNo, String keepValue, String date, String time, int rowNumber) {
        this.sNo = sNo;
        this.hardwareNo = hardwareNo;
        this.keepValue = keepValue;
        this.date = date;
        this.time = time;
        this.rowNumber = rowNumber;
    }

    public int getsNo() {
        return sNo;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }

    public String getHardwareNo() {
        return hardwareNo;
    }

    public void setHardwareNo(String hardwareNo) {
        this.hardwareNo = hardwareNo;
    }

    public String getKeepValue() {
        return keepValue;
    }

    public void setKeepValue(String keepValue) {
        this.keepValue = keepValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }
}
