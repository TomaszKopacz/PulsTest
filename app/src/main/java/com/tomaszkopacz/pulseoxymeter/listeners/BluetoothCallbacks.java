package com.tomaszkopacz.pulseoxymeter.listeners;

import com.tomaszkopacz.pulseoxymeter.model.CMSData;

/**
 * Created by tomaszkopacz on 29.11.17.
 * Bluetooth events listening.
 */

public interface BluetoothCallbacks {

    void onConnectionOpenRequest();

    void onConnectionCloseRequest();

    void onDataIncome(CMSData data);
}
