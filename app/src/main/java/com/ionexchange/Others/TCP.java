package com.ionexchange.Others;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.ionexchange.Others.ApplicationClass.Packet;
import static com.ionexchange.Others.ApplicationClass.mIPAddress;
import static com.ionexchange.Others.ApplicationClass.mPortNumber;


public class TCP extends IntentService {

    public static final String ACTION_MyIntentService = "com.ionExchange.RESPONSE";
    private static final String TAG = "TCP";
    public static Socket socketDevice = null;
    public static BufferedReader _inputSteam;

    /* START : END */
    public String startPacket = "{*", endPacket = "*}";

    public TCP() {
        super("Test the service");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        String stringPassedToThisService = intent.getStringExtra("dataPacket");

        if (stringPassedToThisService != null) {

            new Thread(new SendToDevice(stringPassedToThisService)).start();
        }
    }

    public void stop() {
        SendToDevice STD = new SendToDevice(null);
        STD.close();
    }

    private void intentMessage(String message) {
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_MyIntentService);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra("received_data", message);
        sendBroadcast(intentResponse);
    }

    class SendToDevice implements Runnable {
        SendToDevice(String command) {
            Packet = framePacket(command);
        }

        @Override
        public void run() {

            try {
                if (Connect()) {
                    if (send()) {
                        Receive();
                    }
                }
            } catch (Exception e1) {
                intentMessage("No Device");
                Log.d("Send Message", "No Device");
            }

        }

        public boolean send() {
            PrintWriter out0;
            Log.d("Send Message", Packet);
            try {
                out0 = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(
                                socketDevice.getOutputStream())), true);

                out0.println(Packet);

                Log.e(TAG, "Sent -->  " + Packet);

                return true;
            } catch (Exception e) {
                intentMessage("sendCatch");
                Log.d("Send Message", e.getMessage());
            }
            intentMessage("pckError");
            Log.d("Send Message", "Packet Error");
            // close();
            return false;
        }

        public void Receive() {
            try {
                char[] buffer = new char[2048];
                int charsRead = 0;
                while ((charsRead = _inputSteam.read(buffer)) != -1) {
                    String message = new String(buffer).substring(0, charsRead);
                    if (!message.isEmpty()) {
                        intentMessage(message);
                        Log.e(TAG, "Received -->  " + message);
                    } else {
                        Log.d("Receive Error Message", message);
                    }
                }
            } catch (java.io.InterruptedIOException e) {
                intentMessage("timeOut");
                Log.d("Receive Message", e.getMessage());
                Log.d("Receive Message", "timeOut");
            } catch (UnknownHostException e1) {
                intentMessage("UnknownHostException");
                Log.d("Receive Message", e1.getMessage());
                Log.d("Receive Message", "UnknownHostException");

            } catch (IOException e1) {
                intentMessage("restart");
                Log.d("Receive Message", e1.getMessage());
                Log.d("Receive Message", "restart");
            }
            close();
        }

        public String framePacket(String packet) {
            return startPacket + packet + endPacket;
        }

        public boolean Connect() {

            try {
                if (socketDevice == null) {
                    socketDevice = new Socket();
                    socketDevice.connect(new InetSocketAddress(
                            mIPAddress, mPortNumber), 30000);

                    _inputSteam = new BufferedReader(new InputStreamReader(socketDevice.getInputStream()));
                    socketDevice.setKeepAlive(true);
                    socketDevice.setSoLinger(true, 1);
                    Log.e(TAG, "Device Connected");
                }
                return true;
            } catch (UnknownHostException e1) {
                intentMessage("UnknownHostException");
                Log.d("Communication", e1.getMessage());
                socketDevice = null;
            } catch (IOException e1) {
                intentMessage("FailedToConnect");
                Log.d("Communication", e1.getMessage());
                socketDevice = null;
            } catch (Exception e) {
                socketDevice = null;
            }
            return false;
        }

        public void close() {
            if (socketDevice != null) {
                if (socketDevice.isConnected()) {
                    try {
                        socketDevice.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    socketDevice = null;
                }
            }
        }
    }
}