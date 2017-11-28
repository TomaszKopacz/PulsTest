package com.tomaszkopacz.pulseoxymeter.btservice;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by tomaszkopacz on 28.11.17.
 */

public class DetectionService extends IntentService {

    public DetectionService() {
        super("DetectionService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
