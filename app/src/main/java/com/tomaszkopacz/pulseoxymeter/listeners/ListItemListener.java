package com.tomaszkopacz.pulseoxymeter.listeners;

import android.widget.TextView;

/**
 * Created by tomaszkopacz on 09.08.17.
 */

public abstract class ListItemListener {

    /**
     * Runs, when clicked item from a list.
     * @param position
     * @param deviceNameTextView
     * @param deviceInfoTextView
     */
    public abstract void itemClicked(int position, TextView deviceNameTextView, TextView deviceInfoTextView);
}
