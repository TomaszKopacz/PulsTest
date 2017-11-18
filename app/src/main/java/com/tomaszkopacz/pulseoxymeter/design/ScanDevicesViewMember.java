package com.tomaszkopacz.pulseoxymeter.design;

import android.bluetooth.BluetoothDevice;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.controller.DeviceItemListener;
import com.tomaszkopacz.pulseoxymeter.controller.ScanDevicesViewListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public class ScanDevicesViewMember implements ScanDevicesView {

    //general
    private View rootView;
    private ScanDevicesViewListener listener;

    //components
    @BindView(R.id.btTxtView)
    TextView btTextView;

    @BindView(R.id.enableBTSwitch)
    Switch btSwitch;

    @BindView(R.id.pairedDevTxtView)
    TextView pairedDevicesTextView;

    @BindView(R.id.pairedDevicesRecView)
    RecyclerView pairedDevicesRecView;

    @BindView(R.id.discovDevTxtView)
    TextView discoveredDevicesTextView;

    @BindView(R.id.discoveredDevicesRecView)
    RecyclerView discoveredDevicesRecView;

    @BindView(R.id.scanBtn)
    CircularProgressButton scanBtn;

    @BindView(R.id.startBtn)
    Button startBtn;

    //fonts
    private Typeface fontThin;
    private Typeface fontRegular;
    private Typeface fontBold;

    //lists
    private DevicesAdapter pairedDevicesAdapter;
    private DevicesAdapter discoveredDevicesAdapter;

    //item view
    private TextView deviceNameTextView;
    private TextView deviceInfoTextView;

    //scan button
    private final int BUTTON_LAZY = 0;
    private final int BUTTON_IN_PROGRESS = 10;

    /*==============================================================================================
                                        INITIALIZING
    ==============================================================================================*/

    //Constructor - inflates view from xml file and parses componenets.
    public ScanDevicesViewMember(LayoutInflater inflater, ViewGroup views){
        rootView = inflater.inflate(R.layout.fragment_devices_lists, views, false);
        ButterKnife.bind(this, rootView);
    }

    @Override
    public void customizeLayout(Resources resources) {

        //create fonts
        fontThin = Typeface.createFromAsset(resources.getAssets(), FONT_THIN);
        fontRegular = Typeface.createFromAsset(resources.getAssets(), FONT_REGULAR);
        fontBold = Typeface.createFromAsset(resources.getAssets(), FONT_BOLD);
        setFonts();

        //scan button
        scanBtn.setIndeterminateProgressMode(true);
    }

    /**
     * Changes text of text views of root view with prepared fonts.
     */
    private void setFonts(){
        btTextView.setTypeface(fontBold);
        pairedDevicesTextView.setTypeface(fontRegular);
        discoveredDevicesTextView.setTypeface(fontRegular);
    }
    /*==============================================================================================
                                            CONTROLS
    ==============================================================================================*/

    @OnCheckedChanged(R.id.enableBTSwitch)
    public void btSwitchChanged(boolean b){
        if (listener != null) {
            listener.btStateChanged(b);
        }
    }

    @OnClick(R.id.scanBtn)
    public void onScanBtnPressed(){

        switch (scanBtn.getProgress()) {

            case BUTTON_LAZY:
                if (listener != null)
                    listener.startScan();
                break;

            case BUTTON_IN_PROGRESS:
                if (listener != null)
                    listener.stopScan();
                break;
        }
    }

    @OnClick(R.id.startBtn)
    public void onStartBtnPressed(){
    }

    /*==============================================================================================

    ==============================================================================================*/

    @Override
    public void setListener(final ScanDevicesViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void setBtSwitchChecked(boolean b) {
        btSwitch.setChecked(b);
    }

    @Override
    public void createPairedDevicesList(List<BluetoothDevice> devices, DeviceItemListener listener) {

        //create settings
        pairedDevicesAdapter = new DevicesAdapter(devices, listener);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(rootView.getContext());

        //set recycler view
        pairedDevicesRecView.setAdapter(pairedDevicesAdapter);
        pairedDevicesRecView.setLayoutManager(layout);
    }

    @Override
    public void createDiscoveredDevicesList(List<BluetoothDevice> devices, DeviceItemListener listener) {

        //create settings
        discoveredDevicesAdapter = new DevicesAdapter(devices, listener);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(rootView.getContext());

        //set recycler view
        discoveredDevicesRecView.setAdapter(discoveredDevicesAdapter);
        discoveredDevicesRecView.setLayoutManager(layout);
    }

    @Override
    public void insertToPairedDevicesList(int position) {
        pairedDevicesAdapter.notifyItemInserted(position);
    }

    @Override
    public void insertToDiscoveredDevicesList(int position) {
        discoveredDevicesAdapter.notifyItemInserted(position);
    }

    @Override
    public void removeFromDiscoveredDevicesList(int position) {
        discoveredDevicesAdapter.notifyItemRemoved(position);
    }

    @Override
    public void setItemView(TextView deviceNameTextView, TextView deviceInfoTextView) {
        this.deviceNameTextView = deviceNameTextView;
        this.deviceInfoTextView = deviceInfoTextView;
    }

    @Override
    public void setInfoText(String info) {
        if (deviceInfoTextView != null)
            deviceInfoTextView.setText(info);
    }

    @Override
    public void startScan() {
        scanBtn.setProgress(BUTTON_IN_PROGRESS);
    }

    @Override
    public void stopScan() {
        scanBtn.setProgress(BUTTON_LAZY);
    }

    @Override
    public View getView() {
        return rootView;
    }
}
