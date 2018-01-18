package com.tomaszkopacz.pulseoxymeter.listeners;

/**
 * Created by tomaszkopacz on 27.11.17.
 * Listener for CommunicationFragment buttons.
 */

public interface CommunicationFragmentListener {

    void showHRVInfo();

    void stopReading();

    void saveData();
}
