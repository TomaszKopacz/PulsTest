package com.tomaszkopacz.pulseoxymeter.controller;

import android.content.Intent;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public interface BluetoothListener {

    void onBtEventAppears(Intent intent, int event);
}
