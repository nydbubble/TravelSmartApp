package com.example.nydia.travelsmartapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.nydia.travelsmartapp.models.DirectionsResults;
import com.example.nydia.travelsmartapp.network.DirectionsRepository;

/**
 * Created by nydia on 3/28/18.
 */

public class DirectionsViewModel extends AndroidViewModel{
    private final LiveData<DirectionsResults> directionsResultsObservable;

    public DirectionsViewModel(@NonNull Application application, String origin, String destination) {
        super(application);

        directionsResultsObservable = DirectionsRepository.getInstance().getDirections(origin, destination);

    }

    public LiveData<DirectionsResults> getDirectionsResultsObservable() {
        return directionsResultsObservable;
    }
}
