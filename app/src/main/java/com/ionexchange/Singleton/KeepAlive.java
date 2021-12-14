package com.ionexchange.Singleton;


import static com.ionexchange.Others.ApplicationClass.Acknowledge;
import static com.ionexchange.Others.ApplicationClass.alarmArr;
import static com.ionexchange.Others.ApplicationClass.eventLogArr;
import static com.ionexchange.Others.PacketControl.ACK;
import static com.ionexchange.Others.PacketControl.ALARM_STATUS;
import static com.ionexchange.Others.PacketControl.CRC;
import static com.ionexchange.Others.PacketControl.ENDPACKET;
import static com.ionexchange.Others.PacketControl.INPUT_VOLTAGE;
import static com.ionexchange.Others.PacketControl.OUTPUT_STATUS;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.STARTPACKET;

import android.content.Context;

import com.ionexchange.Database.Dao.AlarmLogDao;
import com.ionexchange.Database.Dao.EventLogDao;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.Entity.EventLogEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Others.ApplicationClass;

import java.util.ArrayList;
import java.util.List;

//created by Silambu
public class KeepAlive {
    static KeepAlive keepAlive;
    WaterTreatmentDb db = null;
    KeepAliveCurrentValueDao keepAliveCurrentValueDao = null;
    OutputKeepAliveDao outputKeepAliveDao;
    AlarmLogDao alarmLogDao;
    EventLogDao eventLogDao;
    InputConfigurationDao inputConfigurationDao;
    String sensorType;

    public KeepAlive() {
    }

    public static KeepAlive getInstance() {
        if (keepAlive == null) {
            keepAlive = new KeepAlive();
        }
        return keepAlive;
    }

    public void processKeepAlive(String data, Context applicationContext) {
        if (db == null) {
            db = WaterTreatmentDb.getDatabase(applicationContext);
        }
        if (keepAliveCurrentValueDao == null) {
            keepAliveCurrentValueDao = db.keepAliveCurrentValueDao();
            outputKeepAliveDao = db.outputKeepAliveDao();
            inputConfigurationDao = db.inputConfigurationDao();
            alarmLogDao = db.alarmLogDao();
            eventLogDao = db.eventLogDao();
        }

        if (data != null) {
            spiltData(data.split("\\*")[1].split(RES_SPILT_CHAR));
        }
    }

    void spiltData(String[] data) {
        if (data[2].equals(INPUT_VOLTAGE)) {
            Acknowledge = STARTPACKET + SPILT_CHAR + CRC + SPILT_CHAR + "007" + SPILT_CHAR + INPUT_VOLTAGE + SPILT_CHAR + ACK + SPILT_CHAR + ENDPACKET;
            int i = 0;
            int j;
            if (data[3].equals("5")) {
                j = 7;
            } else {
                j = 10;
            }
            while (i < j) {
                if (data[i + 4].length() > 2) {
                    if (Integer.parseInt(data[i + 4].substring(0, 2)) > 33 && Integer.parseInt(data[i + 4].substring(0, 2)) < 50) { // DIGITAL & TANK
                        if (data[i + 4].substring(2, data[i + 4].length()).equals("1")) {
                            keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 4].substring(0, 2)), "OPEN");
                        } else if (data[i + 4].substring(2, data[i + 4].length()).equals("2")) {
                            keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 4].substring(0, 2)), "CLOSE");
                        }
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 4].substring(0, 2)), data[i + 4].substring(2, data[i + 4].length()));
                    }
                } else {
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 4].substring(0, 2)), "N/A");
                }
                i++;
            }
        }
        if (data[2].equals(OUTPUT_STATUS)) {
            int i = 0;
            while (i <= 21) {
                if (data[3 + i].length() <= 1) {
                    outputKeepAliveDao.updateOutputStatus(i + 1, data[3 + i]);
                }
                if (data[3 + i].length() > 1) {
                    outputKeepAliveDao.updateOutputStatus(i + 1, data[3 + i].substring(0, 1));
                    outputKeepAliveDao.updateOutputRelayStatus(i + 1, data[3 + i].substring(1));
                }
                i++;
            }
        }
        if (data[2].equals(ALARM_STATUS)) {
            if (alarmLogDao.getAlarmLogList().size() >= 1000) {
                alarmLogDao.deleteFirstRow();
            }
            if (eventLogDao.getEventLogList().size() >= 1000) {
                eventLogDao.deleteFirstRow();
            }

            switch (data[3].substring(0, 2)) {
                case "IN":
                    sensorType = inputConfigurationDao.getSensorType(Integer.parseInt(data[3].substring(2, 4)));
                    break;
                case "OP":
                    sensorType = outputKeepAliveDao.getOutputStatus(Integer.parseInt(data[3].substring(2, 4)));
                    break;
                case "TI":
                    sensorType = "TIMER" + data[3].substring(2, 4);
                    break;
            }
            if (data[4].equals("0")) {
                AlarmLogEntity alarmLogEntity = new AlarmLogEntity(alarmLogDao.getLastSno() + 1,
                        data[3], sensorType,
                        alarmArr[Integer.parseInt(data[5])],
                        ApplicationClass.getCurrentTime(),
                        ApplicationClass.getCurrentDate());
                List<AlarmLogEntity> outputEntryList = new ArrayList<>();
                outputEntryList.add(alarmLogEntity);
                updateToAlarmDb(outputEntryList);

            } else if (data[4].equals("1")) {
                EventLogEntity eventLogEntity = new EventLogEntity(eventLogDao.getLastSno() + 1,
                        data[3], sensorType,
                        eventLogArr[Integer.parseInt(data[5])],
                        ApplicationClass.getCurrentTime(), ApplicationClass.getCurrentDate());
                List<EventLogEntity> eventLogEntities = new ArrayList<>();
                eventLogEntities.add(eventLogEntity);
                updateToEventDb(eventLogEntities);
            }

        }
    }

    public void updateToAlarmDb(List<AlarmLogEntity> entryList) {
        alarmLogDao.insert(entryList.toArray(new AlarmLogEntity[0]));
    }

    public void updateToEventDb(List<EventLogEntity> entryList) {
        eventLogDao.insert(entryList.toArray(new EventLogEntity[0]));
    }
}