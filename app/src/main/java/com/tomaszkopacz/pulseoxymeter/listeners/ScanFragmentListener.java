package com.tomaszkopacz.pulseoxymeter.listeners;

/**
 * Created by tomaszkopacz on 21.11.17.
 */

public interface ScanFragmentListener {


    /**
     * Runs, when bluetooth state is changed manually (e.g. by switch).
     * @param state
     */
    void btStateChanged(boolean state);

    /**
     * Runs, when toolbar options item is clicked.
     */
    void optionsItemClicked();

    /**
     * Runs, when scan is started.
     */
    void startScan();

    /**
     * Runs, when scan is stopped.
     */
    void stopScan();
}
