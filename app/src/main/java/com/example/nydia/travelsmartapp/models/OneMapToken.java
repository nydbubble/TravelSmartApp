package com.example.nydia.travelsmartapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nydia on 4/5/18.
 */

public class OneMapToken {
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("expiry_timestamp")
    @Expose
    private Integer expiryTimestamp;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpiryTimestamp() {
        return expiryTimestamp;
    }

    public void setExpiryTimestamp(Integer expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }
}
