package com.tomaszkopacz.pulseoxymeter.controller;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.design.ConnectionFragmentLayout;
import com.tomaszkopacz.pulseoxymeter.design.GuideFragmentLayout;
import com.tomaszkopacz.pulseoxymeter.design.MainActivityLayout;

public class GuideFragment extends Fragment {

    //view
    private MainActivityLayout mMainActivityLayout;
    private GuideFragmentLayout mGuideFragmentLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //get activity layout
        mMainActivityLayout = ((MainActivity)getActivity()).getLayout();

        //create fragment layout
        mGuideFragmentLayout = new GuideFragmentLayout(inflater, container);
        mMainActivityLayout.getToolbar().setTitle(R.string.guide_fragment_title);

        return mGuideFragmentLayout.getView();
    }

}
