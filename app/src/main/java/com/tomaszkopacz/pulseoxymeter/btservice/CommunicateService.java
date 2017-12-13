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

    private Timer timer;
    private HoldCommunicationTask keepCommunicatingTask;
    private static final int KEEP_COMMUNICATING_DELAY = 4500;
    private static final byte KEEP_COMMUNICATING_BYTE = (byte) 0xa7;

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

    public void unbind(){

        if (readThread != null)
            readThread.interrupt();

        communicationEnabled = false;

        if (timer != null && keepCommunicatingTask != null){
            keepCommunicatingTask.cancel();
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    /*==============================================================================================
                                        COMMUNICATION
    ==============================================================================================*/

    /**
     * Holds communication with bluetooth device by sending request every 5 seconds.
     * @param socket
     */
    public void holdCommunication(BluetoothSocket socket){

        OutputStream tempOutputStream = null;
        mOutputStream = null;

        try {
            tempOutputStream = socket.getOutputStream();

        } catch (IOException e) {
            Log.d("TomaszKopacz", "Cannot write byte");
        }

        mOutputStream = tempOutputStream;

        timer = new Timer();
        keepCommunicatingTask = new HoldCommunicationTask();
        timer.schedule(keepCommunicatingTask, 0, KEEP_COMMUNICATING_DELAY);
    }

    private class HoldCommunicationTask extends TimerTask {

        @Override
        public void run() {
            try {
                mOutputStream.write(KEEP_COMMUNICATING_BYTE);

            } catch (IOException e) {
                Log.d("TomaszKopacz", "write byte failed");
            }
        }
    }

    /**
     * Reads data from device. Sends a special byte 0xa7 to hold sending data from device.
     */
    public void read(BluetoothSocket socket){

        InputStream tempInputStream = null;
        mInputStream = null;

        try {
            tempInputStream = socket.getInputStream();

        } catch (IOException e) {
        }

        mInputStream = tempInputStream;

        readThread = new Thread(readRunnable);
        readThread.start();
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

        while (communicationEnabled) {
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
            byte readByte = (byte) mInputStream.read();
            return readByte;

        } catch (IOException e) {
            communicationEnabled = false;
            return 0;
        }
    }

    public void stopReading(){
        communicationEnabled = false;
        callback.onConnectionCloseRequest();
    }
}
