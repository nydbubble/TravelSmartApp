package com.example.nydia.travelsmartapp.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.nydia.travelsmartapp.models.DirectionsResults;
import com.example.nydia.travelsmartapp.models.GeocodingResults;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nydia on 3/28/18.
 */

public class DirectionsRepository {
    private DirectionsService directionsService;
    private static DirectionsRepository directionsRepository;

    private static Retrofit retrofit;

    private static final String apiKey = "AIzaSyBTMhpbR4rYP5Xeb310j6KU4gV7abI2eaE";
    private static final String base_url = "https://maps.googleapis.com/maps/api/";

    private DirectionsRepository() {
        if (retrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            OkHttpClient client = builder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();

            directionsService = retrofit.create(DirectionsService.class);
        }
    }

    public synchronized static DirectionsRepository getInstance() {
        //TODO No need to implement this singleton in Part #2 since Dagger will handle it ...
        if (directionsRepository == null) {
            if (directionsRepository == null) {
                directionsRepository = new DirectionsRepository();
            }
        }
        return directionsRepository;
    }

    public LiveData<DirectionsResults> getDirections(String origin, String destination) {
        final MutableLiveData<DirectionsResults> data = new MutableLiveData<>();

        directionsService.getDirections(origin, destination, apiKey).enqueue(new Callback<DirectionsResults>() {
            @Override
            public void onResponse(Call<DirectionsResults> call, Response<DirectionsResults> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<DirectionsResults> call, Throwable t) {

                // Error handling will be explained in the next article …
            }

        });

        return data;
    }

    public LiveData<String> getAddress(String latlng) {
        final MutableLiveData<String> data = new MutableLiveData<>();

        directionsService.getReverseGeocoding(latlng, apiKey).enqueue(new Callback<GeocodingResults>() {
            @Override
            public void onResponse(Call<GeocodingResults> call, Response<GeocodingResults> response) {
                data.setValue(response.body().getResults().get(0).getFormattedAddress());
            }

            @Override
            public void onFailure(Call<GeocodingResults> call, Throwable t) {

                // Error handling will be explained in the next article …
            }

        });

        return data;
    }

}
