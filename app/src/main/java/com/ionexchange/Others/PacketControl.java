package com.ionexchange.Others;

import com.ionexchange.BuildConfig;

public class PacketControl {

    /* General */
    public static String SPILT_CHAR = "#",
            DEVICE_PASSWORD = "1234",
            WRITE_PACKET = "0",
            READ_PACKET = "1";

    /* Packet Identifier */
    public static String PCK_connectPacket = "00", PCK_panelIpConfig = "01", PCK_target_ip = "02",

    /* Connect Packet */
    APP_VERSION = BuildConfig.VERSION_NAME, CONNECT_COMMAND = "0", DISCONNECT_COMMAND = "1",
            ADMIN = "0", ENGINEER = "1", USER;
}
