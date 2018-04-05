package com.example.nydia.travelsmartapp.network;

import com.example.nydia.travelsmartapp.models.OneMapToken;
import com.example.nydia.travelsmartapp.models.PlanningArea;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by nydia on 4/5/18.
 */

public interface OneMapService {

    @GET("privateapi/popapi/getPlanningarea?")
    Call<List<PlanningArea>> getPlanningArea(@Query("token") String token,
                                             @Query("lat") Double lat,
                                             @Query("lng") Double lng);
}
