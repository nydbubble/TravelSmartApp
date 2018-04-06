package com.example.nydia.travelsmartapp.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.util.Log;

import com.example.nydia.travelsmartapp.models.Carpark;
import com.example.nydia.travelsmartapp.models.CarparkAvailabilityResponse;
import com.example.nydia.travelsmartapp.models.TrafficCamera;
import com.example.nydia.travelsmartapp.models.TrafficCameraResponse;
import com.example.nydia.travelsmartapp.models.TrafficIncident;
import com.example.nydia.travelsmartapp.models.TrafficIncidentResponse;
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

/**
 * Created by nydia on 3/28/18.
 */

public class LTARepository extends LiveData {
    private LTAService LTAService;
    private static LTARepository LTARepository;

    private static Retrofit retrofit = null;

    private static final String apiKey = "6VW6Xv1vTpu2/gFswVmVmA==";
    private static final String base_url = "http://datamall2.mytransport.sg/";

    private LTARepository(){
        if(retrofit==null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("AccountKey", apiKey); // <-- this is the important line

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

            LTAService = retrofit.create(LTAService.class);
        }
    }

        public synchronized static LTARepository getInstance() {
            //TODO No need to implement this singleton in Part #2 since Dagger will handle it ...
            if (LTARepository == null) {
                if (LTARepository == null) {
                    LTARepository = new LTARepository();
                }
            }
            return LTARepository;
        }

    public LiveData<TrafficCameraResponse> getTrafficCamera() {
        final MutableLiveData<TrafficCameraResponse> data = new MutableLiveData<>();
        Log.d("LTARepository", "Fetching traffic cameras");

        LTAService.getCameras().enqueue(new Callback<TrafficCameraResponse>() {
            @Override
            public void onResponse(Call<TrafficCameraResponse> call, Response<TrafficCameraResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<TrafficCameraResponse> call, Throwable t) {

                // Error handling will be explained in the next article …
            }

        });

        return data;
    }

    public LiveData<List<TrafficCamera>> getNearestTrafficCamera(final List<LatLng> polyline) {
        final MutableLiveData<List<TrafficCamera>> data = new MutableLiveData<>();

        LTAService.getCameras().enqueue(new Callback<TrafficCameraResponse>() {
            @Override
            public void onResponse(Call<TrafficCameraResponse> call, Response<TrafficCameraResponse> response) {
                List<TrafficCamera> list = new ArrayList<>();
                for (int i=0; i<response.body().getTrafficCamera().size(); i++) {
                    LatLng point = new LatLng(response.body().getTrafficCamera().get(i).getLatitude(), response.body().getTrafficCamera().get(i).getLongitude());
                    boolean result = PolyUtil.isLocationOnPath(point, polyline, false, 10);
                    if (result) {
                        Log.d("Repository", "Nearest camera found");
                        list.add(response.body().getTrafficCamera().get(i));
                    }
                }
                data.setValue(list);

            }

            @Override
            public void onFailure(Call<TrafficCameraResponse> call, Throwable t) {

                // Error handling will be explained in the next article …
            }

        });
        return data;
    }

    public LiveData<TrafficIncidentResponse> getTrafficIncident() {
        final MutableLiveData<TrafficIncidentResponse> data = new MutableLiveData<>();

        LTAService.getIncidents().enqueue(new Callback<TrafficIncidentResponse>() {
            @Override
            public void onResponse(Call<TrafficIncidentResponse> call, Response<TrafficIncidentResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<TrafficIncidentResponse> call, Throwable t) {

                // Error handling will be explained in the next article …
            }

        });

        return data;
    }

    public LiveData<List<TrafficIncident>> getNearestTrafficIncident(final List<LatLng> polyline) {
        final MutableLiveData<List<TrafficIncident>> data = new MutableLiveData<>();

            LTAService.getIncidents().enqueue(new Callback<TrafficIncidentResponse>() {
                @Override
                public void onResponse(Call<TrafficIncidentResponse> call, Response<TrafficIncidentResponse> response) {
                    List<TrafficIncident> list = new ArrayList<>();
                    for (int i = 0; i < response.body().getTrafficIncidents().size(); i++) {
                        LatLng point = new LatLng(response.body().getTrafficIncidents().get(i).getLatitude(), response.body().getTrafficIncidents().get(i).getLongitude());
                        boolean result = PolyUtil.isLocationOnPath(point, polyline, false, 10);
                        if (result) {
                            Log.d("Repository", "Nearest incident found");
                            list.add(response.body().getTrafficIncidents().get(i));
                        }
                    }
                    data.setValue(list);

                }

                @Override
                public void onFailure(Call<TrafficIncidentResponse> call, Throwable t) {

                    // Error handling will be explained in the next article …
                }

            });
            return data;
    }

    public LiveData<CarparkAvailabilityResponse> getCarparkAvailability() {
        final MutableLiveData<CarparkAvailabilityResponse> data = new MutableLiveData<>();
        Log.d("CarparkRespository", "Fetching carparks");

        LTAService.getCarparkAvailability().enqueue(new Callback<CarparkAvailabilityResponse>() {
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

        LTAService.getCarparkAvailability().enqueue(new Callback<CarparkAvailabilityResponse>() {
            @Override
            public void onResponse(Call<CarparkAvailabilityResponse> call, Response<CarparkAvailabilityResponse> response) {
                List<Carpark> list = new ArrayList<>();
                for (int i = 0; i < response.body().getValue().size(); i++) {
                    LatLng point = new LatLng(response.body().getValue().get(i).getLatitude(), response.body().getValue().get(i).getLongitude());
                    float[] distance = new float[1];
                    Location.distanceBetween(point.latitude, point.longitude, destination.latitude, destination.longitude, distance);
                    boolean result = distance[0] <= 500;
                    boolean available = response.body().getValue().get(i).getAvailableLots() > 0;
                    boolean car = response.body().getValue().get(i).getLotType().contains("C");
                    if (result && available && car) {
                        Log.d("Repository", "Nearest available carpark found");
                        list.add(response.body().getValue().get(i));
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
