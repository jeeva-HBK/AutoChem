package com.ionexchange.Others;


import static com.ionexchange.Others.ApplicationClass.Acknowledge;
import static com.ionexchange.Others.ApplicationClass.outputStatusarr;
import static com.ionexchange.Others.PacketControl.ACK;
import static com.ionexchange.Others.PacketControl.CRC;
import static com.ionexchange.Others.PacketControl.INPUT_VOLTAGE;
import static com.ionexchange.Others.PacketControl.OUTPUT_STATUS;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.TCP.endPacket;

import android.content.Context;

import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.WaterTreatmentDb;

public class KeepAlive {
    private Context context;
    WaterTreatmentDb db = null;
    KeepAliveCurrentValueDao keepAliveCurrentValueDao = null;
    OutputKeepAliveDao outputKeepAliveDao;

    public KeepAlive(String data, Context applicationContext) {
        if (db == null) {
            db = WaterTreatmentDb.getDatabase(applicationContext);
        }
        if (keepAliveCurrentValueDao == null) {
            keepAliveCurrentValueDao = db.keepAliveCurrentValueDao();
            outputKeepAliveDao = db.outputKeepAliveDao();
        }
        if (data != null) {
            spiltData(data.split("\\*")[1].split(RES_SPILT_CHAR));
        }
    }

    void spiltData(String[] data) {
        if (data[2].equals(INPUT_VOLTAGE)) {
            Acknowledge = endPacket + SPILT_CHAR + CRC + SPILT_CHAR + "007" + SPILT_CHAR + INPUT_VOLTAGE + SPILT_CHAR + ACK + SPILT_CHAR + endPacket;
            int i = 0;
            while (i < 10) {
                if (data[i + 4].length() > 2) {
                    if (Integer.parseInt(data[i + 4].substring(0, 2)) > 33 && Integer.parseInt(data[i + 4].substring(0, 2)) < 50) { // DIGITAL & TANK
                        if (data[i + 4].substring(2, data[i + 4].length()).equals("0")) {
                            keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 4].substring(0, 2)), "OPEN");
                        } else {
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
            while (i < 22) {
                outputKeepAliveDao.updateOutputStatus(i, outputStatusarr[Integer.parseInt(i + data[4])]);
                i++;
            }
        }
    }
}

