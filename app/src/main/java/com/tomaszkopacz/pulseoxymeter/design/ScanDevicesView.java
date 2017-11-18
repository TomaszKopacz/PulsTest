package com.tomaszkopacz.pulseoxymeter.design;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.TextView;

import com.tomaszkopacz.pulseoxymeter.controller.DeviceItemListener;
import com.tomaszkopacz.pulseoxymeter.controller.ScanDevicesViewListener;

import java.util.List;

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
     * Create lists for paired devices.
     */
    void createPairedDevicesList(List<BluetoothDevice> devices, DeviceItemListener listener);

    /**
     * Create lists for discovered devices.
     */
    void createDiscoveredDevicesList(List<BluetoothDevice> devices, DeviceItemListener listener);

    /**
     * Notify new item in paired devices list.
     * @param position where change occurs
     */
    void insertToPairedDevicesList(int position);

    /**
     * Notify new item in discovered devices list.
     * @param position where change occurs
     */
    void insertToDiscoveredDevicesList(int position);

    /**
     * Notify deleting item from discovered devices list.
     * @param position
     */
    void removeFromDiscoveredDevicesList(int position);

    /**
     * Sets item view properties.
     * @param
     */
    void setItemView(TextView deviceName, TextView deviceInfo);

    /**
     * Sets text with information about device.
     * @param info
     */
    void setInfoText(String info);

    /**
     * Makes some events on view when scan is started.
     */
    void startScan();

    /**
     * Makes some events on view when scan is stopped.
     */
    void stopScan();
}
