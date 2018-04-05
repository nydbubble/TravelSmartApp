package com.example.nydia.travelsmartapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nydia on 4/5/18.
 */

public class PlanningArea {
    @SerializedName("pln_area_n")
    @Expose
    private String plnAreaN;
    @SerializedName("geojson")
    @Expose
    private String geojson;

    public String getPlnAreaN() {
        return plnAreaN;
    }

    public void setPlnAreaN(String plnAreaN) {
        this.plnAreaN = plnAreaN;
    }

    public String getGeojson() {
        return geojson;
    }

    public void setGeojson(String geojson) {
        this.geojson = geojson;
    }

}
