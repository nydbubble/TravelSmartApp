package com.example.nydia.travelsmartapp.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.nydia.travelsmartapp.models.Forecast;
import com.example.nydia.travelsmartapp.models.WeatherForecast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nydia on 4/5/18.
 */

public class DataGovRepository {

    private static final String base_url = "https://api.data.gov.sg/v1/";
    private static DataGovRepository dataGovRepository;
    private DataGovService dataGovService;
    private  Retrofit retrofit;

    private final ArrayList<String> centralregion = new ArrayList<>(Arrays.asList(
            "DOWNTOWN CORE",
            "MARINA EAST",
            "MARINA WEST",
            "MUSEUM",
            "NEWTON",
            "ORCHARD",
            "OUTRAM",
            "RIVER VALLEY",
            "ROCHOR",
            "SINGAPORE RIVER",
            "STRAITS VIEW"));

    private DataGovRepository() {
        if (retrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            OkHttpClient client = builder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();

            dataGovService = retrofit.create(DataGovService.class);
        }
    }

    public synchronized static DataGovRepository getInstance() {
        //TODO No need to implement this singleton in Part #2 since Dagger will handle it ...
        if (dataGovRepository == null) {
            if (dataGovRepository == null) {
                dataGovRepository = new DataGovRepository();
            }
        }
        return dataGovRepository;
    }

    public LiveData<Forecast> getWeatherForecast(final String area){
        final MutableLiveData<Forecast> data = new MutableLiveData<>();

        dataGovService.getWeatherForecast().enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                //Check if parameter and data "area" is same
                List<Forecast> list = new ArrayList<>();
                for (Forecast forecast : response.body().getItems().get(0).getForecasts()) {
                    String fArea = forecast.getArea().toUpperCase();
                    String pArea = area.toUpperCase();
                    if (checkAreaEquals(pArea, fArea)) {
                        data.setValue(forecast);
                        return;
                    }
                }
                data.setValue(new Forecast());
            }

            @Override
            public void onFailure(Call<WeatherForecast> call, Throwable t) {

                // Error handling will be explained in the next article …
            }

        });

        return data;
    }

    private boolean checkAreaEquals(String pArea, String fArea){
        pArea = pArea.toUpperCase();
        fArea = fArea.toUpperCase();
        return (fArea.equals(pArea) || centralregion.contains(pArea));
    }

}
