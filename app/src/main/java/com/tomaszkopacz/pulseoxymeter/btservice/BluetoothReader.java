package com.tomaszkopacz.pulseoxymeter.btservice;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tomaszkopacz on 15.11.17.
 */

public class BluetoothReader extends Thread{

    private final BluetoothSocket mBluetoothSocket;
    private final InputStream mInputStream;

    public BluetoothReader(BluetoothSocket socket){
        this.mBluetoothSocket = socket;

        InputStream tempInputStream = null;
        try {
            tempInputStream = socket.getInputStream();
        } catch (IOException e) {}

        mInputStream = tempInputStream;
    }

    public void read() throws Exception{

        byte startByte;
        byte byte1;
        byte byte2;
        byte byte3;
        byte byte4;
        byte byte5;
        byte byte6;
        byte byte7;
        byte byte8;

        for (int i = 0; i < 100; i++) {
            while (true) {
                startByte = waitForNextByte();
                if (startByte == 1) {
                    byte1 = waitForNextByte();
                    byte2 = waitForNextByte();
                    byte3 = waitForNextByte();
                    byte4 = waitForNextByte();
                    byte5 = waitForNextByte();
                    byte6 = waitForNextByte();
                    byte7 = waitForNextByte();
                    byte8 = waitForNextByte();
                    break;
                }
            }
            Log.d("TomaszKopacz", "Byte 3 (waveform): " + byte3);
        }


    }

    private byte waitForNextByte() throws IOException {
        return (byte) mInputStream.read();
    }
}
