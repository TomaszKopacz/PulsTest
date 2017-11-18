package com.tomaszkopacz.pulseoxymeter.controller;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public interface ScanDevicesViewListener {

    /**
     * Runs, when bluetooth state is changed.
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
}
