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
 */

public class ConnectService extends Service {

    private IBinder binder = new ConnectBinder();
    private BluetoothCallbacks callback;

    private BluetoothDevice device;
    private BluetoothSocket socket;

    private static final String SOCKET_UUID = "00001101-0000-1000-8000-00805f9b34fb";
    private static final int CONNECT_PERIOD = 10000;

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

    public void registerCallback(BluetoothCallbacks callback) {
        this.callback = callback;
    }

    /*==============================================================================================
                                        CONNECTION
    ==============================================================================================*/

    /**
     * Attempts to create connection with bluetooth device.
     * Attempt last maximum 10 seconds.
     * @param device
     */
    public void connect(BluetoothDevice device) {

        closeConnection();

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
                callback.onConnectionOpenRequest();

            } catch (Exception e) {
                callback.onConnectionCloseRequest();
            }
        }
    };

    private Runnable stopConnectRunnable = new Runnable() {
        @Override
        public void run() {
            if (socket != null && socket.isConnected())
                return;

            callback.onConnectionCloseRequest();
        }
    };

    /**
     * Closes bluetooth connection.
     */
    public void closeConnection() {

        try {
            if (socket != null && socket.isConnected()) {
                socket.getInputStream().close();
                socket.getOutputStream().close();
                socket.close();
                socket = null;
            }

        } catch (IOException e) {
            Log.d("TomaszKopacz", "stop connection failed");

        }
    }

    public BluetoothSocket getSocket(){
        return socket;
    }
}
