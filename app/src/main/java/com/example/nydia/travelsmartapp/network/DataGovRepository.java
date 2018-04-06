package com.example.nydia.travelsmartapp.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.nydia.travelsmartapp.models.PsiTwentyFourHourly;
import com.example.nydia.travelsmartapp.models.Forecast;
import com.example.nydia.travelsmartapp.models.PSIResponse;
import com.example.nydia.travelsmartapp.models.WeatherForecast;

import java.util.ArrayList;
import java.util.Arrays;

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

    private final ArrayList<String> central = new ArrayList<>(Arrays.asList(
            "BISHAN",
            "TOA PAYOH",
            "BUKIT MERAH",
            "GEYLANG",
            "MARINE PARADE",
            "KALLANG",
            "NOVENA",
            "QUEENSTOWN",
            "TANGLIN",
            "BUKIT TIMAH"));

    private final ArrayList<String> west = new ArrayList<>(Arrays.asList(
            "BUKIT BATOK",
            "BUKIT PANJANG",
            "CHOA CHU KANG",
            "CLEMENTI",
            "JURONG EAST",
            "JURONG WEST"));

    private final ArrayList<String> north = new ArrayList<>(Arrays.asList(
            "SEMBAWANG",
            "WOODLANDS",
            "YISHUN"));

    private final ArrayList<String> northeast = new ArrayList<>(Arrays.asList(
            "ANG MO KIO",
            "HOUGANG",
            "PUNGGOL",
            "SENGKANG",
            "SERANGOON"));

    private final ArrayList<String> east = new ArrayList<>(Arrays.asList(
            "BEDOK",
            "CHANGI",
            "PASIR RIS",
            "TAMPINES"));


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

    public LiveData<String> getPsi(final String area){
        final MutableLiveData<String> data = new MutableLiveData<>();

        dataGovService.getPSI().enqueue(new Callback<PSIResponse>() {
            @Override
            public void onResponse(Call<PSIResponse> call, Response<PSIResponse> response) {
                //Check if parameter and data "area" is same
                String region = getRegion(area);
                Log.i("DataGovRepository: ", region);
                PsiTwentyFourHourly psi = response.body().getItems().get(0).getReadings().getPsiTwentyFourHourly();
                if (region.equals("west"))
                    data.setValue(String.valueOf(psi.getWest()));
                else if (region.equals("east"))
                    data.setValue(String.valueOf(psi.getEast()));
                else if (region.equals("north"))
                    data.setValue(String.valueOf(psi.getNorth()));
                else if (region.equals("south"))
                    data.setValue(String.valueOf(psi.getSouth()));
                else if (region.equals("central"))
                    data.setValue(String.valueOf(psi.getCentral()));
                else if (region.equals("national"))
                    data.setValue(String.valueOf(psi.getNational()));
            }

            @Override
            public void onFailure(Call<PSIResponse> call, Throwable t) {

                // Error handling will be explained in the next article …
            }

        });

        return data;
    }

    private String getRegion(String pArea) {
        String area = pArea.toUpperCase();
        if (central.contains(area) || centralregion.contains(area))
            return "south";
        else if (west.contains(area))
            return "west";
        else if (east.contains(area))
            return "east";
        else if (north.contains(area))
            return "north";
        else if (northeast.contains(area))
            return "central";
        else return "national";
    }

    private boolean checkAreaEquals(String pArea, String fArea){
        pArea = pArea.toUpperCase();
        fArea = fArea.toUpperCase();
        return (fArea.equals(pArea) || (fArea.equals("CITY") && centralregion.contains(pArea)));
    }

}
