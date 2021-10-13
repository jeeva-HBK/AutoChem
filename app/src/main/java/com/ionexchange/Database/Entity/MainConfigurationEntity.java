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

    @ColumnInfo(name = "page_No")
    public int pageNo;

    @ColumnInfo(name = "hardware_no")
    public int hardware_no;

    @ColumnInfo(name = "inputType")
    public String inputType;

    @ColumnInfo(name = "sensor_sequence_no")
    public int sensorSequenceNo;

    @ColumnInfo(name = "sensor_name")
    public String sensorName;

    @ColumnInfo(name = "flag_Value")
    public int flag_Value;

    public MainConfigurationEntity(int sNo, int screenNo,
                                   int layoutNo, int windowNo, int pageNo, int hardware_no,
                                   String inputType, int sensorSequenceNo, String sensorName, int flag_Value) {
        this.sNo = sNo;
        this.screenNo = screenNo;
        this.layoutNo = layoutNo;
        this.windowNo = windowNo;
        this.pageNo = pageNo;
        this.hardware_no = hardware_no;
        this.inputType = inputType;
        this.sensorSequenceNo = sensorSequenceNo;
        this.sensorName = sensorName;
        this.flag_Value = flag_Value;
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

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getHardware_no() {
        return hardware_no;
    }

    public void setHardware_no(int hardware_no) {
        this.hardware_no = hardware_no;
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

    public int getFlag_Value() {
        return flag_Value;
    }

    public void setFlag_Value(int flag_Value) {
        this.flag_Value = flag_Value;
    }
}
