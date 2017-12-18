package com.tomaszkopacz.pulseoxymeter.listeners;

/**
 * Created by tomaszkopacz on 27.11.17.
 */

public interface CommunicationFragmentListener {

    void stopReading();

    void saveData();

    void chooseGraphType();
}
