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

public class GuideFragmentLayout {

    //general
    private View rootView;

    //layout
    @BindView(R.id.guideTextView)
    TextView guideTextView;


    /*==============================================================================================
                                        INITIALIZING
    ==============================================================================================*/

    public GuideFragmentLayout(LayoutInflater inflater, ViewGroup container){
        this.rootView = inflater.inflate(R.layout.fragment_guide, container, false);
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
        guideTextView.setTypeface(MainApp.FONT_REGULAR);
    }

    private void setText(){
        guideTextView.setText(R.string.guide_text);
    }
}
