package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"sNo"})
public class ServicesNotificationEntity {

    @ColumnInfo(name = "sNo")
    public int sNo;

    public ServicesNotificationEntity(int sNo) {
        this.sNo = sNo;
    }

    public int getsNo() {
        return sNo;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }
}
