package com.tomaszkopacz.pulseoxymeter.design;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

/**
 * Created by tomaszkopacz on 17.11.17.
 */

public interface GeneralView {

    /**
     * Returns view.
     * @return View
     */
    View getView();

    /**
     * Customises layout of a view.
     */
    void customizeLayout();
}
