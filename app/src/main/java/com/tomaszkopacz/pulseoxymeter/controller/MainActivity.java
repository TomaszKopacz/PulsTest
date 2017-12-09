package com.tomaszkopacz.pulseoxymeter.controller;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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
    private boolean connected = false;
    private BluetoothSocket socket;


    /*==============================================================================================
                                       LIFE CYCLE
     =============================================================================================*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivityLayout = new MainActivityLayout(this);
        mMainActivityLayout.setListener(this);
        manager = getSupportFragmentManager();

        setStartContent();
        setFragment(ConnectionFragment.class);

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
                setFragment(DiaryFragment.class);
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

    public boolean getState(){
        return connected;
    }

    public void setConnected(boolean b){
        connected = b;
    }
}
