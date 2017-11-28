package com.tomaszkopacz.pulseoxymeter.btservice;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothListener;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by tomaszkopacz on 28.11.17.
 */

public class BluetoothConnector {

    private Activity activity;
    private BluetoothListener listener;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;

    private static final String SOCKET_UUID = "00001101-0000-1000-8000-00805f9b34fb";
    private static final int CONNECT_PERIOD = 5000;

    public static final int CONNECTED = 100;
    public static final int DISCONNECTED = -100;

    private int state = DISCONNECTED;

    public BluetoothConnector(Activity activity, BluetoothListener listener){
        this.activity = activity;
        this.listener = listener;

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        activity.registerReceiver(mReceiver, filter1);
        activity.registerReceiver(mReceiver, filter2);
        activity.registerReceiver(mReceiver, filter3);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                state = CONNECTED;
                activity.runOnUiThread(uiRunnable);

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                state = DISCONNECTED;
                activity.runOnUiThread(uiRunnable);

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                state = DISCONNECTED;
                activity.runOnUiThread(uiRunnable);
            }
        }
    };

    private Runnable uiRunnable = new Runnable() {
        @Override
        public void run() {
            listener.btEventAppears(null, state);
        }
    };

    public void connect(BluetoothDevice device){

        closeConnection();

        if (device == null)
            return;

        this.mBluetoothDevice = device;

        Thread connectThread = new Thread(conStartedRunnable);
        Handler stopHandler = new Handler();

        stopHandler.postDelayed(stopConRunnable, CONNECT_PERIOD);
        connectThread.start();
    }

    private Runnable conStartedRunnable = new Runnable() {
        @Override
        public void run() {

            try {

                UUID mUuid = UUID.fromString(SOCKET_UUID);
                mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(mUuid);
                mBluetoothSocket.connect();

            } catch (Exception e) {
            }
        }
    };

    private Runnable stopConRunnable = new Runnable() {
        @Override
        public void run() {

            if (mBluetoothSocket.isConnected())
                return;

            try {
                mBluetoothSocket.getInputStream().close();
                mBluetoothSocket.getOutputStream().close();
                mBluetoothSocket.close();

            } catch (IOException e) {
            }
        }
    };

    public void closeConnection(){
        try {
            if (mBluetoothSocket != null && mBluetoothSocket.isConnected()){
                mBluetoothSocket.getInputStream().close();
                mBluetoothSocket.getOutputStream().close();
                mBluetoothSocket.close();
                mBluetoothSocket = null;
            }

        } catch (IOException e) {
        }
    }
}
