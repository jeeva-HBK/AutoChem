package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"hardwareNo"})
public class InputConfigurationEntity {

    @ColumnInfo(name = "hardwareNo")
    public int hardwareNo;

    @ColumnInfo(name = "inputType")
    public String inputType;

    @ColumnInfo(name = "sensorType")
    public String sensorType;

    @ColumnInfo(name = "inputsequenceName")
    public String inputsequenceName;

    @ColumnInfo(name = "inputSequenceNumber")
    public int inputSequenceNumber;

    @ColumnInfo(name = "inputLabel")
    public String inputLabel;

    @ColumnInfo(name = "subValueOne")
    public String subValueOne;

    @ColumnInfo(name = "subValueTwo")
    public String subValueTwo;

    @ColumnInfo(name = "flagKey")
    public int flagKey;

    public InputConfigurationEntity(int hardwareNo, String inputType, String sensorType,String inputsequenceName,
                                    int inputSequenceNumber, String inputLabel, String subValueOne, String subValueTwo, int flagKey) {
        this.hardwareNo = hardwareNo;
        this.inputType = inputType;
        this.sensorType = sensorType;
        this.inputsequenceName = inputsequenceName;
        this.inputSequenceNumber = inputSequenceNumber;
        this.inputLabel = inputLabel;
        this.subValueOne = subValueOne;
        this.subValueTwo = subValueTwo;
        this.flagKey = flagKey;
    }

    public int getHardwareNo() {
        return hardwareNo;
    }

    public void setHardwareNo(int hardwareNo) {
        this.hardwareNo = hardwareNo;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getInputsequenceName() {
        return inputsequenceName;
    }

    public void setInputsequenceName(String inputsequenceName) {
        this.inputsequenceName = inputsequenceName;
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

    public int getFlagKey() {
        return flagKey;
    }

    public void setFlagKey(int flagKey) {
        this.flagKey = flagKey;
    }
}
