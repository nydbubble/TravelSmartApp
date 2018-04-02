package com.example.nydia.travelsmartapp.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nydia on 3/27/18.
 */


public class TrafficCamera {

    @SerializedName("CameraID")
    @Expose
    private String cameraID;
    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;
    @SerializedName("ImageLink")
    @Expose
    private String imageLink;

    public TrafficCamera(String cameraID, Double latitude, Double longitude, String imageLink){
        this.cameraID = cameraID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageLink = imageLink;
    }

    public String getCameraID() {
        return cameraID;
    }

    public void setCameraID(String cameraID) {
        this.cameraID = cameraID;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

}