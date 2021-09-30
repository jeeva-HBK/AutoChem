package com.ionexchange.Database.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"hardwareNo"})
public class KeepAliveCurrentEntity {

    @ColumnInfo(name = "hardwareNo")
    public int hardWare;

    @ColumnInfo(name = "currentValue")
    public String currentValue;

    public KeepAliveCurrentEntity(int hardWare, String currentValue) {
        this.hardWare = hardWare;
        this.currentValue = currentValue;
    }

    public int getHardWare() {
        return hardWare;
    }

    public void setHardWare(int hardWare) {
        this.hardWare = hardWare;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }
}
