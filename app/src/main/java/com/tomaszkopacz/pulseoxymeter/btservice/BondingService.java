package com.tomaszkopacz.pulseoxymeter.btservice;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;

/**
 * Created by tomaszkopacz on 28.11.17.
 */

public class BondingService extends IntentService {

    public BondingService() {
        super("BluetoothService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String deviceAddress = intent.getStringExtra("address");
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
        pair(device);
    }

    private void pair(BluetoothDevice device){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            device.createBond();
        }
    }
}
