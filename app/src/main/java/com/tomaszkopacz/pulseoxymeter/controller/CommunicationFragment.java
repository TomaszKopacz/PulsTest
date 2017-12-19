package com.tomaszkopacz.pulseoxymeter.controller;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
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

    private GraphView pulseTrendGraph;
    private TextView pulseAverageTextView;

    private GraphView saturationTrendGraph;
    private TextView saturationAverageTextView;

    private TextView pulseTextView;
    private TextView saturationTextView;
    private GraphView waveformGraph;

    //bluetooth
    private CommunicateService service;

    //incoming data
    private long startTime = -1;
    private long currentTime = -1;
    private double episode = 0;

    private int pulseValue = -1;
    private int saturationValue = -1;
    private int wavePoint = 0;

    private BarGraphSeries<DataPoint> pulseSeries;
    private BarGraphSeries<DataPoint> saturationSeries;
    private LineGraphSeries<DataPoint> waveform;
    private int trendPointer = 1;
    private int avgPointer = 0;
    private int pointer = 0;

    //status
    private boolean isReading = false;
    private int graphType = GRAPH_NORMAL;

    //maximal 7-bytes value of data element: 2^7 = 128
    private static final int MAX_VALUE = 128;
    private static final int AVERAGE_VALUES_SIZE = 30;
    private static int MAX_WAVEFORM_SIZE = 100000;

    //data: trend graphs
    private int[] pulseValuesOf30Sec = new int[AVERAGE_VALUES_SIZE];
    private int[] saturationValuesOf30Sec = new int[AVERAGE_VALUES_SIZE];

    //data: waveform
    private double[] timeArray = new double[MAX_WAVEFORM_SIZE];
    private int[] pulseArray = new int[MAX_WAVEFORM_SIZE];
    private int[] saturationArray = new int[MAX_WAVEFORM_SIZE];
    private int[] waveArray = new int[MAX_WAVEFORM_SIZE];

    private static final int GRAPH_NORMAL = 0;
    private static final int GRAPH_DIFFERENTIAL = 1;
    private static final String ALBUM_NAME = "/CMS";


    /*==============================================================================================
                                        LIFE CYCLE
    ==============================================================================================*/

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
        pulseTrendGraph = mCommunicationFragmentLayout.getPulseTrendGraph();
        pulseAverageTextView = mCommunicationFragmentLayout.getPulseAverageTextView();
        pulseSeries = (BarGraphSeries)pulseTrendGraph.getSeries().get(0);

        saturationTrendGraph = mCommunicationFragmentLayout.getSaturationTrendGraph();
        saturationAverageTextView = mCommunicationFragmentLayout.getSaturationAverageTextView();
        saturationSeries = (BarGraphSeries)saturationTrendGraph.getSeries().get(0);

        pulseTextView = mCommunicationFragmentLayout.getPulseTextView();
        saturationTextView = mCommunicationFragmentLayout.getSaturationTextView();
        waveformGraph = mCommunicationFragmentLayout.getWaveformGraph();

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
        service.stopHoldingCommunication();
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

            //read data
            service.holdCommunication(((MainActivity)getActivity()).getSocket());
            if (service.read(((MainActivity)getActivity()).getSocket()))
                isReading = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isReading = false;
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
        service.stopHoldingCommunication();

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
    public void stopReading(){
        if (isReading) {
            service.stopReading();
            isReading = false;
            CircularProgressButton stopBtn = mCommunicationFragmentLayout.getStopBtn();
            stopBtn.setText(R.string.end);
            stopBtn.setTextColor(getResources().getColor(R.color.colorAccent));
            stopBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            stopBtn.setClickable(false);
        }
    }

    @Override
    public void saveData() {

        if (isReading)
            Toast.makeText(getContext(), R.string.stop_reading, Toast.LENGTH_SHORT).show();

        else if (isExternalStorageWritable()){
            File file = getFileExternalDirectory();
            if (saveFile(file))
                Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
        }

        else
            Toast.makeText(getContext(), R.string.no_external_storage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void chooseGraphType() {

        if (isReading){
            Toast.makeText(getContext(), R.string.stop_reading, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] types = new String[]{"Krzywa PPG", "Pochodna"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.ok, acceptItemListener)
                .setNeutralButton(R.string.cancel, cancelDialogListener)
                .setSingleChoiceItems(types, graphType, chooseItemListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    DialogInterface.OnClickListener acceptItemListener
            = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            setGraph();
        }
    };

    DialogInterface.OnClickListener cancelDialogListener
            = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    };

    DialogInterface.OnClickListener chooseItemListener
            = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i){
                case 0:
                    graphType = GRAPH_NORMAL;
                    break;

                case 1:
                    graphType = GRAPH_DIFFERENTIAL;
                    break;
            }
        }
    };

    /*==============================================================================================
                                        EVENT LISTENERS
    ==============================================================================================*/

    @Override
    public void onDataIncome(final CMSData data) {

        //get bytes and transform to unsigned
        pulseValue = MAX_VALUE + data.getPulseByte();
        saturationValue = MAX_VALUE + data.getSaturationByte();
        wavePoint = MAX_VALUE + data.getWaveformByte();

        //count time episodes
        if (startTime == -1)
            startTime = System.currentTimeMillis() - 1;

        currentTime = System.currentTimeMillis();
        episode = episode + ((currentTime - startTime) * 0.001);

        //save to array
        timeArray[pointer] = episode;
        pulseArray[pointer] = pulseValue;
        saturationArray[pointer] = saturationValue;
        waveArray[pointer] = wavePoint;

        startTime = currentTime;

        //put values into interface
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    pulseTextView.setText(String.valueOf(pulseValue));
                    saturationTextView.setText(String.valueOf(saturationValue));
                    waveform.appendData(new DataPoint(episode, wavePoint), true, MAX_WAVEFORM_SIZE);

                    if (pointer % 60 == 0){
                        DataPoint pulsePoint = new DataPoint(trendPointer, pulseValue);
                        DataPoint satPoint = new DataPoint(trendPointer, saturationValue);

                        pulseSeries.appendData(pulsePoint, true, MAX_WAVEFORM_SIZE/60);
                        saturationSeries.appendData(satPoint, true, MAX_WAVEFORM_SIZE/60);

                        pulseValuesOf30Sec[avgPointer] = pulseValue;
                        saturationValuesOf30Sec[avgPointer] = saturationValue;

                        if (avgPointer == 29) {
                            int pulseAvg = countAverage(pulseValuesOf30Sec);
                            int satAvg = countAverage(saturationValuesOf30Sec);
                            pulseAverageTextView.setText("" + pulseAvg);
                            saturationAverageTextView.setText("" + satAvg);
                            avgPointer = 0;

                        } else {
                            avgPointer++;
                        }

                        trendPointer++;
                    }
                }
            });
        }

        pointer++;
    }

    @Override
    public void onConnectionOpenRequest() {
        isReading = true;
    }

    @Override
    public void onConnectionCloseRequest() {
        isReading = false;
    }


    /*==============================================================================================
                                        PRIVATE UTIL METHODS
    ==============================================================================================*/

    private void setGraph(){

        switch (graphType){
            case GRAPH_NORMAL:
                mCommunicationFragmentLayout.getWaveformGraph().getViewport().setMinY(0);
                mCommunicationFragmentLayout.getWaveformGraph().getViewport().setMaxY(128);
                waveform.resetData(countCurve(GRAPH_NORMAL, timeArray, waveArray));
                break;

            case GRAPH_DIFFERENTIAL:
                mCommunicationFragmentLayout.getWaveformGraph().getViewport().setMinY(-40);
                mCommunicationFragmentLayout.getWaveformGraph().getViewport().setMaxY(40);
                waveform.resetData(countCurve(GRAPH_DIFFERENTIAL, timeArray, waveArray));
                break;
        }
    }

    private int countAverage(int[] values){
        int avg = 0;
        for (int i = 0; i < values.length; i++)
            avg += values[i];
        avg = avg/values.length;

        return avg;
    }

    private DataPoint[] countCurve(int type, double[] time, int[] signal){

        //not all of primarily initialized 10000 elements of signal has value, need to cut them
        int lastNonZeroIndex = -1;
        int numOfPoints = signal.length;
        for (int i = 0; i < numOfPoints; i++){
            if (time[i] == 0)
                break;
            lastNonZeroIndex = i;
        }

        int size = lastNonZeroIndex + 1;

        switch (type){
            case GRAPH_NORMAL:
                DataPoint[] curve = new DataPoint[size];
                for (int i = 0; i < size; i++)
                    curve[i] = new DataPoint(time[i], signal[i]);
                return curve;

            case GRAPH_DIFFERENTIAL:
                return countDifferential(size, time, signal);
        }

        return null;
    }

    private DataPoint[] countDifferential(int size, double[] time, int[] signal){

        DataPoint[] differential = new DataPoint[size-1];

        //get time and count difference
        for (int i = 0; i < size - 1; i++){

            double timePoint = time[i];
            double signalPoint = signal[i+1] - signal[i];
            differential[i] = new DataPoint(timePoint, signalPoint);
        }

        return differential;
    }

    /**
     * Checks, whether external storage is available.
     * @return
     */
    private boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) )
            return true;

        return false;
    }

    /**
     * Gets album path and creates new file.
     * @return
     */
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

    /**
     * Saves data to a given file.
     * @param file
     * @return
     */
    private boolean saveFile(File file){

        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);

            for (int i = 0; i <= pointer; i++) {

                double time = timeArray[i];
                int pulse = pulseArray[i];
                int saturation = saturationArray[i];
                int wave = waveArray[i];

                if (pulse == 0)
                    continue;

                pw.println(time + "," + pulse + "," + saturation + "," + wave);
            }

            pw.close();
            fos.close();

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
