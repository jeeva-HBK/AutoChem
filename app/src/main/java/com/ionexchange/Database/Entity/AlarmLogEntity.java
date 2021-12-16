package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"sNo"})
public class AlarmLogEntity {

    @ColumnInfo(name = "sNo")
    public int sNo;

    @ColumnInfo(name = "hardwareNo")
    public String hardwareNo;

    @ColumnInfo(name = "sensorType")
    public String sensorType;

    @ColumnInfo(name = "alarmLog")
    public String alarmLog;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "lockOutAlarm")
    public String lockOutAlarm;

    public AlarmLogEntity(int sNo, String hardwareNo, String sensorType, String alarmLog, String time, String date, String lockOutAlarm) {
        this.sNo = sNo;
        this.hardwareNo = hardwareNo;
        this.sensorType = sensorType;
        this.alarmLog = alarmLog;
        this.time = time;
        this.date = date;
        this.lockOutAlarm = lockOutAlarm;
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

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getAlarmLog() {
        return alarmLog;
    }

    public void setAlarmLog(String alarmLog) {
        this.alarmLog = alarmLog;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLockOutAlarm() {
        return lockOutAlarm;
    }

    public void setLockOutAlarm(String lockOutAlarm) {
        this.lockOutAlarm = lockOutAlarm;
    }
}
