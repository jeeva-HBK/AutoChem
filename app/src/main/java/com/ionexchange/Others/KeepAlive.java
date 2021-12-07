package com.ionexchange.Others;


import static com.ionexchange.Others.ApplicationClass.Acknowledge;
import static com.ionexchange.Others.ApplicationClass.alarmArr;
import static com.ionexchange.Others.ApplicationClass.eventLogArr;
import static com.ionexchange.Others.PacketControl.ACK;
import static com.ionexchange.Others.PacketControl.ALARM_STATUS;
import static com.ionexchange.Others.PacketControl.CRC;
import static com.ionexchange.Others.PacketControl.INPUT_VOLTAGE;
import static com.ionexchange.Others.PacketControl.OUTPUT_STATUS;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.TCP.endPacket;
import static com.ionexchange.Others.TCP.startPacket;

import android.content.Context;

import com.ionexchange.Database.Dao.AlarmLogDao;
import com.ionexchange.Database.Dao.EventLogDao;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.Entity.EventLogEntity;
import com.ionexchange.Database.WaterTreatmentDb;

import java.util.ArrayList;
import java.util.List;

//created by Silambu
public class KeepAlive {
    WaterTreatmentDb db = null;
    KeepAliveCurrentValueDao keepAliveCurrentValueDao = null;
    OutputKeepAliveDao outputKeepAliveDao;
    AlarmLogDao alarmLogDao;
    EventLogDao eventLogDao;
    InputConfigurationDao inputConfigurationDao;
    String sensorType;



    public KeepAlive(String data, Context applicationContext) {


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
            Acknowledge = startPacket + SPILT_CHAR + CRC + SPILT_CHAR + "007" + SPILT_CHAR + INPUT_VOLTAGE + SPILT_CHAR + ACK + SPILT_CHAR + endPacket;
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


            if (data[3].substring(0, 2).equals("IN")) {
              sensorType =  inputConfigurationDao.getSensorType(Integer.parseInt(data[3].substring(2, 4)));
            } else {
              sensorType =   outputKeepAliveDao.getOutputStatus(Integer.parseInt(data[3].substring(2, 4)));
            }
            if (data[4].equals("00") || data[4].equals("01")
                    || data[4].equals("02") || data[4].equals("03") || data[4].equals("04")
                    || data[4].equals("05") || data[4].equals("06") || data[4].equals("07")
                    || data[4].equals("08")) {
                if (alarmLogDao.getAlarmLogList().size() >= 1000) {
                    alarmLogDao.deleteFirstRow();
                }
                AlarmLogEntity alarmLogEntity = new AlarmLogEntity(alarmLogDao.getLastSno() + 1,
                        data[3], sensorType,
                        alarmArr[Integer.parseInt(data[4])],
                        ApplicationClass.getCurrentTime(),
                        ApplicationClass.getCurrentDate());
                List<AlarmLogEntity> outputEntryList = new ArrayList<>();
                outputEntryList.add(alarmLogEntity);
                updateToAlarmDb(outputEntryList);
            } else {
                if (eventLogDao.getEventLogList().size() >= 1000) {
                    eventLogDao.deleteFirstRow();
                }
                EventLogEntity eventLogEntity = new EventLogEntity(eventLogDao.getLastSno() + 1,
                        data[3], sensorType,
                        eventLogArr[Integer.parseInt(data[4])],
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

// {*1200$$01$4$410$420$430$440$450$460$470$480$490$500*}
// {*1200$$01$0$010$020$030$04200.00$050.000000$060$070$080$090$100*}{*1200$$01$1$110$120$130$140$150.0000$160.0000$170.0000$180$195.6272$200.0000*}{*1200$$01$2$210$220$230$240$250$260.00$270$280$290$300*}{*1200$$01$3$310$320$330$341$350$360$370$380$390$400*}{*1200$$01$4$410$420$430$440$450$460$470$480$490$500*}