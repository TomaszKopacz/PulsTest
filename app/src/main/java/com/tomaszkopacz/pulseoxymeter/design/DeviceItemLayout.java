package com.tomaszkopacz.pulseoxymeter.design;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.views.DeviceItemView;

/**
 * Created by tomaszkopacz on 19.11.17.
 */

public class DeviceItemLayout implements DeviceItemView {

    private View rootView;
    private TextView nameTextView;
    private TextView infoTextView;

    public DeviceItemLayout(LayoutInflater inflater, ViewGroup container){
        this.rootView = inflater.inflate(R.layout.device_item, container, false);
    }

    @Override
    public View getView() {
        return rootView;
    }

    @Override
    public void setNameTextView(TextView view) {
        this.nameTextView = view;
    }

    @Override
    public void setInfoTextView(TextView view) {
        this.infoTextView = view;
    }

    @Override
    public TextView getNameTextView() {
        return this.nameTextView;
    }

    @Override
    public TextView getInfoTextView() {
        return this.infoTextView;
    }
}
