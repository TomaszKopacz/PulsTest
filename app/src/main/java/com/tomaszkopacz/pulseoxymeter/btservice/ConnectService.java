package com.tomaszkopacz.pulseoxymeter.btservice;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothCallbacks;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by tomaszkopacz on 09.12.17.
 * Service for connectin to a device.
 */

public class ConnectService extends Service {

    private IBinder binder = new ConnectBinder();
    private BluetoothCallbacks callback;

    private BluetoothDevice device;
    private BluetoothSocket socket;

    private static final String SOCKET_UUID = "00001101-0000-1000-8000-00805f9b34fb";

    //10 sec
    private static final int CONNECT_PERIOD = 10000;

    private boolean connected = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ConnectBinder extends Binder {

        public ConnectService getService() {
            return ConnectService.this;
        }
    }

    /**
     * Registers listener for bluetooth connection events.
     * @param callback
     */
    public void registerCallback(BluetoothCallbacks callback) {
        this.callback = callback;
    }

    /*==============================================================================================
                                        CONNECTION
    ==============================================================================================*/

    /**
     * Attempts to create connection with bluetooth device.
     * Attempt lasts maximum 10 seconds.
     * @param device
     */
    public void connect(BluetoothDevice device) {

        if (device == null)
            return;
        this.device = device;

        Thread connectThread = new Thread(connectRunnable);
        Handler stopHandler = new Handler();

        stopHandler.postDelayed(stopConnectRunnable, CONNECT_PERIOD);
        connectThread.start();
    }

    private Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {

            try {
                UUID mUuid = UUID.fromString(SOCKET_UUID);
                socket = device.createInsecureRfcommSocketToServiceRecord(mUuid);
                socket.connect();

                connected = true;
                callback.onConnectionOpenRequest();

            } catch (Exception e) {
                connected = false;
                callback.onConnectionCloseRequest();
            }
        }
    };

    private Runnable stopConnectRunnable = new Runnable() {
        @Override
        public void run() {
            if (socket != null) {
                if (connected)
                    return;
                else
                    try {
                        socket.close();
                        callback.onConnectionCloseRequest();

                    } catch (IOException e) {
                        callback.onConnectionCloseRequest();
                    }
            }

        }
    };

    /**
     * Closes bluetooth connection.
     */
    public boolean closeConnection(BluetoothSocket btSocket) {

        try {
            if (btSocket != null && btSocket.isConnected()) {
                btSocket.getInputStream().close();
                btSocket.getOutputStream().close();
                btSocket.close();

            }
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Returns socket.
     * @return
     */
    public BluetoothSocket getSocket(){
        return socket;
    }
}
