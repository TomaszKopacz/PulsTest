package com.tomaszkopacz.pulseoxymeter.controller;

import android.app.Application;
import android.graphics.Typeface;

/**
 * Created by tomaszkopacz on 28.11.17.
 * Global assets provider.
 */

public class MainApp extends Application {

    //util fonts
    public static final String FONT_THIN_NAME = "Comfortaa_Thin.ttf";
    public static final String FONT_REGULAR_NAME = "Comfortaa_Regular.ttf";
    public static final String FONT_BOLD_NAME = "Comfortaa_Bold.ttf";

    public static Typeface FONT_THIN;
    public static Typeface FONT_REGULAR;
    public static Typeface FONT_BOLD;

    @Override
    public void onCreate() {
        super.onCreate();

        createFonts();
    }

    private void createFonts(){
        FONT_THIN = Typeface.createFromAsset(getAssets(), FONT_THIN_NAME);
        FONT_REGULAR = Typeface.createFromAsset(getAssets(), FONT_REGULAR_NAME);
        FONT_BOLD = Typeface.createFromAsset(getAssets(), FONT_BOLD_NAME);
    }
}
