package com.tomaszkopacz.pulseoxymeter.listeners;

/**
 * Created by tomaszkopacz on 21.11.17.
 * Listener for ConnectionFragment buttons.
 */

public interface ConnectionFragmentListener {

    void btStateChanged(boolean state);

    void startScan();

    void stopScan();
}
