package com.tomaszkopacz.pulseoxymeter.btservice;

import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothCallbacks;
import com.tomaszkopacz.pulseoxymeter.model.CMSData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tomaszkopacz on 09.12.17.
 */

public class CommunicateService extends Service {

    private IBinder binder = new CommunicateService.CommunicateBinder();
    private BluetoothCallbacks callback;

    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private Thread readThread;

    private static Timer timer;
    private static HoldCommunicationTask keepCommunicatingTask;
    private static final int KEEP_COMMUNICATING_DELAY = 4500;

    private boolean communicationEnabled = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class CommunicateBinder extends Binder{

        public CommunicateService getService(){
            return CommunicateService.this;
        }
    }

    public void registerCallback(BluetoothCallbacks callback) {
        this.callback = callback;
    }

    /*==============================================================================================
                                        COMMUNICATION
    ==============================================================================================*/

    /**
     * Holds communication with bluetooth device by sending request every 5 seconds.
     * @param socket
     */
    public void holdCommunication(BluetoothSocket socket){


        try {
            mOutputStream = socket.getOutputStream();

            timer = new Timer();
            keepCommunicatingTask = new HoldCommunicationTask();
            timer.schedule(keepCommunicatingTask, 0, KEEP_COMMUNICATING_DELAY);

        } catch (IOException e) {
        }
    }

    public void stopHoldingCommunication(){

        if (readThread != null)
            readThread.interrupt();

        keepCommunicatingTask.cancel();
        timer.cancel();
        timer.purge();

        stopReading();
    }

    private class HoldCommunicationTask extends TimerTask {

        @Override
        public void run() {
            writeCommand();
        }
    }

    private void writeCommand(){
        writeByte((byte) 0x7D);
        writeByte((byte) 0x81);
        writeByte((byte) 0xAF);

        for (int i = 0; i < 6; i++)
            writeByte((byte) 0x80);
    }

    private void writeByte(byte hex){
        byte[] buffer = new byte[1];
        buffer[0] = hex;
        try {
            mOutputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads data from device. Sends a special byte 0xa7 to hold sending data from device.
     */
    public boolean read(BluetoothSocket socket){

        InputStream tempInputStream;
        mInputStream = null;


        try {
            tempInputStream = socket.getInputStream();

        } catch (IOException e) {
            return false;
        }

        mInputStream = tempInputStream;

        readThread = new Thread(readRunnable);
        readThread.start();

        return true;
    }

    Runnable readRunnable = new Runnable() {
        @Override
        public void run() {
            communicationEnabled = true;
            callback.onConnectionOpenRequest();
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

        while (true) {
            byte0 = waitForNextByte();
            byte1 = waitForNextByte();
            byte2 = waitForNextByte();
            dataPackage.setWaveformByte(waitForNextByte());
            byte4 = waitForNextByte();
            dataPackage.setPulseByte(waitForNextByte());
            dataPackage.setSaturationByte(waitForNextByte());
            byte7 = waitForNextByte();
            byte8 = waitForNextByte();

            if (communicationEnabled)
                callback.onDataIncome(dataPackage);
            else break;
        }
    }

    private byte waitForNextByte() {
        try {
            byte readByte = (byte) mInputStream.read();
            return readByte;

        } catch (IOException e) {
            communicationEnabled = false;
            return (byte) 128;
        }
    }

    public void stopReading(){
        communicationEnabled = false;
        callback.onConnectionCloseRequest();
    }
}
