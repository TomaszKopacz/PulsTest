package com.tomaszkopacz.pulseoxymeter.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.adapters.DevicesAdapter;
import com.tomaszkopacz.pulseoxymeter.listeners.DeviceItemListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class DevicesListActivity extends AppCompatActivity{

    //layout components
    @BindView(R.id.btTxtView)
    TextView btTxtView;

    @BindView(R.id.enableBTSwitch)
    Switch enableBtSwitch;

    @BindView(R.id.pairedDevTxtView)
    TextView pairedDevicesTxtView;

    @BindView(R.id.pairedDevicesRecView)
    RecyclerView pairedRecView;

    @BindView(R.id.discovDevTxtView)
    TextView discoveredDevicesTxtView;

    @BindView(R.id.discoveredDevicesRecView)
    RecyclerView discoveredRecView;

    @BindView(R.id.scanBtn)
    CircularProgressButton scanBtn;

    //filter for bluetooth state changes
    private IntentFilter mIntentFilter;

    //bluetooth settings
    private BluetoothAdapter mBluetoothAdapter;

    //bluetooth devices
    private List<BluetoothDevice> pairedDevices = new ArrayList<>();
    private List<BluetoothDevice> discoveredDevices = new ArrayList<>();

    //list adapters
    private RecyclerView.Adapter pairedDevicesAdapter;
    private RecyclerView.Adapter discoveredDevicesAdapter;

    //fonts
    private Typeface fontThin;
    private Typeface fontRegular;
    private Typeface fontBold;

    //scanBtn states
    private static final int BUTTON_LAZY = 0;
    private static final int BUTTON_IN_PROGRESS = 10;

    /*==============================================================================================
                                    ACTIVITY LIFE CYCLE
    ==============================================================================================*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);
        ButterKnife.bind(this);

        //set fonts
        fontThin = Typeface.createFromAsset(getAssets(), "Comfortaa_Thin.ttf");
        fontRegular = Typeface.createFromAsset(getAssets(), "Comfortaa_Regular.ttf");
        fontBold = Typeface.createFromAsset(getAssets(), "Comfortaa_Bold.ttf");

        btTxtView.setTypeface(fontBold);
        pairedDevicesTxtView.setTypeface(fontRegular);
        discoveredDevicesTxtView.setTypeface(fontRegular);

        //scan button
        scanBtn.setIndeterminateProgressMode(true);

        //check for bt LE compatibility and set switch
        if (isDeviceBtCompatible() && mBluetoothAdapter.isEnabled())
            enableBtSwitch.setChecked(true);

        initIntentFilter();
        registerReceiver(mBroadcastReceiver, mIntentFilter);

        createDevicesLists();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    /*==============================================================================================
                                        CONTROLS
    ==============================================================================================*/

    @OnCheckedChanged(R.id.enableBTSwitch)
    public void onBtSwitchStateChanged(boolean checked){
        if (checked)
            mBluetoothAdapter.enable();
        else
            mBluetoothAdapter.disable();
    }

    @OnClick(R.id.scanBtn)
    public void onDiscoverBtnClick(){

        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            switch (scanBtn.getProgress()) {
                case BUTTON_LAZY:
                    scanBtn.setProgress(BUTTON_IN_PROGRESS);
                    BluetoothAdapter.getDefaultAdapter().startDiscovery();
                    break;

                case BUTTON_IN_PROGRESS:
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    scanBtn.setProgress(BUTTON_LAZY);
                    break;
            }
        } else
            Toast.makeText(this, R.string.bt_off_msg, Toast.LENGTH_SHORT).show();
    }

    /*==============================================================================================
                                        BLUETOOTH SETTINGS
    ==============================================================================================*/

    /**
     * Checks whether device supports bluetooth connections.
     */
    private boolean isDeviceBtCompatible(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null){
            finish();
            return false;

        } else
            return true;
    }

    /**
     * Broadcast receiver for discovering and pairing new bluetooth devices.
     */
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //bluetooth on\off - change switch position
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){

                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                        == BluetoothAdapter.STATE_ON) {
                    enableBtSwitch.setChecked(true);
                    createDevicesLists();
                }

                else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                        == BluetoothAdapter.STATE_OFF)
                    enableBtSwitch.setChecked(false);

            }

            //when new device is discovered
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice deviceDiscovered
                        = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                insertDiscoveredDevice(deviceDiscovered);
            }

            //when discovering is finished
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            }

            //when state of bonding is changed (bond turned off, pairing, paired)
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                BluetoothDevice bd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (bd.getBondState() == BluetoothDevice.BOND_NONE)
                    Log.d("BroadcastReceiver", "BOND NONE");

                else if (bd.getBondState() == BluetoothDevice.BOND_BONDING)
                    Log.d("BroadcastReceiver", "BOND BONDING");

                else if (bd.getBondState() == BluetoothDevice.BOND_BONDED) {

                    //remove from discovered, add to paired devices list
                    removeDiscoveredDevice(bd);
                    insertPairedDevice(bd);
                }
            }
        }
    };

    /**
     * Determines bluetooth actions to be serviced by receiver.
     */
    private void initIntentFilter(){
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
    }

    /*==============================================================================================
                                    PREPARING DEVICES LISTS
     =============================================================================================*/

    /**
     * Creates RecyclerViews for paired and discovered devices lists.
     */
    private void createDevicesLists(){
        //set devices list
        pairedDevices = getPairedDevices();

        //create adapters
        pairedDevicesAdapter = new DevicesAdapter(pairedDevices, pairedDeviceListener);
        discoveredDevicesAdapter = new DevicesAdapter(discoveredDevices, discoveredDeviceListener);

        //create layouts
        RecyclerView.LayoutManager pairedLayoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager discoveredLayoutManager = new LinearLayoutManager(this);

        //set recycler views properties
        pairedRecView.setAdapter(pairedDevicesAdapter);
        pairedRecView.setLayoutManager(pairedLayoutManager);

        discoveredRecView.setAdapter(discoveredDevicesAdapter);
        discoveredRecView.setLayoutManager(discoveredLayoutManager);
    }

    /**
     * Retrieves set of paired bluetooth devices and converts it to the List.
     */
    private List<BluetoothDevice> getPairedDevices(){
        Set<BluetoothDevice> devicesSet = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

        List<BluetoothDevice> devices = new ArrayList<>();
        for (BluetoothDevice bd : devicesSet)
            devices.add(bd);

        return devices;
    }

    /**
     * Insert paired device to paired devices list and notify that in recycler view.
     * @param device
     */
    private void insertPairedDevice(BluetoothDevice device){
        pairedDevices.add(0, device);
        pairedDevicesAdapter.notifyItemInserted(0);
    }

    /**
     * Insert newly discovered device in discovered devices list and notify that in a recycler view.
     * @param device
     */
    private void insertDiscoveredDevice(BluetoothDevice device){
        if (!discoveredDevices.contains(device)) {
            discoveredDevices.add(device);
            int position = discoveredDevices.indexOf(device);
            discoveredDevicesAdapter.notifyItemInserted(position);
        }
    }

    /**
     * Remove device from discovered devices list and notify that in a recycler view.
     * @param device
     */
    private void removeDiscoveredDevice(BluetoothDevice device){
        int position = discoveredDevices.indexOf(device);
        discoveredDevices.remove(position);
        discoveredDevicesAdapter.notifyItemRemoved(position);
    }

    /*==============================================================================================
                                    LISTENERS FOR LISTED DEVICES
    ==============================================================================================*/

    private DeviceItemListener discoveredDeviceListener = new DeviceItemListener() {

        @Override
        public void itemClicked(int position, TextView deviceNameTextView, TextView deviceAddressTextView) {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            scanBtn.setProgress(BUTTON_LAZY);

            BluetoothDevice bd = discoveredDevices.get(position);

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){

                deviceNameTextView.setTextColor(Color.GRAY);
                deviceAddressTextView.setTextColor(Color.GRAY);
                deviceAddressTextView.setText(R.string.pairing);

                bd.createBond();
            }
        }
    };

    private DeviceItemListener pairedDeviceListener = new DeviceItemListener() {
        @Override
        public void itemClicked(int position, TextView deviceNameTextView, TextView deviceAddressTextView) {

        }
    };

}
