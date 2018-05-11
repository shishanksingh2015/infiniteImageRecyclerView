package com.shishank.infinitelist.utils;

import android.content.Context;
import android.hardware.SensorManager;

/**
 * @author shishank
 */

public class Utils {

    private static final double FRICTION = 0.84;

    public static Double calculateDeceleration(Context context) {
        return SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.3700787 // inches per meter
                // pixels per inch. 160 is the "default" dpi, i.e. one dip is one pixel on a 160 dpi
                // screen
                * context.getResources().getDisplayMetrics().density * 160.0f * FRICTION;
    }
}
