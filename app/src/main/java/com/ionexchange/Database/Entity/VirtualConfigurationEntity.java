package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"hardwareNo"})
public class VirtualConfigurationEntity {
    @ColumnInfo(name = "hardwareNo")
    public int hardwareNo;

    @ColumnInfo(name = "virtualType")
    public String virtualType;

    @ColumnInfo(name = "inputSequenceNumber")
    public int inputSequenceNumber;

    @ColumnInfo(name = "inputLabel")
    public String inputLabel;

    @ColumnInfo(name = "inputType")
    public String inputType;

    @ColumnInfo(name = "subValueOne")
    public String subValueOne;

    @ColumnInfo(name = "subValueTwo")
    public String subValueTwo;

    @ColumnInfo(name = "unit")
    public String unit;

    @ColumnInfo(name = "writePacket")
    public String writePacket;

    public VirtualConfigurationEntity(int hardwareNo, String virtualType, int inputSequenceNumber,
                                      String inputLabel, String inputType, String subValueOne, String subValueTwo, String unit, String writePacket) {
        this.hardwareNo = hardwareNo;
        this.virtualType = virtualType;
        this.inputSequenceNumber = inputSequenceNumber;
        this.inputLabel = inputLabel;
        this.inputType = inputType;
        this.subValueOne = subValueOne;
        this.subValueTwo = subValueTwo;
        this.unit = unit;
        this.writePacket = writePacket;
    }

    public int getHardwareNo() {
        return hardwareNo;
    }

    public void setHardwareNo(int hardwareNo) {
        this.hardwareNo = hardwareNo;
    }

    public String getVirtualType() {
        return virtualType;
    }

    public void setVirtualType(String virtualType) {
        this.virtualType = virtualType;
    }

    public int getInputSequenceNumber() {
        return inputSequenceNumber;
    }

    public void setInputSequenceNumber(int inputSequenceNumber) {
        this.inputSequenceNumber = inputSequenceNumber;
    }

    public String getInputLabel() {
        return inputLabel;
    }

    public void setInputLabel(String inputLabel) {
        this.inputLabel = inputLabel;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getSubValueOne() {
        return subValueOne;
    }

    public void setSubValueOne(String subValueOne) {
        this.subValueOne = subValueOne;
    }

    public String getSubValueTwo() {
        return subValueTwo;
    }

    public void setSubValueTwo(String subValueTwo) {
        this.subValueTwo = subValueTwo;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getWritePacket() {
        return writePacket;
    }

    public void setWritePacket(String writePacket) {
        this.writePacket = writePacket;
    }
}


