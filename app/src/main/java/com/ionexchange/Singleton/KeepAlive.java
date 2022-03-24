package com.ionexchange.Singleton;


import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.ionexchange.Database.Dao.AlarmLogDao;
import com.ionexchange.Database.Dao.EventLogDao;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.Dao.TrendDao;
import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.Entity.EventLogEntity;
import com.ionexchange.Database.Entity.TrendEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.alarmArr;
import static com.ionexchange.Others.ApplicationClass.alertKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.inputKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.outputKeepAliveData;
import static com.ionexchange.Others.ApplicationClass.trendDataCollector;
import static com.ionexchange.Others.PacketControl.ALARM_STATUS;
import static com.ionexchange.Others.PacketControl.INPUT_VOLTAGE;
import static com.ionexchange.Others.PacketControl.OUTPUT_STATUS;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;

//created by Silambu
public class KeepAlive implements DataReceiveCallback {
    static KeepAlive keepAlive;
    ApplicationClass mAppClass;
    WaterTreatmentDb db = null;
    KeepAliveCurrentValueDao keepAliveCurrentValueDao = null;
    OutputKeepAliveDao outputKeepAliveDao;
    AlarmLogDao alarmLogDao;
    EventLogDao eventLogDao;
    InputConfigurationDao inputConfigurationDao;
    TrendDao trendDao;
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
            trendDao = db.trendDao();
        }
        mAppClass = ApplicationClass.getInstance();
        if (data != null) {
            spiltData(data.split("\\*")[1].split(RES_SPILT_CHAR));
            String[] splitInput = data.split("\\*")[1].split(RES_SPILT_CHAR);
            if (splitInput[2].equals(INPUT_VOLTAGE)) {
                inputKeepAliveData = data;
                trendDataCollector = data;
            } else if (splitInput[2].equals(OUTPUT_STATUS)) {
                outputKeepAliveData = data;
            } else if (splitInput[2].equals(ALARM_STATUS)) {
                alertKeepAliveData = data;
            }
        }
    }

    void spiltData(String[] data) {
        if (data[2].equals(INPUT_VOLTAGE)) {
            // Acknowledge = STARTPACKET + SPILT_CHAR + CRC + SPILT_CHAR + "007" + SPILT_CHAR + INPUT_VOLTAGE + SPILT_CHAR + ACK + SPILT_CHAR + ENDPACKET;
            int i = 0;
            while (i < 57) {
                if (data[i + 3].length() > 2) {
                    if (Integer.parseInt(data[i + 3].substring(0, 2)) > 33 && Integer.parseInt(data[i + 3].substring(0, 2)) < 50) { // DIGITAL & TANK
                        if (data[i + 3].substring(2, data[i + 3].length()).equals("0")) {
                            keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 3].substring(0, 2)), "DISABLED");
                        } if (data[i + 3].substring(2, data[i + 3].length()).equals("1")) {
                            keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 3].substring(0, 2)), "OPEN");
                        } else if (data[i + 3].substring(2, data[i + 3].length()).equals("2")) {
                            keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 3].substring(0, 2)), "CLOSE");
                        }
                    } else if (data[i + 3].substring(0, 2).equals("01")) {
                        if(!data[i+3].equals("010")) {
                            if (data[i + 3].split("\\.")[1].length() >= 1) {
                                keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 3].substring(0, 2)), data[i + 3].split("\\.")[0].substring(2) +
                                        "." + data[i + 3].split("\\.")[1].substring(0, 2));
                            } else {
                                keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 3].substring(0, 2)), data[i + 3]);
                            }
                        } else {
                            keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 3].substring(0, 2)), "0");
                        }
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 3].substring(0, 2)), data[i + 3].substring(2, data[i + 3].length()));
                    }
                } else {
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 3].substring(0, 2)), "N/A");
                }
                i++;
            }
        }

        if (data[2].equals(OUTPUT_STATUS)) {
            int i = 0;
            while (i < 25) {
                if (i < 3) {
               /* } else if (i < 17) {
                    outputKeepAliveDao.updateOutputStatus(i - 2, data[i]); */
                } else {
                    outputKeepAliveDao.updateOutputStatus(i - 2, data[i].substring(0, 1));
                    outputKeepAliveDao.updateOutputRelayStatus(i - 2, data[i].length() > 1 ? data[i].substring(1) : "N/A");
                }
                i++;
            }
        }
        if (data[2].equals(ALARM_STATUS)) {
            if (data[4].equals("0")) {
                if (data[5].substring(0, 2).equals("11")) {
                    if (Integer.parseInt(data[5].substring(2)) > 3000) {
                        AlarmLogEntity alarmLogEntity = new AlarmLogEntity(alarmLogDao.getLastSno() + 1,
                                data[3].substring(2, 4), data[3].substring(0, 2),
                                alarmArr[Integer.parseInt(data[5].substring(0, 2))] + " - Cell Fouling Error",
                                ApplicationClass.getCurrentTime(),
                                ApplicationClass.getCurrentDate(), "1");
                        List<AlarmLogEntity> alarmEntryList = new ArrayList<>();
                        alarmEntryList.add(alarmLogEntity);
                        updateToAlarmDb(alarmEntryList);
                    } else  {
                        AlarmLogEntity alarmLogEntity = new AlarmLogEntity(alarmLogDao.getLastSno() + 1,
                                data[3].substring(2, 4), data[3].substring(0, 2),
                                alarmArr[Integer.parseInt(data[5].substring(0, 2))]+ " - No Fouling Error",
                                ApplicationClass.getCurrentTime(),
                                ApplicationClass.getCurrentDate(), "0");
                        List<AlarmLogEntity> alarmEntryList = new ArrayList<>();
                        alarmEntryList.add(alarmLogEntity);
                        updateToAlarmDb(alarmEntryList);
                    }
                }

                if (data[5].substring(0, 2).equals("08")) {
                    AlarmLogEntity alarmLogEntity = new AlarmLogEntity(alarmLogDao.getLastSno() + 1,
                            data[3].substring(2, 4), data[3].substring(0, 2),
                            alarmArr[Integer.parseInt(data[5])],
                            ApplicationClass.getCurrentTime(),
                            ApplicationClass.getCurrentDate(), "1");
                    List<AlarmLogEntity> alarmEntryList = new ArrayList<>();
                    alarmEntryList.add(alarmLogEntity);
                    updateToAlarmDb(alarmEntryList);
                } else {
                    // sendPacket("CRC"+SPILT_CHAR+"01"+SPILT_CHAR+"03"+SPILT_CHAR+"1"+SPILT_CHAR);
                }
            } else {
                // sendPacket("CRC"+SPILT_CHAR+"01"+SPILT_CHAR+"03"+SPILT_CHAR+"1"+SPILT_CHAR);
            }
            if (alarmLogDao.getAlarmLogList().size() >= 1000) {
                alarmLogDao.deleteFirstRow();
            }
            if (eventLogDao.getEventLogList().size() >= 1000) {
                eventLogDao.deleteFirstRow();
            }

            switch (data[3].substring(0, 2)) {
                case "IN":
                    sensorType = inputConfigurationDao.getInputType(Integer.parseInt(data[3].substring(2, 4)));
                    break;
                case "OP":
                    sensorType = outputKeepAliveDao.getOutputStatus(Integer.parseInt(data[3].substring(2, 4)));
                    break;
                case "TI":
                    sensorType = "TIMER" + data[3].substring(2, 4);
                    break;
            }
            if (data[4].equals("0")) {
                if (!data[5].substring(0, 2).equals("08") && !data[5].substring(0, 2).equals("11")) {
                    AlarmLogEntity alarmLogEntity = new AlarmLogEntity(alarmLogDao.getLastSno() + 1,
                            data[3].substring(2, 4), data[3].substring(0, 2),
                            alarmArr[Integer.parseInt(data[5])],
                            ApplicationClass.getCurrentTime(),
                            ApplicationClass.getCurrentDate(), "0");
                    List<AlarmLogEntity> alarmEntryList = new ArrayList<>();
                    alarmEntryList.add(alarmLogEntity);
                    updateToAlarmDb(alarmEntryList);
                }
            }
        }
    }

    public void updateToAlarmDb(List<AlarmLogEntity> entryList) {
        alarmLogDao.insert(entryList.toArray(new AlarmLogEntity[0]));
    }

    public void updateToEventDb(List<EventLogEntity> entryList) {
        eventLogDao.insert(entryList.toArray(new EventLogEntity[0]));
    }

    public void updateToTrend(List<TrendEntity> entryList) {
        trendDao.insert(entryList.toArray(new TrendEntity[0]));
    }

    public void trendEntity(int sNo, String hardwareNo, String keepValue, String date, String time, int row) {
        keepValue = Integer.parseInt(hardwareNo) > 25 && Integer.parseInt(hardwareNo) < 34 ? keepValue.split("T")[1] : keepValue;
        TrendEntity trendEntity = new TrendEntity(sNo, hardwareNo, keepValue, date, time, row);
        List<TrendEntity> trendEntities = new ArrayList<>();
        trendEntities.add(trendEntity);
        updateToTrend(trendEntities);
    }

    public void sendPacket(String packet) {
        mAppClass.sendPacket(this, packet);
    }

    @Override
    public void OnDataReceive(String data) { }

    public void collectTrendData() {
        Log.e("TAG", "collectTrendData: ");
        collect();

        new CountDownTimer(60 * 1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                collect();
                start();
            }
        }.start();
    }

    private void collect() {
        try {
            if(!trendDataCollector.isEmpty() && !trendDataCollector.equalsIgnoreCase("00")) {
                String[] data = trendDataCollector.split("\\*")[1].split("\\$");
                if (data[2].equals(INPUT_VOLTAGE)) {
                    int i = 0;
                    int maxRow = trendDao.lastRowNumber() == null ? 1 : trendDao.lastRowNumber() + 1;
                    while (i < 57) {
                        if (data[i + 3].length() > 2) {
                            if (data[i + 3].substring(0, 2).equals("01")) {
                                if(!data[i+3].equals("010")) {
                                    if (data[i + 3].split("\\.")[1].length() >= 1) {
                                        trendEntity(trendDao.getLastSno() + 1, data[i + 3].substring(0, 2),
                                                data[i + 3].split("\\.")[0].substring(2) + "." + data[i + 3].split("\\.")[1].substring(0, 2),
                                                ApplicationClass.getCurrentTrendFormatDate(), ApplicationClass.getCurrentTime(), maxRow);
                                    } else {
                                        trendEntity(trendDao.getLastSno() + 1, data[i + 3].substring(0, 2), data[i + 3],
                                                ApplicationClass.getCurrentTrendFormatDate(), ApplicationClass.getCurrentTime(), maxRow);
                                    }
                                } else {
                                    trendEntity(trendDao.getLastSno() + 1, data[i + 3].substring(0, 2), "0",
                                            ApplicationClass.getCurrentTrendFormatDate(), ApplicationClass.getCurrentTime(), maxRow);
                                }
                            } else {
                                trendEntity(trendDao.getLastSno() + 1, data[i + 3].substring(0, 2),
                                        data[i + 3].substring(2, data[i + 3].length()), ApplicationClass.getCurrentTrendFormatDate(),
                                        ApplicationClass.getCurrentTime(), maxRow);
                            }
                        }
                        i++;
                    }
                }
                trendDataCollector = "00";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}