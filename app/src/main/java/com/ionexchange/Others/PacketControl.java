package com.ionexchange.Others;

import com.ionexchange.BuildConfig;

public class PacketControl {

    /* General */
    public static String SPILT_CHAR = "$", CRC = "1234", ACK = "0", NCK = "1",
            RES_SPILT_CHAR = "\\$",
            DEVICE_PASSWORD = "1234",
            WRITE_PACKET = "0",
            READ_PACKET = "1",
            RES_SUCCESS = "0", RES_FAILED = "1", CONN_TYPE = "0";

    /* Packet Identifier */
    public static String PCK_connectPacket = "00", PCK_panelIpConfig = "01", PCK_target_ip = "02", PCK_GENERAL = "03",
            PCK_INPUT_SENSOR_CONFIG = "04", VIRTUAL_INPUT = "05",
            PCK_OUTPUT_CONFIG = "06", PCK_TIMER_CONFIG = "08", PCK_WEEKLY_CONFIG = "09", PCK_DIAGNOSTIC = "11",
            PCK_SENSORCALIB = "10",

    /* Connect Packet */
    APP_VERSION = BuildConfig.VERSION_NAME, CONNECT_COMMAND = "0", DISCONNECT_COMMAND = "1",
            ADMIN = "0", ENGINEER = "1", USER, OUTPUT_CONTROL_CONFIG = "13",CHARGE_CONTROL_PACKET="15";

    /*ServerPacketID*/
    public static String INPUT_VOLTAGE = "01", OUTPUT_STATUS = "02", ALARM_STATUS = "03";
}
