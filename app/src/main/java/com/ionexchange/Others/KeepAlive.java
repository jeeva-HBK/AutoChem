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

import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.WaterTreatmentDb;

//created by Silambu
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
            }
        }
        if (data[2].equals(OUTPUT_STATUS)) {
            int i = 0;
            while (i <= 21) {
                if (data[3 + i].length() <= 1) {
                    outputKeepAliveDao.updateOutputStatus(i+1, data[3 + i]);
                }
                if (data[3+i].length() > 1){
                    if (data[3+i].substring(0,2).equals("10")) {
                        outputKeepAliveDao.updateOutputStatus(i+1, data[3 + i].substring(0,2));
                        outputKeepAliveDao.updateOutputRelayStatus(i+1, data[3 + i].substring(2));
                    } else {
                        outputKeepAliveDao.updateOutputStatus(i+1, data[3 + i].substring(0,1));
                        outputKeepAliveDao.updateOutputRelayStatus(i+1, data[3 + i].substring(1));
                    }
                }
                i++;
            }
        }
    }
}

// {*1200$$01$4$410$420$430$440$450$460$470$480$490$500*}
// {*1200$$01$0$010$020$030$04200.00$050.000000$060$070$080$090$100*}{*1200$$01$1$110$120$130$140$150.0000$160.0000$170.0000$180$195.6272$200.0000*}{*1200$$01$2$210$220$230$240$250$260.00$270$280$290$300*}{*1200$$01$3$310$320$330$341$350$360$370$380$390$400*}{*1200$$01$4$410$420$430$440$450$460$470$480$490$500*}