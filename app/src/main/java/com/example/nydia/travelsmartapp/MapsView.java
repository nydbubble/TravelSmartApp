package com.example.nydia.travelsmartapp;

import android.location.Location;

/**
 * Created by nydia on 3/27/18.
 */

public interface MapsView {

    void generateMap();
    void updateLocationOnMap(Location location);
    void showStartEnd();
    void showRoute();
}
