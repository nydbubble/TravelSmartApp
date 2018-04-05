package com.example.nydia.travelsmartapp.models;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nydia on 4/3/18.
 */

public class CarparkAvailabilityResponse {

        @SerializedName("Result")
        @Expose
        private List<Carpark> result = null;
        @SerializedName("Status")
        @Expose
        private String status;
        @SerializedName("Message")
        @Expose
        private String message;

        public List<Carpark> getResult() {
            return result;
        }

        public void setResult(List<Carpark> result) {
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
