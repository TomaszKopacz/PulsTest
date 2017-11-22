package com.tomaszkopacz.pulseoxymeter.controller;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.btservice.BluetoothConnector;
import com.tomaszkopacz.pulseoxymeter.btservice.BluetoothDetector;
import com.tomaszkopacz.pulseoxymeter.design.DeviceItemViewMember;
import com.tomaszkopacz.pulseoxymeter.design.ScanDevicesViewMember;
import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothListener;
import com.tomaszkopacz.pulseoxymeter.listeners.ListItemListener;
import com.tomaszkopacz.pulseoxymeter.listeners.ScanFragmentListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public class DevicesListFragment
        extends Fragment
        implements ScanFragmentListener, BluetoothListener {

    //view
    private ScanDevicesViewMember mScanDevicesViewMember;
    private DeviceItemViewMember mDeviceItemViewMember;

    //bluetooth settings
    private BluetoothDetector mBluetoothDetector;
    private BluetoothConnector mBluetoothConnector;

    private int state;

    private static final int CONNECT_PERIOD = 5000;

    //devices
    private List<BluetoothDevice> pairedDevices = new ArrayList<>();
    private List<BluetoothDevice> discoveredDevices = new ArrayList<>();


    /*==============================================================================================
                                       LIFE CYCLE
     =============================================================================================*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //set action bar
        ((MainActivity)getActivity())
                .setActionBar(getResources().getString(R.string.devices_fragment_title));

        //prepare view
        mScanDevicesViewMember = new ScanDevicesViewMember(inflater, container);
        mScanDevicesViewMember.setListener(this);

        mDeviceItemViewMember = new DeviceItemViewMember(inflater, container);

        //bluetooth
        if (BluetoothDetector.isDeviceBtCompatible()) {

            mBluetoothDetector = new BluetoothDetector(getContext(), this);
            mBluetoothConnector = new BluetoothConnector(getActivity(), this);

            if (BluetoothDetector.isBtAdapterEnabled())
                mScanDevicesViewMember.notifyBtStateChanged(true);

            //get devices
            createDevicesLists();
        }

        return mScanDevicesViewMember.getView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothDetector.unregisterBtReceiver(getContext());
    }

    /*==============================================================================================
                                   BLUETOOTH EVENTS SERVICE
     =============================================================================================*/

    @Override
    public void btStateChanged(boolean b) {

        if (b) {
            BluetoothDetector.deviceBtAdapter.enable();
            createDevicesLists();

        } else {
            BluetoothDetector.deviceBtAdapter.disable();
        }
    }

    @Override
    public void btEventAppears(Intent intent, int event) {
        switch (event){

            case BluetoothDetector.BT_ON:
                mScanDevicesViewMember.notifyBtStateChanged(true);
                createDevicesLists();
                break;

            case BluetoothDetector.BT_OFF:
                stopScan();
                mScanDevicesViewMember.notifyBtScanStateChanged(false);
                mScanDevicesViewMember.notifyBtStateChanged(false);
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
                if(mDeviceItemViewMember.getInfoTextView() != null)
                    mDeviceItemViewMember.getInfoTextView().setText(R.string.pairing);
                break;

            case BluetoothDetector.PAIRED:
                BluetoothDevice pairedDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                removeDiscoveredDevice(pairedDevice);
                insertPairedDevice(pairedDevice);
                break;

            case BluetoothConnector.DISCONNECTED:
                mDeviceItemViewMember.getInfoTextView().setText(R.string.disconnected);
                break;

            case BluetoothConnector.CONNECTED:
                mDeviceItemViewMember.getInfoTextView().setText(R.string.connected);
                break;
        }
    }

    @Override
    public void startScan() {
        if (BluetoothDetector.isBtAdapterEnabled()) {
            mScanDevicesViewMember.notifyBtScanStateChanged(true);
            BluetoothDetector.startScanning();

        } else
            Toast.makeText(getContext(), R.string.bt_off_msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void stopScan() {
        if (BluetoothDetector.isBtAdapterEnabled())
            BluetoothDetector.stopScanning();
        mScanDevicesViewMember.notifyBtScanStateChanged(false);
    }


    /*==============================================================================================
                                           LISTS' LISTENERS
     =============================================================================================*/

    private ListItemListener pairedDevicesListener = new ListItemListener() {
        @Override
        public void itemClicked(int position,
                                TextView deviceNameTextView,
                                TextView deviceInfoTextView) {

            //stop scanning
            BluetoothDetector.stopScanning();
            mScanDevicesViewMember.notifyBtScanStateChanged(false);

            //get device
            BluetoothDevice device = pairedDevices.get(position);

            //create item view
            mDeviceItemViewMember.setNameTextView(deviceNameTextView);
            mDeviceItemViewMember.setInfoTextView(deviceInfoTextView);
            mDeviceItemViewMember.getInfoTextView().setText(R.string.connecting);

            //connect
            mBluetoothConnector.connect(device);
        }
    };

    private ListItemListener discoveredDevicesListener = new ListItemListener() {
        @Override
        public void itemClicked(int position,
                                TextView deviceNameTextView,
                                TextView deviceInfoTextView) {

            //stop scanning
            BluetoothDetector.stopScanning();
            mScanDevicesViewMember.notifyBtScanStateChanged(false);

            //get device
            BluetoothDevice device = discoveredDevices.get(position);

            //create item view
            mDeviceItemViewMember.setNameTextView(deviceNameTextView);
            mDeviceItemViewMember.setInfoTextView(deviceInfoTextView);

            //try to pair
            BluetoothDetector.pair(device);
        }
    };


    /*==============================================================================================
                                        PRIVATE UTIL METHODS
     =============================================================================================*/

    private void createDevicesLists(){

        //clear lists if not empty
        pairedDevices.clear();
        discoveredDevices.clear();

        //get devices (discovered devices list is empty by default)
        pairedDevices = BluetoothDetector.getPairedDevices();

        //set lists
        mScanDevicesViewMember
                .createPairedDevicesList(pairedDevices, pairedDevicesListener);

        mScanDevicesViewMember
                .createDiscoveredDevicesList(discoveredDevices, discoveredDevicesListener);
    }

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