package com.example.nydia.travelsmartapp.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.util.Log;

import com.example.nydia.travelsmartapp.models.Carpark;
import com.example.nydia.travelsmartapp.models.CarparkAvailabilityResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import net.qxcg.svy21.*;

/**
 * Created by nydia on 4/3/18.
 */

public class CarparkRepository {
    private CarparkService carparkService;
    private static CarparkRepository carparkRepository;

    private static Retrofit retrofit = null;

    private static final String apiKey = "25837153-f457-4997-baa4-e56070763c3f";
    private static final String base_url = "https://www.ura.gov.sg/";
    private static final String token = "uTS6aYK8qK57YpmG5e0-8yM73W56r5c4qqUS5-969aA-46h673MS@6B7mfC5k3Za9284mfba8KfM7a7qa+x636vF54655R66wF0-";

    private CarparkRepository(){
        if(retrofit==null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("AccessKey", apiKey)
                            .header("Token", token);// <-- this is the important line

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

            OkHttpClient client = builder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();

            carparkService = retrofit.create(CarparkService.class);
        }
    }

    public synchronized static CarparkRepository getInstance() {
        //TODO No need to implement this singleton in Part #2 since Dagger will handle it ...
        if (carparkRepository == null) {
            if (carparkRepository == null) {
                carparkRepository = new CarparkRepository();
            }
        }
        return carparkRepository;
    }



    public LiveData<CarparkAvailabilityResponse> getCarparkAvailability() {
        final MutableLiveData<CarparkAvailabilityResponse> data = new MutableLiveData<>();
        Log.d("CarparkRespository", "Fetching carparks");

        carparkService.getCarparkAvailability().enqueue(new Callback<CarparkAvailabilityResponse>() {
            @Override
            public void onResponse(Call<CarparkAvailabilityResponse> call, Response<CarparkAvailabilityResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<CarparkAvailabilityResponse> call, Throwable t) {

                // Error handling will be explained in the next article …
            }

        });

        return data;
    }

    public LiveData<List<Carpark>> getNearestAvailableCarparks(final LatLng destination) {
        final MutableLiveData<List<Carpark>> data = new MutableLiveData<>();

        carparkService.getCarparkAvailability().enqueue(new Callback<CarparkAvailabilityResponse>() {
            @Override
            public void onResponse(Call<CarparkAvailabilityResponse> call, Response<CarparkAvailabilityResponse> response) {
                List<Carpark> list = new ArrayList<>();
                    for (int i = 0; i < response.body().getResult().size(); i++) {
                        if (response.body().getResult().get(i).getGeometries() != null) {
                            LatLng point = new LatLng(response.body().getResult().get(i).getGeometries().get(0).getLatitude(), response.body().getResult().get(i).getGeometries().get(0).getLongitude());
                            float[] distance = new float[1];
                            Location.distanceBetween(point.latitude, point.longitude, destination.latitude, destination.longitude, distance);
                            boolean result = distance[0] <= 500;
                            boolean available = Integer.parseInt(response.body().getResult().get(i).getLotsAvailable()) > 0;
                            boolean car = response.body().getResult().get(i).getLotType().contains("C");
                            if (result && available && car) {
                                Log.d("Repository", "Nearest available carpark found");
                                list.add(response.body().getResult().get(i));
                            }
                        }
                    }
                    data.setValue(list);
            }

            @Override
            public void onFailure(Call<CarparkAvailabilityResponse> call, Throwable t) {

                // Error handling will be explained in the next article …
            }

        });
        return data;
    }
}
