package com.pacmac.adbmocklocation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by pmachala on 2017-03-31.
 */

public class MockService extends Service {

    private final static int SATELLITE_COUNT = 5;
    protected LocationManager mLocationManager;
    boolean isADBCommandCorrect = false;
    boolean stopLocationUpdates = false;

    private boolean isCircle = false;
    private boolean isRectangle = false;
    private int distance = 10;
    private long timeInterval = 10 * 1000;
    private int EARTH_RADIUS = 6378;
    private final static double RAD_CONST = Math.PI / 180;
    private double angularDistance = (double) distance / EARTH_RADIUS;
    int degree = 0;
    Handler handler = new Handler();
    Runnable runnable;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("PACMAC", "MockService onStartCommand");

        stopLocationUpdates = false;
        double lat = 48.424152;
        double lon = -123.356799;

        isCircle = intent.getBooleanExtra("circle", false);
        String location = intent.getStringExtra("loc");
        if (location != null) {
            String[] latLon = location.split(",");
            lat = Double.parseDouble(latLon[0]);
            lon = Double.parseDouble(latLon[1]);
            isADBCommandCorrect = true;
        } else {
            isADBCommandCorrect = false;
        }

        double alt = (double) intent.getIntExtra("alt", 200);
        distance = intent.getIntExtra("distance", 10);
        timeInterval = intent.getIntExtra("interval", 30) * 1000;

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (isCircle) {
            stopLocationUpdates = false;
            Utils.registerTestProvider(mLocationManager);
            spawnLocationInCircle(lat * RAD_CONST, lon * RAD_CONST, alt);
        } else if (isADBCommandCorrect) {
            stopLocationUpdates = true;
            Utils.registerTestProvider(mLocationManager);
            Utils.mocLocation(mLocationManager,
                    Utils.createMockLocation(lat, lon, alt, SATELLITE_COUNT));
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void spawnLocationInCircle(final double latitudeCenter, final double longitudeCenter,
            final double altitude) {

        final Handler handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                if (degree > 359) {
                    degree = 0;
                }
                if (stopLocationUpdates) {
                    handler.removeCallbacks(this);
                    Log.d("PACMAC", "location handler removed");
                    stopSelf();
                    return;
                }

                double latitude;
                double longitude;

                // calculate lat / lon for circle from ref point in distance of
                // ..
                latitude = Math.asin(Math.sin(latitudeCenter) * Math.cos(angularDistance)
                        + Math.cos(latitudeCenter) * Math.sin(angularDistance)
                                * Math.cos(degree * RAD_CONST));

                longitude = longitudeCenter + Math.atan2(
                        Math.sin(degree * RAD_CONST) * Math.sin(angularDistance)
                                * Math.cos(latitudeCenter),
                        Math.cos(angularDistance) - Math.sin(latitudeCenter) * Math.sin(latitude));

                degree += 10;

                Utils.mocLocation(mLocationManager, Utils.createMockLocation(toDegrees(latitude),
                        toDegrees(longitude), altitude, SATELLITE_COUNT));

                handler.postDelayed(this, timeInterval);

            }
        };

        handler.post(runnable);

        // handler.removeCallbacks(runnable);
    }

    private double toDegrees(double coord) {
        return coord * 180 / Math.PI;
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates = true;
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
        Log.d("PACMAC", "MockServiceDestroyed");
        super.onDestroy();

    }
}
