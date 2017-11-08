package com.tomaszkopacz.pulseoxymeter.btservice;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by tomaszkopacz on 08.08.17.
 */

public class DeviceConnection extends Thread {

    private final BluetoothSocket socket;
    private final BluetoothDevice device;
    private final UUID uuid;

    public DeviceConnection(BluetoothDevice device, UUID uuid){
        BluetoothSocket tmp = null;
        this.device = device;
        this.uuid = uuid;

        try {
            tmp = this.device.createRfcommSocketToServiceRecord(uuid);
            Log.d("myConstructor", "hello1");

        } catch (IOException e) {
            e.printStackTrace();
        }

        socket = tmp;
        Log.d("myConstructor", "end: socket: " + socket + " device: " + device.getName());
    }

    public void run(){
        try {
            socket.connect();
            Log.d("myrun()", "connection");

        } catch (IOException e) {
            try {
                Log.d("myrun()", "no connection");
                socket.close();

            } catch (IOException e1) {
                Log.d("myrun()", "cannot close socket");
            }
            return;
        }
        Log.d("myDevice connection", "sparowano!!!");
    }

    public void cancel(){
        try {
            socket.close();

        } catch (IOException e) {
        }
    }
}
