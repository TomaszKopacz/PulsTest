package com.tomaszkopacz.pulseoxymeter.controller;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.btservice.BluetoothConnector;
import com.tomaszkopacz.pulseoxymeter.btservice.BluetoothDetector;
import com.tomaszkopacz.pulseoxymeter.design.DeviceItemViewMember;
import com.tomaszkopacz.pulseoxymeter.design.ScanDevicesViewMember;
import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothListener;
import com.tomaszkopacz.pulseoxymeter.listeners.ListItemListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public class DevicesListFragment extends Fragment implements BluetoothListener {

    //view
    private ScanDevicesViewMember mScanDevicesViewMember;
    private DeviceItemViewMember mDeviceItemViewMember;

    //bluetooth settings
    private BluetoothDetector mBluetoothDetector;
    private BluetoothConnector mBluetoothConnector;

    //devices
    private List<BluetoothDevice> pairedDevices = new ArrayList<>();
    private List<BluetoothDevice> discoveredDevices = new ArrayList<>();


    /*==============================================================================================
                                       LIFE CYCLE
     =============================================================================================*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //prepare view
        mScanDevicesViewMember = new ScanDevicesViewMember(inflater, container);
        mScanDevicesViewMember.setListener(this);
        mScanDevicesViewMember.customizeLayout();

        mDeviceItemViewMember = new DeviceItemViewMember(getContext());

        //bluetooth
        if (BluetoothDetector.isDeviceBtCompatible()) {

            mBluetoothDetector = new BluetoothDetector();
            mBluetoothDetector.registerBtReceiver(getContext(), this);

            if (BluetoothDetector.isBtAdapterEnabled())
                mScanDevicesViewMember.btStateChanged(true);

            //get devices
            createDevicesList();
        }

        return mScanDevicesViewMember.getView();
    }

    /*==============================================================================================
                                   BLUETOOTH EVENTS SERVICE
     =============================================================================================*/

    @Override
    public void btStateChanged(boolean b) {

        if (b) {
            BluetoothDetector.deviceBtAdapter.enable();
            createDevicesList();

        } else {
            BluetoothDetector.deviceBtAdapter.disable();
        }
    }

    @Override
    public void onBtEventAppears(Intent intent, int event) {
        switch (event){

            case BluetoothDetector.BT_ON:
                mScanDevicesViewMember.btStateChanged(true);
                createDevicesList();
                break;

            case BluetoothDetector.BT_OFF:
                stopScan();
                mScanDevicesViewMember.stopScan();
                mScanDevicesViewMember.btStateChanged(false);
                break;

            case BluetoothDetector.DEVICE_DISCOVERED:
                BluetoothDevice discoveredDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (!pairedDevices.contains(discoveredDevice)
                        && !discoveredDevices.contains(discoveredDevice))
                    insertDiscoveredDevice(discoveredDevice);
                break;

            case BluetoothDetector.NOT_PAIRED:
                break;

            case BluetoothDetector.PAIRING:
                mDeviceItemViewMember.getInfoTextView().setText(R.string.pairing);
                break;

            case BluetoothDetector.PAIRED:
                BluetoothDevice pairedDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                removeDiscoveredDevice(pairedDevice);
                insertPairedDevice(pairedDevice);
                break;
        }
    }

    @Override
    public void startScan() {
        if (BluetoothDetector.isBtAdapterEnabled()) {
            mScanDevicesViewMember.startScan();
            BluetoothDetector.startScanning();
        }
    }

    @Override
    public void stopScan() {
        if (BluetoothDetector.isBtAdapterEnabled())
            BluetoothDetector.stopScanning();
        mScanDevicesViewMember.stopScan();
    }


    /*==============================================================================================
                                           DEVICES LISTS
     =============================================================================================*/

    private void createDevicesList(){

        //get devices (discovered devices list is empty by default)
        pairedDevices = BluetoothDetector.getPairedDevices();

        //set lists
        mScanDevicesViewMember
                .createPairedDevicesList(pairedDevices, pairedDevicesListener);

        mScanDevicesViewMember
                .createDiscoveredDevicesList(discoveredDevices, discoveredDevicesListener);
    }

    private ListItemListener pairedDevicesListener = new ListItemListener() {
        @Override
        public void itemClicked(int position,
                                TextView deviceNameTextView,
                                TextView deviceAddressTextView) {

        }
    };

    private ListItemListener discoveredDevicesListener = new ListItemListener() {
        @Override
        public void itemClicked(int position,
                                TextView deviceNameTextView,
                                TextView deviceInfoTextView) {

            //stop scanning
            BluetoothDetector.stopScanning();
            mScanDevicesViewMember.stopScan();

            //get device
            BluetoothDevice device = discoveredDevices.get(position);

            //create item view
            mDeviceItemViewMember.setNameTextView(deviceNameTextView);
            mDeviceItemViewMember.setInfoTextView(deviceInfoTextView);

            //try to pair
            BluetoothDetector.pair(device);
        }
    };

    //insert new device to paired devices list
    private void insertPairedDevice(BluetoothDevice device){
        int position = 0;
        pairedDevices.add(position, device);
        mScanDevicesViewMember.notifyInsertToPairedDevices(position);
    }

    //insert new device to discovered devices list
    private void insertDiscoveredDevice(BluetoothDevice device){
        int position = 0;
        discoveredDevices.add(position, device);
        mScanDevicesViewMember.notifyInsertToDiscoveredDevices(position);
    }

    //remove device from discovered devices list
    private void removeDiscoveredDevice(BluetoothDevice device){
        int position = discoveredDevices.indexOf(device);
        discoveredDevices.remove(position);
        mScanDevicesViewMember.notifyRemoveFromDiscoveredDevices(position);
    }
}