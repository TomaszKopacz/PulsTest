package com.tomaszkopacz.pulseoxymeter.btservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tomaszkopacz.pulseoxymeter.controller.BluetoothListener;

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
        return deviceBtAdapter.isEnabled() ? true : false;
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

    /**
     *
     * @param context
     */
    public void unregisterBtReceiver(Context context){
        context.unregisterReceiver(btReceiver);
        this.listener = null;
    }
}
