package com.tomaszkopacz.pulseoxymeter.design;

import android.bluetooth.BluetoothDevice;

import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothListener;
import com.tomaszkopacz.pulseoxymeter.listeners.ListItemListener;

import java.util.List;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public interface ScanDevicesView extends GeneralView {

    /**
     * Sets listener notifying user's actions.
     * @param listener
     */
    void setListener(BluetoothListener listener);

    /**
     * Makes some events when bluetooth state is changed.
     * @param b {@code true} if new state is on, {@code false} if new state is off
     */
    void btStateChanged(boolean b);

    /**
     * Create lists for paired devices.
     */
    void createPairedDevicesList(List<BluetoothDevice> devices, ListItemListener listener);

    /**
     * Create lists for discovered devices.
     */
    void createDiscoveredDevicesList(List<BluetoothDevice> devices, ListItemListener listener);

    /**
     * Notify new item in paired devices list.
     * @param position where change occurs
     */
    void notifyInsertToPairedDevices(int position);

    /**
     * Notify new item in discovered devices list.
     * @param position where change occurs
     */
    void notifyInsertToDiscoveredDevices(int position);

    /**
     * Notify deleting item from discovered devices list.
     * @param position
     */
    void notifyRemoveFromDiscoveredDevices(int position);

    /**
     * Makes some events on view when scan is started.
     */
    void startScan();

    /**
     * Makes some events on view when scan is stopped.
     */
    void stopScan();
}
