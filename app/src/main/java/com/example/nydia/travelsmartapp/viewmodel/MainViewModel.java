package com.example.nydia.travelsmartapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.nydia.travelsmartapp.models.Carpark;
import com.example.nydia.travelsmartapp.models.CarparkAvailabilityResponse;
import com.example.nydia.travelsmartapp.models.DirectionsResults;
import com.example.nydia.travelsmartapp.models.Forecast;
import com.example.nydia.travelsmartapp.models.PlanningArea;
import com.example.nydia.travelsmartapp.models.TrafficCamera;
import com.example.nydia.travelsmartapp.models.TrafficCameraResponse;
import com.example.nydia.travelsmartapp.models.TrafficIncident;
import com.example.nydia.travelsmartapp.models.TrafficIncidentResponse;
import com.example.nydia.travelsmartapp.network.DataGovRepository;
import com.example.nydia.travelsmartapp.network.DirectionsRepository;
import com.example.nydia.travelsmartapp.network.LTARepository;
import com.example.nydia.travelsmartapp.network.OneMapRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by nydia on 3/28/18.
 */

public class MainViewModel extends AndroidViewModel {
    private LiveData<TrafficCameraResponse> trafficCameraObservable;
    private LiveData<DirectionsResults> directionsResultsObservable;
    private LiveData<List<TrafficCamera>> nearestCamerasObservable;
    private LiveData<List<TrafficIncident>> nearestIncidentsObservable;
    private LiveData<TrafficIncidentResponse> trafficIncidentObservable;
    private LiveData<CarparkAvailabilityResponse> carparkAvailabilityObservable;
    private LiveData<List<Carpark>> nearestAvailableCarpark;
    private LiveData<String> addressObservable;
    private LiveData<List<PlanningArea>> planningArea;
    private LiveData<Forecast> forecastObservable;

    public MainViewModel(@NonNull Application application) {
        super(application);

        // If any transformation is needed, this can be simply done by Transformations class ...

        //List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());

    }

    public LiveData<TrafficCameraResponse> getTrafficCameraObservable() {
        trafficCameraObservable = LTARepository.getInstance().getTrafficCamera();
        return trafficCameraObservable;
    }

    public LiveData<TrafficIncidentResponse> getTrafficIncidentObservable() {
        trafficIncidentObservable = LTARepository.getInstance().getTrafficIncident();
        return trafficIncidentObservable;
    }

    public LiveData<List<TrafficCamera>> getNearestCamerasObservable(List<LatLng> polyline) {
        nearestCamerasObservable = LTARepository.getInstance().getNearestTrafficCamera(polyline);
        return nearestCamerasObservable;
    }

    public LiveData<List<TrafficIncident>> getNearestIncidentsObservable(List<LatLng> polyline) {
        nearestIncidentsObservable = LTARepository.getInstance().getNearestTrafficIncident(polyline);
        return nearestIncidentsObservable;
    }

    public LiveData<CarparkAvailabilityResponse> getCarparkAvailabilityObservable() {
        carparkAvailabilityObservable = LTARepository.getInstance().getCarparkAvailability();
        return carparkAvailabilityObservable;
    }

    public LiveData<List<Carpark>> getNearestAvailableCarparkObservable(LatLng destination) {
        nearestAvailableCarpark = LTARepository.getInstance().getNearestAvailableCarparks(destination);
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

    public LiveData<List<PlanningArea>> getPlanningArea(Double lat, Double lng){
        planningArea = OneMapRepository.getInstance().getPlanningArea(lat, lng);
        return planningArea;
    }

    public LiveData<Forecast> getWeatherForecast(String area){
        forecastObservable = DataGovRepository.getInstance().getWeatherForecast(area);
        return forecastObservable;
    }

    public LiveData<String> getPsi(String area){
        return DataGovRepository.getInstance().getPsi(area);
    }


}
