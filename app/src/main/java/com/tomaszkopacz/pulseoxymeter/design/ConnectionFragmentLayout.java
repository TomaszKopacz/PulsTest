package com.tomaszkopacz.pulseoxymeter.design;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.controller.MainApp;
import com.tomaszkopacz.pulseoxymeter.listeners.ListItemListener;
import com.tomaszkopacz.pulseoxymeter.listeners.ConnectionFragmentListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by tomaszkopacz on 17.11.17.
 * View - layout for ConnectionFragment.
 */

public class ConnectionFragmentLayout {

    //general
    private View rootView;
    private ConnectionFragmentListener listener;

    //components
    @BindView(R.id.btTxtView)
    TextView btTextView;

    @BindView(R.id.enableBTSwitch)
    Switch btSwitch;

    @BindView(R.id.pairedDevTxtView)
    TextView pairedDevicesTextView;

    @BindView(R.id.pairedDevRelLayout)
    RelativeLayout pairedDevicesRelLayout;

    @BindView(R.id.pairedDevicesRecView)
    RecyclerView pairedDevicesRecView;

    @BindView(R.id.discovDevTxtView)
    TextView discoveredDevicesTextView;

    @BindView(R.id.discovDevRelLayout)
    RelativeLayout discoveredDevicesRelLayout;

    @BindView(R.id.discoveredDevicesRecView)
    RecyclerView discoveredDevicesRecView;

    @BindView(R.id.scanBtn)
    CircularProgressButton scanBtn;

    //lists
    private DevicesAdapter pairedDevicesAdapter;
    private DevicesAdapter discoveredDevicesAdapter;

    //scan button
    public static final int BUTTON_LAZY = 0;
    public static final int BUTTON_IN_PROGRESS = 10;


    /*==============================================================================================
                                        INITIALIZING
    ==============================================================================================*/

    //Constructor - inflates view from xml file and parses components.
    public ConnectionFragmentLayout(LayoutInflater inflater, ViewGroup views){

        rootView = inflater.inflate(R.layout.fragment_connection, views, false);
        ButterKnife.bind(this, rootView);

        customizeLayout();
    }


    public void setListener(final ConnectionFragmentListener listener) {
        this.listener = listener;
    }

    public View getView() {
        return rootView;
    }

    public Switch getBtSwitch(){
        return btSwitch;
    }

    public CircularProgressButton getScanBtn(){
        return scanBtn;
    }


    /*==============================================================================================
                                            CONTROLS
    ==============================================================================================*/

    @OnCheckedChanged(R.id.enableBTSwitch)
    public void btSwitchChanged(boolean b){
        if (listener != null)
            listener.btStateChanged(b);

        if (!b)
            stopScan();
    }

    @OnClick(R.id.scanBtn)
    public void onScanBtnClick(){
        switch (scanBtn.getProgress()) {

            case BUTTON_LAZY:
                startScan();
                break;

            case BUTTON_IN_PROGRESS:
                stopScan();
                break;
        }
    }


    /*==============================================================================================
                                        LISTS
    ==============================================================================================*/

    public void createPairedDevicesList(List<BluetoothDevice> devices, ListItemListener listener) {

        //create settings
        pairedDevicesAdapter = new DevicesAdapter(devices, listener);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(rootView.getContext());

        //set recycler view
        pairedDevicesRecView.setAdapter(pairedDevicesAdapter);
        pairedDevicesRecView.setLayoutManager(layout);
    }

    public void createDiscoveredDevicesList(List<BluetoothDevice> devices, ListItemListener listener) {

        //create settings
        discoveredDevicesAdapter = new DevicesAdapter(devices, listener);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(rootView.getContext());

        //set recycler view
        discoveredDevicesRecView.setAdapter(discoveredDevicesAdapter);
        discoveredDevicesRecView.setLayoutManager(layout);
    }

    public void notifyInsertToPairedDevices(int position) {
        pairedDevicesAdapter.notifyItemInserted(position);
    }

    public void notifyInsertToDiscoveredDevices(int position) {
        discoveredDevicesAdapter.notifyItemInserted(position);
    }

    public void notifyRemoveFromDiscoveredDevices(int position) {
        discoveredDevicesAdapter.notifyItemRemoved(position);
    }


    /*==============================================================================================
                                        PRIVATE UTIL METHODS
    ==============================================================================================*/

    private void customizeLayout() {

        setFonts();
        scanBtn.setIndeterminateProgressMode(true);
    }

    private void setFonts(){
        btTextView.setTypeface(MainApp.FONT_BOLD);
        pairedDevicesTextView.setTypeface(MainApp.FONT_BOLD);
        discoveredDevicesTextView.setTypeface(MainApp.FONT_BOLD);
        scanBtn.setTypeface(MainApp.FONT_BOLD);
    }

    private void startScan() {
        if (btSwitch.isChecked())
            scanBtn.setProgress(BUTTON_IN_PROGRESS);

        if (listener != null)
            listener.startScan();
    }

    private void stopScan() {
        if (listener != null)
            listener.stopScan();

        scanBtn.setProgress(BUTTON_LAZY);
    }

}
