package com.tomaszkopacz.pulseoxymeter.btservice;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothListener;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by tomaszkopacz on 08.08.17.
 */

public class BluetoothConnector {

    private Activity activity;
    private BluetoothListener listener;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;

    private static final String SOCKET_UUID = "00001101-0000-1000-8000-00805f9b34fb";
    private static final int CONNECT_PERIOD = 10000;

    public static final int NONE = -100;
    public static final int DISCONNECTED = 100;
    public static final int CONNECTED = 110;

    private int state = NONE;

    public BluetoothConnector(Activity activity, BluetoothListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    /**
     * Tries to connect to bluetooth device.
     * Connection lasts 5 seconds in maximum.
     * In this time, if connection is made state CONNECTED is sent.
     * If connection fails, state DISCONNECTED is sent.
     * @param device
     */
    public void connect(BluetoothDevice device){

        closeConnection();

        state = NONE;

        if (device == null)
            return;

        this.mBluetoothDevice = device;

        Thread connectThread = new Thread(startRunnable);
        Handler stopHandler = new Handler();

        stopHandler.postDelayed(stopRunnable, CONNECT_PERIOD);
        connectThread.start();
    }

    private Runnable startRunnable = new Runnable() {
        @Override
        public void run() {

            try {

                UUID mUuid = UUID.fromString(SOCKET_UUID);
                mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(mUuid);
                mBluetoothSocket.connect();

                state = CONNECTED;
                activity.runOnUiThread(uiRunnable);


            } catch (Exception e) {
                state = DISCONNECTED;
                activity.runOnUiThread(uiRunnable);
            }

        }
    };

    private Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {

            if (mBluetoothSocket.isConnected())
                return;

            try {
                mBluetoothSocket.close();
                state = DISCONNECTED;
                activity.runOnUiThread(uiRunnable);

            } catch (IOException e) {
                state = DISCONNECTED;
                activity.runOnUiThread(uiRunnable);
            }
        }
    };

    private Runnable uiRunnable = new Runnable() {
        @Override
        public void run() {
            listener.btEventAppears(null, state);
        }
    };

    /**
     * Closes socket connection.
     */
    public void closeConnection(){
        try {
            if (mBluetoothSocket != null && mBluetoothSocket.isConnected()){
                mBluetoothSocket.close();
                mBluetoothSocket = null;
            }

            state = DISCONNECTED;

        } catch (IOException e) {
            state = DISCONNECTED;
        }
    }

    /**
     * Returns bluetooth connection socket.
     * @return BluetoothSocket
     */
    public BluetoothSocket getSocket(){
        return this.mBluetoothSocket;
    }

}
