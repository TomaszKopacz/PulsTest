package com.tomaszkopacz.pulseoxymeter.adapters;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.listeners.DeviceItemListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomaszkopacz on 31.07.17.
 *
 * Adapter. Gets list of bluetooth devices and puts their data into layout.
 */

public class DevicesAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

    private List<BluetoothDevice> devices = new ArrayList<>();
    private DeviceItemListener listener;

    public DevicesAdapter(List<BluetoothDevice> devices, DeviceItemListener listener){
        this.devices = devices;
        this.listener = listener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_row, parent, false);

        return new DeviceViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        holder.deviceNameTxtView.setText(devices.get(position).getName());
        holder.deviceAddressTxtView.setText(devices.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}
