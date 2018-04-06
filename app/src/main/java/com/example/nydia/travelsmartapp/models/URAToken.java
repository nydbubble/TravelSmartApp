package com.example.nydia.travelsmartapp.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class URAToken {

    @SerializedName("Result")
    @Expose
    private String result;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Message")
    @Expose
    private String message;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
