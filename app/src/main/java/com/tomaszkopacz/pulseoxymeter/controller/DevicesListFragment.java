package com.tomaszkopacz.pulseoxymeter.controller;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.btservice.BluetoothConnector;
import com.tomaszkopacz.pulseoxymeter.btservice.BluetoothDetector;
import com.tomaszkopacz.pulseoxymeter.design.DeviceItemLayout;
import com.tomaszkopacz.pulseoxymeter.design.ConnectFragmentLayout;
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
    private ConnectFragmentLayout mDevicesListFragmentLayout;
    private DeviceItemLayout mDeviceItemView;

    //bluetooth settings
    private BluetoothDetector mBluetoothDetector;
    private BluetoothConnector mBluetoothConnector;

    //devices
    private List<BluetoothDevice> pairedDevices = new ArrayList<>();
    private List<BluetoothDevice> discoveredDevices = new ArrayList<>();

    private static final int SCAN_PERIOD = 10000;


    /*==============================================================================================
                                       LIFE CYCLE
     =============================================================================================*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //prepare view
        mDevicesListFragmentLayout = new ConnectFragmentLayout(inflater, container);
        mDevicesListFragmentLayout.setListener(this);

        mDeviceItemView = new DeviceItemLayout(inflater, container);

        //bluetooth
        if (BluetoothDetector.isDeviceBtCompatible()) {

            mBluetoothDetector = new BluetoothDetector(getContext(), this);
            mBluetoothConnector = new BluetoothConnector(getActivity(), this);

            if (BluetoothDetector.isBtAdapterEnabled())
                mDevicesListFragmentLayout.notifyBtState(true);

            //get devices
            createDevicesLists();
        }

        return mDevicesListFragmentLayout.getView();
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

        } else
            BluetoothDetector.deviceBtAdapter.disable();
    }

    @Override
    public void btEventAppears(Intent intent, int event) {
        switch (event){

            case BluetoothDetector.BT_ON:
                mDevicesListFragmentLayout.notifyBtState(true);
                createDevicesLists();
                break;

            case BluetoothDetector.BT_OFF:
                stopScan();
                mDevicesListFragmentLayout.notifyBtScanStateChanged(false);
                mDevicesListFragmentLayout.notifyBtState(false);
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
                if(mDeviceItemView.getInfoTextView() != null)
                    mDeviceItemView.getInfoTextView().setText(R.string.pairing);
                break;

            case BluetoothDetector.PAIRED:
                BluetoothDevice pairedDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                removeDiscoveredDevice(pairedDevice);
                insertPairedDevice(pairedDevice);
                break;

            case BluetoothConnector.DISCONNECTED:
                mDeviceItemView.getInfoTextView().setText(R.string.disconnected);
                break;

            case BluetoothConnector.CONNECTED:
                mDeviceItemView.getInfoTextView().setText(R.string.connected);
                break;
        }
    }

    @Override
    public void startScan() {
        if (BluetoothDetector.isBtAdapterEnabled()) {

            stopHandler.postDelayed(stopScanRunnable, SCAN_PERIOD);
            BluetoothDetector.startScanning();

        } else
            Toast.makeText(getContext(), R.string.bt_off_msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void stopScan() {
        if (BluetoothDetector.isBtAdapterEnabled())
            stopHandler.removeCallbacksAndMessages(null);
            BluetoothDetector.stopScanning();
    }

    private  Handler stopHandler = new Handler();

    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
            mDevicesListFragmentLayout.notifyBtScanStateChanged(false);
        }
    };


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
            mDevicesListFragmentLayout.notifyBtScanStateChanged(false);

            //get device
            BluetoothDevice device = pairedDevices.get(position);

            //create device_item view
            mDeviceItemView.setNameTextView(deviceNameTextView);
            mDeviceItemView.setInfoTextView(deviceInfoTextView);
            mDeviceItemView.getInfoTextView().setText(R.string.connecting);

            //try to connect
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
            mDevicesListFragmentLayout.notifyBtScanStateChanged(false);

            //get device
            BluetoothDevice device = discoveredDevices.get(position);

            //create device_item view
            mDeviceItemView.setNameTextView(deviceNameTextView);
            mDeviceItemView.setInfoTextView(deviceInfoTextView);

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
        mDevicesListFragmentLayout
                .createPairedDevicesList(pairedDevices, pairedDevicesListener);

        mDevicesListFragmentLayout
                .createDiscoveredDevicesList(discoveredDevices, discoveredDevicesListener);
    }

    //insert new device to paired devices list
    private void insertPairedDevice(BluetoothDevice device){
        int position = 0;
        pairedDevices.add(position, device);
        mDevicesListFragmentLayout.notifyInsertToPairedDevices(position);
    }

    //insert new device to discovered devices list
    private void insertDiscoveredDevice(BluetoothDevice device){
        int position = 0;
        discoveredDevices.add(position, device);
        mDevicesListFragmentLayout.notifyInsertToDiscoveredDevices(position);
    }

    //remove device from discovered devices list
    private void removeDiscoveredDevice(BluetoothDevice device){
        int position = discoveredDevices.indexOf(device);
        discoveredDevices.remove(position);
        mDevicesListFragmentLayout.notifyRemoveFromDiscoveredDevices(position);
    }
}