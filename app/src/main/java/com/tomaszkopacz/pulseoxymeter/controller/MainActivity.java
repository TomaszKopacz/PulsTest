package com.tomaszkopacz.pulseoxymeter.controller;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.design.MainActivityLayout;
import com.tomaszkopacz.pulseoxymeter.listeners.MainActivityListener;

public class MainActivity
        extends AppCompatActivity
        implements MainActivityListener{

    //layout
    private MainActivityLayout mMainActivityLayout;

    private FragmentManager manager;
    private Fragment fragment;

    //bluetooth
    private IntentFilter btConnectionIntentFilter;
    private BluetoothSocket socket;
    private boolean connected = false;
    private BluetoothDevice connectedDevice;


    /*==============================================================================================
                                       LIFE CYCLE
     =============================================================================================*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivityLayout = new MainActivityLayout(this);
        mMainActivityLayout.setListener(this);
        manager = getSupportFragmentManager();

        setUpIntentFilter();
        registerReceiver(btConnectionReceiver, btConnectionIntentFilter);

        setStartContent();
        setFragment(ConnectionFragment.class);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(btConnectionReceiver);

        super.onDestroy();
    }

    private void setStartContent(){
        mMainActivityLayout.getToolbar().setNavigationIcon(R.drawable.ic_menu);
        setFragment(ConnectionFragment.class);
    }

    public void setFragment(Class fragmentClass){
        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (Exception e) {}

        manager
                .beginTransaction()
                .replace(mMainActivityLayout.getFragmentFrame().getId(), fragment)
                .commit();
    }


    /*==============================================================================================
                                       LISTENERS
     =============================================================================================*/

    private BroadcastReceiver btConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            switch (action){
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Log.d("TomaszKopacz", "ACL_CONNECTED");
                    connected = true;
                    connectedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    break;

                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.d("TomaszKopacz", "ACL_DISCONNECTED");
                    connected = false;
                    connectedDevice = null;
                    break;
            }
        }
    };
    @Override
    public void onNavigationIconClick() {
        mMainActivityLayout.getDrawer().openDrawer(GravityCompat.START);
    }

    @Override
    public void onMenuItemSelected(int item) {

        switch (item){
            case MainActivityLayout.CONNECT_ITEM:
                setFragment(ConnectionFragment.class);
                mMainActivityLayout.getDrawer().closeDrawers();
                break;

            case MainActivityLayout.DIARY_ITEM:
                setFragment(HRVFragment.class);
                mMainActivityLayout.getDrawer().closeDrawers();
                break;
        }
    }


    /*==============================================================================================
                                       SETTERS AND GETTERS
     =============================================================================================*/

    public MainActivityLayout getLayout(){
        return mMainActivityLayout;
    }

    public MainActivityListener getDefaultListener(){
        return this;
    }

    public BluetoothSocket getSocket(){
        return socket;
    }

    public void setSocket(BluetoothSocket socket){
        this.socket = socket;
    }

    public boolean isConnected(){
        return connected;
    }

    public BluetoothDevice getConnectedDevice(){
        return connectedDevice;
    }


    /*==============================================================================================
                                       PRIVATE UTIL METHODS
     =============================================================================================*/

    private void setUpIntentFilter(){
        btConnectionIntentFilter = new IntentFilter();
        btConnectionIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        btConnectionIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    }
}
