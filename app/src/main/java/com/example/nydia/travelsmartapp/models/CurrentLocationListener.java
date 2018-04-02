package com.example.nydia.travelsmartapp.models;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by nydia on 3/30/18.
 */

public class CurrentLocationListener extends LiveData<Location> {
    private static final String TAG = "CurrentLocationListener";
    private static CurrentLocationListener instance;
    private final Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;


    public static CurrentLocationListener getInstance(Context appContext) {
        if (instance == null) {
            instance = new CurrentLocationListener(appContext);
        }
        return instance;
    }

    private CurrentLocationListener(Context appContext) {
        this.context = appContext;
    }

    private synchronized void createLocationRequest() {
        Log.d(TAG, "Creating location request");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient();
        createLocationRequest();
        Looper looper = Looper.myLooper();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, looper);
    }

    @Override
    protected void onInactive() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }


    public FusedLocationProviderClient getFusedLocationProviderClient() {
        if (mFusedLocationClient == null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        }
        return mFusedLocationClient;
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location newLocation = locationResult.getLastLocation();
            double latitude = newLocation.getLatitude();
            double longitude = newLocation.getLongitude();
            float accuracy = newLocation.getAccuracy();
            //Location location = new Location(latitude, longitude);
            if(newLocation!=null)
                setValue(newLocation);
        }
    };
}
