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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
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
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.btservice.CommunicateService;
import com.tomaszkopacz.pulseoxymeter.design.CommunicationFragmentLayout;
import com.tomaszkopacz.pulseoxymeter.design.HRVDialogLayout;
import com.tomaszkopacz.pulseoxymeter.design.MainActivityLayout;
import com.tomaszkopacz.pulseoxymeter.listeners.BluetoothCallbacks;
import com.tomaszkopacz.pulseoxymeter.listeners.CommunicationFragmentListener;
import com.tomaszkopacz.pulseoxymeter.listeners.MainActivityListener;
import com.tomaszkopacz.pulseoxymeter.model.CMSData;
import com.tomaszkopacz.pulseoxymeter.utils.MyMath;

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

    private GraphView diffGraph;
    private GraphView rrGraph;

    private AlertDialog mHrvDialog;
    private HRVDialogLayout mHrvDialogLayout;

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
    private LineGraphSeries<DataPoint> waveformSeries;
    private LineGraphSeries<DataPoint> diffSeries;
    private BarGraphSeries<DataPoint> rrSeries;
    private int trendPointer = 1;
    private int avgPointer = 0;
    private int pointer = -1;

    //status
    private boolean isReading = false;

    //maximal 7-bytes value of data element: 2^7 = 128
    private static final int AVERAGE_VALUES_SIZE = 15;
    private static int MAX_WAVEFORM_SIZE = 1300000; //about 6h

    //data: trend graphs
    private double[] pulseValuesOf15Sec = new double[AVERAGE_VALUES_SIZE];
    private double[] saturationValuesOf15Sec = new double[AVERAGE_VALUES_SIZE];

    //data: waveformSeries
    private double[] timeArray = new double[MAX_WAVEFORM_SIZE];
    private int[] pulseArray = new int[MAX_WAVEFORM_SIZE];
    private int[] saturationArray = new int[MAX_WAVEFORM_SIZE];
    private int[] waveArray = new int[MAX_WAVEFORM_SIZE];

    //data: differential
    private double[] differential = new double[MAX_WAVEFORM_SIZE];

    //data: HRV
    private double[] RRs;
    private double mnnValue;
    private double sdnnValue;
    private double rrmssdValue;
    private int nn50Value;
    private double pnn50Value;
    private double sdsdValue;
    private DataPoint[] poincarePoints;

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
        pulseTextView = mCommunicationFragmentLayout.getPulseTextView();
        pulseTrendGraph = mCommunicationFragmentLayout.getPulseTrendGraph();
        pulseAverageTextView = mCommunicationFragmentLayout.getPulseAverageTextView();
        pulseSeries = (BarGraphSeries)pulseTrendGraph.getSeries().get(0);

        saturationTextView = mCommunicationFragmentLayout.getSaturationTextView();
        saturationTrendGraph = mCommunicationFragmentLayout.getSaturationTrendGraph();
        saturationAverageTextView = mCommunicationFragmentLayout.getSaturationAverageTextView();
        saturationSeries = (BarGraphSeries)saturationTrendGraph.getSeries().get(0);

        waveformGraph = mCommunicationFragmentLayout.getWaveformGraph();
        waveformSeries = (LineGraphSeries)waveformGraph.getSeries().get(0);

        diffGraph = mCommunicationFragmentLayout.getDiffGraph();
        diffSeries = (LineGraphSeries)diffGraph.getSeries().get(0);

        rrGraph = mCommunicationFragmentLayout.getRrGraph();
        rrSeries = (BarGraphSeries)rrGraph.getSeries().get(0);

        //dialog layout
        mHrvDialogLayout = new HRVDialogLayout(getContext());

        return mCommunicationFragmentLayout.getView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //bind bt service
        Intent intent = new Intent(getActivity(), CommunicateService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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

        //close service reading
        if (isReading) {
            service.stopReading();
            isReading = false;

            disableStopButton();

            RRs = MyMath.countRR(timeArray, differential);
            setRRGraph();

            createDialog();
        }
    }

    @Override
    public void showHRVInfo() {

        if (!isReading){

            countValues();
            putValuesToDialog();
            mHrvDialog.show();

        } else
            Toast.makeText(getContext(), R.string.stop_reading, Toast.LENGTH_SHORT).show();

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


    /*==============================================================================================
                                        EVENT LISTENERS
    ==============================================================================================*/

    @Override
    public void onDataIncome(final CMSData data) {

        pointer++;
        if (pointer == MAX_WAVEFORM_SIZE)
            stopReading();

        pulseValue = data.getPulseByte();
        saturationValue = data.getSaturationByte();
        wavePoint = data.getWaveformByte();

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

                    //waveformSeries
                    pulseTextView.setText(String.valueOf(pulseValue));
                    saturationTextView.setText(String.valueOf(saturationValue));
                    waveformSeries.appendData(
                            new DataPoint(episode, wavePoint), true, MAX_WAVEFORM_SIZE);

                    //differential
                    if (pointer > 0) {
                        double diff = (waveArray[pointer] - waveArray[pointer-1]);
                        differential[pointer-1] = diff;
                        DataPoint diffPoint = new DataPoint(timeArray[pointer-1], diff);
                        diffSeries.appendData(diffPoint, true, MAX_WAVEFORM_SIZE);
                    }

                    //trends
                    if (pointer % 60 == 0){
                        DataPoint pulsePoint = new DataPoint(trendPointer, pulseValue);
                        DataPoint satPoint = new DataPoint(trendPointer, saturationValue);

                        pulseSeries.appendData(pulsePoint, true, MAX_WAVEFORM_SIZE/60);
                        saturationSeries.appendData(satPoint, true, MAX_WAVEFORM_SIZE/60);

                        pulseValuesOf15Sec[avgPointer] = pulseValue;
                        saturationValuesOf15Sec[avgPointer] = saturationValue;

                        if (avgPointer == 14) {
                            int pulseAvg = (int) MyMath.countAverage(pulseValuesOf15Sec);
                            int satAvg = (int) MyMath.countAverage(saturationValuesOf15Sec);

                            pulseAverageTextView.setText(String.valueOf(pulseAvg));
                            saturationAverageTextView.setText(String.valueOf(satAvg));
                            avgPointer = 0;

                        } else {
                            avgPointer++;
                        }

                        trendPointer++;
                    }
                }
            });
        }
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

    private void disableStopButton(){
        CircularProgressButton stopBtn = mCommunicationFragmentLayout.getStopBtn();
        stopBtn.setText(R.string.end);
        stopBtn.setTextColor(getResources().getColor(R.color.colorAccent));
        stopBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        stopBtn.setClickable(false);
    }

    private void setRRGraph(){
        for (int i = 0; i < RRs.length; i++){
            DataPoint point = new DataPoint(i, RRs[i]);
            rrSeries.appendData(point, true, 6000);
        }
    }

    private void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(R.string.hrv_title)
                .setView(mHrvDialogLayout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        mHrvDialog = builder.create();
    }

    private void countValues(){
        mnnValue = MyMath.round(MyMath.countAverage(RRs), 3);
        sdnnValue = MyMath.round(MyMath.countStandardDeviation(RRs), 3);
        rrmssdValue = MyMath.round(MyMath.countRMSSD(RRs), 3);
        nn50Value = MyMath.countNN50(RRs);
        pnn50Value = MyMath.round(MyMath.countPNN50(RRs), 1);
        sdsdValue = MyMath.round(MyMath.countSDSD(RRs), 3);
        poincarePoints = MyMath.countPoincarePoints(RRs);
    }

    private void putValuesToDialog(){
        String mnn = "MNN: " + mnnValue + "s";
        String sdnn = "SDNN: " + sdnnValue + "s";
        String rmssd = "RMSSD: " + rrmssdValue + "s";
        String nn50 = "NN50: " + nn50Value;
        String pnn50 = "PNN50: " + pnn50Value + "%";
        String sdsd = "SDSD: " + sdsdValue + "s";

        mHrvDialogLayout.getMnn().setText(mnn);
        mHrvDialogLayout.getSdnn().setText(sdnn);
        mHrvDialogLayout.getRmssd().setText(rmssd);
        mHrvDialogLayout.getNn50().setText(nn50);
        mHrvDialogLayout.getPnn50().setText(pnn50);
        mHrvDialogLayout.getSdsd().setText(sdsd);

        PointsGraphSeries series = new PointsGraphSeries(poincarePoints);
        series.setSize(5);
        mHrvDialogLayout.getPoincareGraph().addSeries(series);
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
