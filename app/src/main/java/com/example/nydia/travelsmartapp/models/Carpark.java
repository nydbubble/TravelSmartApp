package com.example.nydia.travelsmartapp.models;

import android.util.Log;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.qxcg.svy21.*;

public class Carpark {

    @SerializedName("lotsAvailable")
    @Expose
    private String lotsAvailable;
    @SerializedName("lotType")
    @Expose
    private String lotType;
    @SerializedName("carparkNo")
    @Expose
    private String carparkNo;
    @SerializedName("geometries")
    @Expose
    private List<Geometry> geometries = null;

    public String getLotsAvailable() {
        return lotsAvailable;
    }

    public void setLotsAvailable(String lotsAvailable) {
        this.lotsAvailable = lotsAvailable;
    }

    public String getLotType() {
        return lotType;
    }

    public void setLotType(String lotType) {
        this.lotType = lotType;
    }

    public String getCarparkNo() {
        return carparkNo;
    }

    public void setCarparkNo(String carparkNo) {
        this.carparkNo = carparkNo;
    }

    public List<Geometry> getGeometries() {
        return geometries;
    }

    public void setGeometries(List<Geometry> geometries) {
        this.geometries = geometries;
    }

    public class Geometry {

        @SerializedName("coordinates")
        @Expose
        private String coordinates;

        @Expose
        private Double latitude;

        @Expose
        private Double longitude;

        @Expose
        private String address;

        public String getCoordinates() {
            String[] latlong =  coordinates.split(",");
            Double east = Double.parseDouble(latlong[0]);
            Double north = Double.parseDouble(latlong[1]);
            SVY21Coordinate result = new SVY21Coordinate(north,east);
            LatLonCoordinate reverseResult = result.asLatLon();
            return reverseResult.getLatitude() + "," + reverseResult.getLongitude();
        }

        public void setCoordinates(String coordinates) {
            this.coordinates = coordinates;
        }

        public String getAddress(){
            return address;
        }

        public void setAddress(String address){
            this.address = address;
        }

        public Double getLatitude(){
            String[] latlong =  getCoordinates().split(",");
            return Double.parseDouble(latlong[0]);
        }

        public Double getLongitude(){
            String[] latlong =  getCoordinates().split(",");
            return Double.parseDouble(latlong[1]);
        }

    }

}
