package com.tomaszkopacz.pulseoxymeter.btservice;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by tomaszkopacz on 08.08.17.
 */

public class BluetoothConnector {

    private BluetoothDevice device;
    private BluetoothSocket mBluetoothSocket;

    public static final String SOCKET_UUID = "00001101-0000-1000-8000-00805f9b34fb";

    public static int state;
    public static final int NONE = -1;
    public static final int  DISCONNECTED = -10;
    public static final int CONNECTING = 0;
    public static final int CONNECTED = 10;


    public BluetoothConnector(BluetoothDevice device){
        this.device = device;
        mBluetoothSocket = null;

        state = NONE;
    }

    /**
     * Opens bluetooth socket and connects to a device.
     */
    public void connectToDevice(){

        state = CONNECTING;

        try {
            UUID mUuid = UUID.fromString(SOCKET_UUID);
            mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(mUuid);
            mBluetoothSocket.connect();
            state = CONNECTED;

        } catch (Exception e) {
            try {
                mBluetoothSocket.close();

            } catch (IOException e1) {}

            state = DISCONNECTED;
        }
    }

    /**
     * Closes socket connection.
     */
    public void closeConnection(){
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();

            state = DISCONNECTED;

        } catch (IOException e) {}
    }

    /**
     * Returns bluetooth connection socket.
     * @return BluetoothSocket
     */
    public BluetoothSocket getSocket(){
        return this.mBluetoothSocket;
    }

    public int getState(){
        return state;
    }

    public void setState(int state){
        this.state = state;
    }

}
