package com.tomaszkopacz.pulseoxymeter.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.adapters.DevicesAdapter;
import com.tomaszkopacz.pulseoxymeter.listeners.RecyclerViewListener;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DevicesListActivity extends AppCompatActivity implements RecyclerViewListener{

    private BroadcastReceiver broadcastReceiver;

    @BindView(R.id.devicesRecView) RecyclerView devicesRecView;
    @BindView(R.id.discoverProgressBar) ProgressBar progressBar;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<BluetoothDevice> discoveredDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);
        ButterKnife.bind(this);

        setUpBT();

        progressBar.setVisibility(ProgressBar.VISIBLE);
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * Check if bluetooth is available, if so, enable it and set receiver for discovering devices.
     */
    private void setUpBT(){
        this.broadcastReceiver = this.createBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, filter);
    }

    /**
     * Sets broadcast receiver for discovering new bluetooth devices
     */
    private BroadcastReceiver createBroadcastReceiver(){
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice deviceDiscovered = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    discoveredDevices.add(deviceDiscovered);

                    prepareDevicesList(discoveredDevices, devicesRecView);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        };
        return broadcastReceiver;
    }

    /**
     * Creates recycler view for found devices.
     */
    private void prepareDevicesList(List<BluetoothDevice> devices, RecyclerView recView){
        this.adapter = new DevicesAdapter(devices, this);
        this.layoutManager = new LinearLayoutManager(this);

        recView.setAdapter(adapter);
        recView.setLayoutManager(layoutManager);
    }

    @Override
    public void itemClicked(View view, int position) {
        progressBar.setVisibility(ProgressBar.GONE);
        BluetoothDevice bd = discoveredDevices.get(position);
        Toast.makeText(this, bd.getName(), Toast.LENGTH_SHORT).show();
    }
}
