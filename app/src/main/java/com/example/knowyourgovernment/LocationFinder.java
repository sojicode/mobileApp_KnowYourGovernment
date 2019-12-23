package com.example.knowyourgovernment;


import android.Manifest;
import android.location.Criteria;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LocationFinder {

    private static final String TAG = "LocationFinder";
    private MainActivity mainActivity;

    private static int LOCATION_REQUEST_CODE = 123;
    private LocationManager locaManager;
    private Criteria criteria;

    LocationFinder(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        if(permissionCheck()) {
            setLocation();
        }
    }

    public boolean permissionCheck() {

        if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    mainActivity,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_REQUEST_CODE);
            return false;

        } else {
            return true;
        }
    }

    public void setLocation() {

    }



}
