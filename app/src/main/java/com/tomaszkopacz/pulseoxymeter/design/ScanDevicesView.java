package com.tomaszkopacz.pulseoxymeter.design;

import android.bluetooth.BluetoothDevice;

import com.tomaszkopacz.pulseoxymeter.listeners.ListItemListener;
import com.tomaszkopacz.pulseoxymeter.listeners.ScanFragmentListener;

import java.util.List;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public interface ScanDevicesView extends GeneralView {

    /**
     * Sets listener notifying user's actions.
     * @param listener
     */
    void setListener(ScanFragmentListener listener);

    /**
     * Makes some events when bluetooth state is changed.
     * @param b {@code true} if new state is on, {@code false} if new state is off
     */
    void notifyBtStateChanged(boolean b);

    /**
     * Makes some events when bluetooth scan state is changed.
     * @param b {@code true} if new state is on, {@code false} if new state is off
     */
    void notifyBtScanStateChanged(boolean b);

    /**
     * Create lists for paired devices.
     */
    void createPairedDevicesList(List<BluetoothDevice> devices, ListItemListener listener);

    /**
     * Create lists for discovered devices.
     */
    void createDiscoveredDevicesList(List<BluetoothDevice> devices, ListItemListener listener);

}
