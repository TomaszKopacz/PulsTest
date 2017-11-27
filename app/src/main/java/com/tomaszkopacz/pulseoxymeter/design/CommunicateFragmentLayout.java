package com.tomaszkopacz.pulseoxymeter.design;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.jjoe64.graphview.GraphView;
import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.listeners.CommunicationFragmentListener;
import com.tomaszkopacz.pulseoxymeter.views.CommunicateFragmentView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tomaszkopacz on 27.11.17.
 */

public class CommunicateFragmentLayout implements CommunicateFragmentView{

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
    public CommunicateFragmentLayout(LayoutInflater inflater, ViewGroup views){

        rootView = inflater.inflate(R.layout.fragment_communicate, views, false);
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


    /*==============================================================================================
                                        COMMUNICATION
    ==============================================================================================*/

    @Override
    public void startCommunication() {

    }

    @Override
    public void stopCommunication() {

    }


    /*==============================================================================================
                                        LISTENERS
    ==============================================================================================*/

    private View.OnClickListener navigationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            listener.onNavigationItemClicked();
        }
    };


    /*==============================================================================================
                                        PRIVATE UTIL METHODS
    ==============================================================================================*/

    private void customizeLayout(){

        //set toolbar

        createFonts();
        startBtn.setIndeterminateProgressMode(true);
    }

    private void createFonts() {
        pulseTextView.setTypeface(MainActivityLayout.FONT_BOLD);
        pulseValueTextView.setTypeface(MainActivityLayout.FONT_BOLD);
        saturationTextView.setTypeface(MainActivityLayout.FONT_BOLD);
        saturationValueTextView.setTypeface(MainActivityLayout.FONT_BOLD);
        startBtn.setTypeface(MainActivityLayout.FONT_BOLD);
    }
}
