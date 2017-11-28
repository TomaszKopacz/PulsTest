package com.tomaszkopacz.pulseoxymeter.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.btservice.BluetoothDetector;
import com.tomaszkopacz.pulseoxymeter.btservice.BondingService;
import com.tomaszkopacz.pulseoxymeter.btservice.DetectionService;
import com.tomaszkopacz.pulseoxymeter.design.DeviceItemLayout;
import com.tomaszkopacz.pulseoxymeter.design.ConnectionFragmentLayout;
import com.tomaszkopacz.pulseoxymeter.listeners.ListItemListener;
import com.tomaszkopacz.pulseoxymeter.listeners.ConnectionFragmentListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public class ConnectionFragment
        extends Fragment
        implements ConnectionFragmentListener{

    //activity parent
    private MainActivity mMainActivity;

    //view
    private ConnectionFragmentLayout mDevicesListFragmentLayout;
    private DeviceItemLayout mDeviceItemView;

    //bluetooth settings
    private IntentFilter btDetectionIntentFilter;
    private IntentFilter btBondingIntentFilter;
    private IntentFilter btConnectionIntentFilter;
    private Intent mDetectionIntent;
    private Intent mBondingIntent;

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
        mMainActivity = (MainActivity)getActivity();

        mDevicesListFragmentLayout = new ConnectionFragmentLayout(inflater, container);
        mDevicesListFragmentLayout.setListener(this);

        mDeviceItemView = new DeviceItemLayout(inflater, container);

        //bluetooth
        if (BluetoothDetector.isDeviceBtCompatible()) {

            setUpIntentFilters();
            getActivity().registerReceiver(btDetectionReceiver, btDetectionIntentFilter);
            getActivity().registerReceiver(btBondingReceiver, btBondingIntentFilter);

            mDetectionIntent = new Intent(getActivity(), DetectionService.class);
            mBondingIntent = new Intent(getActivity(), BondingService.class);

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
        getActivity().unregisterReceiver(btDetectionReceiver);
        getActivity().unregisterReceiver(btBondingReceiver);
    }


    /*==============================================================================================
                                   BLUETOOTH EVENTS
     =============================================================================================*/

    private void setUpIntentFilters(){
        btDetectionIntentFilter = new IntentFilter();
        btDetectionIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        btDetectionIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);

        btBondingIntentFilter = new IntentFilter();
        btBondingIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        btConnectionIntentFilter = new IntentFilter();
        btConnectionIntentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
    }

    private BroadcastReceiver btDetectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            switch (action){
                case BluetoothAdapter.ACTION_STATE_CHANGED:

                    if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                            == BluetoothAdapter.STATE_ON) {
                        mDevicesListFragmentLayout.notifyBtState(true);
                        createDevicesLists();

                    } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                            == BluetoothAdapter.STATE_OFF) {
                        stopScan();
                        mDevicesListFragmentLayout.notifyBtScanStateChanged(false);
                        mDevicesListFragmentLayout.notifyBtState(false);
                    }

                    break;

                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (!pairedDevices.contains(device) && !discoveredDevices.contains(device))
                        insertDiscoveredDevice(device);
                    break;
            }
        }
    };

    private BroadcastReceiver btBondingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                switch (device.getBondState()){
                    case BluetoothDevice.BOND_BONDED:
                        BluetoothDevice pairedDevice =
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        removeDiscoveredDevice(pairedDevice);
                        insertPairedDevice(pairedDevice);
                        break;

                    case BluetoothDevice.BOND_NONE:
                        break;
                }
            }

        }
    };

    @Override
    public void btStateChanged(boolean b) {

        if (b) {
            BluetoothDetector.deviceBtAdapter.enable();
            createDevicesLists();

        } else
            BluetoothDetector.deviceBtAdapter.disable();
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
            //mBondingIntent.connect(device);
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
            mBondingIntent.putExtra("address", device.getAddress());
            getActivity().startService(mBondingIntent);
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