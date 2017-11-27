package com.tomaszkopacz.pulseoxymeter.controller;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.design.CommunicateFragmentLayout;
import com.tomaszkopacz.pulseoxymeter.listeners.CommunicationFragmentListener;


public class CommunicateFragment extends Fragment implements CommunicationFragmentListener {

    //view
    private CommunicateFragmentLayout mCommunicateFragmentLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCommunicateFragmentLayout = new CommunicateFragmentLayout(inflater, container);

        mCommunicateFragmentLayout.setListener(this);

        return mCommunicateFragmentLayout.getView();
    }

    @Override
    public void onNavigationItemClicked() {
    }
}
