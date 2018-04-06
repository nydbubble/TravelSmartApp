package com.example.nydia.travelsmartapp.models;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PSIResponse {

    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item {

        @SerializedName("timestamp")
        @Expose
        private String timestamp;
        @SerializedName("update_timestamp")
        @Expose
        private String updateTimestamp;
        @SerializedName("readings")
        @Expose
        private Readings readings;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getUpdateTimestamp() {
            return updateTimestamp;
        }

        public void setUpdateTimestamp(String updateTimestamp) {
            this.updateTimestamp = updateTimestamp;
        }

        public Readings getReadings() {
            return readings;
        }

        public void setReadings(Readings readings) {
            this.readings = readings;
        }

    }

    public class Readings {

        @SerializedName("psi_twenty_four_hourly")
        @Expose
        private PsiTwentyFourHourly psiTwentyFourHourly;

        public PsiTwentyFourHourly getPsiTwentyFourHourly() {
            return psiTwentyFourHourly;
        }

        public void setPsiTwentyFourHourly(PsiTwentyFourHourly psiTwentyFourHourly) {
            this.psiTwentyFourHourly = psiTwentyFourHourly;
        }

    }

}