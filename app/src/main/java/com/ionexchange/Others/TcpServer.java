package com.ionexchange.Others;


import static com.ionexchange.Others.ApplicationClass.Acknowledge;
import static com.ionexchange.Others.ApplicationClass.Packet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ionexchange.Interface.DataReceiveCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpServer extends Service {


    public static final String START_SERVER = "startserver";
    public static final String STOP_SERVER = "stopserver";
    public static final int SERVERPORT = 5000;
    public static final String ACTION_MyIntent = "com.ionExchange.server";
    Thread serverThread;
    ServerSocket serverSocket;
    BufferedReader inputReader;
    Socket socket;
    int charsRead;
    private static final String TAG = "TestTCPServer";


    public TcpServer() {
    }

    //called when the services starts
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(START_SERVER)) {
            //start your server thread from here
            this.serverThread = new Thread(new ServerThread());
            this.serverThread.start();
        }

        //configures behaviour if service is killed by system
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("TAG", ": " + intent);
        return null;
    }

    class ServerThread implements Runnable {
        public void run() {
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = serverSocket.accept();
                    socket.setReuseAddress(true);
                    socket.setKeepAlive(true);
                    socket.setTcpNoDelay(true);
                    socket.setSoLinger(true, 1);
                    clientReceivedData(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                    socket = null;
                }
            }
        }
    }

    public void acknowledgement() {
        PrintWriter out0;
        try {
            out0 = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(
                            socket.getOutputStream())), true);

            out0.println(Acknowledge);
            Log.e(TAG, "Sent -->  " + Acknowledge);

            return;
        } catch (Exception e) {
            intentMessage("sendCatch");
            Log.d("Send Message", e.getMessage());
        }
        intentMessage("pckError");
        Log.d("Send Message", "Packet Error");
        // close();
    }


    void clientReceivedData(Socket socket) {
        try {
            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            char[] buffer = new char[2048];
            charsRead = 0;
            while ((charsRead = inputReader.read(buffer)) != -1) {
                String message = new String(buffer).substring(0, charsRead);
                if (!message.isEmpty()) {
                    intentMessage(message);
                    Log.e("TCP_SERVER", "Received <--  " + message);
                    new KeepAlive(message, getApplicationContext());
                    acknowledgement();
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


    private void intentMessage(String message) {
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_MyIntent);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra("received_server_data", message);
        sendBroadcast(intentResponse);
    }

    public void close() {
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }
    }

    public void send(String packet) {
        PrintWriter out0;
        try {
            out0 = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);

            out0.println(packet);

        } catch (Exception e) {
            //   intentMessage("sendCatch");
            Log.d("Send Message", e.getMessage());
        }
        //intentMessage("pckError");
        // Log.d("Send Message", "Packet Error");
    }


}
