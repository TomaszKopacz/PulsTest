package com.tomaszkopacz.pulseoxymeter.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.design.CommunicationActivityLayout;
import com.tomaszkopacz.pulseoxymeter.design.MainActivityLayout;
import com.tomaszkopacz.pulseoxymeter.listeners.CommunicationActivityListener;

public class CommunicationActivity
        extends AppCompatActivity
        implements CommunicationActivityListener{

    //layout
    private CommunicationActivityLayout mCommunicationActivityLayout;


    /*==============================================================================================
                                       LIFE CYCLE
     =============================================================================================*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCommunicationActivityLayout = new CommunicationActivityLayout(this);
        mCommunicationActivityLayout.setListener(this);
    }


    /*==============================================================================================
                                       LISTENERS
     =============================================================================================*/

    @Override
    public void onNavigationIconClick() {
        Intent backActivity = new Intent(CommunicationActivity.this, MainActivity.class);
        this.startActivity(backActivity);
    }
}
