package com.example.nydia.travelsmartapp.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.nydia.travelsmartapp.models.PlanningArea;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nydia on 4/5/18.
 */

public class OneMapRepository extends LiveData {

    private static Retrofit retrofit = null;
    private OneMapService oneMapService;
    private static OneMapRepository oneMapRepository;

    private static final String base_url = "https://developers.onemap.sg/";
    private static final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjE0OTAsInVzZXJfaWQiOjE0OTAsImVtYWlsIjoibnlkLmJ1YmJsZUBnbWFpbC5jb20iLCJmb3JldmVyIjpmYWxzZSwiaXNzIjoiaHR0cDpcL1wvb20yLmRmZS5vbmVtYXAuc2dcL2FwaVwvdjJcL3VzZXJcL3Nlc3Npb24iLCJpYXQiOjE1MjI4NTgyNjAsImV4cCI6MTUyMzI5MDI2MCwibmJmIjoxNTIyODU4MjYwLCJqdGkiOiIwZWQ1Y2I2MzhmZWY5MGJmZmQzNGYxN2QxZmY3MzYyNyJ9.ZLGa2ElZmDTvi5gf4R4tRHILoQu1m3xT7qySo5ywwVo";


    private OneMapRepository(){
        if (retrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            OkHttpClient client = builder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
            Log.d("OneMapRespository", "bikin retrofit");
            oneMapService = retrofit.create(OneMapService.class);
        }
    }

    public synchronized static OneMapRepository getInstance() {
        //TODO No need to implement this singleton in Part #2 since Dagger will handle it ...
        if (oneMapRepository == null) {
            if (oneMapRepository == null) {
                oneMapRepository = new OneMapRepository();
            }
        }
        return oneMapRepository;
    }

    public LiveData<List<PlanningArea>> getPlanningArea(Double lat, Double lng) {
        final MutableLiveData<List<PlanningArea>> data = new MutableLiveData<>();
        oneMapService.getPlanningArea(token,lat,lng).enqueue(new Callback<List<PlanningArea>>() {
            @Override
            public void onResponse(Call<List<PlanningArea>> call, Response<List<PlanningArea>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<PlanningArea>> call, Throwable t) {
                // Error handling will be explained in the next article …
            }

        });

        return data;
    }

}
