package com.tomaszkopacz.pulseoxymeter.controller;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomaszkopacz.pulseoxymeter.design.CommunicationFragmentLayout;
import com.tomaszkopacz.pulseoxymeter.listeners.CommunicationFragmentListener;


public class CommunicationFragment extends Fragment implements CommunicationFragmentListener {

    //view
    private CommunicationFragmentLayout mCommunicateFragmentLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCommunicateFragmentLayout = new CommunicationFragmentLayout(inflater, container);

        mCommunicateFragmentLayout.setListener(this);

        return mCommunicateFragmentLayout.getView();
    }

    @Override
    public void onNavigationItemClicked() {
    }
}
