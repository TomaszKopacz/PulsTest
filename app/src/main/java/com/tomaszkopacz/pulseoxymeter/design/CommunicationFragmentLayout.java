package com.tomaszkopacz.pulseoxymeter.design;

import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.controller.MainApp;
import com.tomaszkopacz.pulseoxymeter.listeners.CommunicationFragmentListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tomaszkopacz on 27.11.17.
 */

public class CommunicationFragmentLayout {

    //general
    private View rootView;
    private CommunicationFragmentListener listener;

    //components
    @BindView(R.id.pulseTxtView)
    TextView pulseTextView;

    @BindView(R.id.pulseValueTextView)
    TextView pulseValueTextView;

    @BindView(R.id.pulseTrendGraph)
    GraphView pulseTrendGraph;

    @BindView(R.id.pulseAverageTextView)
    TextView pulseAverageTextView;

    @BindView(R.id.saturationTxtView)
    TextView saturationTextView;

    @BindView(R.id.saturationValueTextView)
    TextView saturationValueTextView;

    @BindView(R.id.saturationTrendGraph)
    GraphView saturationTrendGraph;

    @BindView(R.id.saturationAverageTextView)
    TextView saturationAverageTextView;

    @BindView(R.id.waveformGraph)
    GraphView waveformGraph;

    @BindView(R.id.diffGraph)
    GraphView diffGraph;

    @BindView(R.id.rrGraph)
    GraphView rrGraph;

    @BindView(R.id.stopBtn)
    CircularProgressButton stopBtn;

    @BindView(R.id.saveBtn)
    ImageButton saveBtn;


    /*==============================================================================================
                                        INITIALIZING
    ==============================================================================================*/

    //Constructor - inflates view from xml file and parses components.
    public CommunicationFragmentLayout(LayoutInflater inflater, ViewGroup views){

        rootView = inflater.inflate(R.layout.fragment_communication, views, false);
        ButterKnife.bind(this, rootView);

        customizeLayout();
    }

    public void setListener(CommunicationFragmentListener listener){
        this.listener = listener;
    }

    public View getView(){
        return rootView;
    }

    public TextView getPulseTextView(){
        return pulseValueTextView;
    }

    public GraphView getPulseTrendGraph(){
        return pulseTrendGraph;
    }

    public TextView getPulseAverageTextView(){
        return pulseAverageTextView;
    }

    public TextView getSaturationTextView(){
        return saturationValueTextView;
    }

    public GraphView getSaturationTrendGraph(){
        return saturationTrendGraph;
    }

    public TextView getSaturationAverageTextView(){
        return saturationAverageTextView;
    }

    public GraphView getWaveformGraph(){
        return waveformGraph;
    }

    public GraphView getDiffGraph(){
        return diffGraph;
    }

    public GraphView getRrGraph(){
        return rrGraph;
    }

    public CircularProgressButton getStopBtn(){
        return stopBtn;
    }


    /*==============================================================================================
                                        COMMUNICATION
    ==============================================================================================*/

    @OnClick(R.id.stopBtn)
    public void stop(){
        listener.stopReading();
    }

    @OnClick(R.id.rrGraph)
    public void showHRVInfo(){
        listener.showHRVInfo();
    }

    @OnClick(R.id.saveBtn)
    public void save(){
        listener.saveData();
    }


    /*==============================================================================================
                                        PRIVATE UTIL METHODS
    ==============================================================================================*/

    private void customizeLayout(){

        setFonts();
        createPulseTrendWaveform();
        createSaturationTrendWaveform();
        createWaveform();
        createDiffWaveform();
        createRRWaveform();
    }

    private void setFonts() {
        pulseTextView.setTypeface(MainApp.FONT_BOLD);
        pulseValueTextView.setTypeface(MainApp.FONT_BOLD);
        saturationTextView.setTypeface(MainApp.FONT_BOLD);
        saturationValueTextView.setTypeface(MainApp.FONT_BOLD);
        stopBtn.setTypeface(MainApp.FONT_BOLD);
    }

    private void createPulseTrendWaveform(){
        pulseTrendGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        pulseTrendGraph.getGridLabelRenderer().setNumVerticalLabels(3);
        Viewport viewport = pulseTrendGraph.getViewport();
        viewport.setScalable(true);
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(120);
        viewport.setMinX(0);
        viewport.setMaxX(60);
    }

    private void createSaturationTrendWaveform(){
        saturationTrendGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        saturationTrendGraph.getGridLabelRenderer().setNumVerticalLabels(3);
        Viewport viewport = saturationTrendGraph.getViewport();
        viewport.setScalable(true);
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(80);
        viewport.setMaxY(100);
        viewport.setMinX(0);
        viewport.setMaxX(60);
    }

    private void createWaveform(){
        waveformGraph.setTitle("PPG");
        waveformGraph.getGridLabelRenderer().setNumHorizontalLabels(6);
        Viewport viewport = waveformGraph.getViewport();
        viewport.setScalable(false);
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setYAxisBoundsManual(true);
        viewport.scrollToEnd();
        viewport.setMinX(0);
        viewport.setMaxX(5);
        viewport.setMinY(0);
        viewport.setMaxY(128);
    }

    private void createDiffWaveform(){
        diffGraph.setTitle("Pochodna");
        diffGraph.getGridLabelRenderer().setNumVerticalLabels(3);
        diffGraph.getGridLabelRenderer().setNumHorizontalLabels(6);
        Viewport viewport = diffGraph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setScrollable(true);
        viewport.setScalable(false);
        viewport.setMinY(-30);
        viewport.setMaxY(30);
        viewport.setMinX(0);
        viewport.setMaxX(5);
    }

    private void createRRWaveform(){
        rrGraph.setTitle("RR");
        rrGraph.getGridLabelRenderer().setNumVerticalLabels(3);
        Viewport viewport = rrGraph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setScrollable(true);
        viewport.setMinY(0);
        viewport.setMaxY(1.2);
        viewport.setMinX(0);
        viewport.setMaxX(10);
    }
}
