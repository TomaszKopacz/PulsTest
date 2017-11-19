package com.tomaszkopacz.pulseoxymeter.design;

import android.widget.TextView;

/**
 * Created by tomaszkopacz on 19.11.17.
 */

public interface DeviceItemView extends GeneralView {

    /**
     * Setter for device name TextView.
     * @param view
     */
    void setNameTextView(TextView view);

    /**
     * Setter for device info TextView.
     * @param view
     */
    void setInfoTextView(TextView view);

    /**
     * Getter for device name TextView.
     * @return TextView
     */
    TextView getNameTextView();

    /**
     * Getter for device info TextView.
     * @return TextView
     */
    TextView getInfoTextView();
}
