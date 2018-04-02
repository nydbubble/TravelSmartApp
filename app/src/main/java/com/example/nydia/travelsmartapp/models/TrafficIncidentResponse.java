package com.example.nydia.travelsmartapp.models;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrafficIncidentResponse {

    @SerializedName("odata.metadata")
    @Expose
    private String odataMetadata;
    @SerializedName("value")
    @Expose
    private List<TrafficIncident> trafficIncidents = null;

    public String getOdataMetadata() {
        return odataMetadata;
    }

    public void setOdataMetadata(String odataMetadata) {
        this.odataMetadata = odataMetadata;
    }

    public List<TrafficIncident> getTrafficIncidents() {
        return trafficIncidents;
    }

    public void setValue(List<TrafficIncident> value) {
        this.trafficIncidents = value;
    }

}
