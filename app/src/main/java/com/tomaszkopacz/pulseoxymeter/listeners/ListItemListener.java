package com.tomaszkopacz.pulseoxymeter.listeners;

import android.widget.TextView;

/**
 * Created by tomaszkopacz on 09.08.17.
 * Listener for list item.
 */

public interface ListItemListener {

    void itemClicked(int position, TextView deviceNameTextView, TextView deviceInfoTextView);

    void itemLongClicked(int position, TextView deviceNameTextView, TextView deviceInfoTextView);
}
