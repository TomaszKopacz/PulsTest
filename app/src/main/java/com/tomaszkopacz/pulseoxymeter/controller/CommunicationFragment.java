package com.tomaszkopacz.pulseoxymeter.controller;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
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
import com.tomaszkopacz.pulseoxymeter.btservice.CommunicateService;
import com.tomaszkopacz.pulseoxymeter.design.CommunicationFragmentLayout;
import com.tomaszkopacz.pulseoxymeter.design.MainActivityLayout;
import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothCallbacks;
import com.tomaszkopacz.pulseoxymeter.listeners.CommunicationFragmentListener;
import com.tomaszkopacz.pulseoxymeter.listeners.MainActivityListener;
import com.tomaszkopacz.pulseoxymeter.model.CMSData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


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
    private CommunicateService service;

    //incoming data
    private int pulseValue = -1;
    private int saturationValue = -1;
    private int wavePoint = 0;
    private LineGraphSeries<DataPoint> waveform;

    private int pointer = 0;

    //maximal 7-bytes value of data element: 2^7 = 128
    private static final int MAX_VALUE = 128;

    //data for saving file
    private int[] pulseArray = new int[3000];
    private int[] saturationArray = new int[3000];
    private int[] waveArray = new int[3000];
    private static final String ALBUM_NAME = "/CMS";



    /*==============================================================================================
                                        LIFE CYCLE
    ==============================================================================================*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public void onStart() {
        super.onStart();

        //bind bt service
        Intent intent = new Intent(getActivity(), CommunicateService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unbindService(connection);
        service.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /*==============================================================================================
                                       BLUETOOTH SERVICE
     =============================================================================================*/

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            CommunicateService.CommunicateBinder binder
                    = (CommunicateService.CommunicateBinder) iBinder;
            service = binder.getService();

            registerCallback();

            //hold bt communication
            service.holdCommunication(((MainActivity)getActivity()).getSocket());

            //read data
            startReading();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };


    private void registerCallback(){
        service.registerCallback(this);
    }


    /*==============================================================================================
                                        ON CLICK LISTENERS
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
    public void startReading(){
        service.read(((MainActivity)getActivity()).getSocket());
    }

    @Override
    public void saveData() {

        if (isExternalStorageWritable()){
            File file = getFileExternalDirectory();

            try {
                FileOutputStream fos = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(fos);

                for (int i = 0; i <= pointer; i++) {

                    int pulse = pulseArray[i];
                    int saturation = saturationArray[i];
                    int wave = waveArray[i];

                    if (pulse == 0)
                        continue;

                    pw.println(pulse + "," + saturation + "," + wave);
                }

                pw.close();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    /*==============================================================================================
                                        EVENT LISTENERS
    ==============================================================================================*/

    @Override
    public void onDataIncome(final CMSData data) {

        //get bytes and transform to unsigned
        pulseValue = MAX_VALUE + data.getPulseByte();
        saturationValue = MAX_VALUE + data.getSaturationByte();
        wavePoint = MAX_VALUE + data.getWaveformByte();

        //save to array
        pulseArray[pointer] = pulseValue;
        saturationArray[pointer] = saturationValue;
        waveArray[pointer] = wavePoint;

        //put values into interface
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    pulseTextView.setText(String.valueOf(pulseValue));
                    saturationTextView.setText(String.valueOf(saturationValue));
                    waveform.appendData(new DataPoint(pointer, wavePoint), true, 5000);
                    pointer++;
                }
            });
        }
    }

    @Override
    public void onConnectionOpenRequest() {

    }

    @Override
    public void onConnectionCloseRequest() {

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

    private boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
            return true;
        return false;
    }

    private File getFileExternalDirectory(){

        //create album if not exists
        String albumPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + ALBUM_NAME;

        File album = new File(albumPath);

        if (!album.exists())
            album.mkdirs();

        //create file
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String fileName = s.format(new Date()) + ".csv";
        File file = new File(albumPath, fileName);

        try {
            file.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
