package com.tomaszkopacz.pulseoxymeter.design;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.controller.ScanDevicesViewListener;

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
                                        EVENTS NOTIFYING
    ==============================================================================================*/

    @OnCheckedChanged(R.id.enableBTSwitch)
    public void btSwitchChanged(boolean b){
        if (listener != null) {
            listener.btStateChanged(b);
        }
    }

    @OnClick(R.id.scanBtn)
    public void onScanBtnPressed(){
        if (listener != null)
            listener.startScan();
    }

    @OnClick(R.id.startBtn)
    public void onStartBtnPressed(){
        if (listener != null)
            listener.startScan();
    }

    @Override
    public void setListener(final ScanDevicesViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void setBtSwitchChecked(boolean b) {
        btSwitch.setChecked(b);
    }

    @Override
    public void startScan() {

    }

    @Override
    public void stopScan() {

    }

    @Override
    public View getView() {
        return rootView;
    }
}
