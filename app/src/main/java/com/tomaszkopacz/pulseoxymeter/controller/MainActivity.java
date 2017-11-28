package com.tomaszkopacz.pulseoxymeter.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tomaszkopacz.pulseoxymeter.design.MainActivityLayout;
import com.tomaszkopacz.pulseoxymeter.listeners.MainActivityListener;

public class MainActivity
        extends AppCompatActivity
        implements MainActivityListener{

    //layout
    private MainActivityLayout mMainActivityLayout;


    /*==============================================================================================
                                       LIFE CYCLE
     =============================================================================================*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivityLayout = new MainActivityLayout(this);
        mMainActivityLayout.setListener(this);
    }


    /*==============================================================================================
                                       LISTENERS
     =============================================================================================*/

    @Override
    public void onNavigationIconClick() {

    }

    @Override
    public void onMenuItemSelected(int item) {
        switch (item){
            case MainActivityLayout.CONNECT_ITEM:
                mMainActivityLayout.setFragmentContent(ConnectionFragment.class);
                break;

            case MainActivityLayout.DIARY_ITEM:
                mMainActivityLayout.setFragmentContent(DiaryFragment.class);
                break;
        }
    }
}
