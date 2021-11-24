package com.ionexchange.Others;


import static com.ionexchange.Others.ApplicationClass.Acknowledge;
import static com.ionexchange.Others.PacketControl.ACK;
import static com.ionexchange.Others.PacketControl.CRC;
import static com.ionexchange.Others.PacketControl.INPUT_VOLTAGE;
import static com.ionexchange.Others.PacketControl.OUTPUT_STATUS;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.TCP.endPacket;
import static com.ionexchange.Others.TCP.startPacket;

import android.content.Context;
import android.util.Log;

import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.WaterTreatmentDb;

// created by Silambu
public class KeepAlive {
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
            Acknowledge = startPacket + SPILT_CHAR + CRC + SPILT_CHAR + "007" + SPILT_CHAR + INPUT_VOLTAGE + SPILT_CHAR + ACK + SPILT_CHAR + endPacket;
            /* int i = 0;
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
                        } else if (data[i + 4].substring(2, data[i + 4].length()).equals("2")){
                            keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 4].substring(0, 2)), "CLOSE");
                        }
                    } else {
                        keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 4].substring(0, 2)), data[i + 4].substring(2, data[i + 4].length()));
                    }
                } else {
                    keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 4].substring(0, 2)), "N/A");
                }
                i++;
            } */
            int i =0;
            while(i<57){
                if (data[i + 3].length() > 2) {
                    if (Integer.parseInt(data[i + 3].substring(0, 2)) > 33 && Integer.parseInt(data[i + 3].substring(0, 2)) < 50) { // DIGITAL & TANK
                        if (data[i + 3].substring(2, data[i + 3].length()).equals("1")) {
                            keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 3].substring(0, 2)), "OPEN");
                        } else if (data[i + 3].substring(2, data[i + 3].length()).equals("2")){
                            keepAliveCurrentValueDao.updateCurrentValue(Integer.parseInt(data[i + 3].substring(0, 2)), "CLOSE");
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
            while (i <= 21) {
                if (data[3 + i].length() <= 1) {
                    outputKeepAliveDao.updateOutputStatus(i+1, data[3 + i]);
                    Log.e("outputControl", "spiltData: "+(i+1) +"!"+data[3+i] );
                }
                if (data[3+i].length() > 1) {
                    if (data[3+i].substring(0,2).equals("10")){
                        outputKeepAliveDao.updateOutputStatus(i+1, data[3 + i].substring(0,2));
                        outputKeepAliveDao.updateOutputRelayStatus(i+1, data[3 + i].substring(2));
                    }else {
                        outputKeepAliveDao.updateOutputStatus(i+1, data[3 + i].substring(0,1));
                        outputKeepAliveDao.updateOutputRelayStatus(i+1, data[3 + i].substring(1));
                    }
                }
                i++;
            }
        }
    }
}