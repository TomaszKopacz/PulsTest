package com.tomaszkopacz.pulseoxymeter.listeners;

import com.tomaszkopacz.pulseoxymeter.model.CMSData;

/**
 * Created by tomaszkopacz on 28.11.17.
 */

public interface BluetoothDataListener {

    void bluetoothDataSent(CMSData dataPackage);
}
