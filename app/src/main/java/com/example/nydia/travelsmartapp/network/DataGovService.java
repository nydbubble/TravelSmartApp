package com.example.nydia.travelsmartapp.network;

import com.example.nydia.travelsmartapp.models.WeatherForecast;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by nydia on 4/5/18.
 */

public interface DataGovService {
    @GET("environment/2-hour-weather-forecast")
    Call<WeatherForecast> getWeatherForecast();
}
