package com.example.nydia.travelsmartapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nydia on 4/5/18.
 */

public class WeatherForecast {
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

        @SerializedName("update_timestamp")
        @Expose
        private String updateTimestamp;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;
        @SerializedName("forecasts")
        @Expose
        private List<Forecast> forecasts = null;

        public String getUpdateTimestamp() {
            return updateTimestamp;
        }

        public void setUpdateTimestamp(String updateTimestamp) {
            this.updateTimestamp = updateTimestamp;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public List<Forecast> getForecasts() {
            return forecasts;
        }

        public void setForecasts(List<Forecast> forecasts) {
            this.forecasts = forecasts;
        }

    }
}
