package com.tomaszkopacz.pulseoxymeter.controller;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.tomaszkopacz.pulseoxymeter.R;

public class MainActivity extends AppCompatActivity {


    //fonts
    private String FONT_THIN_NAME = "Comfortaa_Thin.ttf";
    private String FONT_REGULAR_NAME = "Comfortaa_Regular.ttf";
    private String FONT_BOLD_NAME = "Comfortaa_Bold.ttf";

    public static Typeface FONT_THIN;
    public static Typeface FONT_REGULAR;
    public static Typeface FONT_BOLD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fonts init
        FONT_THIN = Typeface.createFromAsset(getAssets(), FONT_THIN_NAME);
        FONT_REGULAR = Typeface.createFromAsset(getAssets(), FONT_REGULAR_NAME);
        FONT_BOLD = Typeface.createFromAsset(getAssets(), FONT_BOLD_NAME);
    }

    public void setActionBar(String title){
        getSupportActionBar().setTitle(title);
    }
}
