package com.tomaszkopacz.pulseoxymeter.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tomaszkopacz.pulseoxymeter.design.MainActivityLayout;

public class MainActivity extends AppCompatActivity {

    //layout
    private MainActivityLayout mainActivityLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityLayout = new MainActivityLayout(this);
    }

}
