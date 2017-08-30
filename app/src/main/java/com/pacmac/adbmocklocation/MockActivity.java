package com.pacmac.adbmocklocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by pmachala on 2017-03-30.
 */

public class MockActivity extends Activity {


    private final static int SATELLITE_COUNT = 5;
    protected LocationManager mLocationManager;

    boolean isADBCommandCorrect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        //boolean showUI = intent.getBooleanExtra("showUI", true);
        double lat = 48.424152;
        double lon = -123.356799;

        String location = intent.getStringExtra("loc");
        if (location != null) {
            String[] latLon = location.split(",");
            lat = Double.parseDouble(latLon[0]);
            lon = Double.parseDouble(latLon[1]);
            isADBCommandCorrect = true;
        }
        double alt = (double) intent.getIntExtra("alt", 200);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(isADBCommandCorrect) {
            Utils.registerTestProvider(mLocationManager);
            Log.d("PACMAC", "Pushing new location " + lat + ", " + lon);
            Utils.mocLocation(mLocationManager, Utils.createMockLocation(lat, lon, alt, SATELLITE_COUNT));
            return;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        this.finish();
    }
}
