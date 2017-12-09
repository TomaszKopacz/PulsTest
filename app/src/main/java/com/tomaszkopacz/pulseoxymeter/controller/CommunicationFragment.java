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
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
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

    private TextView pulseTextView;
    private TextView saturationTextView;
    private GraphView waveformGraph;

    //bluetooth
    private ConnectionService service;
    private boolean bound = false;

    //data
    private int pulseValue = -1;
    private int saturationValue = -1;
    private int wavePoint = 0;
    private LineGraphSeries<DataPoint> waveform;

    private int pointer = 0;

    //maximal 7-bytes value of data element: 2^7 = 128
    private static final int MAX_VALUE = 128;


    /*==============================================================================================
                                        LIFE CYCLE
    ==============================================================================================*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //bind bt service
        Intent intent = new Intent(getActivity(), ConnectionService.class);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        bound = true;
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

        //components
        pulseTextView = mCommunicationFragmentLayout.getPulseTextView();
        saturationTextView = mCommunicationFragmentLayout.getSaturationTextView();
        waveformGraph = mCommunicationFragmentLayout.getWaveformGraph();
        createWaveform(waveformGraph);

        waveform = (LineGraphSeries)waveformGraph.getSeries().get(0);

        return mCommunicationFragmentLayout.getView();
    }

    @Override
    public void onDestroy() {
        Log.d("TomaszKopacz", "on destroy");
        if (bound){
            getActivity().unbindService(mServiceConnection);
            service.unbind();
            service = null;
            bound = false;
        }

        super.onDestroy();
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

        //disable service timer
        service.unbind();

        //give back default activity settings and go back to connection fragment
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
    public void startReading(){
        service.read();
    }

    @Override
    public void onDataIncome(final CMSData data) {

        //get bytes and transform to unsigned
        pulseValue = MAX_VALUE + data.getPulseByte();
        saturationValue = MAX_VALUE + data.getSaturationByte();
        wavePoint = MAX_VALUE + data.getWaveformByte();

        //set values to interface
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                pulseTextView.setText(String.valueOf(pulseValue));
                saturationTextView.setText(String.valueOf(saturationValue));
                waveform.appendData(new DataPoint(pointer, wavePoint), true, 10000);
                pointer++;
            }
        });

    }


    /*==============================================================================================
                                        PRIVATE UTIL METHODS
    ==============================================================================================*/

    private void createWaveform(GraphView graph){

        Viewport viewport = graph.getViewport();
        viewport.setScalable(false);
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        viewport.scrollToEnd();
        viewport.setMinX(0);
        viewport.setMaxX(500);
        viewport.setMinY(0);
        viewport.setMaxY(128);
    }
}
