package com.tomaszkopacz.pulseoxymeter.controller;


import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomaszkopacz.pulseoxymeter.btservice.BluetoothReader;
import com.tomaszkopacz.pulseoxymeter.design.CommunicationFragmentLayout;
import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothDataListener;
import com.tomaszkopacz.pulseoxymeter.listeners.CommunicationFragmentListener;
import com.tomaszkopacz.pulseoxymeter.model.CMSData;


public class CommunicationFragment
        extends Fragment
        implements CommunicationFragmentListener, BluetoothDataListener {

    //view
    private Activity activity;
    private CommunicationFragmentLayout mCommunicateFragmentLayout;

    //bluetooth
    private BluetoothReader mBluetoothReader;
    private BluetoothSocket mBluetoothSocket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //prepare view
        activity = (CommunicationActivity)getActivity();

        mCommunicateFragmentLayout = new CommunicationFragmentLayout(inflater, container);
        mCommunicateFragmentLayout.setListener(this);

        //bluetooth
        //mBluetoothReader = new BluetoothReader();

        return mCommunicateFragmentLayout.getView();
    }

    @Override
    public void onNavigationItemClicked() {
    }

    @Override
    public void bluetoothDataSent(CMSData dataPackage) {

    }
}
