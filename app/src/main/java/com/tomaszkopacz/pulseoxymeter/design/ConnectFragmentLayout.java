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
import com.tomaszkopacz.pulseoxymeter.listeners.ListItemListener;
import com.tomaszkopacz.pulseoxymeter.listeners.ScanFragmentListener;
import com.tomaszkopacz.pulseoxymeter.views.ConnectFragmentView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public class ConnectFragmentLayout implements ConnectFragmentView {

    //general
    private View rootView;
    private ScanFragmentListener listener;

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
    private final int BUTTON_LAZY = 0;
    private final int BUTTON_IN_PROGRESS = 10;


    /*==============================================================================================
                                        INITIALIZING
    ==============================================================================================*/

    //Constructor - inflates view from xml file and parses components.
    public ConnectFragmentLayout(LayoutInflater inflater, ViewGroup views){
        rootView = inflater.inflate(R.layout.fragment_connect, views, false);
        ButterKnife.bind(this, rootView);

        customizeLayout();
    }

    @Override
    public void setListener(final ScanFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView() {
        return rootView;
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
    public void onScanBtnPressed(){
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
                                        REACTIONS
    ==============================================================================================*/


    @Override
    public void notifyBtState(boolean b) {
        btSwitch.setChecked(b);

        if (!b)
            stopScan();
    }

    @Override
    public void notifyBtScanStateChanged(boolean b) {
        if (b)
            scanBtn.setProgress(BUTTON_IN_PROGRESS);
        else
            scanBtn.setProgress(BUTTON_LAZY);
    }


    /*==============================================================================================
                                        LISTS
    ==============================================================================================*/

    @Override
    public void createPairedDevicesList(List<BluetoothDevice> devices, ListItemListener listener) {

        //create settings
        pairedDevicesAdapter = new DevicesAdapter(devices, listener);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(rootView.getContext());

        //set recycler view
        pairedDevicesRecView.setAdapter(pairedDevicesAdapter);
        pairedDevicesRecView.setLayoutManager(layout);
    }

    @Override
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
                                        PRIVATE METHODS
    ==============================================================================================*/

    private void customizeLayout() {
        createFonts();
        scanBtn.setIndeterminateProgressMode(true);
    }

    private void createFonts(){
        btTextView.setTypeface(MainActivityLayout.FONT_BOLD);
        pairedDevicesTextView.setTypeface(MainActivityLayout.FONT_BOLD);
        discoveredDevicesTextView.setTypeface(MainActivityLayout.FONT_BOLD);
        scanBtn.setTypeface(MainActivityLayout.FONT_BOLD);
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
