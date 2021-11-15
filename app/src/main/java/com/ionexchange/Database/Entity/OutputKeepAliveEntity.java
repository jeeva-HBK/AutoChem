package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"hardwareNo"})
public class OutputKeepAliveEntity {

    @ColumnInfo(name = "hardwareNo")
    public int hardWare;

    @ColumnInfo(name = "outputStatus")
    public String outputStatus;

    public OutputKeepAliveEntity(int hardWare, String outputStatus) {
        this.hardWare = hardWare;
        this.outputStatus = outputStatus;
    }

    public int getHardWare() {
        return hardWare;
    }

    public void setHardWare(int hardWare) {
        this.hardWare = hardWare;
    }

    public String getOutputStatus() {
        return outputStatus;
    }

    public void setOutputStatus(String outputStatus) {
        this.outputStatus = outputStatus;
    }
}
