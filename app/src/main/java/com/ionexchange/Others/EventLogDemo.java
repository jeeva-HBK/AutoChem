package com.ionexchange.Others;

import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

import android.content.Context;

import com.ionexchange.Database.Dao.EventLogDao;
import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.Entity.EventLogEntity;
import com.ionexchange.Database.WaterTreatmentDb;

import java.util.ArrayList;
import java.util.List;

public class EventLogDemo {

    WaterTreatmentDb waterTreatmentDb;
    EventLogDao eventLogDao;

    public EventLogDemo(String hardwareNo,String sensorType,String eventLog, Context applicationContext) {
        waterTreatmentDb = WaterTreatmentDb.getDatabase(applicationContext);
        eventLogDao = waterTreatmentDb.eventLogDao();
        EventLogEntity eventLogEntity = new EventLogEntity(eventLogDao.getLastSno() + 1,
                hardwareNo, sensorType, eventLog,  ApplicationClass.getCurrentTime(),
                ApplicationClass.getCurrentDate());
        List<EventLogEntity> outputEntryList = new ArrayList<>();
        outputEntryList.add(eventLogEntity);
        updateToDb(outputEntryList);
    }


    public void updateToDb(List<EventLogEntity> entryList) {
        eventLogDao.insert(entryList.toArray(new EventLogEntity[0]));
    }
}
