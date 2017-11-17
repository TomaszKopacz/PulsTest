package com.tomaszkopacz.pulseoxymeter.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomaszkopacz.pulseoxymeter.btservice.BluetoothDetector;
import com.tomaszkopacz.pulseoxymeter.design.ScanDevicesViewMember;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public class DevicesListFragment extends Fragment implements ScanDevicesViewListener, BluetoothListener{

    //view
    private ScanDevicesViewMember mScanDevicesViewMember;

    //bluetooth settings
    private BluetoothDetector mBluetoothDetector;

    /*==============================================================================================
                                    LIFE CYCLE
     =============================================================================================*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //prepare view
        mScanDevicesViewMember = new ScanDevicesViewMember(inflater, container);
        mScanDevicesViewMember.setListener(this);
        mScanDevicesViewMember.customizeLayout(getActivity().getResources());

        //bluetooth
        if (BluetoothDetector.isDeviceBtCompatible()) {

            mBluetoothDetector = new BluetoothDetector();
            mBluetoothDetector.registerBtReceiver(getContext(), this);

            if (BluetoothDetector.isBtAdapterEnabled())
                mScanDevicesViewMember.setBtSwitchChecked(true);
        }

        return mScanDevicesViewMember.getView();
    }

    @Override
    public void btStateChanged(boolean b) {

        if (b) {
            BluetoothDetector.deviceBtAdapter.enable();

        } else {
            BluetoothDetector.deviceBtAdapter.disable();
        }
    }

    @Override
    public void startScan() {
    }

    @Override
    public void onBtEventAppears(Intent intent, int state) {
        switch (state){

            case BluetoothDetector.BT_ON:
                mScanDevicesViewMember.setBtSwitchChecked(true);
                break;

            case BluetoothDetector.BT_OFF:
                mScanDevicesViewMember.setBtSwitchChecked(false);
                break;

            case BluetoothDetector.DEVICE_DISCOVERED:
                break;

            case BluetoothDetector.NOT_PAIRED:
                break;

            case BluetoothDetector.PAIRING:
                break;

            case BluetoothDetector.PAIRED:
                break;
        }
    }
}
