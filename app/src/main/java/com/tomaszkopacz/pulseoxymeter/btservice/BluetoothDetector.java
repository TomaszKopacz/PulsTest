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
     * Bluetooth is on.
     */
    public static final int BT_ON = 10;

    /**
     * Bluetooth is off.
     */
    public static final int BT_OFF = 20;

    /**
     *
     */
    public static final int DEVICE_DISCOVERED = 30;

    /**
     * Pairing to device failed.
     */
    public static final int NOT_PAIRED = 40;

    /**
     * Pairing to device in progress.
     */
    public static final int PAIRING = 50;

    /**
     * Paired to device.
     */
    public static final int PAIRED = 60;

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

    /**
     * Tries to pair with a given bluetooth device.
     * @param device
     */
    public static void pair(BluetoothDevice device){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            device.createBond();
        }
    }

    private IntentFilter filter;
    private BluetoothListener listener;

    /**
     * Registers broadcast receiver for bluetooth changes.
     * When new event appears sends state and intent to a given listener.
     * Possible states are: BT_ON, BT_OFF, DEVICE_DISCOVERED, NOT_PAIRED, PAIRING, PAIRED.
     * @param context
     * @param listener
     */
    public void registerBtReceiver(Context context, BluetoothListener listener){

        filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        this.listener = listener;

        context.registerReceiver(btReceiver, filter);
    }

    //bluetooth state changes listening
    private BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //bluetooth on\off
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){

                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                        == BluetoothAdapter.STATE_ON)
                    listener.onBtEventAppears(intent, BT_ON);

                else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                        == BluetoothAdapter.STATE_OFF)
                    listener.onBtEventAppears(intent, BT_OFF);
            }

            //when new device is discovered
            else if (BluetoothDevice.ACTION_FOUND.equals(action))
                listener.onBtEventAppears(intent, DEVICE_DISCOVERED);

                //when sta\te of bonding is changed (bond turned off, pairing, paired)
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                BluetoothDevice bd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (bd.getBondState() == BluetoothDevice.BOND_NONE)
                    listener.onBtEventAppears(intent, NOT_PAIRED);

                else if (bd.getBondState() == BluetoothDevice.BOND_BONDING)
                    listener.onBtEventAppears(intent, PAIRING);

                else if (bd.getBondState() == BluetoothDevice.BOND_BONDED)
                    listener.onBtEventAppears(intent, PAIRED);
            }
        }
    };

    /**
     * Unregisters bluetooth broadcast receiver.
     * @param context
     */
    public void unregisterBtReceiver(Context context){
        context.unregisterReceiver(btReceiver);
        this.listener = null;
    }
}
