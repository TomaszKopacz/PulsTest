package com.tomaszkopacz.pulseoxymeter.design;

import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.jjoe64.graphview.GraphView;
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

    @BindView(R.id.saturationTxtView)
    TextView saturationTextView;

    @BindView(R.id.saturationValueTextView)
    TextView saturationValueTextView;

    @BindView(R.id.waveformGraph)
    GraphView waveformGraph;

    @BindView(R.id.saveBtn)
    FloatingActionButton saveBtn;


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

    public TextView getSaturationTextView(){
        return saturationValueTextView;
    }

    public GraphView getWaveformGraph(){
        return waveformGraph;
    }


    /*==============================================================================================
                                        COMMUNICATION
    ==============================================================================================*/

    @OnClick(R.id.waveformGraph)
    public void graphClicked(){
        listener.stopReading();
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
    }

    private void setFonts() {
        pulseTextView.setTypeface(MainApp.FONT_BOLD);
        pulseValueTextView.setTypeface(MainApp.FONT_BOLD);
        saturationTextView.setTypeface(MainApp.FONT_BOLD);
        saturationValueTextView.setTypeface(MainApp.FONT_BOLD);
    }
}
