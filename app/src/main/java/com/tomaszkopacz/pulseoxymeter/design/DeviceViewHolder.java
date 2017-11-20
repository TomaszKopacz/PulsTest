package com.tomaszkopacz.pulseoxymeter.design;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.listeners.ListItemListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tomaszkopacz on 31.07.17.
 *
 * View holder for bluetooth device data like name or class.
 * RecyclerViewListener itemClicked() method is called when item is touched.
 */

public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.devnameTxtView)
    TextView deviceNameTxtView;

    @BindView(R.id.devaddressTxtView)
    TextView deviceInfoTxtView;

    private ListItemListener listener;

    public DeviceViewHolder(View itemView, ListItemListener listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.listener = listener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        listener.itemClicked(getAdapterPosition(), deviceNameTxtView, deviceInfoTxtView);
    }
}
