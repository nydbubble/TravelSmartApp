package com.example.nydia.travelsmartapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.example.nydia.travelsmartapp.R;
import com.example.nydia.travelsmartapp.models.Carpark;
import com.example.nydia.travelsmartapp.models.CarparkAvailabilityResponse;
import com.example.nydia.travelsmartapp.models.DirectionsResults;
import com.example.nydia.travelsmartapp.models.GeocodingResults;
import com.example.nydia.travelsmartapp.models.TrafficCamera;
import com.example.nydia.travelsmartapp.models.TrafficCameraResponse;
import com.example.nydia.travelsmartapp.models.TrafficIncident;
import com.example.nydia.travelsmartapp.models.TrafficIncidentResponse;
import com.example.nydia.travelsmartapp.network.CarparkRepository;
import com.example.nydia.travelsmartapp.network.DirectionsRepository;
import com.example.nydia.travelsmartapp.network.TrafficCameraRepository;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by nydia on 3/28/18.
 */

public class TrafficCameraViewModel extends AndroidViewModel {
    private LiveData<TrafficCameraResponse> trafficCameraObservable;
    private LiveData<DirectionsResults> directionsResultsObservable;
    private LiveData<List<TrafficCamera>> nearestCamerasObservable;
    private LiveData<List<TrafficIncident>> nearestIncidentsObservable;
    private LiveData<TrafficIncidentResponse> trafficIncidentObservable;
    private LiveData<CarparkAvailabilityResponse> carparkAvailabilityObservable;
    private LiveData<List<Carpark>> nearestAvailableCarpark;
    private LiveData<String> addressObservable;

    public TrafficCameraViewModel(@NonNull Application application) {
        super(application);

        // If any transformation is needed, this can be simply done by Transformations class ...

        //List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());

    }

    public LiveData<TrafficCameraResponse> getTrafficCameraObservable() {
        trafficCameraObservable = TrafficCameraRepository.getInstance().getTrafficCamera();
        return trafficCameraObservable;
    }

    public LiveData<TrafficIncidentResponse> getTrafficIncidentObservable() {
        trafficIncidentObservable = TrafficCameraRepository.getInstance().getTrafficIncident();
        return trafficIncidentObservable;
    }

    public LiveData<List<TrafficCamera>> getNearestCamerasObservable(List<LatLng> polyline) {
        nearestCamerasObservable = TrafficCameraRepository.getInstance().getNearestTrafficCamera(polyline);
        return nearestCamerasObservable;
    }

    public LiveData<List<TrafficIncident>> getNearestIncidentsObservable(List<LatLng> polyline) {
        nearestIncidentsObservable = TrafficCameraRepository.getInstance().getNearestTrafficIncident(polyline);
        return nearestIncidentsObservable;
    }

    public LiveData<CarparkAvailabilityResponse> getCarparkAvailabilityObservable() {
        carparkAvailabilityObservable = CarparkRepository.getInstance().getCarparkAvailability();
        return carparkAvailabilityObservable;
    }

    public LiveData<List<Carpark>> getNearestAvailableCarparkObservable(LatLng destination) {
        nearestAvailableCarpark = CarparkRepository.getInstance().getNearestAvailableCarparks(destination);
        return nearestAvailableCarpark;
    }

    public LiveData<DirectionsResults> getDirectionsResultsObservable(String origin, String destination) {
        directionsResultsObservable = DirectionsRepository.getInstance().getDirections(origin,destination);
        return directionsResultsObservable;
    }

    public LiveData<String> getAddress(String latlng) {
        addressObservable = DirectionsRepository.getInstance().getAddress(latlng);
        return addressObservable;
    }

}
