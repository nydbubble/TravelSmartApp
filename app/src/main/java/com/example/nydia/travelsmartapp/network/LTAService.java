package com.example.nydia.travelsmartapp.network;

import com.example.nydia.travelsmartapp.models.CarparkAvailabilityResponse;
import com.example.nydia.travelsmartapp.models.TrafficCameraResponse;
import com.example.nydia.travelsmartapp.models.TrafficIncidentResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

/**
 * Created by nydia on 3/28/18.
 */

public interface LTAService {
    @GET("ltaodataservice/Traffic-Images")
    Call<TrafficCameraResponse> getCameras();

    @GET("ltaodataservice/TrafficIncidents")
    Call<TrafficIncidentResponse> getIncidents();

    @GET("ltaodataservice/CarParkAvailabilityv2")
    Call<CarparkAvailabilityResponse> getCarparkAvailability();
}
