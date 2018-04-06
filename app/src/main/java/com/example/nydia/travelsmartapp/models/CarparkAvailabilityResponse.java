package com.example.nydia.travelsmartapp.models;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nydia on 4/3/18.
 */

public class CarparkAvailabilityResponse {
        @SerializedName("odata.metadata")
        @Expose
        private String odataMetadata;
        @SerializedName("value")
        @Expose
        private List<Carpark> value = null;

        public String getOdataMetadata() {
            return odataMetadata;
        }

        public void setOdataMetadata(String odataMetadata) {
            this.odataMetadata = odataMetadata;
        }

        public List<Carpark> getValue() {
            return value;
        }

        public void setValue(List<Carpark> value) {
            this.value = value;
        }

}

