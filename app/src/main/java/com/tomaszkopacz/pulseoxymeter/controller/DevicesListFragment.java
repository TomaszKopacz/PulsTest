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

    //connecting state
    private int state;

    //maximum connecting time 5 seconds
    private static final int CONNECT_PERIOD = 5000;


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

        mDeviceItemViewMember = new DeviceItemViewMember(inflater, container);

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
                if(mDeviceItemViewMember.getInfoTextView() != null)
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

        } else
            Toast.makeText(getContext(), R.string.bt_off_msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void stopScan() {
        if (BluetoothDetector.isBtAdapterEnabled())
            BluetoothDetector.stopScanning();
        mScanDevicesViewMember.stopScan();
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
            mScanDevicesViewMember.stopScan();

            //get device
            BluetoothDevice device = pairedDevices.get(position);

            //create item view
            mDeviceItemViewMember.setNameTextView(deviceNameTextView);
            mDeviceItemViewMember.setInfoTextView(deviceInfoTextView);
            mDeviceItemViewMember.getInfoTextView().setText(R.string.connecting);

            //connect
            mBluetoothConnector = new BluetoothConnector(device);
            connectToDevice(mBluetoothConnector);
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


    /*==============================================================================================
                                        PRIVATE UTIL METHODS
     =============================================================================================*/

    private void createDevicesList(){

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

    //Connects to device. Connecting runs in new thread and lasts maximum 10 seconds.
    //After all info text view is set up.
    private void connectToDevice(final BluetoothConnector connector){
        final Runnable connectionStartRunnable;
        final Runnable connectionStopRunnable;
        final Runnable uiLayoutChangeRunnable;

        final Thread connectionThread;
        final Handler stopThreadHandler;

        //RUNNABLE: change text view - only possible in ui thread
        uiLayoutChangeRunnable = new Runnable() {
            @Override
            public void run() {
                if (state == BluetoothConnector.CONNECTED)
                    mDeviceItemViewMember.getInfoTextView().setText(R.string.connected);

                else
                    mDeviceItemViewMember.getInfoTextView().setText(R.string.disconnected);
            }
        };

        //RUNNABLE: stop connecting after period of time
        connectionStopRunnable = new Runnable() {
            @Override
            public void run() {
                connector.closeConnection();
            }
        };

        //RUNNABLE: start connection
        connectionStartRunnable = new Runnable() {
            @Override
            public void run() {
                connector.closeConnection();
                connector.connectToDevice();

                state = connector.getState();
                while (state == BluetoothConnector.CONNECTING){

                }

                getActivity().runOnUiThread(uiLayoutChangeRunnable);
            }
        };

        //start
        connectionThread = new Thread(connectionStartRunnable);
        stopThreadHandler = new Handler();
        stopThreadHandler.postDelayed(connectionStopRunnable, CONNECT_PERIOD);
        connectionThread.start();
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