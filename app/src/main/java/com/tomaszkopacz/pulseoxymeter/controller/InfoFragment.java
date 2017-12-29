package com.tomaszkopacz.pulseoxymeter.controller;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.design.GuideFragmentLayout;
import com.tomaszkopacz.pulseoxymeter.design.InfoFragmentLayout;
import com.tomaszkopacz.pulseoxymeter.design.MainActivityLayout;

public class InfoFragment extends Fragment {

    //view
    private MainActivityLayout mMainActivityLayout;
    private InfoFragmentLayout mInfoFragmentLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //get activity layout
        mMainActivityLayout = ((MainActivity)getActivity()).getLayout();

        //create fragment layout
        mInfoFragmentLayout = new InfoFragmentLayout(inflater, container);
        mMainActivityLayout.getToolbar().setTitle(R.string.info_fragment_title);

        return mInfoFragmentLayout.getView();
    }

}
