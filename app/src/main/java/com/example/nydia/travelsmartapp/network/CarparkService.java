package com.example.nydia.travelsmartapp.network;

import com.example.nydia.travelsmartapp.models.CarparkAvailabilityResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by nydia on 4/3/18.
 */

public interface CarparkService {
    @GET("uraDataService/invokeUraDS?service=Car_Park_Availability")
    Call<CarparkAvailabilityResponse> getCarparkAvailability();
}
