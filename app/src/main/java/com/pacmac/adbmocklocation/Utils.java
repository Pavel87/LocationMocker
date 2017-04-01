package com.pacmac.adbmocklocation;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by pmachala on 2017-03-31.
 */

public class Utils {

    private final static String locationProviderName = LocationManager.GPS_PROVIDER;
    private final static int accuracy = 1;

    public static void registerTestProvider(LocationManager mLocationManager) {
        try {
            mLocationManager.addTestProvider(locationProviderName, false, false, false, false, true,
                    true, true, 0, accuracy);
            mLocationManager.setTestProviderEnabled(locationProviderName, true);
        } catch (IllegalArgumentException ex) {
            Log.e("PACMAC", "IllegalArgumentException thrown in _register");
        }
    }

    public static void mocLocation(LocationManager mLocationManager, Location mockLocation) {

        mLocationManager.setTestProviderLocation(locationProviderName, mockLocation);
        Log.w("PACMAC", "Location set to: " + mockLocation.getLatitude() + ","
                + mockLocation.getLongitude());
    }

    public static Location createMockLocation(double latitude, double longitude, double altitude,
            int satellites) {
        Location mockLocation = new Location(locationProviderName); // a string
        mockLocation.setLatitude(latitude); // double
        mockLocation.setLongitude(longitude);
        mockLocation.setAltitude(altitude);
        if (satellites != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt("satellites", satellites);
            mockLocation.setExtras(bundle);
        }
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(accuracy);

        try {
            Method locationJellyBeanFixMethod = Location.class.getMethod("makeComplete");
            if (locationJellyBeanFixMethod != null) {
                locationJellyBeanFixMethod.invoke(mockLocation);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return mockLocation;
    }

}
