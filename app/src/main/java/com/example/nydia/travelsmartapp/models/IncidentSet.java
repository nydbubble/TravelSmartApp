package com.example.nydia.travelsmartapp.models;

/**
 * Created by nydia on 3/28/18.
 */

public class IncidentSet {

    private String Type,Latitude, Longitude, Message;


    public IncidentSet(String type, String latitude,
                       String longitude, String message) {
        super();
        Type = type;
        Latitude = latitude;
        Longitude = longitude;
        Message = message;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getLatitude() {
        return Latitude;
    }

    public float getLatitudeFloat() {
        return Float.parseFloat(Latitude);
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public float getLongitudeFloat() {
        return Float.parseFloat(Longitude);
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @Override
    public String toString() {
        return "IncidentSet{" +
                "Type='" + Type + '\'' +
                ", Latitude='" + Latitude + '\'' +
                ", Longitude='" + Longitude + '\'' +
                ", Message='" + Message + '\'' +
                '}';
    }
}
