package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MainConfigurationEntity {
    @PrimaryKey(autoGenerate = true)
    public int sNo;

    @ColumnInfo(name = "screen_no")
    public int screenNo;

    @ColumnInfo(name = "layout_no")
    public int layoutNo;

    @ColumnInfo(name = "window_no")
    public int windowNo;

    @ColumnInfo(name = "inputType")
    public String inputType;

    @ColumnInfo(name = "sensor_sequence_no")
    public int sensorSequenceNo;

    @ColumnInfo(name = "sensor_name")
    public String sensorName;

    @ColumnInfo(name = "macId")
    public String macId;

    public MainConfigurationEntity(int screenNo, int layoutNo, int windowNo, String inputType,
                                   int sensorSequenceNo, String sensorName, String macId) {
        this.screenNo = screenNo;
        this.layoutNo = layoutNo;
        this.windowNo = windowNo;
        this.inputType = inputType;
        this.sensorSequenceNo = sensorSequenceNo;
        this.sensorName = sensorName;
        this.macId = macId;
    }

    public int getsNo() {
        return sNo;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }

    public int getScreenNo() {
        return screenNo;
    }

    public void setScreenNo(int screenNo) {
        this.screenNo = screenNo;
    }

    public int getLayoutNo() {
        return layoutNo;
    }

    public void setLayoutNo(int layoutNo) {
        this.layoutNo = layoutNo;
    }

    public int getWindowNo() {
        return windowNo;
    }

    public void setWindowNo(int windowNo) {
        this.windowNo = windowNo;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public int getSensorSequenceNo() {
        return sensorSequenceNo;
    }

    public void setSensorSequenceNo(int sensorSequenceNo) {
        this.sensorSequenceNo = sensorSequenceNo;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }
}
