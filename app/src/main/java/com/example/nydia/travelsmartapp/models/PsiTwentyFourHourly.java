package com.example.nydia.travelsmartapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PsiTwentyFourHourly {

    @SerializedName("west")
    @Expose
    private Integer west;
    @SerializedName("national")
    @Expose
    private Integer national;
    @SerializedName("east")
    @Expose
    private Integer east;
    @SerializedName("central")
    @Expose
    private Integer central;
    @SerializedName("south")
    @Expose
    private Integer south;
    @SerializedName("north")
    @Expose
    private Integer north;

    public Integer getWest() {
        return west;
    }

    public void setWest(Integer west) {
        this.west = west;
    }

    public Integer getNational() {
        return national;
    }

    public void setNational(Integer national) {
        this.national = national;
    }

    public Integer getEast() {
        return east;
    }

    public void setEast(Integer east) {
        this.east = east;
    }

    public Integer getCentral() {
        return central;
    }

    public void setCentral(Integer central) {
        this.central = central;
    }

    public Integer getSouth() {
        return south;
    }

    public void setSouth(Integer south) {
        this.south = south;
    }

    public Integer getNorth() {
        return north;
    }

    public void setNorth(Integer north) {
        this.north = north;
    }

}