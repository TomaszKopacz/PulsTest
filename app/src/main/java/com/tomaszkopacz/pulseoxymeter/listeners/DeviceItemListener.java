package com.tomaszkopacz.pulseoxymeter.listeners;

import android.widget.TextView;

/**
 * Created by tomaszkopacz on 09.08.17.
 */

public abstract class DeviceItemListener {

    public abstract void itemClicked(int position, TextView deviceNameTextView, TextView deviceAddressTextView);
}
