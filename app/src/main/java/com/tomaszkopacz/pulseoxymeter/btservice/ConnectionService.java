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
import com.tomaszkopacz.pulseoxymeter.model.CMSData;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by tomaszkopacz on 28.11.17.
 */

public class ConnectionService extends Service {

    private final IBinder binder = new LocalBinder();

    private BluetoothCallbacks callback;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private InputStream mInputStream;

    private static final String SOCKET_UUID = "00001101-0000-1000-8000-00805f9b34fb";
    private static final int CONNECT_PERIOD = 5000;

    private boolean readEnabled = false;


    /*==============================================================================================
                                        INITIALIZATION
    ==============================================================================================*/

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


    /*==============================================================================================
                                        CONNECTION
    ==============================================================================================*/

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

    /*==============================================================================================
                                        READING DATA
    ==============================================================================================*/

    public void read(){
        InputStream tempInputStream = null;
        mInputStream = null;

        try {
            tempInputStream = mBluetoothSocket.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mInputStream = tempInputStream;
        Thread readThread = new Thread(readRunnable);
        readThread.start();
    }

    Runnable readRunnable = new Runnable() {
        @Override
        public void run() {
            readEnabled = true;
            getBytes();

        }
    };

    private void getBytes() {

        byte byte0;
        byte byte1;
        byte byte2;
        byte byte3;
        byte byte4;
        byte byte5;
        byte byte6;
        byte byte7;
        byte byte8;

        CMSData dataPackage = new CMSData();

        while (readEnabled) {
            byte0 = waitForNextByte();
            byte1 = waitForNextByte();
            byte2 = waitForNextByte();
            dataPackage.setWaveformByte(waitForNextByte());
            byte4 = waitForNextByte();
            dataPackage.setPulseByte(waitForNextByte());
            dataPackage.setSaturationByte(waitForNextByte());
            byte7 = waitForNextByte();
            byte8 = waitForNextByte();

            callback.onDataIncome(dataPackage);
        }
    }

    private byte waitForNextByte() {
        try {
            return (byte) mInputStream.read();

        } catch (IOException e) {

            readEnabled = false;
            return 0;
        }
    }
}