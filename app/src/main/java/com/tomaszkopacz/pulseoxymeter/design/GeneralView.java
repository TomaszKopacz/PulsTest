package com.tomaszkopacz.pulseoxymeter.design;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public interface GeneralView {

    //fonts in assets
    String FONT_THIN = "Comfortaa_Thin.ttf";
    String FONT_REGULAR = "Comfortaa_Regular.ttf";
    String FONT_BOLD = "Comfortaa_Bold.ttf";

    /**
     * Returns view.
     * @return View
     */
    View getView();

    /**
     * Customises layout of a view.
     * @param resources
     */
    void customizeLayout(Resources resources);
}
