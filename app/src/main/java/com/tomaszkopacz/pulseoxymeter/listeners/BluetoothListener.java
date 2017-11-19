package com.tomaszkopacz.pulseoxymeter.listeners;

import android.content.Intent;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public interface BluetoothListener {

    /**
     * Runs, when bluetooth state is changed manually (e.g. by switch).
     * @param state
     */
    void btStateChanged(boolean state);

    /**
     * Runs, when scan is started.
     */
    void startScan();

    /**
     * Runs, when scan is stopped.
     */
    void stopScan();

    /**
     * Runs, when new bluetooth state occurs.
     * @param intent
     * @param event
     */
    void onBtEventAppears(Intent intent, int event);

}
