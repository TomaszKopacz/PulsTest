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
import com.tomaszkopacz.pulseoxymeter.controller.MainActivity;
import com.tomaszkopacz.pulseoxymeter.listeners.ListItemListener;
import com.tomaszkopacz.pulseoxymeter.listeners.ScanFragmentListener;

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

    @BindView(R.id.noDevicesPairedTextView)
    TextView noDevicesPairedTextView;

    @BindView(R.id.pairedDevicesRecView)
    RecyclerView pairedDevicesRecView;

    @BindView(R.id.discovDevTxtView)
    TextView discoveredDevicesTextView;

    @BindView(R.id.discovDevRelLayout)
    RelativeLayout discoveredDevicesRelLayout;

    @BindView(R.id.noDevicesDiscoveredTextView)
    TextView noDevicesDiscoveredTextView;

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
    public ScanDevicesViewMember(LayoutInflater inflater, ViewGroup views){
        rootView = inflater.inflate(R.layout.fragment_devices_lists, views, false);
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
                startScan();
                break;

            case BUTTON_IN_PROGRESS:
                if (listener != null)
                    listener.stopScan();
                stopScan();
                break;
        }
    }

    @OnClick(R.id.pairedDevTxtView)
    public void onPairedDevicesTitleClick(){
        showPairedDevices();
    }

    @OnClick(R.id.discovDevTxtView)
    public void onDiscoveredDeviceTitleClick(){
        showDiscoveredDevices();
    }

    /*==============================================================================================
                                        REACTIONS
    ==============================================================================================*/


    @Override
    public void notifyBtStateChanged(boolean b) {
        btSwitch.setChecked(b);
    }

    @Override
    public void notifyBtScanStateChanged(boolean b) {
        if (b)
            startScan();
        else
            stopScan();
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

        //show list or information if list is empty
        showListOrInformation(
                pairedDevicesAdapter,
                pairedDevicesRecView,
                noDevicesPairedTextView);
    }

    @Override
    public void createDiscoveredDevicesList(List<BluetoothDevice> devices, ListItemListener listener) {

        //create settings
        discoveredDevicesAdapter = new DevicesAdapter(devices, listener);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(rootView.getContext());

        //set recycler view
        discoveredDevicesRecView.setAdapter(discoveredDevicesAdapter);
        discoveredDevicesRecView.setLayoutManager(layout);

        //show list or information if list is empty
        showListOrInformation(
                discoveredDevicesAdapter,
                discoveredDevicesRecView,
                noDevicesDiscoveredTextView);
    }

    public void notifyInsertToPairedDevices(int position) {

        if (pairedDevicesAdapter.getItemCount() == 1){
            noDevicesPairedTextView.setVisibility(View.GONE);
            pairedDevicesRecView.setVisibility(View.VISIBLE);
        }

        pairedDevicesAdapter.notifyItemInserted(position);
    }

    public void notifyInsertToDiscoveredDevices(int position) {

        if (discoveredDevicesAdapter.getItemCount() == 1){
            noDevicesDiscoveredTextView.setVisibility(View.GONE);
            discoveredDevicesRecView.setVisibility(View.VISIBLE);
        }

        discoveredDevicesAdapter.notifyItemInserted(position);
    }

    public void notifyRemoveFromDiscoveredDevices(int position) {
        discoveredDevicesAdapter.notifyItemRemoved(position);

        if (discoveredDevicesAdapter.getItemCount() == 0){
            noDevicesDiscoveredTextView.setVisibility(View.VISIBLE);
            discoveredDevicesRecView.setVisibility(View.GONE);
        }
    }


    /*==============================================================================================
                                        PRIVATE METHODS
    ==============================================================================================*/

    private void customizeLayout() {

        btTextView.setTypeface(MainActivity.FONT_BOLD);

        pairedDevicesTextView.setTypeface(MainActivity.FONT_BOLD);
        noDevicesPairedTextView.setTypeface(MainActivity.FONT_THIN);

        discoveredDevicesTextView.setTypeface(MainActivity.FONT_REGULAR);
        noDevicesDiscoveredTextView.setTypeface(MainActivity.FONT_THIN);

        scanBtn.setIndeterminateProgressMode(true);
    }

    private void showPairedDevices() {
        discoveredDevicesRelLayout.setVisibility(View.GONE);
        discoveredDevicesTextView.setTypeface(MainActivity.FONT_REGULAR);

        pairedDevicesRelLayout.setVisibility(View.VISIBLE);
        pairedDevicesTextView.setTypeface(MainActivity.FONT_BOLD);
    }

    private void showDiscoveredDevices() {
        pairedDevicesRelLayout.setVisibility(View.GONE);
        pairedDevicesTextView.setTypeface(MainActivity.FONT_REGULAR);

        discoveredDevicesRelLayout.setVisibility(View.VISIBLE);
        discoveredDevicesTextView.setTypeface(MainActivity.FONT_BOLD);
    }

    private void startScan() {
        scanBtn.setProgress(BUTTON_IN_PROGRESS);
        showDiscoveredDevices();
    }

    private void stopScan() {
        scanBtn.setProgress(BUTTON_LAZY);
    }

    private void showListOrInformation(DevicesAdapter adapter,
                                       RecyclerView recyclerView,
                                       TextView infoTextView) {

        if (adapter.getItemCount() == 0){
            recyclerView.setVisibility(View.GONE);
            infoTextView.setVisibility(View.VISIBLE);

        } else {
            infoTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}
