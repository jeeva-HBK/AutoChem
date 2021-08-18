package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"timerNo"})
public class TimerConfigurationEntity {

    @ColumnInfo(name = "timerNo")
    public int timerNo;

    @ColumnInfo(name = "timerName")
    public String timerName;

    @ColumnInfo(name = "outputLinked")
    public String outputLinked;

    @ColumnInfo(name = "mode")
    public String mode;

    @ColumnInfo(name = "startTime")
    public long startTime;

    @ColumnInfo(name = "duration")
    public long duration;

    @ColumnInfo(name = "status")
    public String status;

    public TimerConfigurationEntity(int timerNo, String timerName, String outputLinked,
                                    String mode, long startTime, long duration, String status) {
        this.timerNo = timerNo;
        this.timerName = timerName;
        this.outputLinked = outputLinked;
        this.mode = mode;
        this.startTime = startTime;
        this.duration = duration;
        this.status = status;
    }

    public int getTimerNo() {
        return timerNo;
    }

    public void setTimerNo(int timerNo) {
        this.timerNo = timerNo;
    }

    public String getTimerName() {
        return timerName;
    }

    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }

    public String getOutputLinked() {
        return outputLinked;
    }

    public void setOutputLinked(String outputLinked) {
        this.outputLinked = outputLinked;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
