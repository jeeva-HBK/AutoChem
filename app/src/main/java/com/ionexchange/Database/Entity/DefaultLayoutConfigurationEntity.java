package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"s_no"})
public class DefaultLayoutConfigurationEntity {

    @ColumnInfo(name = "s_no")
    public int sNo;

    @ColumnInfo(name = "screen_no")
    public int screenNo;

    @ColumnInfo(name = "default_screen_enable")
    public int defaultScreenEnableNo;

    @ColumnInfo(name = "macId")
    public String macId;

    @ColumnInfo(name = "default_layout_no")
    public int default_layout_no;

    public DefaultLayoutConfigurationEntity(int sNo, int screenNo, int defaultScreenEnableNo, String macId, int default_layout_no) {
        this.sNo = sNo;
        this.screenNo = screenNo;
        this.defaultScreenEnableNo = defaultScreenEnableNo;
        this.macId = macId;
        this.default_layout_no = default_layout_no;
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

    public int getDefaultLaoutEnableNo() {
        return defaultScreenEnableNo;
    }

    public void setDefaultLaoutEnableNo(int defaultScreenEnableNo) {
        this.defaultScreenEnableNo = defaultScreenEnableNo;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public int getDefault_layout_no() {
        return default_layout_no;
    }

    public void setDefault_layout_no(int default_layout_no) {
        this.default_layout_no = default_layout_no;
    }
}
