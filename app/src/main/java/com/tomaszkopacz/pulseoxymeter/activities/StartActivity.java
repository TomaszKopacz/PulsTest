package com.tomaszkopacz.pulseoxymeter.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import com.tomaszkopacz.pulseoxymeter.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {

    private BluetoothAdapter adapter;
    @BindView(R.id.enablebtSwitch) Switch enableBTSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        adapter = BluetoothAdapter.getDefaultAdapter();
        initBTSwitch();
    }

    private void initBTSwitch(){
        enableBTSwitch.setChecked(
                (adapter != null) ?
                        adapter.isEnabled() : false
        );
    }

    //start activity for listing paired bluetooth devices
    @OnClick(R.id.paireddevicesBTN)
    public void showPairedDeviceBtnClicked(){
        if (adapter.isEnabled()) {
            Intent devicesListIntent = new Intent(this, DevicesListActivity.class);
            startActivity(devicesListIntent);

        }else{
            Toast.makeText(this, R.string.btstatemsg, Toast.LENGTH_LONG).show();
        }
    }

    //enable/disable bluetooth
    @OnCheckedChanged(R.id.enablebtSwitch)
    public void enableBT(CompoundButton button, boolean checked){
        if (adapter == null)
            return;

        else if (checked){
            adapter.enable();

        }else{
            adapter.disable();
        }
    }
}
