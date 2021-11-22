package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"hardwareNo"})
public class OutputKeepAliveEntity {

    @ColumnInfo(name = "hardwareNo")
    public int hardWare;

    @ColumnInfo(name = "outputStatus")
    public String outputStatus;

    @ColumnInfo(name = "outputStatusType")
    public String outputRelayStatus;

    public OutputKeepAliveEntity(int hardWare, String outputStatus, String outputRelayStatus) {
        this.hardWare = hardWare;
        this.outputStatus = outputStatus;
        this.outputRelayStatus = outputRelayStatus;
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

    public String getOutputRelayStatus() {
        return outputRelayStatus;
    }

    public void setOutputRelayStatus(String outputRelayStatus) {
        this.outputRelayStatus = outputRelayStatus;
    }
}
