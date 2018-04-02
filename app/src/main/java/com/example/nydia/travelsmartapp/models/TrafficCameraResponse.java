package com.example.nydia.travelsmartapp.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nydia on 3/27/18.
 */


public class TrafficCameraResponse {

    @SerializedName("odata.metadata")
    @Expose
    private String odataMetadata;
    @SerializedName("value")
    @Expose
    private List<TrafficCamera> trafficCamera = null;

    public TrafficCameraResponse(String odataMetadata, List<TrafficCamera> value) {
        this.odataMetadata = odataMetadata;
        this.trafficCamera = value;
    }


    public String getOdataMetadata() {
        return odataMetadata;
    }

    public void setOdataMetadata(String odataMetadata) {
        this.odataMetadata = odataMetadata;
    }

    public List<TrafficCamera> getTrafficCamera() {
        return trafficCamera;
    }

    public void setValue(List<TrafficCamera> trafficCamera) {
        this.trafficCamera = trafficCamera;
    }

}