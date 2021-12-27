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

    @ColumnInfo(name = "mainTimerPacket")
    public String mainTimerPacket;

    @ColumnInfo(name = "weekOnePacket")
    public String weekOnePacket;

    @ColumnInfo(name = "weekTwoPacket")
    public String weekTwoPacket;

    @ColumnInfo(name = "weekThreePacket")
    public String weekThreePacket;

    @ColumnInfo(name = "weekFourPacket")
    public String weekFourPacket;

    public TimerConfigurationEntity(int timerNo, String timerName, String outputLinked,
                                    String mode, String mainTimerPacket, String weekOnePacket,
                                    String weekTwoPacket,String weekThreePacket,String weekFourPacket) {
        this.timerNo = timerNo;
        this.timerName = timerName;
        this.outputLinked = outputLinked;
        this.mode = mode;
        this.mainTimerPacket = mainTimerPacket;
        this.weekOnePacket = weekOnePacket;
        this.weekTwoPacket = weekTwoPacket;
        this.weekThreePacket = weekThreePacket;
        this.weekFourPacket = weekFourPacket;
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

    public String getMainTimerPacket() {
        return mainTimerPacket;
    }

    public void setMainTimerPacket(String mainTimerPacket) {
        this.mainTimerPacket = mainTimerPacket;
    }

    public String getWeekOnePacket() {
        return weekOnePacket;
    }

    public void setWeekOnePacket(String weekOnePacket) {
        this.weekOnePacket = weekOnePacket;
    }

    public String getWeekTwoPacket() {
        return weekTwoPacket;
    }

    public void setWeekTwoPacket(String weekTwoPacket) {
        this.weekTwoPacket = weekTwoPacket;
    }

    public String getWeekThreePacket() {
        return weekThreePacket;
    }

    public void setWeekThreePacket(String weekThreePacket) {
        this.weekThreePacket = weekThreePacket;
    }

    public String getWeekFourPacket() {
        return weekFourPacket;
    }

    public void setWeekFourPacket(String weekFourPacket) {
        this.weekFourPacket = weekFourPacket;
    }
}
