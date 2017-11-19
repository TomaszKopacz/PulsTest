package com.tomaszkopacz.pulseoxymeter.controller;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.tomaszkopacz.pulseoxymeter.R;

public class MainActivity extends FragmentActivity {


    //fonts
    private String FONT_THIN_NAME = "Comfortaa_Thin.ttf";
    private String FONT_NORMAL_NAME = "Comfortaa_Normal.ttf";
    private String FONT_BOLD_NAME = "Comfortaa_Bold.ttf";

    public static Typeface FONT_THIN;
    public static Typeface FONT_NORMAL;
    public static Typeface FONT_BOLD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);

        //fonts init
        FONT_THIN = Typeface.createFromAsset(getAssets(), FONT_THIN_NAME);
        FONT_NORMAL = Typeface.createFromAsset(getAssets(), FONT_NORMAL_NAME);
        FONT_BOLD = Typeface.createFromAsset(getAssets(), FONT_BOLD_NAME);
    }
}
