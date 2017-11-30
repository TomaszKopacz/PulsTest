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
 * Created by tomaszkopacz on 28.11.17.
 */

public class ConnectionService extends Service {

    private final IBinder binder = new LocalBinder();

    private BluetoothCallbacks callback;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;

    private static final String SOCKET_UUID = "00001101-0000-1000-8000-00805f9b34fb";
    private static final int CONNECT_PERIOD = 5000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {

        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    public void registerCallback(BluetoothCallbacks callback) {
        this.callback = callback;
    }

    public void connect(BluetoothDevice device) {
        closeConnection();

        if (device == null)
            return;

        this.mBluetoothDevice = device;

        Thread connectThread = new Thread(connectRunnable);
        Handler stopHandler = new Handler();

        stopHandler.postDelayed(stopConnectRunnable, CONNECT_PERIOD);
        connectThread.start();
    }

    public void closeConnection() {

        try {
            if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
                mBluetoothSocket.getInputStream().close();
                mBluetoothSocket.getOutputStream().close();
                mBluetoothSocket.close();
                mBluetoothSocket = null;
            }

        } catch (IOException e) {
        }
    }

    private Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                UUID mUuid = UUID.fromString(SOCKET_UUID);
                mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(mUuid);
                mBluetoothSocket.connect();
                callback.onConnectionOpenRequest();

            } catch (Exception e) {
            }
        }
    };

    private Runnable stopConnectRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothSocket.isConnected())
                return;

            try {
                mBluetoothSocket.getInputStream().close();
                mBluetoothSocket.getOutputStream().close();
                mBluetoothSocket.close();
                callback.onConnectionCloseRequest();

            } catch (IOException e) {
            }
        }
    };
}
