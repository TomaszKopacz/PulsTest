package com.tomaszkopacz.pulseoxymeter.design;

import com.tomaszkopacz.pulseoxymeter.controller.ScanDevicesViewListener;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public interface ScanDevicesView extends GeneralView {

    /**
     * Sets listener notifying user's actions.
     * @param listener
     */
    void setListener(ScanDevicesViewListener listener);

    /**
     * Makes some events when switch state is changed.
     * @param b
     */
    void setBtSwitchChecked(boolean b);

    /**
     * Makes some events on view when scan is started.
     */
    void startScan();

    /**
     * Makes some events on view when scan is stopped.
     */
    void stopScan();
}
