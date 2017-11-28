package com.tomaszkopacz.pulseoxymeter.listeners;

import android.content.Intent;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public interface BluetoothListener {

    /**
     * Runs, when new bluetooth state occurs.
     * @param intent
     * @param event
     */
    void btEventAppears(Intent intent, int event);
}
