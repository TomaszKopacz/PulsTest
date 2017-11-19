package com.tomaszkopacz.pulseoxymeter.design;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tomaszkopacz.pulseoxymeter.R;

/**
 * Created by tomaszkopacz on 19.11.17.
 */

public class DeviceItemViewMember implements DeviceItemView {

    private View rootView;
    private TextView nameTextView;
    private TextView infoTextView;

    public DeviceItemViewMember(Context context){

        LayoutInflater inflater =
                (LayoutInflater) context
                .getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.rootView = inflater.inflate(R.layout.device_row, null);
    }

    @Override
    public View getView() {
        return rootView;
    }

    @Override
    public void customizeLayout(Resources resources) {
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
