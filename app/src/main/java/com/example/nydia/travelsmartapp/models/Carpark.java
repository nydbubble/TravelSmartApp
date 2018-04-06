package com.example.nydia.travelsmartapp.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.qxcg.svy21.*;

@SuppressLint("ParcelCreator")
public class Carpark implements Parcelable{
    @SerializedName("CarParkID")
    @Expose
    private String carParkID;
    @SerializedName("Area")
    @Expose
    private String area;
    @SerializedName("Development")
    @Expose
    private String development;
    @SerializedName("Location")
    @Expose
    private String location;
    @SerializedName("AvailableLots")
    @Expose
    private Integer availableLots;
    @SerializedName("LotType")
    @Expose
    private String lotType;
    @SerializedName("Agency")
    @Expose
    private String agency;

    public String getCarParkID() {
        return carParkID;
    }

    public void setCarParkID(String carParkID) {
        this.carParkID = carParkID;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDevelopment() {
        return development;
    }

    public void setDevelopment(String development) {
        this.development = development;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getAvailableLots() {
        return availableLots;
    }

    public void setAvailableLots(Integer availableLots) {
        this.availableLots = availableLots;
    }

    public String getLotType() {
        return lotType;
    }

    public void setLotType(String lotType) {
        this.lotType = lotType;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public Double getLatitude(){
        if(location.length()>0) {
            String[] coordinates = location.split(" ");
            return Double.parseDouble(coordinates[0]);
        }
        else return 1000.0;
    }

    public Double getLongitude(){
        if(location.length()>0) {
            String[] coordinates = location.split(" ");
            return Double.parseDouble(coordinates[1]);
        }
        else return 1000.0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
