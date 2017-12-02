package com.tomaszkopacz.pulseoxymeter.controller;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.btservice.ConnectionService;
import com.tomaszkopacz.pulseoxymeter.design.CommunicationFragmentLayout;
import com.tomaszkopacz.pulseoxymeter.design.MainActivityLayout;
import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothCallbacks;
import com.tomaszkopacz.pulseoxymeter.listeners.CommunicationFragmentListener;
import com.tomaszkopacz.pulseoxymeter.listeners.MainActivityListener;
import com.tomaszkopacz.pulseoxymeter.model.CMSData;


public class CommunicationFragment
        extends Fragment
        implements CommunicationFragmentListener, MainActivityListener, BluetoothCallbacks {

    //view
    private MainActivityLayout mMainActivityLayout;
    private CommunicationFragmentLayout mCommunicationFragmentLayout;

    //bluetooth
    private ConnectionService service;
    private boolean bound = false;


    /*==============================================================================================
                                        LIFE CYCLE
    ==============================================================================================*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //bind bt service
        Intent intent = new Intent(getActivity(), ConnectionService.class);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //activity layout
        mMainActivityLayout = ((MainActivity)getActivity()).getLayout();
        mMainActivityLayout.getToolbar().setTitle(R.string.communicate_fragment_title);
        mMainActivityLayout.getToolbar().setNavigationIcon(R.drawable.ic_back);
        mMainActivityLayout.getDrawer().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mMainActivityLayout.setListener(this);

        //fragment layout
        mCommunicationFragmentLayout = new CommunicationFragmentLayout(inflater, container);
        mCommunicationFragmentLayout.setListener(this);

        return mCommunicationFragmentLayout.getView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bound){
            getActivity().unbindService(mServiceConnection);
            bound = false;
        }
    }


    /*==============================================================================================
                                       BLUETOOTH SERVICE
     =============================================================================================*/

    private ServiceConnection mServiceConnection  = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) iBinder;
            service = binder.getService();
            registerCallback();

            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };


    private void registerCallback(){
        service.registerCallback(this);
    }


    /*==============================================================================================
                                        LISTENERS
    ==============================================================================================*/
    @Override
    public void onNavigationIconClick() {

        //give back default activity settings and go back connection fragment
        mMainActivityLayout.getToolbar().setNavigationIcon(R.drawable.ic_menu);
        mMainActivityLayout.getDrawer().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mMainActivityLayout.setListener(((MainActivity)getActivity()).getDefaultListener());
        ((MainActivity)getActivity()).setFragment(ConnectionFragment.class);
    }

    @Override
    public void onMenuItemSelected(int item) {
        //do nothing: navigation view is not visible
    }

    @Override
    public void onConnectionOpenRequest() {

    }

    @Override
    public void onConnectionCloseRequest() {

    }

    @Override
    public void onDataIncome(CMSData data) {
        Log.d("TomaszKopacz", "Pulse is: " + data.getPulseByte());
        Log.d("TomaszKopacz", "Saturation is: " + data.getSaturationByte());
        Log.d("TomaszKopacz", "Wave is: " + data.getWaveformByte());
    }

    @Override
    public void startReading(){
        service.read();
    }
}
