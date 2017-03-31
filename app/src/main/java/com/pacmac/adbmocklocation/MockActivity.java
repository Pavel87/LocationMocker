package com.pacmac.adbmocklocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by pmachala on 2017-03-30.
 */

public class MockActivity extends Activity {


    static String locationProviderName = LocationManager.GPS_PROVIDER;
    protected LocationManager mLocationManager;
    protected int accuracy = 1;

    boolean isADBCommandCorrect = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        //boolean showUI = intent.getBooleanExtra("showUI", true);
        double lat = 48.424152;
        double lon = -123.356799;

        String location = intent.getStringExtra("latLon");
        if (location != null) {
            String[] latLon = location.split(",");
            lat = Double.parseDouble(latLon[0]);
            lon = Double.parseDouble(latLon[1]);
            isADBCommandCorrect = true;
        }
        double alt = (double) intent.getIntExtra("alt", 200);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        _register();

        if(isADBCommandCorrect) {
            _simulate(lon, lat, alt, 5);
            Log.d("PACMAC", "Pushing new location " + lat + ", " + lon);
            return;
        }

    }


    protected void _register() {
        // if the test provider already exists, android handles this fine
        try {
            mLocationManager.addTestProvider(locationProviderName, false, false, false,
                    false, true, true, true, 0, accuracy);
            mLocationManager.setTestProviderEnabled(locationProviderName, true);
        } catch (IllegalArgumentException ex) {
            Log.e("PACMAC", "IllegalArgumentException thrown in _register");
        }
    }


    protected void _simulate(double longitude, double latitude, double altitude, int satellites) {
        Location mockLocation = new Location(locationProviderName); // a string
        mockLocation.setLatitude(latitude);  // double
        mockLocation.setLongitude(longitude);
        mockLocation.setAltitude(altitude);
        if (satellites != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt("satellites", satellites);
            mockLocation.setExtras(bundle);
        }
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(accuracy);
        _simulate(mockLocation);
    }

    protected void _simulate(Location location) {
        if (!location.hasAccuracy()) {
            location.setAccuracy(accuracy);
        }
        if (!location.hasAltitude()) {
            location.setAltitude(0);
        }
        try {
            Method locationJellyBeanFixMethod = Location.class.getMethod("makeComplete");
            if (locationJellyBeanFixMethod != null) {
                locationJellyBeanFixMethod.invoke(location);
            }
        } catch (IllegalAccessException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (InvocationTargetException ignored) {}
        mLocationManager.setTestProviderLocation(locationProviderName, location);
        Log.w("PACMAC" , "Location set to: 49.224599, 17.657078");

        this.finishAffinity();
    }



}
