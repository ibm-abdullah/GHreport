package com.braimahabdullah.ghreport;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;

/**
 * Created by Ibrahim-Abdullah on 12/4/2017.
 */

public class Utillity {

    public Utillity() {
    }

    /**
     * Check if this device has a camera
     */
    public boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
}
