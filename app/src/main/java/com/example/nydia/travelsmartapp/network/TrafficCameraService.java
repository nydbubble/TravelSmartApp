package com.example.nydia.travelsmartapp.network;

import com.example.nydia.travelsmartapp.models.TrafficCameraResponse;
import com.example.nydia.travelsmartapp.models.TrafficIncidentResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

/**
 * Created by nydia on 3/28/18.
 */

public interface TrafficCameraService {
    @GET("ltaodataservice/Traffic-Images")
    Call<TrafficCameraResponse> getCameras();

    @Streaming
    @GET("ltaodataservice/TrafficIncidents")
    Call<TrafficIncidentResponse> getIncidents();
}
