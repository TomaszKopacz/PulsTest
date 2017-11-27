package com.tomaszkopacz.pulseoxymeter.views;

import android.view.View;

import com.tomaszkopacz.pulseoxymeter.listeners.CommunicationFragmentListener;

/**
 * Created by tomaszkopacz on 27.11.17.
 */

public interface CommunicateFragmentView {

    /**
     * Returns root view.
     * @return View
     */
    View getView();

    /**
     * Sets listener.
     * @param listener
     */
    void setListener(CommunicationFragmentListener listener);

    /**
     * Starts data exchange.
     */
    void startCommunication();

    /**
     * Stops data exchange.
     */
    void stopCommunication();
}
