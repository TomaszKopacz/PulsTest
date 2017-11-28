package com.tomaszkopacz.pulseoxymeter.btservice;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothDataListener;
import com.tomaszkopacz.pulseoxymeter.model.CMSData;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tomaszkopacz on 15.11.17.
 */

public class BluetoothReader{

    private Activity activity;
    private BluetoothDataListener listener;

    private final BluetoothSocket mBluetoothSocket;
    private final InputStream mInputStream;

    public BluetoothReader(
            Activity activity,
            BluetoothDataListener listener,
            BluetoothSocket socket){

        this.activity = activity;
        this.listener = listener;
        this.mBluetoothSocket = socket;

        InputStream tempInputStream = null;
        try {
            tempInputStream = socket.getInputStream();
        } catch (IOException e) {}

        mInputStream = tempInputStream;
    }

    public void read(){
        Thread readThread = new Thread(readRunnable);
        readThread.start();
    }

    Runnable readRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                getBytes();
            } catch (Exception e) {
                Log.e("TOMASZ KOPACZ", "Can't get data");
            }
        }
    };

    private void getBytes() throws Exception{

        byte startByte;
        byte byte1;
        byte byte2;
        byte byte3;
        byte byte4;
        byte byte5;
        byte byte6;
        byte byte7;
        byte byte8;

        CMSData dataPackage = new CMSData();

        while (true) {
            startByte = waitForNextByte();

            if (startByte == 1) {

                byte1 = waitForNextByte();
                byte2 = waitForNextByte();
                dataPackage.setWaveformByte(waitForNextByte());
                byte4 = waitForNextByte();
                dataPackage.setPulseByte(waitForNextByte());
                dataPackage.setSaturationByte(waitForNextByte());
                byte7 = waitForNextByte();
                byte8 = waitForNextByte();

                listener.bluetoothDataSent(dataPackage);
                break;
            }
        }
    }

    private byte waitForNextByte() throws IOException {
        return (byte) mInputStream.read();
    }
}
