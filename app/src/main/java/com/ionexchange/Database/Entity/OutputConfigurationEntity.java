package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"outputNumber"})
public class OutputConfigurationEntity {

    @ColumnInfo(name = "outputNumber")
    public int outputHardwareNo;

    @ColumnInfo(name = "outputType")
    public String outputType;

    @ColumnInfo(name = "outputLabel")
    public String outputLabel;

    @ColumnInfo(name = "outputMode")
    public String outputMode;

    @ColumnInfo(name = "outputStatus")
    public String outputStatus;


    public OutputConfigurationEntity(int outputHardwareNo, String outputType, String outputLabel, String outputMode, String outputStatus) {
        this.outputHardwareNo = outputHardwareNo;
        this.outputType = outputType;
        this.outputLabel = outputLabel;
        this.outputMode = outputMode;
        this.outputStatus = outputStatus;
    }

    public int getOutputHardwareNo() {
        return outputHardwareNo;
    }

    public void setOutputHardwareNo(int outputHardwareNo) {
        this.outputHardwareNo = outputHardwareNo;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getOutputLabel() {
        return outputLabel;
    }

    public void setOutputLabel(String outputLabel) {
        this.outputLabel = outputLabel;
    }

    public String getOutputMode() {
        return outputMode;
    }

    public void setOutputMode(String outputMode) {
        this.outputMode = outputMode;
    }

    public String getOutputStatus() {
        return outputStatus;
    }

    public void setOutputStatus(String outputStatus) {
        this.outputStatus = outputStatus;
    }
}


