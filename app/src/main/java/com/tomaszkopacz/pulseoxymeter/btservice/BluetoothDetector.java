package com.tomaszkopacz.pulseoxymeter.btservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public class BluetoothDetector {

    /**
     * Default device bluetooth adapter.
     */
    public static final BluetoothAdapter deviceBtAdapter = BluetoothAdapter.getDefaultAdapter();


    /**
     * Checks, whether device is equipped with bluetooth.
     * @return boolean
     */
    public static boolean isDeviceBtCompatible(){
        if (deviceBtAdapter == null)
            return false;
        else
            return true;
    }

    /**
     * Checks whether bluetooth is on on device.
     * @return boolean
     */
    public static boolean isBtAdapterEnabled(){
        if (isDeviceBtCompatible())
            return deviceBtAdapter.isEnabled() ? true : false;

        return false;
    }

    /**
     * Starts bluetooth devices discovery.
     */
    public static void startScanning(){
        if (isDeviceBtCompatible())
            deviceBtAdapter.startDiscovery();
    }

    /**
     * Stops bluetooth devices discovery.
     */
    public static void stopScanning(){
        if (isDeviceBtCompatible())
            deviceBtAdapter.cancelDiscovery();
    }

    /**
     * Returns list of paired devices.
     * @return devices list
     */
    public static List<BluetoothDevice> getPairedDevices(){

        List<BluetoothDevice> devices = new ArrayList<>();

        if (isDeviceBtCompatible()) {
            Set<BluetoothDevice> devicesSet = deviceBtAdapter.getBondedDevices();
            for (BluetoothDevice bd : devicesSet)
                devices.add(bd);
        }

        return devices;
    }
}
