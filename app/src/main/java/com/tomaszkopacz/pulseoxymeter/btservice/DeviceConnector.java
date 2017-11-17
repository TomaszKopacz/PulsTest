package com.tomaszkopacz.pulseoxymeter.btservice;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by tomaszkopacz on 08.08.17.
 */

public class DeviceConnector extends Thread{

    private BluetoothDevice device;
    private BluetoothSocket mBluetoothSocket;

    public DeviceConnector(BluetoothDevice device){
        this.device = device;
        mBluetoothSocket = null;
    }

    /**
     * Opens bluetooth socket and connects to a device.
     */
    public boolean connectToDevice(){

        //open socket and connect
        try {
            UUID mUuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(mUuid);
            mBluetoothSocket.connect();

            return true;

        //unable to connect
        } catch (Exception e) {
            try {
                mBluetoothSocket.close();

            } catch (IOException e1) {}

            return false;
        }
    }

    /**
     * Closes socket connection.
     */
    public void closeConnection(){
        try {
            mBluetoothSocket.close();

        } catch (IOException e) {}
    }

    /**
     * Returns bluetooth connection socket.
     * @return BluetoothSocket
     */
    public BluetoothSocket getSocket(){
        return this.mBluetoothSocket;
    }

}
