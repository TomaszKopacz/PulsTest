package com.tomaszkopacz.pulseoxymeter.design;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.controller.MainApp;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tomaszkopacz on 29.12.17.
 */

public class InfoFragmentLayout {

    //general
    private View rootView;

    //layout
    @BindView(R.id.infoTextView)
    TextView infoTextView;


    /*==============================================================================================
                                        INITIALIZING
    ==============================================================================================*/


    public InfoFragmentLayout(LayoutInflater inflater, ViewGroup container){
        this.rootView = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, rootView);

        customizeLayout();
    }

    public View getView(){
        return rootView;
    }


    /*==============================================================================================
                                        PRIVATE UTIL METHODS
    ==============================================================================================*/

    private void customizeLayout(){
        setFonts();
        setText();
    }

    private void setFonts(){
        infoTextView.setTypeface(MainApp.FONT_REGULAR);
    }

    private void setText(){
        infoTextView.setText(R.string.info_text);
    }
}
