package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"inputNo"})
public class CalibrationEntity {


    public CalibrationEntity(int inputNo, String inputType, String date, String calibrationValue) {
        this.inputNo = inputNo;
        this.inputType = inputType;
        this.date = date;
        this.calibrationValue = calibrationValue;
    }

    @ColumnInfo(name = "inputNo")
    public int inputNo;

    @ColumnInfo(name = "inputType")
    public String inputType;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "calibrationValue")
    public String calibrationValue;


    public int getInputNo() {
        return inputNo;
    }

    public void setInputNo(int inputNo) {
        this.inputNo = inputNo;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCalibrationValue() {
        return calibrationValue;
    }

    public void setCalibrationValue(String calibrationValue) {
        this.calibrationValue = calibrationValue;
    }
}
