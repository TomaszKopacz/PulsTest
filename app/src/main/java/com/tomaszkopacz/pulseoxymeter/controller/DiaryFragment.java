package com.tomaszkopacz.pulseoxymeter.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomaszkopacz.pulseoxymeter.R;

public class DiaryFragment extends Fragment {


    public DiaryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_diary, container, false);
    }
}
