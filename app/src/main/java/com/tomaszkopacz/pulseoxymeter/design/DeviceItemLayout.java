package com.tomaszkopacz.pulseoxymeter.design;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tomaszkopacz.pulseoxymeter.R;

/**
 * Created by tomaszkopacz on 19.11.17.
 */

public class DeviceItemLayout {

    private View rootView;
    private TextView nameTextView;
    private TextView infoTextView;

    public DeviceItemLayout(LayoutInflater inflater, ViewGroup container){
        this.rootView = inflater.inflate(R.layout.device_item, container, false);
    }

    public View getView() {
        return rootView;
    }

    public void setNameTextView(TextView view) {
        this.nameTextView = view;
    }

    public void setInfoTextView(TextView view) {
        this.infoTextView = view;
    }

    public TextView getNameTextView() {
        return this.nameTextView;
    }

    public TextView getInfoTextView() {
        return this.infoTextView;
    }
}
