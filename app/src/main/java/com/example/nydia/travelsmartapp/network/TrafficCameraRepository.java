package com.example.nydia.travelsmartapp.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

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

public class TrafficCameraRepository  extends LiveData {
    private TrafficCameraService trafficCameraService;
    private static TrafficCameraRepository trafficCameraRepository;

    private static Retrofit retrofit = null;

    private static final String apiKey = "6VW6Xv1vTpu2/gFswVmVmA==";
    private static final String base_url = "http://datamall2.mytransport.sg/";

    private TrafficCameraRepository(){
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

            trafficCameraService = retrofit.create(TrafficCameraService.class);
        }
    }

        public synchronized static TrafficCameraRepository getInstance() {
            //TODO No need to implement this singleton in Part #2 since Dagger will handle it ...
            if (trafficCameraRepository == null) {
                if (trafficCameraRepository == null) {
                    trafficCameraRepository = new TrafficCameraRepository();
                }
            }
            return trafficCameraRepository;
        }

    public LiveData<TrafficCameraResponse> getTrafficCamera() {
        final MutableLiveData<TrafficCameraResponse> data = new MutableLiveData<>();
        Log.d("TrafficCameraRepository", "Fetching traffic cameras");

        trafficCameraService.getCameras().enqueue(new Callback<TrafficCameraResponse>() {
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

        trafficCameraService.getCameras().enqueue(new Callback<TrafficCameraResponse>() {
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

        trafficCameraService.getIncidents().enqueue(new Callback<TrafficIncidentResponse>() {
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

            trafficCameraService.getIncidents().enqueue(new Callback<TrafficIncidentResponse>() {
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


    private void simulateDelay() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
