package com.tomaszkopacz.pulseoxymeter.design;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;
import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.controller.MainApp;
import com.tomaszkopacz.pulseoxymeter.listeners.CommunicationFragmentListener;
import com.tomaszkopacz.pulseoxymeter.views.CommunicationFragmentView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tomaszkopacz on 27.11.17.
 */

public class CommunicationFragmentLayout implements CommunicationFragmentView {

    //general
    private View rootView;
    private CommunicationFragmentListener listener;

    //components
    @BindView(R.id.pulseTxtView)
    TextView pulseTextView;

    @BindView(R.id.pulseValueTextView)
    TextView pulseValueTextView;

    @BindView(R.id.saturationTxtView)
    TextView saturationTextView;

    @BindView(R.id.saturationValueTextView)
    TextView saturationValueTextView;

    @BindView(R.id.waveformGraph)
    GraphView waveformGraph;

    @BindView(R.id.startBtn)
    CircularProgressButton startBtn;


    /*==============================================================================================
                                        INITIALIZING
    ==============================================================================================*/

    //Constructor - inflates view from xml file and parses components.
    public CommunicationFragmentLayout(LayoutInflater inflater, ViewGroup views){

        rootView = inflater.inflate(R.layout.fragment_communication, views, false);
        ButterKnife.bind(this, rootView);

        customizeLayout();
    }

    @Override
    public void setListener(CommunicationFragmentListener listener){
        this.listener = listener;
    }

    @Override
    public View getView(){
        return rootView;
    }

    public TextView getPulseTextView(){
        return pulseValueTextView;
    }

    public TextView getSaturationTextView(){
        return saturationValueTextView;
    }

    public GraphView getWaveformGraph(){
        return waveformGraph;
    }


    /*==============================================================================================
                                        COMMUNICATION
    ==============================================================================================*/

    @Override
    public void startCommunication() {

    }

    @Override
    public void stopCommunication() {

    }

    @OnClick(R.id.startBtn)
    public void start(){
        listener.startReading();
    }


    /*==============================================================================================
                                        PRIVATE UTIL METHODS
    ==============================================================================================*/

    private void customizeLayout(){

        createFonts();
        startBtn.setIndeterminateProgressMode(true);
    }

    private void createFonts() {
        pulseTextView.setTypeface(MainApp.FONT_BOLD);
        pulseValueTextView.setTypeface(MainApp.FONT_BOLD);
        saturationTextView.setTypeface(MainApp.FONT_BOLD);
        saturationValueTextView.setTypeface(MainApp.FONT_BOLD);
        startBtn.setTypeface(MainApp.FONT_BOLD);
    }
}
