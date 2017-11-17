package com.tomaszkopacz.pulseoxymeter.controller;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.tomaszkopacz.pulseoxymeter.R;

public class DevicesListActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);
    }
}
